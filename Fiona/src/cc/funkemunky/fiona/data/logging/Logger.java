package cc.funkemunky.fiona.data.logging;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Detection;

import java.util.List;
import java.util.UUID;

public interface Logger {

    void addLog(Detection detection, double vl, String info, PlayerData data);

    List<Log> getLogs(UUID uuid);
}
