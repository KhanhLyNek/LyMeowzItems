// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.builder.item;

import com.lymeowz.item.api.builder.lorestats.LoreStatsModifier;

public class ItemTier implements com.lymeowz.item.item.ItemTierDefinition
{
    private final String id;
    private final String name;
    private final String prefix;
    private final LoreStatsModifier statsModifier;
    
    public ItemTier(final String id, final String name, final String prefix, final LoreStatsModifier statsModifier) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.statsModifier = statsModifier;
    }
    
    public final String getId() {
        return this.id;
    }
    
    public final String getPrefix() {
        return this.prefix;
    }
    
    public final String getName() {
        return this.name;
    }
    
    @Override
    public final String id() { return getId(); }

    @Override
    public final String name() { return getName(); }

    @Override
    public final String prefix() { return getPrefix(); }

    public final LoreStatsModifier getStatsModifier() {
        return this.statsModifier;
    }
}
