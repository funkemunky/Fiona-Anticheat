package cc.funkemunky.fiona.events.bukkit;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.data.logging.Yaml;
import com.ngxdev.tinyprotocol.api.TinyProtocolHandler;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutTransaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerConnectListeners
        implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Fiona.getInstance().getDataManager().createDataObject(e.getPlayer());
        Fiona.getInstance().getDataManager().getPlayerData(e.getPlayer()).lastLogin.reset();
        new BukkitRunnable() {
            public void run() {
                TinyProtocolHandler.sendPacket(e.getPlayer(), new WrappedOutTransaction(0, (short) 69, true));
            }
        }.runTaskLater(Fiona.getInstance(), 25L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(e.getPlayer());

        ((Yaml) Fiona.getInstance().getDataManager().getLogger()).dumpLog(data);
        Fiona.getInstance().getDataManager().removeDataObject(Fiona.getInstance().getDataManager().getPlayerData(e.getPlayer()));
        Fiona.getInstance().getBannedPlayers().remove(e.getPlayer());
    }
}

