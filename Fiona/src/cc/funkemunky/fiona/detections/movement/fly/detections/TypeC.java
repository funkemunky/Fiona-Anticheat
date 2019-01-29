package cc.funkemunky.fiona.detections.movement.fly.detections;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

public class TypeC extends Detection {

    public TypeC(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("expandBoxBy", 1.0);
        addConfigValue("threshold", 8);
        addConfigValue("addVerbose", 2);
        addConfigValue("violationToFlag", 50);
        setThreshold((int) getConfigValues().get("violationToFlag"));
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.playerMoved(e.getFrom(), e.getTo())) {
                float nextY = nextY((float) data.movement.deltaY), lastY = lastY((float) data.movement.deltaY);

                if (!PlayerUtils.isRiskyForFlight(data)
                        && (data.lastBlockPlace.hasPassed(25) || data.airTicks > 4)
                        && !data.blocksOnTop
                        && !data.player.hasPotionEffect(PotionEffectType.JUMP)
                        && !data.onGround
                        && Fiona.getInstance().getBlockBoxManager().getBlockBox().getCollidingBoxes(data.player.getWorld(),  data.boundingBox.subtract(0, (float) (double) getConfigValues().get("expandBoxBy"), 0, 0, 0, 0)).size() == 0) {
                    boolean shouldFlag = shouldFlag((float) data.movement.deltaY, lastY, data);
                    if (shouldFlag) {
                        if (data.motionVerbose.flagB((int) getConfigValues().get("threshold") + (data.lagTick ? 8 : 0), (int) getConfigValues().get("addVerbose"))) {
                            flag(data, "[" + data.movement.deltaY + "<-" + data.nextY + "], [" + data.movement.lastDeltaY + "<-" + lastY + "]", 1, false, true);
                        }
                    } else {
                        data.motionVerbose.deduct();
                    }
                    debug(data, data.motionVerbose.getVerbose() + ": (" + data.movement.deltaY + ", " + data.nextY + ") (" + lastY + ", " + data.movement.lastDeltaY + "), " + shouldFlag);
                } else {
                    data.motionVerbose.setVerbose(0);
                }
                data.nextY = nextY;
            }
        }
    }

    private float lastY(float yMotion) {
        return yMotion * 1.020408163f + 0.08f;
    }

    private float nextY(float yMotion) {
        return (yMotion - 0.08f) * 0.98f;
    }

    private boolean shouldFlag(float deltaY, float lastY, PlayerData data) {
        return MathUtils.getDelta(deltaY, data.nextY) > 0.01f && MathUtils.getDelta(data.movement.lastDeltaY, lastY) > 0.01f;
    }
}
