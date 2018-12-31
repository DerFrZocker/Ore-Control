package de.derfrzocker.ore.control;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.permissions.Permissible;

import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum Permissions {

    BASE_PERMISSION("ore.control", false),
    RELOAD_PERMISSION("reload", true),
    SET_PERMISSION("set", true);

    @NonNull
    private final String permission;

    private final boolean commandPermission;

    public String getPermission() {
        if (this == BASE_PERMISSION)
            return permission;

        return String.format("%s.%s", BASE_PERMISSION.getPermission(), permission);
    }

    public boolean hasPermission(@NonNull Permissible permissible) {
        return permissible.hasPermission(getPermission());
    }

    public static boolean hasAnyCommandPermission(@NonNull Permissible permissible) {
        return Stream.of(values()).filter(Permissions::isCommandPermission).anyMatch(value -> permissible.hasPermission(value.getPermission()));
    }

}
