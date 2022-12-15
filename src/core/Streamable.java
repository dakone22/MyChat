package core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Streamable {
    void read(ObjectInputStream istream) throws IOException;

    void write(ObjectOutputStream ostream) throws IOException;
}
