package cc.funkemunky.fiona.detections;

import cc.funkemunky.fiona.detections.combat.aimassist.AimPattern;
import cc.funkemunky.fiona.detections.combat.autoclicker.AutoClicker;
import cc.funkemunky.fiona.detections.combat.criticals.Criticals;
import cc.funkemunky.fiona.detections.combat.fastbow.Fastbow;
import cc.funkemunky.fiona.detections.combat.killaura.KillAura;
import cc.funkemunky.fiona.detections.combat.reach.Reach;
import cc.funkemunky.fiona.detections.movement.fly.Fly;
import cc.funkemunky.fiona.detections.movement.invalidmotion.InvalidMotion;
import cc.funkemunky.fiona.detections.movement.jesus.Jesus;
import cc.funkemunky.fiona.detections.movement.nofall.NoFall;
import cc.funkemunky.fiona.detections.movement.noslowdown.NoSlowdown;
import cc.funkemunky.fiona.detections.movement.speed.Speed;
import cc.funkemunky.fiona.detections.movement.sprint.Sprint;
import cc.funkemunky.fiona.detections.movement.step.Step;
import cc.funkemunky.fiona.detections.player.badpackets.BadPackets;
import cc.funkemunky.fiona.detections.player.inventory.Inventory;
import cc.funkemunky.fiona.detections.player.regen.Regen;
import cc.funkemunky.fiona.detections.player.selfhit.SelfHit;
import cc.funkemunky.fiona.detections.player.velocity.Velocity;
import cc.funkemunky.fiona.detections.world.block.Block;
import cc.funkemunky.fiona.detections.world.hand.Hand;
import cc.funkemunky.fiona.detections.world.scaffold.Scaffold;
import cc.funkemunky.fiona.utils.Violation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CheckManager {
    private final List<Check> checks;
    public Map<UUID, List<Violation>> violations;

    public CheckManager() {
        checks = new CopyOnWriteArrayList<>();
        violations = new ConcurrentHashMap<>();
        initializeDetections();
    }

    public void initializeDetections() {
        addCheck(new KillAura());
        addCheck(new Reach());
        addCheck(new Speed());
        addCheck(new Fly());
        addCheck(new Step());
        addCheck(new Criticals());
        addCheck(new Fastbow());
        addCheck(new NoFall());
        addCheck(new BadPackets());
        addCheck(new Regen());
        addCheck(new Scaffold());
        addCheck(new NoSlowdown());
        addCheck(new AutoClicker());
        addCheck(new AimPattern());
        addCheck(new Jesus());
        addCheck(new Sprint());
        addCheck(new SelfHit());
        addCheck(new Inventory());
        addCheck(new Hand());
        addCheck(new Velocity());
        addCheck(new Block());
        addCheck(new InvalidMotion());
    }

    public List<Check> getChecks() {
        return checks;
    }

    public Check getCheckByName(String name) {
        for (Check check : checks) {
            if (check.getName().equalsIgnoreCase(name)) {
                return check;
            }
        }
        return null;
    }

    public boolean isCheck(String name) {
        return getChecks().stream().anyMatch(check -> check.getName().equalsIgnoreCase(name));
    }

    public List<Detection> getDetections(Check check) {
        return check.getDetections();
    }

    private void addCheck(Check check) {
        checks.add(check);
    }

    public void removeCheck(Check check) {
        checks.remove(check);
    }

    public void unregisterAll() {
        checks.clear();
    }
}

