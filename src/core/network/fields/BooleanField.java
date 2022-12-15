package core.network.fields;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BooleanField extends Field<Boolean> {
    @Override
    public void write(ObjectOutputStream ostream) throws IOException {
        ostream.writeByte((byte) (this.getValue() ? 1 : 0));
    }

    @Override
    public void read(ObjectInputStream istream) throws IOException {
        setValue(istream.readByte() > 0);
    }

    @Override
    public boolean isValid(Boolean value) {
        return value != null;
    }

    public static class Builder extends FieldBuilder<Builder, Boolean, BooleanField> {
        @Override
        public BooleanField build() {
            return new BooleanField();
        }
    }
}
