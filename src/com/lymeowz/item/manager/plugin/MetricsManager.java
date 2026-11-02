// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.manager.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.metrics.BStats;
import com.lymeowz.item.builder.handler.HandlerManager;

public class MetricsManager extends HandlerManager
{
    private BStats metricsBStats;
    
    protected MetricsManager(final MyItems plugin) {
        super(plugin);
        this.metricsBStats = new BStats(plugin);
    }
    
    public final BStats getMetricsBStats() {
        return this.metricsBStats;
    }
}
