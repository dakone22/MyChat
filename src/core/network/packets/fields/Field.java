package core.network.packets.fields;

import java.nio.ByteBuffer;

public abstract class Field<T> {
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (!isValid(value)) {
            throw new RuntimeException("Not valid value");
        }

        this.value = value;
    }

    public abstract void write(ByteBuffer buffer);

    public abstract void read(ByteBuffer buffer);

    public abstract boolean isValid(T value);
    public boolean isValid() { return isValid(value); }

    public abstract static class FieldBuilder<B, V, S> {

        public abstract S build();
    }
}
