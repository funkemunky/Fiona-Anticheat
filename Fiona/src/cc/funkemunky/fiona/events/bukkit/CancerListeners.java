package cc.funkemunky.fiona.events.bukkit;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRiptideEvent;

public class CancerListeners implements Listener {
    @EventHandler
    public void onEvent(PlayerRiptideEvent event) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(event.getPlayer());

        data.riptideTicks+= 5;
    }
}
