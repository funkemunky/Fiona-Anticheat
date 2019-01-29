package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.utils.FunkeFile;
import cc.funkemunky.fiona.utils.MathUtils;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class DevArgument extends FunkeArgument {
    public DevArgument(String name, String display, String description) {
        super(name, display, description);
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if(sender instanceof Player) {
            walkToLocation((Player) sender, ((Player) sender).getLocation().clone().add(4, 0, 4), 0.2806);
        }
    }

    public void walkToLocation(LivingEntity entity, Location location, double speed) {
        new BukkitRunnable() {
            public void run() {
                if(entity.getLocation().distance(location) > 0.3) {
                    float yaw = MathUtils.getRotations(entity.getLocation(), location)[0];

                    Vector direction = new Vector(-Math.sin(yaw * 3.1415927F / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(yaw * 3.1415927F / 180.0F) * (float) 1 * 0.5F).multiply(speed);

                    if(entity.getLocation().getY() - location.getY() > 0 && entity.isOnGround()) {
                        direction.setY(Math.min(0.42, entity.getLocation().getY() - location.getY()));
                    }
                    entity.setVelocity(direction);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Fiona.getInstance(), 0L, 1L);

    }
}
