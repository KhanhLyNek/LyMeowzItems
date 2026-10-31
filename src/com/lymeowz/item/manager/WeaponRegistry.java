package com.lymeowz.item.manager;

import com.lymeowz.item.ability.WeaponAbility;
import java.util.Collection;

/**
 * Canonical read-only facade implemented by the existing registered weapon
 * manager. This is not a second registry: all lookups use the same 25 legacy
 * singletons, custom registrations and existing lifecycle.
 */
public interface WeaponRegistry {
    WeaponAbility weapon(String id);
    Collection<? extends WeaponAbility> weapons();
}
