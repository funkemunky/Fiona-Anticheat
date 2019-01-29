package cc.funkemunky.fiona.detections.combat.fastbow;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.combat.fastbow.detections.TypeA;

public class Fastbow
        extends Check {
    public Fastbow() {
        super("Fastbow", CheckType.COMBAT, true, true, false, false, 12, 0);

        addDetection(new TypeA(this, "Type A", true, true));
    }
}

