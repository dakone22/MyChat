package core.network.packets;

import core.network.listeners.PacketListener;

import java.io.*;

public interface Packet<T extends PacketListener> extends Serializable {
    void write(PacketByteBuffer buf);

    void apply(T listener);
}
