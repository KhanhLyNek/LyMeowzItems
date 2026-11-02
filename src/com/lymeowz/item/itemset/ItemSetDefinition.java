package com.lymeowz.item.itemset;

import java.util.Collection;

/** Read-only metadata implemented by the original live ItemSet builder. */
public interface ItemSetDefinition {
    String id();
    String name();
    Collection<Integer> bonusAmounts();
    Collection<String> componentIds();
    ItemSetBonusDefinition findBonus(int amount);
    ItemSetComponentDefinition findComponent(String id);
}
