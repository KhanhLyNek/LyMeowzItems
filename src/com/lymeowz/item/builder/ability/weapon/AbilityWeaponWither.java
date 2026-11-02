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

public class AbilityWeaponWither extends AbilityWeapon implements AbilityWeaponAttributeBaseDamage, AbilityWeaponAttributeEffect
{
    private static final String ABILITY_ID = "Wither";
    
    private AbilityWeaponWither(final MyItems plugin, final String id) {
        super((Plugin)plugin, id);
    }
    
    public static final AbilityWeaponWither getInstance() {
        return AbilityWitherHelper.instance;
    }
    
    @Override
    public String getKeyLore() {
        final MainConfig mainConfig = MainConfig.getInstance();
        return mainConfig.getAbilityWeaponIdentifierWither();
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
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Wither");
        return abilityWeaponProperties.getMaxGrade();
    }
    
    @Override
    public double getBaseBonusDamage(final int grade) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final AbilityWeaponManager abilityWeaponManager = gameManager.getAbilityWeaponManager();
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Wither");
        final double baseBonusDamage = grade * abilityWeaponProperties.getScaleBaseBonusDamage();
        return baseBonusDamage;
    }
    
    @Override
    public double getBasePercentDamage(final int grade) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final AbilityWeaponManager abilityWeaponManager = gameManager.getAbilityWeaponManager();
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Wither");
        final double basePercentDamage = grade * abilityWeaponProperties.getScaleBasePercentDamage();
        return basePercentDamage;
    }
    
    @Override
    public int getEffectDuration(final int grade) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final AbilityWeaponManager abilityWeaponManager = gameManager.getAbilityWeaponManager();
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Wither");
        final int baseDuration = abilityWeaponProperties.getBaseDurationEffect();
        final int scaleDuration = abilityWeaponProperties.getScaleDurationEffect();
        int duration = abilityWeaponProperties.getTotalDuration(grade);
        for (int amplifier = 0; duration > baseDuration + scaleDuration * (5 + amplifier); duration -= scaleDuration * 2, ++amplifier) {}
        return duration;
    }
    
    private final int getEffectAmplifier(final int grade) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final AbilityWeaponManager abilityWeaponManager = gameManager.getAbilityWeaponManager();
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Wither");
        int baseDuration;
        int scaleDuration;
        int duration;
        int amplifier;
        for (baseDuration = abilityWeaponProperties.getBaseDurationEffect(), scaleDuration = abilityWeaponProperties.getScaleDurationEffect(), duration = abilityWeaponProperties.getTotalDuration(grade), amplifier = 0; duration > baseDuration + scaleDuration * (5 + amplifier); duration -= scaleDuration * 2, ++amplifier) {}
        return amplifier;
    }
    
    @Override
    public void cast(final Entity caster, final Entity target, final int grade, final double damage) {
        final MainConfig mainConfig = MainConfig.getInstance();
        if (target instanceof LivingEntity) {
            final LivingEntity victims = (LivingEntity)target;
            final Location location = victims.getEyeLocation();
            final int duration = this.getEffectDuration(grade);
            final int amplifier = this.getEffectAmplifier(grade);
            final Collection<Player> players = (Collection<Player>)PlayerUtil.getNearbyPlayers(location, mainConfig.getEffectRange());
            Bridge.getBridgeParticle().playParticle((Collection)players, ParticleEnum.SUSPENDED_DEPTH, location, 25, 0.5, 0.5, 0.5, 0.10000000149011612);
            Bridge.getBridgeSound().playSound((Collection)players, location, SoundEnum.ENTITY_WITHER_DEATH, 0.7f, 1.0f);
            CombatUtil.applyPotion(victims, PotionEffectType.WITHER, duration, amplifier);
        }
    }
    
    private static class AbilityWitherHelper
    {
        private static final AbilityWeaponWither instance;
        
        static {
            final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
            instance = new AbilityWeaponWither(plugin, "Wither");
        }
    }
}
