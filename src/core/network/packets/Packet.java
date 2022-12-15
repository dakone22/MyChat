package core.network.packets;

import core.network.fields.Fields;
import core.network.listeners.PacketListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public interface Packet<T extends PacketListener> extends Serializable {
    public void apply(T listener);
}
