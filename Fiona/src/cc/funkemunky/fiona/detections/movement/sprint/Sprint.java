package cc.funkemunky.fiona.detections.movement.sprint;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.movement.sprint.detections.TypeA;

import java.util.concurrent.TimeUnit;

public class Sprint extends Check {
    public Sprint() {
        super("Sprint", CheckType.MOVEMENT, true, false, false, false, 30, TimeUnit.MINUTES.toMillis(3), 10);

        addDetection(new TypeA(this, "Type A", true, false));
        //addDetection(new TypeG(this, "TypeG", true, false));
    }
}
