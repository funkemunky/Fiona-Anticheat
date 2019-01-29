package cc.funkemunky.fiona.utils.gui;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.utils.Color;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class GUI implements Listener {
    private String name;
    private Inventory inventory;
    private List<GUIItem> guiItems;

    public GUI(String name, String title, int lines) {
        this.name = name;
        inventory = Bukkit.createInventory(null, lines * 9, Color.translate(title));

        guiItems = new ArrayList<>();

        Fiona.getInstance().getServer().getPluginManager().registerEvents(this, Fiona.getInstance());
    }

    public GUI(String name, String title, int lines, GUIItem... items) {
        this.name = name;
        inventory = Bukkit.createInventory(null, lines * 9, Color.translate(title));

        guiItems = Arrays.asList(items);

        for (GUIItem item : guiItems) {
            inventory.setItem(item.getLocation(), item.getItem());
        }

        Fiona.getInstance().getServer().getPluginManager().registerEvents(this, Fiona.getInstance());
    }

    public GUI(String name, String title, int lines, Collection<GUIItem> items) {
        this.name = name;
        inventory = Bukkit.createInventory(null, lines * 9, Color.translate(title));

        guiItems = (List<GUIItem>) items;

        for (GUIItem item : guiItems) {
            inventory.setItem(item.getLocation(), item.getItem());
        }
    }

    public void addItem(GUIItem item) {
        guiItems.add(item);

        for (GUIItem gItem : guiItems) {
            inventory.setItem(gItem.getLocation(), gItem.getItem());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public List<GUIItem> getGuiItems() {
        return guiItems;
    }

    public void setGuiItems(List<GUIItem> guiItems) {
        this.guiItems = guiItems;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player
                && event.getWhoClicked().hasPermission("fiona.menu")
                && event.getCurrentItem() != null
                && event.getClickedInventory() != null
                && !event.getCurrentItem().getType().equals(Material.AIR)
                && event.getClickedInventory().getTitle().equals(inventory.getTitle())) {
            Player player = (Player) event.getWhoClicked();
            Optional<GUIItem> opItem = guiItems.stream().filter(item -> event.getCurrentItem().isSimilar(item.getItem())).findFirst();

            if (opItem.isPresent()) {
                GUIItem guiItem = opItem.get();

                for (GUIItem.Action action : guiItem.getAction()) {
                    if (action.getAction() == event.getAction()) {
                        if (action.isCloseGUI()) {
                            player.closeInventory();
                        }
                        if (action.getType() == GUIItem.ActionType.CONSOLE_COMMAND) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.getData());
                        } else if (action.getType() == GUIItem.ActionType.PLAYER_COMMAND) {
                            Bukkit.dispatchCommand(player, action.getData());
                        } else if (action.getType() == GUIItem.ActionType.OPEN_INVENTORY) {
                            Fiona.getInstance().getGuiManager().openInventory(player, action.getData());
                        }

                        if (ChatColor.stripColor(event.getClickedInventory().getTitle()).startsWith("Check")
                                && !event.getCurrentItem().getType().equals(Material.AIR)) {
                            Fiona.getInstance().getGuiManager().updateCheckItems();
                        } else if (ChatColor.stripColor(event.getClickedInventory().getTitle()).startsWith("Detection")
                                && !event.getCurrentItem().getType().equals(Material.AIR)) {
                            Fiona.getInstance().getGuiManager().updateDetectionItems();
                        }
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    public static class GUIItem {
        private ItemStack item;
        private int location;
        private Action[] action;

        public GUIItem(ItemStack item, int location, Action action) {
            this.item = item;
            this.location = location;
            this.action = new Action[]{action};
        }

        public GUIItem(ItemStack item, int location, Action... action) {
            this.item = item;
            this.location = location;
            this.action = action;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getLocation() {
            return location;
        }

        public Action[] getAction() {
            return action;
        }

        public enum ActionType {
            CONSOLE_COMMAND, PLAYER_COMMAND, OPEN_INVENTORY, NONE;
        }

        public static class Action {
            private ActionType type;
            private String data;
            private InventoryAction action;
            private boolean closeGUI;

            public Action(ActionType type, String data) {
                this.type = type;
                this.data = data;
                this.closeGUI = false;
                action = InventoryAction.PICKUP_ALL;
            }

            public Action(ActionType type, String data, boolean closeGui) {
                this.type = type;
                this.data = data;
                this.closeGUI = closeGui;
                action = InventoryAction.PICKUP_ALL;
            }

            public Action(ActionType type, String data, InventoryAction action) {
                this.type = type;
                this.data = data;
                this.closeGUI = false;
                this.action = action;
            }

            public Action(ActionType type, String data, InventoryAction action, boolean closeGui) {
                this.type = type;
                this.data = data;
                this.closeGUI = closeGui;
                this.action = action;
            }

            public ActionType getType() {
                return type;
            }

            public String getData() {
                return data;
            }

            public InventoryAction getAction() {
                return action;
            }

            public boolean isCloseGUI() {
                return closeGUI;
            }
        }
    }
}
