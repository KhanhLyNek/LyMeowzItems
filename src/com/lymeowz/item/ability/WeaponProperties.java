package com.lymeowz.item.ability;

/** Immutable configuration view, exposed by legacy AbilityWeaponProperties. */
public interface WeaponProperties {
    int getMaxGrade();
    int getBaseDurationEffect();
    int getScaleDurationEffect();
    double getScaleBaseBonusDamage();
    double getScaleBasePercentDamage();
    double getScaleCastBonusDamage();
    double getScaleCastPercentDamage();
    int getTotalDuration(int grade);
    double getTotalBaseDamage(int grade, double damage);
    double getTotalCastDamage(int grade, double damage);
}
