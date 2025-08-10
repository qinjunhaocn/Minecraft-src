/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.gui.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.pip.OversizedItemRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GlyphEffectRenderState;
import net.minecraft.client.gui.render.state.GlyphRenderState;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.OversizedItemRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class GuiRenderer
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float MAX_GUI_Z = 10000.0f;
    private static final float MIN_GUI_Z = 0.0f;
    private static final float GUI_Z_NEAR = 1000.0f;
    public static final int GUI_3D_Z_FAR = 1000;
    public static final int GUI_3D_Z_NEAR = -1000;
    public static final int DEFAULT_ITEM_SIZE = 16;
    private static final int MINIMUM_ITEM_ATLAS_SIZE = 512;
    private static final int MAXIMUM_ITEM_ATLAS_SIZE = RenderSystem.getDevice().getMaxTextureSize();
    public static final int CLEAR_COLOR = 0;
    private static final Comparator<ScreenRectangle> SCISSOR_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(ScreenRectangle::top).thenComparing(ScreenRectangle::bottom).thenComparing(ScreenRectangle::left).thenComparing(ScreenRectangle::right));
    private static final Comparator<TextureSetup> TEXTURE_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(TextureSetup::getSortKey));
    private static final Comparator<GuiElementRenderState> ELEMENT_SORT_COMPARATOR = Comparator.comparing(GuiElementRenderState::scissorArea, SCISSOR_COMPARATOR).thenComparing(GuiElementRenderState::pipeline, Comparator.comparing(RenderPipeline::getSortKey)).thenComparing(GuiElementRenderState::textureSetup, TEXTURE_COMPARATOR);
    private final Map<Object, AtlasPosition> atlasPositions = new Object2ObjectOpenHashMap();
    private final Map<Object, OversizedItemRenderer> oversizedItemRenderers = new Object2ObjectOpenHashMap();
    final GuiRenderState renderState;
    private final List<Draw> draws = new ArrayList<Draw>();
    private final List<MeshToDraw> meshesToDraw = new ArrayList<MeshToDraw>();
    private final ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(786432);
    private final Map<VertexFormat, MappableRingBuffer> vertexBuffers = new Object2ObjectOpenHashMap();
    private int firstDrawIndexAfterBlur = Integer.MAX_VALUE;
    private final CachedOrthoProjectionMatrixBuffer guiProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("gui", 1000.0f, 11000.0f, true);
    private final CachedOrthoProjectionMatrixBuffer itemsProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("items", -1000.0f, 1000.0f, true);
    private final MultiBufferSource.BufferSource bufferSource;
    private final Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> pictureInPictureRenderers;
    @Nullable
    private GpuTexture itemsAtlas;
    @Nullable
    private GpuTextureView itemsAtlasView;
    @Nullable
    private GpuTexture itemsAtlasDepth;
    @Nullable
    private GpuTextureView itemsAtlasDepthView;
    private int itemAtlasX;
    private int itemAtlasY;
    private int cachedGuiScale;
    private int frameNumber;
    @Nullable
    private ScreenRectangle previousScissorArea = null;
    @Nullable
    private RenderPipeline previousPipeline = null;
    @Nullable
    private TextureSetup previousTextureSetup = null;
    @Nullable
    private BufferBuilder bufferBuilder = null;

    public GuiRenderer(GuiRenderState $$0, MultiBufferSource.BufferSource $$1, List<PictureInPictureRenderer<?>> $$2) {
        this.renderState = $$0;
        this.bufferSource = $$1;
        ImmutableMap.Builder $$3 = ImmutableMap.builder();
        for (PictureInPictureRenderer<?> $$4 : $$2) {
            $$3.put($$4.getRenderStateClass(), $$4);
        }
        this.pictureInPictureRenderers = $$3.buildOrThrow();
    }

    public void incrementFrameNumber() {
        ++this.frameNumber;
    }

    public void render(GpuBufferSlice $$0) {
        this.prepare();
        this.draw($$0);
        for (MappableRingBuffer $$1 : this.vertexBuffers.values()) {
            $$1.rotate();
        }
        this.draws.clear();
        this.meshesToDraw.clear();
        this.renderState.reset();
        this.firstDrawIndexAfterBlur = Integer.MAX_VALUE;
        this.clearUnusedOversizedItemRenderers();
    }

    private void clearUnusedOversizedItemRenderers() {
        Iterator<Map.Entry<Object, OversizedItemRenderer>> $$0 = this.oversizedItemRenderers.entrySet().iterator();
        while ($$0.hasNext()) {
            Map.Entry<Object, OversizedItemRenderer> $$1 = $$0.next();
            OversizedItemRenderer $$2 = $$1.getValue();
            if (!$$2.usedOnThisFrame()) {
                $$2.close();
                $$0.remove();
                continue;
            }
            $$2.resetUsedOnThisFrame();
        }
    }

    private void prepare() {
        this.bufferSource.endBatch();
        this.preparePictureInPicture();
        this.prepareItemElements();
        this.prepareText();
        this.renderState.sortElements(ELEMENT_SORT_COMPARATOR);
        this.addElementsToMeshes(GuiRenderState.TraverseRange.BEFORE_BLUR);
        this.firstDrawIndexAfterBlur = this.meshesToDraw.size();
        this.addElementsToMeshes(GuiRenderState.TraverseRange.AFTER_BLUR);
        this.recordDraws();
    }

    private void addElementsToMeshes(GuiRenderState.TraverseRange $$0) {
        this.previousScissorArea = null;
        this.previousPipeline = null;
        this.previousTextureSetup = null;
        this.bufferBuilder = null;
        this.renderState.forEachElement(this::addElementToMesh, $$0);
        if (this.bufferBuilder != null) {
            this.recordMesh(this.bufferBuilder, this.previousPipeline, this.previousTextureSetup, this.previousScissorArea);
        }
    }

    private void draw(GpuBufferSlice $$0) {
        if (this.draws.isEmpty()) {
            return;
        }
        Minecraft $$1 = Minecraft.getInstance();
        Window $$2 = $$1.getWindow();
        RenderSystem.setProjectionMatrix(this.guiProjectionMatrixBuffer.getBuffer((float)$$2.getWidth() / (float)$$2.getGuiScale(), (float)$$2.getHeight() / (float)$$2.getGuiScale()), ProjectionType.ORTHOGRAPHIC);
        RenderTarget $$3 = $$1.getMainRenderTarget();
        int $$4 = 0;
        for (Draw $$5 : this.draws) {
            if ($$5.indexCount <= $$4) continue;
            $$4 = $$5.indexCount;
        }
        RenderSystem.AutoStorageIndexBuffer $$6 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer $$7 = $$6.getBuffer($$4);
        VertexFormat.IndexType $$8 = $$6.type();
        GpuBufferSlice $$9 = RenderSystem.getDynamicUniforms().writeTransform((Matrix4fc)new Matrix4f().setTranslation(0.0f, 0.0f, -11000.0f), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        if (this.firstDrawIndexAfterBlur > 0) {
            this.executeDrawRange(() -> "GUI before blur", $$3, $$0, $$9, $$7, $$8, 0, Math.min(this.firstDrawIndexAfterBlur, this.draws.size()));
        }
        if (this.draws.size() <= this.firstDrawIndexAfterBlur) {
            return;
        }
        RenderSystem.getDevice().createCommandEncoder().clearDepthTexture($$3.getDepthTexture(), 1.0);
        $$1.gameRenderer.processBlurEffect();
        this.executeDrawRange(() -> "GUI after blur", $$3, $$0, $$9, $$7, $$8, this.firstDrawIndexAfterBlur, this.draws.size());
    }

    private void executeDrawRange(Supplier<String> $$0, RenderTarget $$1, GpuBufferSlice $$2, GpuBufferSlice $$3, GpuBuffer $$4, VertexFormat.IndexType $$5, int $$6, int $$7) {
        try (RenderPass $$8 = RenderSystem.getDevice().createCommandEncoder().createRenderPass($$0, $$1.getColorTextureView(), OptionalInt.empty(), $$1.useDepth ? $$1.getDepthTextureView() : null, OptionalDouble.empty());){
            RenderSystem.bindDefaultUniforms($$8);
            $$8.setUniform("Fog", $$2);
            $$8.setUniform("DynamicTransforms", $$3);
            for (int $$9 = $$6; $$9 < $$7; ++$$9) {
                Draw $$10 = this.draws.get($$9);
                this.executeDraw($$10, $$8, $$4, $$5);
            }
        }
    }

    private void addElementToMesh(GuiElementRenderState $$0, int $$1) {
        RenderPipeline $$2 = $$0.pipeline();
        TextureSetup $$3 = $$0.textureSetup();
        ScreenRectangle $$4 = $$0.scissorArea();
        if ($$2 != this.previousPipeline || this.scissorChanged($$4, this.previousScissorArea) || !$$3.equals((Object)this.previousTextureSetup)) {
            if (this.bufferBuilder != null) {
                this.recordMesh(this.bufferBuilder, this.previousPipeline, this.previousTextureSetup, this.previousScissorArea);
            }
            this.bufferBuilder = this.getBufferBuilder($$2);
            this.previousPipeline = $$2;
            this.previousTextureSetup = $$3;
            this.previousScissorArea = $$4;
        }
        $$0.buildVertices(this.bufferBuilder, 0.0f + (float)$$1);
    }

    private void prepareText() {
        this.renderState.forEachText($$0 -> {
            final Matrix3x2f $$1 = $$0.pose;
            final ScreenRectangle $$2 = $$0.scissor;
            $$0.ensurePrepared().visit(new Font.GlyphVisitor(){

                @Override
                public void acceptGlyph(BakedGlyph.GlyphInstance $$0) {
                    if ($$0.glyph().textureView() != null) {
                        GuiRenderer.this.renderState.submitGlyphToCurrentLayer(new GlyphRenderState($$1, $$0, $$2));
                    }
                }

                @Override
                public void acceptEffect(BakedGlyph $$0, BakedGlyph.Effect $$12) {
                    if ($$0.textureView() != null) {
                        GuiRenderer.this.renderState.submitGlyphToCurrentLayer(new GlyphEffectRenderState($$1, $$0, $$12, $$2));
                    }
                }
            });
        });
    }

    private void prepareItemElements() {
        if (this.renderState.getItemModelIdentities().isEmpty()) {
            return;
        }
        int $$0 = this.getGuiScaleInvalidatingItemAtlasIfChanged();
        int $$12 = 16 * $$0;
        int $$2 = this.calculateAtlasSizeInPixels($$12);
        if (this.itemsAtlas == null) {
            this.createAtlasTextures($$2);
        }
        RenderSystem.outputColorTextureOverride = this.itemsAtlasView;
        RenderSystem.outputDepthTextureOverride = this.itemsAtlasDepthView;
        RenderSystem.setProjectionMatrix(this.itemsProjectionMatrixBuffer.getBuffer($$2, $$2), ProjectionType.ORTHOGRAPHIC);
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        PoseStack $$3 = new PoseStack();
        MutableBoolean $$4 = new MutableBoolean(false);
        MutableBoolean $$52 = new MutableBoolean(false);
        this.renderState.forEachItem($$5 -> {
            int $$10;
            boolean $$8;
            if ($$5.oversizedItemBounds() != null) {
                $$52.setTrue();
                return;
            }
            TrackingItemStackRenderState $$6 = $$5.itemStackRenderState();
            AtlasPosition $$7 = this.atlasPositions.get($$6.getModelIdentity());
            if (!($$7 == null || $$6.isAnimated() && $$7.lastAnimatedOnFrame != this.frameNumber)) {
                this.submitBlitFromItemAtlas((GuiItemRenderState)$$5, $$7.u, $$7.v, $$12, $$2);
                return;
            }
            if (this.itemAtlasX + $$12 > $$2) {
                this.itemAtlasX = 0;
                this.itemAtlasY += $$12;
            }
            boolean bl = $$8 = $$6.isAnimated() && $$7 != null;
            if (!$$8 && this.itemAtlasY + $$12 > $$2) {
                if ($$4.isFalse()) {
                    LOGGER.warn("Trying to render too many items in GUI at the same time. Skipping some of them.");
                    $$4.setTrue();
                }
                return;
            }
            int $$9 = $$8 ? $$7.x : this.itemAtlasX;
            int n = $$10 = $$8 ? $$7.y : this.itemAtlasY;
            if ($$8) {
                RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.itemsAtlas, 0, this.itemsAtlasDepth, 1.0, $$9, $$2 - $$10 - $$12, $$12, $$12);
            }
            this.renderItemToAtlas($$6, $$3, $$9, $$10, $$12);
            float $$11 = (float)$$9 / (float)$$2;
            float $$12 = (float)($$2 - $$10) / (float)$$2;
            this.submitBlitFromItemAtlas((GuiItemRenderState)$$5, $$11, $$12, $$12, $$2);
            if ($$8) {
                $$7.lastAnimatedOnFrame = this.frameNumber;
            } else {
                this.atlasPositions.put($$5.itemStackRenderState().getModelIdentity(), new AtlasPosition(this.itemAtlasX, this.itemAtlasY, $$11, $$12, this.frameNumber));
                this.itemAtlasX += $$12;
            }
        });
        RenderSystem.outputColorTextureOverride = null;
        RenderSystem.outputDepthTextureOverride = null;
        if ($$52.getValue().booleanValue()) {
            this.renderState.forEachItem($$1 -> {
                if ($$1.oversizedItemBounds() != null) {
                    TrackingItemStackRenderState $$2 = $$1.itemStackRenderState();
                    OversizedItemRenderer $$3 = this.oversizedItemRenderers.computeIfAbsent($$2.getModelIdentity(), $$0 -> new OversizedItemRenderer(this.bufferSource));
                    ScreenRectangle $$4 = $$1.oversizedItemBounds();
                    OversizedItemRenderState $$5 = new OversizedItemRenderState((GuiItemRenderState)$$1, $$4.left(), $$4.top(), $$4.right(), $$4.bottom());
                    $$3.prepare($$5, this.renderState, $$0);
                }
            });
        }
    }

    private void preparePictureInPicture() {
        int $$0 = Minecraft.getInstance().getWindow().getGuiScale();
        this.renderState.forEachPictureInPicture($$1 -> this.preparePictureInPictureState($$1, $$0));
    }

    private <T extends PictureInPictureRenderState> void preparePictureInPictureState(T $$0, int $$1) {
        PictureInPictureRenderer<?> $$2 = this.pictureInPictureRenderers.get($$0.getClass());
        if ($$2 != null) {
            $$2.prepare($$0, this.renderState, $$1);
        }
    }

    private void renderItemToAtlas(TrackingItemStackRenderState $$0, PoseStack $$1, int $$2, int $$3, int $$4) {
        boolean $$5;
        $$1.pushPose();
        $$1.translate((float)$$2 + (float)$$4 / 2.0f, (float)$$3 + (float)$$4 / 2.0f, 0.0f);
        $$1.scale($$4, -$$4, $$4);
        boolean bl = $$5 = !$$0.usesBlockLight();
        if ($$5) {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        } else {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        }
        RenderSystem.enableScissorForRenderTypeDraws($$2, this.itemsAtlas.getHeight(0) - $$3 - $$4, $$4, $$4);
        $$0.render($$1, this.bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);
        this.bufferSource.endBatch();
        RenderSystem.disableScissorForRenderTypeDraws();
        $$1.popPose();
    }

    private void submitBlitFromItemAtlas(GuiItemRenderState $$0, float $$1, float $$2, int $$3, int $$4) {
        float $$5 = $$1 + (float)$$3 / (float)$$4;
        float $$6 = $$2 + (float)(-$$3) / (float)$$4;
        this.renderState.submitBlitToCurrentLayer(new BlitRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.singleTexture(this.itemsAtlasView), $$0.pose(), $$0.x(), $$0.y(), $$0.x() + 16, $$0.y() + 16, $$1, $$5, $$2, $$6, -1, $$0.scissorArea(), null));
    }

    private void createAtlasTextures(int $$0) {
        GpuDevice $$1 = RenderSystem.getDevice();
        this.itemsAtlas = $$1.createTexture("UI items atlas", 12, TextureFormat.RGBA8, $$0, $$0, 1, 1);
        this.itemsAtlas.setTextureFilter(FilterMode.NEAREST, false);
        this.itemsAtlasView = $$1.createTextureView(this.itemsAtlas);
        this.itemsAtlasDepth = $$1.createTexture("UI items atlas depth", 8, TextureFormat.DEPTH32, $$0, $$0, 1, 1);
        this.itemsAtlasDepthView = $$1.createTextureView(this.itemsAtlasDepth);
        $$1.createCommandEncoder().clearColorAndDepthTextures(this.itemsAtlas, 0, this.itemsAtlasDepth, 1.0);
    }

    private int calculateAtlasSizeInPixels(int $$0) {
        int $$3;
        Set<Object> $$1 = this.renderState.getItemModelIdentities();
        if (this.atlasPositions.isEmpty()) {
            int $$2 = $$1.size();
        } else {
            $$3 = this.atlasPositions.size();
            for (Object $$4 : $$1) {
                if (this.atlasPositions.containsKey($$4)) continue;
                ++$$3;
            }
        }
        if (this.itemsAtlas != null) {
            int $$5 = this.itemsAtlas.getWidth(0) / $$0;
            int $$6 = $$5 * $$5;
            if ($$3 < $$6) {
                return this.itemsAtlas.getWidth(0);
            }
            this.invalidateItemAtlas();
        }
        int $$7 = $$1.size();
        int $$8 = Mth.smallestSquareSide($$7 + $$7 / 2);
        return Math.clamp((long)Mth.smallestEncompassingPowerOfTwo($$8 * $$0), (int)512, (int)MAXIMUM_ITEM_ATLAS_SIZE);
    }

    private int getGuiScaleInvalidatingItemAtlasIfChanged() {
        int $$0 = Minecraft.getInstance().getWindow().getGuiScale();
        if ($$0 != this.cachedGuiScale) {
            this.invalidateItemAtlas();
            for (OversizedItemRenderer $$1 : this.oversizedItemRenderers.values()) {
                $$1.invalidateTexture();
            }
            this.cachedGuiScale = $$0;
        }
        return $$0;
    }

    private void invalidateItemAtlas() {
        this.itemAtlasX = 0;
        this.itemAtlasY = 0;
        this.atlasPositions.clear();
        if (this.itemsAtlas != null) {
            this.itemsAtlas.close();
            this.itemsAtlas = null;
        }
        if (this.itemsAtlasView != null) {
            this.itemsAtlasView.close();
            this.itemsAtlasView = null;
        }
        if (this.itemsAtlasDepth != null) {
            this.itemsAtlasDepth.close();
            this.itemsAtlasDepth = null;
        }
        if (this.itemsAtlasDepthView != null) {
            this.itemsAtlasDepthView.close();
            this.itemsAtlasDepthView = null;
        }
    }

    private void recordMesh(BufferBuilder $$0, RenderPipeline $$1, TextureSetup $$2, @Nullable ScreenRectangle $$3) {
        MeshData $$4 = $$0.buildOrThrow();
        this.meshesToDraw.add(new MeshToDraw($$4, $$1, $$2, $$3));
    }

    private void recordDraws() {
        this.ensureVertexBufferSizes();
        CommandEncoder $$0 = RenderSystem.getDevice().createCommandEncoder();
        Object2IntOpenHashMap $$1 = new Object2IntOpenHashMap();
        for (MeshToDraw $$2 : this.meshesToDraw) {
            MeshData $$3 = $$2.mesh;
            MeshData.DrawState $$4 = $$3.drawState();
            VertexFormat $$5 = $$4.format();
            MappableRingBuffer $$6 = this.vertexBuffers.get($$5);
            if (!$$1.containsKey((Object)$$5)) {
                $$1.put((Object)$$5, 0);
            }
            ByteBuffer $$7 = $$3.vertexBuffer();
            int $$8 = $$7.remaining();
            int $$9 = $$1.getInt((Object)$$5);
            try (GpuBuffer.MappedView $$10 = $$0.mapBuffer($$6.currentBuffer().slice($$9, $$8), false, true);){
                MemoryUtil.memCopy((ByteBuffer)$$7, (ByteBuffer)$$10.data());
            }
            $$1.put((Object)$$5, $$9 + $$8);
            this.draws.add(new Draw($$6.currentBuffer(), $$9 / $$5.getVertexSize(), $$4.mode(), $$4.indexCount(), $$2.pipeline, $$2.textureSetup, $$2.scissorArea));
            $$2.close();
        }
    }

    private void ensureVertexBufferSizes() {
        Object2IntMap<VertexFormat> $$0 = this.calculatedRequiredVertexBufferSizes();
        for (Object2IntMap.Entry $$1 : $$0.object2IntEntrySet()) {
            VertexFormat $$2 = (VertexFormat)$$1.getKey();
            int $$3 = $$1.getIntValue();
            MappableRingBuffer $$4 = this.vertexBuffers.get($$2);
            if ($$4 != null && $$4.size() >= $$3) continue;
            if ($$4 != null) {
                $$4.close();
            }
            this.vertexBuffers.put($$2, new MappableRingBuffer(() -> "GUI vertex buffer for " + String.valueOf($$2), 34, $$3));
        }
    }

    private Object2IntMap<VertexFormat> calculatedRequiredVertexBufferSizes() {
        Object2IntOpenHashMap $$0 = new Object2IntOpenHashMap();
        for (MeshToDraw $$1 : this.meshesToDraw) {
            MeshData.DrawState $$2 = $$1.mesh.drawState();
            VertexFormat $$3 = $$2.format();
            if (!$$0.containsKey((Object)$$3)) {
                $$0.put((Object)$$3, 0);
            }
            $$0.put((Object)$$3, $$0.getInt((Object)$$3) + $$2.vertexCount() * $$3.getVertexSize());
        }
        return $$0;
    }

    private void executeDraw(Draw $$0, RenderPass $$1, GpuBuffer $$2, VertexFormat.IndexType $$3) {
        RenderPipeline $$4 = $$0.pipeline();
        $$1.setPipeline($$4);
        $$1.setVertexBuffer(0, $$0.vertexBuffer);
        ScreenRectangle $$5 = $$0.scissorArea();
        if ($$5 != null) {
            this.enableScissor($$5, $$1);
        } else {
            $$1.disableScissor();
        }
        if ($$0.textureSetup.texure0() != null) {
            $$1.bindSampler("Sampler0", $$0.textureSetup.texure0());
        }
        if ($$0.textureSetup.texure1() != null) {
            $$1.bindSampler("Sampler1", $$0.textureSetup.texure1());
        }
        if ($$0.textureSetup.texure2() != null) {
            $$1.bindSampler("Sampler2", $$0.textureSetup.texure2());
        }
        $$1.setIndexBuffer($$2, $$3);
        $$1.drawIndexed($$0.baseVertex, 0, $$0.indexCount, 1);
    }

    private BufferBuilder getBufferBuilder(RenderPipeline $$0) {
        return new BufferBuilder(this.byteBufferBuilder, $$0.getVertexFormatMode(), $$0.getVertexFormat());
    }

    private boolean scissorChanged(@Nullable ScreenRectangle $$0, @Nullable ScreenRectangle $$1) {
        if ($$0 == $$1) {
            return false;
        }
        if ($$0 != null) {
            return !$$0.equals((Object)$$1);
        }
        return true;
    }

    private void enableScissor(ScreenRectangle $$0, RenderPass $$1) {
        Window $$2 = Minecraft.getInstance().getWindow();
        int $$3 = $$2.getHeight();
        int $$4 = $$2.getGuiScale();
        double $$5 = $$0.left() * $$4;
        double $$6 = $$3 - $$0.bottom() * $$4;
        double $$7 = $$0.width() * $$4;
        double $$8 = $$0.height() * $$4;
        $$1.enableScissor((int)$$5, (int)$$6, Math.max(0, (int)$$7), Math.max(0, (int)$$8));
    }

    @Override
    public void close() {
        this.byteBufferBuilder.close();
        if (this.itemsAtlas != null) {
            this.itemsAtlas.close();
        }
        if (this.itemsAtlasView != null) {
            this.itemsAtlasView.close();
        }
        if (this.itemsAtlasDepth != null) {
            this.itemsAtlasDepth.close();
        }
        if (this.itemsAtlasDepthView != null) {
            this.itemsAtlasDepthView.close();
        }
        this.pictureInPictureRenderers.values().forEach(PictureInPictureRenderer::close);
        this.guiProjectionMatrixBuffer.close();
        this.itemsProjectionMatrixBuffer.close();
        for (MappableRingBuffer $$0 : this.vertexBuffers.values()) {
            $$0.close();
        }
        this.oversizedItemRenderers.values().forEach(PictureInPictureRenderer::close);
    }

    static final class Draw
    extends Record {
        final GpuBuffer vertexBuffer;
        final int baseVertex;
        private final VertexFormat.Mode mode;
        final int indexCount;
        private final RenderPipeline pipeline;
        final TextureSetup textureSetup;
        @Nullable
        private final ScreenRectangle scissorArea;

        Draw(GpuBuffer $$0, int $$1, VertexFormat.Mode $$2, int $$3, RenderPipeline $$4, TextureSetup $$5, @Nullable ScreenRectangle $$6) {
            this.vertexBuffer = $$0;
            this.baseVertex = $$1;
            this.mode = $$2;
            this.indexCount = $$3;
            this.pipeline = $$4;
            this.textureSetup = $$5;
            this.scissorArea = $$6;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Draw.class, "vertexBuffer;baseVertex;mode;indexCount;pipeline;textureSetup;scissorArea", "vertexBuffer", "baseVertex", "mode", "indexCount", "pipeline", "textureSetup", "scissorArea"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Draw.class, "vertexBuffer;baseVertex;mode;indexCount;pipeline;textureSetup;scissorArea", "vertexBuffer", "baseVertex", "mode", "indexCount", "pipeline", "textureSetup", "scissorArea"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Draw.class, "vertexBuffer;baseVertex;mode;indexCount;pipeline;textureSetup;scissorArea", "vertexBuffer", "baseVertex", "mode", "indexCount", "pipeline", "textureSetup", "scissorArea"}, this, $$0);
        }

        public GpuBuffer vertexBuffer() {
            return this.vertexBuffer;
        }

        public int baseVertex() {
            return this.baseVertex;
        }

        public VertexFormat.Mode mode() {
            return this.mode;
        }

        public int indexCount() {
            return this.indexCount;
        }

        public RenderPipeline pipeline() {
            return this.pipeline;
        }

        public TextureSetup textureSetup() {
            return this.textureSetup;
        }

        @Nullable
        public ScreenRectangle scissorArea() {
            return this.scissorArea;
        }
    }

    static final class MeshToDraw
    extends Record
    implements AutoCloseable {
        final MeshData mesh;
        final RenderPipeline pipeline;
        final TextureSetup textureSetup;
        @Nullable
        final ScreenRectangle scissorArea;

        MeshToDraw(MeshData $$0, RenderPipeline $$1, TextureSetup $$2, @Nullable ScreenRectangle $$3) {
            this.mesh = $$0;
            this.pipeline = $$1;
            this.textureSetup = $$2;
            this.scissorArea = $$3;
        }

        @Override
        public void close() {
            this.mesh.close();
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{MeshToDraw.class, "mesh;pipeline;textureSetup;scissorArea", "mesh", "pipeline", "textureSetup", "scissorArea"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MeshToDraw.class, "mesh;pipeline;textureSetup;scissorArea", "mesh", "pipeline", "textureSetup", "scissorArea"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MeshToDraw.class, "mesh;pipeline;textureSetup;scissorArea", "mesh", "pipeline", "textureSetup", "scissorArea"}, this, $$0);
        }

        public MeshData mesh() {
            return this.mesh;
        }

        public RenderPipeline pipeline() {
            return this.pipeline;
        }

        public TextureSetup textureSetup() {
            return this.textureSetup;
        }

        @Nullable
        public ScreenRectangle scissorArea() {
            return this.scissorArea;
        }
    }

    static final class AtlasPosition {
        final int x;
        final int y;
        final float u;
        final float v;
        int lastAnimatedOnFrame;

        AtlasPosition(int $$0, int $$1, float $$2, float $$3, int $$4) {
            this.x = $$0;
            this.y = $$1;
            this.u = $$2;
            this.v = $$3;
            this.lastAnimatedOnFrame = $$4;
        }
    }
}

