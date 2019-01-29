package cc.funkemunky.fiona.detections.movement.step.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;

public class TypeC extends Detection {
    public TypeC(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (data.movement.deltaXZ == 0 && data.movement.deltaY > 0 && !PlayerUtils.isRiskyForFlight(data)) {
                if (MathUtils.getDelta(data.collidedYDist += data.movement.deltaY, 1) > 0.2f) {
                    flag(data, data.collidedYDist + ">-1", 1, true, true);
                }
            } else {
                data.collidedYDist = 0;
            }
        }
    }
}
