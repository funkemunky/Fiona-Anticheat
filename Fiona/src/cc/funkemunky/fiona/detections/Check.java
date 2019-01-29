package cc.funkemunky.fiona.detections;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.events.custom.FionaPunishEvent;
import cc.funkemunky.fiona.utils.Color;
import cc.funkemunky.fiona.utils.Config;
import cc.funkemunky.fiona.utils.MiscUtils;
import cc.funkemunky.fiona.utils.Violation;
import com.google.common.collect.Lists;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public abstract class Check implements Listener {
    private static Check instance;
    private String name;
    private CheckType type;
    private boolean enabled;
    private boolean cancellable;
    private int cancelThreshold;
    private int executableThreshold;
    private long lastAlert = 0;
    private boolean testMode;
    private boolean executable;
    private String nickName;
    private boolean needsListener;
    private List<Detection> detections;
    private Map<String, Object> configValues = new HashMap<>();


    public Check(String name, CheckType type, boolean enabled, boolean executable, boolean cancellable, boolean needsListener, int executableThreshold, int cancelThreshold) {
        this.name = name;
        this.type = type;
        this.enabled = enabled;
        this.executable = executable;
        this.cancellable = cancellable;
        this.cancelThreshold = cancelThreshold;
        this.needsListener = needsListener;
        this.executableThreshold = executableThreshold;
        this.executableThreshold = executableThreshold;
        this.detections = new CopyOnWriteArrayList<>();

        instance = this;

        testMode = Fiona.getInstance().getConfig().getBoolean("testmode");
    }

    public Check(String name, CheckType type, boolean enabled, boolean executable, boolean cancellable, boolean needsListener, int executableThreshold, long millis, int cancelThreshold) {
        this.name = name;
        this.type = type;
        this.enabled = enabled;
        this.executable = executable;
        this.cancellable = cancellable;
        this.cancelThreshold = cancelThreshold;
        this.executableThreshold = executableThreshold;
        this.needsListener = needsListener;
        this.detections = new CopyOnWriteArrayList<>();

        instance = this;

        Fiona.getInstance().executorService.scheduleAtFixedRate(() -> {
            Fiona.getInstance().profile.start("Task:CheckReset:" + name);

            if (Config.checkVioResetBroadcast) {
                for (PlayerData data : Fiona.getInstance().getDataManager().getDataObjects()) {
                    if (data.alerts && MiscUtils.hasPermissionForAlerts(data.player)) {
                        data.player.sendMessage(Fiona.getInstance().getMessageFields().checkVioReset.replaceAll("%check%", name));
                    }
                    List<Violation> violations = Fiona.getInstance().getCheckManager().violations.getOrDefault(data.player.getUniqueId(), Lists.newArrayList());

                    if(violations.size() > 0) {
                        violations.stream().filter(vio -> vio.getCheck().getName().equals(getName())).forEach(violations::remove);

                        Fiona.getInstance().getCheckManager().violations.put(data.player.getUniqueId(), violations);
                    }
                }
            }
            Fiona.getInstance().profile.stop("Task:CheckReset:" + name);
        }, 1L, millis, TimeUnit.MILLISECONDS);

        testMode = Fiona.getInstance().getConfig().getBoolean("testmode");
    }


    public static Check getInstance() {
        return instance;
    }

    public void addConfigValue(String name, Object object) {
        configValues.put(name, object);
    }

    public void addDetection(Detection detection) {
        if (detection.getVersionMinimum() != null) {
            if (ProtocolVersion.getGameVersion().isOrAbove(detection.getVersionMinimum())) {
                detections.add(detection);
                MiscUtils.printToConsole(Color.Green + getName() + "(" + detection.getId() + ") was added since you are using " + Color.Yellow + ProtocolVersion.getGameVersion().getServerVersion());
            } else
                MiscUtils.printToConsole(Color.Red + getName() + "(" + detection.getId() + ") was not added since you are using " + Color.Yellow + ProtocolVersion.getGameVersion().getServerVersion());
        } else if (detection.getVersionMaxmimum() != null) {
            if (ProtocolVersion.getGameVersion().isBelow(detection.getVersionMaxmimum()) || ProtocolVersion.getGameVersion().equals(detection.getVersionMaxmimum())) {
                detections.add(detection);
                MiscUtils.printToConsole(Color.Green + getName() + "(" + detection.getId() + ") was added since you are using " + Color.Yellow + ProtocolVersion.getGameVersion().getServerVersion());
            } else
                MiscUtils.printToConsole(Color.Red + getName() + "(" + detection.getId() + ") was not added since you are using " + Color.Yellow + ProtocolVersion.getGameVersion().getServerVersion());
        } else {
            detections.add(detection);
        }
    }

    public Detection getDetectionByName(String name) {
        return detections.stream().filter(detection -> detection.getId().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean isDetection(String name) {
        return detections.stream().anyMatch(detection -> detection.getId().equalsIgnoreCase(name));
    }

    public void setEnabled(boolean enabled) {
        if (needsListener) {
            if (enabled) {
                Fiona.getInstance().getServer().getPluginManager().registerEvents(this, Fiona.getInstance());
            } else {
                HandlerList.unregisterAll(this);
            }
        }
        this.enabled = enabled;
    }

    public void checkBan(PlayerData data, Detection detection) {
        if (Fiona.getInstance().bannedPlayers.contains(data.player)) return;

        if ((!this.isExecutable() || !detection.isExecutable()) || data.getViolations(detection) < this.getExecutableThreshold() || data.reliabilityPercentage < 75)
            return;
        FionaPunishEvent event = new FionaPunishEvent(data.player, this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            ((cc.funkemunky.fiona.data.logging.Yaml) Fiona.getInstance().getDataManager().getLogger()).dumpLog(data);
            Fiona.getInstance().executeOnPlayer(data.player, this, detection);
        }
        Fiona.getInstance().getCheckManager().violations.remove(data.player.getUniqueId());
    }

}

