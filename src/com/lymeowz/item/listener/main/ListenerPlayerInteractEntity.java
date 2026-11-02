// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.main;

import com.lymeowz.item.utility.main.TriggerSupportUtil;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import com.lymeowz.item.manager.game.GameManager;
import org.bukkit.plugin.Plugin;
import com.lymeowz.item.api.builder.passive.PassiveEffectEnum;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import com.lymeowz.item.manager.game.PassiveEffectManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.lymeowz.item.manager.game.ItemSetManager;
import org.bukkit.scheduler.BukkitRunnable;
import com.lymeowz.lib.core.enums.main.Slot;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import org.bukkit.entity.ItemFrame;
import com.lymeowz.item.config.plugin.MainConfig;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerPlayerInteractEntity extends HandlerEvent implements Listener
{
    public ListenerPlayerInteractEntity(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void eventPutItemFrame(final PlayerInteractEntityEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final PassiveEffectManager passiveEffectManager = gameManager.getPassiveEffectManager();
        final ItemSetManager itemSetManager = gameManager.getItemSetManager();
        final MainConfig mainConfig = MainConfig.getInstance();
        if (!event.isCancelled()) {
            final Player player = event.getPlayer();
            final Entity entity = event.getRightClicked();
            final boolean enableGradeCalculation = mainConfig.isPassiveEnableGradeCalculation();
            if (entity instanceof ItemFrame) {
                final ItemStack itemMainHand = Bridge.getBridgeEquipment().getEquipment(player, Slot.MAINHAND);
                final ItemStack itemOffHand = Bridge.getBridgeEquipment().getEquipment(player, Slot.OFFHAND);
                final Collection<PassiveEffectEnum> mainPassiveEffects = passiveEffectManager.getPassiveEffects(itemMainHand);
                final Collection<PassiveEffectEnum> offPassiveEffects = passiveEffectManager.getPassiveEffects(itemOffHand);
                new BukkitRunnable() {
                    public void run() {
                        if (itemSetManager.isItemSet(itemMainHand) || itemSetManager.isItemSet(itemOffHand)) {
                            itemSetManager.updateItemSet((LivingEntity)player);
                        }
                        passiveEffectManager.reloadPassiveEffect(player, mainPassiveEffects, enableGradeCalculation);
                        passiveEffectManager.reloadPassiveEffect(player, offPassiveEffects, enableGradeCalculation);
                    }
                }.runTaskLater((Plugin)this.plugin, 1L);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void triggerSupport(final PlayerInteractEntityEvent event) {
        if (!event.isCancelled()) {
            final Entity entity = event.getRightClicked();
            if (entity instanceof ItemFrame) {
                final Player player = event.getPlayer();
                new BukkitRunnable() {
                    public void run() {
                        TriggerSupportUtil.updateSupport(player);
                    }
                }.runTaskLater((Plugin)this.plugin, 2L);
            }
        }
    }
}
