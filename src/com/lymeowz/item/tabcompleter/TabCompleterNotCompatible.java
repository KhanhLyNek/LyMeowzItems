// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.tabcompleter;

import com.lymeowz.item.manager.plugin.LanguageManager;
import com.lymeowz.lib.utility.TabCompleterUtil;
import com.lymeowz.lib.core.enums.branch.SoundEnum;
import com.lymeowz.lib.utility.SenderUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.lymeowz.item.MyItems;
import org.bukkit.command.TabCompleter;
import com.lymeowz.item.builder.handler.HandlerTabCompleter;

public class TabCompleterNotCompatible extends HandlerTabCompleter implements TabCompleter
{
    public TabCompleterNotCompatible(final MyItems plugin) {
        super(plugin);
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        final LanguageManager lang = this.plugin.getPluginManager().getLanguageManager();
        final String message = lang.getText(sender, "MyItems_Not_Compatible");
        final List<String> tabList = new ArrayList<String>();
        SenderUtil.sendMessage(sender, message);
        SenderUtil.playSound(sender, SoundEnum.BLOCK_WOOD_BUTTON_CLICK_ON);
        return (List<String>)TabCompleterUtil.returnList((List)tabList, args);
    }
}
