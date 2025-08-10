/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WeatherEffectRenderer {
    private static final int RAIN_RADIUS = 10;
    private static final int RAIN_DIAMETER = 21;
    private static final ResourceLocation RAIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/rain.png");
    private static final ResourceLocation SNOW_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/snow.png");
    private static final int RAIN_TABLE_SIZE = 32;
    private static final int HALF_RAIN_TABLE_SIZE = 16;
    private int rainSoundTime;
    private final float[] columnSizeX = new float[1024];
    private final float[] columnSizeZ = new float[1024];

    public WeatherEffectRenderer() {
        for (int $$0 = 0; $$0 < 32; ++$$0) {
            for (int $$1 = 0; $$1 < 32; ++$$1) {
                float $$2 = $$1 - 16;
                float $$3 = $$0 - 16;
                float $$4 = Mth.length($$2, $$3);
                this.columnSizeX[$$0 * 32 + $$1] = -$$3 / $$4;
                this.columnSizeZ[$$0 * 32 + $$1] = $$2 / $$4;
            }
        }
    }

    public void render(Level $$0, MultiBufferSource $$1, int $$2, float $$3, Vec3 $$4) {
        float $$5 = $$0.getRainLevel($$3);
        if ($$5 <= 0.0f) {
            return;
        }
        int $$6 = Minecraft.useFancyGraphics() ? 10 : 5;
        ArrayList<ColumnInstance> $$7 = new ArrayList<ColumnInstance>();
        ArrayList<ColumnInstance> $$8 = new ArrayList<ColumnInstance>();
        this.collectColumnInstances($$0, $$2, $$3, $$4, $$6, $$7, $$8);
        if (!$$7.isEmpty() || !$$8.isEmpty()) {
            this.render($$1, $$4, $$6, $$5, $$7, $$8);
        }
    }

    private void collectColumnInstances(Level $$0, int $$1, float $$2, Vec3 $$3, int $$4, List<ColumnInstance> $$5, List<ColumnInstance> $$6) {
        int $$7 = Mth.floor($$3.x);
        int $$8 = Mth.floor($$3.y);
        int $$9 = Mth.floor($$3.z);
        BlockPos.MutableBlockPos $$10 = new BlockPos.MutableBlockPos();
        RandomSource $$11 = RandomSource.create();
        for (int $$12 = $$9 - $$4; $$12 <= $$9 + $$4; ++$$12) {
            for (int $$13 = $$7 - $$4; $$13 <= $$7 + $$4; ++$$13) {
                Biome.Precipitation $$17;
                int $$14 = $$0.getHeight(Heightmap.Types.MOTION_BLOCKING, $$13, $$12);
                int $$15 = Math.max($$8 - $$4, $$14);
                int $$16 = Math.max($$8 + $$4, $$14);
                if ($$16 - $$15 == 0 || ($$17 = this.getPrecipitationAt($$0, $$10.set($$13, $$8, $$12))) == Biome.Precipitation.NONE) continue;
                int $$18 = $$13 * $$13 * 3121 + $$13 * 45238971 ^ $$12 * $$12 * 418711 + $$12 * 13761;
                $$11.setSeed($$18);
                int $$19 = Math.max($$8, $$14);
                int $$20 = LevelRenderer.getLightColor($$0, $$10.set($$13, $$19, $$12));
                if ($$17 == Biome.Precipitation.RAIN) {
                    $$5.add(this.createRainColumnInstance($$11, $$1, $$13, $$15, $$16, $$12, $$20, $$2));
                    continue;
                }
                if ($$17 != Biome.Precipitation.SNOW) continue;
                $$6.add(this.createSnowColumnInstance($$11, $$1, $$13, $$15, $$16, $$12, $$20, $$2));
            }
        }
    }

    private void render(MultiBufferSource $$0, Vec3 $$1, int $$2, float $$3, List<ColumnInstance> $$4, List<ColumnInstance> $$5) {
        if (!$$4.isEmpty()) {
            RenderType $$6 = RenderType.weather(RAIN_LOCATION, Minecraft.useShaderTransparency());
            this.renderInstances($$0.getBuffer($$6), $$4, $$1, 1.0f, $$2, $$3);
        }
        if (!$$5.isEmpty()) {
            RenderType $$7 = RenderType.weather(SNOW_LOCATION, Minecraft.useShaderTransparency());
            this.renderInstances($$0.getBuffer($$7), $$5, $$1, 0.8f, $$2, $$3);
        }
    }

    private ColumnInstance createRainColumnInstance(RandomSource $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, float $$7) {
        int $$8 = $$1 & 0x1FFFF;
        int $$9 = $$2 * $$2 * 3121 + $$2 * 45238971 + $$5 * $$5 * 418711 + $$5 * 13761 & 0xFF;
        float $$10 = 3.0f + $$0.nextFloat();
        float $$11 = -((float)($$8 + $$9) + $$7) / 32.0f * $$10;
        float $$12 = $$11 % 32.0f;
        return new ColumnInstance($$2, $$5, $$3, $$4, 0.0f, $$12, $$6);
    }

    private ColumnInstance createSnowColumnInstance(RandomSource $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, float $$7) {
        float $$8 = (float)$$1 + $$7;
        float $$9 = (float)($$0.nextDouble() + (double)($$8 * 0.01f * (float)$$0.nextGaussian()));
        float $$10 = (float)($$0.nextDouble() + (double)($$8 * (float)$$0.nextGaussian() * 0.001f));
        float $$11 = -((float)($$1 & 0x1FF) + $$7) / 512.0f;
        int $$12 = LightTexture.pack((LightTexture.block($$6) * 3 + 15) / 4, (LightTexture.sky($$6) * 3 + 15) / 4);
        return new ColumnInstance($$2, $$5, $$3, $$4, $$9, $$11 + $$10, $$12);
    }

    private void renderInstances(VertexConsumer $$0, List<ColumnInstance> $$1, Vec3 $$2, float $$3, int $$4, float $$5) {
        for (ColumnInstance $$6 : $$1) {
            float $$7 = (float)((double)$$6.x + 0.5 - $$2.x);
            float $$8 = (float)((double)$$6.z + 0.5 - $$2.z);
            float $$9 = (float)Mth.lengthSquared($$7, $$8);
            float $$10 = Mth.lerp($$9 / (float)($$4 * $$4), $$3, 0.5f) * $$5;
            int $$11 = ARGB.white($$10);
            int $$12 = ($$6.z - Mth.floor($$2.z) + 16) * 32 + $$6.x - Mth.floor($$2.x) + 16;
            float $$13 = this.columnSizeX[$$12] / 2.0f;
            float $$14 = this.columnSizeZ[$$12] / 2.0f;
            float $$15 = $$7 - $$13;
            float $$16 = $$7 + $$13;
            float $$17 = (float)((double)$$6.topY - $$2.y);
            float $$18 = (float)((double)$$6.bottomY - $$2.y);
            float $$19 = $$8 - $$14;
            float $$20 = $$8 + $$14;
            float $$21 = $$6.uOffset + 0.0f;
            float $$22 = $$6.uOffset + 1.0f;
            float $$23 = (float)$$6.bottomY * 0.25f + $$6.vOffset;
            float $$24 = (float)$$6.topY * 0.25f + $$6.vOffset;
            $$0.addVertex($$15, $$17, $$19).setUv($$21, $$23).setColor($$11).setLight($$6.lightCoords);
            $$0.addVertex($$16, $$17, $$20).setUv($$22, $$23).setColor($$11).setLight($$6.lightCoords);
            $$0.addVertex($$16, $$18, $$20).setUv($$22, $$24).setColor($$11).setLight($$6.lightCoords);
            $$0.addVertex($$15, $$18, $$19).setUv($$21, $$24).setColor($$11).setLight($$6.lightCoords);
        }
    }

    public void tickRainParticles(ClientLevel $$0, Camera $$1, int $$2, ParticleStatus $$3) {
        float $$4 = $$0.getRainLevel(1.0f) / (Minecraft.useFancyGraphics() ? 1.0f : 2.0f);
        if ($$4 <= 0.0f) {
            return;
        }
        RandomSource $$5 = RandomSource.create((long)$$2 * 312987231L);
        BlockPos $$6 = BlockPos.containing($$1.getPosition());
        Vec3i $$7 = null;
        int $$8 = (int)(100.0f * $$4 * $$4) / ($$3 == ParticleStatus.DECREASED ? 2 : 1);
        for (int $$9 = 0; $$9 < $$8; ++$$9) {
            int $$11;
            int $$10 = $$5.nextInt(21) - 10;
            BlockPos $$12 = $$0.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, $$6.offset($$10, 0, $$11 = $$5.nextInt(21) - 10));
            if ($$12.getY() <= $$0.getMinY() || $$12.getY() > $$6.getY() + 10 || $$12.getY() < $$6.getY() - 10 || this.getPrecipitationAt($$0, $$12) != Biome.Precipitation.RAIN) continue;
            $$7 = $$12.below();
            if ($$3 == ParticleStatus.MINIMAL) break;
            double $$13 = $$5.nextDouble();
            double $$14 = $$5.nextDouble();
            BlockState $$15 = $$0.getBlockState((BlockPos)$$7);
            FluidState $$16 = $$0.getFluidState((BlockPos)$$7);
            VoxelShape $$17 = $$15.getCollisionShape($$0, (BlockPos)$$7);
            double $$18 = $$17.max(Direction.Axis.Y, $$13, $$14);
            double $$19 = $$16.getHeight($$0, (BlockPos)$$7);
            double $$20 = Math.max($$18, $$19);
            SimpleParticleType $$21 = $$16.is(FluidTags.LAVA) || $$15.is(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire($$15) ? ParticleTypes.SMOKE : ParticleTypes.RAIN;
            $$0.addParticle($$21, (double)$$7.getX() + $$13, (double)$$7.getY() + $$20, (double)$$7.getZ() + $$14, 0.0, 0.0, 0.0);
        }
        if ($$7 != null && $$5.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if ($$7.getY() > $$6.getY() + 1 && $$0.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, $$6).getY() > Mth.floor($$6.getY())) {
                $$0.playLocalSound((BlockPos)$$7, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1f, 0.5f, false);
            } else {
                $$0.playLocalSound((BlockPos)$$7, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2f, 1.0f, false);
            }
        }
    }

    private Biome.Precipitation getPrecipitationAt(Level $$0, BlockPos $$1) {
        if (!$$0.getChunkSource().hasChunk(SectionPos.blockToSectionCoord($$1.getX()), SectionPos.blockToSectionCoord($$1.getZ()))) {
            return Biome.Precipitation.NONE;
        }
        Biome $$2 = $$0.getBiome($$1).value();
        return $$2.getPrecipitationAt($$1, $$0.getSeaLevel());
    }

    static final class ColumnInstance
    extends Record {
        final int x;
        final int z;
        final int bottomY;
        final int topY;
        final float uOffset;
        final float vOffset;
        final int lightCoords;

        ColumnInstance(int $$0, int $$1, int $$2, int $$3, float $$4, float $$5, int $$6) {
            this.x = $$0;
            this.z = $$1;
            this.bottomY = $$2;
            this.topY = $$3;
            this.uOffset = $$4;
            this.vOffset = $$5;
            this.lightCoords = $$6;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ColumnInstance.class, "x;z;bottomY;topY;uOffset;vOffset;lightCoords", "x", "z", "bottomY", "topY", "uOffset", "vOffset", "lightCoords"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ColumnInstance.class, "x;z;bottomY;topY;uOffset;vOffset;lightCoords", "x", "z", "bottomY", "topY", "uOffset", "vOffset", "lightCoords"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ColumnInstance.class, "x;z;bottomY;topY;uOffset;vOffset;lightCoords", "x", "z", "bottomY", "topY", "uOffset", "vOffset", "lightCoords"}, this, $$0);
        }

        public int x() {
            return this.x;
        }

        public int z() {
            return this.z;
        }

        public int bottomY() {
            return this.bottomY;
        }

        public int topY() {
            return this.topY;
        }

        public float uOffset() {
            return this.uOffset;
        }

        public float vOffset() {
            return this.vOffset;
        }

        public int lightCoords() {
            return this.lightCoords;
        }
    }
}

