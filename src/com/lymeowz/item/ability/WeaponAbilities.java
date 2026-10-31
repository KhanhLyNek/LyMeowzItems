package com.lymeowz.item.ability;

import com.lymeowz.item.LyMeowzItem;
import com.lymeowz.item.manager.WeaponRegistry;
import java.util.Collection;
import java.util.List;

/**
 * Public LyMeowz API facade for the existing 25 built-in and custom weapon
 * singletons. Call with the plugin's actual LyMeowzItem entrypoint.
 * No legacy com.praya type is required in the public method signatures.
 */
public final class WeaponAbilities {
    private WeaponAbilities() { }

    public static WeaponRegistry registry(final LyMeowzItem plugin) {
        if (plugin == null || !plugin.isEnabled() || plugin.getRegisterManager() == null) {
            return null;
        }
        return plugin.getRegisterManager().getRegisterAbilityWeaponManager();
    }

    public static WeaponAbility find(final LyMeowzItem plugin, final String id) {
        final WeaponRegistry registry = registry(plugin);
        return registry == null ? null : registry.weapon(id);
    }

    public static Collection<? extends WeaponAbility> all(final LyMeowzItem plugin) {
        final WeaponRegistry registry = registry(plugin);
        return registry == null ? List.of() : registry.weapons();
    }
}
