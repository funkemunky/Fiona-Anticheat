package cc.funkemunky.fiona.detections.movement.noslowdown;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.movement.noslowdown.detections.TypeA;
import cc.funkemunky.fiona.detections.movement.noslowdown.detections.TypeB;
import cc.funkemunky.fiona.detections.movement.noslowdown.detections.TypeC;

import java.util.concurrent.TimeUnit;

public class NoSlowdown extends Check {
    public NoSlowdown() {
        super("NoSlowdown", CheckType.MOVEMENT, true, true, false, false, 50, TimeUnit.SECONDS.toMillis(180), 30);

        addDetection(new TypeA(this, "Type A", true, true));
        addDetection(new TypeB(this, "Type B", true, true));
        addDetection(new TypeC(this, "Type C", true, true));
    }
}
