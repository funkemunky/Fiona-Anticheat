package cc.funkemunky.fiona.detections.movement.speed.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

public class TypeA extends Detection {
    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("speedThreshold.air.init", 0.37);
        addConfigValue("speedThreshold.air.multiplier", 0.99);
        addConfigValue("speedThreshold.air.maxTicks", 16);
        addConfigValue("speedThreshold.ground.init", 0.34);
        addConfigValue("speedThreshold.ground.subtract", 0.0055);
        addConfigValue("speedThreshold.ground.maxTicks", 9);
        addConfigValue("speedThreshold.noSprint", 0.22);
        addConfigValue("speedThreshold.sneak", 0.15);
        addConfigValue("threshold", 20);
        addConfigValue("resetTime", 1200);
        addConfigValue("verboseToAdd", 2);
        addConfigValue("verboseToDeduct", 1);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (!data.generalCancel && MathUtils.moved(e.getFrom(), e.getTo())) {
                double baseSpeed;


                if (data.airTicks > 0) {
                    baseSpeed = (double) getConfigValues().get("speedThreshold.air.init") * Math.pow((double) getConfigValues().get("speedThreshold.air.multiplier"), Math.min((int) getConfigValues().get("speedThreshold.air.maxTicks"), data.airTicks));
                } else {
                    baseSpeed = (double) getConfigValues().get("speedThreshold.ground.init") - ((double) getConfigValues().get("speedThreshold.ground.subtract") * Math.min((int) getConfigValues().get("speedThreshold.ground.maxTicks"), data.groundTicks));
                }

                baseSpeed += PlayerUtils.getPotionEffectLevel(data.player, PotionEffectType.SPEED) * (data.onGround ? 0.06f : 0.045f);
                baseSpeed *= data.halfBlockTicks > 0 ? 2.5 : 1;
                baseSpeed *= data.blockTicks > 0 ? 3.4 : 1;
                baseSpeed *= data.iceTicks > 0 && data.groundTicks < 6 ? 2.5f : 1.0;
                baseSpeed += data.slimeTicks > 0 ? 0.1 : 0;
                baseSpeed += data.lastBlockPlace.hasNotPassed(15) ? 0.1 : 0;
                baseSpeed += (data.player.getWalkSpeed() - 0.2) * 2.0f;

                if (data.movement.deltaXZ > baseSpeed && !data.isVelocityTaken()) {
                    if (data.limitVerbose.flag(data.lagTick || data.hasLag() ? 45 : 38, 1200L, (data.movement.deltaXZ / baseSpeed > 1.4 ? 3 : 2))) {
                        flag(data, MathUtils.round(data.movement.deltaXZ, 4) + ">-" + MathUtils.round(baseSpeed, 4), 1, false, true);
                    }
                } else {
                    data.limitVerbose.deduct();
                }

                debug(data, data.movement.deltaXZ + ", " + baseSpeed + ", " + data.groundTicks + ", " + data.airTicks + ", " + data.iceTicks + ", " + data.halfBlockTicks);
            }
        }
    }
}
