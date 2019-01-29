package cc.funkemunky.fiona.detections.movement.fly.detections;

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

        setThreshold(20);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.playerMoved(e.getFrom(), e.getTo())) {
                float deltaY = (float) data.movement.deltaY;

                if (Math.abs(data.motionY) < 0.005) {
                    data.motionY = 0;
                    if (data.movement.deltaXZ == 0) {
                        updateY(data, deltaY, e.isOnGround());
                    }
                }

                if (data.movement.hasJumped) {
                    jump(data);
                }

                debug(data, deltaY + ", " + data.motionY + ", " + MathUtils.round(e.getTo().getY(), 3) + ", " + MathUtils.round(e.getFrom().getY(), 3) + ", " + data.movement.hasJumped + ", " + e.isOnGround() + ", " + data.movement.inAir);

                if (MathUtils.getDelta(deltaY, data.motionY) > 0.15
                        && !PlayerUtils.isRiskyForFlight(data)
                        && !data.onGround
                        && deltaY > -0.95
                        && !data.player.hasPotionEffect(PotionEffectType.JUMP)
                        && (data.blockTicks == 0 || deltaY == 0)
                        && data.flyAVerbose.flag(deltaY > 0 ? 5 : 10, 400L)) {
                    flag(data, deltaY + ">-" + data.motionY, 1, false, true);
                }
                updateY(data, deltaY, e.isOnGround());
            }
        }
    }

    private float getJumpUpwardsMotion(PlayerData data) {
        return 0.42F + PlayerUtils.getPotionEffectLevel(data.player, PotionEffectType.JUMP) * 0.11f;
    }

    private void updateY(PlayerData data, float deltaY, boolean onGround) {
        if (data.generalCancel) {
            data.motionY = deltaY;
        } else if (data.onClimbable) {
            data.motionY = 0.2f;
        } else if (!data.onGround) {
            data.motionY -= 0.08f;
            data.motionY *= 0.9800000190734863f;
        } else {
            data.motionY = 0;
        }
    }

    private void jump(PlayerData data) {
        data.motionY = this.getJumpUpwardsMotion(data);
    }
}