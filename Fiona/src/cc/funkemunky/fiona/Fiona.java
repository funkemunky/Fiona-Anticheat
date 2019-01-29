package cc.funkemunky.fiona;

import cc.funkemunky.fiona.commands.FunkeCommandManager;
import cc.funkemunky.fiona.data.DataManager;
import cc.funkemunky.fiona.data.ReliabilitySystem;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckManager;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.bukkit.CancerListeners;
import cc.funkemunky.fiona.events.bukkit.DataEvents;
import cc.funkemunky.fiona.events.bukkit.PlayerConnectListeners;
import cc.funkemunky.fiona.events.custom.FionaPunishEvent;
import cc.funkemunky.fiona.events.system.EventManager;
import cc.funkemunky.fiona.profiling.ToggleableProfiler;
import cc.funkemunky.fiona.utils.*;
import cc.funkemunky.fiona.utils.blockbox.BlockBoxManager;
import cc.funkemunky.fiona.utils.gui.GUIManager;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.api.TinyProtocolHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;


@Getter
public class Fiona
        extends JavaPlugin {
    private static Fiona instance;
    public Set<Player> bannedPlayers;
    public double tps = -1;
    private boolean goldEnabled;
    public ConsoleCommandSender consoleSender;
    public ToggleableProfiler profile = new ToggleableProfiler();
    public ToggleableProfiler specificProfile = new ToggleableProfiler();
    public ScheduledExecutorService executorService;
    public ExecutorService executorOne;
    public ExecutorService executorTwo;
    public ExecutorService executorThree;
    public String serverVersion;
    public CommandSender debuggingPlayer;
    private TinyProtocolHandler protocol;
    private CheckManager checkManager;
    private FunkeCommandManager commandManager;
    private Messages messageFields;
    private File messagesFile;
    private FileConfiguration messages;
    private GUIManager guiManager;
    private ReliabilitySystem system;
    private BlockBoxManager blockBoxManager;
    private long timeGoldCheckStarted;
    private DataManager dataManager;
    private int currentTick;

    public static Fiona getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        consoleSender = Bukkit.getConsoleSender();

        consoleSender.sendMessage(Color.translate("&cInitializing Fiona..."));
        serverVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        instance = this;
        MiscUtils.printToConsole("&cLoading configuration objects...");

        startupConfiguration();

        MiscUtils.printToConsole("&cFiring up the thread turbines...");

        initializeThreads();

        MiscUtils.printToConsole("");
        MiscUtils.printToConsole("&eRegistered objects:");
        new BukkitRunnable() {
            public void run() {
                protocol = new TinyProtocolHandler();
                blockBoxManager = new BlockBoxManager();

                if(!getConfig().getString("gold.key").isEmpty()) {
                    getLogger().log(Level.INFO, "Starting download process ...");
                    if(!getConfig().getBoolean("gold.legacySendPort")) {
                        sendRequestToGold(80);
                        Fiona.getInstance().run(getConfig().getInt("gold.receivePort"));
                    } else {
                        sendRequestToGold(1024);
                        Fiona.getInstance().run(getConfig().getInt("gold.receivePort"));
                    }
                } else {
                    MiscUtils.printToConsole("&cGold key not set in config");
                }
            }
        }.runTaskLater(this, 20L);
        MiscUtils.printToConsole("&7- Packet handler");
        dataManager = new DataManager();
        MiscUtils.printToConsole("&7- Data handler");
        checkManager = new CheckManager();
        MiscUtils.printToConsole("&7- Check handler");
        new BlockUtils();
        new ReflectionsUtil();
        new Color();
        new MiscUtils();

        MiscUtils.printToConsole("&7- Utilities");
        messageFields = new Messages();
        commandManager = new FunkeCommandManager();
        bannedPlayers = new HashSet<>();
        guiManager = new GUIManager();
        system = new ReliabilitySystem();
        MiscUtils.printToConsole("&7- Misc data");

        MiscUtils.printToConsole("&cLoading data objects...");
        loadUsers();

        MiscUtils.printToConsole("&cFinalizing...");
        loadChecks();
        registerEvents();
        runTasks();

        MiscUtils.printToConsole("&aCompleted!");
    }

    private String unloadPlugin(String pl) {
        PluginManager pm = getServer().getPluginManager();
        SimplePluginManager spm = (SimplePluginManager)pm;
        SimpleCommandMap cmdMap = null;
        List plugins = null;
        Map names = null;
        Map commands = null;
        Map listeners = null;
        boolean reloadlisteners = true;
        if(spm != null) {
            try {
                Field tp = spm.getClass().getDeclaredField("plugins");
                tp.setAccessible(true);
                plugins = (List)tp.get(spm);
                Field arr$ = spm.getClass().getDeclaredField("lookupNames");
                arr$.setAccessible(true);
                names = (Map)arr$.get(spm);

                Field len$;
                try {
                    len$ = spm.getClass().getDeclaredField("listeners");
                    len$.setAccessible(true);
                    listeners = (Map)len$.get(spm);
                } catch (Exception var19) {
                    reloadlisteners = false;
                }

                len$ = spm.getClass().getDeclaredField("commandMap");
                len$.setAccessible(true);
                cmdMap = (SimpleCommandMap)len$.get(spm);
                Field i$ = cmdMap.getClass().getDeclaredField("knownCommands");
                i$.setAccessible(true);
                commands = (Map)i$.get(cmdMap);
            } catch (IllegalAccessException | NoSuchFieldException var20) {
                return "Failed to unload plugin!";
            }
        }

        String var21 = "";
        Plugin[] var22 = getServer().getPluginManager().getPlugins();
        int var23 = var22.length;

        for(int var24 = 0; var24 < var23; ++var24) {
            Plugin p = var22[var24];
            if(p.getDescription().getName().equalsIgnoreCase(pl)) {
                pm.disablePlugin(p);
                var21 = var21 + p.getName() + " ";
                if(plugins != null && plugins.contains(p)) {
                    plugins.remove(p);
                }

                if(names != null && names.containsKey(pl)) {
                    names.remove(pl);
                }

                Iterator it;
                if(listeners != null && reloadlisteners) {
                    it = listeners.values().iterator();

                    while(it.hasNext()) {
                        SortedSet entry = (SortedSet)it.next();
                        Iterator c = entry.iterator();

                        while(c.hasNext()) {
                            RegisteredListener value = (RegisteredListener)c.next();
                            if(value.getPlugin() == p) {
                                c.remove();
                            }
                        }
                    }
                }

                if(cmdMap != null) {
                    it = commands.entrySet().iterator();

                    while(it.hasNext()) {
                        Map.Entry var25 = (Map.Entry) it.next();
                        if(var25.getValue() instanceof PluginCommand) {
                            PluginCommand var26 = (PluginCommand)var25.getValue();
                            if(var26.getPlugin() == p) {
                                var26.unregister(cmdMap);
                                it.remove();
                            }
                        }
                    }
                }
            }
        }

        return var21 + "has been unloaded and disabled!";
    }

    public void run(int port) {
        try {
            ServerSocket tcpserver = new ServerSocket(port);
            timeGoldCheckStarted = System.currentTimeMillis();
            while (!MathUtils.elapsed(timeGoldCheckStarted, 15000)) {
                Socket socket = tcpserver.accept();

                InputStream is = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);

                Object object = ois.readObject();
                if (object instanceof String) {
                    String error = (String) object;
                    if (error.equalsIgnoreCase("Banned")) {
                        getLogger().log(Level.SEVERE, "Download quest denied due to piracy. Please join this discord if you think this is false: https://discord.gg/Z2Nappq");
                    } else {
                        getLogger().log(Level.SEVERE, "Download request denied for unknown reason. Please join this discord for support: https://discord.gg/Z2Nappq");
                    }
                    ois.close();
                    is.close();
                    goldEnabled = true;
                    socket.close();
                    tcpserver.close();
                } else if (object instanceof byte[]) {
                    getLogger().log(Level.INFO, "Proper response received! Downloading (this might take even longer)...");
                    byte[] fionaBytes = (byte[]) object;

                    File fiona = new File("plugins", "x91ffjk19.jar");

                    if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
                        net.minecraft.util.org.apache.commons.io.FileUtils.writeByteArrayToFile(fiona, fionaBytes);
                    } else {
                        org.apache.commons.io.FileUtils.writeByteArrayToFile(fiona, fionaBytes);
                    }

                    goldEnabled = true;

                    unloadPlugin("Fiona");
                    getLogger().log(Level.INFO, "Downloaded! Loading plugin...");
                    Plugin plugin = getServer().getPluginManager().loadPlugin(fiona);
                    ois.close();
                    is.close();
                    socket.close();
                    tcpserver.close();
                    getServer().getPluginManager().enablePlugin(plugin);
                    if (fiona.delete()) getLogger().log(Level.INFO, "Loaded successfully!");
                } else {
                    getLogger().log(Level.SEVERE, "Unknown error occured. Please join this discord for support: https://discord.gg/Z2Nappq");
                }
            }
        } catch (Exception e) {
            if (!(e instanceof SocketException)) {
                e.printStackTrace();
            }
        }
    }

    private void sendRequestToGold(int port) {
        try {
            getLogger().log(Level.INFO, "Sending request to server...");
            Socket clientSocket = new Socket("192.99.160.204", port);

            OutputStream os = clientSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);

            oos.writeUTF(getConfig().getString("gold.key") + ";" + getDescription().getVersion() + ";" + getConfig().getString("gold.serverIP") + ";" + getConfig().getInt("gold.receivePort"));
            getLogger().log(Level.INFO, "Sent! Waiting for response (this might take awhile)...");
            oos.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Cleaning up Fiona's junk.
    @Override
    public void onDisable() {
        clearFionaInstances();
        clearBukkitInstances();
    }

    private void initializeThreads() {
        int available = Runtime.getRuntime().availableProcessors();

        if (available >= 4) {
            executorOne = Executors.newSingleThreadExecutor();
            executorTwo = Executors.newSingleThreadExecutor();
            executorThree = Executors.newFixedThreadPool(3);
            executorService = Executors.newSingleThreadScheduledExecutor();
        } else {
            executorOne = Executors.newSingleThreadExecutor();
            executorTwo = executorOne;
            executorThree = executorOne;
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerConnectListeners(), this);
        Bukkit.getPluginManager().registerEvents(new DataEvents(), this);

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            Bukkit.getPluginManager().registerEvents(new CancerListeners(), this);
        }
        EventManager.register(new DataEvents());
    }

    private void startupConfiguration() {
        saveDefaultConfig();
        new Config();
        createMessages();
    }

    private void runTasks() {
        //TPS check
        new BukkitRunnable() {
            long sec;
            long currentSec;
            int ticks;

            public void run() {
                sec = (System.currentTimeMillis() / 1000L);
                if (currentSec == sec) {
                    ticks += 1;
                } else {
                    currentSec = sec;
                    tps = (tps == 0.0D ? ticks : (tps + ticks) / 2.0D);
                    ticks = 1;
                }
                currentTick++;
            }
        }.runTaskTimer(this, 0L, 1L);
    }

    private void loadUsers() {
        reloadPlayerData();
    }

    public void executeOnPlayer(Player player, Check check, Detection detection) {
        FionaPunishEvent event = new FionaPunishEvent(player, check);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            new BukkitRunnable() {
                public void run() {
                    for (String string : getConfig().getStringList("executableCommands")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getMessageFields().translateWithPrefix(string
                                .replaceAll("%player%", player.getName())
                                .replaceAll("%check%", check.getName())
                                .replaceAll("%nickname%", check.getNickName() + " (" + detection.getNickname() + ")")));
                    }
                }
            }.runTask(this);
            if (!check.isTestMode()) bannedPlayers.add(player);
        }
    }

    public void clearFionaInstances() {
        consoleSender.sendMessage(Color.translate("&cFiona's Custom Instances Cleared:"));
        getCheckManager().unregisterAll();
        consoleSender.sendMessage(Color.translate("&7- Checks"));
        EventManager.clearRegistered();
        consoleSender.sendMessage(Color.translate("&7- Events"));
        getDataManager().getDataObjects().clear();
        executorService.shutdown();
        consoleSender.sendMessage(Color.translate("&7- Objects"));
        getCommandManager().removeAllCommands();
        getCheckManager().violations.clear();
        consoleSender.sendMessage(Color.translate("&7- Commands"));
    }

    public void clearBukkitInstances() {
        consoleSender.sendMessage(Color.translate("&cFiona's Bukkit Instances Cleared:"));
        Fiona.getInstance().getServer().getScheduler().cancelTasks(this);
        consoleSender.sendMessage(Color.translate("&7- Tasks"));
        HandlerList.unregisterAll(Fiona.getInstance());
        consoleSender.sendMessage(Color.translate("&7- Listeners"));
        getCommandManager().removeAllCommands();
        consoleSender.sendMessage(Color.translate("&7- Commands"));
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        consoleSender.sendMessage(Color.translate("&7- PME"));
    }

    public void loadChecks() {
        for (Check check : getCheckManager().getChecks()) {
            String pathToCheck = "checks." + check.getName() + ".";
            if (getConfig().get("checks." + check.getName()) == null) {
                getConfig().set(pathToCheck + "enabled", check.isEnabled());
                getConfig().set(pathToCheck + "executable", check.isExecutable());
                getConfig().set(pathToCheck + "cancellable", check.isCancellable());
                getConfig().set(pathToCheck + "cancelThreshold", check.getCancelThreshold());
                getConfig().set(pathToCheck + "executableThreshold", check.getExecutableThreshold());
                getConfig().set(pathToCheck + "nickname", check.getNickName() == null ? check.getName() : check.getNickName());
                for (String name : check.getConfigValues().keySet()) {
                    getConfig().set(pathToCheck + "values." + name, check.getConfigValues().get(name));
                }
                saveConfig();
            }
            check.setEnabled(getConfig().getBoolean(pathToCheck + "enabled"));
            check.setExecutable(getConfig().getBoolean(pathToCheck + "executable"));
            check.setCancellable(getConfig().getBoolean(pathToCheck + "cancellable"));
            check.setCancelThreshold(getConfig().getInt(pathToCheck + "cancelThreshold"));
            check.setExecutableThreshold(getConfig().getInt(pathToCheck + "executableThreshold"));
            check.setNickName(getConfig().getString(pathToCheck + "nickname"));
            for (String name : check.getConfigValues().keySet()) {
                if (getConfig().get("checks." + check.getName() + ".values." + name) != null) {
                    check.getConfigValues().put(name, getConfig().get("checks." + check.getName() + ".values." + name));
                } else {
                    getConfig().set("checks." + check.getName() + ".values." + name, check.getConfigValues().get(name));
                    saveConfig();
                }
            }
            for (Detection detection : check.getDetections()) {
                String pathToDetection = pathToCheck + "detections." + detection.getId() + ".";
                if (getConfig().get(pathToCheck + "detections." + detection.getId()) == null) {
                    getConfig().set(pathToDetection + "enabled", detection.isEnabled());
                    getConfig().set(pathToDetection + "executable", detection.isExecutable());
                    getConfig().set(pathToDetection + "cancellable", detection.isCancellable());
                    getConfig().set(pathToDetection + "nickname", detection.getNickname() == null ? detection.getId() : detection.getNickname());
                    for (String name : detection.getConfigValues().keySet()) {
                        getConfig().set(pathToDetection + "values." + name, detection.getConfigValues().get(name));
                    }
                    saveConfig();
                }
                detection.setEnabled(getConfig().getBoolean(pathToDetection + "enabled"));
                detection.setExecutable(getConfig().getBoolean(pathToDetection + "executable"));
                detection.setNickname(getConfig().getString(pathToDetection + "nickname"));
                for (String name : detection.getConfigValues().keySet()) {
                    if (getConfig().get(pathToDetection + "values." + name) != null) {
                        detection.getConfigValues().put(name, getConfig().get(pathToDetection + "values." + name));
                    } else {
                        getConfig().set(pathToDetection + "values." + name, detection.getConfigValues().get(name));
                        saveConfig();
                    }
                }
            }
        }
    }

    public void saveChecks() {
        for (Check check : getCheckManager().getChecks()) {
            String pathToCheck = "checks." + check.getName() + ".";
            getConfig().set(pathToCheck + "enabled", check.isEnabled());
            getConfig().set(pathToCheck + "executable", check.isExecutable());
            getConfig().set(pathToCheck + "cancellable", check.isCancellable());
            getConfig().set(pathToCheck + "cancelThreshold", check.getCancelThreshold());
            getConfig().set(pathToCheck + "executableThreshold", check.getExecutableThreshold());
            getConfig().set(pathToCheck + "nickname", check.getNickName() == null ? check.getName() : check.getNickName());

            for (String name : check.getConfigValues().keySet()) {
                getConfig().set(pathToCheck + "values." + name, check.getConfigValues().get(name));
            }

            for (Detection detection : check.getDetections()) {
                String pathToDetection = pathToCheck + "detections." + detection.getId() + ".";
                if (getConfig().get(pathToCheck + "detections." + detection.getId()) == null) {
                    getConfig().set(pathToDetection + "enabled", detection.isEnabled());
                    getConfig().set(pathToDetection + "executable", detection.isExecutable());
                    getConfig().set(pathToDetection + "cancellable", detection.isCancellable());
                    getConfig().set(pathToDetection + "nickname", detection.getNickname() == null ? detection.getId() : detection.getNickname());
                    for (String name : detection.getConfigValues().keySet()) {
                        getConfig().set(pathToDetection + "values." + name, detection.getConfigValues().get(name));
                    }
                }
            }
        }
        saveConfig();
    }
    public FileConfiguration getMessages() {
        if (messages == null) {
            reloadMessages();
        }
        return messages;
    }


    public void saveMessages() {
        if (messages == null || messagesFile == null) {
            return;
        }
        try {
            getMessages().save(messagesFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + messagesFile, ex);
        }
    }

    public void createMessages() {
        if (messagesFile == null) {
            messagesFile = new File(getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
    }

    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(getDataFolder(), "messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        // Look for defaults in the jar
        try {
            Reader defConfigStream = new InputStreamReader(this.getResource("messages.yml"), "UTF8");
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            messages.setDefaults(defConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadConfigObject() {
        new Config();
    }

    public void reloadMessagesObject() {
        this.messageFields = new Messages();
    }

    public void reloadPlayerData() {
        getDataManager().getDataObjects().clear();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            getDataManager().createDataObject(player);
        }
    }
}

