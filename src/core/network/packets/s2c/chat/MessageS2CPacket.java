package core.network.packets.s2c.chat;

import core.network.listeners.ClientChatPacketListener;
import core.network.packets.Packet;
import core.network.packets.PacketByteBuffer;

public class MessageS2CPacket implements Packet<ClientChatPacketListener> {
    public enum VisibilityType { Public, Private }

    public enum SenderType { Client, Server }

    private final String sender;  // TODO: SenderClass
//    private final LocalDateTime datetime;  // TODO: DateTimePacket
    private final int index;
    private final String message;
    private final VisibilityType visibilityType;
    private final SenderType senderType;

    public MessageS2CPacket(String sender, int index, String message, VisibilityType visibilityType, SenderType senderType) {
        this.sender = sender;
//        this.datetime = datetime;
        this.index = index;
        this.message = message;
        this.visibilityType = visibilityType;
        this.senderType = senderType;
    }

    public MessageS2CPacket(PacketByteBuffer buf) {
        this.sender = buf.readString();
//        this.datetime = buf.readLocalDateTime();
        this.index = buf.readInt();
        this.message = buf.readString();
        this.visibilityType = buf.readEnumConstant(VisibilityType.class);
        this.senderType = buf.readEnumConstant(SenderType.class);
    }

    @Override
    public void write(PacketByteBuffer buf) {
        buf.writeString(sender);
//        buf.writeLocalDateTime(datetime);
        buf.writeInt(index);
        buf.writeString(message);
        buf.writeEnumConstant(visibilityType);
        buf.writeEnumConstant(senderType);
    }

    @Override
    public void apply(ClientChatPacketListener listener) {
        listener.onPublicMessage(this);
    }

    // TODO: maybe separate (Public/Private) (Server/Client) messages classes>
}
