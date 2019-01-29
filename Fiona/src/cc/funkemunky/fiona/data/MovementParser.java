package cc.funkemunky.fiona.data;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.events.system.EventManager;
import cc.funkemunky.fiona.utils.*;
import cc.funkemunky.fiona.utils.blockbox.CollisionAssessment;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.out.WrappedPacketPlayOutWorldParticle;
import com.ngxdev.tinyprotocol.packet.types.WrappedEnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Vehicle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class MovementParser {

    public static boolean parse(WrappedInFlyingPacket packet, PlayerData data) {
        Location to = data.movement.from.clone();

        //Only changing the location whenever it changes.
        if (packet.isPos()) {
            to.setX(packet.getX());
            to.setY(packet.getY());
            to.setZ(packet.getZ());
        }
        if (packet.isLook()) {
            to.setYaw(packet.getYaw());
            to.setPitch(packet.getPitch());
        }

        //Bukkit.broadcastMessage(packet.getPacketName());

        float minX = (float) Math.min(packet.getX() - 0.3f, packet.getX() + 0.3), minY = (float) Math.min(packet.getY(), packet.getY() + 1.8), minZ = (float) Math.min(packet.getZ() - 0.3f, packet.getZ() + 0.3),
                maxX = (float) Math.max(packet.getX() - 0.3f, packet.getX() + 0.3), maxY = (float) Math.max(packet.getY(), packet.getY() + 1.8), maxZ = (float) Math.max(packet.getZ() - 0.3f, packet.getZ() + 0.3);
        data.boundingBox = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);

        if (data.boxDebugEnabled
                && (data.boxDebugType == BoxDebugType.HITBOX
                || data.boxDebugType == BoxDebugType.ALL)) {
            for (float x = data.boundingBox.minX; x < data.boundingBox.maxX + 0.2; x += 0.2f) {
                for (float y = data.boundingBox.minY; y < data.boundingBox.maxY; y += 0.2f) {
                    for (float z = data.boundingBox.minZ; z < data.boundingBox.maxZ + 0.2; z += 0.2f) {
                        WrappedPacketPlayOutWorldParticle packet2 = new WrappedPacketPlayOutWorldParticle(WrappedEnumParticle.CRIT_MAGIC, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                        packet2.sendPacket(packet.getPlayer());
                    }
                }
            }

        }

        data.movement.lastDeltaY = data.movement.deltaY;
        data.movement.deltaY = to.getY() - data.movement.from.getY();
        data.movement.lastDeltaXZ = data.movement.deltaXZ;
        data.movement.deltaXZ = MathUtils.getHorizontalDistance(data.movement.from, to);

        if (data.movement.hasJumped) {
            data.movement.hasJumped = false;
            data.movement.inAir = true;
        }

        if (!data.movement.inAir && !packet.isGround() && data.movement.from.getY() < to.getY()) {
            data.timers.lastJump.reset();
            data.movement.hasJumped = true;

            data.movement.lastGroundY = data.movement.groundY;
            data.movement.groundY = MathUtils.floor(to.getY());
        } else if (packet.isGround()) {
            data.movement.inAir = false;
        }

        if (packet.isGround() || data.inLiquid || data.inWeb || data.player.isInsideVehicle() || data.lastTeleport.hasNotPassed(3)) {
            data.movement.realFallDistance = data.movement.slimeDistance = 0;
        } else if (data.movement.deltaY < 0) {
            data.movement.slimeDistance = (data.movement.realFallDistance -= data.movement.deltaY) * 1.5;
        }

        if (packet.isGround()) data.lastOnGround.reset();

        float yawDelta = Math.abs(data.movement.from.getYaw() - to.getYaw()), pitchDelta = Math.abs(data.movement.from.getPitch() - to.getPitch());
        float yawShit = shit(yawDelta), pitchShit = shit(pitchDelta);
        float smooth = data.yawSmooth.smooth(yawShit, yawShit * 0.05f), smooth2 = data.pitchSmooth.smooth(pitchShit, pitchShit * 0.05f);

        data.lastYawMovement = data.yawMovement;
        data.lastPitchMovement = data.pitchMovement;
        data.pitchMovement = pitchShit;
        data.yawMovement = yawShit;
        data.lastYawDelta = data.yawDelta;
        data.lastPitchDelta = data.pitchDelta;
        data.yawDelta = yawDelta;
        data.pitchDelta = pitchDelta;

        if(data.usingOptifine = Math.abs(smooth - yawShit) < 0.08f || Math.abs(pitchShit - smooth2) < 0.04f) {
            data.optifineTicks++;
        } else {
            data.optifineTicks = 0;
        }

        data.addPastLocation(new FionaLocation(to));

        data.fromBoundingBox = new BoundingBox(new Vector(data.movement.from.getX() - 0.3, data.movement.from.getY(), data.movement.from.getZ() - 0.3), new Vector(data.movement.from.getX() + 0.3, data.movement.from.getY() + 1.8, data.movement.from.getZ() + 0.3));

        List<BoundingBox> box = Fiona.getInstance().getBlockBoxManager().getBlockBox().getCollidingBoxes(to.getWorld(), data.boundingBox.grow(0.5f, 0.1f, 0.5f).subtract(0, 0.5f, 0, 0, 0, 0));

        CollisionAssessment assessment = new CollisionAssessment(data.boundingBox, data);
        data.player.getNearbyEntities(1,1,1).stream().filter(entity -> entity instanceof Vehicle).forEach(entity -> assessment.assessBox(ReflectionsUtil.toBoundingBox(ReflectionsUtil.getBoundingBox(entity)), to.getWorld(), true));

        box.forEach(bb -> assessment.assessBox(bb, to.getWorld(), false));

        data.fromOnGround = data.onGround;
        data.onGround = assessment.isOnGround();
        data.inLiquid = assessment.isInLiquid();
        data.blocksOnTop = assessment.isBlocksOnTop();
        data.pistonsNear = assessment.isPistonsNear();
        data.onHalfBlock = assessment.isOnHalfBlock();
        data.collidedHorizontally = assessment.isCollidesHorizontally();
        data.inWeb = assessment.isInWeb();
        data.onClimbable = assessment.isOnClimbable();
        data.onIce = assessment.isOnIce();
        data.onSoulSand = assessment.getMaterialsCollided().contains(Material.SOUL_SAND);
        data.blocksAround = PlayerUtils.hasBlocksAround(data.player.getLocation());
        data.onSlimeBefore = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8) && assessment.getMaterialsCollided().contains(Material.SLIME_BLOCK);
        CollisionAssessment ssIceAssess = new CollisionAssessment(data.boundingBox, data);
        Fiona.getInstance().getBlockBoxManager().getBlockBox().getCollidingBoxes(to.getWorld(), data.boundingBox.subtract(0, 2, 0, 0, 0, 0)).forEach(bb -> assessment.addMaterial(BlockUtils.getBlock(bb.getMinimum().toLocation(data.player.getWorld()))));
        data.onSoulSandIce = ssIceAssess.getMaterialsCollided().contains(Material.SOUL_SAND);

        if (data.onGround) {
            data.groundTicks++;
            data.airTicks = 0;

            if (data.lastTeleport.hasPassed(5)
                    && data.lastFlag.hasPassed(40)) {
                data.setbackLocation = to;
            }
        } else {
            data.airTicks++;
            data.groundTicks = 0;
        }


        data.halfBlockTicks = data.onHalfBlock && data.halfBlockTicks < 30 ? data.halfBlockTicks + 2 : data.halfBlockTicks > 0 ? data.halfBlockTicks - 1 : 0;
        data.blockTicks = data.blocksOnTop && data.blockTicks < 70 ? data.blockTicks + 1 : data.blockTicks > 0 ? data.blockTicks - 1 : 0;
        data.webTicks = data.inWeb && data.webTicks < 55 ? data.webTicks + 1 : data.webTicks > 0 ? data.webTicks - 1 : 0;
        data.slimeTicks = data.onSlimeBefore && data.slimeTicks < 70 ? data.slimeTicks + 1 : data.slimeTicks > 0 ? data.slimeTicks - 1 : 0;
        data.climbTicks = data.onClimbable && data.climbTicks < 45 ? data.climbTicks + 1 : data.climbTicks > 0 ? data.climbTicks - 1 : 0;
        data.liquidTicks = data.inLiquid && data.liquidTicks < 50 ? data.liquidTicks + 1 : data.liquidTicks > 0 ? data.liquidTicks - 1 : 0;
        data.iceTicks = data.onIce && data.iceTicks < 45 ? data.iceTicks + 2 : data.iceTicks > 0 ? data.iceTicks - 1 : 0;
        data.riptideTicks = Fiona.getInstance().getBlockBoxManager().getBlockBox().isRiptiding(data.player) ? Math.min(40, data.riptideTicks + 1) : Math.max(0, data.riptideTicks - 1);
        data.soulSandTicks = data.onSoulSand ? Math.min(45, data.soulSandTicks + 1) : Math.max(0, data.soulSandTicks - 1);
        if ((data.cancelType == CheckType.MOVEMENT || data.cancelType == CheckType.PLAYER) && data.cancelTicks > 0) {
            new BukkitRunnable() {
                public void run() {
                    data.isBeingCancelled = true;
                    Location from = data.setbackLocation == null ? toGround(data) : data.setbackLocation;

                    data.player.teleport(new Location(data.player.getWorld(), from.getX(), from.getY() + 0.01f, from.getZ(), from.getYaw(), from.getPitch()));
                    data.cancelTicks--;
                    data.isBeingCancelled = false;
                }
            }.runTask(Fiona.getInstance());
        }

        if (packet.isLook() || packet.isPos()) {
            PacketFunkeMoveEvent event = new PacketFunkeMoveEvent(data.player, new FionaLocation(data.movement.from), new FionaLocation(to), packet.isGround(), data.movement.hasJumped);
            if (data.lastTeleport.hasPassed(5) && (packet.isLook() || to.distance(data.movement.from) > 0.005) && data.lastLogin.hasPassed(40)) {
                EventManager.callEvent(event);
            }

            data.movement.from = to;
            return !event.isCancelled();
        }

        return true;
    }

    private static Location toGround(PlayerData data) {
        for (int y = data.player.getLocation().getBlockY(); y > 0; y--) {
            Location loc = data.player.getLocation().clone();
            loc.setY(y);

            if (BlockUtils.isSolid(BlockUtils.getBlock(loc))) {
                return loc.add(0, 1, 0);
            }
        }
        return data.player.getLocation();
    }

    private static float shit(float value) {
        return ((float) Math.cbrt((value / 0.15f) / 8f) - 0.2f) / .6f;
    }
}