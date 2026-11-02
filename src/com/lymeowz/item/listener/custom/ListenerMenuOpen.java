// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.custom;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import com.lymeowz.lib.core.builder.menu.MenuExecutor;
import org.bukkit.entity.Player;
import com.lymeowz.lib.core.builder.menu.Menu;
import com.lymeowz.item.menu.MenuStats;
import com.lymeowz.item.menu.MenuSocket;
import org.bukkit.command.CommandSender;
import com.lymeowz.lib.utility.SenderUtil;
import com.lymeowz.lib.core.enums.branch.SoundEnum;
import com.lymeowz.lib.core.builder.menu.MenuGUI;
import com.lymeowz.lib.api.builder.event.MenuOpenEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerMenuOpen extends HandlerEvent implements Listener
{
    public ListenerMenuOpen(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void menuOpenEvent(final MenuOpenEvent event) {
        final Menu menu = event.getMenu();
        final Player player = event.getPlayer();
        final String id = menu.getID();
        if (!event.isCancelled() && menu instanceof MenuGUI) {
            final MenuGUI menuGUI = (MenuGUI)menu;
            if (id.startsWith("MyItems")) {
                SenderUtil.playSound((CommandSender)player, SoundEnum.ENTITY_EXPERIENCE_ORB_PICKUP);
            }
            if (id.equalsIgnoreCase("MyItems Socket")) {
                final MenuExecutor executor = menu.getExecutor();
                if (executor instanceof MenuSocket) {
                    final MenuSocket executorSocket = (MenuSocket)executor;
                    executorSocket.updateSocketMenu(menuGUI, player);
                }
            }
            else if (id.equalsIgnoreCase("MyItems Stats")) {
                final MenuExecutor executor = menu.getExecutor();
                if (executor instanceof MenuStats) {
                    final MenuStats executorStats = (MenuStats)executor;
                    executorStats.updateStatsMenu(menuGUI, player);
                }
            }
        }
    }
}
