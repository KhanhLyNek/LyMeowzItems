// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.builder.item;

import java.util.List;

public class ItemSetBonus implements com.lymeowz.item.itemset.ItemSetBonusDefinition
{
    private final int amountID;
    private final List<String> description;
    private final ItemSetBonusEffect effect;
    
    public ItemSetBonus(final int amountID, final List<String> description, final ItemSetBonusEffect effect) {
        this.amountID = amountID;
        this.description = description;
        this.effect = effect;
    }
    
    public final int getAmountID() {
        return this.amountID;
    }
    
    public final List<String> getDescription() {
        return this.description;
    }
    
    @Override
    public final int requiredPieces() {
        return getAmountID();
    }

    @Override
    public final List<String> descriptionSnapshot() {
        return this.description == null ? java.util.List.of() :
                java.util.Collections.unmodifiableList(new java.util.ArrayList<>(this.description));
    }

    public final ItemSetBonusEffect getEffect() {
        return this.effect;
    }
}
