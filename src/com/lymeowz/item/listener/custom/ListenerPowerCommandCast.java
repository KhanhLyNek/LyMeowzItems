// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.custom;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import java.util.List;
import com.lymeowz.item.api.builder.player.PlayerPowerCooldown;
import com.lymeowz.item.api.builder.power.PowerCommandProperties;
import org.bukkit.entity.Player;
import com.lymeowz.item.manager.game.LoreStatsManager;
import com.lymeowz.item.manager.player.PlayerPowerManager;
import com.lymeowz.item.manager.game.PowerCommandManager;
import com.lymeowz.item.manager.game.PowerManager;
import com.lymeowz.item.manager.game.GameManager;
import com.lymeowz.item.manager.player.PlayerManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import com.lymeowz.lib.core.enums.main.Slot;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import com.lymeowz.lib.utility.EquipmentUtil;
import com.lymeowz.lib.utility.CommandUtil;
import com.lymeowz.lib.utility.TextUtil;
import java.util.HashMap;
import com.lymeowz.item.api.builder.lorestats.LoreStatsOption;
import com.lymeowz.item.api.builder.lorestats.LoreStatsEnum;
import com.lymeowz.lib.utility.MathUtil;
import org.bukkit.OfflinePlayer;
import com.lymeowz.item.api.builder.event.PowerCommandCastEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerPowerCommandCast extends HandlerEvent implements Listener
{
    public ListenerPowerCommandCast(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void eventPowerCommandCast(final PowerCommandCastEvent event) {
        final PlayerManager playerManager = this.plugin.getPlayerManager();
        final GameManager gameManager = this.plugin.getGameManager();
        final PowerManager powerManager = gameManager.getPowerManager();
        final PowerCommandManager powerCommandManager = powerManager.getPowerCommandManager();
        final PlayerPowerManager playerPowerManager = playerManager.getPlayerPowerManager();
        final LoreStatsManager statsManager = gameManager.getStatsManager();
        if (!event.isCancelled()) {
            final Player player = event.getPlayer();
            final ItemStack item = event.getItem();
            final String keyCommand = event.getKeyCommand();
            final PowerCommandProperties powerCommandProperties = powerCommandManager.getPowerCommandProperties(keyCommand);
            final PlayerPowerCooldown powerCooldown = playerPowerManager.getPlayerPowerCooldown((OfflinePlayer)player);
            final boolean consume = powerCommandProperties.isConsume();
            final double cooldown = event.getCooldown();
            final long timeCooldown = MathUtil.convertSecondsToMilis(cooldown);
            final int durability = (int)statsManager.getLoreValue(item, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT);
            final List<String> commandOP = powerCommandProperties.getCommandOP();
            final List<String> commandConsole = powerCommandProperties.getCommandConsole();
            final HashMap<String, String> mapPlaceholder = new HashMap<String, String>();
            mapPlaceholder.put("player", player.getName());
            for (String command : commandOP) {
                command = TextUtil.placeholder((HashMap)mapPlaceholder, command);
                command = TextUtil.placeholder((HashMap)mapPlaceholder, command, "<", ">");
                command = TextUtil.hookPlaceholderAPI(player, command);
                CommandUtil.sudoCommand(player, command, true);
            }
            for (String command : commandConsole) {
                command = TextUtil.placeholder((HashMap)mapPlaceholder, command);
                command = TextUtil.placeholder((HashMap)mapPlaceholder, command, "<", ">");
                command = TextUtil.hookPlaceholderAPI(player, command);
                CommandUtil.consoleCommand(command);
            }
            if (timeCooldown > 0L) {
                powerCooldown.setPowerCommandCooldown(keyCommand, timeCooldown);
            }
            if (consume) {
                final int amount = item.getAmount();
                if (amount > 1) {
                    EquipmentUtil.setAmount(item, amount - 1);
                }
                else {
                    Bridge.getBridgeEquipment().setEquipment(player, (ItemStack)null, Slot.MAINHAND);
                }
            }
            else if (!statsManager.durability((LivingEntity)player, item, durability, true)) {
                statsManager.sendBrokenCode((LivingEntity)player, Slot.MAINHAND);
            }
        }
    }
}
