// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.tabcompleter;

import java.util.Iterator;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import com.lymeowz.item.manager.plugin.CommandManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import com.lymeowz.lib.utility.TabCompleterUtil;
import org.bukkit.enchantments.Enchantment;
import com.lymeowz.lib.core.enums.main.Slot;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import com.lymeowz.lib.utility.PlayerUtil;
import com.lymeowz.lib.utility.SenderUtil;
import com.lymeowz.lib.core.enums.branch.SoundEnum;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.lymeowz.item.MyItems;
import org.bukkit.command.TabCompleter;
import com.lymeowz.item.builder.handler.HandlerTabCompleter;

public class TabCompleterEnchantmentRemove extends HandlerTabCompleter implements TabCompleter
{
    public TabCompleterEnchantmentRemove(final MyItems plugin) {
        super(plugin);
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final CommandManager commandManager = pluginManager.getCommandManager();
        final List<String> tabList = new ArrayList<String>();
        SenderUtil.playSound(sender, SoundEnum.BLOCK_WOOD_BUTTON_CLICK_ON);
        if (SenderUtil.isPlayer(sender)) {
            final Player player = PlayerUtil.parse(sender);
            final ItemStack item = Bridge.getBridgeEquipment().getEquipment(player, Slot.MAINHAND);
            final ItemMeta itemmeta = item.getItemMeta();
            if (args.length == 1 && commandManager.checkPermission(sender, "Enchant_Remove") && itemmeta.hasEnchants()) {
                for (final Enchantment enchant : itemmeta.getEnchants().keySet()) {
                    tabList.add(enchant.getName());
                }
            }
        }
        return (List<String>)TabCompleterUtil.returnList((List)tabList, args);
    }
}
