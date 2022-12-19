package core.network;

import core.network.listeners.ClientPacketListener;
import core.network.listeners.PacketListener;
import core.network.listeners.ServerPacketListener;
import core.network.packets.Packet;
import core.network.packets.c2s.chat.*;
import core.network.packets.c2s.service.*;
import core.network.packets.s2c.chat.*;
import core.network.packets.s2c.service.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


public class PacketHandler<T extends PacketListener> {
    public static PacketHandler<ClientPacketListener> S2C_PACKET_HANDLER = (new PacketHandler<ClientPacketListener>()
            .register(SystemMessageS2CPacket.class)
            .register(ClientJoinS2CPacket.class)
            .register(ClientLeaveS2CPacket.class)
            .register(ChatUserListS2CPacket.class)
            .register(PublicChatMessageS2CPacket.class)
            .register(PrivateChatMessageS2CPacket.class)
            .register(CustomSystemMessageS2CPacket.class)
            .register(ConnectedSuccessS2CPacket .class)
            .register(ConnectedFailureS2CPacket.class)
            .register(DisconnectedS2CPacket.class)
            .register(LoginRequestS2CPacket.class)
    );

    public static PacketHandler<ServerPacketListener> C2S_PACKET_HANDLER = (new PacketHandler<ServerPacketListener>()
            .register(PublicChatMessageC2SPacket.class)
            .register(PrivateChatMessageC2SPacket.class)
            .register(LoginC2SPacket.class)
            .register(DisconnectC2SPacket.class)
            .register(ChatUserListC2SPacket.class)
    );

    private final Map<Integer, Class<? extends Packet<T>>> packetsById = new HashMap<>();
    private final Map<Class<? extends Packet<T>>, Integer> idsByPacket = new HashMap<>();

    public <P extends Packet<T>> PacketHandler<T> register(Class<P> packetClass) {
        int i = this.packetsById.size();

        this.packetsById.put(i, packetClass);
        this.idsByPacket.put(packetClass, i);

        return this;
    }

    private Integer getId(Class<?> packet) {
        if (!this.idsByPacket.containsKey(packet))
            (new RuntimeException("Unregistered packet class " + packet.toString())).printStackTrace();

        return this.idsByPacket.get(packet);
    }

    private Packet<T> createPacket(int id, ObjectInputStream istream) throws IOException, ClassNotFoundException {
        var packetClass = this.packetsById.getOrDefault(id, null);

        if (packetClass == null)
            return null;

        System.out.printf("Receiving packet %d %s\n", id, packetClass);

        return packetClass.cast(istream.readObject());
    }

    public void sendPacket(ObjectOutputStream outputStream, Packet<? extends T> packet) throws IOException {
        int id = getId(packet.getClass());
        System.out.printf("Sending packet %d %s\n", id, packet.getClass());
        outputStream.writeInt(id);
        outputStream.writeObject(packet);
    }

    public Packet<T> receivePacket(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        int id = inputStream.readInt();
        return createPacket(id, inputStream);
    }

    public static <T extends PacketListener> void handlePacket(Packet<T> packet, T listener) {
        packet.apply(listener);
    }
}