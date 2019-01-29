package cc.funkemunky.fiona.detections.combat.killaura.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketAttackEvent;
import cc.funkemunky.fiona.utils.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.List;

public class TypeE extends Detection {

    public TypeE(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("violationsToFlag", 15);
        setThreshold((int) getConfigValues().get("violationsToFlag"));

        setExperimental(true);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketAttackEvent) {
            PacketAttackEvent e = (PacketAttackEvent) event;

            cc.funkemunky.fiona.utils.math.RayTrace trace = new cc.funkemunky.fiona.utils.math.RayTrace(e.getAttacker().getEyeLocation().toVector(), e.getAttacker().getEyeLocation().getDirection());

            List<Vector> vector = trace.traverse(e.getAttacked().getLocation().distance(e.getAttacker().getLocation()) / 1.2, 0.2);

            vector.forEach(vec -> {
                Block block = vec.toLocation(data.player.getWorld()).getBlock();

                if (BlockUtils.isSolid(block)
                        && !BlockUtils.isStair(block)
                        && BlockUtils.getBlockBoundingBox(vec.toLocation(data.player.getWorld()).getBlock()).get(0).intersectsWithBox(vec)
                        && data.kaRTVerbose.flag(5, 650L)) {
                    flag(data, block.getType().name() + "", 1, false, true);
                    return;
                }
                /*WrappedPacketPlayOutWorldParticle particle = new WrappedPacketPlayOutWorldParticle("FLAME", true, (float) vec.getX(), (float) vec.getY(), (float) vec.getZ(), 0f, 0f, 0f, 0f, 1);

                particle.sendPacket(data.player);*/
            });
        }
    }
}