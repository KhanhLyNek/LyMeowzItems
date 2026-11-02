// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.myitems.config.game;

import org.bukkit.Material;
import java.util.Set;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import com.praya.myitems.manager.plugin.DataManager;
import com.praya.myitems.manager.plugin.PluginManager;
import api.praya.myitems.builder.item.ItemSetComponentItem;
import com.praya.agarthalib.utility.EnchantmentUtil;
import com.praya.agarthalib.utility.MaterialUtil;
import org.bukkit.enchantments.Enchantment;
import core.praya.agarthalib.enums.main.Slot;
import java.util.HashSet;
import api.praya.myitems.builder.item.ItemSetBonusEffect;
import api.praya.myitems.builder.ability.AbilityItemWeapon;
import org.bukkit.ChatColor;
import com.praya.agarthalib.utility.TextUtil;
import api.praya.myitems.builder.item.ItemSetBonusEffectAbilityWeapon;
import api.praya.myitems.builder.item.ItemSetBonusEffectStats;
import java.util.ArrayList;
import com.praya.agarthalib.utility.MathUtil;
import api.praya.myitems.builder.item.ItemSetComponent;
import api.praya.myitems.builder.item.ItemSetBonus;
import org.bukkit.plugin.java.JavaPlugin;
import com.praya.agarthalib.utility.FileUtil;
import java.util.Iterator;
import java.util.Collection;
import com.praya.myitems.MyItems;
import api.praya.myitems.builder.item.ItemSet;
import java.util.HashMap;
import com.praya.myitems.builder.handler.HandlerConfig;

public class ItemSetConfig extends HandlerConfig
{
    private final HashMap<String, ItemSet> mapItemSet;
    
    public ItemSetConfig(final MyItems plugin) {
        super(plugin);
        this.mapItemSet = new HashMap<String, ItemSet>();
    }
    
    public final Collection<String> getItemSetIDs() {
        return this.mapItemSet.keySet();
    }
    
    public final Collection<ItemSet> getAllItemSet() {
        return this.mapItemSet.values();
    }
    
    public final ItemSet getItemSet(final String id) {
        for (final String key : this.getItemSetIDs()) {
            if (key.equalsIgnoreCase(id)) {
                return this.mapItemSet.get(key);
            }
        }
        return null;
    }
    
    public final void setup() {
        this.reset();
        this.loadConfig();
    }
    
    private final void reset() {
        this.mapItemSet.clear();
    }
    
    private final void loadConfig() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final String pathDefault = dataManager.getPath("Path_File_Item_Set");
        final String pathFolder = dataManager.getPath("Path_Folder_Item_Set");
        final File fileDefault = FileUtil.getFile((JavaPlugin)this.plugin, pathDefault);
        final File fileFolder = FileUtil.getFile((JavaPlugin)this.plugin, pathFolder);
        if (!fileDefault.exists()) {
            FileUtil.saveResource((JavaPlugin)this.plugin, pathDefault);
        }
        final File[] files = fileFolder.listFiles();
        if (files == null) {
            return;
        }
        for (final File file : files) {
            if (!file.isFile()) {
                continue;
            }
            this.mapItemSet.putAll(com.lymeowz.item.config.ItemSetDefinitions.read(
                    FileUtil.getFileConfiguration(file)));
        }
    }
}
