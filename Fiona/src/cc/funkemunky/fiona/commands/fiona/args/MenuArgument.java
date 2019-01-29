package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.utils.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuArgument extends FunkeArgument {
    public MenuArgument() {
        super("menu", "menu", "Open the Fiona GUI.", "fiona.menu");

        addAlias("gui");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if (sender instanceof Player) {
            Fiona.getInstance().getGuiManager().openInventory((Player) sender, "Fiona-Menu");
        } else {
            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Red + "Only players can use this command.");
        }
    }
}
