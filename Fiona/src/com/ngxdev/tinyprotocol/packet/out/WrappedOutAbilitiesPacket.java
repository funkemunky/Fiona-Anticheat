package com.ngxdev.tinyprotocol.packet.out;

import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import com.ngxdev.tinyprotocol.reflection.Reflection;
import lombok.Getter;
import org.bukkit.entity.Player;

public class WrappedOutAbilitiesPacket extends NMSObject {
    private static final String packet = Server.ABILITIES;

    private static FieldAccessor<Boolean>
            invulnerableField = fetchField(packet, boolean.class, 0),
            flyingField = fetchField(packet, boolean.class, 1),
            allowedFlightField = fetchField(packet, boolean.class, 2),
            creativeModeField = fetchField(packet, boolean.class, 3);
    private static FieldAccessor<Float>
            flySpeedField = fetchField(packet, float.class, 0),
            walkSpeedField = fetchField(packet, float.class, 1);

    @Getter
    private boolean invulnerable, flying, allowedFlight, creativeMode;
    @Getter
    private float flySpeed, walkSpeed;


    public WrappedOutAbilitiesPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        invulnerable = fetch(invulnerableField);
        flying = fetch(flyingField);
        allowedFlight = fetch(allowedFlightField);
        creativeMode = fetch(creativeModeField);
        flySpeed = fetch(flySpeedField);
        walkSpeed = fetch(walkSpeedField);
    }
}