package com.ngxdev.tinyprotocol.api;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.events.custom.PacketSendEvent;
import cc.funkemunky.fiona.events.system.EventManager;
import cc.funkemunky.fiona.utils.MathUtils;
import io.netty.channel.Channel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TinyProtocolHandler {
    @Getter
    private static AbstractTinyProtocol instance;
    private static String chigga = "%%__USER__%%";

    public TinyProtocolHandler() {
        TinyProtocolHandler self = this;
        instance = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8) ? new TinyProtocol1_7(Fiona.getInstance()) {
            @Override
            public Object onPacketOutAsync(Player receiver, net.minecraft.util.io.netty.channel.Channel channel, Object packet) {
                return self.onPacketOutAsync(receiver, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, net.minecraft.util.io.netty.channel.Channel channel, Object packet) {
                return self.onPacketInAsync(sender, packet);
            }
        } : new TinyProtocol1_8(Fiona.getInstance()) {
            @Override
            public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
                return self.onPacketOutAsync(receiver, packet);
            }

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                return self.onPacketInAsync(sender, packet);
            }
        };
        chigga = "%%__USER__%%";
    }

    // Purely for making the code cleaner
    public static void sendPacket(Player player, Object packet) {
        instance.sendPacket(player, packet);
    }

    public static int getProtocolVersion(Player player) {
        chigga = "%%__USER__%%";
        return instance.getProtocolVersion(player);
    }

    public Object onPacketOutAsync(Player sender, Object packet) {
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1);

        PacketSendEvent event = new PacketSendEvent(sender, packet, packetName);
        EventManager.callEvent(event);

        if(packetName.contains("Position")) {
            PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(sender);

            if(data != null) {
                data.lastTeleport.reset();
            }
        }

        return !event.isCancelled() ? event.getPacket() : null;
    }

    public Object onPacketInAsync(Player sender, Object packet) {
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1);

        //Converting the later packets into their equivalent, more understandable legacy types.
        packetName = packetName.replaceAll("PacketPlayInUseItem", "PacketPlayInBlockPlace");


        PlayerData data = Fiona.getInstance().getDataManager().getPlayerData(sender);

        if (data == null) {
            return packet;
        }

        switch (packetName) {
            case Packet.Client.POSITION:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.LEGACY_POSITION:
            case Packet.Client.LEGACY_LOOK:
            case Packet.Client.LEGACY_POSITION_LOOK:
            case Packet.Client.FLYING: {
                long elapsed = MathUtils.elapsed(data.lastFlyingPacket);
                int skipped = (int) elapsed / 50;

                if (MathUtils.elapsed(data.lastFlyingPacket, 100)
                        && data.lastFlyingPacket > 0) {
                    //data.skippedTicks += skipped;

                    if (data.integDebug)
                        //Bukkit.broadcastMessage("Skipped: " + skipped + ", " + data.skippedTicks + " LastCancel: (" + data.lastPacketCancel.getPassed() + ")");
                        data.lastPacketCancel.reset();

                } else if (data.skippedTicks > 0) {
                    data.skippedTicks--;
                }
                if (elapsed < 2) {
                    data.lagTick = true;
                } else {
                    if (data.packetCancelTicks > 0) {
                        data.packetCancelTicks--;
                    }
                    data.lagTick = false;
                }
                data.lastFlyingPacketDif = elapsed;
                data.lastFlyingPacket = System.currentTimeMillis();
                break;
            }
        }

        PacketRecieveEvent event = new PacketRecieveEvent(sender, packet, packetName);

        EventManager.callEvent(event);

        return !event.isCancelled() ? packet : null;
    }
}

