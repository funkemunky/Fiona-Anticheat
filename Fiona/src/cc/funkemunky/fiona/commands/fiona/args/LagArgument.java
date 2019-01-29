package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.utils.Color;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.MiscUtils;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LagArgument extends FunkeArgument {

    public LagArgument() {
        super("lag", "lag [gc]", "accurately view your server's resources.", "fiona.lag");

        addTabComplete(2, "gc");
    }


    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
            sender.sendMessage(Color.translate("&eTPS&7: &f" + MathUtils.round(Fiona.getInstance().tps, 2)));
            sender.sendMessage(Color.translate("&eThreads Available&7: &f" + Runtime.getRuntime().availableProcessors()));
            sender.sendMessage(Color.translate("&eSpigot Version&7: &f" + ProtocolVersion.getGameVersion()));
            sender.sendMessage(Color.translate("&eFree Memory&7: &f" + Runtime.getRuntime().freeMemory() / 1000000L + "MB"));
            sender.sendMessage(Color.translate("&eTotal Memory&7: &f" + Runtime.getRuntime().totalMemory() / 1000000L + "MB"));
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
        } else {
            if (args[1].equalsIgnoreCase("gc")) {
                sender.sendMessage(Color.Gray + "Attempting to free up memory...");
                Runtime.getRuntime().gc();
                sender.sendMessage(Color.Green + "Finished!");
                sender.sendMessage("");
                sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
                sender.sendMessage(Color.translate("&eTPS&7: &f" + MathUtils.round(Fiona.getInstance().tps, 2)));
                sender.sendMessage(Color.translate("&eFree Memory&7: &f" + Runtime.getRuntime().freeMemory() / 1000000L + "MB"));
                sender.sendMessage(Color.translate("&eTotal Memory&7: &f" + Runtime.getRuntime().totalMemory() / 1000000L + "MB"));
                sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
                return;
            }
            sender.sendMessage(Fiona.getInstance().getMessageFields().invalidArguments);
        }
    }
}
