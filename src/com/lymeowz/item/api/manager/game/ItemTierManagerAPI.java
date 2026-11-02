// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.manager.game;

import com.lymeowz.item.manager.game.GameManager;
import com.lymeowz.item.manager.game.ItemTierManager;
import com.lymeowz.item.api.builder.item.ItemTier;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.builder.handler.HandlerManager;

public class ItemTierManagerAPI extends HandlerManager
{
    protected ItemTierManagerAPI(final MyItems plugin) {
        super(plugin);
    }
    
    public final Collection<String> getItemTierIDs() {
        return this.getItemTierManager().getItemTierIDs();
    }
    
    public final Collection<ItemTier> getItemTiers() {
        return this.getItemTierManager().getItemTiers();
    }
    
    public final ItemTier getItemTier(final String id) {
        return this.getItemTierManager().getItemTier(id);
    }
    
    public final boolean isItemTierExists(final String id) {
        return this.getItemTierManager().isItemTierExists(id);
    }
    
    private final ItemTierManager getItemTierManager() {
        final GameManager gameManager = this.plugin.getGameManager();
        final ItemTierManager itemTierManager = gameManager.getItemTierManager();
        return itemTierManager;
    }
}
