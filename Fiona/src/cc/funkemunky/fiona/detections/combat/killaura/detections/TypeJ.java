package cc.funkemunky.fiona.detections.combat.killaura.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketAttackEvent;

public class TypeJ extends Detection {
    public TypeJ(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("threshold", 4);
        addConfigValue("resetTime", 500);
        addConfigValue("violationsToFlag", 5);
        setThreshold((int) getConfigValues().get("violationsToFlag"));
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketAttackEvent) {
            PacketAttackEvent e = (PacketAttackEvent) event;

            if (e.getAttacker().isDead() && data.killauraDeadVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"))) {
                flag(data, "*", 1, false, true);
            }
        }
    }
}
