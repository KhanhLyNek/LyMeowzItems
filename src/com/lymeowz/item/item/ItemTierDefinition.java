package com.lymeowz.item.item;

/** Read-only view of a live, legacy item tier builder. */
public interface ItemTierDefinition {
    String id();
    String name();
    String prefix();
}
