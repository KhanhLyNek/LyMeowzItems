package com.lymeowz.item.config;

import api.praya.myitems.builder.power.PowerCommandProperties;
import com.praya.agarthalib.utility.TextUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Existing Power Command YAML contract, isolated in the canonical namespace.
 * Command IDs, OP/Console execution targets, order and Consume flags stay
 * exactly as the original parser produced them. Does not execute commands.
 */
public final class PowerCommandDefinitions {
    private PowerCommandDefinitions() { }

    public static HashMap<String, PowerCommandProperties> read(final FileConfiguration config) {
        final HashMap<String, PowerCommandProperties> results = new HashMap<>();
        for (final String key : config.getKeys(false)) {
            final ConfigurationSection mainDataSection = config.getConfigurationSection(key);
            if (mainDataSection == null) {
                continue;
            }
            final List<String> commandOP = new ArrayList<String>();
            final List<String> commandConsole = new ArrayList<String>();
            String keyLore = null;
            boolean consume = false;
            for (final String mainData : mainDataSection.getKeys(false)) {
                if (mainData.equalsIgnoreCase("KeyLore")) {
                    keyLore = TextUtil.colorful(mainDataSection.getString(mainData));
                }
                else if (mainData.equalsIgnoreCase("Consume")) {
                    consume = mainDataSection.getBoolean(mainData);
                }
                else {
                    if (!mainData.equalsIgnoreCase("command")) {
                        continue;
                    }
                    if (mainDataSection.isList(mainData)) {
                        commandOP.addAll(mainDataSection.getStringList(mainData));
                    }
                    else if (mainDataSection.isString(mainData)) {
                        commandOP.add(mainDataSection.getString(mainData));
                    }
                    else {
                        if (!mainDataSection.isConfigurationSection(mainData)) {
                            continue;
                        }
                        final ConfigurationSection commandDataSection = mainDataSection.getConfigurationSection(mainData);
                        if (commandDataSection == null) {
                            continue;
                        }
                        for (final String commandData : commandDataSection.getKeys(false)) {
                            if (commandData.equalsIgnoreCase("OP")) {
                                if (commandDataSection.isList(commandData)) {
                                    commandOP.addAll(commandDataSection.getStringList(commandData));
                                }
                                else {
                                    if (!commandDataSection.isString(commandData)) {
                                        continue;
                                    }
                                    commandOP.add(commandDataSection.getString(commandData));
                                }
                            }
                            else {
                                if (!commandData.equalsIgnoreCase("Console")) {
                                    continue;
                                }
                                if (commandDataSection.isList(commandData)) {
                                    commandConsole.addAll(commandDataSection.getStringList(commandData));
                                }
                                else {
                                    if (!commandDataSection.isString(commandData)) {
                                        continue;
                                    }
                                    commandConsole.add(commandDataSection.getString(commandData));
                                }
                            }
                        }
                    }
                }
            }
            if (keyLore != null && (!commandOP.isEmpty() || !commandConsole.isEmpty())) {
                final PowerCommandProperties powerCommandProperties = new PowerCommandProperties(keyLore, consume, commandOP, commandConsole);
                results.put(key, powerCommandProperties);
            }
        }
        return results;
    }
}
