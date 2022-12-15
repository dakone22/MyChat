package core.network.fields;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IntField extends Field<Integer> {

    private final int min, max;

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    private IntField(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isValid(Integer value) {
        return value != null && min <= value && value <= max;
    }

    @Override
    public void write(ObjectOutputStream ostream) throws IOException {
        ostream.writeInt(getValue());
    }

    @Override
    public void read(ObjectInputStream istream) throws IOException {
        setValue(istream.readInt());
    }

    public static class Builder extends FieldBuilder<Builder, Integer, IntField> {
        private int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;

        public Builder min(int min) {
            this.min = min;
            return this;
        }

        public Builder max(int max) {
            this.max = max;
            return this;
        }

        public Builder range(int min, int max) {
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
            return this;
        }

        @Override
        public IntField build() {
            return new IntField(min, max);
        }
    }
}
