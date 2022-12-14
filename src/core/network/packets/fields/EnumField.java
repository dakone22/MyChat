package core.network.packets.fields;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;


/*
    public <T extends Enum<T>> T readEnumConstant(Class<T> enumClass) {
        return (enumClass.getEnumConstants())[this.readInt()];
    }

    public void writeEnumConstant(Enum<?> instance) {
        this.writeInt(instance.ordinal());
    }
 */
public class EnumField<T extends Enum<T>> extends Field<T>{
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
    public void write(ByteBuffer buffer) {
        ordinal.write(buffer);
    }

    @Override
    public void read(ByteBuffer buffer) {
        ordinal.read(buffer);
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
