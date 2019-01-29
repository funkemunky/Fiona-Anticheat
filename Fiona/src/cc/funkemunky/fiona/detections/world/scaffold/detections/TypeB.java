package cc.funkemunky.fiona.detections.world.scaffold.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.utils.MathUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

public class TypeB extends Detection {

    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
    }

    @Override
    public void onBukkitEvent(org.bukkit.event.Event event, PlayerData data) {
        if (event instanceof BlockPlaceEvent) {
            BlockPlaceEvent e = (BlockPlaceEvent) event;

            if (!data.generalCancel
                    && e.getBlockPlaced().getType().getId() != 60) {
                long timeDelta;

                Player player = e.getPlayer();

                BlockFace face = e.getBlockAgainst().getFace(e.getBlockPlaced());
                if (face == BlockFace.UP
                        && MathUtils.getHorizontalDistance(e.getBlockPlaced().getLocation(), player.getLocation().getBlock().getLocation()) < 1) {
                    timeDelta = MathUtils.elapsed(data.lastVerticalBlockPlace);

                    Block lastBlock = data.lastTowerBlock == null ? e.getBlockAgainst() : data.lastTowerBlock;
                    if (timeDelta < 400
                            && e.getBlockPlaced().getY() - lastBlock.getY() == 1
                            && MathUtils.getHorizontalDistance(lastBlock.getLocation(), e.getBlockPlaced().getLocation()) == 0) {
                        if (data.scaffoldTowerVerbose.flag(4, 800L)) {
                            flag(data, "t: tower d: " + timeDelta, 1, false, true);
                        }
                    } else {
                        data.scaffoldTowerVerbose.deduct();
                    }
                    data.lastVerticalBlockPlace = System.currentTimeMillis();
                    data.lastTowerBlock = e.getBlockPlaced();
                }
                if (face != BlockFace.UP && face != BlockFace.DOWN && player.getLocation().getY() - e.getBlockPlaced().getLocation().getY() >= 1.0) {
                    timeDelta = MathUtils.elapsed(data.lastHorizontalBlockPlace);
                    if (timeDelta < 400
                            && e.getPlayer().isOnGround()
                            && data.scaffoldSpeedVerbose.flag(6, 500L)) {
                        flag(data, "t: speed d: " + timeDelta, 1, false, true);
                    }

                    data.lastHorizontalBlockPlace = System.currentTimeMillis();
                }
            }
        }
    }
}
