package cc.funkemunky.fiona.detections.combat.fastbow.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.utils.MiscUtils;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class TypeA extends Detection {

    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("minimumArrowVelocity", 0.12);
        addConfigValue("velocityRequiredForHigh", 1.2);
        addConfigValue("velocityThreshold.high", 280.0);
        addConfigValue("velocityThreshold.low", 140.0);
        addConfigValue("threshold", 3);
        addConfigValue("resetTime", 500);
    }

    @Override
    public void onBukkitEvent(org.bukkit.event.Event event, PlayerData data) {
        if (event instanceof ProjectileLaunchEvent) {
            ProjectileLaunchEvent e = (ProjectileLaunchEvent) event;
            if (e.getEntity() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getEntity();
                if (arrow.getVelocity().length() < 0.12) {
                    return;
                }

                long threshold = Math.round(arrow.getVelocity().length() > (double) getConfigValues().get("velocityRequiredForHigh") ? (double) getConfigValues().get("velocityThreshold.high") * arrow.getVelocity().length() : (double) getConfigValues().get("velocityThreshold.low") * arrow.getVelocity().length()) / 50;
                if (data.lastBow.hasNotPassed(threshold)) {
                    if ((data.fastbowVerbose = Math.min((int) getConfigValues().get("threshold") + 1, data.fastbowVerbose + 1)) > (int) getConfigValues().get("threshold")) {
                        flag(data, data.lastBow.getPassed() + "<-" + threshold, 1, false, false);
                        e.setCancelled(MiscUtils.canCancel(this, data));
                    }
                } else {
                    data.fastbowVerbose = Math.max(0, data.fastbowVerbose - 1);
                }

                debug(data, data.lastBow.getPassed() + ", " + threshold);
                data.lastBow.reset();
            }
        }
    }
}