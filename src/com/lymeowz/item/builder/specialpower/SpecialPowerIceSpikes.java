// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.builder.specialpower;

import org.bukkit.entity.Player;
import com.lymeowz.item.manager.game.LoreStatsManager;
import com.lymeowz.item.manager.game.GameManager;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import com.lymeowz.lib.core.enums.branch.ParticleEnum;
import org.bukkit.potion.PotionEffectType;
import com.lymeowz.lib.utility.CombatUtil;
import com.lymeowz.lib.utility.BlockUtil;
import org.bukkit.Material;
import com.lymeowz.lib.core.enums.branch.SoundEnum;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import org.bukkit.util.Vector;
import java.util.Set;
import java.util.Collection;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashSet;
import com.lymeowz.lib.utility.PlayerUtil;
import org.bukkit.Location;
import com.lymeowz.item.config.plugin.MainConfig;
import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.item.MyItems;
import org.bukkit.entity.LivingEntity;
import com.lymeowz.item.api.builder.power.PowerSpecialEnum;
import com.lymeowz.item.builder.abs.SpecialPower;

public class SpecialPowerIceSpikes extends SpecialPower
{
    private static final PowerSpecialEnum special;

    static {
        special = PowerSpecialEnum.ICE_SPIKES;
    }
    
    public SpecialPowerIceSpikes() {
        super(SpecialPowerIceSpikes.special);
    }
    
    public final int getDuration() {
        return SpecialPowerIceSpikes.special.getDuration();
    }
    
    @Override
    public final void cast(final LivingEntity caster) {
        final MyItems plugin = (MyItems)JavaPlugin.getProvidingPlugin((Class)MyItems.class);
        final GameManager gameManager = plugin.getGameManager();
        final LoreStatsManager statsManager = gameManager.getStatsManager();
        final MainConfig mainConfig = MainConfig.getInstance();
        final Location horizontalLoc = new Location(caster.getLocation().getWorld(), 0.0, 0.0, 0.0, caster.getLocation().getYaw(), 0.0f);
        final Vector aim = horizontalLoc.getDirection().multiply(2);
        final int duration = this.getDuration();
        final double weaponDamage = statsManager.getLoreStatsWeapon(caster).getDamage();
        final double skillDamage = SpecialPowerIceSpikes.special.getBaseAdditionalDamage() + SpecialPowerIceSpikes.special.getBasePercentDamage() * weaponDamage / 100.0;
        final Collection<Player> players = (Collection<Player>)PlayerUtil.getNearbyPlayers(caster.getLocation(), mainConfig.getEffectRange());
        final Set<LivingEntity> listEntity = new HashSet<LivingEntity>();
        new BukkitRunnable() {
            final int limit = 5;
            final int range = 2;
            int t = 0;
            Location loc = caster.getLocation().add(aim);
            
            public void run() {
                if (this.t >= 5) {
                    this.cancel();
                    return;
                }
                Bridge.getBridgeSound().playSound(players, this.loc, SoundEnum.BLOCK_GLASS_BREAK, 0.8f, 1.0f);
                for (int indexSpikes = 0; indexSpikes < 3; ++indexSpikes) {
                    this.loc = this.loc.add(0.0, (double)indexSpikes, 0.0);
                    if (this.loc.getBlock().getType().equals((Object)Material.AIR)) {
                        BlockUtil.set(this.loc.getBlock().getLocation());
                        this.loc.getBlock().setType(Material.PACKED_ICE);
                        for (final LivingEntity unit : CombatUtil.getNearbyUnits(this.loc, 2.0)) {
                            if (!unit.equals(caster) && !listEntity.contains(unit)) {
                                listEntity.add(unit);
                                CombatUtil.applyPotion(unit, com.lymeowz.lib.utility.PotionUtil.getPotionEffectType("SLOW"), duration, 4);
                                CombatUtil.skillDamage(caster, unit, skillDamage);
                                Bridge.getBridgeParticle().playParticle(players, ParticleEnum.SNOW_SHOVEL, this.loc, 10, 0.2, 0.2, 0.2, 0.10000000149011612);
                            }
                        }
                        new BukkitRunnable() {
                            final Location iceLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
                            
                            public void run() {
                                BlockUtil.remove(this.iceLoc.getBlock().getLocation());
                                if (this.iceLoc.getBlock().getType().equals((Object)Material.PACKED_ICE)) {
                                    this.iceLoc.getBlock().setType(Material.AIR);
                                }
                            }
                        }.runTaskLater((Plugin)plugin, 9L);
                    }
                    this.loc = this.loc.subtract(0.0, (double)indexSpikes, 0.0);
                }
                this.loc.add(aim);
                ++this.t;
            }
        }.runTaskTimer((Plugin)plugin, 0L, 3L);
    }
}
