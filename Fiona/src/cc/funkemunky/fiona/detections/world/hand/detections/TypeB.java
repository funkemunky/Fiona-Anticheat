package cc.funkemunky.fiona.detections.world.hand.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import com.ngxdev.tinyprotocol.api.Packet;

public class TypeB extends Detection {
    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketRecieveEvent) {
            PacketRecieveEvent e = (PacketRecieveEvent) event;

            if (data.lagTick) {
                return;
            }
            if (e.getType().equals(Packet.Client.BLOCK_DIG)) {
                long delta = MathUtils.elapsed(data.lastPlacePacket);
                if (delta < 20) {
                    if (data.handAnimationVerbose.flagB(15, data.handAnimationVerbose.getVerbose() < 17 ? 2 : 0)) {
                        flag(data, delta + "<-20", 1, true, true);
                    }
                    debug(data, delta + "ms");
                } else {
                    data.handAnimationVerbose.deduct();
                }
            } else if (e.getType().equalsIgnoreCase(Packet.Client.BLOCK_PLACE)) {
                data.lastPlacePacket = System.currentTimeMillis();
            }
        }
    }
}
