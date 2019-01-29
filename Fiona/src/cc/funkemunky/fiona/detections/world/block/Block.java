package cc.funkemunky.fiona.detections.world.block;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.world.block.detections.FastBreak1_8;
import cc.funkemunky.fiona.detections.world.block.detections.FastBreak1_9;

public class Block extends Check {
    public Block() {
        super("Block", CheckType.WORLD, false, false, false, false, 120, 0);

        addDetection(new FastBreak1_9(this, "FastBreak", true, false));
        addDetection(new FastBreak1_8(this, "FastBreak", true, false));
    }
}
