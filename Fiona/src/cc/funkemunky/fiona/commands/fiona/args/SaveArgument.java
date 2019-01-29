package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.data.logging.Yaml;
import cc.funkemunky.fiona.utils.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SaveArgument extends FunkeArgument {
    public SaveArgument() {
        super("savelogs", "savelogs", "Force-save the logs of player's to a Yaml file.", "fiona.save");

        addAlias("forcesave");
        addAlias("save");
        addAlias("sl");
        addAlias("slogs");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if (Fiona.getInstance().getDataManager().getLogger() instanceof Yaml) {
            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Red + "Saving all violations logs...");
            Yaml logger = (Yaml) Fiona.getInstance().getDataManager().getLogger();

            logger.dumpLogs();
            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Green + "Saved!");
        } else {
            sender.sendMessage(ChatColor.RED + "This feature is only for YAML logging since it is useless in other forms.");
        }
    }
}
