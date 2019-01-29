package cc.funkemunky.fiona.detections.movement.sprint.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import org.bukkit.util.Vector;

public class TypeA extends Detection {
    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("violationToFlag", 10);
        setThreshold((int) getConfigValues().getOrDefault("violationToFlag", 10));
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.moved(e.getFrom(), e.getTo())
                    && !data.generalCancel
                    && data.liquidTicks == 0
                    && !data.inWeb
                    && data.lastTeleport.hasPassed(10)
                    && data.slimeTicks == 0
                    && e.getPlayer().getWalkSpeed() == 0.2
                    && !data.isVelocityTaken()) {
                Vector movement = new Vector(e.getTo().getX() - e.getFrom().getX(), 0, e.getTo().getZ() - e.getFrom().getZ()),
                        direction = new Vector(-Math.sin(e.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(e.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F);

                double delta = movement.distanceSquared(direction); //The distance between the player's actual velocity and what their velocity should be.

                if (delta > 0.22 //This is the delta if greater would be derived from walking on their direction's x axis or backwards.
                        && data.isFullyOnGround() //To prevent potential false positvies in fights or falling.
                        && e.getPlayer().isSprinting()) {
                    if (data.sprintOmniVerbose.flag(delta > 0.138 && data.onGround ? 7 : 15, 400L)) {
                        flag(data, delta + ">-0.22", 1, true, true);
                    }
                } else {
                    data.sprintOmniVerbose.deduct();
                }
                debug(data, data.sprintOmniVerbose.getVerbose() + ": [" + movement.toString() + "]; [" + direction.toString() + "]" + ", " + delta);
            }
        }
    }

    private boolean sameSign(double one, double two) {
        return (one > 0 && two > 0) || (one < 0 && two < 0);
    }
}
