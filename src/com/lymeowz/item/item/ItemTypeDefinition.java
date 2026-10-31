package com.lymeowz.item.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import java.util.Map;

/** Read-only view of a live, legacy item type builder. */
public interface ItemTypeDefinition {
    String id();
    Material material();
    short legacyData();
    boolean shiny();
    Map<Enchantment, Integer> enchantmentLevels();
}
