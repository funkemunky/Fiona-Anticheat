package com.ngxdev.tinyprotocol.packet.out;

import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutTransaction extends NMSObject {
    private static final String packet = Server.TRANSACTION;
    private static FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static FieldAccessor<Short> fieldAction = fetchField(packet, short.class, 0);
    private static FieldAccessor<Boolean> fieldAccepted = fetchField(packet, boolean.class, 0);
    final String fnwo = "%%__NONCE__%%";
    private int id;
    private short action;
    private boolean accept;

    public WrappedOutTransaction(int id, short action, boolean accept) {
        setPacket(packet, id, action, accept);

    }

    public WrappedOutTransaction(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(fieldId);
        action = fetch(fieldAction);
        accept = fetch(fieldAccepted);
    }
}
