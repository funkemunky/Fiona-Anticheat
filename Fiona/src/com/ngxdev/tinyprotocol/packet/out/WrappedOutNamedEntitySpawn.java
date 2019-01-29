/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.tinyprotocol.packet.out;

import cc.funkemunky.fiona.utils.MathUtils;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.types.WrappedGameProfile;

//TODO make this cleaner
public class WrappedOutNamedEntitySpawn extends NMSObject {
    private static final String packet = NMSObject.Server.NAMED_ENTITY_SPAWN;

    public WrappedOutNamedEntitySpawn(ProtocolVersion version, int id, Object gameProfile, double x, double y, double z, Object dataWatcher, Object watchables) {
        if (version.isBelow(ProtocolVersion.V1_8)) {
            setPacket(packet
                    , id, gameProfile,
                    MathUtils.floor(x * 32.0D),
                    MathUtils.floor(y * 32.0D),
                    MathUtils.floor(z * 32.0D), null, null, null,
                    dataWatcher);
        } else {
            WrappedGameProfile profile = new WrappedGameProfile(gameProfile);
            setPacket(packet
                    , id, profile.getId(), profile.getName(), profile.getPropertyMap(),
                    MathUtils.floor(x * 32.0D),
                    MathUtils.floor(y * 32.0D),
                    MathUtils.floor(z * 32.0D), null, null, null,
                    dataWatcher, watchables);
        }
    }
}
