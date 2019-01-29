package cc.funkemunky.fiona.detections.combat.killaura.detections;

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

        addConfigValue("threshold.normal", 14);
        addConfigValue("threshold.isLagging", 25);
        addConfigValue("verboseToAdd", 1);
        addConfigValue("verboseToDeduct", 1);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketRecieveEvent) {
            PacketRecieveEvent e = (PacketRecieveEvent) event;

            if (e.getType().equals(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket use = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());

                if (use.getAction() != WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) return;

                long elapsed = MathUtils.elapsed(data.lastFlyingA);
                if (elapsed < 40 && !data.hasLag() && !data.lagTick) {
                    if (data.postKillauraVerbose.flag(data.hasLag() ? (int) getConfigValues().get("threshold.isLagging") : (int) getConfigValues().get("threshold.normal"), data.ping / 2 + 350L, (int) getConfigValues().get("verboseToAdd"))) {
                        flag(data, elapsed + "<-40", 1, true, true);
                    }
                } else {
                    data.postKillauraVerbose.deduct((int) getConfigValues().get("verboseToDeduct"));
                }
                debug(data, data.postKillauraVerbose.getVerbose() + ": " + elapsed);
            } else if (e.getType().contains("Flying") || e.getType().contains("Look") || e.getType().contains("Position")) {
                data.lastFlyingA = System.currentTimeMillis();
            }
        }
    }
}
