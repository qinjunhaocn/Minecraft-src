/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.JsonOps
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.PlaceholderLookupProvider;
import net.minecraft.util.StrictJsonParser;
import org.slf4j.Logger;

public class ClientItemInfoLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter LISTER = FileToIdConverter.json("items");

    public static CompletableFuture<LoadedClientInfos> scheduleLoad(ResourceManager $$0, Executor $$1) {
        RegistryAccess.Frozen $$22 = ClientRegistryLayer.createRegistryAccess().compositeAccess();
        return CompletableFuture.supplyAsync(() -> LISTER.listMatchingResources($$0), $$1).thenCompose($$2 -> {
            ArrayList $$32 = new ArrayList($$2.size());
            $$2.forEach(($$3, $$4) -> $$32.add(CompletableFuture.supplyAsync(() -> {
                PendingLoad pendingLoad;
                block8: {
                    Object $$3 = LISTER.fileToId((ResourceLocation)$$3);
                    Object $$4 = $$4.openAsReader();
                    try {
                        PlaceholderLookupProvider $$5 = new PlaceholderLookupProvider($$22);
                        RegistryOps $$6 = $$5.createSerializationContext(JsonOps.INSTANCE);
                        ClientItem $$7 = ClientItem.CODEC.parse($$6, (Object)StrictJsonParser.parse($$4)).ifError($$2 -> LOGGER.error("Couldn't parse item model '{}' from pack '{}': {}", $$3, $$4.sourcePackId(), $$2.message())).result().map($$1 -> {
                            if ($$5.hasRegisteredPlaceholders()) {
                                return $$1.withRegistrySwapper($$5.createSwapper());
                            }
                            return $$1;
                        }).orElse(null);
                        pendingLoad = new PendingLoad((ResourceLocation)$$3, $$7);
                        if ($$4 == null) break block8;
                    } catch (Throwable throwable) {
                        try {
                            if ($$4 != null) {
                                try {
                                    ((Reader)$$4).close();
                                } catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        } catch (Exception $$8) {
                            LOGGER.error("Failed to open item model {} from pack '{}'", $$3, $$4.sourcePackId(), $$8);
                            return new PendingLoad((ResourceLocation)$$3, null);
                        }
                    }
                    ((Reader)$$4).close();
                }
                return pendingLoad;
            }, $$1)));
            return Util.sequence($$32).thenApply($$0 -> {
                Executor $$1 = new HashMap<ResourceLocation, ClientItem>();
                for (PendingLoad $$2 : $$0) {
                    if ($$2.clientItemInfo == null) continue;
                    $$1.put($$2.id, $$2.clientItemInfo);
                }
                return new LoadedClientInfos((Map<ResourceLocation, ClientItem>)((Object)$$1));
            });
        });
    }

    static final class PendingLoad
    extends Record {
        final ResourceLocation id;
        @Nullable
        final ClientItem clientItemInfo;

        PendingLoad(ResourceLocation $$0, @Nullable ClientItem $$1) {
            this.id = $$0;
            this.clientItemInfo = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{PendingLoad.class, "id;clientItemInfo", "id", "clientItemInfo"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PendingLoad.class, "id;clientItemInfo", "id", "clientItemInfo"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PendingLoad.class, "id;clientItemInfo", "id", "clientItemInfo"}, this, $$0);
        }

        public ResourceLocation id() {
            return this.id;
        }

        @Nullable
        public ClientItem clientItemInfo() {
            return this.clientItemInfo;
        }
    }

    public record LoadedClientInfos(Map<ResourceLocation, ClientItem> contents) {
    }
}

