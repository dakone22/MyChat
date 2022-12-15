package core.network.fields;

import core.Streamable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Fields implements Streamable {
    private final List<Field<?>> fieldList = new ArrayList<>();

//    @Override
//    public Iterator<Field<?>> iterator() {
//        return fieldList.iterator();
//    }

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
