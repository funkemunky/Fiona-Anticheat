package cc.funkemunky.fiona.detections.world.hand.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.MiscUtils;
import com.ngxdev.tinyprotocol.api.Packet;

public class TypeA extends Detection {
    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketRecieveEvent) {
            PacketRecieveEvent e = (PacketRecieveEvent) event;

            if (data.lagTick) {
                return;
            }

            switch (e.getType()) {
                case Packet.Client.BLOCK_PLACE: {
                    long elapsed = MathUtils.elapsed(data.lastIllegalFlying);
                    if (elapsed < 15) {
                        if (data.handIllegalVerbose.flag(6, 850L)) {
                            flag(data, elapsed + "-<15", 1, true, true);
                            e.setCancelled(MiscUtils.canCancel(this, data));
                        }
                    } else {
                        data.handIllegalVerbose.deduct();
                    }
                    debug(data, elapsed + "ms");
                    data.lastBlock.reset();
                    break;
                }
                case Packet.Client.POSITION_LOOK:
                case Packet.Client.LOOK:
                case Packet.Client.POSITION:
                case Packet.Client.LEGACY_POSITION:
                case Packet.Client.LEGACY_LOOK:
                case Packet.Client.LEGACY_POSITION_LOOK:
                    data.lastIllegalFlying = System.currentTimeMillis();
                    break;
            }
        }
    }
}
