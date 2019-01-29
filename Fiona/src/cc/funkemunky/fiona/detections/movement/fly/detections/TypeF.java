package cc.funkemunky.fiona.detections.movement.fly.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;

public class TypeF extends Detection {
    public TypeF(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);
    }

    //TODO Test for false positives inside all possible vehicles.
    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.playerMoved(e.getFrom(), e.getTo()) && e.getPlayer().isInsideVehicle() && !e.getPlayer().getAllowFlight()) {

                if (data.movement.deltaY > 0 && MathUtils.getDelta(data.movement.deltaY, data.movement.lastDeltaY) < 1E-5 && !PlayerUtils.isGliding(data.player)) {
                    if (data.flyTypeFVerbose.flag(12, 650L)) {
                        flag(data, MathUtils.getDelta(data.movement.deltaY, data.movement.lastDeltaY) + "< 0.00001", 1, true, true);
                    }
                } else {
                    data.flyTypeFVerbose.deduct();
                }
            }
        }
    }
}
