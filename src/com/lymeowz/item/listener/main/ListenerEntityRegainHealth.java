// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.listener.main;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import com.lymeowz.item.utility.main.CustomEffectUtil;
import com.lymeowz.item.api.builder.passive.PassiveEffectTypeEnum;
import org.bukkit.entity.LivingEntity;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import com.lymeowz.item.MyItems;
import org.bukkit.event.Listener;
import com.lymeowz.item.builder.handler.HandlerEvent;

public class ListenerEntityRegainHealth extends HandlerEvent implements Listener
{
    public ListenerEntityRegainHealth(final MyItems plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void eventEntityRegainHealth(final EntityRegainHealthEvent event) {
        if (!event.isCancelled() && Bridge.getBridgeLivingEntity().isLivingEntity(event.getEntity())) {
            final LivingEntity entity = (LivingEntity)event.getEntity();
            if (CustomEffectUtil.isRunCustomEffect((Entity)entity, PassiveEffectTypeEnum.CURSE)) {
                event.setCancelled(true);
            }
        }
    }
}
