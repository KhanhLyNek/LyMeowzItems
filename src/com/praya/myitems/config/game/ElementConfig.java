// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.myitems.config.game;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import com.praya.myitems.manager.plugin.DataManager;
import com.praya.myitems.manager.plugin.PluginManager;
import api.praya.myitems.builder.element.ElementPotion;
import api.praya.myitems.builder.element.ElementBoost;
import com.praya.agarthalib.utility.MathUtil;
import com.praya.agarthalib.utility.PotionUtil;
import com.praya.agarthalib.utility.TextUtil;
import api.praya.myitems.builder.potion.PotionProperties;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;
import com.praya.agarthalib.utility.FileUtil;
import java.util.Iterator;
import java.util.Collection;
import com.praya.myitems.MyItems;
import api.praya.myitems.builder.element.Element;
import java.util.HashMap;
import com.praya.myitems.builder.handler.HandlerConfig;

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
