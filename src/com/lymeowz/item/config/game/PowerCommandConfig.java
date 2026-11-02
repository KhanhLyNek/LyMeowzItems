// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.config.game;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import com.lymeowz.item.manager.plugin.DataManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import com.lymeowz.lib.utility.TextUtil;
import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.lib.utility.FileUtil;
import java.util.Iterator;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.api.builder.power.PowerCommandProperties;
import java.util.HashMap;
import com.lymeowz.item.builder.handler.HandlerConfig;

public class PowerCommandConfig extends HandlerConfig
{
    private final HashMap<String, PowerCommandProperties> mapPowerCommand;
    
    public PowerCommandConfig(final MyItems plugin) {
        super(plugin);
        this.mapPowerCommand = new HashMap<String, PowerCommandProperties>();
    }
    
    public final Collection<String> getPowerCommandIDs() {
        return this.mapPowerCommand.keySet();
    }
    
    public final Collection<PowerCommandProperties> getPowerCommandPropertyBuilds() {
        return this.mapPowerCommand.values();
    }
    
    public final PowerCommandProperties getPowerCommandProperties(final String id) {
        for (final String key : this.getPowerCommandIDs()) {
            if (key.equalsIgnoreCase(id)) {
                return this.mapPowerCommand.get(key);
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
        this.mapPowerCommand.clear();
    }
    
    public final void loadConfig() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final String path = dataManager.getPath("path_File_Power_Command");
        final File file = FileUtil.getFile((JavaPlugin)this.plugin, path);
        if (!file.exists()) {
            FileUtil.saveResource((JavaPlugin)this.plugin, path);
        }
        this.mapPowerCommand.putAll(com.lymeowz.item.config.PowerCommandDefinitions.read(
                FileUtil.getFileConfiguration(file)));
    }

    private final void moveOldFile() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final String pathSource = "command.yml";
        final String pathTarget = dataManager.getPath("path_File_Power_Command");
        final File fileSource = FileUtil.getFile((JavaPlugin)this.plugin, "command.yml");
        final File fileTarget = FileUtil.getFile((JavaPlugin)this.plugin, pathTarget);
        if (fileSource.exists()) {
            FileUtil.moveFileSilent(fileSource, fileTarget);
        }
    }
}
