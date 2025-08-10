/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.server;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface PackReloadConfig {
    public void scheduleReload(Callbacks var1);

    public static interface Callbacks {
        public void onSuccess();

        public void onFailure(boolean var1);

        public List<IdAndPath> packsToLoad();
    }

    public record IdAndPath(UUID id, Path path) {
    }
}

