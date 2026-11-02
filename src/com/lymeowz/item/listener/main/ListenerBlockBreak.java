// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.main;

import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import com.lymeowz.item.manager.game.LoreStatsManager;
import com.lymeowz.item.manager.game.GameManager;
import org.bukkit.entity.LivingEntity;
import com.lymeowz.item.api.builder.lorestats.LoreStatsOption;
import com.lymeowz.item.api.builder.lorestats.LoreStatsEnum;
import com.lymeowz.lib.utility.EquipmentUtil;
import com.lymeowz.lib.core.enums.main.Slot;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import com.lymeowz.lib.utility.BlockUtil;
import org.bukkit.event.block.BlockBreakEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerBlockBreak extends HandlerEvent implements Listener
{
    public ListenerBlockBreak(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void eventBlockBreak(final BlockBreakEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final LoreStatsManager statsManager = gameManager.getStatsManager();
        if (!event.isCancelled()) {
            if (BlockUtil.isSet(event.getBlock())) {
                event.setCancelled(true);
                return;
            }
            final Player player = event.getPlayer();
            final ItemStack item = Bridge.getBridgeEquipment().getEquipment(player, Slot.MAINHAND);
            if (EquipmentUtil.hasLore(item)) {
                final int durability = (int)statsManager.getLoreValue(item, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT);
                if (!statsManager.durability((LivingEntity)player, item, durability, true)) {
                    event.setCancelled(true);
                    statsManager.sendBrokenCode((LivingEntity)player, Slot.MAINHAND);
                }
            }
        }
    }
}
