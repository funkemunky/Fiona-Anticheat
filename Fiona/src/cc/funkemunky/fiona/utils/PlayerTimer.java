/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.fiona.utils;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.data.PlayerData;

/**
 * A timer based on
 */
public class PlayerTimer {
    public int startTime;
    private PlayerData player;

    public PlayerTimer(PlayerData player) {
        this.player = player;
        this.reset();
    }

    public static boolean hasPassed(long startTime, long toPass) {
        return (System.currentTimeMillis() - startTime) >= toPass;
    }

    public boolean wasReset() {
        return this.startTime == Fiona.getInstance().getCurrentTick();
    }

    public void reset() {
        this.startTime = Fiona.getInstance().getCurrentTick();
    }

    public long getPassed() {
        return Fiona.getInstance().getCurrentTick() - this.startTime;
    }

    public void add(int amount) {
        this.startTime -= amount;
    }

    public boolean hasPassed(long toPass) {
        return this.getPassed() >= toPass;
    }

    public boolean hasNotPassed(long toPass) {
        return this.getPassed() < toPass;
    }

    public boolean hasPassed(long toPass, boolean reset) {
        boolean passed = this.getPassed() >= toPass;
        if (passed && reset) reset();
        return passed;
    }
}