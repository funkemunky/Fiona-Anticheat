package cc.funkemunky.fiona.events.custom;

import cc.funkemunky.fiona.events.system.Cancellable;
import cc.funkemunky.fiona.events.system.Event;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@Getter
@Setter
public class PacketAttackEvent extends Event implements Cancellable {

    private boolean cancelled;
    private Player attacker;
    private LivingEntity attacked;

    public PacketAttackEvent(Player attacker, LivingEntity attacked) {
        this.attacker = attacker;
        this.attacked = attacked;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
