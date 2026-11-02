// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.custom;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.lymeowz.item.api.builder.player.PlayerPowerCooldown;
import com.lymeowz.item.api.builder.power.PowerSpecialEnum;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import com.lymeowz.item.manager.player.PlayerPowerManager;
import com.lymeowz.item.manager.game.LoreStatsManager;
import com.lymeowz.item.manager.game.GameManager;
import com.lymeowz.lib.core.enums.main.Slot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.OfflinePlayer;
import com.lymeowz.item.api.builder.lorestats.LoreStatsOption;
import com.lymeowz.item.api.builder.lorestats.LoreStatsEnum;
import com.lymeowz.lib.utility.MathUtil;
import com.lymeowz.item.builder.abs.SpecialPower;
import com.lymeowz.item.api.builder.event.PowerSpecialCastEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerPowerSpecialCast extends HandlerEvent implements Listener
{
    public ListenerPowerSpecialCast(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventPowerSpecialCast(final PowerSpecialCastEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final LoreStatsManager statsManager = gameManager.getStatsManager();
        final PlayerPowerManager playerPowerManager = this.plugin.getPlayerManager().getPlayerPowerManager();
        if (!event.isCancelled()) {
            final Player player = event.getPlayer();
            final ItemStack item = event.getItem();
            final PowerSpecialEnum special = event.getSpecial();
            final SpecialPower specialPower = SpecialPower.getSpecial(special);
            final double cooldown = event.getCooldown();
            final long timeCooldown = MathUtil.convertSecondsToMilis(cooldown);
            final int durability = (int)statsManager.getLoreValue(item, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT);
            final PlayerPowerCooldown powerCooldown = playerPowerManager.getPlayerPowerCooldown((OfflinePlayer)player);
            specialPower.cast((LivingEntity)player);
            if (timeCooldown > 0L) {
                powerCooldown.setPowerSpecialCooldown(special, timeCooldown);
            }
            if (!statsManager.durability((LivingEntity)player, item, durability, true)) {
                statsManager.sendBrokenCode((LivingEntity)player, Slot.MAINHAND);
            }
        }
    }
}
