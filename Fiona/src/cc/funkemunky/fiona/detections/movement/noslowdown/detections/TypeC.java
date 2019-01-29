package cc.funkemunky.fiona.detections.movement.noslowdown.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

public class TypeC extends Detection {

    public TypeC(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("threshold", 7);
        addConfigValue("resetTime", 200);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.moved(e.getFrom(), e.getTo())
                    && !data.generalCancel
                    && data.inWeb) {

                double threshold = 0.14;

                threshold += PlayerUtils.getPotionEffectLevel(e.getPlayer(), PotionEffectType.SPEED) * 0.005;
                threshold += (e.getPlayer().getWalkSpeed() - 0.2) * 0.01;

                if (data.movement.deltaXZ > threshold
                        && data.noSlowTypeCVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"))) {
                    flag(data, MathUtils.round(data.movement.deltaXZ, 3) + ">-" + MathUtils.trim(3, threshold), 1, true, true);
                }
                debug(data, data.movement.deltaXZ + "bps");
            }
        }
    }
}
