// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.config.game;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import com.lymeowz.item.manager.plugin.DataManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.lib.utility.FileUtil;
import java.util.Iterator;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.api.builder.ability.AbilityWeaponProperties;
import java.util.HashMap;
import com.lymeowz.item.builder.handler.HandlerConfig;

public class AbilityWeaponConfig extends HandlerConfig
{
    private final HashMap<String, AbilityWeaponProperties> mapAbilityWeaponProperties;
    
    public AbilityWeaponConfig(final MyItems plugin) {
        super(plugin);
        this.mapAbilityWeaponProperties = new HashMap<String, AbilityWeaponProperties>();
    }
    
    public final Collection<String> getAbilityWeaponPropertiesIDs() {
        return this.mapAbilityWeaponProperties.keySet();
    }
    
    public final Collection<AbilityWeaponProperties> getAllAbilityWeaponProperties() {
        return this.mapAbilityWeaponProperties.values();
    }
    
    public final AbilityWeaponProperties getAbilityWeaponProperties(final String ability) {
        if (ability != null) {
            for (final String key : this.getAbilityWeaponPropertiesIDs()) {
                if (key.equalsIgnoreCase(ability)) {
                    return this.mapAbilityWeaponProperties.get(key);
                }
            }
        }
        return null;
    }
    
    public final void setup() {
        this.moveOldFile();
        this.reset();
        this.loadConfig();
    }
    
    private final void reset() {
        this.mapAbilityWeaponProperties.clear();
    }
    
    private final void loadConfig() {
        this.mapAbilityWeaponProperties.putAll(
                com.lymeowz.item.config.AbilityWeaponDefinitions.load(this.plugin));
    }

    private final void moveOldFile() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final String pathSource = "Configuration/ability.yml";
        final String pathTarget = dataManager.getPath("Path_File_Ability_Weapon");
        final File fileSource = FileUtil.getFile((JavaPlugin)this.plugin, "Configuration/ability.yml");
        final File fileTarget = FileUtil.getFile((JavaPlugin)this.plugin, pathTarget);
        if (fileSource.exists()) {
            FileUtil.moveFileSilent(fileSource, fileTarget);
        }
    }
}
