package cc.funkemunky.fiona.detections.movement.nofall.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;

public class TypeB extends Detection {

    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("cancelAsDamage", true);
        addConfigValue("violationsToFlag", 30);
        setThreshold((int) getConfigValues().get("violationsToFlag"));
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.playerMoved(e.getFrom(), e.getTo()) && e.getFrom().toVector().distance(e.getTo().toVector()) > 0.1 && !PlayerUtils.isRiskyForFlight(data) && data.blockTicks == 0 && !data.onHalfBlock) {

                //Type A - Checking if the ground packets from the client match-up to the ground calculations server-side.
                if (e.isOnGround() != data.onGround && !data.isBeingCancelled && data.lastBlockPlace.hasPassed(10)) {
                    if (data.nofallGroundVerbose.flag(7, 650L)) {
                        flag(data, e.isOnGround() + " != " + data.onGround, 1, true, true);
                        setCancelled(data);
                    }
                } else {
                    data.nofallGroundVerbose.deduct();
                }
            }
        }
    }

    private void setCancelled(PlayerData data) {
        if (isCancellable() && getParentCheck().isCancellable()) {
            if ((boolean) getConfigValues().get("cancelAsDamage") && data.movement.realFallDistance > 3) {
                data.player.damage(data.movement.realFallDistance * 0.75f);
            } else {
                data.setCancelled(CheckType.MOVEMENT, 1);
            }
        }
        data.lastNoFallCancel = System.currentTimeMillis();
    }
}
