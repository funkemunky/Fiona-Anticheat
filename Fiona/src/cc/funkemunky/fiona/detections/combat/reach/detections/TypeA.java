package cc.funkemunky.fiona.detections.combat.reach.detections;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketAttackEvent;
import cc.funkemunky.fiona.utils.BoundingBox;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.MiscUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class TypeA extends Detection {

    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("staticThreshold", 3.1);
        addConfigValue("dynamicThreshold", 3.5);
        addConfigValue("latencyMultiplier", 0.0035);
        addConfigValue("velocityMultiplier", 1.3);
        addConfigValue("threshold", 8);
        addConfigValue("resetTime", 2500);

        addConfigValue("violationsToFlag", 3);
        setThreshold((int) getConfigValues().get("violationsToFlag"));
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketAttackEvent) {
            PacketAttackEvent e = (PacketAttackEvent) event;

            if (data.generalCancel) {
                return;
            }

            LivingEntity entity = e.getAttacked();
            BoundingBox entityBox = MiscUtils.getEntityBoundingBox(entity);

            if (Math.abs(entityBox.maxY - entityBox.minY) < 1.4f) {
                return;
            }

            float threshold = (float) ((double) getConfigValues().get(entity.getVelocity().length() < 0.08 ? "staticThreshold" : "dynamicThreshold"));

            if (threshold > 3.0f) {
                threshold += data.ping * (double) getConfigValues().get("latencyMultiplier");

                if (e.getAttacked() instanceof Player) {
                    PlayerData dataAttacked = Fiona.getInstance().getDataManager().getPlayerData((Player) e.getAttacked());

                    if (dataAttacked != null) {
                        threshold += dataAttacked.ping * (double) getConfigValues().get("latencyMultiplier");
                    }
                }
                threshold += Math.abs(entity.getVelocity().length()) * (double) getConfigValues().get("velocityMultiplier");
            }
            float directionY = (float) Math.abs(e.getAttacker().getEyeLocation().getDirection().normalize().getY()) / 1.3f;

            if (threshold < 3) {
                threshold = 3;
            }

            entityBox = entityBox.grow(threshold, threshold, threshold);

            if (!entityBox.intersectsWithBox(data.boundingBox) && MathUtils.round(entity.getEyeLocation().toVector().distance(e.getAttacker().getEyeLocation().toVector())) > threshold
                    && (data.reachAVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"), data.lastReachAttack.hasPassed(10) ? 3 : 1))) {
                flag(data, MathUtils.round(entity.getEyeLocation().toVector().distance(e.getAttacker().getEyeLocation().toVector()), 4) + ">-" + threshold, 1, true, true);
            }

            debug(data, data.reachAVerbose.getVerbose() + ", " + directionY + ", (" + entity.getEyeLocation().toVector().distance(e.getAttacker().getEyeLocation().toVector()) + ">-" + threshold + ")" + ", " + data.lastReachAttack.getPassed());
        }
    }
}