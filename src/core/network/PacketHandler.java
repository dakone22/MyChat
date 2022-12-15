package core.network;

import core.network.listeners.ClientPacketListener;
import core.network.listeners.PacketListener;
import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;
import core.network.packets.c2s.ChatMessageC2SPacket;
import core.network.packets.s2c.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


public class PacketHandler<T extends PacketListener> {
    @FunctionalInterface
    interface UnsafeFunction<T, R> {
        R apply(T t) throws IOException;
    }

    public static PacketHandler<ClientPacketListener> S2C_PACKET_HANDLER = new PacketHandler<ClientPacketListener>()
            .register(SystemMessageS2CPacket.class, SystemMessageS2CPacket::new)
            .register(ClientJoinS2CPacket.class, ClientJoinS2CPacket::new)
            .register(ClientLeaveS2CPacket.class, ClientLeaveS2CPacket::new)
            .register(ChatMessageS2CPacket.Public.class, ChatMessageS2CPacket.Public::new)
            .register(ChatMessageS2CPacket.Private.class, ChatMessageS2CPacket.Private::new)
            .register(CustomSystemMessageS2CPacket.class, CustomSystemMessageS2CPacket::new)
    ;

    public static PacketHandler<ServerPacketListener> C2S_PACKET_HANDLER = (new PacketHandler<ServerPacketListener>()
            .register(ChatMessageC2SPacket.Public.class, ChatMessageC2SPacket.Public::new)
            .register(ChatMessageC2SPacket.Private.class, ChatMessageC2SPacket.Private::new)
    );

    private final Map<Class<? extends Packet<T>>, UnsafeFunction<ObjectInputStream, ? extends Packet<T>>> packetFactories = new HashMap<>();
    private final Map<Integer, Class<? extends Packet<T>>> packetsById = new HashMap<>();
    private final Map<Class<? extends Packet<T>>, Integer> idsByPacket = new HashMap<>();

    public <P extends Packet<T>> PacketHandler<T> register(Class<P> packetClass, UnsafeFunction<ObjectInputStream, P> packetFactory) {
        int i = this.packetsById.size();

        this.packetsById.put(i, packetClass);
        this.idsByPacket.put(packetClass, i);

        this.packetFactories.put(packetClass, packetFactory);

        return this;
    }

    private Integer getId(Class<?> packet) {
        return this.idsByPacket.getOrDefault(packet, null);
    }

    private Packet<T> createPacket(int id, ObjectInputStream istream) throws IOException {
        var packetClass = this.packetsById.getOrDefault(id, null);

        if (packetClass == null)
            return null;

        var packetFactory = this.packetFactories.getOrDefault(packetClass, null);

        if (packetFactory == null)
            return null;

        return packetFactory.apply(istream);
    }

    public void sendPacket(ObjectOutputStream outputStream, Packet<? extends T> packet) throws IOException {
        int id = getId(packet.getClass());
        outputStream.writeInt(id);
        packet.write(outputStream);
    }

    public Packet<T> receivePacket(ObjectInputStream inputStream) throws IOException {
        int id = inputStream.readInt();
        return createPacket(id, inputStream);
    }

//    public Iterator<Class<? extends Packet<?>>> getPacketTypes() {
//        return this.idsByPacket.keySet().iterator();
//    }

    public static <T extends PacketListener> void handlePacket(Packet<T> packet, T listener) {
        packet.apply(listener);
    }
}