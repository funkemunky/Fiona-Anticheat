package cc.funkemunky.fiona.utils.gui;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.utils.Color;
import cc.funkemunky.fiona.utils.Config;
import cc.funkemunky.fiona.utils.MiscUtils;
import com.google.common.collect.Sets;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GUIManager {
    private Set<GUI> guiSet;

    public GUIManager() {
        guiSet = Sets.newHashSet();

        addMainGUI();
        addCheckMenu();
        addCheckGUIs();
    }

    private void addCheckMenu() {
        GUI gui = new GUI("Fiona-Check-Menu", "&6&lCheck Settings", 3);

        createItem(gui, new GUI.GUIItem(MiscUtils.createItem(Material.PAPER, 1, "&eToggle"), 11, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Check-Toggle", false)));
        createItem(gui, new GUI.GUIItem(MiscUtils.createItem(Material.PAPER, 1, "&eCancellable"), 13, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Check-Cancellable", false)));
        createItem(gui, new GUI.GUIItem(MiscUtils.createItem(Material.PAPER, 1, "&eExecutable"), 15, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Check-Executable", false)));
        createItem(gui, new GUI.GUIItem(MiscUtils.createItem(Material.REDSTONE, 1, "&cBack"), 26, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Menu")));
        addGUI(gui);
    }

    private void addCheckGUIs() {
        String[] guis = new String[]{"Toggle", "Cancellable", "Executable"};

        for (String name : guis) {
            GUI gui = new GUI("Fiona-Check-" + name, "&6&lCheck " + (!name.equals("Toggle") ? name : "On/Off"), 6);

            for (int i = 0; i < Fiona.getInstance().getCheckManager().getChecks().size(); i++) {
                Check check = Fiona.getInstance().getCheckManager().getChecks().get(i);

                GUI detectionGui = new GUI("Fiona-Detection-" + check.getName() + "-" + name, "&6&lDetection " + (!name.equals("Toggle") ? name : "On/Off"), 3);

                for (int i2 = 0; i2 < check.getDetections().size(); i2++) {
                    Detection detection = check.getDetections().get(i2);
                    switch (name) {
                        case "Toggle":
                            createItem(detectionGui, new GUI.GUIItem(MiscUtils.createItem((detection.isEnabled() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + detection.getId(), "&fLeft TypeA &7to toggle.", "", "&7Enabled: " + (detection.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (detection.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (detection.isCancellable() ? "&atrue" : "&cfalse")), i2, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName() + " " + detection.getId(), false)));
                            break;
                        case "Cancellable":
                            createItem(detectionGui, new GUI.GUIItem(MiscUtils.createItem((detection.isCancellable() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + detection.getId(), "&fLeft TypeA &7to toggle.", "", "&7Enabled: " + (detection.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (detection.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (detection.isCancellable() ? "&atrue" : "&cfalse")), i2, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName() + " " + detection.getId(), false)));
                            break;
                        default:
                            createItem(detectionGui, new GUI.GUIItem(MiscUtils.createItem((detection.isExecutable() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + detection.getId(), "&fLeft TypeA &7to toggle.", "", "&7Enabled: " + (detection.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (detection.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (detection.isCancellable() ? "&atrue" : "&cfalse")), i2, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName() + " " + detection.getId(), false)));
                            break;
                    }
                }
                createItem(detectionGui, new GUI.GUIItem(MiscUtils.createItem(Material.REDSTONE, 1, "&cBack"), 26, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Check-" + name)));
                addGUI(detectionGui);
                switch (name) {
                    case "Toggle":
                        createItem(gui, new GUI.GUIItem(MiscUtils.createItem((check.isEnabled() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + check.getName(), "&fLeft TypeA &7to toggle.", "", "&7Enabled: " + (check.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (check.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (check.isCancellable() ? "&atrue" : "&cfalse")), i, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName(), false), new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Detection-" + check.getName() + "-" + name, InventoryAction.MOVE_TO_OTHER_INVENTORY, false)));
                        break;
                    case "Cancellable":
                        createItem(gui, new GUI.GUIItem(MiscUtils.createItem((check.isCancellable() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + check.getName(), "&fLeft TypeA &7to toggle.", "", "&7Enabled: " + (check.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (check.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (check.isCancellable() ? "&atrue" : "&cfalse")), i, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName(), false), new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Detection-" + check.getName() + "-" + name, InventoryAction.MOVE_TO_OTHER_INVENTORY, false)));
                        break;
                    default:
                        createItem(gui, new GUI.GUIItem(MiscUtils.createItem((check.isExecutable() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + check.getName(), "&fLeft TypeA &7to toggle.", "", "&7Enabled: " + (check.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (check.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (check.isCancellable() ? "&atrue" : "&cfalse")), i, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName(), false), new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Detection-" + check.getName() + "-" + name, InventoryAction.MOVE_TO_OTHER_INVENTORY, false)));
                        break;
                }
            }
            createItem(gui, new GUI.GUIItem(MiscUtils.createItem(Material.REDSTONE, 1, "&cBack"), 53, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Check-Menu")));

            addGUI(gui);
        }
    }

    public void updateCheckItems() {
        String[] guis = new String[]{"Toggle", "Cancellable", "Executable"};

        for (String name : guis) {
            Optional<GUI> opGUI = getGuiByName("Fiona-Check-" + name);
            if (opGUI.isPresent()) {
                GUI gui = opGUI.get();
                gui.getGuiItems().clear();
                for (int i = 0; i < Fiona.getInstance().getCheckManager().getChecks().size(); i++) {
                    Check check = Fiona.getInstance().getCheckManager().getChecks().get(i);
                    switch (name) {
                        case "Toggle":
                            createItem(gui, new GUI.GUIItem(MiscUtils.createItem((check.isEnabled() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + check.getName(), "&fLeft TypeA &7to toggle.", "&fShift + Left TypeA &7to modify detections.", "", "&7Enabled: " + (check.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (check.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (check.isCancellable() ? "&atrue" : "&cfalse")), i, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName(), false), new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Detection-" + check.getName() + "-" + name, InventoryAction.MOVE_TO_OTHER_INVENTORY, false)));
                            break;
                        case "Cancellable":
                            createItem(gui, new GUI.GUIItem(MiscUtils.createItem((check.isCancellable() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + check.getName(), "&fLeft TypeA &7to toggle.", "&fShift + Left TypeA &7to modify detections.", "", "&7Enabled: " + (check.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (check.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (check.isCancellable() ? "&atrue" : "&cfalse")), i, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName(), false), new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Detection-" + check.getName() + "-" + name, InventoryAction.MOVE_TO_OTHER_INVENTORY, false)));
                            break;
                        default:
                            createItem(gui, new GUI.GUIItem(MiscUtils.createItem((check.isExecutable() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + check.getName(), "&fLeft TypeA &7to toggle.", "&fShift + Left TypeA &7to modify detections.", "", "&7Enabled: " + (check.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (check.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (check.isCancellable() ? "&atrue" : "&cfalse")), i, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName(), false), new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Detection-" + check.getName() + "-" + name, InventoryAction.MOVE_TO_OTHER_INVENTORY, false)));
                            break;
                    }
                }
                createItem(gui, new GUI.GUIItem(MiscUtils.createItem(Material.REDSTONE, 1, "&cBack"), 53, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Check-Menu")));
            }
        }
    }

    public void updateDetectionItems() {
        String[] guis = new String[]{"Toggle", "Cancellable", "Executable"};

        for (Check check : Fiona.getInstance().getCheckManager().getChecks()) {
            for (String name : guis) {
                Optional<GUI> opGUI = getGuiByName("Fiona-Detection-" + check.getName() + "-" + name);
                if (opGUI.isPresent()) {
                    GUI gui = opGUI.get();
                    gui.getGuiItems().clear();
                    for (int i = 0; i < check.getDetections().size(); i++) {
                        Detection detection = check.getDetections().get(i);
                        switch (name) {
                            case "Toggle":
                                createItem(gui, new GUI.GUIItem(MiscUtils.createItem((detection.isEnabled() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + detection.getId(), "&fLeft TypeA &7to toggle.", "", "&7Enabled: " + (detection.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (detection.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (detection.isCancellable() ? "&atrue" : "&cfalse")), i, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName() + " " + detection.getId().replaceAll(" ", "_"), false)));
                                break;
                            case "Cancellable":
                                createItem(gui, new GUI.GUIItem(MiscUtils.createItem((detection.isCancellable() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + detection.getId(), "&fLeft TypeA &7to toggle.", "", "&7Enabled: " + (detection.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (detection.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (detection.isCancellable() ? "&atrue" : "&cfalse")), i, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName() + " " + detection.getId().replaceAll(" ", "_"), false)));
                                break;
                            default:
                                createItem(gui, new GUI.GUIItem(MiscUtils.createItem((detection.isExecutable() ? Material.ENCHANTED_BOOK : Material.BOOK), 1, Color.Yellow + detection.getId(), "&fLeft TypeA &7to toggle.", "", "&7Enabled: " + (detection.isEnabled() ? "&atrue" : "&cfalse"), "&7Executable: " + (detection.isExecutable() ? "&atrue" : "&cfalse"), "&7Cancellable: " + (detection.isCancellable() ? "&atrue" : "&cfalse")), i, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.CONSOLE_COMMAND, "fiona " + name.toLowerCase() + " " + check.getName() + " " + detection.getId().replaceAll(" ", "_"), false)));
                                break;
                        }
                    }
                    createItem(gui, new GUI.GUIItem(MiscUtils.createItem(Material.REDSTONE, 1, "&cBack"), 26, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Check-" + name)));
                }
            }
        }
    }

    private void addMainGUI() {
        GUI gui = new GUI("Fiona-Menu", "&6&lFiona Menu", 5);

        if (Config.animationType.equals("RAINBOW")) {
            new ItemAnimation(gui, MiscUtils.createItem(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13) ? Material.getMaterial("LEGACY_STAINED_GLASS_PANE") : Material.getMaterial("STAINED_GLASS_PANE"), 1, ""), ItemAnimation.AnimationType.RAINBOW, 100L, TimeUnit.MILLISECONDS, 0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 44, 43, 42, 41, 40, 39, 38, 37, 36, 27, 18, 9);
        }

        createItem(gui, new GUI.GUIItem(MiscUtils.createItem(Material.ENCHANTED_BOOK, 1, "&eCheck Menu"), 20, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.OPEN_INVENTORY, "Fiona-Check-Menu")));
        createItem(gui, new GUI.GUIItem(MiscUtils.createItem(Material.ENCHANTED_BOOK, 1, "&cInformation", "", "&7You are using &fFiona v" + Fiona.getInstance().getDescription().getVersion(), "&7by &ffunkemunky&7, &fXtasyCode&7, and &fMartin&7.", "", "&7There have been &f" + Fiona.getInstance().bannedPlayers.size() + " players &7detected today."), 22, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.NONE, "N/A", false)));
        createItem(gui, new GUI.GUIItem(MiscUtils.createItem(Material.ENCHANTED_BOOK, 1, "&eReload Fiona"), 24, new GUI.GUIItem.Action(GUI.GUIItem.ActionType.PLAYER_COMMAND, "fiona reload full", false)));
        addGUI(gui);
    }

    public void addGUI(GUI gui) {
        guiSet.add(gui);
    }

    public void createItem(GUI gui, GUI.GUIItem item) {
        gui.addItem(item);
    }

    public void createItem(GUI gui, GUI.GUIItem item, int min, int max) {
        for (int i = min; i < max; i++) {
            gui.addItem(item);
        }
    }

    public Optional<GUI> getGuiByName(String name) {
        Optional<GUI> guiOp = guiSet.stream().filter(gui -> gui.getName().equalsIgnoreCase(name)).findFirst();

        return guiOp;
    }

    public void openInventory(Player player, String name) {
        Optional<GUI> guiOp = getGuiByName(name);

        if (guiOp.isPresent()) {
            GUI gui = guiOp.get();

            player.openInventory(gui.getInventory());
        }
    }
}
