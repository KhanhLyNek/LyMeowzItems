// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.config.game;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import com.lymeowz.item.manager.plugin.DataManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import com.lymeowz.item.api.builder.lorestats.LoreStatsUniversal;
import com.lymeowz.item.api.builder.lorestats.LoreStatsArmor;
import com.lymeowz.item.api.builder.lorestats.LoreStatsWeapon;
import com.lymeowz.lib.core.enums.main.TagsAttribute;
import com.lymeowz.lib.utility.EnchantmentUtil;
import com.lymeowz.lib.utility.MaterialUtil;
import com.lymeowz.item.api.builder.lorestats.LoreStatsModifier;
import com.lymeowz.item.api.builder.item.ItemTypeNBT;
import com.lymeowz.lib.core.enums.main.Slot;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.lib.utility.FileUtil;
import java.util.Iterator;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.api.builder.item.ItemType;
import java.util.HashMap;
import com.lymeowz.item.builder.handler.HandlerConfig;

public class ItemTypeConfig extends HandlerConfig
{
    private final HashMap<String, ItemType> mapType;
    
    public ItemTypeConfig(final MyItems plugin) {
        super(plugin);
        this.mapType = new HashMap<String, ItemType>();
    }
    
    public final Collection<String> getItemTypeIDs() {
        return this.mapType.keySet();
    }
    
    public final Collection<ItemType> getItemTypes() {
        return this.mapType.values();
    }
    
    public final ItemType getItemType(final String id) {
        for (final String key : this.getItemTypeIDs()) {
            if (key.equalsIgnoreCase(id)) {
                return this.mapType.get(key);
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
        this.mapType.clear();
    }
    
    private final void loadConfig() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        this.mapType.putAll(com.lymeowz.item.config.ItemTypeDefinitions.load(
                this.plugin, dataManager.getPath("Path_File_Item_Type"),
                dataManager.getPath("Path_Folder_Item_Type")));
    }

    private final void moveOldFile() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final String pathSource_1 = "item_type.yml";
        final String pathSource_2 = "Configuration/item_type.yml";
        final String pathTarget = dataManager.getPath("Path_File_Item_Type");
        final File fileSource_1 = FileUtil.getFile((JavaPlugin)this.plugin, "item_type.yml");
        final File fileSource_2 = FileUtil.getFile((JavaPlugin)this.plugin, "Configuration/item_type.yml");
        final File fileTarget = FileUtil.getFile((JavaPlugin)this.plugin, pathTarget);
        if (fileSource_1.exists()) {
            FileUtil.moveFileSilent(fileSource_1, fileTarget);
        }
        else if (fileSource_2.exists()) {
            FileUtil.moveFileSilent(fileSource_2, fileTarget);
        }
    }
}
