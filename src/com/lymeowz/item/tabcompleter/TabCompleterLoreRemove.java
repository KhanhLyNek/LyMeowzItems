// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.tabcompleter;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import com.lymeowz.item.manager.plugin.LanguageManager;
import com.lymeowz.item.manager.plugin.CommandManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import com.lymeowz.lib.utility.TabCompleterUtil;
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

public class TabCompleterLoreRemove extends HandlerTabCompleter implements TabCompleter
{
    public TabCompleterLoreRemove(final MyItems plugin) {
        super(plugin);
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        final PluginManager pluginManager = this.plugin.getPluginManager();
        final CommandManager commandManager = pluginManager.getCommandManager();
        final LanguageManager lang = pluginManager.getLanguageManager();
        final List<String> tabList = new ArrayList<String>();
        SenderUtil.playSound(sender, SoundEnum.BLOCK_WOOD_BUTTON_CLICK_ON);
        if (SenderUtil.isPlayer(sender)) {
            final Player player = PlayerUtil.parse(sender);
            final ItemStack item = Bridge.getBridgeEquipment().getEquipment(player, Slot.MAINHAND);
            final ItemMeta meta = item.getItemMeta();
            if (args.length == 1) {
                if (meta.hasLore()) {
                    if (commandManager.checkPermission(sender, "Lore_Remove")) {
                        for (int t = 1; t <= meta.getLore().size(); ++t) {
                            tabList.add(String.valueOf(t));
                        }
                    }
                }
                else {
                    final String message = lang.getText(sender, "MyItems_RemoveLore_Empty");
                    tabList.add("");
                    SenderUtil.sendMessage(sender, message);
                }
            }
        }
        return (List<String>)TabCompleterUtil.returnList((List)tabList, args);
    }
}
