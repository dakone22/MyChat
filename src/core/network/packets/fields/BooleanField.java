package core.network.packets.fields;

import java.nio.ByteBuffer;

public class BooleanField extends Field<Boolean> {
    @Override
    public void write(ByteBuffer buffer) {
        buffer.put((byte) (getValue() ? 1 : 0));
    }

    @Override
    public void read(ByteBuffer buffer) {
        setValue(buffer.get() > 0);
    }

    @Override
    public boolean isValid(Boolean value) {
        return true;
    }

    public static class Builder extends FieldBuilder<Builder, Boolean, BooleanField> {
        @Override
        public BooleanField build() {
            return new BooleanField();
        }
    }
}
