package cc.funkemunky.fiona.detections.world.scaffold;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.world.scaffold.detections.TypeA;
import cc.funkemunky.fiona.detections.world.scaffold.detections.TypeB;
import cc.funkemunky.fiona.detections.world.scaffold.detections.TypeC;

public class Scaffold
        extends Check {
    public Scaffold() {
        super("Scaffold", CheckType.WORLD, true, true, false, false, 25, 0);

        addDetection(new TypeB(this, "Type B", true, true));
        addDetection(new TypeA(this, "Type A", true, false));
        addDetection(new TypeC(this, "Type C", false, false));
        //addDetection(new TypeC(this, "TypeC", true, false));
    }
}

