package cc.funkemunky.fiona.detections.combat.aimassist.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;

public class TypeB extends Detection {

    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("minYawDelta", 0.75);
        addConfigValue("threshold", 18);
        addConfigValue("resetTime", 850);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.looked(e.getFrom(), e.getTo())) {
                float yawDelta = Math.abs(e.getFrom().getYaw() - e.getTo().getYaw()),
                        pitchDelta = Math.abs(e.getFrom().getPitch() - e.getTo().getPitch());
                float deltaDifference = Math.abs(yawDelta - data.lastRepeatYawDelta);

                if (deltaDifference == 0
                        && yawDelta > (double) getConfigValues().get("minYawDelta")
                        && data.aaRepeatVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"))) {
                    flag(data, "t: " + data.aaRepeatVerbose.getVerbose() + " d: " + MathUtils.round(yawDelta, 5), 1, true, false);
                }

                debug(data, data.aaRepeatVerbose.getVerbose() + ": " + yawDelta + ", " + deltaDifference + ", " + pitchDelta);
                data.lastRepeatYawDelta = yawDelta;
            }
        }
    }
}
