package com.lymeowz.item.item;

import java.util.List;
import java.util.Map;

/**
 * Read-only snapshots of an existing configured item generator.
 * Gameplay still uses the original live ItemGenerator instance.
 */
public interface ItemGeneratorDefinition {
    String id();
    String displayName();
    boolean unbreakable();
    List<String> loreLines();
    List<String> flagNames();
    Map<String, Integer> typeWeights();
    Map<String, Integer> tierWeights();
}
