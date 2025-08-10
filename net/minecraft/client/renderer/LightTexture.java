/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class LightTexture
implements AutoCloseable {
    public static final int FULL_BRIGHT = 0xF000F0;
    public static final int FULL_SKY = 0xF00000;
    public static final int FULL_BLOCK = 240;
    private static final int TEXTURE_SIZE = 16;
    private static final int LIGHTMAP_UBO_SIZE = new Std140SizeCalculator().putFloat().putFloat().putFloat().putInt().putFloat().putFloat().putFloat().putFloat().putVec3().get();
    private final GpuTexture texture;
    private final GpuTextureView textureView;
    private boolean updateLightTexture;
    private float blockLightRedFlicker;
    private final GameRenderer renderer;
    private final Minecraft minecraft;
    private final MappableRingBuffer ubo;

    public LightTexture(GameRenderer $$0, Minecraft $$1) {
        this.renderer = $$0;
        this.minecraft = $$1;
        GpuDevice $$2 = RenderSystem.getDevice();
        this.texture = $$2.createTexture("Light Texture", 12, TextureFormat.RGBA8, 16, 16, 1, 1);
        this.texture.setTextureFilter(FilterMode.LINEAR, false);
        this.textureView = $$2.createTextureView(this.texture);
        $$2.createCommandEncoder().clearColorTexture(this.texture, -1);
        this.ubo = new MappableRingBuffer(() -> "Lightmap UBO", 130, LIGHTMAP_UBO_SIZE);
    }

    public GpuTextureView getTextureView() {
        return this.textureView;
    }

    @Override
    public void close() {
        this.texture.close();
        this.textureView.close();
        this.ubo.close();
    }

    public void tick() {
        this.blockLightRedFlicker += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
        this.blockLightRedFlicker *= 0.9f;
        this.updateLightTexture = true;
    }

    public void turnOffLightLayer() {
        RenderSystem.setShaderTexture(2, null);
    }

    public void turnOnLightLayer() {
        RenderSystem.setShaderTexture(2, this.textureView);
    }

    private float calculateDarknessScale(LivingEntity $$0, float $$1, float $$2) {
        float $$3 = 0.45f * $$1;
        return Math.max(0.0f, Mth.cos(((float)$$0.tickCount - $$2) * (float)Math.PI * 0.025f) * $$3);
    }

    public void updateLightTexture(float $$0) {
        float $$12;
        float $$5;
        if (!this.updateLightTexture) {
            return;
        }
        this.updateLightTexture = false;
        ProfilerFiller $$1 = Profiler.get();
        $$1.push("lightTex");
        ClientLevel $$2 = this.minecraft.level;
        if ($$2 == null) {
            return;
        }
        float $$3 = $$2.getSkyDarken(1.0f);
        if ($$2.getSkyFlashTime() > 0) {
            float $$4 = 1.0f;
        } else {
            $$5 = $$3 * 0.95f + 0.05f;
        }
        float $$6 = this.minecraft.options.darknessEffectScale().get().floatValue();
        float $$7 = this.minecraft.player.getEffectBlendFactor(MobEffects.DARKNESS, $$0) * $$6;
        float $$8 = this.calculateDarknessScale(this.minecraft.player, $$7, $$0) * $$6;
        float $$9 = this.minecraft.player.getWaterVision();
        if (this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION)) {
            float $$10 = GameRenderer.getNightVisionScale(this.minecraft.player, $$0);
        } else if ($$9 > 0.0f && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER)) {
            float $$11 = $$9;
        } else {
            $$12 = 0.0f;
        }
        Vector3f $$13 = new Vector3f($$3, $$3, 1.0f).lerp((Vector3fc)new Vector3f(1.0f, 1.0f, 1.0f), 0.35f);
        float $$14 = this.blockLightRedFlicker + 1.5f;
        float $$15 = $$2.dimensionType().ambientLight();
        boolean $$16 = $$2.effects().forceBrightLightmap();
        float $$17 = this.minecraft.options.gamma().get().floatValue();
        RenderSystem.AutoStorageIndexBuffer $$18 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer $$19 = $$18.getBuffer(6);
        CommandEncoder $$20 = RenderSystem.getDevice().createCommandEncoder();
        try (GpuBuffer.MappedView $$21 = $$20.mapBuffer(this.ubo.currentBuffer(), false, true);){
            Std140Builder.intoBuffer($$21.data()).putFloat($$15).putFloat($$5).putFloat($$14).putInt($$16 ? 1 : 0).putFloat($$12).putFloat($$8).putFloat(this.renderer.getDarkenWorldAmount($$0)).putFloat(Math.max(0.0f, $$17 - $$7)).putVec3((Vector3fc)$$13);
        }
        try (RenderPass $$22 = $$20.createRenderPass(() -> "Update light", this.textureView, OptionalInt.empty());){
            $$22.setPipeline(RenderPipelines.LIGHTMAP);
            RenderSystem.bindDefaultUniforms($$22);
            $$22.setUniform("LightmapInfo", this.ubo.currentBuffer());
            $$22.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
            $$22.setIndexBuffer($$19, $$18.type());
            $$22.drawIndexed(0, 0, 6, 1);
        }
        this.ubo.rotate();
        $$1.pop();
    }

    public static float getBrightness(DimensionType $$0, int $$1) {
        return LightTexture.getBrightness($$0.ambientLight(), $$1);
    }

    public static float getBrightness(float $$0, int $$1) {
        float $$2 = (float)$$1 / 15.0f;
        float $$3 = $$2 / (4.0f - 3.0f * $$2);
        return Mth.lerp($$0, $$3, 1.0f);
    }

    public static int pack(int $$0, int $$1) {
        return $$0 << 4 | $$1 << 20;
    }

    public static int block(int $$0) {
        return $$0 >>> 4 & 0xF;
    }

    public static int sky(int $$0) {
        return $$0 >>> 20 & 0xF;
    }

    public static int lightCoordsWithEmission(int $$0, int $$1) {
        if ($$1 == 0) {
            return $$0;
        }
        int $$2 = Math.max(LightTexture.sky($$0), $$1);
        int $$3 = Math.max(LightTexture.block($$0), $$1);
        return LightTexture.pack($$3, $$2);
    }
}

