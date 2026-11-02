// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.config.game;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import com.lymeowz.item.manager.game.ItemTierManager;
import com.lymeowz.item.manager.game.ItemTypeManager;
import com.lymeowz.item.manager.plugin.DataManager;
import com.lymeowz.item.manager.game.GameManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import com.lymeowz.lib.utility.TextUtil;
import com.lymeowz.item.api.builder.item.ItemGeneratorTier;
import com.lymeowz.item.api.builder.item.ItemTier;
import com.lymeowz.item.api.builder.item.ItemGeneratorType;
import com.lymeowz.item.api.builder.item.ItemType;
import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.lib.utility.FileUtil;
import java.util.Iterator;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.api.builder.item.ItemGenerator;
import java.util.HashMap;
import com.lymeowz.item.builder.handler.HandlerConfig;

public class ItemGeneratorConfig extends HandlerConfig
{
    private final HashMap<String, ItemGenerator> mapItemGenerator;
    
    public ItemGeneratorConfig(final MyItems plugin) {
        super(plugin);
        this.mapItemGenerator = new HashMap<String, ItemGenerator>();
    }
    
    public final Collection<String> getItemGeneratorIDs() {
        return this.mapItemGenerator.keySet();
    }
    
    public final Collection<ItemGenerator> getItemGenerators() {
        return this.mapItemGenerator.values();
    }
    
    public final ItemGenerator getItemGenerator(final String nameid) {
        for (final String key : this.getItemGeneratorIDs()) {
            if (key.equalsIgnoreCase(nameid)) {
                return this.mapItemGenerator.get(key);
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
        this.mapItemGenerator.clear();
    }
    
    private final void loadConfig() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final GameManager gameManager = this.plugin.getGameManager();
        final DataManager dataManager = pluginManager.getDataManager();
        this.mapItemGenerator.putAll(com.lymeowz.item.config.ItemGeneratorDefinitions.load(
                this.plugin, dataManager.getPath("Path_File_Item_Generator"),
                dataManager.getPath("Path_Folder_Item_Generator"),
                gameManager.getItemTypeManager(), gameManager.getItemTierManager()));
    }

    private final void moveOldFile() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final String pathSource_1 = "item_generator.yml";
        final String pathSource_2 = "Configuration/item_generator.yml";
        final String pathTarget = dataManager.getPath("Path_File_Item_Generator");
        final File fileSource_1 = FileUtil.getFile((JavaPlugin)this.plugin, "item_generator.yml");
        final File fileSource_2 = FileUtil.getFile((JavaPlugin)this.plugin, "Configuration/item_generator.yml");
        final File fileTarget = FileUtil.getFile((JavaPlugin)this.plugin, pathTarget);
        if (fileSource_1.exists()) {
            FileUtil.moveFileSilent(fileSource_1, fileTarget);
        }
        else if (fileSource_2.exists()) {
            FileUtil.moveFileSilent(fileSource_2, fileTarget);
        }
    }
}
