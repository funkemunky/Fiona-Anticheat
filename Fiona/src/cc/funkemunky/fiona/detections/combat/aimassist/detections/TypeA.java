package cc.funkemunky.fiona.detections.combat.aimassist.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;

public class TypeA extends Detection {
    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("yawThreshold", 0.05);
        addConfigValue("minYawMovement", 0.75);
        addConfigValue("threshold", 20);
        addConfigValue("resetTime", 400);
        addConfigValue("violationsToFlag", 10);
        setThreshold((int) getConfigValues().get("violationsToFlag"));
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.looked(e.getFrom(), e.getTo()) && !data.usingOptifine) {
                float yawDelta = Math.abs(data.yawMovement - data.lastYawMovement), max = (float) (double) getConfigValues().get("yawThreshold");
                if (yawDelta < max && data.yawDelta > (double) getConfigValues().get("minYawMovement") && data.pitchDelta > 0 && data.aimPatternYawVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"))) {
                    flag(data, yawDelta + "<-" + max, 1, true, true);
                }
                debug(data, data.aimPatternYawVerbose.getVerbose() + ": " + yawDelta + ", " + data.pitchMovement);
            }
        }
    }
}
