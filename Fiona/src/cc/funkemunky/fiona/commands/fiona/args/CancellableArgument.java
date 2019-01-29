package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.utils.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CancellableArgument
        extends FunkeArgument {
    public CancellableArgument() {
        super("cancellable", "cancellable <check> [detection]", "enable or disable the checks ability to send commands.", "fiona.cancel");

        addAlias("cancel");
        addAlias("setback");
        addAlias("rubberband");

        addTabComplete(2, "%check%");
        addTabComplete(2, "*");
        addTabComplete(3, "%detection%,!*,2");
        addTabComplete(3, "enable,*,2");
        addTabComplete(3, "disable,*,2");
        addTabComplete(4, "save,*,2");
    }

    @Override
    public void onArgument(CommandSender sender, Command command, String[] args) {
        if(args.length > 1) {
            if (args[1].equals("*") && args.length > 2) {
                if (args[2].equalsIgnoreCase("disable")) {
                    for (Check check : Fiona.getInstance().getCheckManager().getChecks()) {
                        check.setCancellable(false);
                        check.getDetections().forEach(detection -> detection.setCancellable(false));
                    }
                    if(args.length > 3 && args[3].equalsIgnoreCase("save")) {
                        Fiona.getInstance().saveChecks();
                    }
                    sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Green + "Disabled all checks' cancelling abilities!");
                    return;
                } else if (args[2].equalsIgnoreCase("enable")) {
                    for (Check check : Fiona.getInstance().getCheckManager().getChecks()) {
                        check.setCancellable(true);
                        check.getDetections().forEach(detection -> detection.setCancellable(true));
                    }

                    if(args.length > 3 && args[3].equalsIgnoreCase("save")) {
                        Fiona.getInstance().saveChecks();
                    }
                    sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Green + "Enabled all checks' cancelling abilities!");
                    return;
                }
            } else if(Fiona.getInstance().getCheckManager().isCheck(args[1])) {
                Check check = Fiona.getInstance().getCheckManager().getCheckByName(args[1]);

                if(args.length == 3) {
                    String toCheck = args[2].replaceAll("_", " ");
                    if(check.isDetection(toCheck)) {
                        Detection detection = check.getDetectionByName(toCheck);

                        detection.setCancellable(!detection.isCancellable());

                        sender.sendMessage(Color.translate(
                                Fiona.getInstance().getMessageFields().prefix + " &7The check &c" + check.getName() + "&7(&e" + detection.getId() + "&7)'s cancelling abilities have been " + (detection.isCancellable() ? "&aenabled" : "&cdisabled") + "&7."));

                    } else {
                        sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Red + "The detection \"" + toCheck + "\" does not exist!");
                    }

                } else {
                    check.setCancellable(!check.isCancellable());
                    sender.sendMessage(Color.translate(
                            Fiona.getInstance().getMessageFields().prefix + " &7The check &c" + check.getName() + "&7's cancelling abilities have been " + (check.isCancellable() ? "&aenabled" : "&cdisabled") + "&7."));
                }
                Fiona.getInstance().saveChecks();
            } else {
                sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Red + "The check \"" + args[1] + "\" does not exist!");
            }
            return;
        }
        sender.sendMessage(Fiona.getInstance().getMessageFields().invalidArguments);
    }
}

