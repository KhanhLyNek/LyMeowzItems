// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.main;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import com.lymeowz.item.manager.game.ItemSetManager;
import com.lymeowz.item.manager.game.GameManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerInventoryOpen extends HandlerEvent implements Listener
{
    public ListenerInventoryOpen(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void inventoryOpenEvent(final InventoryOpenEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final ItemSetManager itemSetManager = gameManager.getItemSetManager();
        if (!event.isCancelled()) {
            final Inventory inventory = event.getInventory();
            final HumanEntity human = event.getPlayer();
            if (human instanceof Player) {
                final Player player = (Player)human;
                itemSetManager.updateItemSet((LivingEntity)player, false, inventory);
            }
        }
    }
}
