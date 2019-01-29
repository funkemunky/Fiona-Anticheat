package cc.funkemunky.fiona.detections.combat.aimassist.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;

public class TypeD extends Detection {
    public TypeD(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.looked(e.getFrom(), e.getTo())) {
                float yaw, yawDif = ((yaw = e.getTo().getYaw()) - e.getFrom().getYaw());

                if (yaw % 0.5 == 0 && !data.isBeingCancelled && data.apIntVerbose.flag(10, 550L)) {
                    flag(data, "t: a; " + yaw, 1, true, true);
                } else if (yawDif % 0.1 == 0 && !data.isBeingCancelled && data.apIntVerbose.flag(20, 450L)) {
                    flag(data, "t: b; " + yawDif, 1, true, true);
                }
            }
        }
    }
}
