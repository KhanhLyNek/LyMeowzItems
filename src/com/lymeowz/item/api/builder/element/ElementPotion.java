// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.builder.element;

import com.lymeowz.item.api.builder.potion.PotionProperties;
import org.bukkit.potion.PotionEffectType;
import java.util.HashMap;

public class ElementPotion implements com.lymeowz.item.element.ElementPotionDefinition
{
    private final HashMap<PotionEffectType, PotionProperties> potionAttacker;
    private final HashMap<PotionEffectType, PotionProperties> potionVictims;
    
    public ElementPotion(final HashMap<PotionEffectType, PotionProperties> potionAttacker, final HashMap<PotionEffectType, PotionProperties> potionVictims) {
        this.potionAttacker = potionAttacker;
        this.potionVictims = potionVictims;
    }
    
    @Override
    public final java.util.Map<PotionEffectType, com.lymeowz.item.element.ElementEffect> attackerEffects() {
        return snapshot(this.potionAttacker);
    }

    @Override
    public final java.util.Map<PotionEffectType, com.lymeowz.item.element.ElementEffect> victimEffects() {
        return snapshot(this.potionVictims);
    }

    private static java.util.Map<PotionEffectType, com.lymeowz.item.element.ElementEffect> snapshot(
            final java.util.Map<PotionEffectType, PotionProperties> source) {
        final java.util.Map<PotionEffectType, com.lymeowz.item.element.ElementEffect> copy =
                new java.util.HashMap<>();
        for (final java.util.Map.Entry<PotionEffectType, PotionProperties> entry : source.entrySet()) {
            final PotionProperties attributes = entry.getValue();
            copy.put(entry.getKey(), new com.lymeowz.item.element.ElementEffect(
                    attributes.getGrade(), attributes.getScaleChance(),
                    attributes.getScaleDuration()));
        }
        return java.util.Collections.unmodifiableMap(copy);
    }

    public final HashMap<PotionEffectType, PotionProperties> getPotionAttacker() {
        return this.potionAttacker;
    }
    
    public final HashMap<PotionEffectType, PotionProperties> getPotionVictims() {
        return this.potionVictims;
    }
}
