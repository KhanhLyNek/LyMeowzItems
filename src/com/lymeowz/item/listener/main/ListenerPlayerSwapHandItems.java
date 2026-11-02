// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.main;

import com.lymeowz.item.api.builder.passive.PassiveEffectEnum;
import java.util.Collection;
import org.bukkit.Material;
import com.lymeowz.item.manager.game.PassiveEffectManager;
import com.lymeowz.item.utility.main.TriggerSupportUtil;
import com.lymeowz.lib.utility.EquipmentUtil;
import com.lymeowz.item.config.plugin.MainConfig;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import com.lymeowz.item.manager.game.GameManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.lymeowz.item.manager.game.ItemSetManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerPlayerSwapHandItems extends HandlerEvent implements Listener
{
    public ListenerPlayerSwapHandItems(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void triggerEquipmentChangeEvent(final PlayerSwapHandItemsEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final ItemSetManager itemSetManager = gameManager.getItemSetManager();
        if (!event.isCancelled()) {
            final Player player = event.getPlayer();
            final ItemStack itemMainHand = event.getMainHandItem();
            final ItemStack itemOffHand = event.getOffHandItem();
            if (itemSetManager.isItemSet(itemMainHand) || itemSetManager.isItemSet(itemOffHand)) {
                new BukkitRunnable() {
                    public void run() {
                        itemSetManager.updateItemSet((LivingEntity)player);
                    }
                }.runTaskLater((Plugin)this.plugin, 0L);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerSwapHandItemsEvent(final PlayerSwapHandItemsEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final PassiveEffectManager passiveEffectManager = gameManager.getPassiveEffectManager();
        final MainConfig mainConfig = MainConfig.getInstance();
        if (!event.isCancelled()) {
            final Player player = event.getPlayer();
            final ItemStack itemMainHand = event.getMainHandItem();
            final ItemStack itemOffHand = event.getOffHandItem();
            final boolean enableItemUniversal = mainConfig.isStatsEnableItemUniversal();
            final boolean enableGradeCalculation = mainConfig.isPassiveEnableGradeCalculation();
            boolean isShieldMainHand;
            if (EquipmentUtil.isSolid(itemMainHand)) {
                final Material materialMainHand = itemMainHand.getType();
                final Collection<PassiveEffectEnum> passiveEffectsMainHand = passiveEffectManager.getPassiveEffects(itemMainHand);
                isShieldMainHand = materialMainHand.toString().equalsIgnoreCase("SHIELD");
                passiveEffectManager.reloadPassiveEffect(player, passiveEffectsMainHand, enableGradeCalculation);
            }
            else {
                isShieldMainHand = false;
            }
            boolean isShieldOffHand;
            if (EquipmentUtil.isSolid(itemOffHand)) {
                final Material materialOffHand = itemOffHand.getType();
                final Collection<PassiveEffectEnum> passiveEffectsOffHand = passiveEffectManager.getPassiveEffects(itemOffHand);
                isShieldOffHand = materialOffHand.toString().equalsIgnoreCase("SHIELD");
                passiveEffectManager.reloadPassiveEffect(player, passiveEffectsOffHand, enableGradeCalculation);
            }
            else {
                isShieldOffHand = false;
            }
            if (enableItemUniversal || isShieldMainHand || isShieldOffHand) {
                new BukkitRunnable() {
                    public void run() {
                        TriggerSupportUtil.updateSupport(player);
                    }
                }.runTaskLater((Plugin)this.plugin, 0L);
            }
        }
    }
}
