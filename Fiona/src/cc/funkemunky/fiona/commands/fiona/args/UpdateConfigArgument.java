package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.time.LocalDateTime;

public class UpdateConfigArgument extends FunkeArgument {

    public UpdateConfigArgument() {
        super("updateConfig", "updateConfig <full, checks, checkValues>", "Update the config to the recommended defaults.", "fiona.updateConfig");

        addTabComplete(2, "full");
        addTabComplete(2, "all");
        addTabComplete(2, "checks");
        addTabComplete(2, "check");
        addTabComplete(2, "detection");
        addTabComplete(2, "detections");
        addTabComplete(2, "checkvalues");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(Fiona.getInstance().getMessageFields().invalidArguments);
        } else {
            switch (args[1].toLowerCase()) {
                case "full":
                case "all": {
                    sender.sendMessage(MiscUtils.formattedMessage("&7Updating Fiona configuration to it's recommended defaults..."));
                    File config = new File(Fiona.getInstance().getDataFolder(), "config.yml");

                    LocalDateTime now = LocalDateTime.now();
                    File oldConfig = new File(Fiona.getInstance().getDataFolder(), "config_old_" + now + ".yml");

                    if (config.renameTo(oldConfig)) {
                        sender.sendMessage(MiscUtils.formattedMessage("&7Old configuration saved as: &e" + oldConfig.getName()));
                    } else {
                        sender.sendMessage(MiscUtils.formattedMessage("&cError occurred trying to make back-up of your old configuration!") + "\n" + MiscUtils.formattedMessage("&7&oYou can do /fiona " + args[1] + " force to continue anyway."));
                        return;
                    }
                    Fiona.getInstance().saveDefaultConfig();
                    sender.sendMessage(MiscUtils.formattedMessage("&7Updated the default config. Reloading Fiona..."));
                    if (sender.hasPermission("fiona.reload") || sender.hasPermission("fiona.admin")) {
                        Bukkit.dispatchCommand(sender, "fiona reload full");
                    } else {
                        Bukkit.dispatchCommand(Fiona.getInstance().consoleSender, "fiona reload full");
                    }
                    MiscUtils.formattedMessage("&aDone!");

                    break;
                }
                case "checks":
                case "check":
                case "detections":
                case "detection": {
                    sender.sendMessage(MiscUtils.formattedMessage("&7Updating the check section to its recommended default..."));
                    Fiona.getInstance().getConfig().set("checks", null);
                    Fiona.getInstance().saveConfig();
                    Fiona.getInstance().getCheckManager().getChecks().clear();
                    Fiona.getInstance().getCheckManager().initializeDetections();
                    sender.sendMessage(MiscUtils.formattedMessage("&7Removed previous section. Setting up new section..."));
                    Fiona.getInstance().loadChecks();
                    sender.sendMessage(MiscUtils.formattedMessage("&aDone!"));
                    break;
                }
                case "checkvalues":
                case "values": {
                    sender.sendMessage(MiscUtils.formattedMessage("&7Updating the check values to their recommended defaults..."));
                    Fiona.getInstance().getCheckManager().getChecks().forEach(check -> {
                        String checkPath = "checks." + check.getName() + ".";
                        Fiona.getInstance().getConfig().set(checkPath + "values", null);

                        check.getDetections().forEach(detection -> Fiona.getInstance().getConfig().set(checkPath + "detections." + detection.getId() + ".values", null));
                    });
                    Fiona.getInstance().saveConfig();
                    Fiona.getInstance().getCheckManager().getChecks().clear();
                    Fiona.getInstance().getCheckManager().initializeDetections();
                    sender.sendMessage(MiscUtils.formattedMessage("&7Reset values. Setting defaults..."));
                    Fiona.getInstance().reloadConfig();
                    Fiona.getInstance().loadChecks();
                    sender.sendMessage(MiscUtils.formattedMessage("&aDone!"));
                    break;
                }
                default: {
                    sender.sendMessage(Fiona.getInstance().getMessageFields().invalidArguments);
                    break;
                }
            }
        }
    }
}
