/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.resources.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.BlockStateDefinitions;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.slf4j.Logger;

public class BlockStateModelLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter BLOCKSTATE_LISTER = FileToIdConverter.json("blockstates");

    public static CompletableFuture<LoadedModels> loadBlockStates(ResourceManager $$0, Executor $$1) {
        Function<ResourceLocation, StateDefinition<Block, BlockState>> $$22 = BlockStateDefinitions.definitionLocationToBlockStateMapper();
        return CompletableFuture.supplyAsync(() -> BLOCKSTATE_LISTER.listMatchingResourceStacks($$0), $$1).thenCompose($$2 -> {
            ArrayList<CompletableFuture<LoadedModels>> $$3 = new ArrayList<CompletableFuture<LoadedModels>>($$2.size());
            for (Map.Entry $$4 : $$2.entrySet()) {
                $$3.add(CompletableFuture.supplyAsync(() -> {
                    Object $$2 = BLOCKSTATE_LISTER.fileToId((ResourceLocation)$$4.getKey());
                    StateDefinition $$3 = (StateDefinition)$$22.apply((ResourceLocation)$$2);
                    if ($$3 == null) {
                        LOGGER.debug("Discovered unknown block state definition {}, ignoring", $$2);
                        return null;
                    }
                    List $$4 = (List)$$4.getValue();
                    ArrayList<LoadedBlockModelDefinition> $$5 = new ArrayList<LoadedBlockModelDefinition>($$4.size());
                    for (Resource $$6 : $$4) {
                        try {
                            BufferedReader $$7 = $$6.openAsReader();
                            try {
                                JsonElement $$8 = StrictJsonParser.parse($$7);
                                BlockModelDefinition $$9 = (BlockModelDefinition)((Object)((Object)((Object)BlockModelDefinition.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)$$8).getOrThrow(JsonParseException::new))));
                                $$5.add(new LoadedBlockModelDefinition($$6.sourcePackId(), $$9));
                            } finally {
                                if ($$7 == null) continue;
                                ((Reader)$$7).close();
                            }
                        } catch (Exception $$10) {
                            LOGGER.error("Failed to load blockstate definition {} from pack {}", $$2, $$6.sourcePackId(), $$10);
                        }
                    }
                    try {
                        return BlockStateModelLoader.loadBlockStateDefinitionStack($$2, $$3, $$5);
                    } catch (Exception $$11) {
                        LOGGER.error("Failed to load blockstate definition {}", $$2, (Object)$$11);
                        return null;
                    }
                }, $$1));
            }
            return Util.sequence($$3).thenApply($$0 -> {
                Executor $$1 = new IdentityHashMap<BlockState, BlockStateModel.UnbakedRoot>();
                for (LoadedModels $$2 : $$0) {
                    if ($$2 == null) continue;
                    $$1.putAll($$2.models());
                }
                return new LoadedModels((Map<BlockState, BlockStateModel.UnbakedRoot>)((Object)$$1));
            });
        });
    }

    private static LoadedModels loadBlockStateDefinitionStack(ResourceLocation $$0, StateDefinition<Block, BlockState> $$1, List<LoadedBlockModelDefinition> $$2) {
        IdentityHashMap<BlockState, BlockStateModel.UnbakedRoot> $$3 = new IdentityHashMap<BlockState, BlockStateModel.UnbakedRoot>();
        for (LoadedBlockModelDefinition $$4 : $$2) {
            $$3.putAll($$4.contents.instantiate($$1, () -> String.valueOf($$0) + "/" + $$1.source));
        }
        return new LoadedModels($$3);
    }

    static final class LoadedBlockModelDefinition
    extends Record {
        final String source;
        final BlockModelDefinition contents;

        LoadedBlockModelDefinition(String $$0, BlockModelDefinition $$1) {
            this.source = $$0;
            this.contents = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LoadedBlockModelDefinition.class, "source;contents", "source", "contents"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LoadedBlockModelDefinition.class, "source;contents", "source", "contents"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LoadedBlockModelDefinition.class, "source;contents", "source", "contents"}, this, $$0);
        }

        public String source() {
            return this.source;
        }

        public BlockModelDefinition contents() {
            return this.contents;
        }
    }

    public record LoadedModels(Map<BlockState, BlockStateModel.UnbakedRoot> models) {
    }
}

