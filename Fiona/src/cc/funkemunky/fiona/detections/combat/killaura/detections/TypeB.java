package cc.funkemunky.fiona.detections.combat.killaura.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketArmSwingEvent;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.utils.Color;
import com.ngxdev.tinyprotocol.api.Packet;

public class TypeB extends Detection {
    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketRecieveEvent) {
            PacketRecieveEvent e = (PacketRecieveEvent) event;

            switch (e.getType()) {
                case Packet.Client.POSITION_LOOK:
                case Packet.Client.LOOK:
                case Packet.Client.LEGACY_LOOK:
                case Packet.Client.LEGACY_POSITION_LOOK:
                    if (data.lastClick.hasNotPassed(6)) {
                        if (data.lastClick.getPassed() == data.lastTwo) {
                            if (data.killauraLVerbose.flag(100, 400L)) {
                                flag(data, "t: " + data.killauraLVerbose.getVerbose(), 1, true, true);
                            }
                            debug(data, Color.Green + "Flagged: " + data.killauraLVerbose.getVerbose());
                        }
                        debug(data, "One: " + data.lastClick.getPassed());
                    }
                    break;
            }
        } else if (event instanceof PacketArmSwingEvent) {
            PacketArmSwingEvent e = (PacketArmSwingEvent) event;

            if(e.isLookingAtBlock()) return;
            debug(data, "Two: " + data.lastClick.getPassed());
            data.lastTwo = (int) data.lastClick.getPassed();
            data.lastClick.reset();
        }
    }
}
