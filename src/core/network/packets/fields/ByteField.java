package core.network.packets.fields;

import java.nio.ByteBuffer;

@Deprecated
public class ByteField extends Field<Byte> {
    @Override
    public void write(ByteBuffer buffer) {
        buffer.put(getValue());
    }

    @Override
    public void read(ByteBuffer buffer) {
        setValue(buffer.get());
    }

    @Override
    public boolean isValid(Byte value) {
        return true;
    }

    public static class Builder extends FieldBuilder<Builder, Byte, ByteField> {
        @Override
        public ByteField build() {
            return new ByteField();
        }
    }
}
