package cc.funkemunky.fiona.data.logging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Log {
    private UUID uuid;
    private String check, detection;
    private String data;
    private int ping;
    private double tps, vl, reliabilityPercentage;
}
