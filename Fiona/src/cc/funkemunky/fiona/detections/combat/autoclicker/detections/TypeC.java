package cc.funkemunky.fiona.detections.combat.autoclicker.detections;

import cc.funkemunky.fiona.data.PlayerData;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.events.custom.PacketRecieveEvent;
import cc.funkemunky.fiona.utils.MathUtils;
import com.ngxdev.tinyprotocol.api.Packet;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;

public class TypeC extends Detection {
    public TypeC(Check parentCheck, String id, boolean enabled, boolean executable) {
        super(parentCheck, id, enabled, executable);

        addConfigValue("vlToSubtract.regular", 0.25);
        addConfigValue("vlToSubtract.noClicks", 0.35);
        addConfigValue("vlToSubtract.largeAttackDelta", 0.45);
        addConfigValue("vlToSubtract.mediumAttackDelta", 0.25);
        addConfigValue("vlToSubtract.smallAttackDelta", 0.15);
        addConfigValue("vlToAdd", 1D);
        addConfigValue("vlToReset", 0D);
        addConfigValue("minClicks", 1);
        addConfigValue("deltaToCheck", 25);
        addConfigValue("minTimeThreshold", 42);
        addConfigValue("maxTimeThreshold", 55);
        addConfigValue("threshold", 4.0);
        addConfigValue("combatOnly", false);
    }

    @Override
    public void onFunkeEvent(Object event, PlayerData data) {
        if (event instanceof PacketRecieveEvent) {
            PacketRecieveEvent e = (PacketRecieveEvent) event;

            switch (e.getType()) {
                case Packet.Client.POSITION:
                case Packet.Client.LOOK:
                case Packet.Client.POSITION_LOOK:
                case Packet.Client.LEGACY_POSITION:
                case Packet.Client.LEGACY_LOOK:
                case Packet.Client.LEGACY_POSITION_LOOK:
                case Packet.Client.FLYING: {
                    if (data.attack) {
                        long time = MathUtils.elapsed(data.lastDeltaAttack);

                        /*
                        Your time was too low which will end up causing false flags
                        Typically any jitter/butterfly clicker will always click at around ~50ms, usually no less than 43.
                        It is best to check at a safer area instead of going more in the danger zone of less time
                        NOTE: Why data.clicks > 1? You can false this by slowly clicking. That'll help stop the issue.
                         */
                        if (time > (int) getConfigValues().get("minTimeThreshold") && time < (int) getConfigValues().get("maxTimeThreshold") && data.clicks > (int) getConfigValues().get("minClicks")) {
                            /*
                            TODO: Get a better and more safer value, so far hasn't falsed for me and flags just fine, but I am sure it can be falsed.
                             */
                            if ((data.vl += (double) getConfigValues().get("vlToAdd")) > (double) getConfigValues().get("threshold")) {
                                flag(data, "t: b l: " + time, 1, true, true);
                                data.vl = (double) getConfigValues().get("vlToAdd");
                            }
                        } else if (data.vl > 0) {
                            data.vl -= (double) getConfigValues().get("vlToSubtract.regular");
                        }
                        debug(data, "t: " + time);
                        data.attack = false;
                    }
                    data.lastClickerFlying = System.currentTimeMillis();
                    break;
                }
                case Packet.Client.ARM_ANIMATION: {
                    WrappedInArmAnimationPacket packet = new WrappedInArmAnimationPacket(e.getPacket(), e.getPlayer());

                    boolean combat = !(boolean) getConfigValues().get("combatOnly") || data.lastAttack.hasNotPassed(5);
                    if (!packet.isPunchingBlock() && combat) {
                        if (!MathUtils.elapsed(data.lastClickerFlying, (int) getConfigValues().get("deltaToCheck")) /*&& MathUtils.elapsed(data.lastLoopSwing, 70L)*/) { //Commented out until full testing proves it not required.
                            data.lastDeltaAttack = System.currentTimeMillis();
                            data.attack = true;
                        } else if (data.vl > 0) {
                            long time = MathUtils.elapsed(data.lastDeltaAttack);

                            data.vl -= data.clicks < 2 ? (double) getConfigValues().get("vlToSubtract.noClicks") : time > 55 ? (double) getConfigValues().get("vlToSubtract.largeAttackDelta") : time > 35 ? (double) getConfigValues().get("vlToSubtract.mediumAttackDelta") : time <= 25 ? (double) getConfigValues().get("vlToSubtract.mediumAttackDelta") : (double) getConfigValues().get("vlToSubtract.smallAttackDelta");
                        }
                        //data.lastLoopSwing = System.currentTimeMillis();
                    }
                }
            }
        }
    }
}
