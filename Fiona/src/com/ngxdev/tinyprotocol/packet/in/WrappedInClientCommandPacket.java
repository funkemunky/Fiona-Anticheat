package com.ngxdev.tinyprotocol.packet.in;

import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInClientCommandPacket extends NMSObject {
    private static final String packet = Client.CLIENT_COMMAND;

    // Fields
    private static FieldAccessor<Enum> fieldCommand = fetchField(packet, Enum.class, 0);

    // Decoded data
    EnumClientCommand command;

    public WrappedInClientCommandPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        command = EnumClientCommand.values()[fetch(fieldCommand).ordinal()];
    }

    public enum EnumClientCommand {
        PERFORM_RESPAWN,
        REQUEST_STATS,
        OPEN_INVENTORY_ACHIEVEMENT;

        private EnumClientCommand() {
        }
    }
}
