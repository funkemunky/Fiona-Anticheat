package cc.funkemunky.fiona.detections.movement.nofall.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;

public class TypeC extends Detection {

    public TypeC(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.moved(e.getFrom(), e.getTo())
                    && !PlayerUtils.isRiskyForFlight(data)
                    && MathUtils.elapsed(data.lastNoFallCancel, 500L)
                    && data.slimeTicks == 0
                    && data.blockTicks == 0) {

                //Type B - Checking if fallDistance does not reset when on ground.
                if (e.isOnGround()
                        && e.getPlayer().getFallDistance() > 0
                        && data.noFallBVerbose.flag(5, 400L)) {
                    flag(data, "t: b d: " + e.getPlayer().getFallDistance() + ">-0", 1, true, false);
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
