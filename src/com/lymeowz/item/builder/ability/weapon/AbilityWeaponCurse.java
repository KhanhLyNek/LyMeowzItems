// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.builder.ability.weapon;

import org.bukkit.entity.Player;
import com.lymeowz.lib.core.builder.message.MessageBuild;
import org.bukkit.Location;
import org.bukkit.projectiles.ProjectileSource;
import com.lymeowz.item.manager.plugin.LanguageManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import com.lymeowz.lib.core.enums.branch.SoundEnum;
import java.util.Collection;
import com.lymeowz.lib.core.enums.branch.ParticleEnum;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import com.lymeowz.item.utility.main.CustomEffectUtil;
import com.lymeowz.item.api.builder.passive.PassiveEffectTypeEnum;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import com.lymeowz.lib.utility.PlayerUtil;
import com.lymeowz.lib.utility.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
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

public class AbilityWeaponCurse extends AbilityWeapon implements AbilityWeaponAttributeBaseDamage, AbilityWeaponAttributeEffect
{
    private static final String ABILITY_ID = "Curse";
    
    private AbilityWeaponCurse(final MyItems plugin, final String id) {
        super((Plugin)plugin, id);
    }
    
    public static final AbilityWeaponCurse getInstance() {
        return AbilityCurseHelper.instance;
    }
    
    @Override
    public String getKeyLore() {
        final MainConfig mainConfig = MainConfig.getInstance();
        return mainConfig.getAbilityWeaponIdentifierCurse();
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
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Curse");
        return abilityWeaponProperties.getMaxGrade();
    }
    
    @Override
    public double getBaseBonusDamage(final int grade) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final AbilityWeaponManager abilityWeaponManager = gameManager.getAbilityWeaponManager();
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Curse");
        final double baseBonusDamage = grade * abilityWeaponProperties.getScaleBaseBonusDamage();
        return baseBonusDamage;
    }
    
    @Override
    public double getBasePercentDamage(final int grade) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final AbilityWeaponManager abilityWeaponManager = gameManager.getAbilityWeaponManager();
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Curse");
        final double basePercentDamage = grade * abilityWeaponProperties.getScaleBasePercentDamage();
        return basePercentDamage;
    }
    
    @Override
    public int getEffectDuration(final int grade) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final AbilityWeaponManager abilityWeaponManager = gameManager.getAbilityWeaponManager();
        final AbilityWeaponProperties abilityWeaponProperties = abilityWeaponManager.getAbilityWeaponProperties("Curse");
        return abilityWeaponProperties.getTotalDuration(grade);
    }
    
    @Override
    public void cast(final Entity caster, final Entity target, final int grade, final double damage) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final PluginManager pluginManager = plugin.getPluginManager();
        final LanguageManager lang = pluginManager.getLanguageManager();
        final MainConfig mainConfig = MainConfig.getInstance();
        LivingEntity attacker;
        if (caster instanceof Projectile) {
            final Projectile projectile = (Projectile)caster;
            final ProjectileSource projectileSource = projectile.getShooter();
            if (projectileSource == null || !(projectileSource instanceof LivingEntity)) {
                return;
            }
            attacker = (LivingEntity)projectileSource;
        }
        else {
            attacker = (LivingEntity)caster;
        }
        if (target instanceof LivingEntity) {
            final LivingEntity victims = (LivingEntity)target;
            final Location location = victims.getLocation();
            final int duration = this.getEffectDuration(grade);
            final double seconds = MathUtil.roundNumber(duration / 20.0);
            final MessageBuild messageAttacker = lang.getMessage(attacker, "Ability_Curse_Attacker");
            final MessageBuild messageVictims = lang.getMessage(victims, "Ability_Curse_Victims");
            final Collection<Player> players = (Collection<Player>)PlayerUtil.getNearbyPlayers(location, mainConfig.getEffectRange());
            final HashMap<String, String> mapPlaceholder = new HashMap<String, String>();
            mapPlaceholder.put("seconds", String.valueOf(seconds));
            if (attacker instanceof Player playerAttacker) {
                messageAttacker.sendMessage((CommandSender)playerAttacker, (HashMap)mapPlaceholder);
            }
            if (victims instanceof Player playerVictims) {
                messageVictims.sendMessage((CommandSender)playerVictims, (HashMap)mapPlaceholder);
            }
            CustomEffectUtil.setCustomEffect((Entity)victims, MathUtil.convertTickToMilis(duration), PassiveEffectTypeEnum.CURSE);
            Bridge.getBridgeParticle().playParticle((Collection)players, ParticleEnum.SPELL_WITCH, location, 25, 0.5, 0.25, 0.5, 0.10000000149011612);
            Bridge.getBridgeSound().playSound((Collection)players, location, SoundEnum.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.0f);
        }
    }
    
    private static class AbilityCurseHelper
    {
        private static final AbilityWeaponCurse instance;
        
        static {
            final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
            instance = new AbilityWeaponCurse(plugin, "Curse");
        }
    }
}
