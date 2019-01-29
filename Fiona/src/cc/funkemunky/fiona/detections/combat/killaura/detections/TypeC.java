package cc.funkemunky.fiona.detections.combat.killaura.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketAttackEvent;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.utils.PlayerUtils;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class TypeC extends Detection {
    public TypeC(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketAttackEvent) {
            PacketAttackEvent e = (PacketAttackEvent) event;

            if (!data.generalCancel && e.getAttacked() instanceof Player && data.lastVelocity.hasPassed(5) && data.movement.deltaXZ > getBaseSpeed(data) && data.killauraActionVerbose.flag(14, 1400L)) {
                flag(data, data.movement.deltaXZ + ">-" + getBaseSpeed(data), 1, true, true);
            }

            debug(data, data.killauraActionVerbose.getVerbose() + ", " + data.movement.deltaXZ + "," + getBaseSpeed(data));
        }
    }

    private float getBaseSpeed(PlayerData data) {
        return 0.26f + (PlayerUtils.getPotionEffectLevel(data.player, PotionEffectType.SPEED) * 0.06f) + ((data.player.getWalkSpeed() - 0.2f) * 1.1f);
    }
}
