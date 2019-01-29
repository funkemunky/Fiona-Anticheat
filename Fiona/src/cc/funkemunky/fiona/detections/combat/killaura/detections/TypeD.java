package cc.funkemunky.fiona.detections.combat.killaura.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketAttackEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

public class TypeD extends Detection {
    public TypeD(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        setExperimental(true);
    }

    //TODO Check if a move event is required, detects, and doesn't false flag.
    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketAttackEvent) {
            PacketAttackEvent e = (PacketAttackEvent) event;

            double offset = data.offsetArray[0], average = data.typeOAverge.getAverage();

            if (average < 5.0 && (data.movement.deltaXZ > getBaseSpeed(data) || data.yawDelta > 2.0) && data.kaOVerbose.flag(50, 600L)) {
                flag(data, average + "<-4.0->" + data.kaOVerbose.getVerbose(), 1, true, true);
            }

            debug(data, MathUtils.round(offset, 5) + ", " + MathUtils.round(average, 5) + ", " + data.kaOVerbose.getVerbose());
            data.typeOAverge.add(offset, System.currentTimeMillis());
        }
    }

    private float getBaseSpeed(PlayerData data) {
        return 0.2f + (PlayerUtils.getPotionEffectLevel(data.player, PotionEffectType.SPEED) * 0.06f) + ((data.player.getWalkSpeed() - 0.2f) * 0.75f);
    }
}
