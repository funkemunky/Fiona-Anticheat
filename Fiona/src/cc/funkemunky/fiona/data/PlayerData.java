package cc.funkemunky.fiona.data;

import cc.funkemunky.fiona.Fiona;
import cc.funkemunky.fiona.detections.Check;
import cc.funkemunky.fiona.detections.CheckType;
import cc.funkemunky.fiona.detections.Detection;
import cc.funkemunky.fiona.utils.*;
import cc.funkemunky.fiona.utils.math.*;
import com.google.common.collect.Lists;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class PlayerData {

    public final Object object = new Object();
    /**
     * Common Data
     **/
    public Player player;
    public boolean onSlimeBefore, onIce, generalCancel, inWeb, onHalfBlock, alerts, isBeingCancelled,
            onClimbable, onGround, fromOnGround, inLiquid, pistonsNear, blocksOnTop, boxDebugEnabled = false, onGroundFive,
            checkDebugEnabled, isUsingItem, lastAllowedFlight, integDebug, lagTick, usingOptifine, collidedHorizontally, blocksAround,
            onSoulSand, onSoulSandIce, breakingBlock;
    public long lastFlyingPacket, lastFlyingPacketDif,
            lastServerKeepAlive = 0, lastItemConsume, lastSneak;
    public int groundTicks, airTicks, webTicks, slimeTicks, blockTicks, climbTicks, cancelTicks, liquidTicks = 0, ping,
            skippedTicks = 0, riptideTicks, clicks, serverPosTicks, halfBlockTicks, packetCancelTicks, iceTicks, soulSandTicks,
            currentTick = 0, lastPing = 0, serverGroundTicks, serverAirTicks, optifineTicks;
    public CheckType cancelType;
    public double[] offsetArray;
    public Location setbackLocation;
    public LivingEntity lastHitEntity;
    public float lastRepeatYawDelta, reliabilityPercentage = 100, reliabilityPoints = 0, pitchMovement,
            yawMovement, lastYawMovement, lastPitchMovement, yawDelta, pitchDelta, lastYawDelta, lastPitchDelta;
    public Detection debugDetection;
    public BoundingBox boundingBox, fromBoundingBox;
    public BoxDebugType boxDebugType;
    public MoveData moveData;
    public FionaLocation lastTo;
    public Vector lastVelocityTaken = new Vector(0, 0, 0);
    public MCSmooth yawSmooth = new MCSmooth(), pitchSmooth = new MCSmooth();
    public PlayerTimer lastFlag = new PlayerTimer(this), lastAttack = new PlayerTimer(this), lastOnGround = new PlayerTimer(this),
            lastVelocity = new PlayerTimer(this), lastLogin = new PlayerTimer(this), lastUseItem = new PlayerTimer(this),
            lastArmAnimation = new PlayerTimer(this), lastBlockPlace = new PlayerTimer(this), lastPacketCancel = new PlayerTimer(this),
            lastTeleport = new PlayerTimer(PlayerData.this), lastUselessDamage = new PlayerTimer(PlayerData.this), lastFlyChange = new PlayerTimer(this);
    public List<FionaLocation> pastLocation = Lists.newArrayList();

    /**
     * Individual Check Data
     **/

    public int speedTicks, diffTicks;

    //Speed
    public float threshold;
    //AimPattern
    public int aaStableVerbose, aaHVerbose;
    public float lastDeltaYaw;
    public List<Float> pitchMatch = new ArrayList<>();
    //FastBow
    public int fastbowVerbose;
    public PlayerTimer lastBow = new PlayerTimer(this);
    //Hand
    public long lastPlacePacket;
    public PlayerTimer lastBlock = new PlayerTimer(this);
    //Block
    public boolean started = false, canDestroy;
    public double destroySpeed = 0, blockDura = 0, damage = 0;
    public long breakTime;
    //Velocity
    public int velHorzVerbose, velVertVerbose;
    public PlayerTimer lastVelocityApplied = new PlayerTimer(this);
    public double velocityY, velocityX, velocityZ;
    //Inventory
    public long lastElapsed, lastInventoryCheckClick;
    public PlayerTimer lastInvClick = new PlayerTimer(this);
    //Reach
    public PlayerTimer lastReachAttack = new PlayerTimer(this);
    //NoFall
    public double lastFallDistance;
    public boolean isFDCanceled, cancelEvent;
    public long lastNoFallCancel;
    //AutoClicker
    public long lastArmSwing, lastHeurSwing, lastSwingDelta, lastClickerFlying, lastTypeEClick, typeGTimestamp;
    public int acEVerbose, typeGVl, ticks, cps, typeGVl2;
    public double lastHeurRange, typeGLastDelta;
    public final RollingAverageDouble cpsAverage = new RollingAverageDouble(5, 0);
    public PlayerTimer cpsResetTime = new PlayerTimer(this);
    public List<Double> swings = Lists.newArrayList();
    //Scaffold
    public Block lastTowerBlock;
    public float beforeYaw;
    public long lastVerticalBlockPlace, lastHorizontalBlockPlace, lastIllegalFlying;
    //Motion
    public int motionThreshold;
    //Regen
    public long lastHealthRegain;
    //BadPackets
    public long sentTrans, timerStart, lastTimer;
    public boolean sentServerAbilities, lastIsFlying;
    public GameMode lastGamemode;
    public int invalidFallVerbose, sneakTicks, pingspoofVerbose, timerTicks, bpAVerbose, bpIVerbose;
    public PlayerTimer sneakTime = new PlayerTimer(this);
    public StatisticalAnalysis statistics = new StatisticalAnalysis(20);
    //NoSlowdown
    public long blockDig;
    //Killaura
    public long lastDeltaAttack, lastFlyingA;
    public boolean attack, hasSwung;
    public double vl, lastAuraRange;
    public int lastTwo;
    public PlayerTimer lastClick = new PlayerTimer(this);
    public RollingAverage typeOAverge = new RollingAverage(50);
    public List<Double> offsets = Lists.newArrayList();
    //Fly
    public int collisionTicks, flyTypeGVerbose;
    public double motionY;
    public float stepTotalYDist, nextY, lastHDeltaXZ, collidedYDist;
    //Autoclicker
    public final DynamicRollingAverage avgSpeed = new DynamicRollingAverage(14), avgDiff = new DynamicRollingAverage(9);
    public final StatisticalAnalysis speedAnalysis = new StatisticalAnalysis(9);
    public final Deque<Double> devDeque = new LinkedList<>();
    public long lastArmSwingTypeF = System.currentTimeMillis();
    public int lastMode;
    public boolean didDeduct;
    public List<Double> values = new ArrayList<>();
    public DynamicRollingAverage acTypeD = new DynamicRollingAverage(20), acTypeF = new DynamicRollingAverage(20);

    //Verbose
    public Verbose ladderVerbose = new Verbose(this), flyAVerbose = new Verbose(this), speedTypeDVerbose = new Verbose(this),
            limitVerbose = new Verbose(this), scaffoldSpeedVerbose = new Verbose(this), kaOVerbose = new Verbose(this),
            noSlowdownBVerbose = new Verbose(this), noSlowdownAVerbose = new Verbose(this), flyTypeFVerbose = new Verbose(this),
            criticalsVerbose = new Verbose(this), regenVerbose = new Verbose(this), flyTypeEVerbose = new Verbose(this),
            aaRepeatVerbose = new Verbose(this), noFallBVerbose = new Verbose(this), killauraActionVerbose = new Verbose(this),
            reachAVerbose = new Verbose(this), nofallGroundVerbose = new Verbose(this), jesusSpeedVerbose = new Verbose(this),
            scaffoldTowerVerbose = new Verbose(this), speedTypeBVerbose = new Verbose(this), bpGVerbose = new Verbose(this),
            acConsistentVerbose = new Verbose(this), killauraDirectionVerbose = new Verbose(this), motionVerbose = new Verbose(this),
            noSlowTypeCVerbose = new Verbose(this), scaffoldTopVerbose = new Verbose(this), jesusWalkVerbose = new Verbose(this),
            scaffoldSnapVerbose = new Verbose(this), invalidMotionVerbose = new Verbose(this), killauraLVerbose = new Verbose(this),
            sprintOmniVerbose = new Verbose(this), killauraOffsetVerbose = new Verbose(this), aimPatternPitchVerbose = new Verbose(this),
            criticalsFallVerbose = new Verbose(this), killauraSwingVerbose = new Verbose(this), aimPatternVapeVerbose = new Verbose(this),
            killauraInvVerbose = new Verbose(this), invMoveVerbose = new Verbose(this), invClickVerbose = new Verbose(this), killauraDeadVerbose = new Verbose(this),
            handIllegalVerbose = new Verbose(this), auraPointVerbose = new Verbose(this), apIntVerbose = new Verbose(this),
            dynamicVerbose = new Verbose(this), aimPatternYawVerbose = new Verbose(this),
            handAnimationVerbose = new Verbose(this), kaAngleVerbose = new Verbose(this), kaRTVerbose = new Verbose(this),
            reachTypeBVerbose = new Verbose(this), autoclickerHeuristicVerbose = new Verbose(this), postKillauraVerbose = new Verbose(this),
            criticalsCVerbose = new Verbose(this), autoclickerTypeFVerbose = new Verbose(this);
    public List<String> cachedLogStrings;
    public Movement movement;
    public Timers timers;
    public Skiderino skiderino;

    public PlayerData(Player player) {
        this.player = player;
        this.alerts = true;
        cachedLogStrings = new ArrayList<>();
        moveData = new MoveData();
        movement = new Movement();
        skiderino = new Skiderino();
        timers = new Timers();

        boundingBox = fromBoundingBox = new BoundingBox(0, 0, 0, 0, 0, 0);
    }

    public boolean hasLag() {
        return Math.abs(ping - lastPing) > 60;
    }

    public void setCancelled(CheckType type, int ticks) {
        this.cancelType = type;
        this.cancelTicks += cancelTicks < 10 ? ticks : 0;
    }

    public float getViolations(Check check) {
        if (Fiona.getInstance().getCheckManager().violations.containsKey(player.getUniqueId())) {
            List<Violation> violationList = Fiona.getInstance().getCheckManager().violations.get(player.getUniqueId());

            Optional<Violation> violationOp = violationList.stream().filter(vl -> vl.getCheck().getName().equals(check.getName())).findFirst();

            if (violationOp.isPresent()) {
                return violationOp.get().getCombinedAmount();
            }
        }
        return 0f;
    }

    public float getViolations(Detection detection) {
        if (Fiona.getInstance().getCheckManager().violations.containsKey(player.getUniqueId())) {
            List<Violation> violationList = Fiona.getInstance().getCheckManager().violations.get(player.getUniqueId());

            Optional<Violation> violationOp = violationList.stream().filter(vl -> vl.getCheck().getName().equals(detection.getParentCheck().getName())).findFirst();

            if (violationOp.isPresent()) {
                Violation violation = violationOp.get();

                Optional<Detection> detectionOp = violation.getSpecificViolations().keySet().stream().filter(detect -> detect.getId().equals(detection.getId())).findFirst();

                if (detectionOp.isPresent()) {
                    return violation.getSpecificViolations().get(detectionOp.get());
                }
            }
        }
        return 0f;
    }

    public boolean isFullyOnGround() {
        return onGround || fromOnGround;
    }

    public boolean isVelocityTaken() {
        return !lastVelocity.hasPassed(90);
    }

    public FionaLocation getLocationByTime(long millis) {
        return pastLocation.stream().min(Comparator.comparingLong(location -> (long) Math.abs(MathUtils.elapsed(location.getTimeStamp()) - millis))).orElse(pastLocation.get(pastLocation.size() - 1));
    }

    public void addPastLocation(FionaLocation fionaLocation) {
        if (pastLocation.size() >= 20) {
            pastLocation.remove(0);
        }

        pastLocation.add(fionaLocation);
    }

    public class Movement {
        public Location from = new Location(player.getWorld(), player.getEyeLocation().getX(), player.getEyeLocation().getY(), player.getEyeLocation().getZ(), player.getEyeLocation().getYaw(), player.getEyeLocation().getPitch());
        public int lastGroundY, groundY;
        public double deltaXZ, lastDeltaXZ, deltaY, realFallDistance, slimeDistance, lastDeltaY;

        public boolean hasJumped, inAir;
    }

    public class Skiderino {
        public float yaw = 0, walkSpeed = 0.1f;
        public boolean fMath = false;

        public double posX, posY, posZ = 0; // Position of the Player
        public double lPosX, lPosY, lPosZ = 0; // Position of the Player from the last MovePacket
        public double lmotionX, lmotionY, lmotionZ = 0; // Motion of the Player from the last MovePacket
        public double rmotionX, rmotionY, rmotionZ = 0; // Motion of the Player
        public boolean lastOnGround, fastMath, walkSpecial, lastSneak, lastSprint, lastVelocity = false; // Values from MovePacket befor
        public boolean fly, sneak, sprint, useSword, hit, dropItem, velocity, onGround = false; // Values from the Player
    }

    public class Timers {
        public PlayerTimer lastJump = new PlayerTimer(PlayerData.this);
    }

    // functions of the Optifine src
    private static final float[] SIN_TABLE_FAST = new float[4096];
    private static final float[] SIN_TABLE = new float[65536];

    public float sin(float p_76126_0_) {
        return skiderino.fastMath ? SIN_TABLE_FAST[(int) (p_76126_0_ * 651.8986F) & 4095]
                : SIN_TABLE[(int) (p_76126_0_ * 10430.378F) & 65535];
    }

    public float cos(float p_76134_0_) {
        return skiderino.fastMath ? SIN_TABLE_FAST[(int) ((p_76134_0_ + ((float) Math.PI / 2F)) * 651.8986F) & 4095]
                : SIN_TABLE[(int) (p_76134_0_ * 10430.378F + 16384.0F) & 65535];
    }

    static {
        int i;

        for (i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
        }

        for (i = 0; i < 4096; ++i) {
            SIN_TABLE_FAST[i] = (float) Math.sin((double) (((float) i + 0.5F) / 4096.0F * ((float) Math.PI * 2F)));
        }

        for (i = 0; i < 360; i += 90) {
            SIN_TABLE_FAST[(int) ((float) i * 11.377778F) & 4095] = (float) Math
                    .sin((double) ((float) i * 0.017453292F));
        }
    }

    // functions of minecraft MathHelper.java
    public static float sqrt_float(float p_76129_0_) {
        return (float) Math.sqrt((double) p_76129_0_);
    }

    public static float sqrt_double(double p_76133_0_) {
        return (float) Math.sqrt(p_76133_0_);
    }
}