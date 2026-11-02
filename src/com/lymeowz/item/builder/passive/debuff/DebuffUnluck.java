// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.builder.passive.debuff;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.lymeowz.lib.utility.PotionUtil;
import com.lymeowz.lib.utility.ServerUtil;
import com.lymeowz.lib.core.enums.main.VersionNMS;
import com.lymeowz.item.config.plugin.MainConfig;
import org.bukkit.entity.Player;
import com.lymeowz.item.api.builder.passive.PassiveEffectEnum;
import com.lymeowz.item.builder.abs.PassiveEffect;

public class DebuffUnluck extends PassiveEffect
{
    private static final PassiveEffectEnum debuff;
    
    static {
        debuff = PassiveEffectEnum.UNLUCK;
    }
    
    public DebuffUnluck() {
        super(DebuffUnluck.debuff, 1);
    }
    
    public DebuffUnluck(final int grade) {
        super(DebuffUnluck.debuff, grade);
    }
    
    @Override
    public final void cast(final Player player) {
        final MainConfig mainConfig = MainConfig.getInstance();
        if (ServerUtil.isCompatible(VersionNMS.V1_9_R2)) {
            final PotionEffectType potionType = this.getPotion();
            final boolean isEnableParticle = mainConfig.isMiscEnableParticlePotion();
            final PotionEffect potion = PotionUtil.createPotion(potionType, 96000, this.grade, true, isEnableParticle);
            player.addPotionEffect(potion);
        }
    }
}
