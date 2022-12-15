package core.network.packets;

import core.Streamable;
import core.network.listeners.PacketListener;
import core.network.fields.Fields;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Packet<T extends PacketListener> implements Streamable {

    protected final Fields fields = new Fields();

    public abstract void apply(T listener);

    public void write(ObjectOutputStream ostream) throws IOException {
        fields.write(ostream);
    }

    public void read(ObjectInputStream istream) throws IOException {
        fields.read(istream);
    }

    protected Packet() { }
}
