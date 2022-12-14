package core.network.packets.fields;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Fields implements Iterable<Field<?>> {
    private static final List<Field<?>> fieldList = new ArrayList<>();

    @Override
    public Iterator<Field<?>> iterator() {
        return fieldList.iterator();
    }

    public <T> Field<T> add(Field<T> field) {
        fieldList.add(field);
        return field;
    }
}
