package core.network.packets.c2s.service;

//public class HandshakeC2SPacket implements Packet<ServerHandshakePacketListener> {  // TODO: handshake
//    private final int protocolVersion;
//    private final String address;
//    private final short port;
//
//    public HandshakeC2SPacket(int protocolVersion, String address, short port) {
//        this.protocolVersion = protocolVersion;
//        this.address = address;
//        this.port = port;
//    }
//
//    public HandshakeC2SPacket(PacketByteBuffer buf) {
//        this.protocolVersion = buf.readInt();
//        this.address = buf.readString();
//        this.port = buf.readShort();
//    }
//
//    @Override
//    public void write(PacketByteBuffer buf) {
//        buf.writeInt(this.protocolVersion);
//        buf.writeString(this.address);
//        buf.writeShort(this.port);
//    }
//
//    @Override
//    public void apply(ServerHandshakePacketListener serverHandshakePacketListener) {
//        serverHandshakePacketListener.onHandshake(this);
//    }
//}
