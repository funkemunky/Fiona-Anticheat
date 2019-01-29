package cc.funkemunky.fiona.detections.player.badpackets.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;

public class TypeE extends Detection {
    public TypeE(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (Math.abs(e.getTo().getPitch()) > 90) {
                flag(data, Math.abs(e.getTo().getPitch()) + ">-90", 1, false, true);
            }
        }
    }
}
