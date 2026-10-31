package com.lymeowz.item.element;

import java.util.Map;

/**
 * New namespace's read-only element builder surface. Concrete legacy
 * Element/ElementBoost/Potion objects keep their binary constructors/getters.
 */
public interface ElementDefinition {
    String loreKey();
    double additionalDamageScale();
    double percentDamageScale();
    Map<String, Double> resistanceSnapshot();
}
