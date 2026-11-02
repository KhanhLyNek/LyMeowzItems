// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.builder.passive.buff;

import com.lymeowz.lib.utility.MathUtil;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.lymeowz.item.manager.game.PassiveEffectManager;
import com.lymeowz.item.manager.game.GameManager;
import com.lymeowz.lib.utility.PotionUtil;
import org.bukkit.OfflinePlayer;
import com.lymeowz.item.config.plugin.MainConfig;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.item.MyItems;
import org.bukkit.entity.Player;
import com.lymeowz.item.api.builder.passive.PassiveEffectEnum;
import com.lymeowz.item.builder.abs.PassiveEffect;

public class BuffVision extends PassiveEffect
{
    private static final PassiveEffectEnum buff;
    
    static {
        buff = PassiveEffectEnum.VISION;
    }
    
    public BuffVision() {
        super(BuffVision.buff, 1);
    }
    
    public BuffVision(final int grade) {
        super(BuffVision.buff, grade);
    }
    
    @Override
    public final void cast(final Player player) {
        final MyItems plugin = (MyItems)JavaPlugin.getPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final PassiveEffectManager passiveEffectManager = gameManager.getPassiveEffectManager();
        final MainConfig mainConfig = MainConfig.getInstance();
        if (!passiveEffectManager.isPassiveEffectCooldown(BuffVision.buff, (OfflinePlayer)player)) {
            final PotionEffectType potionType = this.getPotion();
            final long cooldown = this.getCooldown();
            final int duration = this.getDuration();
            final boolean isEnableParticle = mainConfig.isMiscEnableParticlePotion();
            final PotionEffect potion = PotionUtil.createPotion(potionType, duration, this.grade, true, isEnableParticle);
            player.addPotionEffect(potion);
            passiveEffectManager.setPassiveEffectCooldown(BuffVision.buff, (OfflinePlayer)player, cooldown);
        }
    }
    
    private final int getDuration() {
        final MainConfig mainConfig = MainConfig.getInstance();
        return mainConfig.getPassivePeriodEffect() * this.grade + 20;
    }
    
    private final long getCooldown() {
        final MainConfig mainConfig = MainConfig.getInstance();
        return MathUtil.convertTickToMilis(mainConfig.getPassivePeriodEffect()) * PassiveEffectEnum.HASTE.getMaxGrade();
    }
}
