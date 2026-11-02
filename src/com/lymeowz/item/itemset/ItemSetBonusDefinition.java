package com.lymeowz.item.itemset;

import java.util.List;

/** Bonus threshold and an immutable detached description list. */
public interface ItemSetBonusDefinition {
    int requiredPieces();
    List<String> descriptionSnapshot();
}
