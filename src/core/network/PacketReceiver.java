package core.network;

import core.network.listeners.PacketListener;
import core.network.packets.Packet;
import core.network.packets.s2c.chat.ClientJoinS2CPacket;
import core.network.packets.s2c.chat.ClientLeaveS2CPacket;
import core.network.packets.s2c.chat.SystemMessageS2CPacket;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class PacketReceiver {
    public static Map<NetworkSide, PacketReceiver> packetReceivers;

    static {
        packetReceivers = new HashMap<>();

        packetReceivers.put(
                NetworkSide.SERVERBOUND,
                (new PacketReceiver())
                        .register(SystemMessageS2CPacket.class, SystemMessageS2CPacket::new)
                        .register(ClientJoinS2CPacket.class, ClientJoinS2CPacket::new)
                        .register(ClientLeaveS2CPacket.class, ClientLeaveS2CPacket::new)
        );
    }

    private final Map<Class<? extends Packet<?>>, Function<ByteBuffer, ? extends Packet<?>>> packetFactories = new HashMap<>();
    private final Map<Integer, Class<? extends Packet<?>>> packetsById = new HashMap<>();
    private final Map<Class<? extends Packet<?>>, Integer> idsByPacket = new HashMap<>();

    public <P extends Packet<?>> PacketReceiver register(Class<P> packetClass, Function<ByteBuffer, P> packetFactory) {
        int i = this.packetsById.size();

        this.packetsById.put(i, packetClass);
        this.idsByPacket.put(packetClass, i);

        this.packetFactories.put(packetClass, packetFactory);

        return this;
    }

    public Integer getId(Class<?> packet) {
        return this.idsByPacket.getOrDefault(packet, null);
    }

    public Packet<?> createPacket(int id, ByteBuffer buf) {
        var packetClass = this.packetsById.getOrDefault(id, null);

        if (packetClass == null)
            return null;

        var packetFactory = this.packetFactories.getOrDefault(packetClass, null);

        if (packetFactory == null)
            return null;

        return packetFactory.apply(buf);
    }

    public Iterator<Class<? extends Packet<?>>> getPacketTypes() {
        return this.idsByPacket.keySet().iterator();
    }

    public static <T extends PacketListener> void handlePacket(Packet<T> packet, T listener) {
        packet.apply(listener);
    }
}