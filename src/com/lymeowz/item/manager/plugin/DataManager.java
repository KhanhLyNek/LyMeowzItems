// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.manager.plugin;

import java.util.ArrayList;
import java.util.List;
import com.lymeowz.lib.core.builder.main.DataBuild;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.config.plugin.DataConfig;
import com.lymeowz.item.builder.handler.HandlerManager;

public class DataManager extends HandlerManager
{
    private final DataConfig dataConfig;
    
    protected DataManager(final MyItems plugin) {
        super(plugin);
        this.dataConfig = new DataConfig(plugin);
    }
    
    public final DataConfig getDataConfig() {
        return this.dataConfig;
    }
    
    public final Collection<String> getDataIDs() {
        return this.getDataConfig().getDataIDs();
    }
    
    public final Collection<DataBuild> getDataBuilds() {
        return this.getDataConfig().getDataBuilds();
    }
    
    public final DataBuild getData(final String id) {
        return this.getDataConfig().getData(id);
    }
    
    public final boolean isDataExists(final String id) {
        return this.getData(id) != null;
    }
    
    public final List<String> getListPath(final String id) {
        final DataBuild data = this.getData(id);
        return (data != null) ? data.getListPath() : new ArrayList<String>();
    }
    
    public final String getPath(final String id) {
        final DataBuild data = this.getData(id);
        return (data != null) ? data.getPath() : "";
    }
}
