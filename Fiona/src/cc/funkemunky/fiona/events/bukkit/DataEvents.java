package cc.funkemunky.fiona.events.bukkit;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.MovementParser;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.*;
import cc.funkemunky.fiona.events.system.EventManager;
import cc.funkemunky.fiona.events.system.EventMethod;
import cc.funkemunky.fiona.events.system.Listener;
import cc.funkemunky.fiona.utils.*;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.api.TinyProtocolHandler;
import com.ngxdev.tinyprotocol.packet.in.*;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutEntityMetadata;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutTransaction;
import com.ngxdev.tinyprotocol.packet.out.WrappedPacketPlayOutWorldParticle;
import com.ngxdev.tinyprotocol.packet.types.WrappedEnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class DataEvents
        implements org.bukkit.event.Listener,
        Listener {

    @EventMethod
    public void onPacketSend(PacketSendEvent e) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(e.getPlayer());

        if (data != null) {
            switch (e.getType()) {
                case Packet.Server.POSITION: {
                    //Added the distance check to prevent cheat clients from intentionally invoking position packets.

                    if (!data.isBeingCancelled) {
                        data.serverPosTicks++;
                    }
                    break;
                }
                case Packet.Server.TRANSACTION:
                    data.lastServerKeepAlive = System.currentTimeMillis();
                    break;
            }
            callFunkeEvent(e, data);
        }
    }

    

    @EventMethod
    public void onPacketReceive(PacketRecieveEvent e) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(e.getPlayer());

        if (data == null) {
            return;
        }

        switch (e.getType()) {
            case Packet.Client.POSITION:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.LEGACY_POSITION:
            case Packet.Client.LEGACY_LOOK:
            case Packet.Client.LEGACY_POSITION_LOOK: {
                WrappedInFlyingPacket flying = new WrappedInFlyingPacket(e.getPacket(), e.getPlayer());

                e.setCancelled(MovementParser.parse(flying, data));
                if (data.lastTeleport.hasPassed(3) && data.serverPosTicks > 0) data.serverPosTicks--;

                if ((data.isUsingItem = Fiona.getInstance().getBlockBoxManager().getBlockBox().isUsingItem(e.getPlayer()))) {

                    data.lastUseItem.reset();
                }
                break;
            }
            case Packet.Client.FLYING: {
                data.boundingBox = MiscUtils.getEntityBoundingBox(e.getPlayer());
                data.movement.deltaXZ = 0;
                data.movement.deltaY = 0;

                if (data.boxDebugEnabled
                        && (data.boxDebugType == BoxDebugType.HITBOX
                        || data.boxDebugType == BoxDebugType.ALL)) {
                    for (float x = data.boundingBox.minX; x < data.boundingBox.maxX + 0.2; x += 0.2f) {
                        for (float y = data.boundingBox.minY; y < data.boundingBox.maxY; y += 0.2f) {
                            for (float z = data.boundingBox.minZ; z < data.boundingBox.maxZ + 0.2; z += 0.2f) {
                                WrappedPacketPlayOutWorldParticle packet = new WrappedPacketPlayOutWorldParticle(WrappedEnumParticle.CRIT_MAGIC, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                                packet.sendPacket(e.getPlayer());
                            }
                        }
                    }

                }

                break;
            }
            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket use = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());

                //Bukkit.broadcastMessage(use.getAction().name());
                switch (use.getAction()) {
                    case ATTACK: {
                        if (use.getEntity() instanceof LivingEntity && (ReflectionsUtil.isBukkitVerison("1_7") || !use.getEntity().getType().equals(EntityType.ARMOR_STAND))) {
                            data.lastHitEntity = (LivingEntity) use.getEntity();
                            data.offsetArray = MathUtils.getOffsetFromEntity(e.getPlayer(), data.lastHitEntity);
                            PacketAttackEvent event = new PacketAttackEvent(e.getPlayer(), (LivingEntity) use.getEntity());
                            EventManager.callEvent(event);

                            if (event.isCancelled()) {
                                e.setCancelled(true);
                            }
                        }
                    }
                    case INTERACT: {
                        if (e.getPlayer().getItemInHand().getType().equals(Material.BLAZE_ROD) && e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(Color.Red + "Magic Box Wand") && use.getEntity() instanceof LivingEntity) {
                            BoundingBox box = MiscUtils.getEntityBoundingBox((LivingEntity) use.getEntity());
                            for (float x = box.minX; x < box.maxX + 0.2; x += 0.25f) {
                                for (float y = box.minY; y < box.maxY; y += 0.25f) {
                                    for (float z = box.minZ; z < box.maxZ + 0.2; z += 0.25f) {
                                        WrappedPacketPlayOutWorldParticle packet = new WrappedPacketPlayOutWorldParticle(WrappedEnumParticle.FLAME, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                                        packet.sendPacket(e.getPlayer());
                                    }
                                }
                            }
                            e.getPlayer().sendMessage(box.toString());
                        }
                    }
                }
                break;
            }
            case Packet.Client.WINDOW_CLICK:
                WrappedInWindowClickPacket packet = new WrappedInWindowClickPacket(e.getPacket(), e.getPlayer());

                PacketInvClickEvent event = new PacketInvClickEvent(e.getPlayer(), packet.getAction(), packet.getItem());

                EventManager.callEvent(event);
                callFunkeEvent(event, data);

                data.lastInvClick.reset();
                break;
            case Packet.Client.BLOCK_DIG: {
                WrappedInBlockDigPacket blockDig = new WrappedInBlockDigPacket(e.getPacket(), e.getPlayer());

                switch (blockDig.getAction()) {
                    case START_DESTROY_BLOCK:
                        data.breakingBlock = true;
                        break;
                    case ABORT_DESTROY_BLOCK:
                    case STOP_DESTROY_BLOCK:
                        data.breakingBlock = false;
                        break;
                }
                break;
            }
            case Packet.Client.ARM_ANIMATION:
                EventManager.callEvent(new PacketArmSwingEvent(e.getPlayer(), new WrappedInArmAnimationPacket(e.getPacket(), e.getPlayer()).isPunchingBlock()));

                data.lastArmAnimation.reset();
                break;

            case Packet.Client.TRANSACTION:
                WrappedInTransactionPacket trans = new WrappedInTransactionPacket(e.getPacket(), e.getPlayer());

                if(trans.getId() == 0) {
                    data.lastPing = data.ping;
                    data.ping = (int) MathUtils.elapsed(data.lastServerKeepAlive);

                    TinyProtocolHandler.sendPacket(data.player, new WrappedOutTransaction(0, (short) 69, false).getObject());
                }
                break;
        }

        callFunkeEvent(e, data);
    }

    @EventMethod
    public void onPacketMoveEvent(PacketFunkeMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(player);
        data.generalCancel = player.getAllowFlight()
                || data.riptideTicks > 0
                || player.isDead()
                || player.getGameMode().equals(GameMode.CREATIVE)
                || data.hasLag()
                || data.skippedTicks > 0
                || player.getVehicle() != null
                || data.lastLogin.hasNotPassed(50)
                || data.lastFlyChange.hasNotPassed(50)
                || PlayerUtils.isGliding(event.getPlayer());

        if(data.hasLag()) {
            data.skippedTicks+= 20;
        }

        data.lastTo = event.getTo();

        callFunkeEvent(event, data);
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(event.getPlayer());

        if (data != null) {
            data.lastSneak = System.currentTimeMillis();
            callBukkitEvent(event, data);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player) {
            PlayerData data = Fiona.getInstance().getDataManager().getPlayerData((Player) event.getPlayer());

            if (data != null) {
                callBukkitEvent(event, data);
            }
        }
    }

    @EventMethod
    public void onPacketSwing(PacketArmSwingEvent event) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(event.getPlayer());

        if (data != null) {
            callFunkeEvent(event, data);
        }
    }

    @EventMethod
    public void onPacketAttack(PacketAttackEvent event) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(event.getAttacker());

        if (data != null) {
            //Bukkit.broadcastMessage("attacked");
            callFunkeEvent(event, data);

            data.lastHitEntity = event.getAttacked();
            data.lastAttack.reset();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(event.getPlayer());

        if (data != null) {
            if (event.getPlayer().getAllowFlight() != data.lastAllowedFlight) {
                data.lastFlyChange.reset();
            }
            data.lastAllowedFlight = event.getPlayer().getAllowFlight();
            callBukkitEvent(event, data);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            PlayerData data = Fiona.getInstance().getDataManager().getPlayerData((Player) event.getDamager());

            if (data != null) {

                if (data.cancelType == CheckType.COMBAT) {
                    if (data.cancelTicks-- > 0) {
                        event.setCancelled(true);
                    } else {
                        data.cancelType = null;
                    }
                }
                callBukkitEvent(event, data);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(event.getPlayer());

        if (data != null) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getItem() != null
                        && event.getItem().getType().equals(Material.BLAZE_ROD)
                        && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Color.Red + "Magic Box Wand")) {
                    Block block = event.getClickedBlock();

                    event.getPlayer().sendMessage(block.getType().name() + "'s Data: " + block.getData());
                    for (BoundingBox box : Fiona.getInstance().getBlockBoxManager().getBlockBox().getSpecificBox(block.getLocation())) {
                        for (float x = box.minX; x < box.maxX; x += 0.2f) {
                            for (float y = box.minY; y < box.maxY; y += 0.2f) {
                                for (float z = box.minZ; z < box.maxZ; z += 0.2f) {
                                    WrappedPacketPlayOutWorldParticle packet = new WrappedPacketPlayOutWorldParticle(WrappedEnumParticle.FLAME, true, x, y, z, 0f, 0f, 0f, 0f, 1, null);
                                    packet.sendPacket(event.getPlayer());
                                }
                            }
                        }
                        event.getPlayer().sendMessage(ReflectionsUtil.getVanillaBlock(event.getClickedBlock()).getClass().getSimpleName() + ": " + box.toString());
                    }
                }
            }

            callBukkitEvent(event, data);
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDmaage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            PlayerData data = Fiona.getInstance().getDataManager().getPlayerData((Player) event.getEntity());

            if (data != null) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL
                        || event.getCause() == EntityDamageEvent.DamageCause.MAGIC
                        || event.getCause() == EntityDamageEvent.DamageCause.DROWNING
                        || event.getCause() == EntityDamageEvent.DamageCause.FIRE
                        || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                        || event.getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK
                        || event.getCause() == EntityDamageEvent.DamageCause.POISON) {
                    data.lastUselessDamage.reset();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Fiona.getInstance().profile.start("data:BlockPlaceEvent");
        if (event.isCancelled()) {
            event.getPlayer().setVelocity(new Vector(0, -0.5, 0));
        }
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(event.getPlayer());
        if (data != null) {
            if (data.cancelType == CheckType.WORLD && data.cancelTicks > 0) {
                event.setCancelled(true);
                data.cancelTicks--;
            }

            data.lastBlockPlace.reset();
            callBukkitEvent(event, data);
        }
        Fiona.getInstance().profile.stop("data:BlockPlaceEvent");
    }

    @EventHandler
    public void onProjectile(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            PlayerData data = Fiona.getInstance().getDataManager().getPlayerData((Player) event.getEntity().getShooter());

            if (data != null) callBukkitEvent(event, data);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Fiona.getInstance().profile.start("data:BlockBreakEvent");
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(event.getPlayer());
        if (data != null) {
            callBukkitEvent(event, data);
            if (data.cancelType == CheckType.WORLD && data.cancelTicks > 0) {
                event.setCancelled(true);
                data.cancelTicks--;
            }
        }
        Fiona.getInstance().profile.stop("data:BlockBreakEvent");
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(event.getPlayer());

        if (data != null) {
            if (BlockUtils.isEdible(event.getItem().getType())) {
                data.lastItemConsume = System.currentTimeMillis();
            }
            callBukkitEvent(event, data);
        }
    }

    @EventHandler
    public void entityRegainHealthEvent(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            PlayerData data = Fiona.getInstance().getDataManager().getPlayerData((Player) event.getEntity());

            if (data != null) callBukkitEvent(event, data);
        }
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent e) {
        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(e.getPlayer());

        if (data != null) {
            if (data.lastUselessDamage.hasPassed(2) && data.lastTeleport.hasPassed(1)) {
                data.lastVelocity.reset();
            }
            data.lastVelocityTaken = new Vector(e.getVelocity().getX(), e.getVelocity().getY(), e.getVelocity().getZ());
        }
    }

    private void callBukkitEvent(Event event, PlayerData data) {
        try {
            Fiona.getInstance().executorTwo.execute(() -> {
                if (data.boundingBox != null) {
                    Fiona.getInstance().getCheckManager().getChecks().stream().filter(Check::isEnabled).forEach(check -> check.getDetections().stream().filter(Detection::isEnabled).forEach(detection -> {
                        try {
                            Fiona.getInstance().profile.start("check:" + check.getName() + ":" + detection.getId());
                            detection.onBukkitEvent(event, data);
                            Fiona.getInstance().profile.stop("check:" + check.getName() + ":" + detection.getId());
                        } catch (Exception e) {
                            new BukkitRunnable() {
                                public void run() {
                                    try {
                                        detection.onBukkitEvent(event, data);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.runTask(Fiona.getInstance());
                        }
                    }));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callFunkeEvent(cc.funkemunky.fiona.events.system.Event event, PlayerData data) {
        try {
            if (data.boundingBox != null) {
                Fiona.getInstance().executorOne.execute(() -> Fiona.getInstance().getCheckManager().getChecks().stream().filter(Check::isEnabled).forEach(check -> {
                    check.getDetections().stream().filter(Detection::isEnabled).forEach(detection -> {
                        try {
                            Fiona.getInstance().profile.start("check:" + check.getName() + ":" + detection.getId());
                            detection.onFunkeEvent(event, data);
                            Fiona.getInstance().profile.stop("check:" + check.getName() + ":" + detection.getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}