package cc.funkemunky.fiona.utils;

import lombok.Getter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Getter
public class MoveData {
    private Map<Long, FionaLocation> movements;

    public MoveData() {
        movements = new HashMap<>();

    }

    public void addMovement(FionaLocation location) {
        if (movements.size() == 20) {
            movements.remove(movements.keySet().stream().findFirst().orElse(null));
        }
        movements.put(System.currentTimeMillis(), location);
    }

    public FionaLocation getMovement(long time) {
        long closest = movements.keySet().stream()
                .min(Comparator.comparingLong(i -> Math.abs(i - time))).orElse(System.currentTimeMillis());

        return movements.get(closest);
    }
}
