package com.lymeowz.item.element;

import java.util.Map;
import org.bukkit.potion.PotionEffectType;

/**
 * Read-only potion effect mappings from an original elemental gameplay builder.
 * PotionEffectType keys are original Bukkit types; values are immutable copies.
 */
public interface ElementPotionDefinition {
    Map<PotionEffectType, ElementEffect> attackerEffects();
    Map<PotionEffectType, ElementEffect> victimEffects();
}
