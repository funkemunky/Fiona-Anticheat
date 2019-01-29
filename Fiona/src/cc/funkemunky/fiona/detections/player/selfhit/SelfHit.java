package cc.funkemunky.fiona.detections.player.selfhit;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.player.selfhit.detections.TypeB;

public class SelfHit extends Check {

    public SelfHit() {
        super("SelfHit", CheckType.PLAYER, true, false, false, false, 8, 1);

        addDetection(new TypeB(this, "Type B", true, true));
    }
}
