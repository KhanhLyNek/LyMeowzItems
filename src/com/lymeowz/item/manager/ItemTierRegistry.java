package com.lymeowz.item.manager;

import com.lymeowz.item.item.ItemTierDefinition;
import java.util.Collection;

/** Read-only projection of the original live ItemTierManager. */
public interface ItemTierRegistry {
    Collection<String> ids();
    ItemTierDefinition find(String id);
}
