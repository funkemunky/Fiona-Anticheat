package cc.funkemunky.fiona.detections.movement.fly.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;

public class TypeE extends Detection {
    public TypeE(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setVersionMinimum(ProtocolVersion.V1_9);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.playerMoved(e.getFrom(), e.getTo()) && !data.onGround && PlayerUtils.isGliding(e.getPlayer()) && !e.getPlayer().getAllowFlight()) {
                if (data.movement.deltaY >= 0 && data.flyTypeEVerbose.flag(15, 250L)) {
                    flag(data, data.movement.deltaY + ">-0", 1, true, true);
                }
            }
        }
    }
}
