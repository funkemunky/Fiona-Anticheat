package cc.funkemunky.fiona.detections.player.velocity;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.player.velocity.detections.TypeA;
import cc.funkemunky.fiona.detections.player.velocity.detections.TypeB;

public class Velocity extends Check {
    public Velocity() {
        super("Velocity", CheckType.PLAYER, true, false, false, false, 4, 1);


        addDetection(new TypeA(this, "Type A", true, false, true));
        addDetection(new TypeB(this, "Type B", false, false));
    }
}
