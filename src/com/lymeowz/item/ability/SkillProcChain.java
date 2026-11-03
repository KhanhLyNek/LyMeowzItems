package com.lymeowz.item.ability;

import com.lymeowz.item.MyItems;
import com.lymeowz.item.stats.CombatMath;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Attenuates weapon ability rolls for damage produced by an earlier ability.
 * A direct hit retains its original lore chance. Each secondary generation
 * keeps 20% of the preceding generation's chance (80% reduction).
 *
 * The thread-local covers synchronous nested Bukkit damage events. The short
 * lived attacker metadata retains a conservative HIGH-WATER depth for delayed
 * ability tasks, without modifying any protected legacy skill implementations.
 */
public final class SkillProcChain {
    public static final int MAX_CHAIN_DEPTH = 4;
    private static final double[] CHANCE_FACTORS = {1.0, 0.2, 0.04, 0.008, 0.0016};
    private static final String SOURCE_KEY = "LyMeowzItem_Ability_Chain";
    private static final long SOURCE_TTL_MS = 5_000L;
    private static final ThreadLocal<Integer> CAST_DEPTH = new ThreadLocal<>();

    private SkillProcChain() { }

    public static double multiplier(int depth) {
        return depth < 0 || depth > MAX_CHAIN_DEPTH ? 0.0 : CHANCE_FACTORS[depth];
    }

    public static double adjustedChance(double originalPercent, int depth) {
        if (!Double.isFinite(originalPercent)) return 0.0;
        return CombatMath.chance(originalPercent) * multiplier(depth);
    }

    /** Testable exact probability roll, with a [0,1) uniform input. */
    public static boolean roll(double originalPercent, int depth, double uniform) {
        return CombatMath.rollChance(adjustedChance(originalPercent, depth), uniform);
    }

    /** Depth zero is the original attack; secondary hits begin at depth one. */
    public static int depthForHit(LivingEntity attacker, boolean secondary) {
        if (!secondary) return 0;
        final Integer activeCast = CAST_DEPTH.get();
        if (activeCast != null) return Math.min(MAX_CHAIN_DEPTH + 1, activeCast + 1);
        final Source previous = readSource(attacker);
        return previous == null ? 1 : Math.min(MAX_CHAIN_DEPTH + 1, previous.depth + 1);
    }

    /** Keep the origin context through a synchronous cast and always restore it. */
    public static void castAtDepth(LivingEntity attacker, int depth, Runnable cast) {
        final Integer previous = CAST_DEPTH.get();
        CAST_DEPTH.set(depth);
        try {
            if (attacker != null && depth >= 0 && depth <= MAX_CHAIN_DEPTH) {
                recordSource(attacker, depth);
            }
            cast.run();
        } finally {
            if (previous == null) CAST_DEPTH.remove();
            else CAST_DEPTH.set(previous);
        }
    }

    private static void recordSource(LivingEntity attacker, int depth) {
        final Plugin owner = JavaPlugin.getPlugin(MyItems.class);
        final Source previous = readSource(attacker);
        // A pending task from an older generation must not regain a high
        // proc rate when a new direct hit happens during the same short window.
        final int retainedDepth = previous == null ? depth : Math.max(previous.depth, depth);
        final long expiresAt = System.currentTimeMillis() + SOURCE_TTL_MS;
        attacker.setMetadata(SOURCE_KEY,
                new FixedMetadataValue(owner, new Source(retainedDepth, expiresAt)));
    }

    private static Source readSource(LivingEntity attacker) {
        if (attacker == null) return null;
        final Plugin owner = JavaPlugin.getPlugin(MyItems.class);
        for (MetadataValue value : attacker.getMetadata(SOURCE_KEY)) {
            if (value.getOwningPlugin() != owner) continue;
            if (value.value() instanceof Source source
                    && source.expiresAt >= System.currentTimeMillis()) {
                return source;
            }
            attacker.removeMetadata(SOURCE_KEY, owner);
        }
        return null;
    }

    private record Source(int depth, long expiresAt) { }
}
