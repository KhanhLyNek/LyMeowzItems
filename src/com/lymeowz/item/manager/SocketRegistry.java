package com.lymeowz.item.manager;

import com.lymeowz.item.socket.SocketDefinition;
import java.util.Collection;

/** Canonical read-only access to the original socket manager. */
public interface SocketRegistry {
    Collection<String> ids();
    SocketDefinition find(String id);
}
