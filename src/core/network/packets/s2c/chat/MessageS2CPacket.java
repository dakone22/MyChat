package core.network.packets.s2c.chat;

import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;
import core.network.packets.fields.Field;
import core.network.packets.fields.IntField;
import core.network.packets.fields.LocalDateTimeField;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

public abstract class MessageS2CPacket extends Packet<ClientPacketListener> {
    protected final Field<Integer> index = new IntField.Builder().min(0).build();
    protected final Field<LocalDateTime> dateTime = new LocalDateTimeField.Builder().build();

    protected MessageS2CPacket(Integer index, LocalDateTime dateTime) {
        this.index.setValue(index);
        this.dateTime.setValue(dateTime);
    }

    protected MessageS2CPacket(ByteBuffer buffer) { read(buffer); }

    public Integer getIndex() {
        return index.getValue();
    }

    public LocalDateTime getDateTime() {
        return dateTime.getValue();
    }
}
