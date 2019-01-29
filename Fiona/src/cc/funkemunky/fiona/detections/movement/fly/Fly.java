package cc.funkemunky.fiona.detections.movement.fly;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.movement.fly.detections.*;

import java.util.concurrent.TimeUnit;

public class Fly extends Check {
    public Fly() {
        super("Fly", CheckType.MOVEMENT, true, true, false, false, 100, TimeUnit.MINUTES.toMillis(2), 50);

        addDetection(new TypeA(this, "Type A", true, false));
        addDetection(new TypeB(this, "Type B", true, true));
        addDetection(new TypeC(this, "Type C", true, true));
        addDetection(new TypeD(this, "Type D", true, true));
        addDetection(new TypeE(this, "Type E", true, true)); //1.9+ only check (elytras).
        addDetection(new TypeF(this, "Type F", true, false));
        addDetection(new TypeG(this, "Type G", true, false));
        addDetection(new TypeH(this, "Type H", true, true));
    }
}
