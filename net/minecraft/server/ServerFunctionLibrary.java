/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerFunctionLibrary
implements PreparableReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceKey<Registry<CommandFunction<CommandSourceStack>>> TYPE_KEY = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("function"));
    private static final FileToIdConverter LISTER = new FileToIdConverter(Registries.elementsDirPath(TYPE_KEY), ".mcfunction");
    private volatile Map<ResourceLocation, CommandFunction<CommandSourceStack>> functions = ImmutableMap.of();
    private final TagLoader<CommandFunction<CommandSourceStack>> tagsLoader = new TagLoader(($$0, $$1) -> this.getFunction($$0), Registries.tagsDirPath(TYPE_KEY));
    private volatile Map<ResourceLocation, List<CommandFunction<CommandSourceStack>>> tags = Map.of();
    private final int functionCompilationLevel;
    private final CommandDispatcher<CommandSourceStack> dispatcher;

    public Optional<CommandFunction<CommandSourceStack>> getFunction(ResourceLocation $$0) {
        return Optional.ofNullable(this.functions.get($$0));
    }

    public Map<ResourceLocation, CommandFunction<CommandSourceStack>> getFunctions() {
        return this.functions;
    }

    public List<CommandFunction<CommandSourceStack>> getTag(ResourceLocation $$0) {
        return this.tags.getOrDefault($$0, List.of());
    }

    public Iterable<ResourceLocation> getAvailableTags() {
        return this.tags.keySet();
    }

    public ServerFunctionLibrary(int $$02, CommandDispatcher<CommandSourceStack> $$12) {
        this.functionCompilationLevel = $$02;
        this.dispatcher = $$12;
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$02, ResourceManager $$1, Executor $$2, Executor $$3) {
        CompletableFuture<Map> $$4 = CompletableFuture.supplyAsync(() -> this.tagsLoader.load($$1), $$2);
        CompletionStage $$5 = CompletableFuture.supplyAsync(() -> LISTER.listMatchingResources($$1), $$2).thenCompose($$12 -> {
            HashMap<ResourceLocation, CompletableFuture<CommandFunction>> $$22 = Maps.newHashMap();
            CommandSourceStack $$3 = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, null, this.functionCompilationLevel, "", CommonComponents.EMPTY, null, null);
            for (Map.Entry $$4 : $$12.entrySet()) {
                ResourceLocation $$5 = (ResourceLocation)$$4.getKey();
                ResourceLocation $$6 = LISTER.fileToId($$5);
                $$22.put($$6, CompletableFuture.supplyAsync(() -> {
                    List<String> $$3 = ServerFunctionLibrary.readLines((Resource)$$4.getValue());
                    return CommandFunction.fromLines($$6, this.dispatcher, $$3, $$3);
                }, $$2));
            }
            CompletableFuture[] $$7 = $$22.values().toArray(new CompletableFuture[0]);
            return CompletableFuture.allOf($$7).handle(($$1, $$2) -> $$22);
        });
        return ((CompletableFuture)((CompletableFuture)$$4.thenCombine($$5, Pair::of)).thenCompose($$02::wait)).thenAcceptAsync($$0 -> {
            Map $$12 = (Map)$$0.getSecond();
            ImmutableMap.Builder $$2 = ImmutableMap.builder();
            $$12.forEach(($$1, $$22) -> ((CompletableFuture)$$22.handle(($$2, $$3) -> {
                if ($$3 != null) {
                    LOGGER.error("Failed to load function {}", $$1, $$3);
                } else {
                    $$2.put($$1, $$2);
                }
                return null;
            })).join());
            this.functions = $$2.build();
            this.tags = this.tagsLoader.build((Map)$$0.getFirst());
        }, $$3);
    }

    private static List<String> readLines(Resource $$0) {
        List list;
        block8: {
            BufferedReader $$1 = $$0.openAsReader();
            try {
                list = $$1.lines().toList();
                if ($$1 == null) break block8;
            } catch (Throwable throwable) {
                try {
                    if ($$1 != null) {
                        try {
                            $$1.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (IOException $$2) {
                    throw new CompletionException($$2);
                }
            }
            $$1.close();
        }
        return list;
    }
}

