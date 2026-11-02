// 
// Decompiled by Procyon v0.5.36
// 

package com.lymeowz.item;

import com.lymeowz.item.utility.main.AntiBugUtil;
import org.bukkit.event.Listener;
import com.lymeowz.lib.utility.PluginUtil;
import com.lymeowz.item.listener.main.ListenerPlayerSwapHandItems;
import com.lymeowz.item.listener.main.ListenerBlockExplode;
import org.bukkit.plugin.Plugin;
import com.lymeowz.lib.utility.ServerEventUtil;
import com.lymeowz.item.listener.support.ListenerPlayerHealthMaxChange;
import com.lymeowz.item.listener.custom.ListenerPowerSpecialCast;
import com.lymeowz.item.listener.custom.ListenerPowerShootCast;
import com.lymeowz.item.listener.custom.ListenerPowerPreCast;
import com.lymeowz.item.listener.custom.ListenerPowerCommandCast;
import com.lymeowz.item.listener.custom.ListenerMenuOpen;
import com.lymeowz.item.listener.custom.ListenerMenuClose;
import com.lymeowz.item.listener.custom.ListenerCombatCriticalDamage;
import com.lymeowz.item.listener.main.ListenerProjectileHit;
import com.lymeowz.item.listener.main.ListenerEntityShootBow;
import com.lymeowz.item.listener.main.ListenerPlayerRespawn;
import com.lymeowz.item.listener.main.ListenerPlayerJoin;
import com.lymeowz.item.listener.main.ListenerPlayerInteractEntity;
import com.lymeowz.item.listener.main.ListenerPlayerInteract;
import com.lymeowz.item.listener.main.ListenerPlayerItemDamage;
import com.lymeowz.item.listener.main.ListenerInventoryOpen;
import com.lymeowz.item.listener.main.ListenerInventoryDrag;
import com.lymeowz.item.listener.main.ListenerInventoryClick;
import com.lymeowz.item.listener.main.ListenerHeldItem;
import com.lymeowz.item.listener.main.ListenerEntityRegainHealth;
import com.lymeowz.item.listener.main.ListenerEntityDeath;
import com.lymeowz.item.listener.main.ListenerEntityDamageByEntity;
import com.lymeowz.item.listener.main.ListenerEntityDamage;
import com.lymeowz.item.listener.main.ListenerPlayerDropItem;
import com.lymeowz.item.listener.main.ListenerCommand;
import com.lymeowz.item.listener.main.ListenerBlockPhysic;
import com.lymeowz.item.listener.main.ListenerBlockBreak;
import org.bukkit.command.TabCompleter;
import com.lymeowz.item.tabcompleter.TabCompleterFlagRemove;
import com.lymeowz.item.tabcompleter.TabCompleterFlagAdd;
import com.lymeowz.item.tabcompleter.TabCompleterNotCompatible;
import com.lymeowz.item.tabcompleter.TabCompleterUnbreakable;
import com.lymeowz.item.tabcompleter.TabCompleterSocket;
import com.lymeowz.item.tabcompleter.TabCompleterLoreRemove;
import com.lymeowz.item.tabcompleter.TabCompleterEnchantmentRemove;
import com.lymeowz.item.tabcompleter.TabCompleterEnchantmentAdd;
import com.lymeowz.item.tabcompleter.TabCompleterAttributes;
import com.lymeowz.item.tabcompleter.TabCompleterMyItems;
import org.bukkit.command.CommandExecutor;
import com.lymeowz.item.command.CommandFlagClear;
import com.lymeowz.item.command.CommandFlagRemove;
import com.lymeowz.item.command.CommandFlagAdd;
import com.lymeowz.item.command.CommandFlag;
import com.lymeowz.lib.utility.ServerUtil;
import com.lymeowz.lib.core.enums.main.VersionNMS;
import com.lymeowz.item.command.CommandNotCompatible;
import com.lymeowz.item.command.CommandUnbreakable;
import com.lymeowz.item.command.CommandSocket;
import com.lymeowz.item.command.CommandNBTClear;
import com.lymeowz.item.command.CommandLoreSet;
import com.lymeowz.item.command.CommandLoreRemove;
import com.lymeowz.item.command.CommandLoreInsert;
import com.lymeowz.item.command.CommandLoreClear;
import com.lymeowz.item.command.CommandLoreAdd;
import com.lymeowz.item.command.CommandLore;
import com.lymeowz.item.command.CommandItemName;
import com.lymeowz.item.command.CommandEnchantRemove;
import com.lymeowz.item.command.CommandEnchantClear;
import com.lymeowz.item.command.CommandEnchantAdd;
import com.lymeowz.item.command.CommandEnchant;
import com.lymeowz.item.command.CommandAttributes;
import com.lymeowz.item.command.CommandMyItems;
import java.util.List;
import com.lymeowz.lib.modern.Paper26PluginFolderMigration;
import com.lymeowz.item.manager.register.RegisterManager;
import com.lymeowz.item.manager.task.TaskManager;
import com.lymeowz.item.manager.game.GameManager;
import com.lymeowz.item.manager.player.PlayerManager;
import com.lymeowz.item.manager.plugin.PluginManager;
import com.lymeowz.lib.core.builder.face.Agartha;
import org.bukkit.plugin.java.JavaPlugin;

public class MyItems extends JavaPlugin implements Agartha
{
    private final String type = "Premium";
    private final String placeholder = "myitems";
    private PluginManager pluginManager;
    private PlayerManager playerManager;
    private GameManager gameManager;
    private TaskManager taskManager;
    private RegisterManager registerManager;
    
    public String getPluginName() {
        return this.getName();
    }
    
    public String getPluginType() {
        return "Premium";
    }
    
    public String getPluginVersion() {
        return this.getDescription().getVersion();
    }
    
    public String getPluginPlaceholder() {
        return "myitems";
    }
    
    public String getPluginWebsite() {
        return this.getPluginManager().getPluginPropertiesManager().getWebsite();
    }
    
    public String getPluginLatest() {
        return this.getPluginManager().getPluginPropertiesManager().getPluginTypeVersion(this.getPluginType());
    }
    
    public List<String> getPluginDevelopers() {
        return this.getPluginManager().getPluginPropertiesManager().getDevelopers();
    }
    
    public final PluginManager getPluginManager() {
        return this.pluginManager;
    }
    
    public final GameManager getGameManager() {
        return this.gameManager;
    }
    
    public final PlayerManager getPlayerManager() {
        return this.playerManager;
    }
    
    public final TaskManager getTaskManager() {
        return this.taskManager;
    }
    
    public final RegisterManager getRegisterManager() {
        return this.registerManager;
    }
    
    @Override
    public void onLoad() {
        // Copy legacy configs and item YAML before any managers initialize.
        Paper26PluginFolderMigration.copyMissing(this, "MyItems");
    }

    public void onEnable() {
        this.setPluginManager();
        if (!isEnabled()) return; // An initialization callback may disable the plugin.
        this.setPlayerManager();
        this.setGameManager();
        this.setTaskManager();
        this.setRegisterManager();
        this.getPluginManager().getDependencyManager().getDependencyConfig().setup();
        this.getPluginManager().getHookManager().getHookConfig().setup();
        // A missing hard dependency disables the plugin in HookConfig.
        // Do not create gameplay state, commands or event listeners afterward.
        if (!isEnabled()) return;
        this.setup();
        this.registerCommand();
        this.registerTabComplete();
        this.registerEvent();
        this.registerPlaceholder();
    }
    
    private final void setPluginManager() {
        (this.pluginManager = new PluginManager(this)).initialize();
    }
    
    private final void setGameManager() {
        this.gameManager = new GameManager(this);
    }
    
    private final void setPlayerManager() {
        this.playerManager = new PlayerManager(this);
    }
    
    private final void setTaskManager() {
        this.taskManager = new TaskManager(this);
    }
    
    private final void setRegisterManager() {
        this.registerManager = new RegisterManager(this);
    }
    
    private final void setup() {
        this.gameManager.getAbilityWeaponManager().getAbilityWeaponConfig().setup();
        this.gameManager.getElementManager().getElementConfig().setup();
        this.gameManager.getPowerManager().getPowerCommandManager().getPowerCommandConfig().setup();
        this.gameManager.getPowerManager().getPowerSpecialManager().getPowerSpecialConfig().setup();
        this.gameManager.getSocketManager().getSocketConfig().setup();
        this.gameManager.getItemManager().getItemConfig().setup();
        this.gameManager.getItemTypeManager().getItemTypeConfig().setup();
        this.gameManager.getItemTierManager().getItemTierConfig().setup();
        this.gameManager.getItemGeneratorManager().getItemGeneratorConfig().setup();
        this.gameManager.getItemSetManager().getItemSetConfig().setup();
    }
    
    private final void registerPlaceholder() {
        this.getPluginManager().getPlaceholderManager().registerAll();
    }
    
    private final void registerCommand() {
        final CommandExecutor commandMyItems = (CommandExecutor)new CommandMyItems(this);
        final CommandExecutor commandAttributes = (CommandExecutor)new CommandAttributes(this);
        final CommandExecutor commandEnchant = (CommandExecutor)new CommandEnchant(this);
        final CommandExecutor commandEnchantAdd = (CommandExecutor)new CommandEnchantAdd(this);
        final CommandExecutor commandEnchantClear = (CommandExecutor)new CommandEnchantClear(this);
        final CommandExecutor commandEnchantRemove = (CommandExecutor)new CommandEnchantRemove(this);
        final CommandExecutor commandItemName = (CommandExecutor)new CommandItemName(this);
        final CommandExecutor commandLore = (CommandExecutor)new CommandLore(this);
        final CommandExecutor commandLoreAdd = (CommandExecutor)new CommandLoreAdd(this);
        final CommandExecutor commandLoreClear = (CommandExecutor)new CommandLoreClear(this);
        final CommandExecutor commandLoreInsert = (CommandExecutor)new CommandLoreInsert(this);
        final CommandExecutor commandLoreRemove = (CommandExecutor)new CommandLoreRemove(this);
        final CommandExecutor commandLoreSet = (CommandExecutor)new CommandLoreSet(this);
        final CommandExecutor commandNBTClear = (CommandExecutor)new CommandNBTClear(this);
        final CommandExecutor commandSocket = (CommandExecutor)new CommandSocket(this);
        final CommandExecutor commandUnbreakable = (CommandExecutor)new CommandUnbreakable(this);
        final CommandExecutor commandNotCompatible = (CommandExecutor)new CommandNotCompatible(this);
        final CommandExecutor commandFlag = (CommandExecutor)(ServerUtil.isCompatible(VersionNMS.V1_8_R3) ? new CommandFlag(this) : commandNotCompatible);
        final CommandExecutor commandAddFlag = (CommandExecutor)(ServerUtil.isCompatible(VersionNMS.V1_8_R3) ? new CommandFlagAdd(this) : commandNotCompatible);
        final CommandExecutor commandRemoveFlag = (CommandExecutor)(ServerUtil.isCompatible(VersionNMS.V1_8_R3) ? new CommandFlagRemove(this) : commandNotCompatible);
        final CommandExecutor commandClearFlag = (CommandExecutor)(ServerUtil.isCompatible(VersionNMS.V1_8_R3) ? new CommandFlagClear(this) : commandNotCompatible);
        this.getCommand("LyMeowzItem").setExecutor(commandMyItems);
        this.getCommand("ItemAtt").setExecutor(commandAttributes);
        this.getCommand("Enchant").setExecutor(commandEnchant);
        this.getCommand("EnchantAdd").setExecutor(commandEnchantAdd);
        this.getCommand("EnchantRemove").setExecutor(commandEnchantRemove);
        this.getCommand("EnchantClear").setExecutor(commandEnchantClear);
        this.getCommand("ItemName").setExecutor(commandItemName);
        this.getCommand("Lore").setExecutor(commandLore);
        this.getCommand("LoreSet").setExecutor(commandLoreSet);
        this.getCommand("LoreInsert").setExecutor(commandLoreInsert);
        this.getCommand("LoreAdd").setExecutor(commandLoreAdd);
        this.getCommand("LoreRemove").setExecutor(commandLoreRemove);
        this.getCommand("LoreClear").setExecutor(commandLoreClear);
        this.getCommand("NBTClear").setExecutor(commandNBTClear);
        this.getCommand("Socket").setExecutor(commandSocket);
        this.getCommand("Unbreakable").setExecutor(commandUnbreakable);
        this.getCommand("Flag").setExecutor(commandFlag);
        this.getCommand("FlagAdd").setExecutor(commandAddFlag);
        this.getCommand("FlagRemove").setExecutor(commandRemoveFlag);
        this.getCommand("FlagClear").setExecutor(commandClearFlag);
    }
    
    private final void registerTabComplete() {
        final TabCompleter tabCompleterMyItems = (TabCompleter)new TabCompleterMyItems(this);
        final TabCompleter tabCompleterAttributes = (TabCompleter)new TabCompleterAttributes(this);
        final TabCompleter tabCompleterEnchantmentAdd = (TabCompleter)new TabCompleterEnchantmentAdd(this);
        final TabCompleter tabCompleterEnchantmentRemove = (TabCompleter)new TabCompleterEnchantmentRemove(this);
        final TabCompleter tabCompleterLoreRemove = (TabCompleter)new TabCompleterLoreRemove(this);
        final TabCompleter tabCompleterSocket = (TabCompleter)new TabCompleterSocket(this);
        final TabCompleter tabCompleterUnbreakable = (TabCompleter)new TabCompleterUnbreakable(this);
        final TabCompleter tabCompleterNotCompatible = (TabCompleter)new TabCompleterNotCompatible(this);
        final TabCompleter tabCompleterFlagAdd = (TabCompleter)(ServerUtil.isCompatible(VersionNMS.V1_8_R3) ? new TabCompleterFlagAdd(this) : tabCompleterNotCompatible);
        final TabCompleter tabCompleterFlagRemove = (TabCompleter)(ServerUtil.isCompatible(VersionNMS.V1_8_R3) ? new TabCompleterFlagRemove(this) : tabCompleterNotCompatible);
        this.getCommand("LyMeowzItem").setTabCompleter(tabCompleterMyItems);
        this.getCommand("ItemAtt").setTabCompleter(tabCompleterAttributes);
        this.getCommand("EnchantAdd").setTabCompleter(tabCompleterEnchantmentAdd);
        this.getCommand("EnchantRemove").setTabCompleter(tabCompleterEnchantmentRemove);
        this.getCommand("LoreRemove").setTabCompleter(tabCompleterLoreRemove);
        this.getCommand("Socket").setTabCompleter(tabCompleterSocket);
        this.getCommand("Unbreakable").setTabCompleter(tabCompleterUnbreakable);
        this.getCommand("FlagAdd").setTabCompleter(tabCompleterFlagAdd);
        this.getCommand("FlagRemove").setTabCompleter(tabCompleterFlagRemove);
    }
    
    private final void registerEvent() {
        final Listener listenerBlockBreak = (Listener)new ListenerBlockBreak(this);
        final Listener listenerBlockPhysic = (Listener)new ListenerBlockPhysic(this);
        final Listener listenerCommand = (Listener)new ListenerCommand(this);
        final Listener listenerPlayerDropItem = (Listener)new ListenerPlayerDropItem(this);
        final Listener listenerEntityDamage = (Listener)new ListenerEntityDamage(this);
        final Listener listenerEntityDamageByEntity = (Listener)new ListenerEntityDamageByEntity(this);
        final Listener listenerEntityDeath = (Listener)new ListenerEntityDeath(this);
        final Listener listenerEntityRegainHealth = (Listener)new ListenerEntityRegainHealth(this);
        final Listener listenerHeldItem = (Listener)new ListenerHeldItem(this);
        final Listener listenerInventoryClick = (Listener)new ListenerInventoryClick(this);
        final Listener listenerInventoryDrag = (Listener)new ListenerInventoryDrag(this);
        final Listener listenerInventoryOpen = (Listener)new ListenerInventoryOpen(this);
        final Listener listenerPlayerItemDamage = (Listener)new ListenerPlayerItemDamage(this);
        final Listener listenerPlayerInteract = (Listener)new ListenerPlayerInteract(this);
        final Listener listenerPlayerInteractEntity = (Listener)new ListenerPlayerInteractEntity(this);
        final Listener listenerPlayerJoin = (Listener)new ListenerPlayerJoin(this);
        final Listener listenerPlayerRespawn = (Listener)new ListenerPlayerRespawn(this);
        final Listener listenerEntityShootBowEvent = (Listener)new ListenerEntityShootBow(this);
        final Listener listenerProjectileHit = (Listener)new ListenerProjectileHit(this);
        final Listener listenerCombatCriticalDamage = (Listener)new ListenerCombatCriticalDamage(this);
        final Listener listenerMenuClose = (Listener)new ListenerMenuClose(this);
        final Listener listenerMenuOpen = (Listener)new ListenerMenuOpen(this);
        final Listener listenerPowerCommandCast = (Listener)new ListenerPowerCommandCast(this);
        final Listener listenerPowerPreCast = (Listener)new ListenerPowerPreCast(this);
        final Listener listenerPowerShootCast = (Listener)new ListenerPowerShootCast(this);
        final Listener listenerPowerSpecialCast = (Listener)new ListenerPowerSpecialCast(this);
        final Listener listenerPlayerHealthMaxChange = (Listener)new ListenerPlayerHealthMaxChange(this);
        ServerEventUtil.registerEvent((Plugin)this, listenerBlockBreak);
        ServerEventUtil.registerEvent((Plugin)this, listenerBlockPhysic);
        ServerEventUtil.registerEvent((Plugin)this, listenerCommand);
        ServerEventUtil.registerEvent((Plugin)this, listenerEntityDamage);
        ServerEventUtil.registerEvent((Plugin)this, listenerEntityDamageByEntity);
        ServerEventUtil.registerEvent((Plugin)this, listenerPlayerDropItem);
        ServerEventUtil.registerEvent((Plugin)this, listenerEntityDeath);
        ServerEventUtil.registerEvent((Plugin)this, listenerEntityRegainHealth);
        ServerEventUtil.registerEvent((Plugin)this, listenerHeldItem);
        ServerEventUtil.registerEvent((Plugin)this, listenerInventoryClick);
        ServerEventUtil.registerEvent((Plugin)this, listenerInventoryDrag);
        ServerEventUtil.registerEvent((Plugin)this, listenerInventoryOpen);
        ServerEventUtil.registerEvent((Plugin)this, listenerPlayerItemDamage);
        ServerEventUtil.registerEvent((Plugin)this, listenerPlayerInteract);
        ServerEventUtil.registerEvent((Plugin)this, listenerPlayerInteractEntity);
        ServerEventUtil.registerEvent((Plugin)this, listenerPlayerJoin);
        ServerEventUtil.registerEvent((Plugin)this, listenerPlayerRespawn);
        ServerEventUtil.registerEvent((Plugin)this, listenerEntityShootBowEvent);
        ServerEventUtil.registerEvent((Plugin)this, listenerProjectileHit);
        ServerEventUtil.registerEvent((Plugin)this, listenerCombatCriticalDamage);
        ServerEventUtil.registerEvent((Plugin)this, listenerMenuClose);
        ServerEventUtil.registerEvent((Plugin)this, listenerMenuOpen);
        ServerEventUtil.registerEvent((Plugin)this, listenerPowerCommandCast);
        ServerEventUtil.registerEvent((Plugin)this, listenerPowerPreCast);
        ServerEventUtil.registerEvent((Plugin)this, listenerPowerShootCast);
        ServerEventUtil.registerEvent((Plugin)this, listenerPowerSpecialCast);
        ServerEventUtil.registerEvent((Plugin)this, listenerPlayerHealthMaxChange);
        if (ServerUtil.isCompatible(VersionNMS.V1_9_R2)) {
            final Listener listenerBlockExplode = (Listener)new ListenerBlockExplode(this);
            final Listener listenerPlayerSwapHandItems = (Listener)new ListenerPlayerSwapHandItems(this);
            ServerEventUtil.registerEvent((Plugin)this, listenerBlockExplode);
            ServerEventUtil.registerEvent((Plugin)this, listenerPlayerSwapHandItems);
        }
        // Legacy SkillAPI, MythicMobs v4, CombatStamina, LifeEssence event
        // classes do not exist on Paper 26.3. They are not registered here.
        // The core MyItems combat, item, ability and health-max listeners above
        // remain registered with unchanged YAML/item formula semantics.
        if (PluginUtil.isPluginInstalled("MythicMobs")) {
            if (!com.lymeowz.item.modern.Paper26MythicMobsEventBridge.register(this)) {
                getLogger().warning("MythicMobs 5 optional event integration unavailable;"
                        + " legacy drop tables and item equip callbacks remain inactive.");
            }
        }
        for (final String obsolete : new String[] {"SkillAPI",
                                                     "LifeEssence", "CombatStamina"}) {
            if (PluginUtil.isPluginInstalled(obsolete)) {
                getLogger().warning("Legacy " + obsolete + " event bridge is retired"
                        + " on Paper 26.3; use provider-specific current APIs."
                        + " Old custom drops/stamina callbacks are NOT migrated.");
            }
        }
    }
    
    public void onDisable() {
        AntiBugUtil.antiBugCustomStats();
    }
}
