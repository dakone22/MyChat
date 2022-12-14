package core.network.packets;

import core.network.listeners.PacketListener;
import core.network.packets.fields.Fields;

import java.nio.ByteBuffer;

public abstract class Packet<T extends PacketListener> {

    protected static final Fields fields = new Fields();

    public abstract void apply(T listener);

    public void write(ByteBuffer buffer) {
        for (var field : fields) { field.write(buffer); }
    }
    protected Packet<T> read(ByteBuffer buffer) {
        for (var field : fields) { field.read(buffer); }
        return this;
    }

//    public static <P extends Packet<?>> P createPacket(Class<P> packetClass, ByteBuffer buffer) {
//        var emptyPacket = new
//    }

//    Packet() {}
//
//    public Packet(ByteBuffer buffer) {
//        this();
//        read(buffer);
//    }

}
