package com.lymeowz.item.config;

import com.praya.agarthalib.utility.EquipmentUtil;
import com.praya.agarthalib.utility.FileUtil;
import com.praya.myitems.MyItems;
import java.io.File;
import java.util.HashMap;
import java.util.function.UnaryOperator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Canonical reader for the existing item YAML format.
 *
 * The original ItemConfig still owns legacy database migration, item write
 * conversion, and its public Java API. This reader deliberately uses the
 * original Bukkit ItemStack YAML deserializer and the original lore/game
 * converter so item IDs, serialized metadata and gameplay remain unchanged.
 */
public final class ItemDefinitions {
    private ItemDefinitions() {
    }

    public static HashMap<String, ItemStack> load(final MyItems plugin,
                                                  final String path,
                                                  final UnaryOperator<ItemStack> toGame) {
        final File file = FileUtil.getFile((JavaPlugin) plugin, path);
        if (!file.exists()) {
            FileUtil.saveResource((JavaPlugin) plugin, path);
        }
        return read(FileUtil.getFileConfiguration(file), toGame);
    }

    /**
     * Preserve exact YAML keys and ignore values which are not ItemStacks.
     * The supplied converter is the original ItemConfig.convertToGame,
     * including all historical @MyItems lore aliases.
     */
    public static HashMap<String, ItemStack> read(final FileConfiguration config,
                                                  final UnaryOperator<ItemStack> toGame) {
        final HashMap<String, ItemStack> items = new HashMap<>();
        for (final String key : config.getKeys(false)) {
            final ItemStack stored = config.getItemStack(key);
            if (stored != null) {
                final ItemStack gameItem = toGame.apply(stored);
                EquipmentUtil.colorful(gameItem);
                items.put(key, gameItem);
            }
        }
        return items;
    }
}
