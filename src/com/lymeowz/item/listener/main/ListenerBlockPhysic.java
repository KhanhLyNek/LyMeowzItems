// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerBlockPhysic extends HandlerEvent implements Listener
{
    public ListenerBlockPhysic(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void eventBlockPhysic(final BlockPhysicsEvent event) {
        if (!event.isCancelled() && event.getBlock().hasMetadata("Anti_Block_Physic")) {
            event.setCancelled(true);
        }
    }
}
