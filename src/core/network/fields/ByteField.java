package core.network.fields;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Deprecated
public class ByteField extends Field<Byte> {
    @Override
    public void write(ObjectOutputStream ostream) throws IOException {
        ostream.writeByte(getValue());
    }

    @Override
    public void read(ObjectInputStream istream) throws IOException {
        setValue(istream.readByte());
    }

    @Override
    public boolean isValid(Byte value) {
        return value != null;
    }

    public static class Builder extends FieldBuilder<Builder, Byte, ByteField> {
        @Override
        public ByteField build() {
            return new ByteField();
        }
    }
}
