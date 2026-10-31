package com.lymeowz.item.element;

/** Immutable projection of one original potion grade/chance/duration scale. */
public record ElementEffect(int grade, double chanceScale, double durationScale) { }
