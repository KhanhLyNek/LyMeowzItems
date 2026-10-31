package com.lymeowz.item.manager;

import com.lymeowz.item.item.ItemGeneratorDefinition;
import java.util.Collection;

/** Read-only canonical view of the original item generator manager. */
public interface ItemGeneratorRegistry {
    Collection<String> ids();
    ItemGeneratorDefinition find(String id);
}
