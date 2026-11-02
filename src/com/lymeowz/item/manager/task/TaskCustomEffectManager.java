// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.manager.task;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.plugin.Plugin;
import com.lymeowz.item.builder.task.TaskCustomEffect;
import org.bukkit.Bukkit;
import com.lymeowz.item.MyItems;
import org.bukkit.scheduler.BukkitTask;
import com.lymeowz.item.builder.handler.HandlerManager;

public class TaskCustomEffectManager extends HandlerManager
{
    private BukkitTask taskCustomEffect;
    
    protected TaskCustomEffectManager(final MyItems plugin) {
        super(plugin);
        this.reloadTaskCustomEffect();
    }
    
    public final void reloadTaskCustomEffect() {
        if (this.taskCustomEffect != null) {
            this.taskCustomEffect.cancel();
        }
        this.taskCustomEffect = this.createTaskCustomEffect();
    }
    
    private final BukkitTask createTaskCustomEffect() {
        final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        final Runnable runnable = new TaskCustomEffect(this.plugin);
        final int delay = 2;
        final int period = 1;
        final BukkitTask task = scheduler.runTaskTimer((Plugin)this.plugin, runnable, 2L, 1L);
        return task;
    }
}
