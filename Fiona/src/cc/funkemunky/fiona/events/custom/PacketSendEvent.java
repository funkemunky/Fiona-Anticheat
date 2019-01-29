package cc.funkemunky.fiona.events.custom;

import cc.funkemunky.fiona.events.system.Cancellable;
import cc.funkemunky.fiona.events.system.Event;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
public class PacketSendEvent extends Event implements Cancellable {
    private Player player;
    @Setter
    private Object packet;
    private boolean cancelled;
    private String type;

    public PacketSendEvent(Player player, Object packet, String type) {
        this.player = player;
        this.packet = packet;
        this.type = type;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
