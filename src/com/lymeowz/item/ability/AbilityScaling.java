package com.lymeowz.item.ability;

import com.lymeowz.lib.utility.MathUtil;

/**
 * Original weapon property arithmetic relocated without changing the clamping
 * order, integer overflow behavior, damage scaling, grades or balance.
 * Never change these operations without versioned gameplay regression tests.
 */
public final class AbilityScaling {
    private AbilityScaling() { }

    public static int duration(int grade, int maxGrade, int baseDuration, int scaleDuration) {
        final int finalGrade = MathUtil.limitInteger(grade, 0, maxGrade);
        final int totalDuration = baseDuration + finalGrade * scaleDuration;
        return totalDuration;
    }

    public static double baseDamage(int grade, int maxGrade, double scaleBonusDamage,
                                    double scalePercentDamage, double damage) {
        final int finalGrade = MathUtil.limitInteger(grade, 0, maxGrade);
        final double totalDamage = finalGrade * scaleBonusDamage
                + damage * (finalGrade * scalePercentDamage / 100.0);
        return totalDamage;
    }

    public static double castDamage(int grade, int maxGrade, double scaleBonusDamage,
                                    double scalePercentDamage, double damage) {
        final int finalGrade = MathUtil.limitInteger(grade, 0, maxGrade);
        final double totalDamage = finalGrade * scaleBonusDamage
                + damage * (finalGrade * scalePercentDamage / 100.0);
        return totalDamage;
    }
}
