// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.manager.game;

import com.lymeowz.item.manager.game.GameManager;
import com.lymeowz.item.manager.game.PowerShootManager;
import com.lymeowz.lib.core.enums.branch.ProjectileEnum;
import com.lymeowz.item.api.builder.power.PowerClickEnum;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.builder.handler.HandlerManager;

public class PowerShootManagerAPI extends HandlerManager
{
    protected PowerShootManagerAPI(final MyItems plugin) {
        super(plugin);
    }
    
    public final String getTextPowerShoot(final PowerClickEnum click, final ProjectileEnum projectile, final double cooldown) {
        return this.getPowerShootManager().getTextPowerShoot(click, projectile, cooldown);
    }
    
    private final PowerShootManager getPowerShootManager() {
        final GameManager gameManager = this.plugin.getGameManager();
        final PowerShootManager powerShootManager = gameManager.getPowerManager().getPowerShootManager();
        return powerShootManager;
    }
}
