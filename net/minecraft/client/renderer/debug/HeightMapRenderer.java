/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector3f;

public class HeightMapRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int CHUNK_DIST = 2;
    private static final float BOX_HEIGHT = 0.09375f;

    public HeightMapRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        ClientLevel $$5 = this.minecraft.level;
        VertexConsumer $$6 = $$1.getBuffer(RenderType.debugFilledBox());
        BlockPos $$7 = BlockPos.containing($$2, 0.0, $$4);
        for (int $$8 = -2; $$8 <= 2; ++$$8) {
            for (int $$9 = -2; $$9 <= 2; ++$$9) {
                ChunkAccess $$10 = $$5.getChunk($$7.offset($$8 * 16, 0, $$9 * 16));
                for (Map.Entry<Heightmap.Types, Heightmap> $$11 : $$10.getHeightmaps()) {
                    Heightmap.Types $$12 = $$11.getKey();
                    ChunkPos $$13 = $$10.getPos();
                    Vector3f $$14 = this.getColor($$12);
                    for (int $$15 = 0; $$15 < 16; ++$$15) {
                        for (int $$16 = 0; $$16 < 16; ++$$16) {
                            int $$17 = SectionPos.sectionToBlockCoord($$13.x, $$15);
                            int $$18 = SectionPos.sectionToBlockCoord($$13.z, $$16);
                            float $$19 = (float)((double)((float)$$5.getHeight($$12, $$17, $$18) + (float)$$12.ordinal() * 0.09375f) - $$3);
                            ShapeRenderer.addChainedFilledBoxVertices($$0, $$6, (double)((float)$$17 + 0.25f) - $$2, (double)$$19, (double)((float)$$18 + 0.25f) - $$4, (double)((float)$$17 + 0.75f) - $$2, (double)($$19 + 0.09375f), (double)((float)$$18 + 0.75f) - $$4, $$14.x(), $$14.y(), $$14.z(), 1.0f);
                        }
                    }
                }
            }
        }
    }

    private Vector3f getColor(Heightmap.Types $$0) {
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case Heightmap.Types.WORLD_SURFACE_WG -> new Vector3f(1.0f, 1.0f, 0.0f);
            case Heightmap.Types.OCEAN_FLOOR_WG -> new Vector3f(1.0f, 0.0f, 1.0f);
            case Heightmap.Types.WORLD_SURFACE -> new Vector3f(0.0f, 0.7f, 0.0f);
            case Heightmap.Types.OCEAN_FLOOR -> new Vector3f(0.0f, 0.0f, 0.5f);
            case Heightmap.Types.MOTION_BLOCKING -> new Vector3f(0.0f, 0.3f, 0.3f);
            case Heightmap.Types.MOTION_BLOCKING_NO_LEAVES -> new Vector3f(0.0f, 0.5f, 0.5f);
        };
    }
}

