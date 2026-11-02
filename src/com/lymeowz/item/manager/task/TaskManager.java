// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.manager.task;

import com.lymeowz.item.MyItems;
import com.lymeowz.item.builder.handler.HandlerManager;

public class TaskManager extends HandlerManager
{
    private final TaskCustomEffectManager taskCustomEffectManager;
    private final TaskPassiveEffectManager taskPassiveEffectManager;
    private final TaskPowerCooldownManager taskPowerCooldownManager;
    
    public TaskManager(final MyItems plugin) {
        super(plugin);
        this.taskCustomEffectManager = new TaskCustomEffectManager(plugin);
        this.taskPassiveEffectManager = new TaskPassiveEffectManager(plugin);
        this.taskPowerCooldownManager = new TaskPowerCooldownManager(plugin);
    }
    
    public final TaskCustomEffectManager getTaskCustomEffectManager() {
        return this.taskCustomEffectManager;
    }
    
    public final TaskPassiveEffectManager getTaskPassiveEffectManager() {
        return this.taskPassiveEffectManager;
    }
    
    public final TaskPowerCooldownManager getTaskPowerCooldownManager() {
        return this.taskPowerCooldownManager;
    }
}
