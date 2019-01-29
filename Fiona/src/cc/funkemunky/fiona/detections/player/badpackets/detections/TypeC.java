package cc.funkemunky.fiona.detections.player.badpackets.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class TypeC extends Detection {
    public TypeC(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("violationToFlag", 10);
        setThreshold((int) getConfigValues().getOrDefault("violationToFlag", 10));
    }

    @Override
    public void onBukkitEvent(Event event, PlayerData data) {
        if (event instanceof PlayerToggleSneakEvent) {
            if (data.sneakTime.hasPassed(10, true)) {
                if (data.sneakTicks > 12) {
                    flag(data, data.sneakTicks + ">-12", 1, true, true);
                }
                data.sneakTicks = 0;
            } else {
                data.sneakTicks++;
            }
        }
    }
}
