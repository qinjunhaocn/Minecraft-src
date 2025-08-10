/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.io.IOUtils
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.GuiBannerResultRenderer;
import net.minecraft.client.gui.render.pip.GuiBookModelRenderer;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.gui.render.pip.GuiProfilerChartRenderer;
import net.minecraft.client.gui.render.pip.GuiSignRenderer;
import net.minecraft.client.gui.render.pip.GuiSkinRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.CachedPerspectiveProjectionMatrixBuffer;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.GlobalSettingsUniform;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.PerspectiveProjectionMatrixBuffer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.slf4j.Logger;

public class GameRenderer
implements TrackedWaypoint.Projector,
AutoCloseable {
    private static final ResourceLocation BLUR_POST_CHAIN_ID = ResourceLocation.withDefaultNamespace("blur");
    public static final int MAX_BLUR_RADIUS = 10;
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final float PROJECTION_Z_NEAR = 0.05f;
    public static final float PROJECTION_3D_HUD_Z_FAR = 100.0f;
    private static final float PORTAL_SPINNING_SPEED = 20.0f;
    private static final float NAUSEA_SPINNING_SPEED = 7.0f;
    private final Minecraft minecraft;
    private final RandomSource random = RandomSource.create();
    private float renderDistance;
    public final ItemInHandRenderer itemInHandRenderer;
    private final ScreenEffectRenderer screenEffectRenderer;
    private final RenderBuffers renderBuffers;
    private float spinningEffectTime;
    private float spinningEffectSpeed;
    private float fovModifier;
    private float oldFovModifier;
    private float darkenWorldAmount;
    private float darkenWorldAmountO;
    private boolean renderBlockOutline = true;
    private long lastScreenshotAttempt;
    private boolean hasWorldScreenshot;
    private long lastActiveTime = Util.getMillis();
    private final LightTexture lightTexture;
    private final OverlayTexture overlayTexture = new OverlayTexture();
    private boolean panoramicMode;
    protected final CubeMap cubeMap = new CubeMap(ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama"));
    protected final PanoramaRenderer panorama = new PanoramaRenderer(this.cubeMap);
    private final CrossFrameResourcePool resourcePool = new CrossFrameResourcePool(3);
    private final FogRenderer fogRenderer = new FogRenderer();
    private final GuiRenderer guiRenderer;
    private final GuiRenderState guiRenderState;
    @Nullable
    private ResourceLocation postEffectId;
    private boolean effectActive;
    private final Camera mainCamera = new Camera();
    private final Lighting lighting = new Lighting();
    private final GlobalSettingsUniform globalSettingsUniform = new GlobalSettingsUniform();
    private final PerspectiveProjectionMatrixBuffer levelProjectionMatrixBuffer = new PerspectiveProjectionMatrixBuffer("level");
    private final CachedPerspectiveProjectionMatrixBuffer hud3dProjectionMatrixBuffer = new CachedPerspectiveProjectionMatrixBuffer("3d hud", 0.05f, 100.0f);

    public GameRenderer(Minecraft $$0, ItemInHandRenderer $$1, RenderBuffers $$2) {
        this.minecraft = $$0;
        this.itemInHandRenderer = $$1;
        this.lightTexture = new LightTexture(this, $$0);
        this.renderBuffers = $$2;
        this.guiRenderState = new GuiRenderState();
        MultiBufferSource.BufferSource $$3 = $$2.bufferSource();
        this.guiRenderer = new GuiRenderer(this.guiRenderState, $$3, List.of((Object)new GuiEntityRenderer($$3, $$0.getEntityRenderDispatcher()), (Object)new GuiSkinRenderer($$3), (Object)new GuiBookModelRenderer($$3), (Object)new GuiBannerResultRenderer($$3), (Object)new GuiSignRenderer($$3), (Object)new GuiProfilerChartRenderer($$3)));
        this.screenEffectRenderer = new ScreenEffectRenderer($$0, $$3);
    }

    @Override
    public void close() {
        this.globalSettingsUniform.close();
        this.lightTexture.close();
        this.overlayTexture.close();
        this.resourcePool.close();
        this.guiRenderer.close();
        this.levelProjectionMatrixBuffer.close();
        this.hud3dProjectionMatrixBuffer.close();
        this.lighting.close();
        this.cubeMap.close();
        this.fogRenderer.close();
    }

    public void setRenderBlockOutline(boolean $$0) {
        this.renderBlockOutline = $$0;
    }

    public void setPanoramicMode(boolean $$0) {
        this.panoramicMode = $$0;
    }

    public boolean isPanoramicMode() {
        return this.panoramicMode;
    }

    public void clearPostEffect() {
        this.postEffectId = null;
    }

    public void togglePostEffect() {
        this.effectActive = !this.effectActive;
    }

    public void checkEntityPostEffect(@Nullable Entity $$0) {
        this.postEffectId = null;
        if ($$0 instanceof Creeper) {
            this.setPostEffect(ResourceLocation.withDefaultNamespace("creeper"));
        } else if ($$0 instanceof Spider) {
            this.setPostEffect(ResourceLocation.withDefaultNamespace("spider"));
        } else if ($$0 instanceof EnderMan) {
            this.setPostEffect(ResourceLocation.withDefaultNamespace("invert"));
        }
    }

    private void setPostEffect(ResourceLocation $$0) {
        this.postEffectId = $$0;
        this.effectActive = true;
    }

    public void processBlurEffect() {
        PostChain $$0 = this.minecraft.getShaderManager().getPostChain(BLUR_POST_CHAIN_ID, LevelTargetBundle.MAIN_TARGETS);
        if ($$0 != null) {
            $$0.process(this.minecraft.getMainRenderTarget(), this.resourcePool);
        }
    }

    public void preloadUiShader(ResourceProvider $$0) {
        GpuDevice $$12 = RenderSystem.getDevice();
        BiFunction<ResourceLocation, ShaderType, String> $$22 = ($$1, $$2) -> {
            String string;
            block8: {
                ResourceLocation $$3 = $$2.idConverter().idToFile((ResourceLocation)$$1);
                BufferedReader $$4 = $$0.getResourceOrThrow($$3).openAsReader();
                try {
                    string = IOUtils.toString((Reader)$$4);
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
                    } catch (IOException $$5) {
                        LOGGER.error("Coudln't preload {} shader {}: {}", $$2, $$1, $$5);
                        return null;
                    }
                }
                ((Reader)$$4).close();
            }
            return string;
        };
        $$12.precompilePipeline(RenderPipelines.GUI, $$22);
        $$12.precompilePipeline(RenderPipelines.GUI_TEXTURED, $$22);
        if (TracyClient.isAvailable()) {
            $$12.precompilePipeline(RenderPipelines.TRACY_BLIT, $$22);
        }
    }

    public void tick() {
        this.tickFov();
        this.lightTexture.tick();
        LocalPlayer $$0 = this.minecraft.player;
        if (this.minecraft.getCameraEntity() == null) {
            this.minecraft.setCameraEntity($$0);
        }
        this.mainCamera.tick();
        this.itemInHandRenderer.tick();
        float $$1 = $$0.portalEffectIntensity;
        float $$2 = $$0.getEffectBlendFactor(MobEffects.NAUSEA, 1.0f);
        if ($$1 > 0.0f || $$2 > 0.0f) {
            this.spinningEffectSpeed = ($$1 * 20.0f + $$2 * 7.0f) / ($$1 + $$2);
            this.spinningEffectTime += this.spinningEffectSpeed;
        } else {
            this.spinningEffectSpeed = 0.0f;
        }
        if (!this.minecraft.level.tickRateManager().runsNormally()) {
            return;
        }
        this.minecraft.levelRenderer.tickParticles(this.mainCamera);
        this.darkenWorldAmountO = this.darkenWorldAmount;
        if (this.minecraft.gui.getBossOverlay().shouldDarkenScreen()) {
            this.darkenWorldAmount += 0.05f;
            if (this.darkenWorldAmount > 1.0f) {
                this.darkenWorldAmount = 1.0f;
            }
        } else if (this.darkenWorldAmount > 0.0f) {
            this.darkenWorldAmount -= 0.0125f;
        }
        this.screenEffectRenderer.tick();
    }

    @Nullable
    public ResourceLocation currentPostEffect() {
        return this.postEffectId;
    }

    public void resize(int $$0, int $$1) {
        this.resourcePool.clear();
        this.minecraft.levelRenderer.resize($$0, $$1);
    }

    public void pick(float $$0) {
        Entity entity;
        HitResult $$4;
        Entity $$1 = this.minecraft.getCameraEntity();
        if ($$1 == null) {
            return;
        }
        if (this.minecraft.level == null || this.minecraft.player == null) {
            return;
        }
        Profiler.get().push("pick");
        double $$2 = this.minecraft.player.blockInteractionRange();
        double $$3 = this.minecraft.player.entityInteractionRange();
        this.minecraft.hitResult = $$4 = this.pick($$1, $$2, $$3, $$0);
        if ($$4 instanceof EntityHitResult) {
            EntityHitResult $$5 = (EntityHitResult)$$4;
            entity = $$5.getEntity();
        } else {
            entity = null;
        }
        this.minecraft.crosshairPickEntity = entity;
        Profiler.get().pop();
    }

    private HitResult pick(Entity $$0, double $$1, double $$2, float $$3) {
        double $$4 = Math.max($$1, $$2);
        double $$5 = Mth.square($$4);
        Vec3 $$6 = $$0.getEyePosition($$3);
        HitResult $$7 = $$0.pick($$4, $$3, false);
        double $$8 = $$7.getLocation().distanceToSqr($$6);
        if ($$7.getType() != HitResult.Type.MISS) {
            $$5 = $$8;
            $$4 = Math.sqrt($$5);
        }
        Vec3 $$9 = $$0.getViewVector($$3);
        Vec3 $$10 = $$6.add($$9.x * $$4, $$9.y * $$4, $$9.z * $$4);
        float $$11 = 1.0f;
        AABB $$12 = $$0.getBoundingBox().expandTowards($$9.scale($$4)).inflate(1.0, 1.0, 1.0);
        EntityHitResult $$13 = ProjectileUtil.getEntityHitResult($$0, $$6, $$10, $$12, EntitySelector.CAN_BE_PICKED, $$5);
        if ($$13 != null && $$13.getLocation().distanceToSqr($$6) < $$8) {
            return GameRenderer.filterHitResult($$13, $$6, $$2);
        }
        return GameRenderer.filterHitResult($$7, $$6, $$1);
    }

    private static HitResult filterHitResult(HitResult $$0, Vec3 $$1, double $$2) {
        Vec3 $$3 = $$0.getLocation();
        if (!$$3.closerThan($$1, $$2)) {
            Vec3 $$4 = $$0.getLocation();
            Direction $$5 = Direction.getApproximateNearest($$4.x - $$1.x, $$4.y - $$1.y, $$4.z - $$1.z);
            return BlockHitResult.miss($$4, $$5, BlockPos.containing($$4));
        }
        return $$0;
    }

    private void tickFov() {
        float $$5;
        Entity entity = this.minecraft.getCameraEntity();
        if (entity instanceof AbstractClientPlayer) {
            AbstractClientPlayer $$0 = (AbstractClientPlayer)entity;
            Options $$1 = this.minecraft.options;
            boolean $$2 = $$1.getCameraType().isFirstPerson();
            float $$3 = $$1.fovEffectScale().get().floatValue();
            float $$4 = $$0.getFieldOfViewModifier($$2, $$3);
        } else {
            $$5 = 1.0f;
        }
        this.oldFovModifier = this.fovModifier;
        this.fovModifier += ($$5 - this.fovModifier) * 0.5f;
        this.fovModifier = Mth.clamp(this.fovModifier, 0.1f, 1.5f);
    }

    private float getFov(Camera $$0, float $$1, boolean $$2) {
        FogType $$6;
        LivingEntity $$4;
        Entity entity;
        if (this.panoramicMode) {
            return 90.0f;
        }
        float $$3 = 70.0f;
        if ($$2) {
            $$3 = this.minecraft.options.fov().get().intValue();
            $$3 *= Mth.lerp($$1, this.oldFovModifier, this.fovModifier);
        }
        if ((entity = $$0.getEntity()) instanceof LivingEntity && ($$4 = (LivingEntity)entity).isDeadOrDying()) {
            float $$5 = Math.min((float)$$4.deathTime + $$1, 20.0f);
            $$3 /= (1.0f - 500.0f / ($$5 + 500.0f)) * 2.0f + 1.0f;
        }
        if (($$6 = $$0.getFluidInCamera()) == FogType.LAVA || $$6 == FogType.WATER) {
            float $$7 = this.minecraft.options.fovEffectScale().get().floatValue();
            $$3 *= Mth.lerp($$7, 1.0f, 0.85714287f);
        }
        return $$3;
    }

    private void bobHurt(PoseStack $$0, float $$1) {
        Entity entity = this.minecraft.getCameraEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity $$2 = (LivingEntity)entity;
            float $$3 = (float)$$2.hurtTime - $$1;
            if ($$2.isDeadOrDying()) {
                float $$4 = Math.min((float)$$2.deathTime + $$1, 20.0f);
                $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(40.0f - 8000.0f / ($$4 + 200.0f)));
            }
            if ($$3 < 0.0f) {
                return;
            }
            $$3 /= (float)$$2.hurtDuration;
            $$3 = Mth.sin($$3 * $$3 * $$3 * $$3 * (float)Math.PI);
            float $$5 = $$2.getHurtDir();
            $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-$$5));
            float $$6 = (float)((double)(-$$3) * 14.0 * this.minecraft.options.damageTiltStrength().get());
            $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees($$6));
            $$0.mulPose((Quaternionfc)Axis.YP.rotationDegrees($$5));
        }
    }

    /*
     * WARNING - void declaration
     */
    private void bobView(PoseStack $$0, float $$1) {
        void $$3;
        Entity entity = this.minecraft.getCameraEntity();
        if (!(entity instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer $$2 = (AbstractClientPlayer)entity;
        float $$4 = $$3.walkDist - $$3.walkDistO;
        float $$5 = -($$3.walkDist + $$4 * $$1);
        float $$6 = Mth.lerp($$1, $$3.oBob, $$3.bob);
        $$0.translate(Mth.sin($$5 * (float)Math.PI) * $$6 * 0.5f, -Math.abs(Mth.cos($$5 * (float)Math.PI) * $$6), 0.0f);
        $$0.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(Mth.sin($$5 * (float)Math.PI) * $$6 * 3.0f));
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Math.abs(Mth.cos($$5 * (float)Math.PI - 0.2f) * $$6) * 5.0f));
    }

    private void renderItemInHand(float $$0, boolean $$1, Matrix4f $$2) {
        if (this.panoramicMode) {
            return;
        }
        PoseStack $$3 = new PoseStack();
        $$3.pushPose();
        $$3.mulPose((Matrix4fc)$$2.invert(new Matrix4f()));
        Matrix4fStack $$4 = RenderSystem.getModelViewStack();
        $$4.pushMatrix().mul((Matrix4fc)$$2);
        this.bobHurt($$3, $$0);
        if (this.minecraft.options.bobView().get().booleanValue()) {
            this.bobView($$3, $$0);
        }
        if (this.minecraft.options.getCameraType().isFirstPerson() && !$$1 && !this.minecraft.options.hideGui && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.lightTexture.turnOnLightLayer();
            this.itemInHandRenderer.renderHandsWithItems($$0, $$3, this.renderBuffers.bufferSource(), this.minecraft.player, this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, $$0));
            this.lightTexture.turnOffLightLayer();
        }
        $$4.popMatrix();
        $$3.popPose();
    }

    public Matrix4f getProjectionMatrix(float $$0) {
        Matrix4f $$1 = new Matrix4f();
        return $$1.perspective($$0 * ((float)Math.PI / 180), (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight(), 0.05f, this.getDepthFar());
    }

    public float getDepthFar() {
        return Math.max(this.renderDistance * 4.0f, (float)(this.minecraft.options.cloudRange().get() * 16));
    }

    public static float getNightVisionScale(LivingEntity $$0, float $$1) {
        MobEffectInstance $$2 = $$0.getEffect(MobEffects.NIGHT_VISION);
        if (!$$2.endsWithin(200)) {
            return 1.0f;
        }
        return 0.7f + Mth.sin(((float)$$2.getDuration() - $$1) * (float)Math.PI * 0.2f) * 0.3f;
    }

    public void render(DeltaTracker $$0, boolean $$1) {
        if (this.minecraft.isWindowActive() || !this.minecraft.options.pauseOnLostFocus || this.minecraft.options.touchscreen().get().booleanValue() && this.minecraft.mouseHandler.isRightPressed()) {
            this.lastActiveTime = Util.getMillis();
        } else if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
        }
        if (this.minecraft.noRender) {
            return;
        }
        this.globalSettingsUniform.update(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.minecraft.options.glintStrength().get(), this.minecraft.level == null ? 0L : this.minecraft.level.getGameTime(), $$0, this.minecraft.options.getMenuBackgroundBlurriness());
        ProfilerFiller $$2 = Profiler.get();
        boolean $$3 = this.minecraft.isGameLoadFinished();
        int $$4 = (int)this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
        int $$5 = (int)this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
        if ($$3 && $$1 && this.minecraft.level != null) {
            $$2.push("world");
            this.renderLevel($$0);
            this.tryTakeScreenshotIfNeeded();
            this.minecraft.levelRenderer.doEntityOutline();
            if (this.postEffectId != null && this.effectActive) {
                RenderSystem.resetTextureMatrix();
                PostChain $$6 = this.minecraft.getShaderManager().getPostChain(this.postEffectId, LevelTargetBundle.MAIN_TARGETS);
                if ($$6 != null) {
                    $$6.process(this.minecraft.getMainRenderTarget(), this.resourcePool);
                }
            }
        }
        this.fogRenderer.endFrame();
        RenderTarget $$7 = this.minecraft.getMainRenderTarget();
        RenderSystem.getDevice().createCommandEncoder().clearDepthTexture($$7.getDepthTexture(), 1.0);
        this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        this.guiRenderState.reset();
        GuiGraphics $$8 = new GuiGraphics(this.minecraft, this.guiRenderState);
        if ($$3 && $$1 && this.minecraft.level != null) {
            $$2.popPush("gui");
            this.minecraft.gui.render($$8, $$0);
            $$2.pop();
        }
        if (this.minecraft.getOverlay() != null) {
            try {
                this.minecraft.getOverlay().render($$8, $$4, $$5, $$0.getGameTimeDeltaTicks());
            } catch (Throwable $$9) {
                CrashReport $$10 = CrashReport.forThrowable($$9, "Rendering overlay");
                CrashReportCategory $$11 = $$10.addCategory("Overlay render details");
                $$11.setDetail("Overlay name", () -> this.minecraft.getOverlay().getClass().getCanonicalName());
                throw new ReportedException($$10);
            }
        }
        if ($$3 && this.minecraft.screen != null) {
            try {
                this.minecraft.screen.renderWithTooltip($$8, $$4, $$5, $$0.getGameTimeDeltaTicks());
            } catch (Throwable $$12) {
                CrashReport $$13 = CrashReport.forThrowable($$12, "Rendering screen");
                CrashReportCategory $$14 = $$13.addCategory("Screen render details");
                $$14.setDetail("Screen name", () -> this.minecraft.screen.getClass().getCanonicalName());
                this.minecraft.mouseHandler.fillMousePositionDetails($$14, this.minecraft.getWindow());
                throw new ReportedException($$13);
            }
            try {
                if (this.minecraft.screen != null) {
                    this.minecraft.screen.handleDelayedNarration();
                }
            } catch (Throwable $$15) {
                CrashReport $$16 = CrashReport.forThrowable($$15, "Narrating screen");
                CrashReportCategory $$17 = $$16.addCategory("Screen details");
                $$17.setDetail("Screen name", () -> this.minecraft.screen.getClass().getCanonicalName());
                throw new ReportedException($$16);
            }
        }
        if ($$3 && $$1 && this.minecraft.level != null) {
            this.minecraft.gui.renderSavingIndicator($$8, $$0);
        }
        if ($$3) {
            try (Zone $$18 = $$2.zone("toasts");){
                this.minecraft.getToastManager().render($$8);
            }
        }
        this.guiRenderer.render(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
        this.guiRenderer.incrementFrameNumber();
        this.resourcePool.endFrame();
    }

    private void tryTakeScreenshotIfNeeded() {
        if (this.hasWorldScreenshot || !this.minecraft.isLocalServer()) {
            return;
        }
        long $$02 = Util.getMillis();
        if ($$02 - this.lastScreenshotAttempt < 1000L) {
            return;
        }
        this.lastScreenshotAttempt = $$02;
        IntegratedServer $$1 = this.minecraft.getSingleplayerServer();
        if ($$1 == null || $$1.isStopped()) {
            return;
        }
        $$1.getWorldScreenshotFile().ifPresent($$0 -> {
            if (Files.isRegularFile($$0, new LinkOption[0])) {
                this.hasWorldScreenshot = true;
            } else {
                this.takeAutoScreenshot((Path)$$0);
            }
        });
    }

    private void takeAutoScreenshot(Path $$0) {
        if (this.minecraft.levelRenderer.countRenderedSections() > 10 && this.minecraft.levelRenderer.hasRenderedAllSections()) {
            Screenshot.takeScreenshot(this.minecraft.getMainRenderTarget(), $$1 -> Util.ioPool().execute(() -> {
                int $$2 = $$1.getWidth();
                int $$3 = $$1.getHeight();
                int $$4 = 0;
                int $$5 = 0;
                if ($$2 > $$3) {
                    $$4 = ($$2 - $$3) / 2;
                    $$2 = $$3;
                } else {
                    $$5 = ($$3 - $$2) / 2;
                    $$3 = $$2;
                }
                try (NativeImage $$6 = new NativeImage(64, 64, false);){
                    $$1.resizeSubRectTo($$4, $$5, $$2, $$3, $$6);
                    $$6.writeToFile($$0);
                } catch (IOException $$7) {
                    LOGGER.warn("Couldn't save auto screenshot", $$7);
                } finally {
                    $$1.close();
                }
            }));
        }
    }

    private boolean shouldRenderBlockOutline() {
        boolean $$1;
        if (!this.renderBlockOutline) {
            return false;
        }
        Entity $$0 = this.minecraft.getCameraEntity();
        boolean bl = $$1 = $$0 instanceof Player && !this.minecraft.options.hideGui;
        if ($$1 && !((Player)$$0).getAbilities().mayBuild) {
            ItemStack $$2 = ((LivingEntity)$$0).getMainHandItem();
            HitResult $$3 = this.minecraft.hitResult;
            if ($$3 != null && $$3.getType() == HitResult.Type.BLOCK) {
                BlockPos $$4 = ((BlockHitResult)$$3).getBlockPos();
                BlockState $$5 = this.minecraft.level.getBlockState($$4);
                if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
                    $$1 = $$5.getMenuProvider(this.minecraft.level, $$4) != null;
                } else {
                    BlockInWorld $$6 = new BlockInWorld(this.minecraft.level, $$4, false);
                    HolderLookup.RegistryLookup $$7 = this.minecraft.level.registryAccess().lookupOrThrow(Registries.BLOCK);
                    $$1 = !$$2.isEmpty() && ($$2.canBreakBlockInAdventureMode($$6) || $$2.canPlaceOnBlockInAdventureMode($$6));
                }
            }
        }
        return $$1;
    }

    public void renderLevel(DeltaTracker $$0) {
        float $$1 = $$0.getGameTimeDeltaPartialTick(true);
        LocalPlayer $$2 = this.minecraft.player;
        this.lightTexture.updateLightTexture($$1);
        if (this.minecraft.getCameraEntity() == null) {
            this.minecraft.setCameraEntity($$2);
        }
        this.pick($$1);
        ProfilerFiller $$3 = Profiler.get();
        $$3.push("center");
        boolean $$4 = this.shouldRenderBlockOutline();
        $$3.popPush("camera");
        Camera $$5 = this.mainCamera;
        LocalPlayer $$6 = this.minecraft.getCameraEntity() == null ? $$2 : this.minecraft.getCameraEntity();
        float $$7 = this.minecraft.level.tickRateManager().isEntityFrozen($$6) ? 1.0f : $$1;
        $$5.setup(this.minecraft.level, $$6, !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), $$7);
        this.renderDistance = this.minecraft.options.getEffectiveRenderDistance() * 16;
        float $$8 = this.getFov($$5, $$1, true);
        Matrix4f $$9 = this.getProjectionMatrix($$8);
        PoseStack $$10 = new PoseStack();
        this.bobHurt($$10, $$5.getPartialTickTime());
        if (this.minecraft.options.bobView().get().booleanValue()) {
            this.bobView($$10, $$5.getPartialTickTime());
        }
        $$9.mul((Matrix4fc)$$10.last().pose());
        float $$11 = this.minecraft.options.screenEffectScale().get().floatValue();
        float $$12 = Mth.lerp($$1, $$2.oPortalEffectIntensity, $$2.portalEffectIntensity);
        float $$13 = $$2.getEffectBlendFactor(MobEffects.NAUSEA, $$1);
        float $$14 = Math.max($$12, $$13) * ($$11 * $$11);
        if ($$14 > 0.0f) {
            float $$15 = 5.0f / ($$14 * $$14 + 5.0f) - $$14 * 0.04f;
            $$15 *= $$15;
            Vector3f $$16 = new Vector3f(0.0f, Mth.SQRT_OF_TWO / 2.0f, Mth.SQRT_OF_TWO / 2.0f);
            float $$17 = (this.spinningEffectTime + $$1 * this.spinningEffectSpeed) * ((float)Math.PI / 180);
            $$9.rotate($$17, (Vector3fc)$$16);
            $$9.scale(1.0f / $$15, 1.0f, 1.0f);
            $$9.rotate(-$$17, (Vector3fc)$$16);
        }
        float $$18 = Math.max($$8, (float)this.minecraft.options.fov().get().intValue());
        Matrix4f $$19 = this.getProjectionMatrix($$18);
        RenderSystem.setProjectionMatrix(this.levelProjectionMatrixBuffer.getBuffer($$9), ProjectionType.PERSPECTIVE);
        Quaternionf $$20 = $$5.rotation().conjugate(new Quaternionf());
        Matrix4f $$21 = new Matrix4f().rotation((Quaternionfc)$$20);
        this.minecraft.levelRenderer.prepareCullFrustum($$5.getPosition(), $$21, $$19);
        $$3.popPush("fog");
        boolean $$22 = this.minecraft.level.effects().isFoggyAt($$5.getBlockPosition().getX(), $$5.getBlockPosition().getZ()) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
        Vector4f $$23 = this.fogRenderer.setupFog($$5, this.minecraft.options.getEffectiveRenderDistance(), $$22, $$0, this.getDarkenWorldAmount($$1), this.minecraft.level);
        GpuBufferSlice $$24 = this.fogRenderer.getBuffer(FogRenderer.FogMode.WORLD);
        $$3.popPush("level");
        this.minecraft.levelRenderer.renderLevel(this.resourcePool, $$0, $$4, $$5, $$21, $$9, $$24, $$23, !$$22);
        $$3.popPush("hand");
        boolean $$25 = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
        RenderSystem.setProjectionMatrix(this.hud3dProjectionMatrixBuffer.getBuffer(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.getFov($$5, $$1, false)), ProjectionType.PERSPECTIVE);
        RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.minecraft.getMainRenderTarget().getDepthTexture(), 1.0);
        this.renderItemInHand($$1, $$25, $$21);
        $$3.popPush("screen effects");
        MultiBufferSource.BufferSource $$26 = this.renderBuffers.bufferSource();
        this.screenEffectRenderer.renderScreenEffect($$25, $$1);
        $$26.endBatch();
        $$3.pop();
        RenderSystem.setShaderFog(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
        if (this.minecraft.gui.shouldRenderDebugCrosshair()) {
            this.minecraft.getDebugOverlay().render3dCrosshair($$5);
        }
    }

    public void resetData() {
        this.screenEffectRenderer.resetItemActivation();
        this.minecraft.getMapTextureManager().resetData();
        this.mainCamera.reset();
        this.hasWorldScreenshot = false;
    }

    public void displayItemActivation(ItemStack $$0) {
        this.screenEffectRenderer.displayItemActivation($$0, this.random);
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public float getDarkenWorldAmount(float $$0) {
        return Mth.lerp($$0, this.darkenWorldAmountO, this.darkenWorldAmount);
    }

    public float getRenderDistance() {
        return this.renderDistance;
    }

    public Camera getMainCamera() {
        return this.mainCamera;
    }

    public LightTexture lightTexture() {
        return this.lightTexture;
    }

    public OverlayTexture overlayTexture() {
        return this.overlayTexture;
    }

    @Override
    public Vec3 projectPointToScreen(Vec3 $$0) {
        Matrix4f $$1 = this.getProjectionMatrix(this.getFov(this.mainCamera, 0.0f, true));
        Quaternionf $$2 = this.mainCamera.rotation().conjugate(new Quaternionf());
        Matrix4f $$3 = new Matrix4f().rotation((Quaternionfc)$$2);
        Matrix4f $$4 = $$1.mul((Matrix4fc)$$3);
        Vec3 $$5 = this.mainCamera.getPosition();
        Vec3 $$6 = $$0.subtract($$5);
        Vector3f $$7 = $$4.transformProject($$6.toVector3f());
        return new Vec3($$7);
    }

    @Override
    public double projectHorizonToScreen() {
        float $$0 = this.mainCamera.getXRot();
        if ($$0 <= -90.0f) {
            return Double.NEGATIVE_INFINITY;
        }
        if ($$0 >= 90.0f) {
            return Double.POSITIVE_INFINITY;
        }
        float $$1 = this.getFov(this.mainCamera, 0.0f, true);
        return Math.tan($$0 * ((float)Math.PI / 180)) / Math.tan($$1 / 2.0f * ((float)Math.PI / 180));
    }

    public GlobalSettingsUniform getGlobalSettingsUniform() {
        return this.globalSettingsUniform;
    }

    public Lighting getLighting() {
        return this.lighting;
    }

    public void setLevel(@Nullable ClientLevel $$0) {
        if ($$0 != null) {
            this.lighting.updateLevel($$0.effects().constantAmbientLight());
        }
    }

    public PanoramaRenderer getPanorama() {
        return this.panorama;
    }
}

