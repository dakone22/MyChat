package core.network.packets.fields;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringField extends Field<String> {
    private static final int MAX_STRING_LENGTH = (2 << 14) - 1;  // 32767
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Field<Integer> stringLength;

    private StringField(int maxLength) {
        stringLength = new IntField.Builder().range(0, maxLength).build();
    }

    @Override
    public boolean isValid(String value) {
        return stringLength.isValid() && value.length() <= ((IntField)stringLength).getMax();
    }

    @Override
    public void setValue(String value) {
        this.stringLength.setValue(value.length());
        super.setValue(value);
    }

    @Override
    public void write(ByteBuffer buffer) {
        byte[] stringBytes = getValue().getBytes(StandardCharsets.UTF_8);

        stringLength.write(buffer);
        buffer.put(stringBytes);
    }

    @Override
    public void read(ByteBuffer buffer) {
        this.stringLength.read(buffer);
        int readStringLength = stringLength.getValue();

        var stringBuffer = new byte[readStringLength];
        setValue(new String(buffer.get(stringBuffer).array(), CHARSET));
    }

    public static class Builder extends FieldBuilder<Builder, String, StringField> {
        private int maxLength = MAX_STRING_LENGTH;

        public Builder maxLength(int maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        @Override
        public StringField build() {
            return new StringField(maxLength);
        }
    }
}
