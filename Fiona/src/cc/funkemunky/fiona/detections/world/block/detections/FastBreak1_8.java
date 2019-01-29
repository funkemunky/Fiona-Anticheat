package cc.funkemunky.fiona.detections.world.block.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketArmSwingEvent;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;
import cc.funkemunky.fiona.utils.ReflectionsUtil;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

public class FastBreak1_8 extends Detection {
    public FastBreak1_8(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);
        setVersionMaxmimum(ProtocolVersion.V1_8_9);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketRecieveEvent) {
            PacketRecieveEvent e = (PacketRecieveEvent) event;

            //TODO Test to see if the changes in WrappedInBlockDigPacket now allow it to work on 1.9.
            //There is no difference between the reflection used in this class between 1.8 and 1.9+.
            //Only in the WrappedInBlockDigPacket wuth the EnumPlayerDigType is there a minor difference (1.9+ has an extra field at the end).
            if (e.getType().equals(Packet.Client.BLOCK_DIG)) {
                WrappedInBlockDigPacket blockDig = new WrappedInBlockDigPacket(e.getPacket(), e.getPlayer());

                Block block = new Location(data.player.getWorld(), blockDig.getPosition().getX(), blockDig.getPosition().getY(), blockDig.getPosition().getZ()).getBlock();

                data.destroySpeed = e.getPlayer().getItemInHand() == null || e.getPlayer().getItemInHand().getType().equals(Material.AIR)
                        ? 1.0
                        : ReflectionsUtil.getDestroySpeed(block, e.getPlayer());
                data.blockDura = ReflectionsUtil.getBlockDurability(block);

                debug(data, blockDig.getAction().name());
                switch (blockDig.getAction()) {
                    case START_DESTROY_BLOCK:

                        data.canDestroy = ReflectionsUtil.canDestroyBlock(data.player, block) || ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9);
                        data.started = true;
                        data.breakTime = System.currentTimeMillis();
                        debug(data, "started");
                        break;
                    case STOP_DESTROY_BLOCK:

                        double destroySpeed = data.destroySpeed;
                        //Taken from vanilla code in 1.8 client.
                        if (e.getPlayer().hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                            destroySpeed *= 1.0 + (PlayerUtils.getPotionEffectLevel(e.getPlayer(), PotionEffectType.FAST_DIGGING) + 1) * 0.2f;
                        } else if (e.getPlayer().hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
                            float f1 = 1.0F;

                            switch (PlayerUtils.getPotionEffectLevel(e.getPlayer(), PotionEffectType.SLOW_DIGGING)) {
                                case 1:
                                    f1 = 0.3F;
                                    break;

                                case 2:
                                    f1 = 0.09F;
                                    break;

                                case 3:
                                    f1 = 0.0027F;
                                    break;

                                case 4:
                                default:
                                    f1 = 8.1E-4F;
                            }

                            destroySpeed *= f1;
                        }

                        if (!e.getPlayer().isOnGround()) {
                            destroySpeed /= 5f;
                        }

                        if (e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getEnchantments().keySet().contains(Enchantment.DIG_SPEED)) {
                            int i = e.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.DIG_SPEED);
                            destroySpeed += (float) (i * i + 1);
                        }

                        //End 1.8 skid.

                        double delta;
                        if (Math.abs(delta = (1 / ((destroySpeed / data.blockDura) / (!data.canDestroy ? 100 : 30)) * 50) - MathUtils.elapsed(data.breakTime)) > 300) {
                            flag(data, data.damage + "<-1.0" + "; " + delta + ">-300", 1, true, true);
                        }

                        debug(data, (1 / ((destroySpeed / data.blockDura) / (!data.canDestroy ? 100 : 30)) * 50) + ", " + delta + ", " + data.canDestroy);

                        data.damage = data.destroySpeed = 0;
                        data.started = data.canDestroy = false;
                        break;
                    case ABORT_DESTROY_BLOCK: {
                        data.damage = data.destroySpeed = 0;
                        data.started = data.canDestroy = false;
                    }
                }
            }
        } else if (event instanceof PacketArmSwingEvent) {
            PacketArmSwingEvent e = (PacketArmSwingEvent) event;

            if (data.started && e.isLookingAtBlock()) {
                data.damage += ((data.destroySpeed / data.blockDura) / (!data.canDestroy ? 100 : 30));
                debug(data, data.damage + " damage. " + data.canDestroy);
            }
        }
    }
}
