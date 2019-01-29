package cc.funkemunky.fiona.detections.combat.killaura;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.combat.killaura.detections.*;

import java.util.concurrent.TimeUnit;

public class KillAura
        extends Check {
    public KillAura() {
        super("KillAura", CheckType.COMBAT, true, true, false, false, 50, TimeUnit.MINUTES.toMillis(4), 5);

        addDetection(new TypeJ(this, "Type J", true, true));
        addDetection(new TypeE(this, "Type E", false, false));
        addDetection(new TypeH(this, "Type H", true, false));
        addDetection(new TypeF1_7(this, "Type F", true, true));
        addDetection(new TypeF1_8(this, "Type F", true, true));
        addDetection(new TypeG(this, "Type G", false, false));
        addDetection(new TypeK(this, "Type K", true, true));
        addDetection(new TypeI(this, "Type I", false, false));
        addDetection(new TypeA(this, "Type A", true, true));
        addDetection(new TypeC(this, "Type C", true, true));
        addDetection(new TypeB(this, "Type B", true, false));
        addDetection(new TypeD(this, "Type D", true, false));
    }
}

