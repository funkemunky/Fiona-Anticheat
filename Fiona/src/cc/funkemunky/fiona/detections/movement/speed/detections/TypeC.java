package cc.funkemunky.fiona.detections.movement.speed.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketFunkeMoveEvent;
import cc.funkemunky.fiona.utils.MathUtils;

public class TypeC extends Detection {
    public TypeC(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);
        /* Configurable addition points per check */
        addConfigValue("additionA", 2);
        addConfigValue("additionB", 3);
        addConfigValue("additionC", 2);
        addConfigValue("additionD", 1);

        /* Configurable threshold before flagging */
        addConfigValue("threshold", 40);

        setExperimental(true);
        addConfigValue("violationToFlag", 30);
        setThreshold((int) getConfigValues().get("violationToFlag"));
    }

    //TODO Test with knockback and in combat.
    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketFunkeMoveEvent) {
            PacketFunkeMoveEvent e = (PacketFunkeMoveEvent) event;

            if (data.generalCancel || !MathUtils.moved(e.getFrom(), e.getTo())
                    || !MathUtils.playerMoved(e.getFrom(), e.getTo())
                    || data.lastTeleport.hasNotPassed(10)) {
                return;
            }


            if(!data.isVelocityTaken()) {
                if (MathUtils.getDelta(data.movement.deltaY, data.movement.lastDeltaY) > 0.2 && !data.fromOnGround && data.halfBlockTicks == 0) { //Checks if the player's yDelta is unnaturally large.
                    flagCombined(data, "a", (int) getConfigValues().get("additionA"));
                    debug(data, "Added A (" + data.dynamicVerbose.getVerbose() + "): " + MathUtils.getDelta(data.movement.deltaY, data.movement.lastDeltaY));
                } else if (data.movement.deltaXZ < data.movement.lastDeltaXZ && data.fromOnGround && !data.onGround && data.halfBlockTicks == 0) { //Checks if the player's jump y is less than the previous.
                    flagCombined(data, "b", (int) getConfigValues().get("additionB"));
                    debug(data, "Added B (" + data.dynamicVerbose.getVerbose() + ")");
                } else if (MathUtils.getDelta(data.movement.deltaXZ, data.movement.lastDeltaXZ) > 0.03 && !data.isVelocityTaken() && data.airTicks > 5) { //Checks if the player's air deceleration is impossibly fast.
                    flagCombined(data, "c", (int) getConfigValues().get("additionC"));
                    debug(data, "Added C (" + data.dynamicVerbose.getVerbose() + "): " + MathUtils.getDelta(data.movement.deltaXZ, data.movement.lastDeltaXZ));
                } else { //If nothing flags, deduct 1 verbose point.
                    data.dynamicVerbose.deduct();
                }
            }
            debug(data, data.movement.deltaY + "");
        }
    }

    /* Method for checks to use to addValue their verbose points */
    private void flagCombined(PlayerData data, String type, int addition) {
        if (data.dynamicVerbose.flagB((int) getConfigValues().get("threshold"), addition)) {
            flag(data, "t: " + type, 1, true, true);
            data.dynamicVerbose.setVerbose(0);
        }
    }
}
