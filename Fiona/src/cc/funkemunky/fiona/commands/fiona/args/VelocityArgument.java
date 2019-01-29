package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VelocityArgument extends FunkeArgument {

    public VelocityArgument() {
        super("velocity", "velocity [xz] [y]", "apply velocity to yourself.", "fiona.velocity");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length >= 3) {
                try {
                    double xz = Double.parseDouble(args[1]);
                    double y = Double.parseDouble(args[2]);

                    if (args.length == 3) {
                        player.setVelocity(new Vector(xz, y, xz));
                        sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Green + "You have been given velocity.");
                    } else {
                        Player target = Bukkit.getPlayer(args[3]);

                        if (target != null) {
                            target.setVelocity(new Vector(xz, y, xz));
                            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Green + "You have given velocity to " + target.getName() + ".");
                        } else {
                            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Red + "The player '" + args[3] + "' is not online!");
                        }
                    }
                } catch (Exception e) {
                    sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Red + "One of the values you inputed were invalid.");
                }
            } else {
                sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Fiona.getInstance().getMessageFields().invalidArguments);
            }
        } else {
            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Red + "This command is for players only.");
        }
    }
}
