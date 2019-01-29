package cc.funkemunky.fiona.detections.combat.killaura.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketAttackEvent;
import cc.funkemunky.fiona.utils.BoundingBox;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.MiscUtils;
import cc.funkemunky.fiona.utils.math.RayTrace;

public class TypeH extends Detection {

    public TypeH(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("expandDefault", 0.4);
        addConfigValue("distanceDivisor", 30);
        addConfigValue("threshold", 20);
        addConfigValue("resetTime", 400);
        addConfigValue("addition", 3);
        addConfigValue("deduction", 1);

        addConfigValue("violationsToFlag", 20);
        setThreshold((int) getConfigValues().get("violationsToFlag"));
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketAttackEvent) {
            PacketAttackEvent e = (PacketAttackEvent) event;

            if (MathUtils.getHorizontalDistance(data.player.getLocation(), e.getAttacked().getLocation()) > 1.5) {
                RayTrace trace = new RayTrace(e.getAttacker().getEyeLocation().toVector(), e.getAttacker().getEyeLocation().getDirection());

                double expandDefault = (double) getConfigValues().get("expandDefault");
                float expand = (float) e.getAttacker().getLocation().toVector().distance(e.getAttacked().getLocation().toVector()) / (int) getConfigValues().get("distanceDivisor");
                BoundingBox entityBox = MiscUtils.getEntityBoundingBox(e.getAttacked()).grow((float) expandDefault + expand, (float) expandDefault + expand, (float) expandDefault + expand);

                if (entityBox.maxY - entityBox.minY > 1.0) {
                    double angle = Math.abs(e.getAttacked().getLocation().toVector().subtract(e.getAttacker().getLocation().toVector()).normalize().dot(e.getAttacked().getEyeLocation().getDirection()));

                    if (!trace.intersects(entityBox, e.getAttacker().getLocation().toVector().distance(e.getAttacked().getLocation().toVector()), 0.1)) {
                        if (data.killauraDirectionVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"), (int) getConfigValues().get("addition"))) {
                            flag(data, "a: " + MathUtils.round(angle, 4), 1, true, true);
                        }
                    } else {
                        data.killauraDirectionVerbose.deduct((int) getConfigValues().get("deduction"));
                    }
                    debug(data, data.killauraDirectionVerbose.getVerbose() + ": " + entityBox.toString() + ", " + angle + ", " + expand);
                }
            }
        }
    }
}
