package cc.funkemunky.fiona.detections.movement.nofall.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;

public class TypeD extends Detection {
    public TypeD(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setThreshold(10);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.moved(e.getFrom(), e.getTo())
                    && !PlayerUtils.isRiskyForFlight(data)
                    && MathUtils.elapsed(data.lastNoFallCancel, 500L)
                    && data.slimeTicks == 0) {

                //Type C - Checking if fallDistance resets while in the air.
                if (!e.isOnGround()
                        && !data.onGroundFive
                        && data.lastBlockPlace.hasPassed(10)
                        && data.lastOnGround.hasPassed(10)
                        && data.movement.deltaY < 0
                        && data.lastFallDistance - e.getPlayer().getFallDistance() > 2.5) {
                    flag(data, "t: object d: " + data.lastFallDistance + ">-" + e.getPlayer().getFallDistance(), 1, true, true);
                    setCancelled(data);
                }

                debug(data, data.player.getFallDistance() + ", " + data.movement.realFallDistance);

                data.lastFallDistance = e.getPlayer().getFallDistance();
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
