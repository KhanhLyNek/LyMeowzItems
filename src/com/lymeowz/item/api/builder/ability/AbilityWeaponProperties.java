// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.builder.ability;

import com.lymeowz.lib.utility.MathUtil;

public class AbilityWeaponProperties implements com.lymeowz.item.ability.WeaponProperties
{
    private final int maxGrade;
    private final int baseDurationEffect;
    private final int scaleDurationEffect;
    private final double scaleBaseBonusDamage;
    private final double scaleBasePercentDamage;
    private final double scaleCastBonusDamage;
    private final double scaleCastPercentDamage;
    
    public AbilityWeaponProperties(final int maxGrade, final int baseDurationEffect, final int scaleDurationEffect, final double scaleBaseBonusDamage, final double scaleBasePercentDamage, final double scaleCastBonusDamage, final double scaleCastPercentDamage) {
        this.maxGrade = maxGrade;
        this.baseDurationEffect = baseDurationEffect;
        this.scaleDurationEffect = scaleDurationEffect;
        this.scaleBaseBonusDamage = scaleBaseBonusDamage;
        this.scaleBasePercentDamage = scaleBasePercentDamage;
        this.scaleCastBonusDamage = scaleCastBonusDamage;
        this.scaleCastPercentDamage = scaleCastPercentDamage;
    }
    
    public final int getMaxGrade() {
        return this.maxGrade;
    }
    
    public final int getBaseDurationEffect() {
        return this.baseDurationEffect;
    }
    
    public final int getScaleDurationEffect() {
        return this.scaleDurationEffect;
    }
    
    public final double getScaleBaseBonusDamage() {
        return this.scaleBaseBonusDamage;
    }
    
    public final double getScaleBasePercentDamage() {
        return this.scaleBasePercentDamage;
    }
    
    public final double getScaleCastBonusDamage() {
        return this.scaleCastBonusDamage;
    }
    
    public final double getScaleCastPercentDamage() {
        return this.scaleCastPercentDamage;
    }
    
    public final int getTotalDuration(final int grade) {
        return com.lymeowz.item.ability.AbilityScaling.duration(grade, getMaxGrade(),
                getBaseDurationEffect(), getScaleDurationEffect());
    }

    public final double getTotalBaseDamage(final int grade, final double damage) {
        return com.lymeowz.item.ability.AbilityScaling.baseDamage(grade, getMaxGrade(),
                getScaleBaseBonusDamage(), getScaleBasePercentDamage(), damage);
    }

    public final double getTotalCastDamage(final int grade, final double damage) {
        return com.lymeowz.item.ability.AbilityScaling.castDamage(grade, getMaxGrade(),
                getScaleCastBonusDamage(), getScaleCastPercentDamage(), damage);
    }

}
