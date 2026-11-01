// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.myitems.config.game;

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
import com.praya.myitems.builder.handler.HandlerConfig;

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
