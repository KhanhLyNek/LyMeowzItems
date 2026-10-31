package com.lymeowz.item.manager;

import com.lymeowz.item.ability.WeaponAbility;
import java.util.Map;

/** The original case-insensitive weapon ID lookup, moved without re-keying. */
public final class WeaponLookup {
    private WeaponLookup() { }

    public static <T extends WeaponAbility> T find(final Map<String, T> entries, final String id) {
        if (id != null) {
            for (final String key : entries.keySet()) {
                if (key.equalsIgnoreCase(id)) {
                    return entries.get(key);
                }
            }
        }
        return null;
    }
}
