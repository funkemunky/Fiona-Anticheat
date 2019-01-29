package cc.funkemunky.fiona.detections.combat.autoclicker;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.combat.autoclicker.detections.*;
import cc.funkemunky.fiona.detections.combat.killaura.detections.TypeF1_8;

import java.util.concurrent.TimeUnit;

public class AutoClicker extends Check {

    public AutoClicker() {
        super("AutoClicker", CheckType.COMBAT, true, true, false, false, 50, TimeUnit.MINUTES.toMillis(3), 20);

        addDetection(new TypeA(this, "Type A", true, true));
        addDetection(new TypeB(this, "Type B", true, true));
        addDetection(new TypeC(this, "Type C", true, true));
    }
}
