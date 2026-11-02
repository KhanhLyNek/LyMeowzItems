// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.builder.task;

import java.util.Iterator;
import com.lymeowz.item.utility.customeffect.CustomEffectFreeze;
import org.bukkit.entity.Player;
import com.lymeowz.lib.utility.PlayerUtil;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.builder.handler.HandlerTask;

public class TaskCustomEffect extends HandlerTask implements Runnable
{
    public TaskCustomEffect(final MyItems plugin) {
        super(plugin);
    }
    
    @Override
    public void run() {
        for (final Player loopPlayer : PlayerUtil.getOnlinePlayers()) {
            CustomEffectFreeze.cast(loopPlayer);
        }
    }
}
