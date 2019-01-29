package cc.funkemunky.fiona.events.custom;

import cc.funkemunky.fiona.events.system.Event;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PacketArmSwingEvent extends Event {
    private Player player;
    private boolean lookingAtBlock;

    public PacketArmSwingEvent(Player player, boolean lookingAtBlock) {
        this.player = player;
        this.lookingAtBlock = lookingAtBlock;
    }
}
