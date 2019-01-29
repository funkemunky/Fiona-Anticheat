package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.utils.Color;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViewArgument
        extends FunkeArgument {

    public ViewArgument() {
        super("view", "view <player>", "view a player's data collected by Fiona.", "fiona.violations");
    }

    @Override
    public void onArgument(CommandSender sender, Command command, String[] args) {
        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Color.Red + "That player is not online!");
                return;
            }
            PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(target);
            if (data == null) {
                sender.sendMessage(Color.Red + "Unknown error occurred that prevented Fiona from accessing the target's data.");
                return;
            }
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
            sender.sendMessage(Color.Gold + Color.Bold + target.getName() + "'s Information");
            sender.sendMessage("");
            sender.sendMessage(Color.Yellow + "Ping: " + Color.White + data.ping);
            sender.sendMessage(Color.Yellow + "Reliability: " + Color.White + data.reliabilityPercentage + "%");
            sender.sendMessage("");
            sender.sendMessage(Color.Yellow + "Violations:");
            if (!Fiona.getInstance().getCheckManager().violations.containsKey(data.player.getUniqueId())) {
                sender.sendMessage(Color.Gray + "None! :)");
            } else {
                Fiona.getInstance().getCheckManager().violations.get(data.player.getUniqueId()).forEach(vl -> {
                    sender.sendMessage(Color.translate("&8» &6" + vl.getCheck().getName() + " &7[&c" + MathUtils.trim(2, vl.getCombinedAmount()) + "&7]"));

                    vl.getSpecificViolations().keySet().forEach(detection -> {
                        sender.sendMessage(Color.translate("&8- &e" + detection.getId() + " &7[" + vl.getSpecificViolations().get(detection) + "&7]"));
                    });
                });
            }
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
        } else {
            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Fiona.getInstance().getMessageFields().invalidArguments);
        }
    }
}

