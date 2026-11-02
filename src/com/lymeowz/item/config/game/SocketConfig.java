// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.config.game;

import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import com.lymeowz.item.manager.plugin.DataManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import com.lymeowz.item.api.builder.socket.SocketGemsProperties;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import java.util.List;
import com.lymeowz.lib.utility.EquipmentUtil;
import com.lymeowz.lib.core.enums.main.RomanNumber;
import com.lymeowz.item.api.builder.socket.SocketGems;
import com.lymeowz.lib.core.enums.branch.MaterialEnum;
import com.lymeowz.lib.utility.MathUtil;
import com.lymeowz.lib.utility.EnchantmentUtil;
import com.lymeowz.lib.utility.MaterialUtil;
import com.lymeowz.lib.utility.TextUtil;
import com.lymeowz.lib.core.enums.main.SlotType;
import org.bukkit.enchantments.Enchantment;
import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.lib.utility.FileUtil;
import com.lymeowz.item.config.plugin.MainConfig;
import java.util.Iterator;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.api.builder.socket.SocketGemsTree;
import java.util.HashMap;
import com.lymeowz.item.builder.handler.HandlerConfig;

public class SocketConfig extends HandlerConfig
{
    private final HashMap<String, SocketGemsTree> mapSocketTree;
    
    public SocketConfig(final MyItems plugin) {
        super(plugin);
        this.mapSocketTree = new HashMap<String, SocketGemsTree>();
    }
    
    public final Collection<String> getSocketIDs() {
        return this.mapSocketTree.keySet();
    }
    
    public final Collection<SocketGemsTree> getSocketTreeBuilds() {
        return this.mapSocketTree.values();
    }
    
    public final SocketGemsTree getSocketTree(final String id) {
        for (final String key : this.getSocketIDs()) {
            if (key.equalsIgnoreCase(id)) {
                return this.mapSocketTree.get(key);
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
        this.mapSocketTree.clear();
    }
    
    private final void loadConfig() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final MainConfig mainConfig = MainConfig.getInstance();
        final String path = dataManager.getPath("Path_File_Socket");
        final File file = FileUtil.getFile((JavaPlugin)this.plugin, path);
        if (!file.exists()) {
            FileUtil.saveResource((JavaPlugin)this.plugin, path);
        }
        this.mapSocketTree.putAll(com.lymeowz.item.config.SocketDefinitions.read(
                FileUtil.getFileConfiguration(file), mainConfig));
    }

    private final void moveOldFile() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final String pathSource = "socket.yml";
        final String pathTarget = dataManager.getPath("Path_File_Socket");
        final File fileSource = FileUtil.getFile((JavaPlugin)this.plugin, "socket.yml");
        final File fileTarget = FileUtil.getFile((JavaPlugin)this.plugin, pathTarget);
        if (fileSource.exists()) {
            FileUtil.moveFileSilent(fileSource, fileTarget);
        }
    }
}
