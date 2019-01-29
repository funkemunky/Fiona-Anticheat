package cc.funkemunky.fiona.detections.movement.noslowdown.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

public class TypeA extends Detection {

    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("packetDeltaThreshold", 7);
        addConfigValue("skippedTicksMax", 4);
        addConfigValue("threshold", 6);
        addConfigValue("resetTime", 175);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketRecieveEvent) {
            PacketRecieveEvent e = (PacketRecieveEvent) event;

            if (data.lagTick) {
                return;
            }

            switch (e.getType()) {
                case Packet.Client.BLOCK_DIG:
                    data.blockDig = System.currentTimeMillis();
                    break;
                case Packet.Client.USE_ENTITY: {
                    WrappedInUseEntityPacket use = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());

                    if (use.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                        if (MathUtils.elapsed(data.blockDig) < (int) getConfigValues().get("packetDeltaThreshold")
                                && data.skippedTicks < (int) getConfigValues().get("skippedTicksMax")
                                && data.noSlowdownAVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"))) {
                            flag(data, "t: " + MathUtils.elapsed(data.blockDig), 1, true, true);
                        }
                    }
                    break;
                }
            }
        }
    }
}
