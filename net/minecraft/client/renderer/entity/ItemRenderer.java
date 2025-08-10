/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemRenderer {
    public static final ResourceLocation ENCHANTED_GLINT_ARMOR = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_armor.png");
    public static final ResourceLocation ENCHANTED_GLINT_ITEM = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
    public static final float SPECIAL_FOIL_UI_SCALE = 0.5f;
    public static final float SPECIAL_FOIL_FIRST_PERSON_SCALE = 0.75f;
    public static final float SPECIAL_FOIL_TEXTURE_SCALE = 0.0078125f;
    public static final int NO_TINT = -1;
    private final ItemModelResolver resolver;
    private final ItemStackRenderState scratchItemStackRenderState = new ItemStackRenderState();

    public ItemRenderer(ItemModelResolver $$0) {
        this.resolver = $$0;
    }

    public static void a(ItemDisplayContext $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, int $$4, int[] $$5, List<BakedQuad> $$6, RenderType $$7, ItemStackRenderState.FoilType $$8) {
        VertexConsumer $$11;
        if ($$8 == ItemStackRenderState.FoilType.SPECIAL) {
            PoseStack.Pose $$9 = $$1.last().copy();
            if ($$0 == ItemDisplayContext.GUI) {
                MatrixUtil.mulComponentWise($$9.pose(), 0.5f);
            } else if ($$0.firstPerson()) {
                MatrixUtil.mulComponentWise($$9.pose(), 0.75f);
            }
            VertexConsumer $$10 = ItemRenderer.getSpecialFoilBuffer($$2, $$7, $$9);
        } else {
            $$11 = ItemRenderer.getFoilBuffer($$2, $$7, true, $$8 != ItemStackRenderState.FoilType.NONE);
        }
        ItemRenderer.a($$1, $$11, $$6, $$5, $$3, $$4);
    }

    public static VertexConsumer getArmorFoilBuffer(MultiBufferSource $$0, RenderType $$1, boolean $$2) {
        if ($$2) {
            return VertexMultiConsumer.create($$0.getBuffer(RenderType.armorEntityGlint()), $$0.getBuffer($$1));
        }
        return $$0.getBuffer($$1);
    }

    private static VertexConsumer getSpecialFoilBuffer(MultiBufferSource $$0, RenderType $$1, PoseStack.Pose $$2) {
        return VertexMultiConsumer.create(new SheetedDecalTextureGenerator($$0.getBuffer(ItemRenderer.useTransparentGlint($$1) ? RenderType.glintTranslucent() : RenderType.glint()), $$2, 0.0078125f), $$0.getBuffer($$1));
    }

    public static VertexConsumer getFoilBuffer(MultiBufferSource $$0, RenderType $$1, boolean $$2, boolean $$3) {
        if ($$3) {
            if (ItemRenderer.useTransparentGlint($$1)) {
                return VertexMultiConsumer.create($$0.getBuffer(RenderType.glintTranslucent()), $$0.getBuffer($$1));
            }
            return VertexMultiConsumer.create($$0.getBuffer($$2 ? RenderType.glint() : RenderType.entityGlint()), $$0.getBuffer($$1));
        }
        return $$0.getBuffer($$1);
    }

    private static boolean useTransparentGlint(RenderType $$0) {
        return Minecraft.useShaderTransparency() && $$0 == Sheets.translucentItemSheet();
    }

    private static int a(int[] $$0, int $$1) {
        if ($$1 < 0 || $$1 >= $$0.length) {
            return -1;
        }
        return $$0[$$1];
    }

    private static void a(PoseStack $$0, VertexConsumer $$1, List<BakedQuad> $$2, int[] $$3, int $$4, int $$5) {
        PoseStack.Pose $$6 = $$0.last();
        for (BakedQuad $$7 : $$2) {
            float $$16;
            float $$15;
            float $$14;
            float $$13;
            if ($$7.isTinted()) {
                int $$8 = ItemRenderer.a($$3, $$7.tintIndex());
                float $$9 = (float)ARGB.alpha($$8) / 255.0f;
                float $$10 = (float)ARGB.red($$8) / 255.0f;
                float $$11 = (float)ARGB.green($$8) / 255.0f;
                float $$12 = (float)ARGB.blue($$8) / 255.0f;
            } else {
                $$13 = 1.0f;
                $$14 = 1.0f;
                $$15 = 1.0f;
                $$16 = 1.0f;
            }
            $$1.putBulkData($$6, $$7, $$14, $$15, $$16, $$13, $$4, $$5);
        }
    }

    public void renderStatic(ItemStack $$0, ItemDisplayContext $$1, int $$2, int $$3, PoseStack $$4, MultiBufferSource $$5, @Nullable Level $$6, int $$7) {
        this.renderStatic(null, $$0, $$1, $$4, $$5, $$6, $$2, $$3, $$7);
    }

    public void renderStatic(@Nullable LivingEntity $$0, ItemStack $$1, ItemDisplayContext $$2, PoseStack $$3, MultiBufferSource $$4, @Nullable Level $$5, int $$6, int $$7, int $$8) {
        this.resolver.updateForTopItem(this.scratchItemStackRenderState, $$1, $$2, $$5, $$0, $$8);
        this.scratchItemStackRenderState.render($$3, $$4, $$6, $$7);
    }
}

