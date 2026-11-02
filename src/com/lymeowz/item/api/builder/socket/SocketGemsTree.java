// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.builder.socket;

import java.util.HashMap;
import com.lymeowz.lib.core.enums.main.SlotType;

public class SocketGemsTree implements com.lymeowz.item.socket.SocketDefinition
{
    private final String gems;
    private final int maxGrade;
    private final SlotType typeItem;
    private final HashMap<Integer, SocketGems> mapSocket;
    
    public SocketGemsTree(final String gems, final int maxGrade, final SlotType typeItem, final HashMap<Integer, SocketGems> mapSocket) {
        this.gems = gems;
        this.maxGrade = maxGrade;
        this.typeItem = typeItem;
        this.mapSocket = mapSocket;
    }
    
    public final String getGems() {
        return this.gems;
    }
    
    public final int getMaxGrade() {
        return this.maxGrade;
    }
    
    public final SlotType getTypeItem() {
        return this.typeItem;
    }
    
    public final HashMap<Integer, SocketGems> getMapSocket() {
        return this.mapSocket;
    }
    
    @Override
    public final String id() {
        return getGems();
    }

    @Override
    public final int maxGrade() {
        return getMaxGrade();
    }

    @Override
    public final String applicableItemType() {
        return this.typeItem == null ? null : this.typeItem.name();
    }

    @Override
    public final java.util.Collection<Integer> availableGrades() {
        return java.util.Collections.unmodifiableList(
                new java.util.ArrayList<>(this.mapSocket.keySet()));
    }

    @Override
    public final com.lymeowz.item.socket.SocketGemDefinition findGrade(final int grade) {
        return getSocketBuild(grade);
    }

    public final boolean isGemsAvailable(final int grade) {
        return this.mapSocket.containsKey(grade);
    }
    
    public final SocketGems getSocketBuild(final int grade) {
        return this.isGemsAvailable(grade) ? this.mapSocket.get(grade) : null;
    }
}
