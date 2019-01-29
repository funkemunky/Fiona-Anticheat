package cc.funkemunky.fiona.detections.player.selfhit.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.utils.MathUtils;
import org.bukkit.event.entity.EntityDamageEvent;

public class TypeB extends Detection {

    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
    }

    @Override
    public void onBukkitEvent(org.bukkit.event.Event event, PlayerData data) {
        if (event instanceof EntityDamageEvent) {
            EntityDamageEvent e = (EntityDamageEvent) event;
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL
                    && !e.isCancelled()) {

                if (!data.onGroundFive
                        && !data.generalCancel) {
                    if (++data.invalidFallVerbose > 1) {
                        flag(data, "d: " + MathUtils.round(data.player.getFallDistance(), 4), 1, true, true);
                    }
                } else {
                    data.invalidFallVerbose = 0;
                }
            }
        }
    }
}
