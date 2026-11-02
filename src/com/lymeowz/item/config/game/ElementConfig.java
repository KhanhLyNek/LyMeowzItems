// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.config.game;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import com.lymeowz.item.manager.plugin.DataManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import com.lymeowz.item.api.builder.element.ElementPotion;
import com.lymeowz.item.api.builder.element.ElementBoost;
import com.lymeowz.lib.utility.MathUtil;
import com.lymeowz.lib.utility.PotionUtil;
import com.lymeowz.lib.utility.TextUtil;
import com.lymeowz.item.api.builder.potion.PotionProperties;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.lib.utility.FileUtil;
import java.util.Iterator;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.api.builder.element.Element;
import java.util.HashMap;
import com.lymeowz.item.builder.handler.HandlerConfig;

public class ElementConfig extends HandlerConfig
{
    private final HashMap<String, Element> mapElement;
    
    public ElementConfig(final MyItems plugin) {
        super(plugin);
        this.mapElement = new HashMap<String, Element>();
    }
    
    public final Collection<String> getElements() {
        return this.mapElement.keySet();
    }
    
    public final Collection<Element> getElementBuilds() {
        return this.mapElement.values();
    }
    
    public final Element getElementBuild(final String element) {
        for (final String key : this.getElements()) {
            if (key.equalsIgnoreCase(element)) {
                return this.mapElement.get(key);
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
        this.mapElement.clear();
    }
    
    private final void loadConfig() {
        this.mapElement.putAll(com.lymeowz.item.config.ElementDefinitions.load(this.plugin));
    }

    private final void moveOldFile() {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final DataManager dataManager = pluginManager.getDataManager();
        final String pathSource = "element.yml";
        final String pathTarget = dataManager.getPath("Path_File_Element");
        final File fileSource = FileUtil.getFile((JavaPlugin)this.plugin, "element.yml");
        final File fileTarget = FileUtil.getFile((JavaPlugin)this.plugin, pathTarget);
        if (fileSource.exists()) {
            FileUtil.moveFileSilent(fileSource, fileTarget);
        }
    }
}
