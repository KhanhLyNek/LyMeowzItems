package com.lymeowz.item.socket;

import java.util.Collection;

/** A live socket family with immutable grade-ID snapshots. */
public interface SocketDefinition {
    String id();
    int maxGrade();
    String applicableItemType();
    Collection<Integer> availableGrades();
    SocketGemDefinition findGrade(int grade);
}
