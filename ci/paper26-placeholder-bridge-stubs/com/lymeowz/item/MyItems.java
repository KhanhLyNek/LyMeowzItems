package com.lymeowz.item;

import com.lymeowz.item.manager.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/** CI-only plugin signature: not in MyItems production source set. */
public class MyItems extends JavaPlugin {
    public PluginManager getPluginManager() {
        return null;
    }
}
