package core.network.packets;

import core.network.fields.Fields;
import core.network.listeners.PacketListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Packet<T extends PacketListener> implements Serializable {

    public abstract void apply(T listener);

    protected Packet() {
    }
}
