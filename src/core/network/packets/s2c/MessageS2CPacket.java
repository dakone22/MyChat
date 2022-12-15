package core.network.packets.s2c;

import core.network.fields.Field;
import core.network.fields.IntField;
import core.network.fields.LocalDateTimeField;
import core.network.listeners.ClientPacketListener;
import core.network.packets.Packet;

import java.time.LocalDateTime;

public abstract class MessageS2CPacket extends Packet<ClientPacketListener> {
    protected final Field<Integer> index = fields.add(new IntField.Builder().min(0).build());
    protected final Field<LocalDateTime> dateTime = fields.add(new LocalDateTimeField.Builder().build());

    protected MessageS2CPacket(Integer index, LocalDateTime dateTime) {
        this.index.setValue(index);
        this.dateTime.setValue(dateTime);
    }

    protected MessageS2CPacket() {}

    public Integer getIndex() {
        return index.getValue();
    }

    public LocalDateTime getDateTime() {
        return dateTime.getValue();
    }
}
