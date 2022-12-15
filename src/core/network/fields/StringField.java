package core.network.fields;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringField extends Field<String> {
    private static final int MAX_STRING_LENGTH = (2 << 14) - 1;  // 32767
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Field<Integer> stringByteLength;

    private StringField(int maxLength) {
        stringByteLength = new IntField.Builder().range(0, maxLength).build();
    }

    @Override
    public boolean isValid(String value) {
        return value != null && stringByteLength.isValid() && value.length() <= ((IntField) stringByteLength).getMax();
    }

    @Override
    public void setValue(String value) {
        this.stringByteLength.setValue(value.getBytes(StandardCharsets.UTF_8).length);
        super.setValue(value);
    }

    @Override
    public void write(ObjectOutputStream ostream) throws IOException {
        byte[] stringBytes = getValue().getBytes(StandardCharsets.UTF_8);

        stringByteLength.write(ostream);
        ostream.write(stringBytes);
    }

    @Override
    public void read(ObjectInputStream istream) throws IOException {
        this.stringByteLength.read(istream);
        int readStringLength = stringByteLength.getValue();

        var stringBuffer = new byte[readStringLength];
        int readBytes = istream.read(stringBuffer);

        if (readBytes != readStringLength) {
            throw new IOException("Expected " + readStringLength + " bytes of string, got " + readBytes + " bytes!");
        }

        setValue(new String(stringBuffer, CHARSET));
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
