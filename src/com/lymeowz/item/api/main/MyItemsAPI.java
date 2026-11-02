// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item.api.main;

import org.bukkit.plugin.java.JavaPlugin;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.api.manager.player.PlayerManagerAPI;
import com.lymeowz.item.api.manager.game.GameManagerAPI;

public class MyItemsAPI
{
    private final GameManagerAPI gameManagerAPI;
    private final PlayerManagerAPI playerManagerAPI;
    
    private MyItemsAPI(final MyItems plugin) {
        this.gameManagerAPI = new GameManagerAPI(plugin);
        this.playerManagerAPI = new PlayerManagerAPI(plugin);
    }
    
    public static final MyItemsAPI getInstance() {
        return MyItemsAPIHelper.instance;
    }
    
    public final GameManagerAPI getGameManagerAPI() {
        return this.gameManagerAPI;
    }
    
    public final PlayerManagerAPI getPlayerManagerAPI() {
        return this.playerManagerAPI;
    }
    
    private static class MyItemsAPIHelper
    {
        private static final MyItemsAPI instance;
        
        static {
            final MyItems plugin = (MyItems)JavaPlugin.getProvidingPlugin((Class)MyItems.class);
            instance = new MyItemsAPI(plugin);
        }
    }
}
