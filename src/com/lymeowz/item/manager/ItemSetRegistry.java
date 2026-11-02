package com.lymeowz.item.manager;

import com.lymeowz.item.itemset.ItemSetDefinition;
import java.util.Collection;

/** Read-only facade over the original live ItemSetManager. */
public interface ItemSetRegistry {
    Collection<String> ids();
    ItemSetDefinition find(String id);
}
