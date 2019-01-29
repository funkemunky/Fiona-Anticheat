/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.tinyprotocol.packet.out;

import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutVelocityPacket extends NMSObject {
    private static final String packet = Server.ENTITY_VELOCITY;

    // Fields
    private static FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static FieldAccessor<Integer> fieldX = fetchField(packet, int.class, 1);
    private static FieldAccessor<Integer> fieldY = fetchField(packet, int.class, 2);
    private static FieldAccessor<Integer> fieldZ = fetchField(packet, int.class, 3);

    // Decoded data
    private int id;
    private double x, y, z;

    public WrappedOutVelocityPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(fieldId);
        x = fetch(fieldX) / 8000D;
        y = fetch(fieldY) / 8000D;
        z = fetch(fieldZ) / 8000D;
    }
}
