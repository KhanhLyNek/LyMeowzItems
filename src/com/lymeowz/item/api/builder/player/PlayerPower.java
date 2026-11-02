// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.builder.player;

import com.lymeowz.lib.utility.PlayerUtil;
import org.bukkit.OfflinePlayer;
import java.util.UUID;

public class PlayerPower
{
    private final UUID uuid;
    private final PlayerPowerCooldown powerCooldown;
    
    public PlayerPower(final OfflinePlayer player) {
        this.uuid = player.getUniqueId();
        this.powerCooldown = new PlayerPowerCooldown();
    }
    
    public final UUID getUUID() {
        return this.uuid;
    }
    
    public final PlayerPowerCooldown getPowerCooldown() {
        return this.powerCooldown;
    }
    
    public final OfflinePlayer getOfflinePlayer() {
        return PlayerUtil.getPlayer(this.getUUID());
    }
}
