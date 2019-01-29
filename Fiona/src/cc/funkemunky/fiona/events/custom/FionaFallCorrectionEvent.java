package cc.funkemunky.fiona.events.custom;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class FionaFallCorrectionEvent extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();
    private Player player;
    private boolean cancelled;
    private double damage;

    public FionaFallCorrectionEvent(Player player, double damage) {
        this.player = player;
        this.damage = damage;
    }


    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
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
