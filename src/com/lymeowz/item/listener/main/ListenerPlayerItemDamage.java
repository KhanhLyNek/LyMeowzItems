// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.main;

import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import com.lymeowz.lib.core.bridge.unity.BridgeTagsNBT;
import com.lymeowz.item.manager.game.LoreStatsManager;
import com.lymeowz.item.manager.game.GameManager;
import com.lymeowz.item.api.builder.lorestats.LoreStatsEnum;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import org.bukkit.event.player.PlayerItemDamageEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerPlayerItemDamage extends HandlerEvent implements Listener
{
    public ListenerPlayerItemDamage(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void itemDamageEvent(final PlayerItemDamageEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final LoreStatsManager statsManager = gameManager.getStatsManager();
        if (!event.isCancelled()) {
            final BridgeTagsNBT bridgeTagsNBT = Bridge.getBridgeTagsNBT();
            final ItemStack item = event.getItem();
            if (bridgeTagsNBT.isUnbreakable(item) || statsManager.hasLoreStats(item, LoreStatsEnum.DURABILITY)) {
                event.setCancelled(true);
            }
        }
    }
}
