package cc.funkemunky.fiona.utils.blockbox;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.utils.BlockUtils;
import cc.funkemunky.fiona.utils.BoundingBox;
import cc.funkemunky.fiona.utils.BoxDebugType;
import cc.funkemunky.fiona.utils.MiscUtils;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.out.WrappedPacketPlayOutWorldParticle;
import com.ngxdev.tinyprotocol.packet.types.WrappedEnumParticle;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CollisionAssessment {
    private PlayerData data;
    private boolean onGround, inLiquid, blocksOnTop, pistonsNear, onHalfBlock, onClimbable, onIce, collidesHorizontally, inWeb;
    private Set<Material> materialsCollided;
    private BoundingBox playerBox;

    public CollisionAssessment(BoundingBox playerBox, PlayerData data) {
        onGround = inLiquid = blocksOnTop = false;
        this.data = data;
        this.playerBox = playerBox;
        materialsCollided = new HashSet<>();
    }

    public void assessBox(BoundingBox bb, World world, boolean isEntity) {
        Location location = bb.getMinimum().toLocation(world);
        Block block = location.getBlock();

        if (BlockUtils.isSolid(block) || isEntity) {
            if (bb.getMinimum().getY() < (playerBox.getMinimum().getY() + 0.1) && bb.collidesVertically(playerBox.subtract(0, 0.001f, 0, 0, 0, 0))) {
                onGround = true;
            }

            if ((bb.getMaximum().getY() + 0.1) > playerBox.getMaximum().getY() && bb.collidesVertically(playerBox.add(0, 0, 0, 0, 0.35f, 0))) {
                blocksOnTop = true;
            }

            if (BlockUtils.isPiston(block)) {
                pistonsNear = true;
            }

            if (BlockUtils.isSlab(block) || BlockUtils.isStair(block) || block.getType().getId() == 92 || block.getType().getId() == 397) {
                onHalfBlock = true;
            }

            if (BlockUtils.isIce(block)) {
                onIce = true;
            }

            if (bb.collidesHorizontally(playerBox.grow(0.05f + (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13) ? 0.05f : 0), 0, 0.05f + (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13) ? 0.05f : 0)))) {
                collidesHorizontally = true;
            }

            if (BlockUtils.isClimbableBlock(block) && playerBox.grow(0.3f, 0, 0.3f).collidesHorizontally(bb)) {
                onClimbable = true;
            }
        } else {
            if (BlockUtils.isLiquid(block) && playerBox.collidesVertically(bb)) {
                inLiquid = true;
            }
            if (block.getType().toString().contains("WEB") && playerBox.collidesVertically(bb)) {
                inWeb = true;
            }
        }

        if (data.boxDebugEnabled && data.boxDebugType.equals(BoxDebugType.COLLIDED) && bb.collides(playerBox)) {
            for (float x = bb.minX; x < bb.maxX + 0.2; x += 0.2f) {
                for (float y = bb.minY; y < bb.maxY + 0.2; y += 0.2f) {
                    for (float z = bb.minZ; z < bb.maxZ + 0.2; z += 0.2f) {
                        if (playerBox.collides(new Vector(x, y, z))) {
                            WrappedPacketPlayOutWorldParticle packet = new WrappedPacketPlayOutWorldParticle(WrappedEnumParticle.FLAME, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                            packet.sendPacket(data.player);
                        }
                    }
                }
            }
        }
        addMaterial(location.getBlock());
    }

    public void assessBox(BoundingBox bb, World world) {
        Location location = bb.getMinimum().toLocation(world);

        val posX = Location.locToBlock(location.getX());
        val posY = Location.locToBlock(location.getY());
        val posZ = Location.locToBlock(location.getZ());

        inLiquid = false;
        onGround = false;
        onIce = false;
        collidesHorizontally = false;
        onClimbable = false;
        onHalfBlock = false;
        inWeb = false;
        pistonsNear = false;
        blocksOnTop = false;

        val typeBelow = world.getBlockAt(posX, posY - 1, posZ);
        val typeFeet = world.getBlockAt(posX, posY, posZ);
        val typeHead = world.getBlockAt(posX, posY + 1, posZ);
        val typeAbove = world.getBlockAt(posX, posY + 2, posZ);

        if (BlockUtils.isLiquid(typeFeet) || BlockUtils.isLiquid(typeHead)) {
            inLiquid = true;
        } else if (BlockUtils.isSolid(typeFeet)) {
            onGround = true;
        } else if (BlockUtils.isSolid(typeBelow)) {
            val distance = location.getY() - location.getBlockY();

            if (distance < 0.2 || typeBelow.getType().getId() == 8 && distance < 0.51) {
                onGround = true;
            }
        }

        if (BlockUtils.isClimbableBlock(typeBelow) || BlockUtils.isClimbableBlock(typeAbove)) {
            onClimbable = true;
        }

        if (BlockUtils.isSolid(typeHead) || BlockUtils.isSolid(typeHead)) {
            blocksOnTop = true;
        }

        if (BlockUtils.isIce(typeFeet)) {
            onIce = true;
        }

        if (BlockUtils.isSlab(typeFeet) || BlockUtils.isStair(typeFeet) || BlockUtils.isBed(typeFeet)) {
            onHalfBlock = true;
        }

        if (BlockUtils.isSolid(typeAbove) || BlockUtils.isSolid(typeHead)) {
            blocksOnTop = true;
        }

        if (bb.collidesHorizontally(playerBox.grow(0.05f + (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13) ? 0.05f : 0),0.04f,0.05f + (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13) ? 0.05f : 0)))) {
            collidesHorizontally = true;
        }

        if (location.getBlock().getType().toString().contains("WEB") && playerBox.collidesVertically(bb)) {
            inWeb = true;
        }

        if (BlockUtils.isPiston(location.getBlock())) {
            pistonsNear = true;
        }
    }

    public void addMaterial(Block block) {
        materialsCollided.add(block.getType());
    }
}
