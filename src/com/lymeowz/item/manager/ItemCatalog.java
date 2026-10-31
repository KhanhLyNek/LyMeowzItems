package com.lymeowz.item.manager;

import java.util.Collection;
import org.bukkit.inventory.ItemStack;

/**
 * New namespace's safe read-only item database view. ItemStack reads use
 * the original ItemConfig.getItem, which clones stored item instances.
 */
public interface ItemCatalog {
    Collection<String> ids();
    ItemStack find(String id);
}
