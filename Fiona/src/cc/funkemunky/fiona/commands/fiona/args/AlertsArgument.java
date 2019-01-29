package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.utils.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertsArgument
        extends FunkeArgument {
    public AlertsArgument() {
        super("alerts", "alerts", "toggle alerts on or off.", "fiona.alerts");

        addTabComplete(2, "true");
        addTabComplete(2, "false");
    }

    @Override
    public void onArgument(CommandSender sender, Command command, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(player);
            if (data == null) {
                player.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Red + "Unknown error occured where your data object returns null.");
                return;
            }
            data.alerts = !data.alerts;
            player.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Gray + "Set your alerts mode to " + (data.alerts ? Color.Green + "true" : Color.Red + "false") + Color.Gray + " !");
        } else {
            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Red + "You must be a player to use this command!");
        }
    }
}

