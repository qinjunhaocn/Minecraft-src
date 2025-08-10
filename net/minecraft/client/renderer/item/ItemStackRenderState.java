/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ItemStackRenderState {
    ItemDisplayContext displayContext = ItemDisplayContext.NONE;
    private int activeLayerCount;
    private boolean animated;
    private boolean oversizedInGui;
    @Nullable
    private AABB cachedModelBoundingBox;
    private LayerRenderState[] layers = new LayerRenderState[]{new LayerRenderState()};

    public void ensureCapacity(int $$0) {
        int $$2 = this.activeLayerCount + $$0;
        int $$1 = this.layers.length;
        if ($$2 > $$1) {
            this.layers = Arrays.copyOf(this.layers, $$2);
            for (int $$3 = $$1; $$3 < $$2; ++$$3) {
                this.layers[$$3] = new LayerRenderState();
            }
        }
    }

    public LayerRenderState newLayer() {
        this.ensureCapacity(1);
        return this.layers[this.activeLayerCount++];
    }

    public void clear() {
        this.displayContext = ItemDisplayContext.NONE;
        for (int $$0 = 0; $$0 < this.activeLayerCount; ++$$0) {
            this.layers[$$0].clear();
        }
        this.activeLayerCount = 0;
        this.animated = false;
        this.oversizedInGui = false;
        this.cachedModelBoundingBox = null;
    }

    public void setAnimated() {
        this.animated = true;
    }

    public boolean isAnimated() {
        return this.animated;
    }

    public void appendModelIdentityElement(Object $$0) {
    }

    private LayerRenderState firstLayer() {
        return this.layers[0];
    }

    public boolean isEmpty() {
        return this.activeLayerCount == 0;
    }

    public boolean usesBlockLight() {
        return this.firstLayer().usesBlockLight;
    }

    @Nullable
    public TextureAtlasSprite pickParticleIcon(RandomSource $$0) {
        if (this.activeLayerCount == 0) {
            return null;
        }
        return this.layers[$$0.nextInt((int)this.activeLayerCount)].particleIcon;
    }

    public void visitExtents(Consumer<Vector3fc> $$0) {
        Vector3f $$1 = new Vector3f();
        PoseStack.Pose $$2 = new PoseStack.Pose();
        for (int $$3 = 0; $$3 < this.activeLayerCount; ++$$3) {
            Vector3f[] $$6;
            LayerRenderState $$4 = this.layers[$$3];
            $$4.transform.apply(this.displayContext.leftHand(), $$2);
            Matrix4f $$5 = $$2.pose();
            for (Vector3f $$7 : $$6 = $$4.extents.get()) {
                $$0.accept((Vector3fc)$$1.set((Vector3fc)$$7).mulPosition((Matrix4fc)$$5));
            }
            $$2.setIdentity();
        }
    }

    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3) {
        for (int $$4 = 0; $$4 < this.activeLayerCount; ++$$4) {
            this.layers[$$4].render($$0, $$1, $$2, $$3);
        }
    }

    public AABB getModelBoundingBox() {
        AABB $$1;
        if (this.cachedModelBoundingBox != null) {
            return this.cachedModelBoundingBox;
        }
        AABB.Builder $$0 = new AABB.Builder();
        this.visitExtents($$0::include);
        this.cachedModelBoundingBox = $$1 = $$0.build();
        return $$1;
    }

    public void setOversizedInGui(boolean $$0) {
        this.oversizedInGui = $$0;
    }

    public boolean isOversizedInGui() {
        return this.oversizedInGui;
    }

    public class LayerRenderState {
        private static final Vector3f[] NO_EXTENTS = new Vector3f[0];
        public static final Supplier<Vector3f[]> NO_EXTENTS_SUPPLIER = () -> NO_EXTENTS;
        private final List<BakedQuad> quads = new ArrayList<BakedQuad>();
        boolean usesBlockLight;
        @Nullable
        TextureAtlasSprite particleIcon;
        ItemTransform transform = ItemTransform.NO_TRANSFORM;
        @Nullable
        private RenderType renderType;
        private FoilType foilType = FoilType.NONE;
        private int[] tintLayers = new int[0];
        @Nullable
        private SpecialModelRenderer<Object> specialRenderer;
        @Nullable
        private Object argumentForSpecialRendering;
        Supplier<Vector3f[]> extents = NO_EXTENTS_SUPPLIER;

        public void clear() {
            this.quads.clear();
            this.renderType = null;
            this.foilType = FoilType.NONE;
            this.specialRenderer = null;
            this.argumentForSpecialRendering = null;
            Arrays.fill(this.tintLayers, -1);
            this.usesBlockLight = false;
            this.particleIcon = null;
            this.transform = ItemTransform.NO_TRANSFORM;
            this.extents = NO_EXTENTS_SUPPLIER;
        }

        public List<BakedQuad> prepareQuadList() {
            return this.quads;
        }

        public void setRenderType(RenderType $$0) {
            this.renderType = $$0;
        }

        public void setUsesBlockLight(boolean $$0) {
            this.usesBlockLight = $$0;
        }

        public void setExtents(Supplier<Vector3f[]> $$0) {
            this.extents = $$0;
        }

        public void setParticleIcon(TextureAtlasSprite $$0) {
            this.particleIcon = $$0;
        }

        public void setTransform(ItemTransform $$0) {
            this.transform = $$0;
        }

        public <T> void setupSpecialModel(SpecialModelRenderer<T> $$0, @Nullable T $$1) {
            this.specialRenderer = LayerRenderState.eraseSpecialRenderer($$0);
            this.argumentForSpecialRendering = $$1;
        }

        private static SpecialModelRenderer<Object> eraseSpecialRenderer(SpecialModelRenderer<?> $$0) {
            return $$0;
        }

        public void setFoilType(FoilType $$0) {
            this.foilType = $$0;
        }

        public int[] a(int $$0) {
            if ($$0 > this.tintLayers.length) {
                this.tintLayers = new int[$$0];
                Arrays.fill(this.tintLayers, -1);
            }
            return this.tintLayers;
        }

        void render(PoseStack $$0, MultiBufferSource $$1, int $$2, int $$3) {
            $$0.pushPose();
            this.transform.apply(ItemStackRenderState.this.displayContext.leftHand(), $$0.last());
            if (this.specialRenderer != null) {
                this.specialRenderer.render(this.argumentForSpecialRendering, ItemStackRenderState.this.displayContext, $$0, $$1, $$2, $$3, this.foilType != FoilType.NONE);
            } else if (this.renderType != null) {
                ItemRenderer.a(ItemStackRenderState.this.displayContext, $$0, $$1, $$2, $$3, this.tintLayers, this.quads, this.renderType, this.foilType);
            }
            $$0.popPose();
        }
    }

    public static final class FoilType
    extends Enum<FoilType> {
        public static final /* enum */ FoilType NONE = new FoilType();
        public static final /* enum */ FoilType STANDARD = new FoilType();
        public static final /* enum */ FoilType SPECIAL = new FoilType();
        private static final /* synthetic */ FoilType[] $VALUES;

        public static FoilType[] values() {
            return (FoilType[])$VALUES.clone();
        }

        public static FoilType valueOf(String $$0) {
            return Enum.valueOf(FoilType.class, $$0);
        }

        private static /* synthetic */ FoilType[] a() {
            return new FoilType[]{NONE, STANDARD, SPECIAL};
        }

        static {
            $VALUES = FoilType.a();
        }
    }
}

