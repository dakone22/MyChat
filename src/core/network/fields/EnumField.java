package core.network.fields;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;


public class EnumField<T extends Enum<T>> extends Field<T> {
    private final Field<Integer> ordinal;
    private final Class<T> type;
    private final T[] values;

    public EnumField(Class<T> type) {
        this.type = type;

        try {
            values = (T[]) getValue().getClass().getMethod("values").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        this.ordinal = new IntField.Builder().range(0, values.length - 1).build();
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        ordinal.setValue(value.ordinal());
    }

    @Override
    public void write(ObjectOutputStream ostream) throws IOException {
        ordinal.write(ostream);
    }

    @Override
    public void read(ObjectInputStream istream) throws IOException {
        ordinal.read(istream);
        setValue(values[ordinal.getValue()]);
    }

    @Override
    public boolean isValid(T value) {
        return ordinal.isValid();
    }

    public static class Builder<T extends Enum<T>> extends FieldBuilder<Builder<T>, T, EnumField<T>> {
        private final Class<T> type;

        public Builder(Class<T> type) {
            super();
            this.type = type;
        }

        @Override
        public EnumField<T> build() {
            return new EnumField<>(type);
        }
    }
}
