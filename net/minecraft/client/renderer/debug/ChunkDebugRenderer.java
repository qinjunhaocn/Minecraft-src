/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.invoke.CallSite;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    final Minecraft minecraft;
    private double lastUpdateTime = Double.MIN_VALUE;
    private final int radius = 12;
    @Nullable
    private ChunkData data;

    public ChunkDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        double $$5 = Util.getNanos();
        if ($$5 - this.lastUpdateTime > 3.0E9) {
            this.lastUpdateTime = $$5;
            IntegratedServer $$6 = this.minecraft.getSingleplayerServer();
            this.data = $$6 != null ? new ChunkData(this, $$6, $$2, $$4) : null;
        }
        if (this.data != null) {
            Map $$7 = this.data.serverData.getNow(null);
            double $$8 = this.minecraft.gameRenderer.getMainCamera().getPosition().y * 0.85;
            for (Map.Entry<ChunkPos, String> $$9 : this.data.clientData.entrySet()) {
                ChunkPos $$10 = $$9.getKey();
                Object $$11 = $$9.getValue();
                if ($$7 != null) {
                    $$11 = (String)$$11 + (String)$$7.get($$10);
                }
                String[] $$12 = ((String)$$11).split("\n");
                int $$13 = 0;
                for (String $$14 : $$12) {
                    DebugRenderer.renderFloatingText($$0, $$1, $$14, SectionPos.sectionToBlockCoord($$10.x, 8), $$8 + (double)$$13, SectionPos.sectionToBlockCoord($$10.z, 8), -1, 0.15f, true, 0.0f, true);
                    $$13 -= 2;
                }
            }
        }
    }

    final class ChunkData {
        final Map<ChunkPos, String> clientData;
        final CompletableFuture<Map<ChunkPos, String>> serverData;

        ChunkData(ChunkDebugRenderer chunkDebugRenderer, IntegratedServer $$0, double $$1, double $$2) {
            ClientLevel $$3 = chunkDebugRenderer.minecraft.level;
            ResourceKey<Level> $$4 = $$3.dimension();
            int $$5 = SectionPos.posToSectionCoord($$1);
            int $$6 = SectionPos.posToSectionCoord($$2);
            ImmutableMap.Builder<ChunkPos, Object> $$7 = ImmutableMap.builder();
            ClientChunkCache $$8 = $$3.getChunkSource();
            for (int $$9 = $$5 - 12; $$9 <= $$5 + 12; ++$$9) {
                for (int $$10 = $$6 - 12; $$10 <= $$6 + 12; ++$$10) {
                    ChunkPos $$11 = new ChunkPos($$9, $$10);
                    Object $$12 = "";
                    LevelChunk $$13 = $$8.getChunk($$9, $$10, false);
                    $$12 = (String)$$12 + "Client: ";
                    if ($$13 == null) {
                        $$12 = (String)$$12 + "0n/a\n";
                    } else {
                        $$12 = (String)$$12 + ($$13.isEmpty() ? " E" : "");
                        $$12 = (String)$$12 + "\n";
                    }
                    $$7.put($$11, $$12);
                }
            }
            this.clientData = $$7.build();
            this.serverData = $$0.submit(() -> {
                ServerLevel $$4 = $$0.getLevel($$4);
                if ($$4 == null) {
                    return ImmutableMap.of();
                }
                ImmutableMap.Builder<ChunkPos, CallSite> $$5 = ImmutableMap.builder();
                ServerChunkCache $$6 = $$4.getChunkSource();
                for (int $$7 = $$5 - 12; $$7 <= $$5 + 12; ++$$7) {
                    for (int $$8 = $$6 - 12; $$8 <= $$6 + 12; ++$$8) {
                        ChunkPos $$9 = new ChunkPos($$7, $$8);
                        $$5.put($$9, (CallSite)((Object)("Server: " + $$6.getChunkDebugData($$9))));
                    }
                }
                return $$5.build();
            });
        }
    }
}

