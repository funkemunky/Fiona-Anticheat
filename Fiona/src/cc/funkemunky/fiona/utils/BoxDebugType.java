package cc.funkemunky.fiona.utils;

public enum BoxDebugType {
    HITBOX("HITBOX"), GROUND("GROUND"), COLLIDED("COLLIDED"), ALL("ALL");

    private String id;

    BoxDebugType(String id) {
        this.id = id;
    }

    public static BoxDebugType getFromString(String id) {
        return valueOf(id);
    }

    public static boolean isDebugType(String id) {
        try {
            valueOf(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
