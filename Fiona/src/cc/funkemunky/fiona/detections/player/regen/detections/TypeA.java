package cc.funkemunky.fiona.detections.player.regen.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.MiscUtils;
import cc.funkemunky.fiona.utils.ReflectionsUtil;
import org.bukkit.Difficulty;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class TypeA extends Detection {

    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
    }

    @Override
    public void onBukkitEvent(org.bukkit.event.Event event, PlayerData data) {
        if (event instanceof EntityRegainHealthEvent) {
            EntityRegainHealthEvent e = (EntityRegainHealthEvent) event;

            if (e.getEntity().getWorld().getDifficulty().equals(Difficulty.PEACEFUL)
                    || e.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED
                    || data.generalCancel) {
                return;
            }

            long delta = MathUtils.elapsed(data.lastHealthRegain);
            if (MathUtils.elapsed(data.lastHealthRegain) < (!ReflectionsUtil.isNewVersion() ? 2000L : 500L)
                    && data.regenVerbose.flag(3, 500L)) {
                flag(data, "t: " + delta, 1, false, false);
                e.setCancelled(MiscUtils.canCancel(this, data));
            }
            data.lastHealthRegain = System.currentTimeMillis();
        }
    }
}
