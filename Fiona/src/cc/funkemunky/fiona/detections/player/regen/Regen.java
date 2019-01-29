package cc.funkemunky.fiona.detections.player.regen;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.player.regen.detections.TypeA;

public class Regen
        extends Check {
    public Regen() {
        super("Regen", CheckType.PLAYER, true, true, false, false, 5, 0);

        addDetection(new TypeA(this, "Type A", true, true));
    }
}

