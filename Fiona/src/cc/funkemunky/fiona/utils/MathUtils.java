package cc.funkemunky.fiona.utils;

import cc.funkemunky.fiona.data.PlayerData;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import io.netty.util.internal.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class MathUtils {

    private String spigot_id;
    public static final double MIN_EXPANSION_VALUE = 0b100000000000000000;
    public static final double MIN_EUCILDEAN_VALUE = 0b100000000000000;
    public static final double EXPANDER = Math.pow(2, 24);

    public MathUtils() {
        spigot_id = "%%__USER__%%";
    }

    public static double offset(Vector from, Vector to) {
        from.setY(0);
        to.setY(0);

        return to.subtract(from).length();
    }

    public static long getGcd(long current, long previous) {
        return previous <= MathUtils.MIN_EUCILDEAN_VALUE ? current :
                MathUtils.getGcd(previous, MathUtils.getDelta(current, previous));
    }

    public static boolean moved(FionaLocation from, FionaLocation to) {
        return playerMoved(from, to);
    }

    public static boolean looked(FionaLocation from, FionaLocation to) {
        return playerLooked(from, to);
    }

    public static boolean playerMoved(Location from, Location to) {
        return playerMoved(from.toVector(), to.toVector());
    }

    private static long gcd(long x, long y) {
        return (y == 0) ? x : gcd(y, x % y);
    }

    public static long gcd(long... numbers) {
        return Arrays.stream(numbers).reduce(0, MathUtils::gcd);
    }

    public static long lcm(long... numbers) {
        return Arrays.stream(numbers).reduce(1, (x, y) -> x * (y / gcd(x, y)));
    }

    public static boolean playerMoved(FionaLocation from, FionaLocation to) {
        return playerMoved(from.toVector(), to.toVector());
    }

    public static int modeAmount(Collection<Double> doubles) {
        Map<Double, Integer> counts = new HashMap<>();
        doubles.forEach(dub -> {
            int count = counts.getOrDefault(dub, 0) + 1;

            counts.put(dub, count);
        });

        return counts.values().stream().max(Comparator.naturalOrder()).orElse(0);
    }

    public static int tryParse(String string) {
        Object object = ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)
                ? ReflectionsUtil.getMethodValue(
                ReflectionsUtil.getMethod(ReflectionsUtil.getClass("com.google.common.primitives.Ints"), "tryParse", String.class), ReflectionsUtil.getClass("com.google.common.primitives.Ints"), string)
                : ReflectionsUtil.getMethodValue(ReflectionsUtil.getMethod(ReflectionsUtil.getClass("net.minecraft.util.com.google.common.primitives.Ints"), "tryParse", String.class), ReflectionsUtil.getClass("net.minecraft.util.com.google.common.primitives.Ints"), string);

        if (object != null) {
            return (int) object;
        }

        return -1;
    }

    public static boolean approxEquals(double accuracy, double... equals) {
        return MathUtils.getDelta(Arrays.stream(equals).sum() / equals.length, equals[0]) < accuracy;
    }

    public static boolean playerMoved(Vector from, Vector to) {
        return from.distance(to) > 0;
    }

    public static boolean playerLooked(Location from, Location to) {
        return (from.getYaw() - to.getYaw() != 0) || (from.getPitch() - to.getPitch() != 0);
    }

    public static boolean playerLooked(FionaLocation from, FionaLocation to) {
        return (from.getYaw() - to.getYaw() != 0) || (from.getPitch() - to.getPitch() != 0);
    }

    public static boolean elapsed(long time, long needed) {
        return Math.abs(System.currentTimeMillis() - time) >= needed;
    }

    public static float getDelta(float one, float two) {
        return Math.abs(one - two);
    }

    public static double getDelta(double one, double two) {
        return Math.abs(one - two);
    }

    public static long getDelta(long one, long two) {
        return Math.abs(one - two);
    }

    public static long elapsed(long time) {
        return Math.abs(System.currentTimeMillis() - time);
    }

    public static double getHorizontalDistance(Location from, Location to) {
        double deltaX = to.getX() - from.getX(), deltaZ = to.getZ() - from.getZ();
        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    public static double getHorizontalDistance(FionaLocation from, FionaLocation to) {
        double deltaX = to.getX() - from.getX(), deltaZ = to.getZ() - from.getZ();
        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    public static long millisToTicks(long millis) {
        return millis / 50;
    }

    public static double getVerticalDistance(Location from, Location to) {
        return Math.abs(from.getY() - to.getY());
    }

    public static double getVerticalDistance(FionaLocation from, FionaLocation to) {
        return Math.abs(from.getY() - to.getY());
    }

    public static int getDistanceToGround(Player p) {
        Location loc = p.getLocation().clone();
        double y = loc.getBlockY();
        int distance = 0;
        for (double i = y; i >= 0.0; i -= 1.0) {
            loc.setY(i);
            if (BlockUtils.getBlock(loc).getType().isSolid() || BlockUtils.getBlock(loc).isLiquid()) break;
            ++distance;
        }
        return distance;
    }

    public static double trim(int degree, double d) {
        String format = "#.#";
        for (int i = 1; i < degree; ++i) {
            format = String.valueOf(format) + "#";
        }
        DecimalFormat twoDForm = new DecimalFormat(format);
        return Double.valueOf(twoDForm.format(d).replaceAll(",", "."));
    }

    public static float trimFloat(int degree, float d) {
        String format = "#.#";
        for (int i = 1; i < degree; ++i) {
            format = String.valueOf(format) + "#";
        }
        DecimalFormat twoDForm = new DecimalFormat(format);
        return Float.valueOf(twoDForm.format(d).replaceAll(",", "."));
    }

    public static double getYawDifference(Location one, Location two) {
        return Math.abs(one.getYaw() - two.getYaw());
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double round(double value, int places, RoundingMode mode) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, mode);
        return bd.doubleValue();
    }

    public static double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, RoundingMode.UP);
        return bd.doubleValue();
    }

    public static int floor(double var0) {
        int var2 = (int) var0;
        return var0 < var2 ? var2 - 1 : var2;
    }

    public static float yawTo180F(float flub) {
        if ((flub %= 360.0f) >= 180.0f) {
            flub -= 360.0f;
        }
        if (flub < -180.0f) {
            flub += 360.0f;
        }
        return flub;
    }

    public static double yawTo180D(double dub) {
        if ((dub %= 360.0) >= 180.0) {
            dub -= 360.0;
        }
        if (dub < -180.0) {
            dub += 360.0;
        }
        return dub;
    }

    public static double getDirection(Location from, Location to) {
        if (from == null || to == null) {
            return 0.0;
        }
        double difX = to.getX() - from.getX();
        double difZ = to.getZ() - from.getZ();
        return MathUtils.yawTo180F((float) (Math.atan2(difZ, difX) * 180.0 / 3.141592653589793) - 90.0f);
    }

    public static float[] getRotations(PlayerData one, LivingEntity two) {
        double diffX = two.getLocation().getX() - one.player.getLocation().getX();
        double diffZ = two.getLocation().getZ() - one.player.getLocation().getZ();

        BoundingBox box = MiscUtils.getEntityBoundingBox(two);
        double diffY = box.maxY - (one.boundingBox.maxY);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public static float[] getRotations(Location one, Location two) {
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public static float[] getRotations(LivingEntity origin, LivingEntity point) {
        Location two = point.getLocation(), one = origin.getLocation();
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public static boolean isLookingTowardsEntity(Location from, Location to, LivingEntity entity) {
        float[] rotFrom = getRotations(from, entity.getLocation()), rotTo = getRotations(to, entity.getLocation());
        float deltaOne = getDelta(from.getYaw(), rotTo[0]), deltaTwo = getDelta(to.getYaw(), rotTo[1]);
        float offsetFrom = getDelta(yawTo180F(from.getYaw()), yawTo180F(rotFrom[0])), offsetTo = getDelta(yawTo180F(to.getYaw()), yawTo180F(rotTo[0]));

        return (deltaOne > deltaTwo && offsetTo > 15) || (MathUtils.getDelta(offsetFrom, offsetTo) < 1 && offsetTo < 10);
    }

    public static double[] getOffsetFromEntity(Player player, LivingEntity entity) {
        double yawOffset = Math.abs(MathUtils.yawTo180F(player.getEyeLocation().getYaw()) - MathUtils.yawTo180F(MathUtils.getRotations(player.getLocation(), entity.getLocation())[0]));
        double pitchOffset = Math.abs(Math.abs(player.getEyeLocation().getPitch()) - Math.abs(MathUtils.getRotations(player.getLocation(), entity.getLocation())[1]));
        return new double[]{yawOffset, pitchOffset};
    }

    public static double[] getOffsetFromLocation(Location one, Location two) {
        double yaw = MathUtils.getRotations(one, two)[0];
        double pitch = MathUtils.getRotations(one, two)[1];
        double yawOffset = Math.abs(yaw - MathUtils.yawTo180F(one.getYaw()));
        double pitchOffset = Math.abs(pitch - one.getPitch());
        return new double[]{yawOffset, pitchOffset};
    }
}

