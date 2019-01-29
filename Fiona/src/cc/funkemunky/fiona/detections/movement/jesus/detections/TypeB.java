package cc.funkemunky.fiona.detections.movement.jesus.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.BlockUtils;
import cc.funkemunky.fiona.utils.MathUtils;

public class TypeB extends Detection {
    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("threshold", 10);
        addConfigValue("resetTime", 250);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (BlockUtils.isLiquid(e.getPlayer().getLocation().subtract(0, 0.1, 0).getBlock())
                    && !BlockUtils.isLiquid(e.getPlayer().getLocation().clone().add(0, 0.2, 0).getBlock())
                    && !data.generalCancel
                    && !data.isVelocityTaken()
                    && data.webTicks == 0) {

                float yDelta = (float) MathUtils.getVerticalDistance(e.getFrom(), e.getTo());

                if (!data.onGround && data.jesusWalkVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"))) {
                    flag(data, data.jesusWalkVerbose.getVerbose() + ">-" + getConfigValues().get("threshold"), 1, true, true);
                }
                debug(data, data.jesusWalkVerbose.getVerbose() + ": " + yDelta + ", " + data.onGround);
            }
        }
    }
}
