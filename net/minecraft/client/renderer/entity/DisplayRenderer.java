/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BlockDisplayEntityRenderState;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemDisplayEntityRenderState;
import net.minecraft.client.renderer.entity.state.TextDisplayEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public abstract class DisplayRenderer<T extends Display, S, ST extends DisplayEntityRenderState>
extends EntityRenderer<T, ST> {
    private final EntityRenderDispatcher entityRenderDispatcher;

    protected DisplayRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.entityRenderDispatcher = $$0.getEntityRenderDispatcher();
    }

    @Override
    protected AABB getBoundingBoxForCulling(T $$0) {
        return ((Display)$$0).getBoundingBoxForCulling();
    }

    @Override
    protected boolean affectedByCulling(T $$0) {
        return ((Display)$$0).affectedByCulling();
    }

    private static int getBrightnessOverride(Display $$0) {
        Display.RenderState $$1 = $$0.renderState();
        return $$1 != null ? $$1.brightnessOverride() : -1;
    }

    @Override
    protected int getSkyLightLevel(T $$0, BlockPos $$1) {
        int $$2 = DisplayRenderer.getBrightnessOverride($$0);
        if ($$2 != -1) {
            return LightTexture.sky($$2);
        }
        return super.getSkyLightLevel($$0, $$1);
    }

    @Override
    protected int getBlockLightLevel(T $$0, BlockPos $$1) {
        int $$2 = DisplayRenderer.getBrightnessOverride($$0);
        if ($$2 != -1) {
            return LightTexture.block($$2);
        }
        return super.getBlockLightLevel($$0, $$1);
    }

    @Override
    protected float getShadowRadius(ST $$0) {
        Display.RenderState $$1 = ((DisplayEntityRenderState)$$0).renderState;
        if ($$1 == null) {
            return 0.0f;
        }
        return $$1.shadowRadius().get(((DisplayEntityRenderState)$$0).interpolationProgress);
    }

    @Override
    protected float getShadowStrength(ST $$0) {
        Display.RenderState $$1 = ((DisplayEntityRenderState)$$0).renderState;
        if ($$1 == null) {
            return 0.0f;
        }
        return $$1.shadowStrength().get(((DisplayEntityRenderState)$$0).interpolationProgress);
    }

    @Override
    public void render(ST $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        Display.RenderState $$4 = ((DisplayEntityRenderState)$$0).renderState;
        if ($$4 == null || !((DisplayEntityRenderState)$$0).hasSubState()) {
            return;
        }
        float $$5 = ((DisplayEntityRenderState)$$0).interpolationProgress;
        super.render($$0, $$1, $$2, $$3);
        $$1.pushPose();
        $$1.mulPose((Quaternionfc)this.calculateOrientation($$4, $$0, new Quaternionf()));
        Transformation $$6 = $$4.transformation().get($$5);
        $$1.mulPose($$6.getMatrix());
        this.renderInner($$0, $$1, $$2, $$3, $$5);
        $$1.popPose();
    }

    private Quaternionf calculateOrientation(Display.RenderState $$0, ST $$1, Quaternionf $$2) {
        Camera $$3 = this.entityRenderDispatcher.camera;
        return switch ($$0.billboardConstraints()) {
            default -> throw new MatchException(null, null);
            case Display.BillboardConstraints.FIXED -> $$2.rotationYXZ((float)(-Math.PI) / 180 * ((DisplayEntityRenderState)$$1).entityYRot, (float)Math.PI / 180 * ((DisplayEntityRenderState)$$1).entityXRot, 0.0f);
            case Display.BillboardConstraints.HORIZONTAL -> $$2.rotationYXZ((float)(-Math.PI) / 180 * ((DisplayEntityRenderState)$$1).entityYRot, (float)Math.PI / 180 * DisplayRenderer.cameraXRot($$3), 0.0f);
            case Display.BillboardConstraints.VERTICAL -> $$2.rotationYXZ((float)(-Math.PI) / 180 * DisplayRenderer.cameraYrot($$3), (float)Math.PI / 180 * ((DisplayEntityRenderState)$$1).entityXRot, 0.0f);
            case Display.BillboardConstraints.CENTER -> $$2.rotationYXZ((float)(-Math.PI) / 180 * DisplayRenderer.cameraYrot($$3), (float)Math.PI / 180 * DisplayRenderer.cameraXRot($$3), 0.0f);
        };
    }

    private static float cameraYrot(Camera $$0) {
        return $$0.getYRot() - 180.0f;
    }

    private static float cameraXRot(Camera $$0) {
        return -$$0.getXRot();
    }

    private static <T extends Display> float entityYRot(T $$0, float $$1) {
        return $$0.getYRot($$1);
    }

    private static <T extends Display> float entityXRot(T $$0, float $$1) {
        return $$0.getXRot($$1);
    }

    protected abstract void renderInner(ST var1, PoseStack var2, MultiBufferSource var3, int var4, float var5);

    @Override
    public void extractRenderState(T $$0, ST $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        ((DisplayEntityRenderState)$$1).renderState = ((Display)$$0).renderState();
        ((DisplayEntityRenderState)$$1).interpolationProgress = ((Display)$$0).calculateInterpolationProgress($$2);
        ((DisplayEntityRenderState)$$1).entityYRot = DisplayRenderer.entityYRot($$0, $$2);
        ((DisplayEntityRenderState)$$1).entityXRot = DisplayRenderer.entityXRot($$0, $$2);
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState entityRenderState) {
        return this.getShadowRadius((ST)((DisplayEntityRenderState)entityRenderState));
    }

    @Override
    protected /* synthetic */ int getBlockLightLevel(Entity entity, BlockPos blockPos) {
        return this.getBlockLightLevel((T)((Display)entity), blockPos);
    }

    @Override
    protected /* synthetic */ int getSkyLightLevel(Entity entity, BlockPos blockPos) {
        return this.getSkyLightLevel((T)((Display)entity), blockPos);
    }

    public static class TextDisplayRenderer
    extends DisplayRenderer<Display.TextDisplay, Display.TextDisplay.TextRenderState, TextDisplayEntityRenderState> {
        private final Font font;

        protected TextDisplayRenderer(EntityRendererProvider.Context $$0) {
            super($$0);
            this.font = $$0.getFont();
        }

        @Override
        public TextDisplayEntityRenderState createRenderState() {
            return new TextDisplayEntityRenderState();
        }

        @Override
        public void extractRenderState(Display.TextDisplay $$0, TextDisplayEntityRenderState $$1, float $$2) {
            super.extractRenderState($$0, $$1, $$2);
            $$1.textRenderState = $$0.textRenderState();
            $$1.cachedInfo = $$0.cacheDisplay(this::splitLines);
        }

        private Display.TextDisplay.CachedInfo splitLines(Component $$0, int $$1) {
            List<FormattedCharSequence> $$2 = this.font.split($$0, $$1);
            ArrayList<Display.TextDisplay.CachedLine> $$3 = new ArrayList<Display.TextDisplay.CachedLine>($$2.size());
            int $$4 = 0;
            for (FormattedCharSequence $$5 : $$2) {
                int $$6 = this.font.width($$5);
                $$4 = Math.max($$4, $$6);
                $$3.add(new Display.TextDisplay.CachedLine($$5, $$6));
            }
            return new Display.TextDisplay.CachedInfo($$3, $$4);
        }

        @Override
        public void renderInner(TextDisplayEntityRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, float $$4) {
            int $$14;
            Display.TextDisplay.TextRenderState $$5 = $$0.textRenderState;
            byte $$6 = $$5.flags();
            boolean $$7 = ($$6 & 2) != 0;
            boolean $$8 = ($$6 & 4) != 0;
            boolean $$9 = ($$6 & 1) != 0;
            Display.TextDisplay.Align $$10 = Display.TextDisplay.getAlign($$6);
            byte $$11 = (byte)$$5.textOpacity().get($$4);
            if ($$8) {
                float $$12 = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
                int $$13 = (int)($$12 * 255.0f) << 24;
            } else {
                $$14 = $$5.backgroundColor().get($$4);
            }
            float $$15 = 0.0f;
            Matrix4f $$16 = $$1.last().pose();
            $$16.rotate((float)Math.PI, 0.0f, 1.0f, 0.0f);
            $$16.scale(-0.025f, -0.025f, -0.025f);
            Display.TextDisplay.CachedInfo $$17 = $$0.cachedInfo;
            boolean $$18 = true;
            int $$19 = this.font.lineHeight + 1;
            int $$20 = $$17.width();
            int $$21 = $$17.lines().size() * $$19 - 1;
            $$16.translate(1.0f - (float)$$20 / 2.0f, (float)(-$$21), 0.0f);
            if ($$14 != 0) {
                VertexConsumer $$22 = $$2.getBuffer($$7 ? RenderType.textBackgroundSeeThrough() : RenderType.textBackground());
                $$22.addVertex($$16, -1.0f, -1.0f, 0.0f).setColor($$14).setLight($$3);
                $$22.addVertex($$16, -1.0f, (float)$$21, 0.0f).setColor($$14).setLight($$3);
                $$22.addVertex($$16, (float)$$20, (float)$$21, 0.0f).setColor($$14).setLight($$3);
                $$22.addVertex($$16, (float)$$20, -1.0f, 0.0f).setColor($$14).setLight($$3);
            }
            for (Display.TextDisplay.CachedLine $$23 : $$17.lines()) {
                float $$24 = switch ($$10) {
                    default -> throw new MatchException(null, null);
                    case Display.TextDisplay.Align.LEFT -> 0.0f;
                    case Display.TextDisplay.Align.RIGHT -> $$20 - $$23.width();
                    case Display.TextDisplay.Align.CENTER -> (float)$$20 / 2.0f - (float)$$23.width() / 2.0f;
                };
                this.font.drawInBatch($$23.contents(), $$24, $$15, $$11 << 24 | 0xFFFFFF, $$9, $$16, $$2, $$7 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET, 0, $$3);
                $$15 += (float)$$19;
            }
        }

        @Override
        public /* synthetic */ EntityRenderState createRenderState() {
            return this.createRenderState();
        }

        @Override
        protected /* synthetic */ float getShadowRadius(EntityRenderState entityRenderState) {
            return super.getShadowRadius((DisplayEntityRenderState)entityRenderState);
        }

        @Override
        protected /* synthetic */ int getBlockLightLevel(Entity entity, BlockPos blockPos) {
            return super.getBlockLightLevel((Display)entity, blockPos);
        }

        @Override
        protected /* synthetic */ int getSkyLightLevel(Entity entity, BlockPos blockPos) {
            return super.getSkyLightLevel((Display)entity, blockPos);
        }
    }

    public static class ItemDisplayRenderer
    extends DisplayRenderer<Display.ItemDisplay, Display.ItemDisplay.ItemRenderState, ItemDisplayEntityRenderState> {
        private final ItemModelResolver itemModelResolver;

        protected ItemDisplayRenderer(EntityRendererProvider.Context $$0) {
            super($$0);
            this.itemModelResolver = $$0.getItemModelResolver();
        }

        @Override
        public ItemDisplayEntityRenderState createRenderState() {
            return new ItemDisplayEntityRenderState();
        }

        @Override
        public void extractRenderState(Display.ItemDisplay $$0, ItemDisplayEntityRenderState $$1, float $$2) {
            super.extractRenderState($$0, $$1, $$2);
            Display.ItemDisplay.ItemRenderState $$3 = $$0.itemRenderState();
            if ($$3 != null) {
                this.itemModelResolver.updateForNonLiving($$1.item, $$3.itemStack(), $$3.itemTransform(), $$0);
            } else {
                $$1.item.clear();
            }
        }

        @Override
        public void renderInner(ItemDisplayEntityRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, float $$4) {
            if ($$0.item.isEmpty()) {
                return;
            }
            $$1.mulPose((Quaternionfc)Axis.YP.rotation((float)Math.PI));
            $$0.item.render($$1, $$2, $$3, OverlayTexture.NO_OVERLAY);
        }

        @Override
        public /* synthetic */ EntityRenderState createRenderState() {
            return this.createRenderState();
        }

        @Override
        protected /* synthetic */ float getShadowRadius(EntityRenderState entityRenderState) {
            return super.getShadowRadius((DisplayEntityRenderState)entityRenderState);
        }

        @Override
        protected /* synthetic */ int getBlockLightLevel(Entity entity, BlockPos blockPos) {
            return super.getBlockLightLevel((Display)entity, blockPos);
        }

        @Override
        protected /* synthetic */ int getSkyLightLevel(Entity entity, BlockPos blockPos) {
            return super.getSkyLightLevel((Display)entity, blockPos);
        }
    }

    public static class BlockDisplayRenderer
    extends DisplayRenderer<Display.BlockDisplay, Display.BlockDisplay.BlockRenderState, BlockDisplayEntityRenderState> {
        private final BlockRenderDispatcher blockRenderer;

        protected BlockDisplayRenderer(EntityRendererProvider.Context $$0) {
            super($$0);
            this.blockRenderer = $$0.getBlockRenderDispatcher();
        }

        @Override
        public BlockDisplayEntityRenderState createRenderState() {
            return new BlockDisplayEntityRenderState();
        }

        @Override
        public void extractRenderState(Display.BlockDisplay $$0, BlockDisplayEntityRenderState $$1, float $$2) {
            super.extractRenderState($$0, $$1, $$2);
            $$1.blockRenderState = $$0.blockRenderState();
        }

        @Override
        public void renderInner(BlockDisplayEntityRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, float $$4) {
            this.blockRenderer.renderSingleBlock($$0.blockRenderState.blockState(), $$1, $$2, $$3, OverlayTexture.NO_OVERLAY);
        }

        @Override
        public /* synthetic */ EntityRenderState createRenderState() {
            return this.createRenderState();
        }

        @Override
        protected /* synthetic */ float getShadowRadius(EntityRenderState entityRenderState) {
            return super.getShadowRadius((DisplayEntityRenderState)entityRenderState);
        }

        @Override
        protected /* synthetic */ int getBlockLightLevel(Entity entity, BlockPos blockPos) {
            return super.getBlockLightLevel((Display)entity, blockPos);
        }

        @Override
        protected /* synthetic */ int getSkyLightLevel(Entity entity, BlockPos blockPos) {
            return super.getSkyLightLevel((Display)entity, blockPos);
        }
    }
}

