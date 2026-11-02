// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.support;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.lymeowz.item.api.builder.socket.SocketGemsProperties;
import com.lymeowz.item.api.builder.lorestats.LoreStatsArmor;
import org.bukkit.entity.Player;
import com.lymeowz.item.manager.game.SocketManager;
import com.lymeowz.item.manager.game.LoreStatsManager;
import com.lymeowz.item.manager.game.GameManager;
import org.bukkit.entity.LivingEntity;
import com.lymeowz.item.config.plugin.MainConfig;
import com.lymeowz.lib.api.builder.event.PlayerHealthMaxChangeEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerPlayerHealthMaxChange extends HandlerEvent implements Listener
{
    public ListenerPlayerHealthMaxChange(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void eventPlayerHealthMaxChange(final PlayerHealthMaxChangeEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final LoreStatsManager statsManager = gameManager.getStatsManager();
        final SocketManager socketManager = gameManager.getSocketManager();
        final MainConfig mainConfig = MainConfig.getInstance();
        final boolean enableMaxHealth = mainConfig.isStatsEnableMaxHealth();
        if (!event.isCancelled() && enableMaxHealth) {
            final Player player = event.getPlayer();
            final LoreStatsArmor statsBuild = statsManager.getLoreStatsArmor((LivingEntity)player);
            final SocketGemsProperties socketBuild = socketManager.getSocketProperties((LivingEntity)player);
            final double healthStats = statsBuild.getHealth();
            final double healthSocket = socketBuild.getHealth();
            final double healthBase = event.getMaxHealth();
            final double healthResult = healthStats + healthSocket + healthBase;
            event.setMaxHealth(healthResult);
        }
    }
}
