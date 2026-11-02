// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.builder.item;

import java.util.Collection;
import com.lymeowz.item.api.builder.ability.AbilityWeapon;
import java.util.HashMap;

public class ItemSetBonusEffectEntity
{
    private final ItemSetBonusEffectStats effectStats;
    private final HashMap<AbilityWeapon, Integer> mapAbilityWeapon;
    
    public ItemSetBonusEffectEntity(final ItemSetBonusEffectStats effectStats, final HashMap<AbilityWeapon, Integer> mapAbilityWeapon) {
        this.effectStats = effectStats;
        this.mapAbilityWeapon = mapAbilityWeapon;
    }
    
    public final ItemSetBonusEffectStats getEffectStats() {
        return this.effectStats;
    }
    
    public final Collection<AbilityWeapon> getAllAbilityWeapon() {
        return this.mapAbilityWeapon.keySet();
    }
    
    public final int getGradeAbilityWeapon(final AbilityWeapon abilityWeapon) {
        return this.mapAbilityWeapon.get(abilityWeapon);
    }
}
