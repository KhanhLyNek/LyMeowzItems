// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.manager.player;

import com.lymeowz.item.MyItems;
import com.lymeowz.item.builder.handler.HandlerManager;

public class PlayerManagerAPI extends HandlerManager
{
    private final PlayerItemStatsManagerAPI playerItemStatsManagerAPI;
    
    public PlayerManagerAPI(final MyItems plugin) {
        super(plugin);
        this.playerItemStatsManagerAPI = new PlayerItemStatsManagerAPI(plugin);
    }
    
    public final PlayerItemStatsManagerAPI getPlayerItemStatsManagerAPI() {
        return this.playerItemStatsManagerAPI;
    }
}
