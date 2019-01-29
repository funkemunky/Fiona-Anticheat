package cc.funkemunky.fiona.detections.combat.killaura.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import net.minecraft.util.com.google.common.util.concurrent.AtomicDouble;

import java.util.Collections;

public class TypeF1_7 extends Detection {
    public TypeF1_7(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("violationsToFlag", 5);
        setThreshold((int) getConfigValues().get("violationsToFlag"));
        setVersionMaxmimum(ProtocolVersion.V1_7_10);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.looked(e.getFrom(), e.getTo())
                    && data.lastAttack.hasNotPassed(4)
                    && data.lastHitEntity != null) {

                if (data.offsetArray[0] < 30) {

                    if (data.offsets.size() >= 20) {
                        Collections.sort(data.offsets);

                        AtomicDouble atomicDouble = new AtomicDouble();
                        data.offsets.forEach(atomicDouble::getAndAdd);

                        float average = atomicDouble.floatValue() / data.offsets.size();
                        double min = data.offsets.get(0);
                        double max = data.offsets.get(data.offsets.size() - 1);
                        double range = Math.abs(max - min);

                        double threshold = data.lastHitEntity.getVelocity().lengthSquared() < 0.3 ? 1 : e.getPlayer().getEyeLocation().distance(data.lastHitEntity.getEyeLocation()) * 1.5 + data.movement.deltaXZ * 8 / (data.offsetArray[0] + data.offsetArray[1]);
                        if (range < threshold
                                && MathUtils.getYawDifference(e.getFrom().toLocation(), e.getTo().toLocation()) > 0.5) {
                            if (data.killauraOffsetVerbose.flag(14, 1250L, range < 3 ? 6 : 4)) {
                                flag(data, range + "<-" + threshold, 1, false, true);
                            }
                            debug(data, "Verbose: " + data.killauraOffsetVerbose.getVerbose());
                        } else {
                            data.killauraOffsetVerbose.deduct();
                        }

                        debug(data, "Range: " + average);
                        data.lastAuraRange = range;
                        data.offsets.clear();
                    } else {
                        data.offsets.add(data.offsetArray[0] + data.offsetArray[1]);
                    }
                }
            }
        }
    }
}
