package com.lymeowz.item.manager;

import com.lymeowz.item.power.PowerCommandDefinition;
import java.util.Collection;

/** Public read-only projection of the original command-power manager. */
public interface PowerCommandRegistry {
    Collection<String> ids();
    PowerCommandDefinition find(String id);
}
