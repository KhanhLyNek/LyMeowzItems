// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.manager.register;

import com.lymeowz.item.MyItems;
import com.lymeowz.item.builder.handler.HandlerManager;

public class RegisterManager extends HandlerManager
{
    private final RegisterAbilityWeaponManager registerAbilityWeaponManager;
    
    public RegisterManager(final MyItems plugin) {
        super(plugin);
        this.registerAbilityWeaponManager = new RegisterAbilityWeaponManager(plugin);
    }
    
    public final RegisterAbilityWeaponManager getRegisterAbilityWeaponManager() {
        return this.registerAbilityWeaponManager;
    }
}
