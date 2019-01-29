package cc.funkemunky.fiona.detections.combat.killaura.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;

public class TypeI extends Detection {
    public TypeI(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("minimumSpeedXZ", 0.24);
        addConfigValue("thresholdMultiplier", 1.5);
        addConfigValue("threshold", 15);
        addConfigValue("resetTime", 600);
        setExperimental(true);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.looked(e.getFrom(), e.getTo())
                    && data.lastAttack.hasNotPassed(3)
                    && data.lastHitEntity != null) {

                float[] rotations = MathUtils.getRotations(data, data.lastHitEntity);

                float value = MathUtils.getDelta(e.getTo().getPitch(), rotations[1]);
                float threshold = (float) data.lastHitEntity.getLocation().toVector().distance(e.getTo().toVector()) + (float) data.movement.deltaXZ * (float) (double) getConfigValues().get("thresholdMultiplier");

                if (value < threshold
                        && data.movement.deltaXZ > (double) getConfigValues().get("minimumSpeedXZ")
                        && data.kaAngleVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"))) {
                    flag(data, "t: b [" + MathUtils.round(value, 4) + "<-" + MathUtils.round(threshold, 4) + "]", 1, true, true);
                }

                debug(data, value + ", " + data.kaAngleVerbose.getVerbose());
            }
        }
    }
}
