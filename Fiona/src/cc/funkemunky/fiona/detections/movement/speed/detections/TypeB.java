package cc.funkemunky.fiona.detections.movement.speed.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;
import cc.funkemunky.fiona.utils.ReflectionsUtil;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class TypeB extends Detection {

    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("violationToFlag", 50);
        setThreshold((int) getConfigValues().getOrDefault("violationToFlag", 50));

        setExperimental(true);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (!PlayerUtils.isRiskyForFlight(data)
                    && !data.lagTick
                    && !data.isVelocityTaken()
                    && MathUtils.playerMoved(e.getFrom(), e.getTo())) {
                Block below = data.player.getLocation().clone().subtract(0, 1, 0).getBlock();

                Vector movement = new Vector(e.getTo().getX() - e.getFrom().getX(), 0, e.getTo().getZ() - e.getFrom().getZ()),
                        direction = new Vector(-Math.sin(e.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(e.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F);

                double delta = movement.distanceSquared(direction); //The distance between the player's actual velocity and what their velocity should be.

                float deltaXZ = (float) Math.hypot(e.getTo().getX() - e.getFrom().getX(), e.getTo().getZ() - e.getFrom().getZ()),
                        friction = !data.onGroundFive || !below.getType().isSolid() ? 0.68f : ReflectionsUtil.getFriction(below),
                        resistance = !e.isOnGround() ? 0.91f : friction * 0.91f,
                        predicted = Math.max(data.lastHDeltaXZ * resistance, getMinimumSpeed(data, delta));

                if (deltaXZ / predicted > 1.26f && !data.onGround && data.player.getWalkSpeed() == 0.2) {
                    if (data.speedTypeBVerbose.flag(12, 550L) || deltaXZ / predicted > 5) {
                        flag(data, deltaXZ / predicted + "> 1.1", 1, true, true);
                    }
                } else {
                    data.speedTypeBVerbose.deduct();
                }
                debug(data, deltaXZ + ", " + predicted + ", " + deltaXZ / predicted);
                data.lastHDeltaXZ = deltaXZ;
            }
        }
    }

    private float getMinimumSpeed(PlayerData data, double delta) {
        float base = data.player.isSprinting() ? 0.2806f : 0.2159f + (delta > 0.12f ? 0.006f : 0);

        base += (data.player.getWalkSpeed() - 0.2f);
        base += PlayerUtils.getPotionEffectLevel(data.player, PotionEffectType.SPEED) * 0.06f;
        return base;
    }
}
