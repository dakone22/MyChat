package core.network.fields;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Fields {
    private final List<Field<?>> fieldList = new ArrayList<>();

    public <T> Field<T> add(Field<T> field) {
        fieldList.add(field);
        return field;
    }

    @Override
    public void read(ObjectInputStream istream) throws IOException {
        for (var field : fieldList) {
            field.read(istream);
        }
    }

    @Override
    public void write(ObjectOutputStream ostream) throws IOException {
        for (var field : fieldList) {
            field.write(ostream);
        }

    }
}
