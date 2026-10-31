package com.lymeowz.item.manager;

import com.lymeowz.item.item.ItemTypeDefinition;
import java.util.Collection;

/** Read-only projection of the original live ItemTypeManager. */
public interface ItemTypeRegistry {
    Collection<String> ids();
    ItemTypeDefinition find(String id);
}
