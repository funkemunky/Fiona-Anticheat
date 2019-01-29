package cc.funkemunky.fiona.detections.movement.step;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.movement.step.detections.TypeA;
import cc.funkemunky.fiona.detections.movement.step.detections.TypeB;
import cc.funkemunky.fiona.detections.movement.step.detections.TypeC;

public class Step extends Check {
    public Step() {
        super("Step", CheckType.MOVEMENT, true, false, false, false, 20, 2);

        addDetection(new TypeB(this, "Type B", true, false));
        addDetection(new TypeA(this, "Type A", false, false));
        //addDetection(new TypeC(this, "Type C", false, false));
    }
}
