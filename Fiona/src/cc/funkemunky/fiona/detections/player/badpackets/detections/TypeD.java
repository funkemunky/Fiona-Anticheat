package cc.funkemunky.fiona.detections.player.badpackets.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

public class TypeD extends Detection {

    public TypeD(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("maxMove", 20.0);

        addConfigValue("violationToFlag", 40);
        setThreshold((int) getConfigValues().getOrDefault("violationToFlag", 40));
    }

    @Override
    public void onBukkitEvent(Event event, PlayerData data) {
        if (event instanceof PlayerMoveEvent) {
            PlayerMoveEvent e = (PlayerMoveEvent) event;

            double lDelta = e.getFrom().toVector().distance(e.getTo().toVector());
            double threshold = (double) getConfigValues().get("maxMove");
            if (lDelta > threshold) {
                flag(data, MathUtils.round(lDelta, 3) + ">-" + threshold, 11, false, true);
                e.setCancelled(true);
            }
        }
    }
}
