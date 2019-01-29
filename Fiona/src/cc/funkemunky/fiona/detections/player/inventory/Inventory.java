package cc.funkemunky.fiona.detections.player.inventory;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.player.inventory.detections.TypeA;
import cc.funkemunky.fiona.detections.player.inventory.detections.TypeB;

public class Inventory extends Check {
    public Inventory() {
        super("Inventory", CheckType.PLAYER, true, true, false, false, 0, 1);

        addDetection(new TypeB(this, "Type B", true, true));
        addDetection(new TypeA(this, "Type A", true, false));
    }


}
