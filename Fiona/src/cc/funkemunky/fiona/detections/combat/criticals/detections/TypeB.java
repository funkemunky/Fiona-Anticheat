package cc.funkemunky.fiona.detections.combat.criticals.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketAttackEvent;
import cc.funkemunky.fiona.utils.MathUtils;

public class TypeB extends Detection {

    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);


        addConfigValue("threshold", 10);
        addConfigValue("resetTime", 1000);
        addConfigValue("verboseToAdd", 2);
        addConfigValue("minFallDistance", 0);
        addConfigValue("minGroundTicks", 10);
        addConfigValue("ticksSinceLastVelocity", 7);
        setThreshold(3);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketAttackEvent) {
            PacketAttackEvent e = (PacketAttackEvent) event;

            if (!data.generalCancel
                    && data.blockTicks == 0
                    && !data.onHalfBlock
                    && !data.inLiquid
                    && data.groundTicks > (int) getConfigValues().get("minGroundTicks")
                    && e.getAttacker().getFallDistance() > (int) getConfigValues().get("minFallDistance")
                    && data.lastVelocity.hasPassed((int) getConfigValues().get("ticksSinceLastVelocity"))
                    && !data.onSlimeBefore) {
                if (data.criticalsFallVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"), (int) getConfigValues().get("verboseToAdd"))) {
                    flag(data, MathUtils.round(e.getAttacker().getFallDistance(), 3) + ">-" + (int) getConfigValues().get("minFallDistance"), 1, true, true);
                }
            } else {
                data.criticalsFallVerbose.deduct();
            }
        }
    }
}
