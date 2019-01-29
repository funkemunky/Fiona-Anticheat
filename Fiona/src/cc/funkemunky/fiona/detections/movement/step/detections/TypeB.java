package cc.funkemunky.fiona.detections.movement.step.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;

public class TypeB extends Detection {

    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("maxStep", 1.2);
        addConfigValue("airTickThreshold", 20);
        addConfigValue("yDeltaThreshold", 0.2);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.playerMoved(e.getFrom(), e.getTo()) && data.movement.deltaY > 0 && data.halfBlockTicks == 0 && !PlayerUtils.isRiskyForFlight(data)) {
                if (((data.stepTotalYDist += data.movement.deltaY) % 0.5 == 0) && data.stepTotalYDist > 0.51) {
                    flag(data, data.stepTotalYDist % 0.5 + "=0", 1, false, true);
                }
                debug(data, data.stepTotalYDist + ", " + data.collidedHorizontally);
            } else {
                data.stepTotalYDist = 0;
            }
        }
    }

}
