// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.builder.ability.weapon;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import com.lymeowz.lib.utility.CombatUtil;
import org.bukkit.potion.PotionEffectType;
import com.lymeowz.lib.core.enums.branch.SoundEnum;
import java.util.Collection;
import com.lymeowz.lib.core.enums.branch.ParticleEnum;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import com.lymeowz.lib.utility.PlayerUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import com.lymeowz.item.api.builder.ability.AbilityWeaponProperties;
import com.lymeowz.item.manager.game.AbilityWeaponManager;
import com.lymeowz.item.manager.game.GameManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import com.lymeowz.item.config.plugin.MainConfig;
import org.bukkit.plugin.Plugin;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.api.builder.ability.AbilityWeaponAttributeEffect;
import com.lymeowz.item.api.builder.ability.AbilityWeaponAttributeBaseDamage;
import com.lymeowz.item.api.builder.ability.AbilityWeapon;

public class AbilityWeaponTired extends AbilityWeapon implements AbilityWeaponAttributeBaseDamage, AbilityWeaponAttributeEffect
{
    private static final String ABILITY_ID = "Tired";
    
    private AbilityWeaponTired(final MyItems plugin, final String id) {
        super((Plugin)plugin, id);
    }
    
    public static final AbilityWeaponTired getInstance() {
        return AbilityTiredHelper.instance;
    }
    
    @Override
    public String getKeyLore() {
        final MainConfig mainConfig = MainConfig.getInstance();
        return mainConfig.getAbilityWeaponIdentifierTired();
    }
    
    @Override
    public List<String> getDescription() {
        return null;
    }
    
    @Override
    public int getMaxGrade() {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final AbilityWeaponManager abilityWeaponManager = gameManager.getAbilityWeaponManager();
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Tired");
        return abilityWeaponProperties.getMaxGrade();
    }
    
    @Override
    public double getBaseBonusDamage(final int grade) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final AbilityWeaponManager abilityWeaponManager = gameManager.getAbilityWeaponManager();
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Tired");
        final double baseBonusDamage = grade * abilityWeaponProperties.getScaleBaseBonusDamage();
        return baseBonusDamage;
    }
    
    @Override
    public double getBasePercentDamage(final int grade) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final AbilityWeaponManager abilityWeaponManager = gameManager.getAbilityWeaponManager();
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Tired");
        final double basePercentDamage = grade * abilityWeaponProperties.getScaleBasePercentDamage();
        return basePercentDamage;
    }
    
    @Override
    public int getEffectDuration(final int grade) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final AbilityWeaponManager abilityWeaponManager = gameManager.getAbilityWeaponManager();
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Tired");
        return abilityWeaponProperties.getTotalDuration(grade);
    }
    
    @Override
    public void cast(final Entity caster, final Entity target, final int grade, final double damage) {
        final MainConfig mainConfig = MainConfig.getInstance();
        if (target instanceof LivingEntity) {
            final LivingEntity victims = (LivingEntity)target;
            final Location location = victims.getEyeLocation();
            final int duration = this.getEffectDuration(grade);
            final Collection<Player> players = (Collection<Player>)PlayerUtil.getNearbyPlayers(location, mainConfig.getEffectRange());
            Bridge.getBridgeParticle().playParticle((Collection)players, ParticleEnum.CRIT, location, 25, 0.5, 0.5, 0.5, 0.5);
            Bridge.getBridgeSound().playSound((Collection)players, location, SoundEnum.BLOCK_COMPARATOR_CLICK, 0.7f, 1.0f);
            CombatUtil.applyPotion(victims, com.lymeowz.lib.utility.PotionUtil.getPotionEffectType("SLOW_DIGGING"), duration, 3);
        }
    }
    
    private static class AbilityTiredHelper
    {
        private static final AbilityWeaponTired instance;
        
        static {
            final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
            instance = new AbilityWeaponTired(plugin, "Tired");
        }
    }
}
