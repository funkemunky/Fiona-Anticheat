package cc.funkemunky.fiona.detections.movement.jesus;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.movement.jesus.detections.TypeA;
import cc.funkemunky.fiona.detections.movement.jesus.detections.TypeB;

public class Jesus extends Check {

    public Jesus() {
        super("Jesus", CheckType.MOVEMENT, true, true, false, false, 50, 20);

        addDetection(new TypeB(this, "Type B", true, true));
        addDetection(new TypeA(this, "Type A", true, true));
    }


}
