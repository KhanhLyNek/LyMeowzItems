// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.builder.ability;

import com.lymeowz.item.manager.register.RegisterAbilityWeaponManager;
import com.lymeowz.item.manager.register.RegisterManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.item.MyItems;
import org.bukkit.plugin.Plugin;

public abstract class AbilityWeapon extends Ability implements com.lymeowz.item.ability.WeaponAbility
{
    public AbilityWeapon(final Plugin plugin, final String id) {
        super(plugin, id);
    }
    
    public final boolean register() {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final RegisterManager registerManager = plugin.getRegisterManager();
        final RegisterAbilityWeaponManager registerAbilityWeaponManager = registerManager.getRegisterAbilityWeaponManager();
        return registerAbilityWeaponManager.register(this);
    }
}
