package cc.funkemunky.fiona.detections.combat.killaura.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketAttackEvent;

public class TypeK extends Detection {
    public TypeK(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("invInteractTickThreshold", 10);
        addConfigValue("threshold", 5);
        addConfigValue("resetTime", 200);
        addConfigValue("violationsToFlag", 5);
        setThreshold((int) getConfigValues().get("violationsToFlag"));
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketAttackEvent) {
            if (data.lastInvClick.hasNotPassed((int) getConfigValues().get("invInteractTickThreshold"))
                    && data.killauraInvVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"))) {
                flag(data, "t: " + data.killauraInvVerbose.getVerbose(), 1, true, true);
            }
        }
    }
}
