package core.network.fields;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    public void write(ObjectOutputStream ostream) throws IOException {
        for (Field<?> field : new Field[]{year, month, day, hour, minute, second}) {
            field.write(ostream);
        }
    }

    @Override
    public void read(ObjectInputStream istream) throws IOException {
        for (Field<?> field : new Field[]{year, month, day, hour, minute, second}) {
            field.read(istream);
        }
        setValue(LocalDateTime.of(year.getValue(), month.getValue(), day.getValue(), hour.getValue(), minute.getValue(), second.getValue()));
    }

    @Override
    public boolean isValid(LocalDateTime value) {
        return value != null;
    }

    @Override
    public void setValue(LocalDateTime value) {
        year.setValue(value.getYear());
        month.setValue(value.getMonthValue());
        day.setValue(value.getDayOfMonth());
        hour.setValue(value.getHour());
        minute.setValue(value.getMinute());
        second.setValue(value.getSecond());
        super.setValue(value);
    }

    public static class Builder extends FieldBuilder<Builder, LocalDateTime, LocalDateTimeField> {
        @Override
        public LocalDateTimeField build() {
            return new LocalDateTimeField();
        }
    }
}
