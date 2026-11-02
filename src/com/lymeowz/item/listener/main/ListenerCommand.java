// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.main;

import org.bukkit.event.EventHandler;
import com.lymeowz.item.manager.game.GameManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.inventory.ItemStack;
import com.lymeowz.item.api.builder.passive.PassiveEffectEnum;
import java.util.Collection;
import com.lymeowz.item.manager.game.PassiveEffectManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.lymeowz.lib.utility.EquipmentUtil;
import com.lymeowz.lib.core.enums.main.Slot;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import com.lymeowz.item.config.plugin.MainConfig;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerCommand extends HandlerEvent implements Listener
{
    public ListenerCommand(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final GameManager gameManager = this.plugin.getGameManager();
        final PassiveEffectManager passiveEffectManager = gameManager.getPassiveEffectManager();
        final MainConfig mainConfig = MainConfig.getInstance();
        final Player player = event.getPlayer();
        final ItemStack itemBefore = Bridge.getBridgeEquipment().getEquipment(player, Slot.MAINHAND);
        final Collection<PassiveEffectEnum> passiveEffectBefore = passiveEffectManager.getPassiveEffects(itemBefore);
        final int idBefore = EquipmentUtil.isSolid(itemBefore) ? itemBefore.hashCode() : 0;
        final boolean enableGradeCalculation = mainConfig.isPassiveEnableGradeCalculation();
        new BukkitRunnable() {
            public void run() {
                if (player.isOnline()) {
                    final ItemStack itemAfter = Bridge.getBridgeEquipment().getEquipment(player, Slot.MAINHAND);
                    final Collection<PassiveEffectEnum> passiveEffectAfter = passiveEffectManager.getPassiveEffects(itemAfter);
                    final int idAfter = EquipmentUtil.isSolid(itemAfter) ? itemAfter.hashCode() : 0;
                    if (idBefore != idAfter) {
                        passiveEffectManager.reloadPassiveEffect(player, passiveEffectBefore, enableGradeCalculation);
                        passiveEffectManager.reloadPassiveEffect(player, passiveEffectAfter, enableGradeCalculation);
                    }
                }
            }
        }.runTaskLater((Plugin)this.plugin, 1L);
    }
}
