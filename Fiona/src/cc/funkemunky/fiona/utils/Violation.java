package cc.funkemunky.fiona.utils;

import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Violation {
    private Check check;
    private Map<Detection, Float> specificViolations;
    private float combinedAmount;

    public Violation(Check check) {
        this.check = check;

        combinedAmount = 0;

        specificViolations = new HashMap<>();
    }

    public void addViolation(Detection detection, float amount) {
        float specificAmount = specificViolations.getOrDefault(detection, 0f) + amount;
        specificViolations.put(detection, specificAmount);
        combinedAmount += amount;
    }

    public float getSpecificViolation(Detection detection) {
        return specificViolations.getOrDefault(detection, 0f);
    }

    public void resetViolations() {
        combinedAmount = 0;

        specificViolations.clear();
    }

    public void resetViolations(Detection detection) {
        if (specificViolations.containsKey(detection)) {
            specificViolations.clear();
        }
    }


}
