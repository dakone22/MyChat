package core.network.packets.s2c.chat;

import core.network.fields.Field;
import core.network.fields.IntField;
import core.network.fields.LocalDateTimeField;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

import java.time.LocalDateTime;

public abstract class MessageS2CPacket extends Packet<ClientPacketListener> {
    protected final Field<Integer> indexField = fields.add(new IntField.Builder().min(0).build());
    protected final Field<LocalDateTime> dateTimeField = fields.add(new LocalDateTimeField.Builder().build());

    protected MessageS2CPacket(Integer index, LocalDateTime dateTime) {
        this.indexField.setValue(index);
        this.dateTimeField.setValue(dateTime);
    }

    protected MessageS2CPacket() {}

    public Integer getIndex() {
        return indexField.getValue();
    }

    public LocalDateTime getDateTime() {
        return dateTimeField.getValue();
    }
}
