package cc.funkemunky.fiona.detections.player.badpackets.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import com.ngxdev.tinyprotocol.api.Packet;

public class TypeG extends Detection {
    public TypeG(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketRecieveEvent) {
            PacketRecieveEvent e = (PacketRecieveEvent) event;

            if (e.getType().equals(Packet.Client.FLYING) || e.getType().contains("Look") || e.getType().contains("Position")) {
                if (MathUtils.getDelta(data.ping, data.lastPing) < 5 && data.skippedTicks > 20) {
                    if (data.bpGVerbose.flag(100, 500L)) {
                        flag(data, MathUtils.getDelta(data.ping, data.lastPing) + "<-5->" + data.skippedTicks, 1, false, true);
                    }
                }
            } else {
                data.bpGVerbose.setVerbose(0);
            }
        }
    }
}
