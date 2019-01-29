package cc.funkemunky.fiona.detections.player.badpackets;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.player.badpackets.detections.*;

import java.util.concurrent.TimeUnit;

public class BadPackets
        extends Check {
    public BadPackets() {
        super("BadPackets", CheckType.PLAYER, true, true, false, false, 65, TimeUnit.MINUTES.toMillis(2), 40);

        addDetection(new TypeD(this, "Type D", true, true));
        addDetection(new TypeA(this, "Type A", true, false));
        addDetection(new TypeC(this, "Type C", false, false));
        addDetection(new TypeB(this, "Type B", true, true));
        addDetection(new TypeE(this, "Type E", true, true));
        //addDetection(new TypeF(this, "Type F", false, false));
        addDetection(new TypeH(this, "Type H", true, true));
        //addDetection(new TypeI(this, "Type I", true, false));
    }
}

