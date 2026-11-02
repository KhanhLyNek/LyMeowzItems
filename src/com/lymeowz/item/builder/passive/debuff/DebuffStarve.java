// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.builder.passive.debuff;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.lymeowz.lib.utility.PotionUtil;
import com.lymeowz.item.config.plugin.MainConfig;
import org.bukkit.entity.Player;
import com.lymeowz.item.api.builder.passive.PassiveEffectEnum;
import com.lymeowz.item.builder.abs.PassiveEffect;

public class DebuffStarve extends PassiveEffect
{
    private static final PassiveEffectEnum debuff;
    
    static {
        debuff = PassiveEffectEnum.STARVE;
    }
    
    public DebuffStarve() {
        super(DebuffStarve.debuff, 1);
    }
    
    public DebuffStarve(final int grade) {
        super(DebuffStarve.debuff, grade);
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
