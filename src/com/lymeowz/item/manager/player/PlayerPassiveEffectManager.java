// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.manager.player;

import org.bukkit.OfflinePlayer;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.api.builder.player.PlayerPassiveEffectCooldown;
import java.util.UUID;
import java.util.HashMap;
import com.lymeowz.item.builder.handler.HandlerManager;

public class PlayerPassiveEffectManager extends HandlerManager
{
    private final HashMap<UUID, PlayerPassiveEffectCooldown> mapPassiveEffectCooldown;
    
    protected PlayerPassiveEffectManager(final MyItems plugin) {
        super(plugin);
        this.mapPassiveEffectCooldown = new HashMap<UUID, PlayerPassiveEffectCooldown>();
    }
    
    public final PlayerPassiveEffectCooldown getPlayerPassiveEffectCooldown(final OfflinePlayer player) {
        final UUID uuid = player.getUniqueId();
        if (!this.mapPassiveEffectCooldown.containsKey(uuid)) {
            this.mapPassiveEffectCooldown.put(uuid, new PlayerPassiveEffectCooldown());
        }
        return this.mapPassiveEffectCooldown.get(uuid);
    }
    
    public final void removePlayerPassiveEffectCooldown(final OfflinePlayer player) {
        final UUID uuid = player.getUniqueId();
        this.mapPassiveEffectCooldown.remove(uuid);
    }
}
