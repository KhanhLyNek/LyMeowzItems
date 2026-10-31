package com.lymeowz.item.ability;

/** Read-only ability base damage contract; unchanged legacy method shapes. */
public interface WeaponBaseDamage {
    double getBaseBonusDamage(int grade);
    double getBasePercentDamage(int grade);
}
