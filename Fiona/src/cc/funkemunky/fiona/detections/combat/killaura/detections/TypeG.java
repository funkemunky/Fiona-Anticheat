package cc.funkemunky.fiona.detections.combat.killaura.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

public class TypeG extends Detection {
    public TypeG(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);

        addConfigValue("threshold", 4);
        addConfigValue("resetTime", 400);
        addConfigValue("toAdd", 2);
        addConfigValue("violationsToFlag", 5);
        setThreshold((int) getConfigValues().get("violationsToFlag"));
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketRecieveEvent) {
            PacketRecieveEvent e = (PacketRecieveEvent) event;

            switch (e.getType()) {
                case Packet.Client.USE_ENTITY:
                    WrappedInUseEntityPacket packet = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());

                    if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                        if (!data.hasSwung
                                && !data.generalCancel
                                && data.killauraSwingVerbose.flag(4, 400L, 2)) {
                            flag(data, String.valueOf(data.hasSwung), 1, true, true);
                        }

                        data.hasSwung = false;
                        debug(data, String.valueOf(data.hasSwung));
                    }
                    break;
                case Packet.Client.ARM_ANIMATION: {
                    data.hasSwung = true;
                    data.killauraSwingVerbose.setVerbose(0);
                }
                case Packet.Client.FLYING:
                    data.hasSwung = false;
            }
        }
    }
}
