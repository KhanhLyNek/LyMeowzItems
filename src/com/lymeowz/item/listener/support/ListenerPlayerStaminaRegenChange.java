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
import api.praya.combatstamina.builder.event.PlayerStaminaRegenChangeEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerPlayerStaminaRegenChange extends HandlerEvent implements Listener
{
    public ListenerPlayerStaminaRegenChange(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void eventPlayerStaminaMaxChange(final PlayerStaminaRegenChangeEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final LoreStatsManager statsManager = gameManager.getStatsManager();
        final SocketManager socketManager = gameManager.getSocketManager();
        if (!event.isCancelled()) {
            final Player player = event.getPlayer();
            final PlayerStaminaRegenChangeEvent.StaminaRegenModifierEnum staminaRegenType = PlayerStaminaRegenChangeEvent.StaminaRegenModifierEnum.BONUS;
            final LoreStatsArmor statsBuild = statsManager.getLoreStatsArmor((LivingEntity)player);
            final SocketGemsProperties socketBuild = socketManager.getSocketProperties((LivingEntity)player);
            final double staminaRegenStats = statsBuild.getStaminaRegen();
            final double staminaRegenSocket = socketBuild.getStaminaRegen();
            final double staminaRegenBase = event.getOriginalStaminaRegen(staminaRegenType);
            final double staminaRegenResult = staminaRegenStats + staminaRegenSocket + staminaRegenBase;
            event.setStaminaRegen(staminaRegenType, staminaRegenResult);
        }
    }
}
