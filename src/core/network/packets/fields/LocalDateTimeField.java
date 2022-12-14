package core.network.packets.fields;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.Year;

public class LocalDateTimeField extends Field<LocalDateTime> {
    private final Field<Integer> year = new IntField.Builder().range(Year.MIN_VALUE, Year.MAX_VALUE).build();
    private final Field<Integer> month = new IntField.Builder().range(1, 12).build();
    private final Field<Integer> day = new IntField.Builder().range(1, 31).build();
    private final Field<Integer> hour = new IntField.Builder().range(0, 23).build();
    private final Field<Integer> minute = new IntField.Builder().range(0, 59).build();
    private final Field<Integer> second = new IntField.Builder().range(0, 59).build();

    @Override
    public void write(ByteBuffer buffer) {
        for (Field<?> field : new Field[]{year, month, day, hour, minute, second}) {
            field.write(buffer);
        }
    }

    @Override
    public void read(ByteBuffer buffer) {
        for (Field<?> field : new Field[]{year, month, day, hour, minute, second}) {
            field.read(buffer);
        }
    }

    @Override
    public boolean isValid(LocalDateTime value) {
        for (Field<?> field : new Field[]{year, month, day, hour, minute, second}) {
            if (!field.isValid()) { return false; }
        }
        return true;
    }

    public static class Builder extends FieldBuilder<Builder, LocalDateTime, LocalDateTimeField> {
        @Override
        public LocalDateTimeField build() {
            return new LocalDateTimeField();
        }
    }
}
