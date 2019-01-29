package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.utils.Color;
import cc.funkemunky.fiona.utils.JsonMessage;
import cc.funkemunky.fiona.utils.MiscUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusArgument
        extends FunkeArgument {
    public StatusArgument() {
        super("status", "status", "check the status of Fiona.", "fiona.status");
    }

    @Override
    public void onArgument(CommandSender sender, Command command, String[] args) {
        if (sender instanceof Player) {
            JsonMessage executable = new JsonMessage();
            JsonMessage nonExecutable = new JsonMessage();
            for (Check check : Fiona.getInstance().getCheckManager().getChecks()) {
                StringBuilder detections = new StringBuilder();
                int size = 0;
                for (Detection detection : check.getDetections()) {
                    size++;
                    detections.append(detection.isEnabled() ? Color.Green : Color.Red).append(detection.isExperimental() ? Color.Italics : "").append(detection.getId()).append(size != check.getDetections().size() ? Color.Gray + ", " : "");
                }
                if (check.isExecutable()) {
                    executable.addText((check.isEnabled() ? Color.Green : Color.Red) + check.getName()).addHoverText(detections.toString());
                    executable.addText(Color.Gray + ", ");
                    continue;
                }
                nonExecutable.addText((check.isEnabled() ? Color.Green : Color.Red) + check.getName()).addHoverText(detections.toString());
                nonExecutable.addText(Color.Gray + ", ");
            }
            Player player = (Player) sender;
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
            sender.sendMessage(Color.Gold + Color.Bold + "Fiona v" + Fiona.getInstance().getDescription().getVersion() + " Status");
            sender.sendMessage("");
            sender.sendMessage(Color.White + Color.Italics + "Italics " + Color.Gray + "= " + Color.Red + "Experimental");
            sender.sendMessage("");
            sender.sendMessage(Color.Yellow + "Executable Checks:");
            executable.sendToPlayer(player);
            sender.sendMessage(Color.Yellow + "NonExecutable Checks:");
            nonExecutable.sendToPlayer(player);
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
        } else {
            StringBuilder executable = new StringBuilder();
            StringBuilder nonExecutable = new StringBuilder();
            for (Check check : Fiona.getInstance().getCheckManager().getChecks()) {
                if (check.isExecutable()) {
                    executable.append(check.isEnabled() ? Color.Green : Color.Red).append(check.getName()).append(Color.Gray).append(", ");
                    continue;
                }
                nonExecutable.append(check.isEnabled() ? Color.Green : Color.Red).append(check.getName()).append(Color.Gray).append(", ");
            }
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
            sender.sendMessage(Color.Gold + Color.Bold + "Fiona's Status");
            sender.sendMessage("");
            sender.sendMessage(Color.White + Color.Italics + "You can view more info as a player");
            sender.sendMessage("");
            sender.sendMessage(Color.Yellow + "Executable Checks:");
            sender.sendMessage(executable.toString());
            sender.sendMessage(Color.Yellow + "NonExecutable Checks:");
            sender.sendMessage(nonExecutable.toString());
        }
    }
}

