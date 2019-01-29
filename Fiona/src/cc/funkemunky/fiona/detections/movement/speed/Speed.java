package cc.funkemunky.fiona.detections.movement.speed;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.movement.speed.detections.*;

import java.util.concurrent.TimeUnit;

public class Speed extends Check {
    public Speed() {
        super("Speed", CheckType.MOVEMENT, true, true, false, false, 80, TimeUnit.MINUTES.toMillis(4), 15);

        addDetection(new TypeA(this, "Type A", true, true));
        addDetection(new TypeB(this, "Type B", true, false));
        addDetection(new TypeC(this, "Type C", false, false));
        addDetection(new TypeD(this, "Type D", true, true));
    }
}
