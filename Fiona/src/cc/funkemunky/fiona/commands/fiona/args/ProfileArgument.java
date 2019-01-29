package cc.funkemunky.fiona.commands.fiona.args;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.commands.FunkeArgument;
import cc.funkemunky.fiona.profiling.ToggleableProfiler;
import cc.funkemunky.fiona.utils.Color;
import cc.funkemunky.fiona.utils.MathUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class ProfileArgument extends FunkeArgument {

    private long profileStart;

    public ProfileArgument() {
        super("profile", "profile", "toggle the timings profile on/off.", "fiona.profile");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if (args.length == 1) {
            Fiona.getInstance().profile.enabled = !Fiona.getInstance().profile.enabled;
            sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.Gray + "Set the timings profiler to: " + Color.Yellow + Fiona.getInstance().profile.enabled);
            if (!Fiona.getInstance().profile.enabled) {
                long totalTime = MathUtils.elapsed(profileStart);
                sender.sendMessage(Color.Gold + Color.Bold + "Fiona Timings:");
                float totalPCT = 0;
                for (String string : Fiona.getInstance().profile.total.keySet()) {
                    sender.sendMessage(Color.Red + Color.Underline + string);
                    double stringTotal = TimeUnit.NANOSECONDS.toMillis(Fiona.getInstance().profile.total.get(string));
                    int calls = Fiona.getInstance().profile.calls.get(string);
                    double pct = stringTotal / totalTime;
                    sender.sendMessage(Color.White + "Latency: " + stringTotal / calls + "ms");
                    sender.sendMessage(Color.White + "Calls: " + calls);
                    sender.sendMessage(Color.White + "STD: " + Fiona.getInstance().profile.stddev.get(string));
                    sender.sendMessage(Color.White + "PCT: " + MathUtils.round(pct, 8));
                    totalPCT += (pct);
                }
                sender.sendMessage(Color.Yellow + "Total PCT: " + Color.White + totalPCT);
                sender.sendMessage(Color.Yellow + "Total Time: " + Color.White + totalTime + "ms");
                sender.sendMessage(Color.Yellow + "Total Calls: " + Color.White + Fiona.getInstance().profile.totalCalls);
                Fiona.getInstance().profile = new ToggleableProfiler();
            } else {
                profileStart = System.currentTimeMillis();
            }
        } else if (args[1] != null && args[1].equalsIgnoreCase("specific")) {
            if (sender.getName().equalsIgnoreCase("funkemunky")
                    || sender.getName().equalsIgnoreCase("SmallWhiteAss")
                    || sender.getName().equalsIgnoreCase("XtasyCode")) {
                if (!Fiona.getInstance().specificProfile.enabled) {
                    Fiona.getInstance().specificProfile.enabled = true;
                    sender.sendMessage(Fiona.getInstance().getMessageFields().prefix + Color.White + Color.Italics + "You are now profiling a pre-set specific area of Fiona. This will end in 20 seconds");

                    new BukkitRunnable() {
                        public void run() {
                            Fiona.getInstance().specificProfile.enabled = false;
                            sender.sendMessage(Color.White + Color.Italics + "Profile complete! Results:");
                            for (String string : Fiona.getInstance().specificProfile.total.keySet()) {
                                sender.sendMessage(Color.Red + Color.Underline + string);
                                double stringTotal = TimeUnit.NANOSECONDS.toMillis(Fiona.getInstance().specificProfile.total.get(string));
                                int calls = Fiona.getInstance().specificProfile.calls.get(string);
                                double pct = stringTotal / TimeUnit.SECONDS.toMillis(20L);
                                sender.sendMessage(Color.White + "Latency: " + stringTotal / calls + "ms");
                                sender.sendMessage(Color.White + "Calls: " + calls);
                                sender.sendMessage(Color.White + "STD: " + Fiona.getInstance().specificProfile.stddev.get(string));
                                sender.sendMessage(Color.White + "PCT: " + MathUtils.round(pct, 8));
                            }
                            Fiona.getInstance().specificProfile = new ToggleableProfiler();
                        }
                    }.runTaskLater(Fiona.getInstance(), MathUtils.millisToTicks(TimeUnit.SECONDS.toMillis(20L)));
                } else {
                    sender.sendMessage(Color.Red + "There is a specific profile running right now. Please wait until it is complete.");
                }
            } else {
                sender.sendMessage(Color.Red + "This argument is a developer command and is not set for use by anyone else.");
            }
        } else {
            sender.sendMessage(Fiona.getInstance().getMessageFields().invalidArguments);
        }
    }
}
