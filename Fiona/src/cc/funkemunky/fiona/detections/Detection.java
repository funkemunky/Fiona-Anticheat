package cc.funkemunky.fiona.detections;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.events.custom.FionaAlertEvent;
import cc.funkemunky.fiona.events.custom.FionaCancelEvent;
import cc.funkemunky.fiona.events.custom.FionaCheatEvent;
import cc.funkemunky.fiona.utils.*;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.*;

@Getter
@Setter
public abstract class Detection implements Listener {

    private Check parentCheck;
    private String id;
    private boolean enabled;
    private boolean executable;
    private ProtocolVersion versionMinimum, versionMaxmimum;
    private boolean cancellable;
    private int tpsToCancel = 12;
    private int ticks = 0;
    private int thresholdToFlag;
    private boolean experimental;
    private String nickname;
    private boolean bypassLag = false, bypassServerPos = false;
    private Map<String, Object> configValues = new HashMap<>();

    public Detection(Check parentCheck, String id, boolean enabled, boolean executable) {
        this.parentCheck = parentCheck;
        this.id = id;
        this.enabled = enabled;
        this.executable = executable;

        experimental = false;
        cancellable = false;

        thresholdToFlag = 10;

        if (parentCheck.isNeedsListener()) {
            Bukkit.getPluginManager().registerEvents(this, Fiona.getInstance());
        }
    }

    public Detection(Check parentCheck, String id, boolean enabled, boolean executable, boolean cancellable) {
        this.parentCheck = parentCheck;
        this.id = id;
        this.enabled = enabled;
        this.executable = executable;
        this.cancellable = cancellable;

        experimental = false;
        thresholdToFlag = 10;

        if (parentCheck.isNeedsListener()) {
            Bukkit.getPluginManager().registerEvents(this, Fiona.getInstance());
        }
    }

    public void addConfigValue(String name, Object object) {
        configValues.put(name, object);
    }

    public void setThreshold(int threshold) {
        this.thresholdToFlag = threshold;
    }

    public void debug(PlayerData data, String string) {
        if (data.checkDebugEnabled
                && data.debugDetection == this) {
            Fiona.getInstance().debuggingPlayer.sendMessage(Color.Gray + "DEBUG: " + string);
        }
    }

    public void onFunkeEvent(Object event, PlayerData data) {
        //Empty body.
    }

    public void onBukkitEvent(Event event, PlayerData data) {
        //Empty body.
    }

    public void flag(PlayerData dataPlayer, String data, int violations, boolean reliabilitySystem, boolean cancel) {
        if (!dataPlayer.player.hasPermission(Config.bypassPermission)
                || !Config.bypassEnabled) {

            if (Fiona.getInstance().tps > 12) {
                float toAdd = (dataPlayer.lastFlag.hasNotPassed(3) ? 4 * violations : violations) * (dataPlayer.reliabilityPercentage / 100);
                List<Violation> violationsList = Fiona.getInstance().getCheckManager().violations.getOrDefault(dataPlayer.player.getUniqueId(), new ArrayList<>());
                Optional<Violation> violationOp = violationsList.stream().filter(vl -> vl.getCheck().getName().equals(getParentCheck().getName())).findFirst();

                Violation violation = violationOp.orElseGet(() -> new Violation(getParentCheck()));

                violationsList.remove(violation);
                violation.addViolation(this, toAdd);
                violationsList.add(violation);

                FionaCheatEvent event = new FionaCheatEvent(dataPlayer.player, this, data, violation.getCombinedAmount());
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    Fiona.getInstance().getCheckManager().violations.put(dataPlayer.player.getUniqueId(), violationsList);
                    if (violation.getSpecificViolations().getOrDefault(this, 0f) > thresholdToFlag) {
                        if (cancel && parentCheck.isCancellable() && isCancellable() && violation.getCombinedAmount() > parentCheck.getCancelThreshold()) {
                            FionaCancelEvent e = new FionaCancelEvent(dataPlayer.player, parentCheck, parentCheck.getType());

                            Bukkit.getPluginManager().callEvent(e);
                            if (!e.isCancelled()) {
                                dataPlayer.setCancelled(parentCheck.getType(), 1);
                            }
                        }
                        JsonMessage jsonMessage = new JsonMessage();
                        JsonMessage.AMText alertMsg = jsonMessage.addText(Fiona.getInstance().getMessageFields().alertMessage.replaceAll("%playerName%", dataPlayer.player.getName()).replaceAll("%check%", parentCheck.getName() + (isExperimental() ? Color.Gray + Color.Italics + "(" + getId() + ")" : "(" + getId() + ")")).replaceAll("%vl%", String.valueOf(MathUtils.trim(2, violation.getCombinedAmount()))).replaceAll("%info%", Color.translate(data)).replaceAll("%ping%", dataPlayer.ping + "ms").replaceAll("%tps%", String.valueOf(MathUtils.round(Fiona.getInstance().tps, 2)))).addHoverText(Fiona.getInstance().getMessageFields().alertHoverMessage.replaceAll("%playerName%", dataPlayer.player.getName()).replaceAll("%info%", Color.translate(data)).replaceAll("%vl%", String.valueOf(MathUtils.trim(2, violation.getCombinedAmount()))).replaceAll("%ping%", dataPlayer.ping + "ms").replaceAll("%tps%", String.valueOf(MathUtils.round(Fiona.getInstance().tps, 2))));

                        FionaAlertEvent e = new FionaAlertEvent(dataPlayer.player, this, alertMsg.getMessage());
                        Bukkit.getPluginManager().callEvent(e);
                        if (!e.isCancelled()) {
                            if (!parentCheck.isTestMode()) {
                                if (Config.alertsDelayEnabled) {
                                    if (MathUtils.elapsed(parentCheck.getLastAlert(), Config.alertsDelayMillis)) {

                                        Fiona.getInstance().getDataManager().getDataObjects().stream().filter(staffPlayer -> staffPlayer.alerts && MiscUtils.hasPermissionForAlerts(staffPlayer.player)).forEach(staffPlayer -> {
                                            alertMsg.setClickEvent(JsonMessage.ClickableType.RunCommand, Config.alertsCommand.replaceAll("%sender%", staffPlayer.player.getName()).replaceAll("%cheater%", dataPlayer.player.getName()));
                                            jsonMessage.sendToPlayer(staffPlayer.player);
                                        });
                                        parentCheck.setLastAlert(System.currentTimeMillis());
                                    }
                                } else {
                                    if (!e.isCancelled()) {
                                        Fiona.getInstance().getDataManager().getDataObjects().stream().filter(staffPlayer -> staffPlayer.alerts && MiscUtils.hasPermissionForAlerts(staffPlayer.player)).forEach(staffPlayer -> {
                                            alertMsg.setClickEvent(JsonMessage.ClickableType.RunCommand, Config.alertsCommand.replaceAll("%sender%", staffPlayer.player.getName()).replaceAll("%cheater%", dataPlayer.player.getName()));
                                            jsonMessage.sendToPlayer(staffPlayer.player);
                                        });
                                    }
                                }
                            } else {
                                if (!dataPlayer.player.hasPermission("fiona.admin") || !dataPlayer.alerts) {
                                    jsonMessage.sendToPlayer(dataPlayer.player);
                                }
                                if (MathUtils.elapsed(parentCheck.getLastAlert(), Config.alertsDelayMillis)
                                        || !Config.alertsDelayEnabled) {
                                    Fiona.getInstance().getDataManager().getDataObjects().stream().filter(staffPlayer -> staffPlayer.alerts && MiscUtils.hasPermissionForAlerts(staffPlayer.player)).forEach(staffPlayer -> {
                                        alertMsg.setClickEvent(JsonMessage.ClickableType.RunCommand, Config.alertsCommand.replaceAll("%sender%", staffPlayer.player.getName()).replaceAll("%cheater%", dataPlayer.player.getName()));
                                        jsonMessage.sendToPlayer(staffPlayer.player);
                                    });
                                    parentCheck.setLastAlert(System.currentTimeMillis());
                                }
                            }
                        }
                    }
                    dataPlayer.lastFlag.reset();
                    Fiona.getInstance().getDataManager().getLogger().addLog(this, violation.getSpecificViolations().get(this), data, dataPlayer);
                }
            } else {
                String message = Fiona.getInstance().getMessageFields().prefix + Color.translate("&e" + dataPlayer.player.getName() + " &7would have flagged &e" + parentCheck.getName() + "(" + getId() + ") &7but the server lagged. &8[&c" + MathUtils.round(Fiona.getInstance().tps, 3) + "&8]");
                Fiona.getInstance().getDataManager().getDataObjects().stream().filter(staffPlayer -> staffPlayer.alerts && MiscUtils.hasPermissionForAlerts(staffPlayer.player)).forEach(staffPlayer -> staffPlayer.player.sendMessage(message));
            }

            if (!reliabilitySystem || dataPlayer.reliabilityPercentage > 50.0) {
                parentCheck.checkBan(dataPlayer, this);
            }
        }
    }
}
