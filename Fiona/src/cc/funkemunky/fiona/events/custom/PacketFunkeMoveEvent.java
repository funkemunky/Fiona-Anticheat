package cc.funkemunky.fiona.events.custom;

import cc.funkemunky.fiona.events.system.Cancellable;
import cc.funkemunky.fiona.events.system.Event;
import cc.funkemunky.fiona.utils.FionaLocation;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PacketFunkeMoveEvent
        extends Event
        implements Cancellable {
    private Player player;
    private FionaLocation from, to;
    private boolean cancelled, onGround, jumped;

    public PacketFunkeMoveEvent(Player player, FionaLocation from, FionaLocation to, boolean onGround, boolean jumped) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.onGround = onGround;
        this.jumped = jumped;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
