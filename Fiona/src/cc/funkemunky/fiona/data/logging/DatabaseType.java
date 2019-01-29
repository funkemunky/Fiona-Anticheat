package cc.funkemunky.fiona.data.logging;

import lombok.Getter;

import java.util.Arrays;

public enum DatabaseType {
    MYSQL("MYSQL"), YAML("YAML"), MONGO("MONGO");

    @Getter
    private String name;

    DatabaseType(String name) {
        this.name = name;
    }

    public static DatabaseType getByName(String name) {
        return Arrays.stream(values()).filter(type -> type.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
