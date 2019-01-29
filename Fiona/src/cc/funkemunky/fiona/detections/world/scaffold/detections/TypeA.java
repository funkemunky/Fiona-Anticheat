package cc.funkemunky.fiona.detections.world.scaffold.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

public class TypeA extends Detection {

    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);
    }

    @Override
    public void onBukkitEvent(Event event, PlayerData data) {
        if (event instanceof BlockPlaceEvent) {
            BlockPlaceEvent e = (BlockPlaceEvent) event;

            if (data.generalCancel
                    || e.getPlayer().getLocation().getBlockY() <= e.getBlockPlaced().getY()
                    || e.getPlayer().isSneaking()
                    || data.movement.deltaXZ < 0.2
                    || data.airTicks > 0) {
                return;
            }

            if (e.getPlayer().getLocation().clone().subtract(0, 1, 0).getBlock().getType().isSolid()
                    && !e.getPlayer().getLocation().clone().subtract(0, 2, 0).getBlock().getType().isSolid()) {
                if (data.scaffoldTopVerbose.flag(4, 1000L)) {
                    flag(data, "t: air", 1, true, true);
                }
            }
            debug(data, data.scaffoldTopVerbose.getVerbose() + ": " + (e.getPlayer().getLocation().clone().subtract(0, 1, 0).getBlock().getType().isSolid()
                    && !e.getPlayer().getLocation().clone().subtract(0, 2, 0).getBlock().getType().isSolid()));
        }
    }
}
