package core.network.fields;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Field<T> {
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (!isValid(value)) {
            throw new RuntimeException("Not valid value \"%s\" for field class %s!".formatted(value.toString(), this.getClass().toString()));
        }

        this.value = value;
    }

    public abstract void write(ObjectOutputStream ostream) throws IOException;

    public abstract void read(ObjectInputStream istream) throws IOException;

    public abstract boolean isValid(T value);
    public boolean isValid() { return isValid(value); }

    public abstract static class FieldBuilder<B, V, S> {

        public abstract S build();
    }
}
