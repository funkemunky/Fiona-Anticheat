package cc.funkemunky.fiona.detections.combat.criticals.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.utils.MathUtils;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

public class TypeA extends Detection {
    public TypeA(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);


        addConfigValue("maxAttackTicksPassed", 5);
        addConfigValue("ticksSinceLastVelocity", 7);
        addConfigValue("threshold", 15);
        addConfigValue("resetTime", 1500L);
        addConfigValue("groundTickThreshold", 15);
        addConfigValue("verboseToAdd", 2);
        setThreshold(3);
    }

    @Override
    public void onBukkitEvent(Event event, PlayerData data) {
        if (event instanceof PlayerMoveEvent) {
            PlayerMoveEvent e = (PlayerMoveEvent) event;

            if (!data.generalCancel
                    && e.getTo().getY() != e.getFrom().getY()
                    && data.blockTicks == 0
                    && !data.onHalfBlock
                    && data.lastAttack.hasNotPassed((int) getConfigValues().get("maxAttackTicksPassed"))
                    && data.lastVelocity.hasPassed((int) getConfigValues().get("ticksSinceLastVelocity"))
                    && !data.onSlimeBefore) {
                if (data.groundTicks > (int) getConfigValues().get("groundTickThreshold")) {
                    if (data.criticalsVerbose.flag((int) getConfigValues().get("threshold"), (int) getConfigValues().get("resetTime"), (int) getConfigValues().get("verboseToAdd"))) {
                        flag(data, MathUtils.round(e.getTo().getY() % e.getTo().getBlockY(), 3) + ">-" + MathUtils.round(e.getFrom().getY() % e.getFrom().getBlockY(), 3), 1, true, true);
                    }
                } else {
                    data.criticalsVerbose.deduct();
                }
                debug(data, data.criticalsVerbose.getVerbose() + ": " + data.movement.deltaY + ", " + e.getPlayer().isOnGround() + ", " + data.onGround);
            }
        }
    }
}
