// 
// Decompiled by Procyon v0.5.36
// 

package com.praya.myitems.manager.game;

import com.praya.myitems.MyItems;

public class GameManager implements com.lymeowz.item.manager.GameServices
{
    private final MyItems owner;
    private final AbilityWeaponManager abilityWeaponManager;
    private final ElementManager elementManager;
    private final ItemManager itemManager;
    private final ItemTypeManager itemTypeManager;
    private final ItemTierManager itemTierManager;
    private final ItemGeneratorManager itemGeneratorManager;
    private final ItemSetManager itemSetManager;
    private final PassiveEffectManager passiveEffectManager;
    private final PowerManager powerManager;
    private final RequirementManager requirementManager;
    private final SocketManager socketManager;
    private final LoreStatsManager statsManager;
    private final MenuManager menuManager;
    
    public GameManager(final MyItems plugin) {
        this.owner = plugin;
        this.abilityWeaponManager = new AbilityWeaponManager(plugin);
        this.elementManager = new ElementManager(plugin);
        this.itemManager = new ItemManager(plugin);
        this.itemTypeManager = new ItemTypeManager(plugin);
        this.itemTierManager = new ItemTierManager(plugin);
        this.itemGeneratorManager = new ItemGeneratorManager(plugin);
        this.itemSetManager = new ItemSetManager(plugin);
        this.passiveEffectManager = new PassiveEffectManager(plugin);
        this.powerManager = new PowerManager(plugin);
        this.requirementManager = new RequirementManager(plugin);
        this.socketManager = new SocketManager(plugin);
        this.statsManager = new LoreStatsManager(plugin);
        this.menuManager = new MenuManager(plugin);
    }
    
    public final AbilityWeaponManager getAbilityWeaponManager() {
        return this.abilityWeaponManager;
    }
    
    public final ElementManager getElementManager() {
        return this.elementManager;
    }
    
    public final ItemManager getItemManager() {
        return this.itemManager;
    }
    
    public final ItemTypeManager getItemTypeManager() {
        return this.itemTypeManager;
    }
    
    public final ItemTierManager getItemTierManager() {
        return this.itemTierManager;
    }
    
    public final ItemGeneratorManager getItemGeneratorManager() {
        return this.itemGeneratorManager;
    }
    
    public final ItemSetManager getItemSetManager() {
        return this.itemSetManager;
    }
    
    public final PassiveEffectManager getPassiveEffectManager() {
        return this.passiveEffectManager;
    }
    
    public final PowerManager getPowerManager() {
        return this.powerManager;
    }
    
    public final RequirementManager getRequirementManager() {
        return this.requirementManager;
    }
    
    public final SocketManager getSocketManager() {
        return this.socketManager;
    }
    
    public final LoreStatsManager getStatsManager() {
        return this.statsManager;
    }
    
    public final MenuManager getMenuManager() {
        return this.menuManager;
    }

    @Override
    public final com.lymeowz.item.manager.WeaponRegistry weaponRegistry() {
        return this.owner.getRegisterManager().getRegisterAbilityWeaponManager();
    }

    @Override
    public final com.lymeowz.item.ability.WeaponProperties weaponProperties(final String id) {
        return this.abilityWeaponManager.getAbilityWeaponProperties(id);
    }

    @Override
    public final java.util.Collection<String> weaponPropertyIds() {
        return java.util.Collections.unmodifiableCollection(
                this.abilityWeaponManager.getAbilityWeaponPropertiesIDs());
    }

    @Override
    public final com.lymeowz.item.manager.ElementRegistry elements() {
        return this.elementManager;
    }

    @Override
    public final com.lymeowz.item.manager.ItemCatalog items() {
        return this.itemManager;
    }
}
