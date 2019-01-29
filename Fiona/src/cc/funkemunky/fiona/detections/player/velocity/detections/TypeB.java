package cc.funkemunky.fiona.detections.player.velocity.detections;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.events.custom.PacketSendEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutVelocityPacket;

public class TypeB extends Detection {

    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (!data.blocksOnTop
                    && data.fromOnGround //Checking if the player was on ground to prevent false positives.
                    && !data.blocksAround
                    && MathUtils.moved(e.getFrom(), e.getTo())
                    && MathUtils.playerMoved(e.getFrom(), e.getTo())) {

                double deltaX = MathUtils.getDelta(data.velocityX, e.getTo().getX() - e.getFrom().getX()), deltaZ = MathUtils.getDelta(data.velocityZ, e.getTo().getZ() - e.getFrom().getZ());

                if (data.velocityX > 0 || data.velocityZ > 0) {
                    if ((deltaX > 0.1f || deltaZ > 0.1f)
                            && !data.hasLag()) {
                        if (data.velHorzVerbose++ > 1 || data.movement.deltaXZ == 0) {
                            flag(data, (e.getTo().getX() - e.getFrom().getX()) + "<-" + data.velocityX + "-" + data.velocityZ + "->" + (e.getTo().getZ() - e.getFrom().getZ()), 1, true, true);
                        }
                    } else {
                        data.velHorzVerbose = 0;
                    }
                    debug(data, data.velHorzVerbose + ": (" + deltaX + ", " + deltaZ + "); (" + data.velocityX + ", " + data.velocityZ + ")");
                    data.velocityX = data.velocityZ = 0;
                }
            }
        } else if (event instanceof PacketRecieveEvent) {
            PacketRecieveEvent e = (PacketRecieveEvent) event;

            if (e.getType().equals(Packet.Client.FLYING)) {
                if ((data.velocityX > 0 || data.velocityZ > 0) && data.lastVelocityApplied.hasPassed(5)) {
                    if(data.skippedTicks == 0) {
                        flag(data, "0<-" + data.velocityX + "-" + data.velocityZ + "->0", 1, true, true);
                    }
                    data.velocityX = data.velocityZ = 0;
                }
            }
        } else if (event instanceof PacketSendEvent) {
            PacketSendEvent e = (PacketSendEvent) event;

            if (e.getType().equals(Packet.Server.ENTITY_VELOCITY)) {
                WrappedOutVelocityPacket velocity = new WrappedOutVelocityPacket(e.getPacket(), e.getPlayer());


                if (data.player.isOnGround()
                        && velocity.getId() == Fiona.getInstance().getBlockBoxManager().getBlockBox().getTrackerId(e.getPlayer())) {
                    data.velocityX = velocity.getX();
                    data.velocityZ = velocity.getZ();
                    data.lastVelocityApplied.reset();
                }
            }
        }
    }
}
