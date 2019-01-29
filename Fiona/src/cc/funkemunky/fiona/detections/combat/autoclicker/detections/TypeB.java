package cc.funkemunky.fiona.detections.combat.autoclicker.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketArmSwingEvent;
import cc.funkemunky.fiona.utils.MathUtils;

public class TypeB extends Detection {
    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("toAdd.likely", 1);
        addConfigValue("toAdd.high", 3);
        addConfigValue("minSwingDelta", 58);
        addConfigValue("timeDeltaThreshold", 52);
        addConfigValue("threshold", 110);
        addConfigValue("resetTime", 650);
        addConfigValue("combatOnly", false);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketArmSwingEvent) {
            PacketArmSwingEvent e = (PacketArmSwingEvent) event;
            boolean combat = !(boolean) getConfigValues().get("combatOnly") || data.lastAttack.hasNotPassed(5);
            if (!e.isLookingAtBlock() && combat) {
                long swing = MathUtils.elapsed(data.lastArmSwing);
                long swingDelta = Math.abs(swing - data.lastSwingDelta);

                if (swingDelta < (int) getConfigValues().get("timeDeltaThreshold")
                        && swing > (int) getConfigValues().get("minSwingDelta")) {
                    if (data.acConsistentVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"), swingDelta < 20 ? (int) getConfigValues().get("toAdd.high") : (int) getConfigValues().get("toAdd.likely"))) {
                        flag(data, "t: " + data.acConsistentVerbose.getVerbose() + " d: " + swingDelta, 1, true, true);
                    }
                } else {
                    data.acConsistentVerbose.deduct(((swingDelta > 98 && swingDelta < 100.8) ? 33 : 8));
                }

                debug(data, data.acConsistentVerbose.getVerbose() + ": " + swingDelta);

                data.lastSwingDelta = swing;
                data.lastArmSwing = System.currentTimeMillis();
            }
        }
    }
}
