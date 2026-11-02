// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.builder.passive.buff;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.lymeowz.lib.utility.PotionUtil;
import com.lymeowz.item.config.plugin.MainConfig;
import org.bukkit.entity.Player;
import com.lymeowz.item.api.builder.passive.PassiveEffectEnum;
import com.lymeowz.item.builder.abs.PassiveEffect;

public class BuffStrength extends PassiveEffect
{
    private static final PassiveEffectEnum buff;
    
    static {
        buff = PassiveEffectEnum.STRENGTH;
    }
    
    public BuffStrength() {
        super(BuffStrength.buff, 1);
    }
    
    public BuffStrength(final int grade) {
        super(BuffStrength.buff, grade);
    }
    
    @Override
    public final void cast(final Player player) {
        final MainConfig mainConfig = MainConfig.getInstance();
        final PotionEffectType potionType = this.getPotion();
        final boolean isEnableParticle = mainConfig.isMiscEnableParticlePotion();
        final PotionEffect potion = PotionUtil.createPotion(potionType, 96000, this.grade, true, isEnableParticle);
        player.addPotionEffect(potion);
    }
}
