package com.lymeowz.item.config;

import api.praya.myitems.builder.item.ItemGenerator;
import api.praya.myitems.builder.item.ItemGeneratorTier;
import api.praya.myitems.builder.item.ItemGeneratorType;
import api.praya.myitems.builder.item.ItemTier;
import api.praya.myitems.builder.item.ItemType;
import com.praya.agarthalib.utility.FileUtil;
import com.praya.agarthalib.utility.TextUtil;
import com.praya.myitems.MyItems;
import com.praya.myitems.manager.game.ItemTierManager;
import com.praya.myitems.manager.game.ItemTypeManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Canonical parser of legacy item generator YAML.
 *
 * Do not rename YAML fields/IDs, rebalance possibilities or normalize
 * ItemGenerator data. Existing builders and generation remain authoritative.
 */
public final class ItemGeneratorDefinitions {
    private ItemGeneratorDefinitions() {
    }

    public static HashMap<String, ItemGenerator> load(final MyItems plugin,
            final String pathDefault, final String pathFolder,
            final ItemTypeManager itemTypeManager, final ItemTierManager itemTierManager) {
        final File fileDefault = FileUtil.getFile((JavaPlugin) plugin, pathDefault);
        final File fileFolder = FileUtil.getFile((JavaPlugin) plugin, pathFolder);
        if (!fileDefault.exists()) {
            FileUtil.saveResource((JavaPlugin) plugin, pathDefault);
        }
        final HashMap<String, ItemGenerator> results = new HashMap<>();
        final File[] listFiles = fileFolder.listFiles();
        if (listFiles == null) {
            // A missing or unreadable generator directory must not crash startup.
            return results;
        }
        for (final File file : listFiles) {
            final FileConfiguration config = FileUtil.getFileConfiguration(file);
            results.putAll(read(config, itemTypeManager, itemTierManager));
        }
        return results;
    }

    /** Parse generator IDs exactly as found in the original YAML file. */
    public static HashMap<String, ItemGenerator> read(final FileConfiguration config,
            final ItemTypeManager itemTypeManager, final ItemTierManager itemTierManager) {
        final HashMap<String, ItemGenerator> mapItemGenerator = new HashMap<>();
        for (final String key : config.getKeys(false)) {
            final ConfigurationSection dataSection = config.getConfigurationSection(key);
            if (dataSection == null) {
                continue;
            }
            final List<String> lores = new ArrayList<String>();
            final List<String> flags = new ArrayList<String>();
            final HashMap<ItemType, ItemGeneratorType> mapType = new HashMap<ItemType, ItemGeneratorType>();
            final HashMap<ItemTier, ItemGeneratorTier> mapTier = new HashMap<ItemTier, ItemGeneratorTier>();
            String displayName = null;
            boolean unbreakable = false;
            for (final String data : dataSection.getKeys(false)) {
                if (data.equalsIgnoreCase("Display_Name")) {
                    displayName = TextUtil.colorful(dataSection.getString(data));
                }
                else if (data.equalsIgnoreCase("Unbreakable")) {
                    unbreakable = dataSection.getBoolean(data);
                }
                else if (data.equalsIgnoreCase("Flags") || data.equalsIgnoreCase("ItemFlags")) {
                    flags.addAll(dataSection.getStringList(data));
                }
                else if (data.equalsIgnoreCase("Lores")) {
                    lores.addAll(dataSection.getStringList(data));
                }
                else if (data.equalsIgnoreCase("Type")) {
                    final ConfigurationSection typeSection = dataSection.getConfigurationSection(data);
                    if (typeSection == null) {
                        continue;
                    }
                    for (final String type : typeSection.getKeys(false)) {
                        final ItemType itemType = itemTypeManager.getItemType(type);
                        if (itemType != null) {
                            final ConfigurationSection typePropertiesSection = typeSection.getConfigurationSection(type);
                            if (typePropertiesSection == null) {
                                continue;
                            }
                            final List<String> description = new ArrayList<String>();
                            final List<String> names = new ArrayList<String>();
                            int possibility = 1;
                            for (final String typeProperties : typePropertiesSection.getKeys(false)) {
                                if (typeProperties.equalsIgnoreCase("Possibility")) {
                                    possibility = typePropertiesSection.getInt(typeProperties);
                                }
                                else if (typeProperties.equalsIgnoreCase("Description")) {
                                    for (final String desc : typePropertiesSection.getStringList(typeProperties)) {
                                        description.add(desc);
                                    }
                                }
                                else {
                                    if (!typeProperties.equalsIgnoreCase("Name") && !typeProperties.equalsIgnoreCase("Names")) {
                                        continue;
                                    }
                                    for (final String name : typePropertiesSection.getStringList(typeProperties)) {
                                        names.add(name);
                                    }
                                }
                            }
                            final ItemGeneratorType itemTypeProperties = new ItemGeneratorType(possibility, description, names);
                            mapType.put(itemType, itemTypeProperties);
                        }
                    }
                }
                else {
                    if (!data.equalsIgnoreCase("Tier")) {
                        continue;
                    }
                    final ConfigurationSection tierSection = dataSection.getConfigurationSection(data);
                    if (tierSection == null) {
                        continue;
                    }
                    for (final String tier : tierSection.getKeys(false)) {
                        final ItemTier itemTier = itemTierManager.getItemTier(tier);
                        if (itemTier != null) {
                            final ConfigurationSection tierPropertiesSection = tierSection.getConfigurationSection(tier);
                            if (tierPropertiesSection == null) {
                                continue;
                            }
                            final List<String> additionalLores = new ArrayList<String>();
                            int possibility2 = 1;
                            for (final String tierProperties : tierPropertiesSection.getKeys(false)) {
                                if (tierProperties.equalsIgnoreCase("Possibility")) {
                                    possibility2 = tierPropertiesSection.getInt(tierProperties);
                                }
                                else {
                                    if (!tierProperties.equalsIgnoreCase("Additional_Lores")) {
                                        continue;
                                    }
                                    for (final String additionalLore : tierPropertiesSection.getStringList(tierProperties)) {
                                        additionalLores.add(additionalLore);
                                    }
                                }
                            }
                            final ItemGeneratorTier itemTierProperties = new ItemGeneratorTier(possibility2, additionalLores);
                            mapTier.put(itemTier, itemTierProperties);
                        }
                    }
                }
            }
            if (displayName != null) {
                final ItemGenerator itemGenerator = new ItemGenerator(key, displayName, unbreakable, lores, flags, mapType, mapTier);
                mapItemGenerator.put(key, itemGenerator);
            }
        }
        return mapItemGenerator;
    }
}
