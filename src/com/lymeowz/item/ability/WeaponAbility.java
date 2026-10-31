package com.lymeowz.item.ability;

import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * Stable LyMeowzItem view of registered weapon abilities. Existing concrete
 * skills also implement this interface without changing their saved IDs,
 * singleton classes, registration, cast behavior or lore representation.
 */
public interface WeaponAbility {
    Plugin getPlugin();
    String getID();
    String getKeyLore();
    List<String> getDescription();
    int getMaxGrade();
    void cast(Entity caster, Entity target, int grade, double damage);
}
