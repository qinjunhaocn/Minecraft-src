/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public interface ResolvedModel
extends ModelDebugName {
    public static final boolean DEFAULT_AMBIENT_OCCLUSION = true;
    public static final UnbakedModel.GuiLight DEFAULT_GUI_LIGHT = UnbakedModel.GuiLight.SIDE;

    public UnbakedModel wrapped();

    @Nullable
    public ResolvedModel parent();

    public static TextureSlots findTopTextureSlots(ResolvedModel $$0) {
        TextureSlots.Resolver $$2 = new TextureSlots.Resolver();
        for (ResolvedModel $$1 = $$0; $$1 != null; $$1 = $$1.parent()) {
            $$2.addLast($$1.wrapped().textureSlots());
        }
        return $$2.resolve($$0);
    }

    default public TextureSlots getTopTextureSlots() {
        return ResolvedModel.findTopTextureSlots(this);
    }

    public static boolean findTopAmbientOcclusion(ResolvedModel $$0) {
        while ($$0 != null) {
            Boolean $$1 = $$0.wrapped().ambientOcclusion();
            if ($$1 != null) {
                return $$1;
            }
            $$0 = $$0.parent();
        }
        return true;
    }

    default public boolean getTopAmbientOcclusion() {
        return ResolvedModel.findTopAmbientOcclusion(this);
    }

    public static UnbakedModel.GuiLight findTopGuiLight(ResolvedModel $$0) {
        while ($$0 != null) {
            UnbakedModel.GuiLight $$1 = $$0.wrapped().guiLight();
            if ($$1 != null) {
                return $$1;
            }
            $$0 = $$0.parent();
        }
        return DEFAULT_GUI_LIGHT;
    }

    default public UnbakedModel.GuiLight getTopGuiLight() {
        return ResolvedModel.findTopGuiLight(this);
    }

    public static UnbakedGeometry findTopGeometry(ResolvedModel $$0) {
        while ($$0 != null) {
            UnbakedGeometry $$1 = $$0.wrapped().geometry();
            if ($$1 != null) {
                return $$1;
            }
            $$0 = $$0.parent();
        }
        return UnbakedGeometry.EMPTY;
    }

    default public UnbakedGeometry getTopGeometry() {
        return ResolvedModel.findTopGeometry(this);
    }

    default public QuadCollection bakeTopGeometry(TextureSlots $$0, ModelBaker $$1, ModelState $$2) {
        return this.getTopGeometry().bake($$0, $$1, $$2, this);
    }

    public static TextureAtlasSprite resolveParticleSprite(TextureSlots $$0, ModelBaker $$1, ModelDebugName $$2) {
        return $$1.sprites().resolveSlot($$0, "particle", $$2);
    }

    default public TextureAtlasSprite resolveParticleSprite(TextureSlots $$0, ModelBaker $$1) {
        return ResolvedModel.resolveParticleSprite($$0, $$1, this);
    }

    public static ItemTransform findTopTransform(ResolvedModel $$0, ItemDisplayContext $$1) {
        while ($$0 != null) {
            ItemTransform $$3;
            ItemTransforms $$2 = $$0.wrapped().transforms();
            if ($$2 != null && ($$3 = $$2.getTransform($$1)) != ItemTransform.NO_TRANSFORM) {
                return $$3;
            }
            $$0 = $$0.parent();
        }
        return ItemTransform.NO_TRANSFORM;
    }

    public static ItemTransforms findTopTransforms(ResolvedModel $$0) {
        ItemTransform $$1 = ResolvedModel.findTopTransform($$0, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        ItemTransform $$2 = ResolvedModel.findTopTransform($$0, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        ItemTransform $$3 = ResolvedModel.findTopTransform($$0, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
        ItemTransform $$4 = ResolvedModel.findTopTransform($$0, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
        ItemTransform $$5 = ResolvedModel.findTopTransform($$0, ItemDisplayContext.HEAD);
        ItemTransform $$6 = ResolvedModel.findTopTransform($$0, ItemDisplayContext.GUI);
        ItemTransform $$7 = ResolvedModel.findTopTransform($$0, ItemDisplayContext.GROUND);
        ItemTransform $$8 = ResolvedModel.findTopTransform($$0, ItemDisplayContext.FIXED);
        return new ItemTransforms($$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8);
    }

    default public ItemTransforms getTopTransforms() {
        return ResolvedModel.findTopTransforms(this);
    }
}

