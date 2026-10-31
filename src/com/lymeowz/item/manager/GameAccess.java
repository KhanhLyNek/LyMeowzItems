package com.lymeowz.item.manager;

import com.lymeowz.item.LyMeowzItem;

/** Obtain the canonical manager view from the live LyMeowzItem instance. */
public final class GameAccess {
    private GameAccess() { }

    public static GameServices of(final LyMeowzItem plugin) {
        if (plugin == null || !plugin.isEnabled()) {
            return null;
        }
        return plugin.getGameManager();
    }
}
