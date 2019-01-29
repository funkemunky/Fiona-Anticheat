package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BroadcastArgument
        extends FunkeArgument {
    public BroadcastArgument() {
        super("broadcast", "broadcast <message>", "broadcast a message to the entire server.", "fiona.broadcast");
    }

    @Override
    public void onArgument(CommandSender sender, Command command, String[] args) {
        StringBuilder message = new StringBuilder();
        for (int i = 1; i != args.length; ++i) {
            message.append(args[i]).append(" ");
        }
        Bukkit.broadcastMessage(Color.translate(message.toString()));
    }
}

