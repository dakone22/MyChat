package core.network.packets;

import core.network.listeners.PacketListener;

import java.io.Serializable;

public interface Packet<T extends PacketListener> extends Serializable {
    public void apply(T listener);
}
