package com.lymeowz.item.manager;

import com.lymeowz.item.element.ElementDefinition;
import java.util.Collection;

/** Read-only view over the original configured element manager. */
public interface ElementRegistry {
    Collection<String> ids();
    ElementDefinition find(String id);
}
