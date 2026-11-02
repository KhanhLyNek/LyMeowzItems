// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.command;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import com.lymeowz.lib.core.builder.message.MessageBuild;
import com.lymeowz.item.manager.plugin.LanguageManager;
import com.lymeowz.item.manager.plugin.CommandManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import com.lymeowz.lib.utility.TextUtil;
import com.lymeowz.lib.utility.EquipmentUtil;
import com.lymeowz.lib.core.enums.main.Slot;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import com.lymeowz.lib.utility.PlayerUtil;
import com.lymeowz.lib.utility.SenderUtil;
import com.lymeowz.lib.core.enums.branch.SoundEnum;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.lymeowz.item.MyItems;
import org.bukkit.command.CommandExecutor;
import com.lymeowz.item.builder.handler.HandlerCommand;

public class CommandLoreAdd extends HandlerCommand implements CommandExecutor
{
    public CommandLoreAdd(final MyItems plugin) {
        super(plugin);
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        return addLore(sender, command, label, args);
    }
    
    protected static final boolean addLore(final CommandSender sender, final Command command, final String label, final String[] args) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final PluginManager pluginManager = plugin.getPluginManager();
        final CommandManager commandManager = pluginManager.getCommandManager();
        final LanguageManager lang = pluginManager.getLanguageManager();
        if (!commandManager.checkPermission(sender, "Lore_Add")) {
            final String permission = commandManager.getPermission("Lore_Add");
            final MessageBuild message = lang.getMessage(sender, "Permission_Lack");
            message.sendMessage(sender, "permission", permission);
            SenderUtil.playSound(sender, SoundEnum.ENTITY_BLAZE_DEATH);
            return true;
        }
        if (!SenderUtil.isPlayer(sender)) {
            final MessageBuild message2 = lang.getMessage(sender, "Console_Command_Forbiden");
            message2.sendMessage(sender);
            SenderUtil.playSound(sender, SoundEnum.ENTITY_BLAZE_DEATH);
            return true;
        }
        final Player player = PlayerUtil.parse(sender);
        final ItemStack item = Bridge.getBridgeEquipment().getEquipment(player, Slot.MAINHAND);
        if (!EquipmentUtil.isSolid(item)) {
            final MessageBuild message3 = lang.getMessage(sender, "Item_MainHand_Empty");
            message3.sendMessage(sender);
            SenderUtil.playSound(sender, SoundEnum.ENTITY_BLAZE_DEATH);
            return true;
        }
        final String lore = (args.length < 1) ? "" : TextUtil.hookPlaceholderAPI(player, TextUtil.concatenate(args));
        final MessageBuild message4 = lang.getMessage(sender, "MyItems_AddLore_Success");
        EquipmentUtil.addLore(item, lore);
        message4.sendMessage(sender);
        SenderUtil.playSound(sender, SoundEnum.ENTITY_EXPERIENCE_ORB_PICKUP);
        player.updateInventory();
        return true;
    }
}
