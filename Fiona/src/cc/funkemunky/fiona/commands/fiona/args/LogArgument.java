package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.data.logging.Log;
import cc.funkemunky.fiona.data.logging.Yaml;
import cc.funkemunky.fiona.utils.*;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LogArgument extends FunkeArgument {

    public LogArgument() {
        super("log", "log <args> <player> [page]", "View the logs of a player.", "fiona.log");

        addTabComplete(2, "vl");
        addTabComplete(2, "violations");
        addTabComplete(2, "verbose");
        addTabComplete(2, "clear");
        addTabComplete(2, "delete");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if (args.length > 2) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
            List<Log> logs = Fiona.getInstance().getDataManager().getLogger().getLogs(player.getUniqueId());
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
            int page = args.length > 3 && MathUtils.tryParse(args[3]) != -1 ? MathUtils.tryParse(args[3]) : 1;
            if (logs.size() > 0) {
                switch (args[1].toLowerCase()) {
                    case "violations":
                    case "vl":
                        List<Violation> violations = Lists.newArrayList();

                        //Formatting the violations.
                        logs.forEach(log -> {
                            Violation violation = violations.stream().filter(vl -> vl.getCheck().getName().equalsIgnoreCase(log.getCheck())).findFirst().orElse(new Violation(Fiona.getInstance().getCheckManager().getCheckByName(log.getCheck())));

                            violations.remove(violation);
                            violation.addViolation(violation.getCheck().getDetectionByName(log.getDetection()), (float) log.getVl());

                            violations.add(violation);
                        });

                        //Sending the formatted violations in pages to the player.

                        sender.sendMessage(Color.Gray + "Page (" + page + "/" + (int) MathUtils.round(violations.size() / 8D) + ")");

                        for (int i = (page - 1) * 8; i < Math.min(violations.size(), page * 8); i++) {
                            Violation vl = violations.get(i);
                            sender.sendMessage(Color.translate("&8» &6" + vl.getCheck().getName() + " &7[&c" + MathUtils.trim(2, vl.getCombinedAmount()) + "&7]"));

                            vl.getSpecificViolations().keySet().forEach(detection -> {
                                sender.sendMessage(Color.translate("&8- &e" + detection.getId() + " &7[" + vl.getSpecificViolations().get(detection) + "&7]"));
                            });
                        }
                        break;
                    case "verbose":
                        sender.sendMessage(Color.Gray + "Page " + page + "/" + (int) Math.ceil(logs.size() / 8D));
                        List<String> logStrings = new ArrayList<>();

                        logs.sort(Comparator.comparingDouble(Log::getVl).thenComparing(Log::getDetection));

                        for (int i = (page - 1) * 8; i < Math.min(logs.size(), page * 8); i++) {
                            Log log = logs.get(i);
                            logStrings.add(Color.translate("&8- &6" + log.getCheck() + "&7(&e" + log.getDetection() + "&7) [&c " + log.getVl() + "&7]: " + log.getData() + "(&f" + log.getReliabilityPercentage() + "% Reliable&7)"));
                        }

                        logStrings.forEach(sender::sendMessage);

                        break;
                    case "clear":
                    case "delete": {
                        Yaml yaml = (Yaml) Fiona.getInstance().getDataManager().getLogger();

                        FunkeFile file = yaml.getLogFile(player.getUniqueId());

                        file.clear();
                        file.write();

                        sender.sendMessage(Color.Green + "Successfully cleared " + player.getName() + "'s log!");
                        break;
                    }
                    default: {
                        sender.sendMessage(Color.Red + "Invalid argument \"" + args[2] + "\"! Options: [violations, vl, verbose, clear, delete].");
                        break;
                    }
                }
            } else {
                sender.sendMessage(MiscUtils.formattedMessage(Color.Red + "Fiona has no logs stored for this player."));
            }
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
        } else {
            sender.sendMessage(Fiona.getInstance().getMessageFields().invalidArguments);
        }
    }
}
