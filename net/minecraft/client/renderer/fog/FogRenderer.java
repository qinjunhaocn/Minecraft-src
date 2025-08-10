/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.renderer.fog;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import net.minecraft.client.renderer.fog.environment.BlindnessFogEnvironment;
import net.minecraft.client.renderer.fog.environment.DarknessFogEnvironment;
import net.minecraft.client.renderer.fog.environment.DimensionOrBossFogEnvironment;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.client.renderer.fog.environment.LavaFogEnvironment;
import net.minecraft.client.renderer.fog.environment.PowderedSnowFogEnvironment;
import net.minecraft.client.renderer.fog.environment.WaterFogEnvironment;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryStack;

public class FogRenderer
implements AutoCloseable {
    public static final int FOG_UBO_SIZE = new Std140SizeCalculator().putVec4().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().get();
    private static final List<FogEnvironment> FOG_ENVIRONMENTS = Lists.newArrayList(new LavaFogEnvironment(), new PowderedSnowFogEnvironment(), new BlindnessFogEnvironment(), new DarknessFogEnvironment(), new WaterFogEnvironment(), new DimensionOrBossFogEnvironment(), new AtmosphericFogEnvironment());
    private static boolean fogEnabled = true;
    private final GpuBuffer emptyBuffer;
    private final MappableRingBuffer regularBuffer;

    public FogRenderer() {
        GpuDevice $$0 = RenderSystem.getDevice();
        this.regularBuffer = new MappableRingBuffer(() -> "Fog UBO", 130, FOG_UBO_SIZE);
        try (MemoryStack $$1 = MemoryStack.stackPush();){
            ByteBuffer $$2 = $$1.malloc(FOG_UBO_SIZE);
            this.updateBuffer($$2, 0, new Vector4f(0.0f), Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
            this.emptyBuffer = $$0.createBuffer(() -> "Empty fog", 128, (ByteBuffer)$$2.flip());
        }
        RenderSystem.setShaderFog(this.getBuffer(FogMode.NONE));
    }

    @Override
    public void close() {
        this.emptyBuffer.close();
        this.regularBuffer.close();
    }

    public void endFrame() {
        this.regularBuffer.rotate();
    }

    public GpuBufferSlice getBuffer(FogMode $$0) {
        if (!fogEnabled) {
            return this.emptyBuffer.slice(0, FOG_UBO_SIZE);
        }
        return switch ($$0.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.emptyBuffer.slice(0, FOG_UBO_SIZE);
            case 1 -> this.regularBuffer.currentBuffer().slice(0, FOG_UBO_SIZE);
        };
    }

    private Vector4f computeFogColor(Camera $$0, float $$1, ClientLevel $$2, int $$3, float $$4, boolean $$5) {
        float $$23;
        LivingEntity $$21;
        FogType $$6 = this.getFogType($$0, $$5);
        Entity $$7 = $$0.getEntity();
        FogEnvironment $$8 = null;
        FogEnvironment $$9 = null;
        for (FogEnvironment $$10 : FOG_ENVIRONMENTS) {
            if ($$10.isApplicable($$6, $$7)) {
                if ($$8 == null && $$10.providesColor()) {
                    $$8 = $$10;
                }
                if ($$9 != null || !$$10.modifiesDarkness()) continue;
                $$9 = $$10;
                continue;
            }
            $$10.onNotApplicable();
        }
        if ($$8 == null) {
            throw new IllegalStateException("No color source environment found");
        }
        int $$11 = $$8.getBaseColor($$2, $$0, $$3, $$4);
        float $$12 = $$2.getLevelData().voidDarknessOnsetRange();
        float $$13 = Mth.clamp(($$12 + (float)$$2.getMinY() - (float)$$0.getPosition().y) / $$12, 0.0f, 1.0f);
        if ($$9 != null) {
            LivingEntity $$14 = (LivingEntity)$$7;
            $$13 = $$9.getModifiedDarkness($$14, $$13, $$1);
        }
        float $$15 = ARGB.redFloat($$11);
        float $$16 = ARGB.greenFloat($$11);
        float $$17 = ARGB.blueFloat($$11);
        if ($$13 > 0.0f && $$6 != FogType.LAVA && $$6 != FogType.POWDER_SNOW) {
            float $$18 = Mth.square(1.0f - $$13);
            $$15 *= $$18;
            $$16 *= $$18;
            $$17 *= $$18;
        }
        if ($$4 > 0.0f) {
            $$15 = Mth.lerp($$4, $$15, $$15 * 0.7f);
            $$16 = Mth.lerp($$4, $$16, $$16 * 0.6f);
            $$17 = Mth.lerp($$4, $$17, $$17 * 0.6f);
        }
        if ($$6 == FogType.WATER) {
            if ($$7 instanceof LocalPlayer) {
                float $$19 = ((LocalPlayer)$$7).getWaterVision();
            } else {
                float $$20 = 1.0f;
            }
        } else if ($$7 instanceof LivingEntity && ($$21 = (LivingEntity)$$7).hasEffect(MobEffects.NIGHT_VISION) && !$$21.hasEffect(MobEffects.DARKNESS)) {
            float $$22 = GameRenderer.getNightVisionScale($$21, $$1);
        } else {
            $$23 = 0.0f;
        }
        if ($$15 != 0.0f && $$16 != 0.0f && $$17 != 0.0f) {
            float $$24 = 1.0f / Math.max($$15, Math.max($$16, $$17));
            $$15 = Mth.lerp($$23, $$15, $$15 * $$24);
            $$16 = Mth.lerp($$23, $$16, $$16 * $$24);
            $$17 = Mth.lerp($$23, $$17, $$17 * $$24);
        }
        return new Vector4f($$15, $$16, $$17, 1.0f);
    }

    public static boolean toggleFog() {
        fogEnabled = !fogEnabled;
        return fogEnabled;
    }

    public Vector4f setupFog(Camera $$0, int $$1, boolean $$2, DeltaTracker $$3, float $$4, ClientLevel $$5) {
        float $$6 = $$3.getGameTimeDeltaPartialTick(false);
        Vector4f $$7 = this.computeFogColor($$0, $$6, $$5, $$1, $$4, $$2);
        float $$8 = $$1 * 16;
        FogType $$9 = this.getFogType($$0, $$2);
        Entity $$10 = $$0.getEntity();
        FogData $$11 = new FogData();
        for (FogEnvironment $$12 : FOG_ENVIRONMENTS) {
            if (!$$12.isApplicable($$9, $$10)) continue;
            $$12.setupFog($$11, $$10, $$0.getBlockPosition(), $$5, $$8, $$3);
            break;
        }
        float $$13 = Mth.clamp($$8 / 10.0f, 4.0f, 64.0f);
        $$11.renderDistanceStart = $$8 - $$13;
        $$11.renderDistanceEnd = $$8;
        try (GpuBuffer.MappedView $$14 = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.regularBuffer.currentBuffer(), false, true);){
            this.updateBuffer($$14.data(), 0, $$7, $$11.environmentalStart, $$11.environmentalEnd, $$11.renderDistanceStart, $$11.renderDistanceEnd, $$11.skyEnd, $$11.cloudEnd);
        }
        return $$7;
    }

    private FogType getFogType(Camera $$0, boolean $$1) {
        FogType $$2 = $$0.getFluidInCamera();
        if ($$2 == FogType.NONE) {
            if ($$1) {
                return FogType.DIMENSION_OR_BOSS;
            }
            return FogType.ATMOSPHERIC;
        }
        return $$2;
    }

    private void updateBuffer(ByteBuffer $$0, int $$1, Vector4f $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8) {
        $$0.position($$1);
        Std140Builder.intoBuffer($$0).putVec4((Vector4fc)$$2).putFloat($$3).putFloat($$4).putFloat($$5).putFloat($$6).putFloat($$7).putFloat($$8);
    }

    public static final class FogMode
    extends Enum<FogMode> {
        public static final /* enum */ FogMode NONE = new FogMode();
        public static final /* enum */ FogMode WORLD = new FogMode();
        private static final /* synthetic */ FogMode[] $VALUES;

        public static FogMode[] values() {
            return (FogMode[])$VALUES.clone();
        }

        public static FogMode valueOf(String $$0) {
            return Enum.valueOf(FogMode.class, $$0);
        }

        private static /* synthetic */ FogMode[] a() {
            return new FogMode[]{NONE, WORLD};
        }

        static {
            $VALUES = FogMode.a();
        }
    }
}

