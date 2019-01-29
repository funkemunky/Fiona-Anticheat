package cc.funkemunky.fiona.detections.combat.aimassist;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.combat.aimassist.detections.*;

import java.util.concurrent.TimeUnit;

public class AimPattern extends Check {

    public AimPattern() {
        super("AimPattern", CheckType.COMBAT, true, true, false, false, 100, TimeUnit.MINUTES.toMillis(5), 50);

        addDetection(new TypeB(this, "Type B", false, false));
        addDetection(new TypeC(this, "Type C", false, false));
        addDetection(new TypeA(this, "Type A", false, false));
        addDetection(new TypeD(this, "Type D", false, false));
    }


}