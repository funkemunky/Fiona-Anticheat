package cc.funkemunky.fiona.detections.movement.jesus.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.BlockUtils;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;

public class TypeA extends Detection {
    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("threshold", 10);
        addConfigValue("verboseToAdd", 2);
        addConfigValue("liquidTicksRequiredForLow", 50);
        addConfigValue("speedThreshold.low", 0.15);
        addConfigValue("speedThreshold.high", 0.18);
        addConfigValue("speedThreshold.1_13_Increment", 0.05);
        addConfigValue("depthStriderMultiplier", 0.06);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;
            if (MathUtils.moved(e.getFrom(), e.getTo())
                    && data.inLiquid
                    && BlockUtils.isLiquid(e.getPlayer().getLocation().subtract(0, 1, 0).getBlock())
                    && data.webTicks == 0
                    && !data.isVelocityTaken()
                    && !data.generalCancel) {
                double threshold = data.liquidTicks == (int) getConfigValues().get("liquidTicksRequiredForLow") ? (double) getConfigValues().get("speedThreshold.low") : (double) getConfigValues().get("speedThreshold.high");

                if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
                    threshold+= (float) (double) getConfigValues().get("speedThreshold.1_13_Increment");
                }

                threshold += PlayerUtils.getDepthStriderLevel(e.getPlayer()) * (double) getConfigValues().get("depthStriderMultiplier");

                if (data.movement.deltaXZ > threshold) {
                    if (data.jesusSpeedVerbose.flagB((int) getConfigValues().get("threshold"), (int) getConfigValues().get("verboseToAdd"))) {
                        flag(data, MathUtils.round(data.movement.deltaXZ, 4) + ">-" + MathUtils.trim(3, threshold), 1, true, true);
                    }
                } else {
                    data.jesusSpeedVerbose.deduct();
                }

            }
        }
    }
}
