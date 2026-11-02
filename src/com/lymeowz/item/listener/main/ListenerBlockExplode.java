// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.main;

import org.bukkit.event.EventHandler;
import com.lymeowz.lib.utility.BlockUtil;
import org.bukkit.event.block.BlockExplodeEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerBlockExplode extends HandlerEvent implements Listener
{
    public ListenerBlockExplode(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onBlockExplode(final BlockExplodeEvent event) {
        if (!event.isCancelled() && BlockUtil.isSet(event.getBlock())) {
            BlockUtil.remove(event.getBlock());
        }
    }
}
