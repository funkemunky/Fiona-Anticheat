package cc.funkemunky.fiona.detections.movement.nofall;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.movement.nofall.detections.TypeA;
import cc.funkemunky.fiona.detections.movement.nofall.detections.TypeB;
import cc.funkemunky.fiona.detections.movement.nofall.detections.TypeC;
import cc.funkemunky.fiona.detections.movement.nofall.detections.TypeD;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.concurrent.TimeUnit;

public class NoFall
        extends Check {
    public NoFall() {
        super("NoFall", CheckType.MOVEMENT, true, false, false, true, 120, TimeUnit.MINUTES.toMillis(2), 30);

        addConfigValue("fallCorrection", false);

        addDetection(new TypeA(this, "Type A", true, true));
        addDetection(new TypeB(this, "Type B", true, false));
        addDetection(new TypeC(this, "Type C", true, true));
        addDetection(new TypeD(this, "Type D", true, false));
    }

    @EventHandler
    public void onFall(PlayerMoveEvent event) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(event.getPlayer());

        if (data == null
                || data.boundingBox == null
                || !((boolean) getConfigValues().get("fallCorrection"))) {
            return;
        }

        double fallDistance = data.movement.realFallDistance;
        boolean onGround = data.onGround;

        data.lastFallDistance = fallDistance > 0 || data.groundTicks > 1 || event.getPlayer().isDead() ? fallDistance : data.lastFallDistance;

        // Bukkit.broadcastMessage(data.lastFallDistance + ", " + data.isFDCanceled);
        if ((onGround || data.lastFallDistance % 1 == 0)
                && !event.getPlayer().getAllowFlight()
                && !event.getPlayer().isInsideVehicle()
                && data.lastFallDistance > 3
                && !event.isCancelled()
                && !data.isFDCanceled) {

            EntityDamageEvent e = getEntityDamageEvent(data.player, EntityDamageEvent.DamageCause.FALL, data.lastFallDistance - 4);

            Bukkit.getPluginManager().callEvent(e);
            data.cancelEvent = false;
            if (!e.isCancelled()) {
                // TODO: account for no damage ticks etc.
                data.player.setLastDamageCause(e);
                data.player.damage(e.getDamage());
                //Bukkit.broadcastMessage("test");
            }
            //Bukkit.broadcastMessage("true");
            data.lastFallDistance = 0;
        }
        data.isFDCanceled = false;

        data.lastFallDistance = data.movement.realFallDistance;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player
                && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            PlayerData data = Fiona.getInstance().getDataManager().getPlayerData((Player) event.getEntity());

            if (data == null
                    || !((boolean) getConfigValues().get("fallCorrection"))) {
                return;
            }

            if (!data.cancelEvent) {
                data.cancelEvent = true;
                return;
            }

            //Bukkit.broadcastMessage("canceled");

            event.setCancelled(true);

        }
    }

    private EntityDamageEvent getEntityDamageEvent(final Entity entity,
                                                   final EntityDamageEvent.DamageCause damageCause, final double damage) {
        try {
            return new EntityDamageEvent(entity, damageCause, damage);
        } catch (IncompatibleClassChangeError e) {
            return new EntityDamageEvent(entity, damageCause,
                    (int) Math.round(damage));
        }
    }
}

