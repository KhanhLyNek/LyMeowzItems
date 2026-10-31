// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.myitems.manager.game;

import api.praya.myitems.builder.item.ItemTier;
import java.util.Collection;
import com.praya.myitems.MyItems;
import com.praya.myitems.config.game.ItemTierConfig;
import com.praya.myitems.builder.handler.HandlerManager;

public class ItemTierManager extends HandlerManager implements com.lymeowz.item.manager.ItemTierRegistry
{
    private final ItemTierConfig itemTierConfig;
    
    protected ItemTierManager(final MyItems plugin) {
        super(plugin);
        this.itemTierConfig = new ItemTierConfig(plugin);
    }
    
    public final ItemTierConfig getItemTierConfig() {
        return this.itemTierConfig;
    }
    
    public final Collection<String> getItemTierIDs() {
        return this.getItemTierConfig().getItemTierIDs();
    }
    
    public final Collection<ItemTier> getItemTiers() {
        return this.getItemTierConfig().getItemTiers();
    }
    
    public final ItemTier getItemTier(final String id) {
        return this.getItemTierConfig().getItemTier(id);
    }
    
    @Override
    public final java.util.Collection<String> ids() {
        return java.util.Collections.unmodifiableList(new java.util.ArrayList<>(getItemTierIDs()));
    }

    @Override
    public final com.lymeowz.item.item.ItemTierDefinition find(final String id) {
        return id == null ? null : getItemTier(id);
    }

    public final boolean isItemTierExists(final String id) {
        return this.getItemTier(id) != null;
    }
}
