package cc.funkemunky.fiona.detections.movement.fly.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;

public class TypeG extends Detection {
    public TypeG(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);
    }

    //TODO Test for false positives. See if can be improved by removing elytra checking so it can be checked for glides.
    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.playerMoved(e.getFrom(), e.getTo()) && e.getPlayer().isInsideVehicle() && !e.getPlayer().getAllowFlight()) {
                if (data.airTicks > 30 && data.movement.deltaY >= 0) {
                    if (data.flyTypeGVerbose++ > 7) {
                        flag(data, data.movement.deltaY + ">-0", 1, true, true);
                    }
                } else {
                    data.flyTypeGVerbose = Math.max(0, data.flyTypeGVerbose - 1);
                }
            }
        }
    }
}
