package com.lymeowz.item.ci;

import com.lymeowz.lib.modern.Paper26LegacyItemTags;
import com.lymeowz.lib.core.bridge.unity.Bridge;
import net.kyori.adventure.nbt.*;
import org.bukkit.Material;
import com.lymeowz.lib.utility.TextUtil;
import com.lymeowz.lib.utility.EquipmentUtil;
import com.lymeowz.lib.modern.Paper26ItemModels;
import com.lymeowz.item.modern.Paper26ExternalModels;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import com.lymeowz.lib.AgarthaLib;
import com.lymeowz.lib.api.builder.support.SupportEssentials;
import com.lymeowz.lib.api.manager.plugin.SupportManagerAPI;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import com.lymeowz.item.MyItems;
import com.lymeowz.item.manager.game.AbilityWeaponManager;
import com.lymeowz.item.manager.register.RegisterAbilityWeaponManager;
import com.lymeowz.item.api.builder.ability.AbilityWeapon;
import com.lymeowz.item.api.builder.ability.AbilityItemWeapon;
import com.lymeowz.item.api.builder.ability.AbilityWeaponAttributeBaseDamage;
import com.lymeowz.item.api.builder.ability.AbilityWeaponAttributeCastDamage;
import com.lymeowz.item.api.builder.ability.AbilityWeaponAttributeEffect;
import com.lymeowz.item.api.builder.lorestats.LoreStatsEnum;
import com.lymeowz.item.api.builder.lorestats.LoreStatsOption;
import com.lymeowz.item.manager.game.LoreStatsManager;
import com.lymeowz.item.modern.Paper26StatsMath;
import com.lymeowz.item.permission.ItemPermissions;
import com.lymeowz.lib.permission.PermissionBridge;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Real Paper server fixture. Kept outside src/ and production jars.
 * Exercises byte serialization -> custom_data -> BridgeTagsNBT without
 * modifying any player's inventory or persistent world data.
 */
public final class Paper26LiveProbe extends JavaPlugin {
    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    /** Validate migrated generator YAML and the original live gameplay builder. */
    private void verifyItemGenerators() {
        final MyItems plugin = (MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem");
        final com.lymeowz.item.manager.game.GameManager game = plugin.getGameManager();
        final com.lymeowz.item.manager.game.ItemGeneratorManager legacy =
                game.getItemGeneratorManager();
        final com.lymeowz.item.manager.GameServices services =
                com.lymeowz.item.manager.GameAccess.of(
                        (com.lymeowz.item.LyMeowzItem) plugin);
        check(services.itemGenerators() == legacy,
                "canonical item generator registry shares the original manager instance");
        check(services.itemGenerators().ids().containsAll(legacy.getItemGeneratorIDs()),
                "generator IDs remain unchanged");
        check(services.itemGenerators().find("missing-generator-id") == null,
                "canonical generator registry preserves missing IDs");
        try {
            services.itemGenerators().ids().clear();
            throw new AssertionError("canonical generator IDs must be immutable");
        } catch (UnsupportedOperationException expected) { }

        for (final String id : legacy.getItemGeneratorIDs()) {
            final com.lymeowz.item.api.builder.item.ItemGenerator original =
                    legacy.getItemGenerator(id);
            final com.lymeowz.item.item.ItemGeneratorDefinition view =
                    services.itemGenerators().find(id.toUpperCase(java.util.Locale.ROOT));
            check(view == original, "canonical lookup returns the original live generator");
            check(view.id().equals(id) && view.displayName().equals(original.getDisplayName()),
                    "generator ID and display name preserved");
            check(view.typeWeights().equals(original.getMapPossibilityType())
                            && view.tierWeights().equals(original.getMapPossibilityTier()),
                    "generator type/tier probability weights preserved");
            try {
                view.typeWeights().clear();
                throw new AssertionError("generator type weights must be read-only");
            } catch (UnsupportedOperationException expected) { }
            try {
                view.loreLines().clear();
                throw new AssertionError("generator lore must be read-only");
            } catch (UnsupportedOperationException expected) { }
        }

        final java.util.Collection<String> types = game.getItemTypeManager().getItemTypeIDs();
        final java.util.Collection<String> tiers = game.getItemTierManager().getItemTierIDs();
        check(!types.isEmpty() && !tiers.isEmpty(), "original type/tier configs loaded");
        final String typeId = types.iterator().next();
        final String tierId = tiers.iterator().next();
        final org.bukkit.configuration.file.YamlConfiguration yaml =
                new org.bukkit.configuration.file.YamlConfiguration();
        yaml.set("MiXeD_Generator.Display_Name", "&aLegacy Name");
        yaml.set("MiXeD_Generator.Unbreakable", true);
        yaml.set("MiXeD_Generator.ItemFlags", List.of("HIDE_ATTRIBUTES"));
        yaml.set("MiXeD_Generator.Lores", List.of("&7Kept Lore"));
        yaml.set("MiXeD_Generator.Type." + typeId + ".Possibility", 7);
        yaml.set("MiXeD_Generator.Type." + typeId + ".Names", List.of("Legacy Name"));
        yaml.set("MiXeD_Generator.Tier." + tierId + ".Possibility", 13);
        yaml.set("MiXeD_Generator.Tier." + tierId + ".Additional_Lores", List.of("&7extra"));
        yaml.set("Ignored_Scalar_Generator", "not an item generator");
        final java.util.Map<String, com.lymeowz.item.api.builder.item.ItemGenerator> decoded =
                com.lymeowz.item.config.ItemGeneratorDefinitions.read(
                        yaml, game.getItemTypeManager(), game.getItemTierManager());
        check(decoded.size() == 1 && decoded.containsKey("MiXeD_Generator"),
                "generator YAML reader retains exact mixed-case IDs");
        final org.bukkit.configuration.file.YamlConfiguration malformedGenerator =
                new org.bukkit.configuration.file.YamlConfiguration();
        malformedGenerator.set("Broken_Generator.Display_Name", "&aBroken but safe");
        malformedGenerator.set("Broken_Generator.Type", "invalid-section");
        malformedGenerator.set("Broken_Generator.Tier", 12);
        check(com.lymeowz.item.config.ItemGeneratorDefinitions.read(
                        malformedGenerator, game.getItemTypeManager(),
                        game.getItemTierManager()).size() == 1,
                "malformed generator subsections are skipped without an NPE");
        final com.lymeowz.item.api.builder.item.ItemGenerator generator =
                decoded.get("MiXeD_Generator");
        check(generator.isUnbreakable()
                        && generator.getFlags().equals(List.of("HIDE_ATTRIBUTES"))
                        && generator.getLores().equals(List.of("&7Kept Lore")),
                "generator YAML reader retains Unbreakable, ItemFlags alias and lore");
        check(generator.getMapPossibilityType().get(typeId) == 7
                        && generator.getMapPossibilityTier().get(tierId) == 13,
                "generator YAML reader retains configured weights");
        check(generator.getMapType().values().iterator().next().getNames()
                        .equals(List.of("Legacy Name")),
                "legacy Names alias retained");
        check(generator.getMapTier().values().iterator().next().getAdditionalLores()
                        .equals(List.of("&7extra")),
                "legacy Additional_Lores retained");

        final java.util.HashMap<com.lymeowz.item.api.builder.item.ItemType,
                com.lymeowz.item.api.builder.item.ItemGeneratorType> validTypes = new java.util.HashMap<>();
        validTypes.put(game.getItemTypeManager().getItemType(typeId),
                new com.lymeowz.item.api.builder.item.ItemGeneratorType(
                        1, List.of(), List.of("test")));
        final java.util.HashMap<com.lymeowz.item.api.builder.item.ItemTier,
                com.lymeowz.item.api.builder.item.ItemGeneratorTier> missingTiers = new java.util.HashMap<>();
        missingTiers.put(new com.lymeowz.item.api.builder.item.ItemTier(
                        "missing-tier-for-probe", "", "", null),
                new com.lymeowz.item.api.builder.item.ItemGeneratorTier(1, List.of()));
        final com.lymeowz.item.api.builder.item.ItemGenerator missingTierGenerator =
                new com.lymeowz.item.api.builder.item.ItemGenerator(
                        "invalid-tier", "test", false, List.of(), List.of(),
                        validTypes, missingTiers);
        check(missingTierGenerator.generateItem() == null,
                "generator safely skips removed/missing tiers without throwing");
        getLogger().info("PASS: legacy item generator YAML, live registry and missing-tier guard");
    }

    /** Verify original RPG type/tier builders and canonical YAML readers share live state. */
    private void verifyItemTypesAndTiers() {
        final MyItems plugin = (MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem");
        final com.lymeowz.item.manager.game.GameManager game = plugin.getGameManager();
        final com.lymeowz.item.manager.GameServices services =
                com.lymeowz.item.manager.GameAccess.of(
                        (com.lymeowz.item.LyMeowzItem) plugin);
        check(services.itemTypes() == game.getItemTypeManager()
                        && services.itemTiers() == game.getItemTierManager(),
                "canonical item type/tier registries share original live managers");
        check(services.itemTypes().ids().containsAll(
                        game.getItemTypeManager().getItemTypeIDs())
                        && services.itemTiers().ids().containsAll(
                                game.getItemTierManager().getItemTierIDs()),
                "canonical item type/tier IDs match original configured IDs");
        check(services.itemTypes().find("missing-type-id") == null
                        && services.itemTiers().find("missing-tier-id") == null,
                "canonical type/tier registries preserve missing-ID behavior");
        try {
            services.itemTypes().ids().clear();
            throw new AssertionError("type registry IDs must be read-only");
        } catch (UnsupportedOperationException expected) { }
        try {
            services.itemTiers().ids().clear();
            throw new AssertionError("tier registry IDs must be read-only");
        } catch (UnsupportedOperationException expected) { }

        for (final String id : game.getItemTypeManager().getItemTypeIDs()) {
            final com.lymeowz.item.api.builder.item.ItemType old =
                    game.getItemTypeManager().getItemType(id);
            final com.lymeowz.item.item.ItemTypeDefinition view =
                    services.itemTypes().find(id.toUpperCase(java.util.Locale.ROOT));
            check(view == old && view.id().equals(id)
                            && view.material() == old.getMaterial()
                            && view.legacyData() == old.getData()
                            && view.shiny() == old.isShiny(),
                    "canonical type view returns identical live builder and metadata");
            check(view.enchantmentLevels().size() == old.getEnchantments().size(),
                    "canonical enchantment view retains all original entries");
            try {
                view.enchantmentLevels().clear();
                throw new AssertionError("type enchantment view must be read-only");
            } catch (UnsupportedOperationException expected) { }
        }
        for (final String id : game.getItemTierManager().getItemTierIDs()) {
            final com.lymeowz.item.api.builder.item.ItemTier old =
                    game.getItemTierManager().getItemTier(id);
            final com.lymeowz.item.item.ItemTierDefinition view =
                    services.itemTiers().find(id.toUpperCase(java.util.Locale.ROOT));
            check(view == old && view.id().equals(id)
                            && view.name().equals(old.getName())
                            && view.prefix().equals(old.getPrefix()),
                    "canonical tier view returns identical live builder and metadata");
        }

        final org.bukkit.configuration.file.YamlConfiguration typeYaml =
                new org.bukkit.configuration.file.YamlConfiguration();
        typeYaml.set("MiXeD_Type.Material", "DIAMOND_SWORD");
        typeYaml.set("MiXeD_Type.Data", 2);
        typeYaml.set("MiXeD_Type.Shiny", true);
        typeYaml.set("MiXeD_Type.Modifier.Damage", 1.5d);
        typeYaml.set("Ignored_Scalar_Type", "not an item type");
        typeYaml.set("MiXeD_Type.Enchantment", "not a section");
        typeYaml.set("MiXeD_Type.NBT", 1);
        final java.util.Map<String, com.lymeowz.item.api.builder.item.ItemType> typeRead =
                com.lymeowz.item.config.ItemTypeDefinitions.read(typeYaml);
        check(typeRead.size() == 1 && typeRead.containsKey("MiXeD_Type"),
                "type YAML reader retains original case-sensitive key");
        final com.lymeowz.item.api.builder.item.ItemType parsedType = typeRead.get("MiXeD_Type");
        check(parsedType.getMaterial() == Material.DIAMOND_SWORD
                        && parsedType.getData() == 2 && parsedType.isShiny()
                        && parsedType.getStatsModifier().getModifier(LoreStatsEnum.DAMAGE) == 1.5d,
                "type YAML reader retains material, legacy data, shine and damage modifier");

        final org.bukkit.configuration.file.YamlConfiguration tierYaml =
                new org.bukkit.configuration.file.YamlConfiguration();
        tierYaml.set("MiXeD_Tier.Name", "&aTier");
        tierYaml.set("MiXeD_Tier.Prefix", "&7Prefix");
        tierYaml.set("MiXeD_Tier.Modifier.Health", 1.25d);
        tierYaml.set("Ignored_Scalar_Tier", "not an item tier");
        final java.util.Map<String, com.lymeowz.item.api.builder.item.ItemTier> tierRead =
                com.lymeowz.item.config.ItemTierDefinitions.read(tierYaml);
        check(tierRead.size() == 1 && tierRead.containsKey("MiXeD_Tier"),
                "tier YAML reader retains original case-sensitive key");
        final com.lymeowz.item.api.builder.item.ItemTier parsedTier = tierRead.get("MiXeD_Tier");
        check(parsedTier.getName() != null && parsedTier.getPrefix() != null
                        && parsedTier.getStatsModifier().getModifier(LoreStatsEnum.HEALTH) == 1.25d,
                "tier YAML reader retains legacy color names and health modifier");
        final org.bukkit.configuration.file.YamlConfiguration malformedTier =
                new org.bukkit.configuration.file.YamlConfiguration();
        malformedTier.set("Broken_Tier.Name", "Broken but safe");
        malformedTier.set("Broken_Tier.Modifier", "not-a-section");
        check(com.lymeowz.item.config.ItemTierDefinitions.read(malformedTier)
                        .containsKey("Broken_Tier"),
                "malformed tier modifier section uses default stats without an NPE");
        getLogger().info("PASS: canonical type/tier YAML and original live builder registries");
    }

    /** Validate read-only elemental potion metadata without applying gameplay effects. */
    private void verifyElementPotionViews() {
        final MyItems plugin = (MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem");
        final com.lymeowz.item.manager.ElementRegistry registry =
                com.lymeowz.item.manager.GameAccess.of(
                        (com.lymeowz.item.LyMeowzItem) plugin).elements();
        check(registry == plugin.getGameManager().getElementManager(),
                "canonical element registry shares original live manager");
        for (final String id : registry.ids()) {
            final com.lymeowz.item.element.ElementDefinition definition = registry.find(id);
            final com.lymeowz.item.api.builder.element.Element legacy =
                    plugin.getGameManager().getElementManager().getElementBuild(id);
            check(definition == legacy && definition.potions() == legacy.getPotionBuild(),
                    "canonical element potion definition is the original live builder");
            check(definition.potions().attackerEffects().size()
                            == legacy.getPotionBuild().getPotionAttacker().size()
                            && definition.potions().victimEffects().size()
                            == legacy.getPotionBuild().getPotionVictims().size(),
                    "element potion effect IDs are preserved exactly");
            try {
                definition.potions().attackerEffects().clear();
                throw new AssertionError("canonical element attacker effects are mutable");
            } catch (UnsupportedOperationException expected) { }
            try {
                definition.potions().victimEffects().clear();
                throw new AssertionError("canonical element victim effects are mutable");
            } catch (UnsupportedOperationException expected) { }
        }

        final org.bukkit.potion.PotionEffectType speed =
                com.lymeowz.lib.utility.PotionUtil.getPotionEffectType("speed");
        check(speed != null, "existing potion speed alias resolves on Paper 26.3");
        final java.util.HashMap<org.bukkit.potion.PotionEffectType,
                com.lymeowz.item.api.builder.potion.PotionProperties> source = new java.util.HashMap<>();
        source.put(speed, new com.lymeowz.item.api.builder.potion.PotionProperties(2, 0.5d, 1.5d));
        final com.lymeowz.item.api.builder.element.ElementPotion potion =
                new com.lymeowz.item.api.builder.element.ElementPotion(source, new java.util.HashMap<>());
        final java.util.Map<org.bukkit.potion.PotionEffectType,
                com.lymeowz.item.element.ElementEffect> snapshot = potion.attackerEffects();
        final com.lymeowz.item.element.ElementEffect first = snapshot.get(speed);
        check(first.grade() == 2 && first.chanceScale() == 0.5d
                        && first.durationScale() == 1.5d,
                "canonical effect snapshot retains original grade/chance/duration semantics");
        source.put(speed, new com.lymeowz.item.api.builder.potion.PotionProperties(4, 0.25d, 3.0d));
        check(snapshot.get(speed).grade() == 2 && potion.attackerEffects().get(speed).grade() == 4,
                "previous immutable snapshot does not track mutations in legacy potion builder");
        check(potion.getPotionAttacker() == source,
                "original legacy mutable potion map API remains unchanged");
        getLogger().info("PASS: element registry and immutable potion metadata snapshots");
    }

    /** Exercise both canonical registries using original live manager instances. */
    private void verifyCanonicalEffectAndPowerCommands() {
        final com.lymeowz.lib.LyMeowzLib lib =
                (com.lymeowz.lib.LyMeowzLib) Bukkit.getPluginManager().getPlugin("LyMeowzLib");
        final com.lymeowz.lib.manager.GameServices libServices =
                com.lymeowz.lib.manager.GameAccess.of(lib);
        check(libServices != null, "canonical LyMeowzLib game services available");
        final com.lymeowz.lib.manager.game.EffectTypeManager oldEffects =
                lib.getGameManager().getEffectTypeManager();
        final com.lymeowz.lib.effect.EffectRegistry effects = libServices.effects();
        check(effects == oldEffects && effects.ids().containsAll(oldEffects.getEffectTypeIds()),
                "new effect registry is the original live legacy manager");
        check(effects.find("not-registered-effect-probe") == null,
                "missing effect lookup remains null");
        try {
            effects.ids().clear();
            throw new AssertionError("canonical effect IDs must be immutable");
        } catch (UnsupportedOperationException expected) { }
        for (final String id : effects.ids()) {
            final com.lymeowz.lib.effect.EffectDefinition canonical =
                    effects.find(id.toUpperCase(java.util.Locale.ROOT));
            final com.lymeowz.lib.effect.EffectType legacy = oldEffects.getEffectType(id);
            check(canonical == legacy && id.equals(canonical.id())
                            && canonical.pluginName().equals(legacy.getPlugin().getName()),
                    "effect metadata/lookup preserves live object identity and owner");
        }
        final com.lymeowz.lib.effect.EffectType detachedEffect =
                new com.lymeowz.lib.effect.EffectType(lib, "Standalone-Effect-Probe") {
                    @Override public String getName(final Player player) { return "Probe"; }
                    @Override public List<String> getDescription(final Player player) {
                        return List.of();
                    }
                };
        check(detachedEffect.id().equals("Standalone-Effect-Probe")
                        && detachedEffect.pluginName().equals("LyMeowzLib")
                        && effects.find(detachedEffect.id()) == null,
                "unregistered effect metadata does not mutate the live registry");

        final MyItems itemPlugin = (MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem");
        final com.lymeowz.item.manager.game.PowerCommandManager legacyCommands =
                itemPlugin.getGameManager().getPowerManager().getPowerCommandManager();
        final com.lymeowz.item.manager.PowerCommandRegistry commandRegistry =
                com.lymeowz.item.manager.GameAccess.of(
                        (com.lymeowz.item.LyMeowzItem) itemPlugin).powerCommands();
        check(commandRegistry == legacyCommands
                        && commandRegistry.ids().containsAll(legacyCommands.getPowerCommandIDs()),
                "Power Command canonical registry is the original live manager");
        check(commandRegistry.find("missing-power-command-probe") == null,
                "missing Power Command preserves legacy lookup");
        try {
            commandRegistry.ids().clear();
            throw new AssertionError("power command IDs must be immutable");
        } catch (UnsupportedOperationException expected) { }
        for (final String id : commandRegistry.ids()) {
            final com.lymeowz.item.power.PowerCommandDefinition view =
                    commandRegistry.find(id.toUpperCase(java.util.Locale.ROOT));
            final com.lymeowz.item.api.builder.power.PowerCommandProperties original =
                    legacyCommands.getPowerCommandProperties(id);
            check(view == original && view.keyLore().equals(original.getKeyLore())
                            && view.consumesItem() == original.isConsume()
                            && view.opCommands().equals(original.getCommandOP())
                            && view.consoleCommands().equals(original.getCommandConsole()),
                    "Power Command metadata preserves original execution definition");
            try {
                view.opCommands().clear();
                throw new AssertionError("OP commands must be immutable snapshots");
            } catch (UnsupportedOperationException expected) { }
            try {
                view.consoleCommands().clear();
                throw new AssertionError("console commands must be immutable snapshots");
            } catch (UnsupportedOperationException expected) { }
        }

        final org.bukkit.configuration.file.YamlConfiguration yaml =
                new org.bukkit.configuration.file.YamlConfiguration();
        yaml.set("MiXeD_Command.KeyLore", "&aLegacy power");
        yaml.set("MiXeD_Command.Consume", true);
        yaml.set("MiXeD_Command.Command.OP", List.of("heal {player}", "fly {player}"));
        yaml.set("MiXeD_Command.Command.Console", "give {player} diamond");
        yaml.set("String_Command.KeyLore", "Simple");
        yaml.set("String_Command.Command", "say old command");
        yaml.set("List_Command.KeyLore", "List");
        yaml.set("List_Command.Command", List.of("say first", "say second"));
        yaml.set("Ignored_Scalar", "not a power command");
        yaml.set("Ignored_NoCommands.KeyLore", "ignored");
        final java.util.Map<String, com.lymeowz.item.api.builder.power.PowerCommandProperties> parsed =
                com.lymeowz.item.config.PowerCommandDefinitions.read(yaml);
        check(parsed.size() == 3 && parsed.containsKey("MiXeD_Command"),
                "new Power Command reader keeps case-sensitive IDs and ignores invalid YAML");
        final com.lymeowz.item.api.builder.power.PowerCommandProperties nested =
                parsed.get("MiXeD_Command");
        check(nested.isConsume() && nested.getCommandOP().equals(List.of("heal {player}", "fly {player}"))
                        && nested.getCommandConsole().equals(List.of("give {player} diamond")),
                "nested Power Command OP and Console formats retain order and consumption");
        check(parsed.get("String_Command").getCommandOP().equals(List.of("say old command"))
                        && parsed.get("List_Command").getCommandOP().equals(
                                List.of("say first", "say second")),
                "legacy flat String and List Command formats still load");
        final java.util.ArrayList<String> mutable = new java.util.ArrayList<>(List.of("old"));
        final com.lymeowz.item.api.builder.power.PowerCommandProperties detached =
                new com.lymeowz.item.api.builder.power.PowerCommandProperties(
                        "Probe", false, mutable, new java.util.ArrayList<>());
        final List<String> oldSnapshot = detached.opCommands();
        mutable.add("new");
        check(oldSnapshot.equals(List.of("old"))
                        && detached.opCommands().equals(List.of("old", "new")),
                "canonical Power Command command lists are detached immutable snapshots");
        getLogger().info("PASS: canonical Effect + Power Command registries and legacy YAML");
    }

    /** Snapshot-only socket API regression: no inventory, crafting or RNG mutation. */
    private void verifyCanonicalSocketRegistry() {
        final MyItems plugin = (MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem");
        final com.lymeowz.item.manager.game.SocketManager original =
                plugin.getGameManager().getSocketManager();
        final com.lymeowz.item.manager.SocketRegistry sockets =
                com.lymeowz.item.manager.GameAccess.of(
                        (com.lymeowz.item.LyMeowzItem) plugin).sockets();
        check(sockets == original && sockets.ids().containsAll(original.getSocketIDs()),
                "canonical SocketRegistry uses original live manager and IDs");
        check(sockets.find("not-configured-socket-probe") == null,
                "unknown socket ID retains old null behavior");
        try {
            sockets.ids().clear();
            throw new AssertionError("canonical socket ID view must be immutable");
        } catch (UnsupportedOperationException expected) { }
        for (final String id : sockets.ids()) {
            final com.lymeowz.item.socket.SocketDefinition view =
                    sockets.find(id.toUpperCase(java.util.Locale.ROOT));
            final com.lymeowz.item.api.builder.socket.SocketGemsTree legacy =
                    original.getSocketTree(id);
            check(view == legacy && view.id().equals(legacy.getGems())
                            && view.maxGrade() == legacy.getMaxGrade()
                            && view.availableGrades().containsAll(legacy.getMapSocket().keySet()),
                    "socket family preserves original live builder and configured grades");
            try {
                view.availableGrades().clear();
                throw new AssertionError("canonical socket grades must be immutable");
            } catch (UnsupportedOperationException expected) { }
            for (final int grade : view.availableGrades()) {
                final com.lymeowz.item.socket.SocketGemDefinition gem = view.findGrade(grade);
                final com.lymeowz.item.api.builder.socket.SocketGems originalGem =
                        legacy.getSocketBuild(grade);
                check(gem == originalGem && gem.gemGrade() == grade
                                && gem.socketId().equals(originalGem.getGems())
                                && gem.successRate() == originalGem.getSuccessRate()
                                && gem.loreKey().equals(originalGem.getKeyLore()),
                        "gem read-only API retains original chance/grade/lore metadata");
                final ItemStack first = gem.itemSnapshot();
                final ItemStack second = gem.itemSnapshot();
                check(first == null || (first != second && first.isSimilar(second)),
                        "canonical gem ItemStack views are separate clones");
            }
        }

        final java.util.HashMap<Integer, com.lymeowz.item.api.builder.socket.SocketGems> mutable =
                new java.util.HashMap<>();
        final com.lymeowz.item.api.builder.socket.SocketGemsTree detached =
                new com.lymeowz.item.api.builder.socket.SocketGemsTree("TestSocket", 4,
                        com.lymeowz.lib.core.enums.main.SlotType.UNIVERSAL, mutable);
        final java.util.Collection<Integer> snapshot = detached.availableGrades();
        mutable.put(2, null);
        check(snapshot.isEmpty() && detached.availableGrades().contains(2),
                "canonical socket grades are detached from live mutable legacy storage");
        try {
            snapshot.clear();
            throw new AssertionError("empty socket grade snapshot must still be immutable");
        } catch (UnsupportedOperationException expected) { }
        check(detached.findGrade(99) == null && "UNIVERSAL".equals(detached.applicableItemType()),
                "missing socket grade and applicable item type preserve original values");
        // Exercise the canonical YAML reader against live Paper materials.
        // No configured item, player inventory, or persistent data is modified.
        final org.bukkit.configuration.file.YamlConfiguration socketYaml =
                new org.bukkit.configuration.file.YamlConfiguration();
        socketYaml.set("MiXeD_Socket.KeyLore", "&eDamage: {additional_damage}");
        socketYaml.set("MiXeD_Socket.Display_Name", "&aGem {grade}");
        socketYaml.set("MiXeD_Socket.Material", "Emerald");
        socketYaml.set("MiXeD_Socket.Type_Item", "Universal");
        socketYaml.set("MiXeD_Socket.Max_Grade", 2);
        socketYaml.set("MiXeD_Socket.Base_Success_Rate", 85.0d);
        socketYaml.set("MiXeD_Socket.Scale_Success_Rate", -15.0d);
        socketYaml.set("MiXeD_Socket.Effect.Bonus_Base_Additional_Damage", 2.5d);
        socketYaml.set("MiXeD_Socket.Effect.Bonus_Scale_Additional_Damage", 1.5d);
        socketYaml.set("Ignored_Scalar", "not a socket section");
        final java.util.Map<String, com.lymeowz.item.api.builder.socket.SocketGemsTree> decoded =
                com.lymeowz.item.config.SocketDefinitions.read(socketYaml,
                        com.lymeowz.item.config.plugin.MainConfig.getInstance());
        check(decoded.size() == 1 && decoded.containsKey("MiXeD_Socket"),
                "canonical socket reader keeps original case-sensitive YAML IDs");
        final com.lymeowz.item.api.builder.socket.SocketGemsTree parsed = decoded.get("MiXeD_Socket");
        check(parsed.getMaxGrade() == 2 && parsed.isGemsAvailable(1)
                        && parsed.isGemsAvailable(2),
                "socket YAML retains original grade generation");
        final com.lymeowz.item.api.builder.socket.SocketGems second = parsed.getSocketBuild(2);
        check(second.getSuccessRate() == 70.0d
                        && second.getSocketProperties().getAdditionalDamage() == 4.0d,
                "socket YAML retains grade scaling for success rate and bonus damage");
        check(second.getItem() != null && second.getItem() != second.getItem(),
                "canonical socket YAML constructs the original clone-safe gem item");
        final org.bukkit.configuration.file.YamlConfiguration badYaml =
                new org.bukkit.configuration.file.YamlConfiguration();
        badYaml.set("Broken_Effect.KeyLore", "Broken safe example");
        badYaml.set("Broken_Effect.Display_Name", "Safe");
        badYaml.set("Broken_Effect.Material", "Emerald");
        badYaml.set("Broken_Effect.Effects", "not a configuration section");
        check(com.lymeowz.item.config.SocketDefinitions.read(badYaml,
                        com.lymeowz.item.config.plugin.MainConfig.getInstance())
                        .containsKey("Broken_Effect"),
                "malformed effect YAML section does not crash gem loading");
        getLogger().info("PASS: canonical socket/gem live builders and immutable snapshots");
    }

    /** Confirm library projectile metadata exposes safe snapshots without spawning entities. */
    private void verifyCanonicalProjectileViews() {
        final com.lymeowz.lib.LyMeowzLib lib =
                (com.lymeowz.lib.LyMeowzLib) Bukkit.getPluginManager().getPlugin("LyMeowzLib");
        final com.lymeowz.lib.manager.GameServices services =
                com.lymeowz.lib.manager.GameAccess.of(lib);
        final com.lymeowz.lib.manager.game.ProjectilePropertiesManager legacy =
                lib.getGameManager().getProjectilePropertiesManager();
        final com.lymeowz.lib.projectile.ProjectileRegistry registry = services.projectiles();
        check(registry == legacy && registry.ids().containsAll(legacy.getProjectileIds()),
                "projectile registry shares the original live manager");
        try {
            registry.ids().clear();
            throw new AssertionError("canonical projectile UUID snapshot is mutable");
        } catch (UnsupportedOperationException expected) { }
        check(registry.find(null) == null,
                "canonical projectile lookup retains old null behavior");
        final java.util.UUID fakeId = java.util.UUID.randomUUID();
        final org.bukkit.entity.Projectile fake = (org.bukkit.entity.Projectile)
                java.lang.reflect.Proxy.newProxyInstance(
                        org.bukkit.entity.Projectile.class.getClassLoader(),
                        new Class<?>[] { org.bukkit.entity.Projectile.class },
                        (proxy, method, args) -> {
                            if (method.getName().equals("getUniqueId")) return fakeId;
                            throw new UnsupportedOperationException(method.getName());
                        });
        check(registry.find(fake) == null,
                "canonical read-only lookup does not register a missing projectile");
        final ItemStack original = new ItemStack(Material.DIAMOND_SWORD);
        final com.lymeowz.lib.projectile.ProjectileProperties detached =
                new com.lymeowz.lib.projectile.ProjectileProperties(fake, original);
        final com.lymeowz.lib.projectile.ProjectileDefinition view = detached;
        check(view.id().equals(fakeId) && view.mainItemSnapshot() != original
                        && view.mainItemSnapshot().isSimilar(original)
                        && view.deputyItemSnapshot() == null,
                "projectile read-only view retains UUID and clones primary equipment");
        final ItemStack snapshot = view.mainItemSnapshot();
        original.setAmount(2);
        check(snapshot.getAmount() == 1 && view.mainItemSnapshot().getAmount() == 2,
                "earlier equipment snapshot is detached from legacy mutable storage");
        final ItemStack deputy = new ItemStack(Material.SHIELD);
        detached.setItemDeputy(deputy);
        check(view.deputyItemSnapshot() != deputy
                        && view.deputyItemSnapshot().isSimilar(deputy),
                "deputy equipment also returns an independent copy");
        check(registry.find(fake) == null && services.trackedProjectileCount() == registry.ids().size(),
                "standalone probe leaves original projectile registry untouched");
        getLogger().info("PASS: original projectile registry and cloned LyMeowz metadata views");
    }

    /** Validate migrated Item Set metadata and original bonus YAML grammar. */
    private void verifyCanonicalItemSets() {
        final MyItems plugin = (MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem");
        final com.lymeowz.item.manager.game.ItemSetManager original =
                plugin.getGameManager().getItemSetManager();
        final com.lymeowz.item.manager.ItemSetRegistry sets =
                com.lymeowz.item.manager.GameAccess.of(
                        (com.lymeowz.item.LyMeowzItem) plugin).itemSets();
        check(sets == original && sets.ids().containsAll(original.getItemSetIDs()),
                "canonical Item Set registry is the original manager");
        check(sets.find("no-such-item-set-probe") == null,
                "unknown set ID still resolves to null");
        try {
            sets.ids().clear();
            throw new AssertionError("Item Set ID snapshot must be immutable");
        } catch (UnsupportedOperationException expected) { }
        for (final String id : sets.ids()) {
            final com.lymeowz.item.itemset.ItemSetDefinition view =
                    sets.find(id.toUpperCase(java.util.Locale.ROOT));
            final com.lymeowz.item.api.builder.item.ItemSet old = original.getItemSet(id);
            check(view == old && view.id().equals(old.getID())
                            && view.name().equals(old.getName())
                            && view.componentIds().containsAll(old.getComponentIDs())
                            && view.bonusAmounts().containsAll(old.getBonusAmountIDs()),
                    "canonical Item Set metadata preserves original case-insensitive lookup");
            try {
                view.componentIds().clear();
                throw new AssertionError("Item Set component IDs must be immutable");
            } catch (UnsupportedOperationException expected) { }
            try {
                view.bonusAmounts().clear();
                throw new AssertionError("Item Set bonus thresholds must be immutable");
            } catch (UnsupportedOperationException expected) { }
            for (final int required : view.bonusAmounts()) {
                final com.lymeowz.item.itemset.ItemSetBonusDefinition bonus = view.findBonus(required);
                final com.lymeowz.item.api.builder.item.ItemSetBonus legacy = old.getItemSetBonus(required);
                check(bonus == legacy && bonus.requiredPieces() == required
                                && bonus.descriptionSnapshot().equals(legacy.getDescription()),
                        "Item Set threshold and description retain original builder");
                try {
                    bonus.descriptionSnapshot().clear();
                    throw new AssertionError("Item Set descriptions must be immutable");
                } catch (UnsupportedOperationException expected) { }
            }
            for (final String componentId : view.componentIds()) {
                final com.lymeowz.item.itemset.ItemSetComponentDefinition component =
                        view.findComponent(componentId.toUpperCase(java.util.Locale.ROOT));
                final com.lymeowz.item.api.builder.item.ItemSetComponent legacy =
                        old.getItemSetComponent(componentId);
                check(component == legacy && component.id().equals(componentId)
                                && component.setId().equals(old.getID())
                                && component.slotNames().size() == legacy.getSlots().size(),
                        "Item Set component slot snapshot matches old slot mapping");
                try {
                    component.slotNames().clear();
                    throw new AssertionError("Item Set slot names must be immutable");
                } catch (UnsupportedOperationException expected) { }
            }
        }

        final org.bukkit.configuration.file.YamlConfiguration yaml =
                new org.bukkit.configuration.file.YamlConfiguration();
        yaml.set("MiXeD_Set.Name", "Mixed Item Set");
        yaml.set("MiXeD_Set.Bonus.2.Description", List.of("&aBonus damage", "Second line"));
        yaml.set("MiXeD_Set.Bonus.2.Effect.Stats.Additional_Damage", 3.75d);
        yaml.set("MiXeD_Set.Component.Sword.KeyLore", "&aMixed Sword");
        yaml.set("MiXeD_Set.Component.Sword.Display_Name", "Mixed Sword");
        yaml.set("MiXeD_Set.Component.Sword.Material", "IRON_SWORD");
        yaml.set("MiXeD_Set.Component.Sword.Slots", List.of("MainHand"));
        yaml.set("MalformedRoot", "invalid scalar");
        final java.util.Map<String, com.lymeowz.item.api.builder.item.ItemSet> parsed =
                com.lymeowz.item.config.ItemSetDefinitions.read(yaml);
        check(parsed.size() == 1 && parsed.containsKey("MiXeD_Set"),
                "Item Set parser retains IDs and skips scalar root keys");
        final com.lymeowz.item.api.builder.item.ItemSet built = parsed.get("MiXeD_Set");
        final com.lymeowz.item.api.builder.item.ItemSetBonus builtBonus = built.getItemSetBonus(2);
        final com.lymeowz.item.api.builder.item.ItemSetComponent builtComponent =
                built.getItemSetComponent("mixed_set_sword");
        check(builtBonus != null && builtBonus.getAmountID() == 2
                        && builtBonus.getEffect().getEffectStats().getAdditionalDamage() == 3.75d,
                "Item Set bonus threshold and stat formula preserve old YAML values");
        check(builtComponent != null && builtComponent.getComponentItem() != null
                        && builtComponent.getComponentItem().getMaterial() == Material.IRON_SWORD
                        && builtComponent.slotNames().contains("MAINHAND"),
                "Item Set YAML retains component naming and equipment slots");
        final org.bukkit.configuration.file.YamlConfiguration malformed =
                new org.bukkit.configuration.file.YamlConfiguration();
        malformed.set("Broken_Bonus.Bonus", "unexpected scalar");
        malformed.set("Broken_Component.Component", "unexpected scalar");
        malformed.set("Broken_Effect.Bonus.2.Effect", "unexpected scalar");
        malformed.set("Broken_Stats.Bonus.2.Effect.Stats", "unexpected scalar");
        malformed.set("Broken_Abilities.Bonus.2.Effect.Ability_Weapon", "unexpected scalar");
        malformed.set("Broken_Enchants.Component.Test.Material", "IRON_SWORD");
        malformed.set("Broken_Enchants.Component.Test.Enchantment", "unexpected scalar");
        check(com.lymeowz.item.config.ItemSetDefinitions.read(malformed).size() == 6,
                "malformed Item Set sections are skipped without a server-start crash");
        getLogger().info("PASS: canonical Item Set registry, YAML, bonuses and slots");
    }

    /** Validate all 25 shipped class singletons against the running plugin,
     * configured damage scales, and exact existing lore encode/decode path.
     * Do not fire destructive combat in this startup smoke fixture.
     */
    private void verifyWeaponAbilities() throws Exception {
        final MyItems myitems = (MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem");
        check(myitems != null && myitems.isEnabled(), "MyItems enabled for ability validation");
        final RegisterAbilityWeaponManager registered =
                myitems.getRegisterManager().getRegisterAbilityWeaponManager();
        final AbilityWeaponManager abilityManager =
                myitems.getGameManager().getAbilityWeaponManager();

        final String[] names = {
                "AirShock", "BadLuck", "Blind", "BubbleDeflector",
                "Cannibalism", "Confuse", "Curse", "DarkFlame", "DarkImpact",
                "Flame", "FlameWheel", "Freeze", "Harm", "Hungry",
                "Levitation", "Lightning", "Poison", "Roots", "Slow",
                "Tired", "Vampirism", "VenomBlast", "VenomSpread",
                "Weak", "Wither"
        };
        final Set<String> ids = new HashSet<>();
        final com.lymeowz.item.manager.GameServices services =
                com.lymeowz.item.manager.GameAccess.of(
                        (com.lymeowz.item.LyMeowzItem) myitems);
        check(services == myitems.getGameManager(),
                "canonical manager facade is same original GameManager instance");
        check(services.elements() == myitems.getGameManager().getElementManager(),
                "new element registry shares original live manager");
        check(services.items() == myitems.getGameManager().getItemManager(),
                "new item catalog shares original live item manager");
        check(services.elements().ids().containsAll(
                        myitems.getGameManager().getElementManager().getElements()),
                "new element manager retains existing configured IDs");
        check(services.items().ids().containsAll(
                        myitems.getGameManager().getItemManager().getItemIDs()),
                "new item catalog retains all database keys");
        check(services.elements().find("not-a-real-element") == null
                        && services.items().find("not-a-real-item") == null,
                "canonical element/item lookups preserve missing-ID behavior");
        try {
            services.elements().ids().clear();
            throw new AssertionError("new element registry IDs are mutable");
        } catch (UnsupportedOperationException expected) { }
        try {
            services.items().ids().clear();
            throw new AssertionError("new item catalog IDs are mutable");
        } catch (UnsupportedOperationException expected) { }
        for (final String elementId : services.elements().ids()) {
            final var oldElement = myitems.getGameManager().getElementManager()
                    .getElementBuild(elementId);
            final var newElement = services.elements().find(
                    elementId.toUpperCase(java.util.Locale.ROOT));
            check(newElement == oldElement,
                    "element facade preserves original builder identity: " + elementId);
            check(newElement.loreKey().equals(oldElement.getKeyLore()),
                    "element lore identity stays unchanged");
            check(newElement.additionalDamageScale()
                            == oldElement.getBoostBuild().getScaleBaseAdditionalDamage()
                            && newElement.percentDamageScale()
                            == oldElement.getBoostBuild().getScaleBasePercentDamage(),
                    "element scaling remains unchanged");
            check(newElement.resistanceSnapshot().equals(oldElement.getResistance()),
                    "element resistances preserved in canonical view");
            try {
                newElement.resistanceSnapshot().clear();
                throw new AssertionError("element facade resistance map is mutable");
            } catch (UnsupportedOperationException expected) { }
        }
        for (final String itemId : services.items().ids()) {
            final ItemStack fromOld = myitems.getGameManager().getItemManager().getItem(itemId);
            final ItemStack fromNew = services.items().find(itemId);
            check(fromNew != fromOld && fromNew.equals(fromOld),
                    "canonical item catalog returns the same cloned item bytes/identity");
        }
        check(services.weaponRegistry() == registered,
                "canonical GameServices uses exact original skill registry");
        check(services.weaponPropertyIds().size() >= 25,
                "canonical GameServices exposes configured skill properties");
        check(com.lymeowz.item.manager.GameAccess.of(null) == null,
                "canonical game accessor handles absent plugin");
        try {
            services.weaponPropertyIds().clear();
            throw new AssertionError("canonical skill property IDs allow mutation");
        } catch (UnsupportedOperationException expected) {
            // The old manager's mutable collection API remains bytecode compatible.
        }
        check(registered instanceof com.lymeowz.item.manager.WeaponRegistry,
                "legacy skill manager exposes the new LyMeowz registry API");
        final com.lymeowz.item.manager.WeaponRegistry modernRegistry = registered;
        final com.lymeowz.item.LyMeowzItem newPlugin =
                (com.lymeowz.item.LyMeowzItem) myitems;
        check(com.lymeowz.item.ability.WeaponAbilities.registry(newPlugin) == modernRegistry,
                "new public skill API uses exact original registry instance");
        check(com.lymeowz.item.ability.WeaponAbilities.all(newPlugin).size() == names.length,
                "new public skill facade exposes all configured abilities");
        check(com.lymeowz.item.ability.WeaponAbilities.find(null, "Poison") == null
                        && com.lymeowz.item.ability.WeaponAbilities.all(null).isEmpty(),
                "new public skill facade handles unavailable plugin safely");
        try {
            modernRegistry.weapons().clear();
            throw new AssertionError("canonical weapon view permits registry mutation");
        } catch (UnsupportedOperationException expected) {
            // Legacy getAllAbilityWeapon() remains unchanged; only new API is read-only.
        }
        check(modernRegistry.weapons().size() == names.length,
                "new weapon registry reuses all 25 old singletons");
        check(modernRegistry.weapon("missing-skill") == null
                        && modernRegistry.weapon(null) == null,
                "new weapon registry handles missing and null IDs unchanged");
        check(registered.getAllAbilityWeapon().size() == names.length,
                "exactly 25 skills registered, not " + registered.getAllAbilityWeapon().size());
        for (String name : names) {
            final Class<?> type = Class.forName(
                    "com.lymeowz.item.builder.ability.weapon.AbilityWeapon" + name);
            final AbilityWeapon ability = (AbilityWeapon) type.getMethod("getInstance")
                    .invoke(null);
            check(ability != null && ability.getPlugin() == myitems,
                    name + " singleton bound to active MyItems");
            final String id = ability.getID();
            check(id != null && !id.isBlank() && ids.add(id),
                    name + " has a unique nonempty ID");
            check(ability instanceof com.lymeowz.item.ability.WeaponAbility,
                    name + " implements new weapon ability API");
            check(registered.getAbilityWeapon(id) == ability,
                    name + " is registered by its original ID");
            check(com.lymeowz.item.ability.WeaponAbilities.find(newPlugin, id) == ability,
                    name + " resolved through public new-package skill facade");
            check(modernRegistry.weapon(id.toUpperCase(java.util.Locale.ROOT)) == ability,
                    name + " case-insensitive new manager lookup keeps singleton identity");
            check(ability.getKeyLore() != null && !ability.getKeyLore().isBlank(),
                    name + " has configured lore alias");
            final int maxGrade = ability.getMaxGrade();
            check(maxGrade > 0, name + " has valid max grade");
            final com.lymeowz.item.api.builder.ability.AbilityWeaponProperties config =
                    abilityManager.getAbilityWeaponProperties(id);
            check(services.weaponProperties(id) == config,
                    name + " new game manager exposes same original property builder");
            check(config instanceof com.lymeowz.item.ability.WeaponProperties,
                    name + " original property object implements canonical builder API");
            for (int candidate : new int[] {-2, 0, 1, maxGrade,
                    maxGrade == Integer.MAX_VALUE ? maxGrade : maxGrade + 1}) {
                final int grade = Math.min(maxGrade, Math.max(0, candidate));
                final int expectedDuration = config.getBaseDurationEffect()
                        + grade * config.getScaleDurationEffect();
                final double expectedBase = grade * config.getScaleBaseBonusDamage()
                        + 24.0 * (grade * config.getScaleBasePercentDamage() / 100.0);
                final double expectedCast = grade * config.getScaleCastBonusDamage()
                        + 24.0 * (grade * config.getScaleCastPercentDamage() / 100.0);
                check(config.getTotalDuration(candidate) == expectedDuration,
                        name + " duration/grade unchanged for " + candidate);
                check(Double.compare(config.getTotalBaseDamage(candidate, 24.0), expectedBase) == 0,
                        name + " base damage/grade unchanged for " + candidate);
                check(Double.compare(config.getTotalCastDamage(candidate, 24.0), expectedCast) == 0,
                        name + " cast damage/grade unchanged for " + candidate);
            }
            if (ability instanceof AbilityWeaponAttributeBaseDamage base) {
                check(base instanceof com.lymeowz.item.ability.WeaponBaseDamage,
                        name + " legacy base damage API also implements LyMeowz API");
                check(Double.isFinite(base.getBaseBonusDamage(1))
                                && Double.isFinite(base.getBasePercentDamage(1)),
                        name + " base damage scales are finite");
            }
            if (ability instanceof AbilityWeaponAttributeCastDamage cast) {
                check(cast instanceof com.lymeowz.item.ability.WeaponCastDamage,
                        name + " legacy cast damage API also implements LyMeowz API");
                check(Double.isFinite(cast.getCastBonusDamage(1))
                                && Double.isFinite(cast.getCastPercentDamage(1)),
                        name + " cast damage scales are finite");
            }
            if (ability instanceof AbilityWeaponAttributeEffect effect) {
                check(effect instanceof com.lymeowz.item.ability.WeaponEffectDuration,
                        name + " legacy effect API also implements LyMeowz API");
                check(effect.getEffectDuration(1) >= 0,
                        name + " effect duration must not be negative");
            }

            final String lore = abilityManager.getTextAbility(id, 1, 65.0);
            check(lore != null && !lore.isEmpty(),
                    name + " can render current lore");
            final AbilityItemWeapon decoded = abilityManager.getAbilityItemWeapon(lore);
            check(decoded != null && decoded.getAbility().equalsIgnoreCase(id)
                            && decoded.getGrade() == 1
                            && Math.abs(decoded.getChance() - 65.0) < 0.001,
                    name + " can decode its own lore without losing grade/chance");
            // A generated String is not an equipped item. Paper 26.3 may
            // normalize away the invisible legacy marker codes on ItemMeta.
            // All 25 skills must survive both item lore and byte serialization.
            final ItemStack encodedWeapon = new ItemStack(Material.IRON_SWORD);
            EquipmentUtil.addLore(encodedWeapon, lore);
            final ItemStack persistedWeapon =
                    ItemStack.deserializeBytes(encodedWeapon.serializeAsBytes());
            final var weaponSkills = abilityManager.getListAbilityItemWeapon(persistedWeapon);
            check(weaponSkills.size() == 1
                            && weaponSkills.get(0).getAbility().equalsIgnoreCase(id)
                            && weaponSkills.get(0).getGrade() == 1
                            && Math.abs(weaponSkills.get(0).getChance() - 65.0) < 0.001,
                    name + " must roundtrip via real Paper ItemMeta and serialized weapon");
        }
        final org.bukkit.configuration.file.YamlConfiguration sample =
                org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
                        new java.io.StringReader(
                                "Test_Ability:\n  max_grade: 0\n  Base_Duration_Effect: -5\n"
                                + "  Scale_Duration_Effect: -9\n"
                                + "  Scale_Base_Percent_Damage: 12.5\n"
                                + "  Scale_Cast_Bonus_Damage: 3.0\n"));
        final var parsed = com.lymeowz.item.config.AbilityWeaponDefinitions.parse(sample);
        final var parsedSkill = parsed.get("Test_Ability");
        check(parsedSkill != null && parsed.size() == 1,
                "new YAML reader preserves skill IDs and key spelling");
        check(parsedSkill.getMaxGrade() == 1
                        && parsedSkill.getBaseDurationEffect() == 0
                        && parsedSkill.getScaleDurationEffect() == 0,
                "new YAML reader preserves legacy grade and duration clamps");
        check(parsedSkill.getScaleBasePercentDamage() == 12.5
                        && parsedSkill.getScaleCastBonusDamage() == 3.0,
                "new YAML reader preserves numerical YAML field conversion");
        final org.bukkit.configuration.file.YamlConfiguration elementSample =
                org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
                        new java.io.StringReader(
                                "Custom_Fire:\n"
                                + "  Keylore: '&cCustom Fire'\n"
                                + "  Scale_Base_Additional_Damage: 1.5\n"
                                + "  Scale_Bonus_Percent_Damage: -2.25\n"
                                + "  Resistance:\n    Water: 3.75\n"
                                + "  Potion_To_Attackers:\n    SPEED:\n"
                                + "      Grade: 99\n"
                                + "      Scale_Chance: -2\n"
                                + "      Scale_Duration: 900\n"));
        final var parsedElements =
                com.lymeowz.item.config.ElementDefinitions.parse(elementSample);
        final var customFire = parsedElements.get("Custom_Fire");
        check(customFire != null && parsedElements.size() == 1,
                "canonical YAML element parser preserves IDs and aliases");
        check(customFire.getBoostBuild().getScaleBaseAdditionalDamage() == 1.5
                        && customFire.getBoostBuild().getScaleBasePercentDamage() == -2.25
                        && customFire.getResistance().get("Water") == 3.75,
                "canonical YAML parser preserves element scaling and resistance");
        final var speed = customFire.getPotionBuild().getPotionAttacker()
                .get(org.bukkit.potion.PotionEffectType.SPEED);
        check(speed != null && speed.getGrade() == 10
                        && speed.getScaleChance() == 0.01
                        && speed.getScaleDuration() == 100.0,
                "canonical YAML reader preserves potion grade/chance/duration clamps");
        final com.lymeowz.item.api.builder.ability.AbilityWeaponProperties settings =
                new com.lymeowz.item.api.builder.ability.AbilityWeaponProperties(
                        3, 40, 10, 2.5, 12.5, 4.0, 20.0);
        check(settings instanceof com.lymeowz.item.ability.WeaponProperties,
                "existing weapon property instances implement canonical builder API");
        check(settings.getTotalDuration(2) == 60
                        && settings.getTotalDuration(99) == 70
                        && settings.getTotalDuration(-3) == 40,
                "canonical ability duration scaling retains grade clamping");
        check(settings.getTotalBaseDamage(2, 20.0) == 10.0
                        && settings.getTotalCastDamage(2, 20.0) == 16.0,
                "canonical ability damage scaling matches original formula");
        check(settings.getTotalBaseDamage(99, 20.0)
                        == com.lymeowz.item.ability.AbilityScaling.baseDamage(
                                99, 3, 2.5, 12.5, 20.0),
                "old builder calculations delegate to new scaling math");
        getLogger().info("PASS: 25/25 weapon ability classes registered, configured and lore-roundtripped");
        getLogger().info("PASS: new LyMeowz ability builder, registry and damage scaling APIs");
    }

    /** Execute real cast() implementations on disposable mobs on Paper's
     * main thread. This proves that mob-vs-mob skill effects cannot fail
     * solely because the attacker/target is not a CommandSender.
     */
    private void verifyMobSkillCasts() {
        final World world = Bukkit.getWorlds().get(0);
        final Location origin = world.getSpawnLocation().clone().add(0.5, 2.0, 0.5);
        final Zombie attacker = world.spawn(origin, Zombie.class);
        final Zombie victim = world.spawn(origin.clone().add(2.5, 0.0, 0.0), Zombie.class);
        attacker.setAI(false);
        victim.setAI(false);
        attacker.setSilent(true);
        victim.setSilent(true);
        attacker.setInvulnerable(true);
        victim.setInvulnerable(true);

        final RegisterAbilityWeaponManager registered =
                ((MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem"))
                        .getRegisterManager().getRegisterAbilityWeaponManager();
        for (String id : new String[] {"Freeze", "Roots", "Curse"}) {
            final AbilityWeapon ability = registered.getAbilityWeapon(id);
            check(ability != null, "mob cast ability exists: " + id);
            try {
                ability.cast(attacker, victim, 1, 3.0);
            } catch (RuntimeException | LinkageError ex) {
                throw new AssertionError("Mob cast failed for " + id, ex);
            }
        }
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (attacker.isValid()) attacker.remove();
            if (victim.isValid()) victim.remove();
        }, 100L);
        getLogger().info("PASS: 3/3 real mob-target skill casts (Freeze, Roots, Curse)");
    }

    /** Real Paper plugin classloader smoke, with an API-shaped test provider.
     * This does NOT claim the real EssentialsX 2.22.0 runs on Paper 26.3.
     */
    private void verifyEssentialsWarpProvider() {
        final AgarthaLib agartha = (AgarthaLib) Bukkit.getPluginManager().getPlugin("LyMeowzLib");
        check(agartha != null && agartha.isEnabled(), "AgarthaLib ready for Essentials warp API");
        final SupportManagerAPI supportManager = new SupportManagerAPI(agartha);
        final SupportEssentials adapter = supportManager.getSupportEssentials();
        check(adapter != null, "EssentialsX named provider detected");
        check(adapter.getSource() == Bukkit.getPluginManager().getPlugin("Essentials"),
                "Live provider plugin instance resolved");
        check(adapter == supportManager.getSupportEssentials(), "Adapter cache retained while provider active");

        final World world = Bukkit.getWorlds().get(0);
        check(List.of("spawn", "market", "missing").equals(
                List.copyOf(adapter.getWarpNames())), "Modern IWarps collection preserved");
        check(List.of("spawn", "market").equals(
                List.copyOf(adapter.getWarpNames(world))), "Current world filtering preserved");
        check(adapter.getWarp("spawn") != null
                        && adapter.getWarp("spawn").getBlockX() == 13,
                "Correct position through the provider-owned IWarps interface");
        check(adapter.getWarp("missing") == null, "Missing warp still fails closed");
        check(adapter.getWarps().size() == 2, "Invalid warp not included in locations");
        check(adapter.getWarps(world).size() == 2, "World-filtered locations preserved");
        getLogger().info("PASS: EssentialsX 2.22 IWarps contract on real Paper plugin classloaders");
    }

    /** Real Paper runtime: optional provider APIs through isolated plugin
     * classloaders, model-only mutation and lossless serialized item state.
     */
    private void verifyItemModels() {
        final ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        item.setAmount(2);
        final NamespacedKey idKey = new NamespacedKey(this, "existing_item_id");
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bMyItems test sword");
        meta.setLore(List.of("§7Damage: 42", "§5Ability: Vampirism I"));
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, "original:myitem");
        item.setItemMeta(meta);
        final ItemStack baseline = item.clone();

        check(Paper26ExternalModels.apply(item, "itemsadder", "missing:item")
                        == Paper26ExternalModels.Status.ID_NOT_FOUND,
                "ItemsAdder unknown registry entry rejected");
        check(item.equals(baseline), "unknown ItemsAdder model cannot mutate item");
        check(Paper26ExternalModels.apply(item, "itemsadder", "test:ruby_sword")
                        == Paper26ExternalModels.Status.APPLIED,
                "ItemsAdder CustomStack.getInstance and getItemStack resolved");
        check(NamespacedKey.fromString("itemsadder:ruby_sword")
                        .equals(item.getItemMeta().getItemModel()),
                "ItemsAdder item_model copied");

        check(Paper26ExternalModels.apply(item, "oraxen", "missing")
                        == Paper26ExternalModels.Status.ID_NOT_FOUND,
                "Oraxen unknown model rejected");
        check(Paper26ExternalModels.apply(item, "oraxen", "storm_blade")
                        == Paper26ExternalModels.Status.APPLIED,
                "Oraxen OraxenItems.getItemById and build resolved");
        check(NamespacedKey.fromString("oraxen:storm_blade")
                        .equals(item.getItemMeta().getItemModel()), "Oraxen item_model copied");
        check(Paper26ExternalModels.apply(item, "itemsadder", "test:legacy_sword")
                        == Paper26ExternalModels.Status.APPLIED,
                "Legacy CMD provider item copied via component API");
        check(item.getItemMeta().getItemModel() == null,
                "CMD selector clears stale modern item_model");
        check(item.getItemMeta().getCustomModelDataComponent().getFloats()
                        .equals(List.of(2026.0F)), "legacy float model data preserved");
        check(Paper26ItemModels.applyKey(item, "myitems:dragon_sword")
                        == Paper26ItemModels.Result.APPLIED,
                "direct Paper26 namespaced item model works");
        check(Paper26ItemModels.applyNumber(item, 6543)
                        == Paper26ItemModels.Result.APPLIED,
                "direct modern CMD component works");

        final ItemStack saved = ItemStack.deserializeBytes(item.serializeAsBytes());
        check(saved.getType() == baseline.getType(), "material unchanged");
        check(saved.getAmount() == baseline.getAmount(), "amount unchanged");
        check(saved.getItemMeta().getDisplayName()
                        .equals(baseline.getItemMeta().getDisplayName()), "name unchanged");
        check(saved.getItemMeta().getLore()
                        .equals(baseline.getItemMeta().getLore()), "lore/skills unchanged");
        check("original:myitem".equals(saved.getItemMeta().getPersistentDataContainer()
                        .get(idKey, PersistentDataType.STRING)), "existing PDC ID unchanged");
        check(saved.getItemMeta().getCustomModelDataComponent().getFloats()
                        .equals(List.of(6543.0F)), "model survived ItemStack serialization");

        check(Paper26ItemModels.clear(item) == Paper26ItemModels.Result.APPLIED,
                "clear visual selectors");
        check(item.getItemMeta().getItemModel() == null
                        && !item.getItemMeta().hasCustomModelDataComponent(),
                "clear must remove only model selectors");
        check("original:myitem".equals(item.getItemMeta().getPersistentDataContainer()
                        .get(idKey, PersistentDataType.STRING)), "clear retains PDC");
        check(item.getItemMeta().getLore().equals(baseline.getItemMeta().getLore()),
                "clear retains ability/stat lore");
        getLogger().info("PASS: ItemsAdder/Oraxen model-only bridges and ItemStack roundtrip");
    }

    /** On a real Paper server use the SAME legacy EquipmentUtil entrypoints
     * as /itemname, /loreadd, /loreset, /loreinsert. Verify that RGB input
     * roundtrips through current Adventure-backed Paper ItemMeta, while the
     * lore-change event path and existing skills/PDC remain untouched.
     */
    private void verifyHexItemColors() {
        final String red = "§x§f§f§3§3§6§6";
        final String blue = "§x§1§2§a§b§c§d";
        final String green = "§x§0§0§f§f§0§0";
        check((red + "Sword §lSoul").equals(
                TextUtil.colorful("&#FF3366Sword &lSoul")),
                "HEX and legacy formatting mix in original TextUtil.colorful");
        check(("§aOld " + blue + "New").equals(
                TextUtil.colorful("&aOld <#12ABCD>New")),
                "old colors and angle HEX can coexist");
        check("§cText".equals(TextUtil.colorful("&cText")),
                "original &c legacy parsing unchanged");
        check("#xyz000".equals(TextUtil.colorful("#xyz000")),
                "invalid hex remains visible unchanged");

        final ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        final NamespacedKey idKey = new NamespacedKey(this, "hex_original_item_id");
        final ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, "myitems:hex_sword");
        item.setItemMeta(meta);

        EquipmentUtil.setDisplayName(item, "&#FF3366Sword &lSoul");
        EquipmentUtil.addLore(item, "&7Damage: 42");
        EquipmentUtil.addLore(item, "<#12ABCD>Ability: Vampirism I");
        EquipmentUtil.setLore(item, 1, "&#00FF00&lDamage: 42");
        EquipmentUtil.insertLore(item, 2, "&eLegacy lore");
        final ItemMeta colored = item.getItemMeta();
        check((red + "Sword §lSoul").equals(EquipmentUtil.getDisplayName(item)),
                "HEX name written by existing EquipmentUtil");
        final List<String> lore = EquipmentUtil.getLores(item);
        check(lore != null && lore.size() == 3, "lore add/set/insert line indexes retained");
        check((green + "§lDamage: 42").equals(lore.get(0)),
                "HEX lore-set uses same existing event-aware entrypoint");
        check("§eLegacy lore".equals(lore.get(1)), "inserted legacy lore preserved");
        check((blue + "Ability: Vampirism I").equals(lore.get(2)),
                "HEX lore-add preserves ability text");
        check(colored.displayName() != null && colored.lore() != null
                && colored.lore().size() == 3, "Paper Adventure ItemMeta content populated");
        check("myitems:hex_sword".equals(
                colored.getPersistentDataContainer().get(idKey, PersistentDataType.STRING)),
                "existing item ID not mutated by coloring");

        // Simulates ItemConfig.loadConfig -> EquipmentUtil.colorful(gameItem).
        // It must not downgrade stored RGB through deprecated String getters.
        EquipmentUtil.colorful(item);
        check((red + "Sword §lSoul").equals(EquipmentUtil.getDisplayName(item)),
                "configuration reload retains original 24-bit display color");
        check(lore.equals(EquipmentUtil.getLores(item)),
                "configuration reload retains 24-bit lore and legacy formatting");

        final ItemStack reloaded = ItemStack.deserializeBytes(item.serializeAsBytes());
        final ItemMeta restored = reloaded.getItemMeta();
        check(EquipmentUtil.getDisplayName(item).equals(EquipmentUtil.getDisplayName(reloaded)),
                "HEX name survives actual Paper ItemStack NBT roundtrip");
        check(lore.equals(EquipmentUtil.getLores(reloaded)),
                "HEX, legacy, skill and stat lores survive NBT roundtrip");
        check("myitems:hex_sword".equals(
                restored.getPersistentDataContainer().get(idKey, PersistentDataType.STRING)),
                "original item ID survives NBT roundtrip");
        getLogger().info("PASS: MyItems HEX and legacy item-name/lore commands on real Paper 26.3");
    }


    /** Test the original stats manager on actual serialized ItemStacks. */
    private void verifyRealStatsLore() {
        final MyItems myitems = (MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem");
        final LoreStatsManager stats = myitems.getGameManager().getStatsManager();
        final ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        EquipmentUtil.addLore(sword, stats.getTextLoreStats(LoreStatsEnum.DAMAGE, 10.0, 20.0));
        EquipmentUtil.addLore(sword, stats.getTextLoreStats(LoreStatsEnum.CRITICAL_CHANCE, 17.5));
        EquipmentUtil.addLore(sword, stats.getTextLoreStats(LoreStatsEnum.CRITICAL_DAMAGE, 1.5));
        EquipmentUtil.addLore(sword, stats.getTextLoreStats(LoreStatsEnum.DURABILITY, 8, 12));
        EquipmentUtil.addLore(sword, stats.getTextLoreStats(LoreStatsEnum.ATTACK_SPEED, 1.25));
        EquipmentUtil.addLore(sword, stats.getTextLoreStats(LoreStatsEnum.ARMOR_TOUGHNESS, 2));
        EquipmentUtil.addLore(sword, stats.getTextLoreStats(LoreStatsEnum.LIFESTEAL, 10));
        EquipmentUtil.addLore(sword, stats.getTextLoreStats(LoreStatsEnum.COOLDOWN_REDUCTION, 15));
        final ItemStack copy = ItemStack.deserializeBytes(sword.serializeAsBytes());
        check(stats.hasLoreStats(copy, LoreStatsEnum.DAMAGE), "real serialized item Damage marker");
        check(stats.getLoreValue(copy, LoreStatsEnum.DAMAGE, LoreStatsOption.MIN) == 10.0, "Damage min");
        check(stats.getLoreValue(copy, LoreStatsEnum.DAMAGE, LoreStatsOption.MAX) == 20.0, "Damage max");
        check(stats.getLoreValue(copy, LoreStatsEnum.CRITICAL_CHANCE, null) == 17.5, "Critical chance");
        check(Math.abs(stats.getLoreValue(copy, LoreStatsEnum.CRITICAL_DAMAGE, null) - 0.5) < 0.001,
                "Critical multiplier");
        check(stats.getLoreValue(copy, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT) == 8, "Durability current");
        check(stats.getLoreValue(copy, LoreStatsEnum.DURABILITY, LoreStatsOption.MAX) == 12, "Durability max");
        final ItemStack worn = new ItemStack(Material.IRON_SWORD);
        EquipmentUtil.addLore(worn, stats.getTextLoreStats(LoreStatsEnum.DURABILITY, 2, 5));
        stats.damageDurability(worn);
        check(stats.getLoreValue(worn, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT) == 1,
                "durability 2 -> 1");
        stats.damageDurability(worn);
        check(stats.getLoreValue(worn, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT) == 0,
                "durability 1 -> 0");
        check(!stats.checkDurability(worn), "zero durability disables item stats");
        stats.damageDurability(worn);
        check(stats.getLoreValue(worn, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT) == 0,
                "broken item does not underflow");
        final ItemStack wornSaved = ItemStack.deserializeBytes(worn.serializeAsBytes());
        check(stats.getLoreValue(wornSaved, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT) == 0,
                "zero durability preserved across Paper NBT roundtrip");
        stats.itemRepair(wornSaved, 1);
        check(stats.getLoreValue(wornSaved, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT) == 1,
                "repair restores durability after broken state");
        stats.itemRepair(wornSaved, 999);
        check(stats.getLoreValue(wornSaved, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT) == 5,
                "repair cannot exceed the maximum");
        stats.itemRepair(wornSaved, Integer.MAX_VALUE);
        check(stats.getLoreValue(wornSaved, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT) == 5,
                "huge repair does not overflow into a broken item");
        stats.itemRepair(wornSaved, Integer.MIN_VALUE);
        check(stats.getLoreValue(wornSaved, LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT) == 0,
                "huge negative repair clamps cleanly to zero");
        check(stats.getLoreStatsWeapon(copy).getDamage() >= 10.0, "standalone weapon stats");
        check(stats.getLoreValue(copy, LoreStatsEnum.ATTACK_SPEED, null) == 1.25, "modern Attack Speed lore");
        check(stats.getLoreValue(copy, LoreStatsEnum.ARMOR_TOUGHNESS, null) == 2, "modern Armor Toughness lore");
        check(stats.getLoreValue(copy, LoreStatsEnum.LIFESTEAL, null) == 10, "modern Lifesteal percentage lore");
        check(stats.getLoreValue(copy, LoreStatsEnum.COOLDOWN_REDUCTION, null) == 15, "modern CDR percentage lore");
        check(Paper26StatsMath.outgoingBonus(true, 10, 20) == 10, "PvP route");
        check(Paper26StatsMath.outgoingBonus(true, 10, 20)
                        == com.lymeowz.item.stats.CombatMath.outgoingBonus(true, 10, 20),
                "legacy Stats math API delegates to new LyMeowz implementation");
        check(com.lymeowz.item.stats.CombatMath.repairedDurability(1, 5, Integer.MAX_VALUE) == 5,
                "new LyMeowz Stats math preserves overflow-safe item repair");
        check(Paper26StatsMath.outgoingBonus(false, 10, 20) == 20, "PvE route");
        check(Paper26StatsMath.incomingDefenseBonus(true, 10, 20) == 10, "PvP defense route");
        check(Paper26StatsMath.incomingDefenseBonus(false, 10, 20) == 20, "PvE defense route");

        // A direct API edit can leave a sidecar in PDC. It MUST NOT restore
        // stale lore/bonus stats after a third-party plugin replaces the lore.
        final ItemStack externallyEdited = copy.clone();
        final ItemMeta changed = externallyEdited.getItemMeta();
        changed.setLore(List.of("§7No combat stats"));
        externallyEdited.setItemMeta(changed);
        check(!stats.hasLoreStats(externallyEdited, LoreStatsEnum.DAMAGE),
                "legacy lore sidecar ignored after external lore edit");
        check(stats.getLoreValue(externallyEdited, LoreStatsEnum.DAMAGE, null) == 0,
                "stale sidecar cannot restore deleted stats");
        getLogger().info("PASS: Paper Stats item lore NBT and PvP/PvE branch contracts");
    }

    /** Disposable mob-vs-mob live damage hook; client PvP requires separate players. */
    private void verifyRealMobStatsCombat() {
        final MyItems myitems = (MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem");
        final LoreStatsManager stats = myitems.getGameManager().getStatsManager();
        final World world = Bukkit.getWorlds().get(0);
        final Location pos = world.getSpawnLocation().clone().add(5.5, 3, 5.5);
        final Zombie a = world.spawn(pos, Zombie.class);
        final Zombie b = world.spawn(pos.clone().add(3, 0, 0), Zombie.class);
        try {
            a.setAI(false);
            b.setAI(false);
            a.setSilent(true);
            b.setSilent(true);
            check(a.getEquipment() != null && b.getEquipment() != null, "mob equipment");
            final ItemStack weapon = new ItemStack(Material.IRON_SWORD);
            EquipmentUtil.addLore(weapon, stats.getTextLoreStats(LoreStatsEnum.DAMAGE, 5));
            a.getEquipment().setItemInMainHand(weapon);
            final ItemStack armor = new ItemStack(Material.IRON_CHESTPLATE);
            EquipmentUtil.addLore(armor, stats.getTextLoreStats(LoreStatsEnum.DEFENSE, 2));
            b.getEquipment().setChestplate(armor);
            check(stats.getLoreStatsWeapon(a).getDamage() >= 5, "equipped mob damage");
            check(stats.getLoreStatsArmor(b).getDefense() >= 2, "equipped mob defense");
            final double before = b.getHealth();
            b.damage(2, a);
            final double after = b.getHealth();
            check(Double.isFinite(after) && after >= 0 && after <= before, "finite live mob combat health");

            // End-to-end regression for the actual user report:
            // /attr Ability Venom_Blast 10 100 -> real lore -> equipped weapon
            // -> chance roll -> Paper entity damage event -> cast effect.
            final AbilityWeaponManager weaponSkills = myitems.getGameManager().getAbilityWeaponManager();
            final AbilityWeapon venomBlast = myitems.getRegisterManager()
                    .getRegisterAbilityWeaponManager().getAbilityWeapon("Venom_Blast");
            check(venomBlast != null, "Venom_Blast exists in live registry");
            final ItemStack venomSword = new ItemStack(Material.IRON_SWORD);
            final String venomLore = weaponSkills.getTextAbility("Venom_Blast", 10, 100.0);
            EquipmentUtil.addLore(venomSword, venomLore);
            final var parsedVenom = weaponSkills.getListAbilityItemWeapon(venomSword);
            check(parsedVenom.size() == 1
                            && parsedVenom.get(0).getAbility().equalsIgnoreCase("Venom_Blast")
                            && parsedVenom.get(0).getGrade() == 10
                            && parsedVenom.get(0).getChance() == 100.0,
                    "Venom_Blast 100% item lore must decode and preserve grade");
            final ItemStack persistedVenom =
                    ItemStack.deserializeBytes(venomSword.serializeAsBytes());
            final var persistedSkills = weaponSkills.getListAbilityItemWeapon(persistedVenom);
            check(persistedSkills.size() == 1
                            && persistedSkills.get(0).getGrade() == 10
                            && persistedSkills.get(0).getChance() == 100.0,
                    "Venom_Blast 100% survives real Paper serialized ItemStack");
            a.getEquipment().setItemInMainHand(persistedVenom);
            final var rolledSkills = weaponSkills.getMapAbilityWeapon(a, true);
            check(rolledSkills.containsKey(venomBlast) && rolledSkills.get(venomBlast) == 10,
                    "Venom_Blast 100% must survive equipped-weapon chance roll");
            b.setNoDamageTicks(0);
            b.removePotionEffect(org.bukkit.potion.PotionEffectType.WITHER);
            b.damage(2.0, a);
            check(b.hasPotionEffect(org.bukkit.potion.PotionEffectType.WITHER),
                    "Venom_Blast equipped real Paper hit must apply Wither to zombies");
            getLogger().info("PASS: Venom_Blast 10 at 100% equipped weapon -> real hit -> Wither");

            // Simulate a protection plugin cancelling the attack at HIGH,
            // before MyItems' HIGHEST damage listener. No damage, skill,
            // or extra lore-durability usage may be applied to this hit.
            b.setNoDamageTicks(0);
            final ItemStack limited = new ItemStack(Material.IRON_SWORD);
            EquipmentUtil.addLore(limited, stats.getTextLoreStats(LoreStatsEnum.DAMAGE, 5));
            EquipmentUtil.addLore(limited, stats.getTextLoreStats(LoreStatsEnum.DURABILITY, 3, 3));
            a.getEquipment().setItemInMainHand(limited);
            final boolean[] intercepted = { false };
            final org.bukkit.event.Listener protection = new org.bukkit.event.Listener() {
                @org.bukkit.event.EventHandler(priority = org.bukkit.event.EventPriority.HIGH)
                public void cancelProtectedAttack(org.bukkit.event.entity.EntityDamageByEntityEvent hit) {
                    if (hit.getDamager().equals(a) && hit.getEntity().equals(b)) {
                        intercepted[0] = true;
                        hit.setCancelled(true);
                    }
                }
            };
            Bukkit.getPluginManager().registerEvents(protection, this);
            try {
                final double protectedHealth = b.getHealth();
                final int protectedDurability = (int)stats.getLoreValue(
                        a.getEquipment().getItemInMainHand(), LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT);
                b.damage(2, a);
                check(intercepted[0], "protection plugin intercepted actual Paper damage event");
                check(b.getHealth() == protectedHealth, "cancelled hit cannot change victim health");
                check(stats.getLoreValue(a.getEquipment().getItemInMainHand(),
                        LoreStatsEnum.DURABILITY, LoreStatsOption.CURRENT) == protectedDurability,
                        "cancelled hit cannot consume weapon lore durability");
            } finally {
                org.bukkit.event.HandlerList.unregisterAll(protection);
            }
            getLogger().info("PASS: Paper live mob-vs-mob stats damage event");
            getLogger().info("PASS: Paper HIGH-priority protection cancellation interoperability");
        } finally {
            if (a.isValid()) a.remove();
            if (b.isValid()) b.remove();
        }
    }


    private static Player mockPermissionPlayer(java.util.Set<String> granted) {
        return (Player) java.lang.reflect.Proxy.newProxyInstance(
                Player.class.getClassLoader(), new Class<?>[] {Player.class},
                (instance, method, args) -> {
                    if (method.getName().equals("hasPermission") && args.length == 1)
                        return granted.contains(((String) args[0]).toLowerCase(java.util.Locale.ROOT));
                    if (method.getName().equals("isOp")) return false;
                    if (method.getName().equals("toString")) return "LyMeowzPermissionProbe";
                    if (method.getReturnType() == boolean.class) return false;
                    if (method.getReturnType() == int.class) return 0;
                    return null;
                });
    }

    private void verifyPermissionMigration() {
        final MyItems item = (MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem");
        final AgarthaLib lib = (AgarthaLib) Bukkit.getPluginManager().getPlugin("LyMeowzLib");
        final String itemOld = "myitems.command.reload";
        final String itemNew = "lymeowzitem.command.reload";
        final String libOld = "agarthalib.command.reload";
        final String libNew = "lymeowzlib.command.reload";
        check(Bukkit.getPluginManager().getPermission("lymeowzitem.admin") != null, "new item admin registered");
        check(Boolean.TRUE.equals(Bukkit.getPluginManager().getPermission("lymeowzitem.admin")
                        .getChildren().get("lymeowzitem.command.model")),
                "new item admin inherits model permission by declared child, not wildcard");
        check(Boolean.TRUE.equals(Bukkit.getPluginManager().getPermission("lymeowzlib.admin")
                        .getChildren().get("lymeowzlib.command.reload")),
                "new lib admin inherits reload permission by declared child");
        check(Bukkit.getPluginManager().getPermission(itemNew) != null, "new item command registered");
        check(Bukkit.getPluginManager().getPermission("lymeowzlib.admin") != null, "new lib admin registered");
        check(Bukkit.getPluginManager().getPermission(libNew) != null, "new lib command registered");
        check(ItemPermissions.canonical(itemOld).equals(itemNew), "item canonical mapping");
        check(PermissionBridge.canonical(libOld).equals(libNew), "lib canonical mapping");
        check(ItemPermissions.canonical("essentials.warp").equals("essentials.warp"), "third-party nodes unchanged");
        check(item.getPluginManager().getCommandManager().getPermission("MyItems_Reload").equals(itemNew),
                "item permission API now reports LyMeowz node");
        check(lib.getPluginManager().getCommandManager().getPermission("AgarthaLib_Reload").equals(libNew),
                "lib permission API now reports LyMeowz node");
        for (String granted : new String[] {itemOld, itemNew}) {
            Player player = mockPermissionPlayer(java.util.Set.of(granted));
            check(item.getPluginManager().getCommandManager().checkPermission(player, "MyItems_Reload"),
                    "item command accepts " + granted);
            check(!lib.getPluginManager().getCommandManager().checkPermission(player, "AgarthaLib_Reload"),
                    "item permission cannot grant lib command");
        }
        for (String granted : new String[] {libOld, libNew}) {
            Player player = mockPermissionPlayer(java.util.Set.of(granted));
            check(lib.getPluginManager().getCommandManager().checkPermission(player, "AgarthaLib_Reload"),
                    "lib command accepts " + granted);
            check(!item.getPluginManager().getCommandManager().checkPermission(player, "MyItems_Reload"),
                    "lib permission cannot grant item command");
        }
        final Player denied = mockPermissionPlayer(java.util.Set.of());
        check(!item.getPluginManager().getCommandManager().checkPermission(denied, "MyItems_Reload"),
                "item command denies player without either namespace");
        check(!lib.getPluginManager().getCommandManager().checkPermission(denied, "AgarthaLib_Reload"),
                "lib command denies player without either namespace");
        check(!ItemPermissions.allowed(denied, "myitems.command.model"), "new model command never bypasses permission");
        check(ItemPermissions.allowed(mockPermissionPlayer(java.util.Set.of("myitems.command.model")),
                "myitems.command.model"), "old model permission remains accepted");
        check(ItemPermissions.allowed(mockPermissionPlayer(java.util.Set.of("lymeowzitem.command.model")),
                "myitems.command.model"), "new model permission works");
        getLogger().info("PASS: LyMeowz permission migration (old/new, independent, denied)");
    }

    private void verifyRenamedPlugins() throws Exception {
        check(Bukkit.getPluginManager().getPlugin("LyMeowzLib") instanceof AgarthaLib,
                "LyMeowzLib plugin registered under new Bukkit identity");
        check(Bukkit.getPluginManager().getPlugin("LyMeowzItem") instanceof MyItems,
                "LyMeowzItem plugin registered under new Bukkit identity");
        check(com.lymeowz.item.modern.Paper26ModelCommand.class.getMethod(
                "execute", org.bukkit.command.CommandSender.class, String[].class) != null,
                "old model command ABI preserved");
        check(com.lymeowz.item.command.ModelCommand.class.getMethod(
                "execute", org.bukkit.command.CommandSender.class, String[].class) != null,
                "canonical model command ABI exported");
        check(Bukkit.getPluginManager().getPlugin("LyMeowzItem").getClass().getName()
                        .equals("com.lymeowz.item.LyMeowzItem"),
                "MyItems Bukkit main class lives in com.lymeowz.item");
        check(Bukkit.getPluginManager().getPlugin("LyMeowzLib").getClass().getName()
                        .equals("com.lymeowz.lib.LyMeowzLib"),
                "AgarthaLib Bukkit main class lives in com.lymeowz.lib");
        check(com.lymeowz.lib.command.CommandText.rewrite(
                "Use /myitems help and /agarthalib reload")
                .equals("Use /lymeowzitem help and /lymeowzlib reload"),
                "new command text converter retains legacy grammar");
        check(com.lymeowz.lib.modern.Paper26CommandText.rewrite(
                "Use /myitems help").equals("Use /lymeowzitem help"),
                "old command text Java API delegates to LyMeowz package");
        check(((MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem"))
                .getPluginManager().getCommandManager().getCommand("MyItems_Reload") != null,
                "MyItems_Reload ID preserved through relocated command parser");
        check(((AgarthaLib) Bukkit.getPluginManager().getPlugin("LyMeowzLib"))
                .getPluginManager().getCommandManager().getCommandBuild("AgarthaLib_Reload") != null,
                "AgarthaLib_Reload ID preserved through relocated command parser");
        check(JavaPlugin.getPlugin(MyItems.class)
                        == Bukkit.getPluginManager().getPlugin("LyMeowzItem"),
                "legacy MyItems JavaPlugin.getPlugin API resolves new main class");
        check(JavaPlugin.getPlugin(AgarthaLib.class)
                        == Bukkit.getPluginManager().getPlugin("LyMeowzLib"),
                "legacy AgarthaLib JavaPlugin.getPlugin API resolves new main class");
        check(JavaPlugin.getPlugin(com.lymeowz.item.LyMeowzItem.class)
                        == Bukkit.getPluginManager().getPlugin("LyMeowzItem"),
                "new LyMeowzItem JavaPlugin.getPlugin lookup works");
        check(JavaPlugin.getPlugin(com.lymeowz.lib.LyMeowzLib.class)
                        == Bukkit.getPluginManager().getPlugin("LyMeowzLib"),
                "new LyMeowzLib JavaPlugin.getPlugin lookup works");
        check(Bukkit.getPluginManager().getPlugin("AgarthaLib") instanceof AgarthaLib,
                "old AgarthaLib soft/hard dependencies resolve via provides");
        check(Bukkit.getPluginManager().getPlugin("MyItems") instanceof MyItems,
                "old MyItems soft/hard dependencies resolve via provides");
        check(Bukkit.getPluginManager().getPermission("myitems.admin") != null,
                "legacy MyItems permission node preserved");
        check(Bukkit.getPluginManager().getPermission("agarthalib.admin") != null,
                "legacy AgarthaLib permission node preserved");
        final MyItems lymeowzItem = (MyItems) Bukkit.getPluginManager().getPlugin("LyMeowzItem");
        final var paths = lymeowzItem.getPluginManager().getDataManager();
        check("Resources/command.yml".equals(paths.getPath("Path_File_Command")),
                "new data.yml decoder retains command configuration path");
        check("Resources/dependency.yml".equals(paths.getPath("Path_File_Dependency")),
                "new data.yml decoder retains dependency path");
        check("Database/item.yml".equals(paths.getPath("Path_File_Item")),
                "new data.yml decoder retains item database path");
        check(paths.getListPath("Path_File_Language").contains("Language/lang_en.yml"),
                "new data.yml decoder retains language file paths");
        check(paths.getDataConfig().getData("Path_File_Command") != null,
                "old DataConfig API still exposes path dictionary builds");
        final AgarthaLib lymeowzLib = (AgarthaLib) Bukkit.getPluginManager().getPlugin("LyMeowzLib");
        final com.lymeowz.lib.manager.GameServices libraryServices =
                com.lymeowz.lib.manager.GameAccess.of(
                        (com.lymeowz.lib.LyMeowzLib) lymeowzLib);
        check(libraryServices == lymeowzLib.getGameManager(),
                "new library manager API resolves original live GameManager");
        check(com.lymeowz.lib.manager.GameAccess.of(null) == null,
                "new library manager accessor tolerates missing plugin");
        check(libraryServices.effectTypeIds().containsAll(
                        lymeowzLib.getGameManager().getEffectTypeManager().getEffectTypeIds()),
                "new library manager API preserves effect registry IDs");
        check(libraryServices.trackedEntityStatsCount()
                        == lymeowzLib.getGameManager().getStatsEntityManager().getEntityIds().size(),
                "new library manager API reports same tracked entity stats");
        check(libraryServices.trackedProjectileCount()
                        == lymeowzLib.getGameManager().getProjectilePropertiesManager().getProjectileIds().size(),
                "new library manager API reports same tracked projectiles");
        check(!libraryServices.hasEffectType("missing-lymeowz-effect-id"),
                "new library manager effect lookup rejects unknown IDs");
        try {
            libraryServices.effectTypeIds().clear();
            throw new AssertionError("new library manager effect IDs allow mutation");
        } catch (UnsupportedOperationException expected) {
            // Legacy effect manager keeps its prior mutable collection signature.
        }
        final com.lymeowz.lib.manager.PluginServices libraryPluginServices =
                com.lymeowz.lib.manager.PluginAccess.of(
                        (com.lymeowz.lib.LyMeowzLib) lymeowzLib);
        check(libraryPluginServices == lymeowzLib.getPluginManager(),
                "new LyMeowz library plugin services return original manager instance");
        check(com.lymeowz.lib.manager.PluginAccess.of(null) == null,
                "new library plugin service accessor handles missing plugin");
        check(libraryPluginServices.commands()
                        == lymeowzLib.getPluginManager().getCommandManager(),
                "new library plugin services use original live command registry");
        check(libraryPluginServices.languageIds().containsAll(
                        lymeowzLib.getPluginManager().getLanguageManager().getLanguageIds()),
                "new library plugin API preserves original locale IDs");
        check(libraryPluginServices.languageLines("en", "Argument_AgarthaLib_Help").equals(
                        lymeowzLib.getPluginManager().getLanguageManager().getListText(
                                "en", "Argument_AgarthaLib_Help")),
                "new library plugin API keeps existing help text contents");
        check(java.util.Objects.equals(libraryPluginServices.placeholder("prefix"),
                        lymeowzLib.getPluginManager().getPlaceholderManager()
                                .getPlaceholder("prefix")),
                "new library placeholder API returns original values");
        try {
            libraryPluginServices.languageIds().clear();
            throw new AssertionError("canonical library language ID collection is mutable");
        } catch (UnsupportedOperationException expected) { }
        try {
            libraryPluginServices.languageLines("en", "Argument_AgarthaLib_Help").clear();
            throw new AssertionError("canonical library help lines allow mutation");
        } catch (UnsupportedOperationException expected) { }
        check(lymeowzLib.getPluginManager().getCommandManager()
                        instanceof com.lymeowz.lib.command.CommandRegistry,
                "legacy library manager implements new CommandRegistry API");
        final com.lymeowz.lib.command.CommandRegistry newCommands =
                lymeowzLib.getPluginManager().getCommandManager();
        check(newCommands.checkCommand("rElOaD", "AgarthaLib_Reload")
                        && newCommands.checkCommand("SETUP", "AgarthaLib_Reload")
                        && !newCommands.checkCommand("unknown", "AgarthaLib_Reload"),
                "LyMeowzLib command registry preserves main and alias case matching");
        check("lymeowzlib.command.reload".equals(newCommands.getPermission("AgarthaLib_Reload")),
                "LyMeowzLib command registry returns new canonical permission");
        check(!com.lymeowz.lib.command.CommandLookup.matches(null, "reload"),
                "LyMeowzLib command matcher preserves absent-command behavior");
        check(lymeowzItem.getPluginManager().getLanguageManager()
                        .getListText("en", "Argument_MyItems_Help").stream()
                        .anyMatch(line -> line.contains("/lymeowzitem help")),
                "real legacy MyItems help strings display /lymeowzitem");
        check(lymeowzLib.getPluginManager().getLanguageManager()
                        .getListText("en", "Argument_AgarthaLib_Help").stream()
                        .anyMatch(line -> line.contains("/lymeowzlib help")),
                "real legacy AgarthaLib help strings display /lymeowzlib");
        check(lymeowzItem.getPluginManager().getPlaceholderManager().getPlaceholderConfig()
                        .getPlaceholder("prefix").contains("LyMeowzItem"),
                "legacy default chat prefix updated without editing user YAML");
        check(Bukkit.getPluginCommand("lymeowzitem") != null,
                "new /lymeowzitem root registered");
        check(Bukkit.getPluginCommand("lymeowzlib") != null,
                "new /lymeowzlib root registered");
        check(Bukkit.getPluginCommand("myitems") == null,
                "old /myitems command is not registered");
        check(Bukkit.getPluginCommand("agarthalib") == null,
                "old /agarthalib command is not registered");
        for (String alias : new String[] {"myitem", "itemeditor", "items", "item", "mi",
                "itematt", "enchant", "itemname", "lore", "socket", "nbtclear"}) {
            check(Bukkit.getPluginCommand(alias) != null, "legacy command /" + alias + " preserved");
        }
        for (String[] folders : new String[][] {
                {"MyItems", "LyMeowzItem"}, {"AgarthaLib", "LyMeowzLib"}
        }) {
            final java.nio.file.Path parent = this.getDataFolder().getParentFile().toPath();
            final java.nio.file.Path from = parent.resolve(folders[0]).resolve("legacy-rename-probe.txt");
            final java.nio.file.Path into = parent.resolve(folders[1]).resolve("legacy-rename-probe.txt");
            final String expected = "Original " + folders[0] + " config marker";
            check(java.nio.file.Files.readString(from).equals(expected),
                    "old " + folders[0] + " data folder preserved");
            check(java.nio.file.Files.readString(into).equals(expected),
                    "new " + folders[1] + " folder inherited old data");
            check(java.nio.file.Files.readString(
                    parent.resolve(folders[1]).resolve("collision-rename-probe.txt"))
                    .equals("New folder wins"), "pre-existing new config never overwritten");
            check(!java.nio.file.Files.exists(
                    parent.resolve(folders[1]).resolve("unsafe-link-probe.txt")),
                    "legacy symlink must never be followed or copied");
        }
        getLogger().info("PASS: LyMeowz plugin identities, command aliases and old data folders");
    }

    /** Exercise the canonical YAML reader without touching player or server files. */
    private void verifyItemDefinitions() {
        final org.bukkit.configuration.file.YamlConfiguration yaml =
                new org.bukkit.configuration.file.YamlConfiguration();
        final ItemStack stored = new ItemStack(Material.DIAMOND_SWORD);
        yaml.set("MixedCaseSword", stored);
        yaml.set("unrelated-setting", "not an ItemStack");

        final java.util.Map<String, ItemStack> decoded =
                com.lymeowz.item.config.ItemDefinitions.read(yaml, ItemStack::clone);
        check(decoded.size() == 1 && decoded.containsKey("MixedCaseSword"),
                "canonical item YAML reader preserves case-sensitive IDs and skips non-items");
        check(decoded.get("MixedCaseSword") != stored
                        && decoded.get("MixedCaseSword").getType() == stored.getType(),
                "canonical item YAML reader retains original clone conversion");
        check(stored.getType() == Material.DIAMOND_SWORD,
                "canonical item YAML reader does not mutate the stored example");
        getLogger().info("PASS: canonical item YAML decoder and legacy item IDs");
    }

    @Override
    public void onEnable() {
        try {
            verifyRenamedPlugins();
            verifyItemDefinitions();
            verifyPermissionMigration();
            ItemStack original = new ItemStack(Material.DIAMOND_SWORD);
            final int originalAmount = original.getAmount();
            final byte[] bytes = original.serializeAsBytes();
            final CompoundBinaryTag root = BinaryTagIO.reader(4 * 1024 * 1024L)
                    .read(new ByteArrayInputStream(bytes), BinaryTagIO.Compression.GZIP);

            final CompoundBinaryTag oldTags = CompoundBinaryTag.builder()
                    .putString("LegacyID", "Kiếm:&a|old")
                    .putInt("Level", 14)
                    .put("Stats12", ListBinaryTag.builder(BinaryTagTypes.STRING)
                            .add(StringBinaryTag.stringBinaryTag("Speed:2"))
                            .add(StringBinaryTag.stringBinaryTag("Damage:5"))
                            .build())
                    .put("Custom", CompoundBinaryTag.builder()
                            .put("Stats15", ListBinaryTag.builder(BinaryTagTypes.COMPOUND)
                                    .add(CompoundBinaryTag.builder()
                                            .putString("stat", "Armor:4").build())
                                    .build())
                            .build())
                    .build();
            final CompoundBinaryTag originalComponents = root.getCompound("components");
            final CompoundBinaryTag.Builder components = CompoundBinaryTag.builder();
            for (String key : originalComponents.keySet())
                components.put(key, originalComponents.get(key));
            components.put("minecraft:custom_data", oldTags);

            final CompoundBinaryTag.Builder rebuilt = CompoundBinaryTag.builder();
            for (String key : root.keySet()) {
                if (!"components".equals(key)) rebuilt.put(key, root.get(key));
            }
            rebuilt.put("components", components.build());
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            BinaryTagIO.writer().write(rebuilt.build(), output, BinaryTagIO.Compression.GZIP);

            final ItemStack restored = ItemStack.deserializeBytes(output.toByteArray());
            check(restored.getType() == Material.DIAMOND_SWORD, "material preserved");
            check(restored.getAmount() == originalAmount, "stack amount preserved");
            check("Kiếm:&a|old".equals(
                    Bridge.getBridgeTagsNBT().getString("LegacyID", restored)),
                    "legacy string read through actual BridgeTagsNBT");
            check(Bridge.getBridgeTagsNBT().getInt("Level", restored) == 14,
                    "historical integer read through BridgeTagsNBT");
            check(List.of("Speed:2","Damage:5").equals(
                    Bridge.getBridgeTagsNBT().getListString("Stats12", restored)),
                    "Minecraft 1.12 List<String> survives");
            check(List.of("Armor:4").equals(
                    Bridge.getBridgeTagsNBT().getListString("Stats15", "Custom", restored)),
                    "Minecraft 1.15 List<Compound{stat}> survives");
            check(Paper26LegacyItemTags.customData(restored) != null, "old data observable");

            Bridge.getBridgeTagsNBT().setString("LegacyID", restored, "modern");
            check("modern".equals(Bridge.getBridgeTagsNBT().getString("LegacyID", restored)),
                    "new PDC has precedence over old raw NBT");
            Bridge.getBridgeTagsNBT().remove("LegacyID", restored);
            check(Bridge.getBridgeTagsNBT().getString("LegacyID", restored) == null,
                    "PDC tombstone prevents resurrecting intentionally removed NBT");
            check(!"modern".equals(Bridge.getBridgeTagsNBT().getString("LegacyID", original)),
                    "original untouched item does not inherit migrated metadata");

            // Modifying old items with current lore must not wipe either the
            // historical root NBT or the modern lore identity sidecar.
            final LoreStatsManager stats = ((MyItems) Bukkit.getPluginManager()
                    .getPlugin("LyMeowzItem")).getGameManager().getStatsManager();
            EquipmentUtil.addLore(restored, stats.getTextLoreStats(LoreStatsEnum.DAMAGE, 9.0));
            final ItemStack migrated = ItemStack.deserializeBytes(restored.serializeAsBytes());
            check(Bridge.getBridgeTagsNBT().getInt("Level", migrated) == 14,
                    "old numeric NBT survives modern marked lore write");
            check(List.of("Speed:2", "Damage:5").equals(
                    Bridge.getBridgeTagsNBT().getListString("Stats12", migrated)),
                    "old compound data survives modern marked lore write");
            check(stats.getLoreValue(migrated, LoreStatsEnum.DAMAGE, null) == 9,
                    "marked combat stat survives legacy NBT item roundtrip");
            getLogger().info("PASS: live legacy NBT read-only fallback and PDC precedence");
            verifyWeaponAbilities();
            verifyCanonicalEffectAndPowerCommands();
            verifyCanonicalProjectileViews();
            verifyCanonicalSocketRegistry();
            verifyCanonicalItemSets();
            verifyElementPotionViews();
            verifyItemTypesAndTiers();
            verifyItemGenerators();
            verifyMobSkillCasts();
            verifyEssentialsWarpProvider();
            verifyItemModels();
            verifyHexItemColors();
            verifyRealStatsLore();
            verifyRealMobStatsCombat();
        } catch (Throwable error) {
            getLogger().severe("FAIL: live NBT/ability compatibility test: " + error);
            throw new IllegalStateException("Paper26 live NBT probe failed", error);
        }
    }
}
