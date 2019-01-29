package cc.funkemunky.fiona.utils;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@Getter
@Setter
public class FionaLocation {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private long timeStamp;

    public FionaLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        timeStamp = System.currentTimeMillis();
    }

    public FionaLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
        timeStamp = System.currentTimeMillis();
    }

    public FionaLocation(Location location) {
        this.x = (float) location.getX();
        this.y = (float) location.getY();
        this.z = (float) location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        timeStamp = System.currentTimeMillis();
    }

    public Location toLocation() {
        return new Location(null, x, y, z, yaw, pitch);
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public FionaLocation getBlockLocation() {
        return new FionaLocation(MathUtils.trim(0, x), MathUtils.trim(0, y), MathUtils.trim(0, z), 0f, 0f);
    }
}
