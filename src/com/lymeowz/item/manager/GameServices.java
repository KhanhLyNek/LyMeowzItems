package com.lymeowz.item.manager;

import com.lymeowz.item.ability.WeaponProperties;
import java.util.Collection;

/**
 * Public new-namespace projection of the original live game managers.
 * No duplicate services are instantiated and all legacy getters remain.
 */
public interface GameServices {
    WeaponRegistry weaponRegistry();
    WeaponProperties weaponProperties(String id);
    Collection<String> weaponPropertyIds();
    ElementRegistry elements();
    ItemCatalog items();
    PowerCommandRegistry powerCommands();
    ItemGeneratorRegistry itemGenerators();
    ItemTypeRegistry itemTypes();
    ItemTierRegistry itemTiers();
}
