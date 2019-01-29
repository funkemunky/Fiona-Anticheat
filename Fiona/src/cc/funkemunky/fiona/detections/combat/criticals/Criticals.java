package cc.funkemunky.fiona.detections.combat.criticals;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.combat.criticals.detections.TypeA;
import cc.funkemunky.fiona.detections.combat.criticals.detections.TypeB;
import cc.funkemunky.fiona.detections.combat.criticals.detections.TypeC;

public class Criticals
        extends Check {


    public Criticals() {
        super("Criticals", CheckType.COMBAT, true, true, false, false, 20, 7);

        addDetection(new TypeA(this, "Type A", true, true));
        addDetection(new TypeB(this, "Type B", true, true));
        addDetection(new TypeC(this, "Type C", true, false));
    }
}

