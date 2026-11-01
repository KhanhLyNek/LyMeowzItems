package com.lymeowz.item.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import com.praya.myitems.manager.plugin.DataManager;
import com.praya.myitems.manager.plugin.PluginManager;
import api.praya.myitems.builder.lorestats.LoreStatsUniversal;
import api.praya.myitems.builder.lorestats.LoreStatsArmor;
import api.praya.myitems.builder.lorestats.LoreStatsWeapon;
import core.praya.agarthalib.enums.main.TagsAttribute;
import com.praya.agarthalib.utility.EnchantmentUtil;
import com.praya.agarthalib.utility.MaterialUtil;
import api.praya.myitems.builder.lorestats.LoreStatsModifier;
import api.praya.myitems.builder.item.ItemTypeNBT;
import core.praya.agarthalib.enums.main.Slot;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import com.praya.agarthalib.utility.FileUtil;
import java.util.Iterator;
import java.util.Collection;
import com.praya.myitems.MyItems;
import api.praya.myitems.builder.item.ItemType;
import java.util.HashMap;

/**
 * Canonical YAML reader for the original ItemType database.
 * The original public builders, IDs and game stat modifiers remain intact.
 */
public final class ItemTypeDefinitions {
    private ItemTypeDefinitions() { }

    public static HashMap<String, ItemType> load(final MyItems plugin,
            final String pathDefault, final String pathFolder) {
        final File fileDefault = FileUtil.getFile((JavaPlugin) plugin, pathDefault);
        final File fileFolder = FileUtil.getFile((JavaPlugin) plugin, pathFolder);
        if (!fileDefault.exists()) {
            FileUtil.saveResource((JavaPlugin) plugin, pathDefault);
        }
        final HashMap<String, ItemType> result = new HashMap<>();
        final File[] listFiles = fileFolder.listFiles();
        if (listFiles == null) {
            // Missing or unreadable directory must not abort plugin startup.
            return result;
        }
        for (final File file : listFiles) {
            final FileConfiguration config = FileUtil.getFileConfiguration(file);
            result.putAll(read(config));
        }
        return result;
    }

    /** Keep the original aliases, modifier defaults and nested NBT mapping. */
    public static HashMap<String, ItemType> read(final FileConfiguration config) {
        final HashMap<String, ItemType> result = new HashMap<>();
        for (final String key : config.getKeys(false)) {
            final ConfigurationSection mainDataSection = config.getConfigurationSection(key);
            if (mainDataSection == null) {
                continue;
            }
            final HashMap<Enchantment, Integer> mapEnchantment = new HashMap<Enchantment, Integer>();
            final HashMap<Slot, ItemTypeNBT> mapNBT = new HashMap<Slot, ItemTypeNBT>();
            Material material = null;
            boolean shiny = false;
            short data = 0;
            LoreStatsModifier statsModifier = new LoreStatsModifier();
            for (final String mainData : mainDataSection.getKeys(false)) {
                if (mainData.equalsIgnoreCase("Material")) {
                    material = MaterialUtil.getMaterial(mainDataSection.getString(mainData));
                }
                else if (mainData.equalsIgnoreCase("Data")) {
                    data = (short)mainDataSection.getInt(mainData);
                }
                else if (mainData.equalsIgnoreCase("Shiny")) {
                    shiny = mainDataSection.getBoolean(mainData);
                }
                else if (mainData.equalsIgnoreCase("Enchantment")) {
                    final ConfigurationSection enchantmentDataSection = mainDataSection.getConfigurationSection(mainData);
                    if (enchantmentDataSection == null) {
                        continue;
                    }
                    for (final String enchantmentData : enchantmentDataSection.getKeys(false)) {
                        final Enchantment enchantment = EnchantmentUtil.getEnchantment(enchantmentData);
                        if (enchantment != null) {
                            final int grade = enchantmentDataSection.getInt(enchantmentData);
                            mapEnchantment.put(enchantment, grade);
                        }
                    }
                }
                else if (mainData.equalsIgnoreCase("NBT")) {
                    final ConfigurationSection nbtDataSection = mainDataSection.getConfigurationSection(mainData);
                    if (nbtDataSection == null) {
                        continue;
                    }
                    for (final String nbtData : nbtDataSection.getKeys(false)) {
                        final Slot slot = Slot.get(nbtData);
                        if (slot != null) {
                            final ConfigurationSection tagsDataSection = nbtDataSection.getConfigurationSection(nbtData);
                            if (tagsDataSection == null) {
                                continue;
                            }
                            final HashMap<TagsAttribute, Double> mapTagsAttribute = new HashMap<TagsAttribute, Double>();
                            for (final String tagsData : tagsDataSection.getKeys(false)) {
                                final TagsAttribute tagsAttribute = TagsAttribute.getTagsAttribute(tagsData);
                                if (tagsAttribute != null) {
                                    final double tagsValue = tagsDataSection.getDouble(tagsData);
                                    mapTagsAttribute.put(tagsAttribute, tagsValue);
                                }
                            }
                            final ItemTypeNBT itemTypeNBT = new ItemTypeNBT(mapTagsAttribute);
                            mapNBT.put(slot, itemTypeNBT);
                        }
                    }
                }
                else {
                    if (!mainData.equalsIgnoreCase("Modifier")) {
                        continue;
                    }
                    final ConfigurationSection modifierDataSection = mainDataSection.getConfigurationSection(mainData);
                    if (modifierDataSection == null) {
                        continue;
                    }
                    double damage = 1.0;
                    double penetration = 1.0;
                    double pvpDamage = 1.0;
                    double pveDamage = 1.0;
                    double criticalChance = 1.0;
                    double criticalDamage = 1.0;
                    double hitRate = 1.0;
                    double defense = 1.0;
                    double pvpDefense = 1.0;
                    double pveDefense = 1.0;
                    double health = 1.0;
                    double healthRegen = 1.0;
                    double staminaMax = 1.0;
                    double staminaRegen = 1.0;
                    double attackAoERadius = 1.0;
                    double attackAoEDamage = 1.0;
                    double blockAmount = 1.0;
                    double blockRate = 1.0;
                    double dodgeRate = 1.0;
                    double durability = 1.0;
                    double level = 1.0;
                    for (final String modifierData : modifierDataSection.getKeys(false)) {
                        if (modifierData.equalsIgnoreCase("Damage")) {
                            damage = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Penetration")) {
                            penetration = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("PvP_Damage")) {
                            pvpDamage = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("PvE_Damage")) {
                            pveDamage = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Critical_Chance")) {
                            criticalChance = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Critical_Damage")) {
                            criticalDamage = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Hit_Rate")) {
                            hitRate = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Defense")) {
                            defense = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("PvP_Defense")) {
                            pvpDefense = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("PvE_Defense")) {
                            pveDefense = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Health")) {
                            health = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Health_Regen")) {
                            healthRegen = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Stamina_Max")) {
                            staminaMax = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Stamina_Regen")) {
                            staminaRegen = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Attack_AoE_Radius")) {
                            attackAoERadius = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Attack_AoE_Damage")) {
                            attackAoEDamage = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Block_Amount")) {
                            blockAmount = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Block_Rate")) {
                            blockRate = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Dodge_Rate")) {
                            dodgeRate = modifierDataSection.getDouble(modifierData);
                        }
                        else if (modifierData.equalsIgnoreCase("Durability")) {
                            durability = modifierDataSection.getDouble(modifierData);
                        }
                        else {
                            if (!modifierData.equalsIgnoreCase("Level")) {
                                continue;
                            }
                            level = modifierDataSection.getDouble(modifierData);
                        }
                    }
                    final LoreStatsWeapon weaponModifier = new LoreStatsWeapon(damage, penetration, pvpDamage, pveDamage, attackAoERadius, attackAoEDamage, criticalChance, criticalDamage, hitRate);
                    final LoreStatsArmor armorModifier = new LoreStatsArmor(defense, pvpDefense, pveDefense, health, healthRegen, staminaMax, staminaRegen, blockAmount, blockRate, dodgeRate);
                    final LoreStatsUniversal universalModifier = new LoreStatsUniversal(durability, level);
                    statsModifier = new LoreStatsModifier(weaponModifier, armorModifier, universalModifier);
                }
            }
            if (material != null) {
                final ItemType itemType = new ItemType(key, material, data, shiny, statsModifier, mapEnchantment, mapNBT);
                result.put(key, itemType);
            }
        }
        return result;
    }
}
