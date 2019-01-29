package cc.funkemunky.fiona.utils.blockbox;

import cc.funkemunky.fiona.utils.BoundingBox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public interface BlockBox {
    List<BoundingBox> getCollidingBoxes(World world, BoundingBox box);

    List<BoundingBox> getSpecificBox(Location location);

    boolean isChunkLoaded(Location loc);

    boolean isUsingItem(Player player);

    boolean isRiptiding(LivingEntity entity);

    boolean isGliding(LivingEntity entity);

    int getTrackerId(Player player);
}
