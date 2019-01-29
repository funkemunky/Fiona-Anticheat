package cc.funkemunky.fiona.detections.movement.nofall.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;

public class TypeA extends Detection {

    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("cancelAsDamage", true);
        addConfigValue("violationsToFlag", 30);
        setThreshold((int) getConfigValues().get("violationsToFlag"));

        setExperimental(true);
    }

    //TODO Test all 3 of these checks.
    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.moved(e.getFrom(), e.getTo())
                    && !PlayerUtils.isRiskyForFlight(data)
                    && MathUtils.elapsed(data.lastNoFallCancel, 500L)
                    && data.slimeTicks == 0) {

                //Type A - Checking the difference of fallDistance
                if (data.movement.realFallDistance - data.player.getFallDistance() > (data.hasLag() ? 10 : 4)) {
                    flag(data, "t: a d: " + data.movement.realFallDistance + ">-" + data.player.getFallDistance(), 1, true, false);
                    setCancelled(data);
                }
                debug(data, data.player.getFallDistance() + ", " + data.movement.realFallDistance);
            }
        }
    }

    private void setCancelled(PlayerData data) {
        if (isCancellable() && getParentCheck().isCancellable()) {
            if ((boolean) getConfigValues().get("cancelAsDamage") && data.movement.realFallDistance > 3) {
                data.player.damage(data.movement.realFallDistance * 0.5f);
            } else {
                data.setCancelled(CheckType.MOVEMENT, 1);
            }
        }
        data.lastNoFallCancel = System.currentTimeMillis();
    }
}
