package com.lymeowz.item.config;

import java.io.File;
import java.util.HashMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffectType;
import com.praya.myitems.MyItems;
import com.praya.myitems.manager.plugin.PluginManager;
import com.praya.myitems.manager.plugin.DataManager;
import com.praya.agarthalib.utility.FileUtil;
import com.praya.agarthalib.utility.TextUtil;
import com.praya.agarthalib.utility.PotionUtil;
import com.praya.agarthalib.utility.MathUtil;
import api.praya.myitems.builder.potion.PotionProperties;
import api.praya.myitems.builder.element.Element;
import api.praya.myitems.builder.element.ElementBoost;
import api.praya.myitems.builder.element.ElementPotion;

/**
 * Canonical elemental YAML reader using the exact legacy key aliases, potion
 * clamping, item lore colors, and file path. This never rewrites element.yml.
 */
public final class ElementDefinitions {
    private ElementDefinitions() { }

    public static HashMap<String, Element> load(final MyItems plugin) {
        final HashMap<String, Element> map = new HashMap<>();
        final PluginManager pluginManager = plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final String path = dataManager.getPath("Path_File_Element");
        final File file = FileUtil.getFile((JavaPlugin)plugin, path);
        if (!file.exists()) {
            FileUtil.saveResource((JavaPlugin)plugin, path);
        }
        final FileConfiguration config = FileUtil.getFileConfiguration(file);
        parseInto(config, map);
        return map;
    }

    /** For fixtures and custom YAML, uses the exact same legacy key grammar. */
    public static HashMap<String, Element> parse(final FileConfiguration config) {
        final HashMap<String, Element> map = new HashMap<>();
        parseInto(config, map);
        return map;
    }

    private static void parseInto(final FileConfiguration config,
                                  final HashMap<String, Element> map) {
        for (final String key : config.getKeys(false)) {
            final ConfigurationSection section = config.getConfigurationSection(key);
            final HashMap<String, Double> resistance = new HashMap<String, Double>();
            final HashMap<PotionEffectType, PotionProperties> potionAttacker = new HashMap<PotionEffectType, PotionProperties>();
            final HashMap<PotionEffectType, PotionProperties> potionVictims = new HashMap<PotionEffectType, PotionProperties>();
            String keyLore = null;
            double scaleAdditionalDamage = 0.0;
            double scalePercentDamage = 0.0;
            for (final String keySection : section.getKeys(false)) {
                if (keySection.equalsIgnoreCase("Keylore")) {
                    keyLore = TextUtil.colorful(section.getString(keySection));
                }
                else if (keySection.equalsIgnoreCase("Scale_Bonus_Additional_Damage") || keySection.equalsIgnoreCase("Scale_Base_Additional_Damage")) {
                    scaleAdditionalDamage = section.getDouble(keySection);
                }
                else if (keySection.equalsIgnoreCase("Scale_Bonus_Percent_Damage") || keySection.equalsIgnoreCase("Scale_Base_Percent_Damage")) {
                    scalePercentDamage = section.getDouble(keySection);
                }
                else if (keySection.equalsIgnoreCase("Resistance")) {
                    final ConfigurationSection resistanceSection = section.getConfigurationSection(keySection);
                    for (final String keyResistance : resistanceSection.getKeys(false)) {
                        resistance.put(keyResistance, resistanceSection.getDouble(keyResistance));
                    }
                }
                else {
                    if (!keySection.equalsIgnoreCase("Potion_To_Attacker") && !keySection.equalsIgnoreCase("Potion_To_Attackers") && !keySection.equalsIgnoreCase("Potion_To_Victim") && !keySection.equalsIgnoreCase("Potion_To_Victims")) {
                        continue;
                    }
                    final ConfigurationSection potionSection = section.getConfigurationSection(keySection);
                    for (final String keyPotion : potionSection.getKeys(false)) {
                        final PotionEffectType potion = PotionUtil.getPotionEffectType(keyPotion);
                        if (potion != null) {
                            final ConfigurationSection attributePotionSection = potionSection.getConfigurationSection(keyPotion);
                            int potionGrade = 1;
                            double potionScaleChance = 0.0;
                            double potionScaleDuration = 1.0;
                            for (final String keyAttribute : attributePotionSection.getKeys(false)) {
                                if (keyAttribute.equalsIgnoreCase("Grade")) {
                                    potionGrade = attributePotionSection.getInt(keyAttribute);
                                }
                                else if (keyAttribute.equalsIgnoreCase("Scale_Chance")) {
                                    potionScaleChance = attributePotionSection.getDouble(keyAttribute);
                                }
                                else {
                                    if (!keyAttribute.equalsIgnoreCase("Scale_Duration")) {
                                        continue;
                                    }
                                    potionScaleDuration = attributePotionSection.getDouble(keyAttribute);
                                }
                            }
                            potionGrade = MathUtil.limitInteger(potionGrade, 1, 10);
                            potionScaleChance = MathUtil.limitDouble(potionScaleChance, 0.01, 100.0);
                            potionScaleDuration = MathUtil.limitDouble(potionScaleDuration, 0.1, 100.0);
                            final PotionProperties potionAttributes = new PotionProperties(potionGrade, potionScaleChance, potionScaleDuration);
                            if (keySection.equalsIgnoreCase("Potion_To_Attacker") || keySection.equalsIgnoreCase("Potion_To_Attackers")) {
                                potionAttacker.put(potion, potionAttributes);
                            }
                            else {
                                if (!keySection.equalsIgnoreCase("Potion_To_Victim") && !keySection.equalsIgnoreCase("Potion_To_Victims")) {
                                    continue;
                                }
                                potionVictims.put(potion, potionAttributes);
                            }
                        }
                    }
                }
            }
            if (keyLore != null) {
                final ElementBoost boostBuild = new ElementBoost(scaleAdditionalDamage, scalePercentDamage);
                final ElementPotion potionBuild = new ElementPotion(potionAttacker, potionVictims);
                final Element build = new Element(keyLore, boostBuild, potionBuild, resistance);
                map.put(key, build);
            }
        }
    }
}
