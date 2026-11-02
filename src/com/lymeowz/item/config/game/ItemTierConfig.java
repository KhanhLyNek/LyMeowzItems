// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.config.game;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import com.lymeowz.item.manager.plugin.DataManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import com.lymeowz.item.api.builder.lorestats.LoreStatsUniversal;
import com.lymeowz.item.api.builder.lorestats.LoreStatsArmor;
import com.lymeowz.item.api.builder.lorestats.LoreStatsWeapon;
import com.lymeowz.lib.utility.TextUtil;
import com.lymeowz.item.api.builder.lorestats.LoreStatsModifier;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.lib.utility.FileUtil;
import java.util.Iterator;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.api.builder.item.ItemTier;
import java.util.HashMap;
import com.lymeowz.item.builder.handler.HandlerConfig;

public class ItemTierConfig extends HandlerConfig
{
    private final HashMap<String, ItemTier> mapTier;
    
    public ItemTierConfig(final MyItems plugin) {
        super(plugin);
        this.mapTier = new HashMap<String, ItemTier>();
    }
    
    public final Collection<String> getItemTierIDs() {
        return this.mapTier.keySet();
    }
    
    public final Collection<ItemTier> getItemTiers() {
        return this.mapTier.values();
    }
    
    public final ItemTier getItemTier(final String id) {
        for (final String key : this.getItemTierIDs()) {
            if (key.equalsIgnoreCase(id)) {
                return this.mapTier.get(key);
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
        this.mapTier.clear();
    }
    
    private final void loadConfig() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        this.mapTier.putAll(com.lymeowz.item.config.ItemTierDefinitions.load(
                this.plugin, dataManager.getPath("Path_File_Item_Tier"),
                dataManager.getPath("Path_Folder_Item_Tier")));
    }

    private final void moveOldFile() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final String pathSource_1 = "item_tier.yml";
        final String pathSource_2 = "Configuration/item_tier.yml";
        final String pathTarget = dataManager.getPath("Path_File_Item_Tier");
        final File fileSource_1 = FileUtil.getFile((JavaPlugin)this.plugin, "item_tier.yml");
        final File fileSource_2 = FileUtil.getFile((JavaPlugin)this.plugin, "Configuration/item_tier.yml");
        final File fileTarget = FileUtil.getFile((JavaPlugin)this.plugin, pathTarget);
        if (fileSource_1.exists()) {
            FileUtil.moveFileSilent(fileSource_1, fileTarget);
        }
        else if (fileSource_2.exists()) {
            FileUtil.moveFileSilent(fileSource_2, fileTarget);
        }
    }
}
