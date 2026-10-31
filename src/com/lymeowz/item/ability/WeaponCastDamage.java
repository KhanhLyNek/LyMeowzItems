package com.lymeowz.item.ability;

/** Read-only ability cast damage contract; unchanged legacy method shapes. */
public interface WeaponCastDamage {
    double getCastBonusDamage(int grade);
    double getCastPercentDamage(int grade);
}
