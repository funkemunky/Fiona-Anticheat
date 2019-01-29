package cc.funkemunky.fiona.utils.gui;

import cc.funkemunky.fiona.Fiona;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class ItemAnimation {
    private ItemStack[] item;
    private int[] locations;
    private long time;
    private TimeUnit unit;
    private int current = 1;
    private GUI gui;
    private AnimationType type;

    public ItemAnimation(GUI gui, AnimationType type, long time, TimeUnit unit, int... locations) {
        this.gui = gui;
        this.locations = locations;
        this.time = time;
        this.unit = unit;


        this.type = type;
        processAnimation();
    }

    public ItemAnimation(GUI gui, ItemStack item, AnimationType type, long time, TimeUnit unit, int... locations) {
        this.gui = gui;
        this.item = new ItemStack[]{item};
        this.locations = locations;
        this.time = time;
        this.unit = unit;


        this.type = type;
        processAnimation();
    }

    public ItemAnimation(GUI gui, int location, long time, TimeUnit unit, ItemStack... item) {
        this.gui = gui;
        this.item = item;
        this.locations = new int[]{location};
        this.time = time;
        this.unit = unit;

        this.type = AnimationType.ITEM;
        processAnimation();
    }

    public ItemAnimation(GUI gui, int[] location, long time, TimeUnit unit, ItemStack... item) {
        this.gui = gui;
        this.item = item;
        this.locations = location;
        this.time = time;
        this.unit = unit;

        this.type = AnimationType.ITEM;
        processAnimation();
    }

    public void processAnimation() {
        if (type == AnimationType.POSITION) {
            Fiona.getInstance().executorService.scheduleAtFixedRate(() -> {
                gui.getInventory().setItem(locations[current], new ItemStack(Material.AIR, 1));
                current = current + 1 == locations.length ? 0 : current + 1;
                gui.getInventory().setItem(locations[current], item[0]);
            }, 0L, time, unit);
        } else if (type == AnimationType.ITEM) {
            Fiona.getInstance().executorService.scheduleAtFixedRate(() -> {
                current = current + 1 == item.length ? 0 : current + 1;
                for (int i : locations) {
                    gui.getInventory().setItem(i, item[current]);
                }
            }, 0L, time, unit);
        } else {
            Fiona.getInstance().executorService.scheduleAtFixedRate(() -> {
                new BukkitRunnable() {
                    public void run() {
                        switch (current) {
                            case 1: {
                                item[0].setDurability((short) 14);
                                for (int i : locations) {
                                    gui.getInventory().setItem(i, item[0]);
                                }
                                break;
                            }
                            case 2: {
                                item[0].setDurability((short) 1);
                                for (int i : locations) {
                                    gui.getInventory().setItem(i, item[0]);
                                }
                                break;
                            }
                            case 3: {
                                item[0].setDurability((short) 4);
                                for (int i : locations) {
                                    gui.getInventory().setItem(i, item[0]);
                                }
                                break;
                            }
                            case 4: {
                                item[0].setDurability((short) 5);
                                for (int i : locations) {
                                    gui.getInventory().setItem(i, item[0]);
                                }
                                break;
                            }
                            case 5: {
                                item[0].setDurability((short) 3);
                                for (int i : locations) {
                                    gui.getInventory().setItem(i, item[0]);
                                }
                                break;
                            }
                            case 6: {
                                item[0].setDurability((short) 2);
                                for (int i : locations) {
                                    gui.getInventory().setItem(i, item[0]);
                                }
                                break;
                            }
                        }
                        current = current == 6 ? 1 : current + 1;
                    }
                }.runTask(Fiona.getInstance());
            }, 0L, time, unit);
        }
    }

    public enum AnimationType {
        ITEM("ITEM"), POSITION("POSITION"), RAINBOW("RAINBOW"), NONE("NONE");
        String name;

        AnimationType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
