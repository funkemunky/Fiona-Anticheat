package cc.funkemunky.fiona.detections.movement.fly.detections;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

public class TypeB extends Detection {
    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("violationToFlag", 20);
        addConfigValue("expand", 1.3);
        addConfigValue("threshold", 16);
        setThreshold((int) getConfigValues().getOrDefault("violationToFlag", 30));
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            debug(data, PlayerUtils.isRiskyForFlight(data) + " risk, " + data.isVelocityTaken());
            if (!PlayerUtils.isRiskyForFlight(data)) {
                boolean collides = Fiona.getInstance().getBlockBoxManager().getBlockBox().getCollidingBoxes(e.getPlayer().getWorld(), data.boundingBox.subtract(0, (float) (double) getConfigValues().get("expand"), 0, 0, 0, 0)).size() > 0;

                if (!collides && data.movement.deltaY >= 0 && !data.player.hasPotionEffect(PotionEffectType.JUMP)) {
                    if (data.collisionTicks++ > (int) getConfigValues().get("threshold")) {
                        flag(data, "t: " + data.collisionTicks, 1, false, true);
                    }
                } else {
                    data.collisionTicks = 0;
                }
            }
        }
    }
}
