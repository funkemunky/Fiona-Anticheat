package cc.funkemunky.fiona.detections.combat.reach;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.combat.criticals.detections.TypeC;
import cc.funkemunky.fiona.detections.combat.reach.detections.TypeA;
import cc.funkemunky.fiona.detections.combat.reach.detections.TypeB;

public class Reach
        extends Check {
    public Reach() {
        super("Reach", CheckType.COMBAT, true, false, false, false, 20, 7);

        addDetection(new TypeA(this, "Type A", true, true));
        addDetection(new TypeB(this, "Type B", false, false));
    }
}

