package cc.funkemunky.fiona.detections.world.scaffold.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import com.ngxdev.tinyprotocol.packet.types.EnumDirection;
import org.bukkit.Location;
import org.bukkit.Material;

public class TypeD extends Detection {

    public TypeD(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketRecieveEvent) {
            PacketRecieveEvent e = (PacketRecieveEvent) event;

            switch (e.getType()) {
                case Packet.Client.BLOCK_PLACE: {
                    WrappedInBlockPlacePacket place = new WrappedInBlockPlacePacket(e.getPacket(), e.getPlayer());


                    if (place.getPosition() != null) {
                        Location blockLoc = new Location(e.getPlayer().getWorld(), place.getPosition().getX(), place.getPosition().getY(), place.getPosition().getZ());
                        if (e.getPlayer().getItemInHand() != null
                                && !e.getPlayer().getItemInHand().getType().equals(Material.AIR)
                                && blockLoc.getBlock().getFace(e.getPlayer().getLocation().getBlock()) != null
                                && MathUtils.getHorizontalDistance(blockLoc, data.player.getLocation()) < 2
                                && data.player.getLocation().getY() > blockLoc.getY()
                                && !place.getFace().equals(EnumDirection.UP)
                                && !place.getFace().equals(EnumDirection.DOWN)
                                && data.player.isOnGround()
                                && data.movement.deltaY == 0) {
                            flag(data, "N/A", 1, true, true);
                        }
                    }
                    debug(data, "Place: " + place.getFace());
                    break;
                }
                case Packet.Client.ARM_ANIMATION: {
                    debug(data, "Swung");
                    break;
                }
            }
        }
    }
}
