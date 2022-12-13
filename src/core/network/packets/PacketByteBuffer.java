package core.network.packets;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class PacketByteBuffer {
    private static final int MAX_STRING_LENGTH = (2 << 14) - 1;  // 32767
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    private final ByteBuffer buffer;

    public PacketByteBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteBuffer getByteBuffer() {
        return buffer;
    }

    // region boolean

    public void writeBoolean(boolean value) {
        buffer.put((byte) (value ? 1 : 0));
    }

    public boolean readBoolean() {
        return buffer.get() > 0;
    }

    // endregion

    // region byte

    public void writeByte(byte value) {
        buffer.put(value);
    }

    public byte readByte() {
        return buffer.get();
    }

    // endregion

    // region bytes

    public void writeBytes(byte[] value) {
        buffer.put(value);
    }

    // ...

    // endregion

    // region short

    public void writeShort(short value) {
        buffer.putShort(value);
    }

    public short readShort() {
        return buffer.getShort();
    }

    // endregion

    // region int

    public void writeInt(int value) {
        buffer.putInt(value);
    }

    public int readInt() {
        return buffer.getInt();
    }

    // endregion

    // region enum

    public <T extends Enum<T>> T readEnumConstant(Class<T> enumClass) {
        return (enumClass.getEnumConstants())[this.readInt()];
    }

    public void writeEnumConstant(Enum<?> instance) {
        this.writeInt(instance.ordinal());
    }

    // endregion

    // region String

    public void writeString(String string) {
        writeString(string, MAX_STRING_LENGTH);
    }

    public void writeString(String string, int maxLength) {
        if (string.length() > maxLength) {
            throw new RuntimeException("String too big (was " + string.length() + " characters, max " + maxLength + ")");
        }

        byte[] bs = string.getBytes(StandardCharsets.UTF_8);

        writeInt(bs.length);
        writeBytes(bs);
    }

    public String readString() {
        return this.readString(MAX_STRING_LENGTH);
    }

    public String readString(int maxLength) {
        int stringLength = readInt();

        if (stringLength > maxLength)
            throw new RuntimeException("The received string length is longer than maximum allowed (" + stringLength + " > " + maxLength + ")");

        if (stringLength < 0)
            throw new RuntimeException("The received encoded string buffer length is less than zero! Weird string!");

        var stringBuffer = new byte[stringLength];
        return new String(buffer.get(stringBuffer).array(), CHARSET);
    }

    // endregion

    // region LocalDateTime

    public void writeLocalDateTime(LocalDateTime value) {
        writeInt(value.getYear());
        writeInt(value.getMonthValue());
        writeInt(value.getDayOfMonth());
        writeInt(value.getHour());
        writeInt(value.getMinute());
        writeInt(value.getSecond());
    }

    public LocalDateTime readLocalDateTime() {
        int year = readInt();
        int month = readInt();
        int day = readInt();
        int hour = readInt();
        int minute = readInt();
        int second = readInt();

        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    // endregion

}
