package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.utils.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadArgument
        extends FunkeArgument {
    public ReloadArgument() {
        super("reload", "reload <full/config/data>", "reload different parts of Fiona.", "fiona.reload");

        addTabComplete(2, "full");
        addTabComplete(2, "partial");
        addTabComplete(2, "config");
        addTabComplete(2, "data");
    }

    @Override
    public void onArgument(CommandSender sender, Command command, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Gray + "Working...");
            switch (args[1]) {
                case "full": {
                    sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Gray + "Reloading configurations...");
                    Fiona.getInstance().reloadConfig();
                    Fiona.getInstance().reloadConfigObject();
                    Fiona.getInstance().reloadMessages();
                    Fiona.getInstance().reloadMessagesObject();
                    sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Gray + "Reloading data objects...");
                    Fiona.getInstance().getCheckManager().violations.clear();
                    Fiona.getInstance().reloadPlayerData();
                    Fiona.getInstance().getCheckManager().getChecks().clear();
                    Fiona.getInstance().getCheckManager().initializeDetections();
                    Fiona.getInstance().loadChecks();
                    break;
                }
                case "config": {
                    sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Gray + "Reloading configurations...");
                    Fiona.getInstance().reloadConfig();
                    Fiona.getInstance().reloadConfigObject();
                    Fiona.getInstance().reloadPlayerData();
                    Fiona.getInstance().reloadMessages();
                    Fiona.getInstance().reloadMessagesObject();
                    break;
                }
                case "data": {
                    sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Gray + "Reloading data objects...");
                    Fiona.getInstance().reloadPlayerData();
                    Fiona.getInstance().getCheckManager().violations.clear();
                    break;
                }
                default: {
                    sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.translate("&c&oIncorrect argument \"" + args[1] + "\". Defaulting to &f&ofull&c&o."));
                    sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Gray + "Reloading configurations...");
                    Fiona.getInstance().reloadConfig();
                    Fiona.getInstance().reloadConfigObject();
                    Fiona.getInstance().reloadMessages();
                    Fiona.getInstance().reloadMessagesObject();
                    sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Gray + "Reloading data objects...");
                    Fiona.getInstance().reloadPlayerData();
                    Fiona.getInstance().getCheckManager().getChecks().clear();
                    Fiona.getInstance().loadChecks();
                    Fiona.getInstance().getCheckManager().violations.clear();
                    break;
                }
            }
            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Green + "Done!");
        } else {
            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.translate("&cInvalid Arguments! &7Options: &ffull&7, &fconfig&7, &fdata&7."));
        }
    }
}

