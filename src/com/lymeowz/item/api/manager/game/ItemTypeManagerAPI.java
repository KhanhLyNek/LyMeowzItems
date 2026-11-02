// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.manager.game;

import com.lymeowz.item.manager.game.GameManager;
import com.lymeowz.item.manager.game.ItemTypeManager;
import com.lymeowz.item.api.builder.item.ItemType;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.builder.handler.HandlerManager;

public class ItemTypeManagerAPI extends HandlerManager
{
    protected ItemTypeManagerAPI(final MyItems plugin) {
        super(plugin);
    }
    
    public final Collection<String> getItemTypeIDs() {
        return this.getItemTypeManager().getItemTypeIDs();
    }
    
    public final Collection<ItemType> getItemTypes() {
        return this.getItemTypeManager().getItemTypes();
    }
    
    public final ItemType getItemType(final String id) {
        return this.getItemTypeManager().getItemType(id);
    }
    
    public final boolean isItemTypeExists(final String id) {
        return this.getItemTypeManager().isItemTypeExists(id);
    }
    
    private final ItemTypeManager getItemTypeManager() {
        final GameManager gameManager = this.plugin.getGameManager();
        final ItemTypeManager itemTypeManager = gameManager.getItemTypeManager();
        return itemTypeManager;
    }
}
