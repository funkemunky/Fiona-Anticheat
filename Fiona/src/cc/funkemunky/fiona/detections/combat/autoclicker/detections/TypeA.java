package cc.funkemunky.fiona.detections.combat.autoclicker.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketArmSwingEvent;

public class TypeA extends Detection {

    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("clickThreshold", 30);
        addConfigValue("combatOnly", false);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketArmSwingEvent) {
            PacketArmSwingEvent e = (PacketArmSwingEvent) event;

            boolean combat = !(boolean) getConfigValues().get("combatOnly") || data.lastAttack.hasNotPassed(5);
            if (!e.isLookingAtBlock() && combat) {
                if (data.cpsResetTime.hasPassed(10)) {
                    if (data.clicks > ((int) getConfigValues().get("clickThreshold") / 2)) {
                        flag(data, "cps: " + (data.clicks * 2), 1, true, true);
                    }
                    data.clicks = 0;
                    data.cpsResetTime.reset();
                } else {
                    data.clicks++;
                }
            }
        }
    }
}
