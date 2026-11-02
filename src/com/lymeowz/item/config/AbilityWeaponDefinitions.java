package com.lymeowz.item.config;

import com.lymeowz.item.api.builder.ability.AbilityWeaponProperties;
import com.lymeowz.lib.utility.FileUtil;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.manager.plugin.DataManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.HashMap;

/**
 * Original shipped skill YAML grammar relocated to LyMeowzItem.
 * Never change field defaults, numeric conversions, case matching or the
 * packaged-config-then-user-override order without a dedicated migration.
 * Existing AbilityWeaponProperties instances remain the actual builders.
 */
public final class AbilityWeaponDefinitions {
    private AbilityWeaponDefinitions() { }

    public static HashMap<String, AbilityWeaponProperties> load(final MyItems plugin) {
        final HashMap<String, AbilityWeaponProperties> map = new HashMap<>();
        final PluginManager pluginManager = plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final String path = dataManager.getPath("Path_File_Ability_Weapon");
        final File file = FileUtil.getFile((JavaPlugin)plugin, path);
        if (!file.exists()) {
            FileUtil.saveResource((JavaPlugin)plugin, path);
        }
        for (int t = 0; t < 2; ++t) {
            FileConfiguration config;
            if (t == 0) {
                config = FileUtil.getFileConfigurationResource((JavaPlugin)plugin, path);
            }
            else {
                config = FileUtil.getFileConfiguration(file);
            }
            parseInto(config, map);
        }
        return map;
    }

    /** Parse one YAML layer without changing key spelling, defaults or clamping. */
    public static HashMap<String, AbilityWeaponProperties> parse(final FileConfiguration config) {
        final HashMap<String, AbilityWeaponProperties> map = new HashMap<>();
        parseInto(config, map);
        return map;
    }

    private static void parseInto(final FileConfiguration config,
                                  final HashMap<String, AbilityWeaponProperties> map) {
            for (final String key : config.getKeys(false)) {
                final ConfigurationSection mainDataSection = config.getConfigurationSection(key);
                int maxGrade = 1;
                int baseDurationEffect = 1;
                int scaleDurationEffect = 1;
                double scaleBaseBonusDamage = 0.0;
                double scaleBasePercentDamage = 0.0;
                double scaleCastBonusDamage = 0.0;
                double scaleCastPercentDamage = 0.0;
                for (final String keySection : mainDataSection.getKeys(false)) {
                    if (keySection.equalsIgnoreCase("Max_Grade")) {
                        maxGrade = mainDataSection.getInt(keySection);
                    }
                    else if (keySection.equalsIgnoreCase("Base_Duration_Effect")) {
                        baseDurationEffect = mainDataSection.getInt(keySection);
                    }
                    else if (keySection.equalsIgnoreCase("Scale_Duration_Effect")) {
                        scaleDurationEffect = mainDataSection.getInt(keySection);
                    }
                    else if (keySection.equalsIgnoreCase("Scale_Base_Bonus_Damage")) {
                        scaleBaseBonusDamage = mainDataSection.getDouble(keySection);
                    }
                    else if (keySection.equalsIgnoreCase("Scale_Base_Percent_Damage")) {
                        scaleBasePercentDamage = mainDataSection.getDouble(keySection);
                    }
                    else if (keySection.equalsIgnoreCase("Scale_Cast_Bonus_Damage")) {
                        scaleCastBonusDamage = mainDataSection.getDouble(keySection);
                    }
                    else {
                        if (!keySection.equalsIgnoreCase("Scale_Cast_Percent_Damage")) {
                            continue;
                        }
                        scaleCastPercentDamage = mainDataSection.getDouble(keySection);
                    }
                }
                maxGrade = Math.max(1, maxGrade);
                baseDurationEffect = Math.max(0, baseDurationEffect);
                scaleDurationEffect = Math.max(0, scaleDurationEffect);
                final AbilityWeaponProperties abilityWeaponProperties = new AbilityWeaponProperties(maxGrade, baseDurationEffect, scaleDurationEffect, scaleBaseBonusDamage, scaleBasePercentDamage, scaleCastBonusDamage, scaleCastPercentDamage);
                map.put(key, abilityWeaponProperties);
            }
    }
}
