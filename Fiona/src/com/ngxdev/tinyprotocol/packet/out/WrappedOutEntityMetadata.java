package com.ngxdev.tinyprotocol.packet.out;

import com.google.common.collect.Lists;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.types.WrappedWatchableObject;
import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Setter
public class WrappedOutEntityMetadata extends NMSObject {
    private static final String packet = Packet.Server.ENTITY_METADATA;

    private static FieldAccessor<Integer> entity_id;
    private static FieldAccessor<List> watchableObjects;

    private List<WrappedWatchableObject> objects;

    public WrappedOutEntityMetadata(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        watchableObjects = fetchField(packet, List.class, 0);

        objects = Lists.newArrayList();

        List list = fetch(watchableObjects);

        if(list != null) {
            list.forEach(object -> objects.add(new WrappedWatchableObject(object)));
        }
    }
}
