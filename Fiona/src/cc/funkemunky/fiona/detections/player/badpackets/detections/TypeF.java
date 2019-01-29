package cc.funkemunky.fiona.detections.player.badpackets.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketSendEvent;
import com.ngxdev.tinyprotocol.api.TinyProtocolHandler;
import com.ngxdev.tinyprotocol.packet.types.WrappedWatchableObject;
import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import com.ngxdev.tinyprotocol.reflection.Reflection;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class TypeF extends Detection {
    public TypeF(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketSendEvent) {
            PacketSendEvent e = (PacketSendEvent) event;

            FieldAccessor<Integer> entityId = Reflection.getFieldSafe(Reflection.NMS_PREFIX + "." + e.getType(), int.class, 0);
            Entity entity = e.getPlayer().getWorld().getLivingEntities().stream().filter(entity2 -> entity2.getEntityId() == entityId.get(e.getPacket())).findFirst().orElse(null);

            if (entity != null && !entity.getUniqueId().equals(e.getPlayer().getUniqueId())) {

                Object packetNew = e.getPacket();

                FieldAccessor<List> woObject = Reflection.getFieldSafe(Reflection.NMS_PREFIX + "." + e.getType(), List.class, 0);
                List list = woObject != null ? woObject.get(packetNew) : null;

                if (list != null) {
                    List<Object> watchableObjects = toOrdinal(list);

                    if (watchableObjects.get(6) != null) {
                        WrappedWatchableObject wrappedWO = new WrappedWatchableObject(watchableObjects.get(6));
                        wrappedWO.setObject(Float.NaN);
                        watchableObjects.remove(6);
                        watchableObjects.add(6, wrappedWO.toWatchableObject());
                        woObject.set(packetNew, watchableObjects);
                    }

                    e.setCancelled(true);
                    TinyProtocolHandler.sendPacket(e.getPlayer(), packetNew);
                }
            }
        }
    }

    private List<Object> toOrdinal(List list) {
        List<Object> ordinals = new ArrayList<>();
        list.forEach(e -> ordinals.add(e));
        return ordinals;
    }
}
