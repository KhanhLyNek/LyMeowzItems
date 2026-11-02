// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.manager.game;

import java.util.Iterator;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import java.util.Collection;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.config.game.ItemConfig;
import com.lymeowz.item.builder.handler.HandlerManager;

public class ItemManager extends HandlerManager implements com.lymeowz.item.manager.ItemCatalog
{
    private final ItemConfig itemConfig;
    
    protected ItemManager(final MyItems plugin) {
        super(plugin);
        this.itemConfig = new ItemConfig(plugin);
    }
    
    public final ItemConfig getItemConfig() {
        return this.itemConfig;
    }
    
    public final Collection<String> getItemIDs() {
        return this.getItemConfig().getItemIDs();
    }
    
    public final ItemStack getItem(final String nameid) {
        return this.getItemConfig().getItem(nameid);
    }

    @Override
    public final java.util.Collection<String> ids() {
        return java.util.Collections.unmodifiableList(new java.util.ArrayList<>(getItemIDs()));
    }

    @Override
    public final ItemStack find(final String id) {
        return id == null ? null : getItem(id);
    }
    
    public final Collection<ItemStack> getItems() {
        final Collection<ItemStack> items = new ArrayList<ItemStack>();
        for (final String id : this.getItemIDs()) {
            final ItemStack item = this.getItem(id);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }
    
    public final boolean isExist(final String id) {
        return this.getItem(id) != null;
    }
    
    public final String getRawName(final String nameid) {
        for (final String key : this.getItemIDs()) {
            if (key.equalsIgnoreCase(nameid)) {
                return key;
            }
        }
        return null;
    }
}
