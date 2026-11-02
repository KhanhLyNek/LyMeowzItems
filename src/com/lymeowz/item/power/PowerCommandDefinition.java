package com.lymeowz.item.power;

import java.util.List;

/** Metadata only; never executes commands or exposes mutable script lists. */
public interface PowerCommandDefinition {
    String keyLore();
    boolean consumesItem();
    List<String> opCommands();
    List<String> consoleCommands();
}
