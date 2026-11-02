// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.builder.task;

import com.lymeowz.item.manager.game.PassiveEffectManager;
import com.lymeowz.item.manager.game.GameManager;
import com.lymeowz.item.config.plugin.MainConfig;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.builder.handler.HandlerTask;

public class TaskPassiveEffect extends HandlerTask implements Runnable
{
    public TaskPassiveEffect(final MyItems plugin) {
        super(plugin);
    }
    
    @Override
    public void run() {
        final GameManager gameManager = this.plugin.getGameManager();
        final PassiveEffectManager passiveEffectManager = gameManager.getPassiveEffectManager();
        final MainConfig mainConfig = MainConfig.getInstance();
        final boolean enableGradeCalculation = mainConfig.isPassiveEnableGradeCalculation();
        passiveEffectManager.loadPassiveEffect(enableGradeCalculation);
    }
}
