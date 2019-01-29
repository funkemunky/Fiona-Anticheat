package cc.funkemunky.fiona.events.custom;

import cc.funkemunky.fiona.detections.Check;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class FionaPunishEvent extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player player;
    private Check check;

    public FionaPunishEvent(Player player, Check check) {
        this.player = player;
        this.check = check;
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
