package com.lymeowz.item.socket;

import org.bukkit.inventory.ItemStack;

/** Immutable metadata view implemented by the live legacy gem builder. */
public interface SocketGemDefinition {
    String socketId();
    int gemGrade();
    String loreKey();
    double successRate();
    String applicableItemType();
    ItemStack itemSnapshot();
}
