package cc.funkemunky.fiona.detections.combat.reach.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketAttackEvent;
import cc.funkemunky.fiona.utils.BoundingBox;
import cc.funkemunky.fiona.utils.MiscUtils;
import cc.funkemunky.fiona.utils.PlayerUtils;
import org.bukkit.GameMode;

public class TypeB extends Detection {
    public TypeB(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("pingAccounting", 0.001);
        addConfigValue("boxExpand", 3.1);

        setExperimental(true);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketAttackEvent) {
            PacketAttackEvent e = (PacketAttackEvent) event;

            if (data.player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            BoundingBox entityBox = MiscUtils.getEntityBoundingBox(e.getAttacked());

            double accounting = (double) getConfigValues().get("pingAccounting"), boxExpand = (double) getConfigValues().get("boxExpand");
            float pingAccount = (float) (data.ping * accounting);
            entityBox = entityBox.grow((float) boxExpand + pingAccount, (float) boxExpand + pingAccount, (float) boxExpand + pingAccount);

            entityBox.grow(PlayerUtils.facingOpposite(data.player, e.getAttacked()) ? 0.25f : 0, PlayerUtils.facingOpposite(data.player, e.getAttacked()) ? 0.25f : 0, PlayerUtils.facingOpposite(data.player, e.getAttacked()) ? 0.25f : 0);

            if (e.getAttacked().getMaximumNoDamageTicks() > 16 && data.lastReachAttack.hasPassed(e.getAttacked().getMaximumNoDamageTicks()) && !data.boundingBox.intersectsWithBox(entityBox)) {
                if (data.reachTypeBVerbose.flag(2) || !data.boundingBox.intersectsWithBox(entityBox)) {
                    flag(data, entityBox.getMaximum().distance(data.boundingBox.getMaximum()) + ">-3", 1, true, true);
                }
            }

            debug(data, e.getAttacked().getMaximumNoDamageTicks() + ", " + data.lastReachAttack.getPassed() + ", " + entityBox.getMaximum().lengthSquared());
            data.lastReachAttack.reset();
        }
    }
}
