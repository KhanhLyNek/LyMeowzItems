// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.manager.game;

import com.lymeowz.item.api.builder.item.ItemType;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.config.game.ItemTypeConfig;
import com.lymeowz.item.builder.handler.HandlerManager;

public class ItemTypeManager extends HandlerManager implements com.lymeowz.item.manager.ItemTypeRegistry
{
    private final ItemTypeConfig itemTypeConfig;
    
    protected ItemTypeManager(final MyItems plugin) {
        super(plugin);
        this.itemTypeConfig = new ItemTypeConfig(plugin);
    }
    
    public final ItemTypeConfig getItemTypeConfig() {
        return this.itemTypeConfig;
    }
    
    public final Collection<String> getItemTypeIDs() {
        return this.getItemTypeConfig().getItemTypeIDs();
    }
    
    public final Collection<ItemType> getItemTypes() {
        return this.getItemTypeConfig().getItemTypes();
    }
    
    public final ItemType getItemType(final String id) {
        return this.getItemTypeConfig().getItemType(id);
    }
    
    @Override
    public final java.util.Collection<String> ids() {
        return java.util.Collections.unmodifiableList(new java.util.ArrayList<>(getItemTypeIDs()));
    }

    @Override
    public final com.lymeowz.item.item.ItemTypeDefinition find(final String id) {
        return id == null ? null : getItemType(id);
    }

    public final boolean isItemTypeExists(final String id) {
        return this.getItemType(id) != null;
    }
}
