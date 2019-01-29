package cc.funkemunky.fiona.detections.movement.fly.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.ReflectionsUtil;
import lombok.val;

public class TypeH extends Detection {
    public TypeH(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
    }

    @Override
    public void onFunkeEvent(Object e, PlayerData data) {
        if (e instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent event = (PacketFunkeMoveEvent) e;

            val player = event.getPlayer();

            val from = event.getFrom();
            val to = event.getTo();

            val yChange = to.getY() - from.getY();
            val motionY = ReflectionsUtil.getMotionY(player);

            val motionChange = Math.abs(yChange - motionY);

            if (data.lastLogin.hasPassed(40)
                    || player.isFlying()
                    || data.inLiquid
                    || data.velocityY > 0.0
                    || to.getY() < 0.0) {
                return;
            }

            if (motionChange > 2.0E-14 && motionChange < 0.07D || motionChange > 0.87D) {
                if (data.motionThreshold++ > 3) {
                    flag(data, "Y: " + motionChange, 1, false, true);
                }
            }
        }
    }
}
