/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.DynamicUniforms;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.WorldBorderRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionBuffers;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.chunk.TranslucencyPointOfView;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Brightness;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.slf4j.Logger;

public class LevelRenderer
implements ResourceManagerReloadListener,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation TRANSPARENCY_POST_CHAIN_ID = ResourceLocation.withDefaultNamespace("transparency");
    private static final ResourceLocation ENTITY_OUTLINE_POST_CHAIN_ID = ResourceLocation.withDefaultNamespace("entity_outline");
    public static final int SECTION_SIZE = 16;
    public static final int HALF_SECTION_SIZE = 8;
    public static final int NEARBY_SECTION_DISTANCE_IN_BLOCKS = 32;
    private static final int MINIMUM_TRANSPARENT_SORT_COUNT = 15;
    private static final Comparator<Entity> ENTITY_COMPARATOR = Comparator.comparing($$0 -> $$0.getType().hashCode());
    private final Minecraft minecraft;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final RenderBuffers renderBuffers;
    private final SkyRenderer skyRenderer = new SkyRenderer();
    private final CloudRenderer cloudRenderer = new CloudRenderer();
    private final WorldBorderRenderer worldBorderRenderer = new WorldBorderRenderer();
    private final WeatherEffectRenderer weatherEffectRenderer = new WeatherEffectRenderer();
    @Nullable
    private ClientLevel level;
    private final SectionOcclusionGraph sectionOcclusionGraph = new SectionOcclusionGraph();
    private final ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections = new ObjectArrayList(10000);
    private final ObjectArrayList<SectionRenderDispatcher.RenderSection> nearbyVisibleSections = new ObjectArrayList(50);
    @Nullable
    private ViewArea viewArea;
    private int ticks;
    private final Int2ObjectMap<BlockDestructionProgress> destroyingBlocks = new Int2ObjectOpenHashMap();
    private final Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress = new Long2ObjectOpenHashMap();
    @Nullable
    private RenderTarget entityOutlineTarget;
    private final LevelTargetBundle targets = new LevelTargetBundle();
    private int lastCameraSectionX = Integer.MIN_VALUE;
    private int lastCameraSectionY = Integer.MIN_VALUE;
    private int lastCameraSectionZ = Integer.MIN_VALUE;
    private double prevCamX = Double.MIN_VALUE;
    private double prevCamY = Double.MIN_VALUE;
    private double prevCamZ = Double.MIN_VALUE;
    private double prevCamRotX = Double.MIN_VALUE;
    private double prevCamRotY = Double.MIN_VALUE;
    @Nullable
    private SectionRenderDispatcher sectionRenderDispatcher;
    private int lastViewDistance = -1;
    private final List<Entity> visibleEntities = new ArrayList<Entity>();
    private int visibleEntityCount;
    private Frustum cullingFrustum;
    private boolean captureFrustum;
    @Nullable
    private Frustum capturedFrustum;
    @Nullable
    private BlockPos lastTranslucentSortBlockPos;
    private int translucencyResortIterationIndex;

    public LevelRenderer(Minecraft $$0, EntityRenderDispatcher $$1, BlockEntityRenderDispatcher $$2, RenderBuffers $$3) {
        this.minecraft = $$0;
        this.entityRenderDispatcher = $$1;
        this.blockEntityRenderDispatcher = $$2;
        this.renderBuffers = $$3;
    }

    public void tickParticles(Camera $$0) {
        this.weatherEffectRenderer.tickRainParticles(this.minecraft.level, $$0, this.ticks, this.minecraft.options.particles().get());
    }

    @Override
    public void close() {
        if (this.entityOutlineTarget != null) {
            this.entityOutlineTarget.destroyBuffers();
        }
        this.skyRenderer.close();
        this.cloudRenderer.close();
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        this.initOutline();
    }

    public void initOutline() {
        if (this.entityOutlineTarget != null) {
            this.entityOutlineTarget.destroyBuffers();
        }
        this.entityOutlineTarget = new TextureTarget("Entity Outline", this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), true);
    }

    @Nullable
    private PostChain getTransparencyChain() {
        if (!Minecraft.useShaderTransparency()) {
            return null;
        }
        PostChain $$0 = this.minecraft.getShaderManager().getPostChain(TRANSPARENCY_POST_CHAIN_ID, LevelTargetBundle.SORTING_TARGETS);
        if ($$0 == null) {
            this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
            this.minecraft.options.save();
        }
        return $$0;
    }

    public void doEntityOutline() {
        if (this.shouldShowEntityOutlines()) {
            this.entityOutlineTarget.blitAndBlendToTexture(this.minecraft.getMainRenderTarget().getColorTextureView());
        }
    }

    protected boolean shouldShowEntityOutlines() {
        return !this.minecraft.gameRenderer.isPanoramicMode() && this.entityOutlineTarget != null && this.minecraft.player != null;
    }

    public void setLevel(@Nullable ClientLevel $$0) {
        this.lastCameraSectionX = Integer.MIN_VALUE;
        this.lastCameraSectionY = Integer.MIN_VALUE;
        this.lastCameraSectionZ = Integer.MIN_VALUE;
        this.entityRenderDispatcher.setLevel($$0);
        this.level = $$0;
        if ($$0 != null) {
            this.allChanged();
        } else {
            if (this.viewArea != null) {
                this.viewArea.releaseAllBuffers();
                this.viewArea = null;
            }
            if (this.sectionRenderDispatcher != null) {
                this.sectionRenderDispatcher.dispose();
            }
            this.sectionRenderDispatcher = null;
            this.sectionOcclusionGraph.waitAndReset(null);
            this.clearVisibleSections();
        }
    }

    private void clearVisibleSections() {
        this.visibleSections.clear();
        this.nearbyVisibleSections.clear();
    }

    public void allChanged() {
        if (this.level == null) {
            return;
        }
        this.level.clearTintCaches();
        if (this.sectionRenderDispatcher == null) {
            this.sectionRenderDispatcher = new SectionRenderDispatcher(this.level, this, Util.backgroundExecutor(), this.renderBuffers, this.minecraft.getBlockRenderer(), this.minecraft.getBlockEntityRenderDispatcher());
        } else {
            this.sectionRenderDispatcher.setLevel(this.level);
        }
        this.cloudRenderer.markForRebuild();
        ItemBlockRenderTypes.setFancy(Minecraft.useFancyGraphics());
        this.lastViewDistance = this.minecraft.options.getEffectiveRenderDistance();
        if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
        }
        this.sectionRenderDispatcher.clearCompileQueue();
        this.viewArea = new ViewArea(this.sectionRenderDispatcher, this.level, this.minecraft.options.getEffectiveRenderDistance(), this);
        this.sectionOcclusionGraph.waitAndReset(this.viewArea);
        this.clearVisibleSections();
        Camera $$0 = this.minecraft.gameRenderer.getMainCamera();
        this.viewArea.repositionCamera(SectionPos.of($$0.getPosition()));
    }

    public void resize(int $$0, int $$1) {
        this.needsUpdate();
        if (this.entityOutlineTarget != null) {
            this.entityOutlineTarget.resize($$0, $$1);
        }
    }

    public String getSectionStatistics() {
        int $$0 = this.viewArea.sections.length;
        int $$1 = this.countRenderedSections();
        return String.format(Locale.ROOT, "C: %d/%d %sD: %d, %s", $$1, $$0, this.minecraft.smartCull ? "(s) " : "", this.lastViewDistance, this.sectionRenderDispatcher == null ? "null" : this.sectionRenderDispatcher.getStats());
    }

    public SectionRenderDispatcher getSectionRenderDispatcher() {
        return this.sectionRenderDispatcher;
    }

    public double getTotalSections() {
        return this.viewArea.sections.length;
    }

    public double getLastViewDistance() {
        return this.lastViewDistance;
    }

    public int countRenderedSections() {
        int $$0 = 0;
        for (SectionRenderDispatcher.RenderSection $$1 : this.visibleSections) {
            if (!$$1.getSectionMesh().hasRenderableLayers()) continue;
            ++$$0;
        }
        return $$0;
    }

    public String getEntityStatistics() {
        return "E: " + this.visibleEntityCount + "/" + this.level.getEntityCount() + ", SD: " + this.level.getServerSimulationDistance();
    }

    private void setupRender(Camera $$0, Frustum $$1, boolean $$2, boolean $$3) {
        Vec3 $$4 = $$0.getPosition();
        if (this.minecraft.options.getEffectiveRenderDistance() != this.lastViewDistance) {
            this.allChanged();
        }
        ProfilerFiller $$5 = Profiler.get();
        $$5.push("camera");
        int $$6 = SectionPos.posToSectionCoord($$4.x());
        int $$7 = SectionPos.posToSectionCoord($$4.y());
        int $$8 = SectionPos.posToSectionCoord($$4.z());
        if (this.lastCameraSectionX != $$6 || this.lastCameraSectionY != $$7 || this.lastCameraSectionZ != $$8) {
            this.lastCameraSectionX = $$6;
            this.lastCameraSectionY = $$7;
            this.lastCameraSectionZ = $$8;
            this.viewArea.repositionCamera(SectionPos.of($$4));
            this.worldBorderRenderer.invalidate();
        }
        this.sectionRenderDispatcher.setCameraPosition($$4);
        $$5.popPush("cull");
        double $$9 = Math.floor($$4.x / 8.0);
        double $$10 = Math.floor($$4.y / 8.0);
        double $$11 = Math.floor($$4.z / 8.0);
        if ($$9 != this.prevCamX || $$10 != this.prevCamY || $$11 != this.prevCamZ) {
            this.sectionOcclusionGraph.invalidate();
        }
        this.prevCamX = $$9;
        this.prevCamY = $$10;
        this.prevCamZ = $$11;
        $$5.popPush("update");
        if (!$$2) {
            boolean $$12 = this.minecraft.smartCull;
            if ($$3 && this.level.getBlockState($$0.getBlockPosition()).isSolidRender()) {
                $$12 = false;
            }
            $$5.push("section_occlusion_graph");
            this.sectionOcclusionGraph.update($$12, $$0, $$1, (List<SectionRenderDispatcher.RenderSection>)this.visibleSections, this.level.getChunkSource().getLoadedEmptySections());
            $$5.pop();
            double $$13 = Math.floor($$0.getXRot() / 2.0f);
            double $$14 = Math.floor($$0.getYRot() / 2.0f);
            if (this.sectionOcclusionGraph.consumeFrustumUpdate() || $$13 != this.prevCamRotX || $$14 != this.prevCamRotY) {
                this.applyFrustum(LevelRenderer.offsetFrustum($$1));
                this.prevCamRotX = $$13;
                this.prevCamRotY = $$14;
            }
        }
        $$5.pop();
    }

    public static Frustum offsetFrustum(Frustum $$0) {
        return new Frustum($$0).offsetToFullyIncludeCameraCube(8);
    }

    private void applyFrustum(Frustum $$0) {
        if (!Minecraft.getInstance().isSameThread()) {
            throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
        }
        Profiler.get().push("apply_frustum");
        this.clearVisibleSections();
        this.sectionOcclusionGraph.addSectionsInFrustum($$0, (List<SectionRenderDispatcher.RenderSection>)this.visibleSections, (List<SectionRenderDispatcher.RenderSection>)this.nearbyVisibleSections);
        Profiler.get().pop();
    }

    public void addRecentlyCompiledSection(SectionRenderDispatcher.RenderSection $$0) {
        this.sectionOcclusionGraph.schedulePropagationFrom($$0);
    }

    public void prepareCullFrustum(Vec3 $$0, Matrix4f $$1, Matrix4f $$2) {
        this.cullingFrustum = new Frustum($$1, $$2);
        this.cullingFrustum.prepare($$0.x(), $$0.y(), $$0.z());
    }

    public void renderLevel(GraphicsResourceAllocator $$0, DeltaTracker $$1, boolean $$2, Camera $$3, Matrix4f $$4, Matrix4f $$5, GpuBufferSlice $$6, Vector4f $$7, boolean $$8) {
        Optional<Integer> $$27;
        float $$9 = $$1.getGameTimeDeltaPartialTick(false);
        this.blockEntityRenderDispatcher.prepare(this.level, $$3, this.minecraft.hitResult);
        this.entityRenderDispatcher.prepare(this.level, $$3, this.minecraft.crosshairPickEntity);
        final ProfilerFiller $$10 = Profiler.get();
        $$10.push("light_update_queue");
        this.level.pollLightUpdates();
        $$10.popPush("light_updates");
        this.level.getChunkSource().getLightEngine().runLightUpdates();
        Vec3 $$11 = $$3.getPosition();
        double $$12 = $$11.x();
        double $$13 = $$11.y();
        double $$14 = $$11.z();
        $$10.popPush("culling");
        boolean $$15 = this.capturedFrustum != null;
        Frustum $$16 = $$15 ? this.capturedFrustum : this.cullingFrustum;
        $$10.popPush("captureFrustum");
        if (this.captureFrustum) {
            this.capturedFrustum = $$15 ? new Frustum($$4, $$5) : $$16;
            this.capturedFrustum.prepare($$12, $$13, $$14);
            this.captureFrustum = false;
        }
        $$10.popPush("cullEntities");
        boolean $$17 = this.collectVisibleEntities($$3, $$16, this.visibleEntities);
        this.visibleEntityCount = this.visibleEntities.size();
        $$10.popPush("terrain_setup");
        this.setupRender($$3, $$16, $$15, this.minecraft.player.isSpectator());
        $$10.popPush("compile_sections");
        this.compileSections($$3);
        Matrix4fStack $$18 = RenderSystem.getModelViewStack();
        $$18.pushMatrix();
        $$18.mul((Matrix4fc)$$4);
        FrameGraphBuilder $$19 = new FrameGraphBuilder();
        this.targets.main = $$19.importExternal("main", this.minecraft.getMainRenderTarget());
        int $$20 = this.minecraft.getMainRenderTarget().width;
        int $$21 = this.minecraft.getMainRenderTarget().height;
        RenderTargetDescriptor $$22 = new RenderTargetDescriptor($$20, $$21, true, 0);
        PostChain $$23 = this.getTransparencyChain();
        if ($$23 != null) {
            this.targets.translucent = $$19.createInternal("translucent", $$22);
            this.targets.itemEntity = $$19.createInternal("item_entity", $$22);
            this.targets.particles = $$19.createInternal("particles", $$22);
            this.targets.weather = $$19.createInternal("weather", $$22);
            this.targets.clouds = $$19.createInternal("clouds", $$22);
        }
        if (this.entityOutlineTarget != null) {
            this.targets.entityOutline = $$19.importExternal("entity_outline", this.entityOutlineTarget);
        }
        FramePass $$24 = $$19.addPass("clear");
        this.targets.main = $$24.readsAndWrites(this.targets.main);
        $$24.executes(() -> {
            RenderTarget $$1 = this.minecraft.getMainRenderTarget();
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures($$1.getColorTexture(), ARGB.colorFromFloat(0.0f, $$0.x, $$0.y, $$0.z), $$1.getDepthTexture(), 1.0);
        });
        if ($$8) {
            this.addSkyPass($$19, $$3, $$9, $$6);
        }
        this.addMainPass($$19, $$16, $$3, $$4, $$6, $$2, $$17, $$1, $$10);
        PostChain $$25 = this.minecraft.getShaderManager().getPostChain(ENTITY_OUTLINE_POST_CHAIN_ID, LevelTargetBundle.OUTLINE_TARGETS);
        if ($$17 && $$25 != null) {
            $$25.addToFrame($$19, $$20, $$21, this.targets);
        }
        this.addParticlesPass($$19, $$3, $$9, $$6);
        CloudStatus $$26 = this.minecraft.options.getCloudsType();
        if ($$26 != CloudStatus.OFF && ($$27 = this.level.dimensionType().cloudHeight()).isPresent()) {
            float $$28 = (float)this.ticks + $$9;
            int $$29 = this.level.getCloudColor($$9);
            this.addCloudsPass($$19, $$26, $$3.getPosition(), $$28, $$29, (float)$$27.get().intValue() + 0.33f);
        }
        this.addWeatherPass($$19, $$3.getPosition(), $$9, $$6);
        if ($$23 != null) {
            $$23.addToFrame($$19, $$20, $$21, this.targets);
        }
        this.addLateDebugPass($$19, $$11, $$6);
        $$10.popPush("framegraph");
        $$19.execute($$0, new FrameGraphBuilder.Inspector(){

            @Override
            public void beforeExecutePass(String $$0) {
                $$10.push($$0);
            }

            @Override
            public void afterExecutePass(String $$0) {
                $$10.pop();
            }
        });
        this.visibleEntities.clear();
        this.targets.clear();
        $$18.popMatrix();
        $$10.pop();
    }

    private void addMainPass(FrameGraphBuilder $$0, Frustum $$1, Camera $$2, Matrix4f $$3, GpuBufferSlice $$4, boolean $$5, boolean $$6, DeltaTracker $$7, ProfilerFiller $$8) {
        FramePass $$9 = $$0.addPass("main");
        this.targets.main = $$9.readsAndWrites(this.targets.main);
        if (this.targets.translucent != null) {
            this.targets.translucent = $$9.readsAndWrites(this.targets.translucent);
        }
        if (this.targets.itemEntity != null) {
            this.targets.itemEntity = $$9.readsAndWrites(this.targets.itemEntity);
        }
        if (this.targets.weather != null) {
            this.targets.weather = $$9.readsAndWrites(this.targets.weather);
        }
        if ($$6 && this.targets.entityOutline != null) {
            this.targets.entityOutline = $$9.readsAndWrites(this.targets.entityOutline);
        }
        ResourceHandle<RenderTarget> $$10 = this.targets.main;
        ResourceHandle<RenderTarget> $$11 = this.targets.translucent;
        ResourceHandle<RenderTarget> $$12 = this.targets.itemEntity;
        ResourceHandle<RenderTarget> $$13 = this.targets.entityOutline;
        $$9.executes(() -> {
            RenderSystem.setShaderFog($$4);
            float $$11 = $$7.getGameTimeDeltaPartialTick(false);
            Vec3 $$12 = $$2.getPosition();
            double $$13 = $$12.x();
            double $$14 = $$12.y();
            double $$15 = $$12.z();
            $$8.push("terrain");
            ChunkSectionsToRender $$16 = this.prepareChunkRenders((Matrix4fc)$$3, $$13, $$14, $$15);
            $$16.renderGroup(ChunkSectionLayerGroup.OPAQUE);
            this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.LEVEL);
            if ($$12 != null) {
                ((RenderTarget)$$12.get()).copyDepthFrom(this.minecraft.getMainRenderTarget());
            }
            if (this.shouldShowEntityOutlines() && $$13 != null) {
                RenderTarget $$17 = (RenderTarget)$$13.get();
                RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures($$17.getColorTexture(), 0, $$17.getDepthTexture(), 1.0);
            }
            PoseStack $$18 = new PoseStack();
            MultiBufferSource.BufferSource $$19 = this.renderBuffers.bufferSource();
            MultiBufferSource.BufferSource $$20 = this.renderBuffers.crumblingBufferSource();
            $$8.popPush("entities");
            this.visibleEntities.sort(ENTITY_COMPARATOR);
            this.renderEntities($$18, $$19, $$2, $$7, this.visibleEntities);
            $$19.endLastBatch();
            this.checkPoseStack($$18);
            $$8.popPush("blockentities");
            this.renderBlockEntities($$18, $$19, $$20, $$2, $$11);
            $$19.endLastBatch();
            this.checkPoseStack($$18);
            $$19.endBatch(RenderType.solid());
            $$19.endBatch(RenderType.endPortal());
            $$19.endBatch(RenderType.endGateway());
            $$19.endBatch(Sheets.solidBlockSheet());
            $$19.endBatch(Sheets.cutoutBlockSheet());
            $$19.endBatch(Sheets.bedSheet());
            $$19.endBatch(Sheets.shulkerBoxSheet());
            $$19.endBatch(Sheets.signSheet());
            $$19.endBatch(Sheets.hangingSignSheet());
            $$19.endBatch(Sheets.chestSheet());
            this.renderBuffers.outlineBufferSource().endOutlineBatch();
            if ($$5) {
                this.renderBlockOutline($$2, $$19, $$18, false);
            }
            $$8.popPush("debug");
            this.minecraft.debugRenderer.render($$18, $$1, $$19, $$13, $$14, $$15);
            $$19.endLastBatch();
            this.checkPoseStack($$18);
            $$19.endBatch(Sheets.translucentItemSheet());
            $$19.endBatch(Sheets.bannerSheet());
            $$19.endBatch(Sheets.shieldSheet());
            $$19.endBatch(RenderType.armorEntityGlint());
            $$19.endBatch(RenderType.glint());
            $$19.endBatch(RenderType.glintTranslucent());
            $$19.endBatch(RenderType.entityGlint());
            $$8.popPush("destroyProgress");
            this.renderBlockDestroyAnimation($$18, $$2, $$20);
            $$20.endBatch();
            this.checkPoseStack($$18);
            $$19.endBatch(RenderType.waterMask());
            $$19.endBatch();
            if ($$11 != null) {
                ((RenderTarget)$$11.get()).copyDepthFrom((RenderTarget)$$10.get());
            }
            $$8.popPush("translucent");
            $$16.renderGroup(ChunkSectionLayerGroup.TRANSLUCENT);
            $$8.popPush("string");
            $$16.renderGroup(ChunkSectionLayerGroup.TRIPWIRE);
            if ($$5) {
                this.renderBlockOutline($$2, $$19, $$18, true);
            }
            $$19.endBatch();
            $$8.pop();
        });
    }

    private void addParticlesPass(FrameGraphBuilder $$0, Camera $$1, float $$2, GpuBufferSlice $$3) {
        FramePass $$4 = $$0.addPass("particles");
        if (this.targets.particles != null) {
            this.targets.particles = $$4.readsAndWrites(this.targets.particles);
            $$4.reads(this.targets.main);
        } else {
            this.targets.main = $$4.readsAndWrites(this.targets.main);
        }
        ResourceHandle<RenderTarget> $$5 = this.targets.main;
        ResourceHandle<RenderTarget> $$6 = this.targets.particles;
        $$4.executes(() -> {
            RenderSystem.setShaderFog($$3);
            if ($$6 != null) {
                ((RenderTarget)$$6.get()).copyDepthFrom((RenderTarget)$$5.get());
            }
            this.minecraft.particleEngine.render($$1, $$2, this.renderBuffers.bufferSource());
        });
    }

    private void addCloudsPass(FrameGraphBuilder $$0, CloudStatus $$1, Vec3 $$2, float $$3, int $$4, float $$5) {
        FramePass $$6 = $$0.addPass("clouds");
        if (this.targets.clouds != null) {
            this.targets.clouds = $$6.readsAndWrites(this.targets.clouds);
        } else {
            this.targets.main = $$6.readsAndWrites(this.targets.main);
        }
        $$6.executes(() -> this.cloudRenderer.render($$4, $$1, $$5, $$2, $$3));
    }

    private void addWeatherPass(FrameGraphBuilder $$0, Vec3 $$1, float $$2, GpuBufferSlice $$3) {
        int $$4 = this.minecraft.options.getEffectiveRenderDistance() * 16;
        float $$5 = this.minecraft.gameRenderer.getDepthFar();
        FramePass $$6 = $$0.addPass("weather");
        if (this.targets.weather != null) {
            this.targets.weather = $$6.readsAndWrites(this.targets.weather);
        } else {
            this.targets.main = $$6.readsAndWrites(this.targets.main);
        }
        $$6.executes(() -> {
            RenderSystem.setShaderFog($$3);
            MultiBufferSource.BufferSource $$5 = this.renderBuffers.bufferSource();
            this.weatherEffectRenderer.render(this.minecraft.level, $$5, this.ticks, $$2, $$1);
            this.worldBorderRenderer.render(this.level.getWorldBorder(), $$1, $$4, $$5);
            $$5.endBatch();
        });
    }

    private void addLateDebugPass(FrameGraphBuilder $$0, Vec3 $$1, GpuBufferSlice $$2) {
        FramePass $$3 = $$0.addPass("late_debug");
        this.targets.main = $$3.readsAndWrites(this.targets.main);
        if (this.targets.itemEntity != null) {
            this.targets.itemEntity = $$3.readsAndWrites(this.targets.itemEntity);
        }
        ResourceHandle<RenderTarget> $$4 = this.targets.main;
        $$3.executes(() -> {
            RenderSystem.setShaderFog($$2);
            PoseStack $$2 = new PoseStack();
            MultiBufferSource.BufferSource $$3 = this.renderBuffers.bufferSource();
            this.minecraft.debugRenderer.renderAfterTranslucents($$2, $$3, $$1.x, $$1.y, $$1.z);
            $$3.endLastBatch();
            this.checkPoseStack($$2);
        });
    }

    private boolean collectVisibleEntities(Camera $$0, Frustum $$1, List<Entity> $$2) {
        Vec3 $$3 = $$0.getPosition();
        double $$4 = $$3.x();
        double $$5 = $$3.y();
        double $$6 = $$3.z();
        boolean $$7 = false;
        boolean $$8 = this.shouldShowEntityOutlines();
        Entity.setViewScale(Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0, 1.0, 2.5) * this.minecraft.options.entityDistanceScaling().get());
        for (Entity $$9 : this.level.entitiesForRendering()) {
            BlockPos $$10;
            if (!this.entityRenderDispatcher.shouldRender($$9, $$1, $$4, $$5, $$6) && !$$9.hasIndirectPassenger(this.minecraft.player) || !this.level.isOutsideBuildHeight(($$10 = $$9.blockPosition()).getY()) && !this.isSectionCompiled($$10) || $$9 == $$0.getEntity() && !$$0.isDetached() && (!($$0.getEntity() instanceof LivingEntity) || !((LivingEntity)$$0.getEntity()).isSleeping()) || $$9 instanceof LocalPlayer && $$0.getEntity() != $$9) continue;
            $$2.add($$9);
            if (!$$8 || !this.minecraft.shouldEntityAppearGlowing($$9)) continue;
            $$7 = true;
        }
        return $$7;
    }

    private void renderEntities(PoseStack $$0, MultiBufferSource.BufferSource $$1, Camera $$2, DeltaTracker $$3, List<Entity> $$4) {
        Vec3 $$5 = $$2.getPosition();
        double $$6 = $$5.x();
        double $$7 = $$5.y();
        double $$8 = $$5.z();
        TickRateManager $$9 = this.minecraft.level.tickRateManager();
        boolean $$10 = this.shouldShowEntityOutlines();
        for (Entity $$11 : $$4) {
            MultiBufferSource.BufferSource $$15;
            if ($$11.tickCount == 0) {
                $$11.xOld = $$11.getX();
                $$11.yOld = $$11.getY();
                $$11.zOld = $$11.getZ();
            }
            if ($$10 && this.minecraft.shouldEntityAppearGlowing($$11)) {
                OutlineBufferSource $$12;
                OutlineBufferSource $$13 = $$12 = this.renderBuffers.outlineBufferSource();
                int $$14 = $$11.getTeamColor();
                $$12.setColor(ARGB.red($$14), ARGB.green($$14), ARGB.blue($$14), 255);
            } else {
                $$15 = $$1;
            }
            float $$16 = $$3.getGameTimeDeltaPartialTick(!$$9.isEntityFrozen($$11));
            this.renderEntity($$11, $$6, $$7, $$8, $$16, $$0, $$15);
        }
    }

    private void renderBlockEntities(PoseStack $$0, MultiBufferSource.BufferSource $$1, MultiBufferSource.BufferSource $$22, Camera $$3, float $$4) {
        Vec3 $$5 = $$3.getPosition();
        double $$6 = $$5.x();
        double $$7 = $$5.y();
        double $$8 = $$5.z();
        for (SectionRenderDispatcher.RenderSection $$9 : this.visibleSections) {
            List<BlockEntity> $$10 = $$9.getSectionMesh().getRenderableBlockEntities();
            if ($$10.isEmpty()) continue;
            for (BlockEntity $$11 : $$10) {
                int $$15;
                BlockPos $$12 = $$11.getBlockPos();
                MultiBufferSource $$13 = $$1;
                $$0.pushPose();
                $$0.translate((double)$$12.getX() - $$6, (double)$$12.getY() - $$7, (double)$$12.getZ() - $$8);
                SortedSet $$14 = (SortedSet)this.destructionProgress.get($$12.asLong());
                if ($$14 != null && !$$14.isEmpty() && ($$15 = ((BlockDestructionProgress)$$14.last()).getProgress()) >= 0) {
                    PoseStack.Pose $$16 = $$0.last();
                    SheetedDecalTextureGenerator $$17 = new SheetedDecalTextureGenerator($$22.getBuffer(ModelBakery.DESTROY_TYPES.get($$15)), $$16, 1.0f);
                    $$13 = $$2 -> {
                        VertexConsumer $$3 = $$1.getBuffer($$2);
                        if ($$2.affectsCrumbling()) {
                            return VertexMultiConsumer.create($$17, $$3);
                        }
                        return $$3;
                    };
                }
                this.blockEntityRenderDispatcher.render($$11, $$4, $$0, $$13);
                $$0.popPose();
            }
        }
        Iterator<BlockEntity> $$18 = this.level.getGloballyRenderedBlockEntities().iterator();
        while ($$18.hasNext()) {
            BlockEntity $$19 = $$18.next();
            if ($$19.isRemoved()) {
                $$18.remove();
                continue;
            }
            BlockPos $$20 = $$19.getBlockPos();
            $$0.pushPose();
            $$0.translate((double)$$20.getX() - $$6, (double)$$20.getY() - $$7, (double)$$20.getZ() - $$8);
            this.blockEntityRenderDispatcher.render($$19, $$4, $$0, $$1);
            $$0.popPose();
        }
    }

    private void renderBlockDestroyAnimation(PoseStack $$0, Camera $$1, MultiBufferSource.BufferSource $$2) {
        Vec3 $$3 = $$1.getPosition();
        double $$4 = $$3.x();
        double $$5 = $$3.y();
        double $$6 = $$3.z();
        for (Long2ObjectMap.Entry $$7 : this.destructionProgress.long2ObjectEntrySet()) {
            SortedSet $$9;
            BlockPos $$8 = BlockPos.of($$7.getLongKey());
            if ($$8.distToCenterSqr($$4, $$5, $$6) > 1024.0 || ($$9 = (SortedSet)$$7.getValue()) == null || $$9.isEmpty()) continue;
            int $$10 = ((BlockDestructionProgress)$$9.last()).getProgress();
            $$0.pushPose();
            $$0.translate((double)$$8.getX() - $$4, (double)$$8.getY() - $$5, (double)$$8.getZ() - $$6);
            PoseStack.Pose $$11 = $$0.last();
            SheetedDecalTextureGenerator $$12 = new SheetedDecalTextureGenerator($$2.getBuffer(ModelBakery.DESTROY_TYPES.get($$10)), $$11, 1.0f);
            this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState($$8), $$8, this.level, $$0, $$12);
            $$0.popPose();
        }
    }

    /*
     * WARNING - void declaration
     */
    private void renderBlockOutline(Camera $$0, MultiBufferSource.BufferSource $$1, PoseStack $$2, boolean $$3) {
        void $$5;
        HitResult hitResult = this.minecraft.hitResult;
        if (!(hitResult instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult $$4 = (BlockHitResult)hitResult;
        if ($$5.getType() == HitResult.Type.MISS) {
            return;
        }
        BlockPos $$6 = $$5.getBlockPos();
        BlockState $$7 = this.level.getBlockState($$6);
        if (!$$7.isAir() && this.level.getWorldBorder().isWithinBounds($$6)) {
            boolean $$8 = ItemBlockRenderTypes.getChunkRenderType($$7).sortOnUpload();
            if ($$8 != $$3) {
                return;
            }
            Vec3 $$9 = $$0.getPosition();
            Boolean $$10 = this.minecraft.options.highContrastBlockOutline().get();
            if ($$10.booleanValue()) {
                VertexConsumer $$11 = $$1.getBuffer(RenderType.secondaryBlockOutline());
                this.renderHitOutline($$2, $$11, $$0.getEntity(), $$9.x, $$9.y, $$9.z, $$6, $$7, -16777216);
            }
            VertexConsumer $$12 = $$1.getBuffer(RenderType.lines());
            int $$13 = $$10 != false ? -11010079 : ARGB.color(102, -16777216);
            this.renderHitOutline($$2, $$12, $$0.getEntity(), $$9.x, $$9.y, $$9.z, $$6, $$7, $$13);
            $$1.endLastBatch();
        }
    }

    private void checkPoseStack(PoseStack $$0) {
        if (!$$0.isEmpty()) {
            throw new IllegalStateException("Pose stack not empty");
        }
    }

    private void renderEntity(Entity $$0, double $$1, double $$2, double $$3, float $$4, PoseStack $$5, MultiBufferSource $$6) {
        double $$7 = Mth.lerp((double)$$4, $$0.xOld, $$0.getX());
        double $$8 = Mth.lerp((double)$$4, $$0.yOld, $$0.getY());
        double $$9 = Mth.lerp((double)$$4, $$0.zOld, $$0.getZ());
        this.entityRenderDispatcher.render($$0, $$7 - $$1, $$8 - $$2, $$9 - $$3, $$4, $$5, $$6, this.entityRenderDispatcher.getPackedLightCoords($$0, $$4));
    }

    private void scheduleTranslucentSectionResort(Vec3 $$0) {
        if (this.visibleSections.isEmpty()) {
            return;
        }
        BlockPos $$1 = BlockPos.containing($$0);
        boolean $$2 = !$$1.equals(this.lastTranslucentSortBlockPos);
        Profiler.get().push("translucent_sort");
        TranslucencyPointOfView $$3 = new TranslucencyPointOfView();
        for (SectionRenderDispatcher.RenderSection $$4 : this.nearbyVisibleSections) {
            this.scheduleResort($$4, $$3, $$0, $$2, true);
        }
        this.translucencyResortIterationIndex %= this.visibleSections.size();
        int $$5 = Math.max(this.visibleSections.size() / 8, 15);
        while ($$5-- > 0) {
            int $$6 = this.translucencyResortIterationIndex++ % this.visibleSections.size();
            this.scheduleResort((SectionRenderDispatcher.RenderSection)this.visibleSections.get($$6), $$3, $$0, $$2, false);
        }
        this.lastTranslucentSortBlockPos = $$1;
        Profiler.get().pop();
    }

    private void scheduleResort(SectionRenderDispatcher.RenderSection $$0, TranslucencyPointOfView $$1, Vec3 $$2, boolean $$3, boolean $$4) {
        boolean $$6;
        $$1.set($$2, $$0.getSectionNode());
        boolean $$5 = $$0.getSectionMesh().isDifferentPointOfView($$1);
        boolean bl = $$6 = $$3 && ($$1.isAxisAligned() || $$4);
        if (($$6 || $$5) && !$$0.transparencyResortingScheduled() && $$0.hasTranslucentGeometry()) {
            $$0.resortTransparency(this.sectionRenderDispatcher);
        }
    }

    private ChunkSectionsToRender prepareChunkRenders(Matrix4fc $$0, double $$12, double $$22, double $$3) {
        ObjectListIterator $$4 = this.visibleSections.listIterator(0);
        EnumMap<ChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>> $$5 = new EnumMap<ChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>>(ChunkSectionLayer.class);
        int $$6 = 0;
        for (ChunkSectionLayer $$7 : ChunkSectionLayer.values()) {
            $$5.put($$7, new ArrayList());
        }
        ArrayList<DynamicUniforms.Transform> $$8 = new ArrayList<DynamicUniforms.Transform>();
        Vector4f $$9 = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        Matrix4f $$10 = new Matrix4f();
        while ($$4.hasNext()) {
            SectionRenderDispatcher.RenderSection $$11 = (SectionRenderDispatcher.RenderSection)$$4.next();
            SectionMesh $$122 = $$11.getSectionMesh();
            for (ChunkSectionLayer $$13 : ChunkSectionLayer.values()) {
                VertexFormat.IndexType $$18;
                GpuBuffer $$17;
                SectionBuffers $$14 = $$122.getBuffers($$13);
                if ($$14 == null) continue;
                if ($$14.getIndexBuffer() == null) {
                    if ($$14.getIndexCount() > $$6) {
                        $$6 = $$14.getIndexCount();
                    }
                    Object $$15 = null;
                    Object $$16 = null;
                } else {
                    $$17 = $$14.getIndexBuffer();
                    $$18 = $$14.getIndexType();
                }
                BlockPos $$19 = $$11.getRenderOrigin();
                int $$20 = $$8.size();
                $$8.add(new DynamicUniforms.Transform($$0, (Vector4fc)$$9, (Vector3fc)new Vector3f((float)((double)$$19.getX() - $$12), (float)((double)$$19.getY() - $$22), (float)((double)$$19.getZ() - $$3)), (Matrix4fc)$$10, 1.0f));
                $$5.get((Object)$$13).add(new RenderPass.Draw<GpuBufferSlice[]>(0, $$14.getVertexBuffer(), $$17, $$18, 0, $$14.getIndexCount(), ($$1, $$2) -> $$2.upload("DynamicTransforms", $$1[$$20])));
            }
        }
        GpuBufferSlice[] $$21 = RenderSystem.getDynamicUniforms().a($$8.toArray(new DynamicUniforms.Transform[0]));
        return new ChunkSectionsToRender($$5, $$6, $$21);
    }

    public void endFrame() {
        this.cloudRenderer.endFrame();
    }

    public void captureFrustum() {
        this.captureFrustum = true;
    }

    public void killFrustum() {
        this.capturedFrustum = null;
    }

    public void tick() {
        if (this.level.tickRateManager().runsNormally()) {
            ++this.ticks;
        }
        if (this.ticks % 20 != 0) {
            return;
        }
        ObjectIterator $$0 = this.destroyingBlocks.values().iterator();
        while ($$0.hasNext()) {
            BlockDestructionProgress $$1 = (BlockDestructionProgress)$$0.next();
            int $$2 = $$1.getUpdatedRenderTick();
            if (this.ticks - $$2 <= 400) continue;
            $$0.remove();
            this.removeProgress($$1);
        }
    }

    private void removeProgress(BlockDestructionProgress $$0) {
        long $$1 = $$0.getPos().asLong();
        Set $$2 = (Set)this.destructionProgress.get($$1);
        $$2.remove($$0);
        if ($$2.isEmpty()) {
            this.destructionProgress.remove($$1);
        }
    }

    private void addSkyPass(FrameGraphBuilder $$0, Camera $$1, float $$2, GpuBufferSlice $$3) {
        FogType $$4 = $$1.getFluidInCamera();
        if ($$4 == FogType.POWDER_SNOW || $$4 == FogType.LAVA || this.doesMobEffectBlockSky($$1)) {
            return;
        }
        DimensionSpecialEffects $$5 = this.level.effects();
        DimensionSpecialEffects.SkyType $$6 = $$5.skyType();
        if ($$6 == DimensionSpecialEffects.SkyType.NONE) {
            return;
        }
        FramePass $$7 = $$0.addPass("sky");
        this.targets.main = $$7.readsAndWrites(this.targets.main);
        $$7.executes(() -> {
            RenderSystem.setShaderFog($$3);
            if ($$6 == DimensionSpecialEffects.SkyType.END) {
                this.skyRenderer.renderEndSky();
                return;
            }
            PoseStack $$4 = new PoseStack();
            float $$5 = this.level.getSunAngle($$2);
            float $$6 = this.level.getTimeOfDay($$2);
            float $$7 = 1.0f - this.level.getRainLevel($$2);
            float $$8 = this.level.getStarBrightness($$2) * $$7;
            int $$9 = $$5.getSunriseOrSunsetColor($$6);
            int $$10 = this.level.getMoonPhase();
            int $$11 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), $$2);
            float $$12 = ARGB.redFloat($$11);
            float $$13 = ARGB.greenFloat($$11);
            float $$14 = ARGB.blueFloat($$11);
            this.skyRenderer.renderSkyDisc($$12, $$13, $$14);
            MultiBufferSource.BufferSource $$15 = this.renderBuffers.bufferSource();
            if ($$5.isSunriseOrSunset($$6)) {
                this.skyRenderer.renderSunriseAndSunset($$4, $$15, $$5, $$9);
            }
            this.skyRenderer.renderSunMoonAndStars($$4, $$15, $$6, $$10, $$7, $$8);
            $$15.endBatch();
            if (this.shouldRenderDarkDisc($$2)) {
                this.skyRenderer.renderDarkDisc();
            }
        });
    }

    private boolean shouldRenderDarkDisc(float $$0) {
        return this.minecraft.player.getEyePosition((float)$$0).y - this.level.getLevelData().getHorizonHeight(this.level) < 0.0;
    }

    private boolean doesMobEffectBlockSky(Camera $$0) {
        Entity entity = $$0.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity $$1 = (LivingEntity)entity;
            return $$1.hasEffect(MobEffects.BLINDNESS) || $$1.hasEffect(MobEffects.DARKNESS);
        }
        return false;
    }

    private void compileSections(Camera $$0) {
        ProfilerFiller $$1 = Profiler.get();
        $$1.push("populate_sections_to_compile");
        RenderRegionCache $$2 = new RenderRegionCache();
        BlockPos $$3 = $$0.getBlockPosition();
        ArrayList<SectionRenderDispatcher.RenderSection> $$4 = Lists.newArrayList();
        for (SectionRenderDispatcher.RenderSection $$5 : this.visibleSections) {
            if (!$$5.isDirty() || $$5.getSectionMesh() == CompiledSectionMesh.UNCOMPILED && !$$5.hasAllNeighbors()) continue;
            boolean $$6 = false;
            if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.NEARBY) {
                BlockPos $$7 = SectionPos.of($$5.getSectionNode()).center();
                $$6 = $$7.distSqr($$3) < 768.0 || $$5.isDirtyFromPlayer();
            } else if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.PLAYER_AFFECTED) {
                $$6 = $$5.isDirtyFromPlayer();
            }
            if ($$6) {
                $$1.push("build_near_sync");
                this.sectionRenderDispatcher.rebuildSectionSync($$5, $$2);
                $$5.setNotDirty();
                $$1.pop();
                continue;
            }
            $$4.add($$5);
        }
        $$1.popPush("upload");
        this.sectionRenderDispatcher.uploadAllPendingUploads();
        $$1.popPush("schedule_async_compile");
        for (SectionRenderDispatcher.RenderSection $$8 : $$4) {
            $$8.rebuildSectionAsync($$2);
            $$8.setNotDirty();
        }
        $$1.pop();
        this.scheduleTranslucentSectionResort($$0.getPosition());
    }

    private void renderHitOutline(PoseStack $$0, VertexConsumer $$1, Entity $$2, double $$3, double $$4, double $$5, BlockPos $$6, BlockState $$7, int $$8) {
        ShapeRenderer.renderShape($$0, $$1, $$7.getShape(this.level, $$6, CollisionContext.of($$2)), (double)$$6.getX() - $$3, (double)$$6.getY() - $$4, (double)$$6.getZ() - $$5, $$8);
    }

    public void blockChanged(BlockGetter $$0, BlockPos $$1, BlockState $$2, BlockState $$3, int $$4) {
        this.setBlockDirty($$1, ($$4 & 8) != 0);
    }

    private void setBlockDirty(BlockPos $$0, boolean $$1) {
        for (int $$2 = $$0.getZ() - 1; $$2 <= $$0.getZ() + 1; ++$$2) {
            for (int $$3 = $$0.getX() - 1; $$3 <= $$0.getX() + 1; ++$$3) {
                for (int $$4 = $$0.getY() - 1; $$4 <= $$0.getY() + 1; ++$$4) {
                    this.setSectionDirty(SectionPos.blockToSectionCoord($$3), SectionPos.blockToSectionCoord($$4), SectionPos.blockToSectionCoord($$2), $$1);
                }
            }
        }
    }

    public void setBlocksDirty(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        for (int $$6 = $$2 - 1; $$6 <= $$5 + 1; ++$$6) {
            for (int $$7 = $$0 - 1; $$7 <= $$3 + 1; ++$$7) {
                for (int $$8 = $$1 - 1; $$8 <= $$4 + 1; ++$$8) {
                    this.setSectionDirty(SectionPos.blockToSectionCoord($$7), SectionPos.blockToSectionCoord($$8), SectionPos.blockToSectionCoord($$6));
                }
            }
        }
    }

    public void setBlockDirty(BlockPos $$0, BlockState $$1, BlockState $$2) {
        if (this.minecraft.getModelManager().requiresRender($$1, $$2)) {
            this.setBlocksDirty($$0.getX(), $$0.getY(), $$0.getZ(), $$0.getX(), $$0.getY(), $$0.getZ());
        }
    }

    public void setSectionDirtyWithNeighbors(int $$0, int $$1, int $$2) {
        this.setSectionRangeDirty($$0 - 1, $$1 - 1, $$2 - 1, $$0 + 1, $$1 + 1, $$2 + 1);
    }

    public void setSectionRangeDirty(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        for (int $$6 = $$2; $$6 <= $$5; ++$$6) {
            for (int $$7 = $$0; $$7 <= $$3; ++$$7) {
                for (int $$8 = $$1; $$8 <= $$4; ++$$8) {
                    this.setSectionDirty($$7, $$8, $$6);
                }
            }
        }
    }

    public void setSectionDirty(int $$0, int $$1, int $$2) {
        this.setSectionDirty($$0, $$1, $$2, false);
    }

    private void setSectionDirty(int $$0, int $$1, int $$2, boolean $$3) {
        this.viewArea.setDirty($$0, $$1, $$2, $$3);
    }

    public void onSectionBecomingNonEmpty(long $$0) {
        SectionRenderDispatcher.RenderSection $$1 = this.viewArea.getRenderSection($$0);
        if ($$1 != null) {
            this.sectionOcclusionGraph.schedulePropagationFrom($$1);
        }
    }

    public void addParticle(ParticleOptions $$0, boolean $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        this.addParticle($$0, $$1, false, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    public void addParticle(ParticleOptions $$0, boolean $$1, boolean $$2, double $$3, double $$4, double $$5, double $$6, double $$7, double $$8) {
        try {
            this.addParticleInternal($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8);
        } catch (Throwable $$9) {
            CrashReport $$10 = CrashReport.forThrowable($$9, "Exception while adding particle");
            CrashReportCategory $$11 = $$10.addCategory("Particle being added");
            $$11.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey($$0.getType()));
            $$11.setDetail("Parameters", () -> ParticleTypes.CODEC.encodeStart(this.level.registryAccess().createSerializationContext(NbtOps.INSTANCE), (Object)$$0).toString());
            $$11.setDetail("Position", () -> CrashReportCategory.formatLocation((LevelHeightAccessor)this.level, $$3, $$4, $$5));
            throw new ReportedException($$10);
        }
    }

    public <T extends ParticleOptions> void addParticle(T $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        this.addParticle($$0, $$0.getType().getOverrideLimiter(), $$1, $$2, $$3, $$4, $$5, $$6);
    }

    @Nullable
    Particle addParticleInternal(ParticleOptions $$0, boolean $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
        return this.addParticleInternal($$0, $$1, false, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Nullable
    private Particle addParticleInternal(ParticleOptions $$0, boolean $$1, boolean $$2, double $$3, double $$4, double $$5, double $$6, double $$7, double $$8) {
        Camera $$9 = this.minecraft.gameRenderer.getMainCamera();
        ParticleStatus $$10 = this.calculateParticleLevel($$2);
        if ($$1) {
            return this.minecraft.particleEngine.createParticle($$0, $$3, $$4, $$5, $$6, $$7, $$8);
        }
        if ($$9.getPosition().distanceToSqr($$3, $$4, $$5) > 1024.0) {
            return null;
        }
        if ($$10 == ParticleStatus.MINIMAL) {
            return null;
        }
        return this.minecraft.particleEngine.createParticle($$0, $$3, $$4, $$5, $$6, $$7, $$8);
    }

    private ParticleStatus calculateParticleLevel(boolean $$0) {
        ParticleStatus $$1 = this.minecraft.options.particles().get();
        if ($$0 && $$1 == ParticleStatus.MINIMAL && this.level.random.nextInt(10) == 0) {
            $$1 = ParticleStatus.DECREASED;
        }
        if ($$1 == ParticleStatus.DECREASED && this.level.random.nextInt(3) == 0) {
            $$1 = ParticleStatus.MINIMAL;
        }
        return $$1;
    }

    public void destroyBlockProgress(int $$02, BlockPos $$1, int $$2) {
        if ($$2 < 0 || $$2 >= 10) {
            BlockDestructionProgress $$3 = (BlockDestructionProgress)this.destroyingBlocks.remove($$02);
            if ($$3 != null) {
                this.removeProgress($$3);
            }
        } else {
            BlockDestructionProgress $$4 = (BlockDestructionProgress)this.destroyingBlocks.get($$02);
            if ($$4 != null) {
                this.removeProgress($$4);
            }
            if ($$4 == null || $$4.getPos().getX() != $$1.getX() || $$4.getPos().getY() != $$1.getY() || $$4.getPos().getZ() != $$1.getZ()) {
                $$4 = new BlockDestructionProgress($$02, $$1);
                this.destroyingBlocks.put($$02, (Object)$$4);
            }
            $$4.setProgress($$2);
            $$4.updateTick(this.ticks);
            ((SortedSet)this.destructionProgress.computeIfAbsent($$4.getPos().asLong(), $$0 -> Sets.newTreeSet())).add($$4);
        }
    }

    public boolean hasRenderedAllSections() {
        return this.sectionRenderDispatcher.isQueueEmpty();
    }

    public void onChunkReadyToRender(ChunkPos $$0) {
        this.sectionOcclusionGraph.onChunkReadyToRender($$0);
    }

    public void needsUpdate() {
        this.sectionOcclusionGraph.invalidate();
        this.cloudRenderer.markForRebuild();
    }

    public static int getLightColor(BlockAndTintGetter $$0, BlockPos $$1) {
        return LevelRenderer.getLightColor(BrightnessGetter.DEFAULT, $$0, $$0.getBlockState($$1), $$1);
    }

    public static int getLightColor(BrightnessGetter $$0, BlockAndTintGetter $$1, BlockState $$2, BlockPos $$3) {
        int $$6;
        if ($$2.emissiveRendering($$1, $$3)) {
            return 0xF000F0;
        }
        int $$4 = $$0.packedBrightness($$1, $$3);
        int $$5 = LightTexture.block($$4);
        if ($$5 < ($$6 = $$2.getLightEmission())) {
            int $$7 = LightTexture.sky($$4);
            return LightTexture.pack($$6, $$7);
        }
        return $$4;
    }

    public boolean isSectionCompiled(BlockPos $$0) {
        SectionRenderDispatcher.RenderSection $$1 = this.viewArea.getRenderSectionAt($$0);
        return $$1 != null && $$1.sectionMesh.get() != CompiledSectionMesh.UNCOMPILED;
    }

    @Nullable
    public RenderTarget entityOutlineTarget() {
        return this.targets.entityOutline != null ? this.targets.entityOutline.get() : null;
    }

    @Nullable
    public RenderTarget getTranslucentTarget() {
        return this.targets.translucent != null ? this.targets.translucent.get() : null;
    }

    @Nullable
    public RenderTarget getItemEntityTarget() {
        return this.targets.itemEntity != null ? this.targets.itemEntity.get() : null;
    }

    @Nullable
    public RenderTarget getParticlesTarget() {
        return this.targets.particles != null ? this.targets.particles.get() : null;
    }

    @Nullable
    public RenderTarget getWeatherTarget() {
        return this.targets.weather != null ? this.targets.weather.get() : null;
    }

    @Nullable
    public RenderTarget getCloudsTarget() {
        return this.targets.clouds != null ? this.targets.clouds.get() : null;
    }

    @VisibleForDebug
    public ObjectArrayList<SectionRenderDispatcher.RenderSection> getVisibleSections() {
        return this.visibleSections;
    }

    @VisibleForDebug
    public SectionOcclusionGraph getSectionOcclusionGraph() {
        return this.sectionOcclusionGraph;
    }

    @Nullable
    public Frustum getCapturedFrustum() {
        return this.capturedFrustum;
    }

    public CloudRenderer getCloudRenderer() {
        return this.cloudRenderer;
    }

    @FunctionalInterface
    public static interface BrightnessGetter {
        public static final BrightnessGetter DEFAULT = ($$0, $$1) -> {
            int $$2 = $$0.getBrightness(LightLayer.SKY, $$1);
            int $$3 = $$0.getBrightness(LightLayer.BLOCK, $$1);
            return Brightness.pack($$3, $$2);
        };

        public int packedBrightness(BlockAndTintGetter var1, BlockPos var2);
    }
}

