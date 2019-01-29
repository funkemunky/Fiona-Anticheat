package cc.funkemunky.fiona.detections.world.hand;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.world.hand.detections.TypeA;
import cc.funkemunky.fiona.detections.world.hand.detections.TypeB;

public class Hand extends Check {
    public Hand() {
        super("Hand", CheckType.WORLD, true, true, false, true, 30, 0);

        //addDetection(new TypeA(this, "TypeA", true, false));
        addDetection(new TypeA(this, "Type A", true, true));
        addDetection(new TypeB(this, "Type B", false, false));
    }


}
