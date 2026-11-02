// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.manager.player;

import com.lymeowz.item.manager.player.PlayerManager;
import com.lymeowz.item.manager.player.PlayerItemStatsManager;
import com.lymeowz.item.api.builder.item.ItemStatsArmor;
import com.lymeowz.item.api.builder.item.ItemStatsWeapon;
import org.bukkit.entity.Player;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.builder.handler.HandlerManager;

public class PlayerItemStatsManagerAPI extends HandlerManager
{
    protected PlayerItemStatsManagerAPI(final MyItems plugin) {
        super(plugin);
    }
    
    public final ItemStatsWeapon getItemStatsWeapon(final Player player) {
        return this.getPlayerItemStatsManager().getItemStatsWeapon(player);
    }
    
    public final ItemStatsArmor getItemStatsArmor(final Player player) {
        return this.getPlayerItemStatsManager().getItemStatsArmor(player);
    }
    
    private final PlayerItemStatsManager getPlayerItemStatsManager() {
        final PlayerManager playerManager = this.plugin.getPlayerManager();
        final PlayerItemStatsManager playerItemStatsManager = playerManager.getPlayerItemStatsManager();
        return playerItemStatsManager;
    }
}
