package com.lymeowz.item.itemset;

import java.util.Collection;

/** Original component identity with immutable equipment-slot name snapshots. */
public interface ItemSetComponentDefinition {
    String id();
    String setId();
    String loreKey();
    Collection<String> slotNames();
}
