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
import api.praya.combatstamina.builder.event.PlayerStaminaMaxChangeEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerPlayerStaminaMaxChange extends HandlerEvent implements Listener
{
    public ListenerPlayerStaminaMaxChange(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void eventPlayerStaminaMaxChange(final PlayerStaminaMaxChangeEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final LoreStatsManager statsManager = gameManager.getStatsManager();
        final SocketManager socketManager = gameManager.getSocketManager();
        if (!event.isCancelled()) {
            final Player player = event.getPlayer();
            final PlayerStaminaMaxChangeEvent.StaminaMaxModifierEnum staminaMaxType = PlayerStaminaMaxChangeEvent.StaminaMaxModifierEnum.BONUS;
            final LoreStatsArmor statsBuild = statsManager.getLoreStatsArmor((LivingEntity)player);
            final SocketGemsProperties socketBuild = socketManager.getSocketProperties((LivingEntity)player);
            final double staminaMaxStats = statsBuild.getStaminaMax();
            final double staminaMaxSocket = socketBuild.getStaminaMax();
            final double staminaMaxBase = event.getOriginalMaxStamina(staminaMaxType);
            final double staminaMaxResult = staminaMaxStats + staminaMaxSocket + staminaMaxBase;
            event.setMaxStamina(staminaMaxType, staminaMaxResult);
        }
    }
}
