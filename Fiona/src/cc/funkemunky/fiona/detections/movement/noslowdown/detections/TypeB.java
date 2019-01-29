package cc.funkemunky.fiona.detections.movement.noslowdown.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.events.custom.PacketSendEvent;
import cc.funkemunky.fiona.utils.Color;
import cc.funkemunky.fiona.utils.MathUtils;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutEntityMetadata;

public class TypeB extends Detection {

    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("threshold", 20);
        addConfigValue("resetTime", 500);
        addConfigValue("verboseToAdd", 2);
        setThreshold(20);
    }

    //TODO Test this when home
    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (MathUtils.moved(e.getFrom(), e.getTo())
                    && !data.generalCancel
                    && data.isUsingItem
                    && !data.isVelocityTaken()) {
                double xDelta = data.movement.deltaXZ;

                double threshold = data.onGround ? 0.15 : 0.25;
                //TODO Check how high the reset time can go.
                if (xDelta > threshold) { //Assumed threshold, make sure to actually debug an appropriate speed threshold.
                    if (data.noSlowdownBVerbose.flag(30, 500L, 2)) { //Also an assumed threshold.
                        flag(data, MathUtils.round(xDelta, 3) + ">-" + threshold, 1, true, true);
                    } else {
                        data.noSlowdownBVerbose.deduct();
                    }
                }
                debug(data, xDelta + ", " + data.noSlowdownBVerbose.getVerbose());
            }
        }
    }
}
