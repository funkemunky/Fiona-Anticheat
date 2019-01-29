package cc.funkemunky.fiona.detections.movement.step.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class TypeA extends Detection {
    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);
    }

    @Override
    public void onBukkitEvent(Event event, PlayerData data) {
        if (event instanceof PlayerMoveEvent) {
            PlayerMoveEvent e = (PlayerMoveEvent) event;

            if (!PlayerUtils.isRiskyForFlight(data)
                    && MathUtils.playerMoved(e.getFrom(), e.getTo())
                    && data.movement.deltaY > 0.6 + (PlayerUtils.getPotionEffectLevel(data.player, PotionEffectType.JUMP) * 0.1)) {
                flag(data, data.movement.deltaY + ">-0.6", 1, false, true);
            }
        }
    }
}
