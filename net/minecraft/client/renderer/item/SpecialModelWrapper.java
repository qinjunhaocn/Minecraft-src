/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class SpecialModelWrapper<T>
implements ItemModel {
    private final SpecialModelRenderer<T> specialRenderer;
    private final ModelRenderProperties properties;

    public SpecialModelWrapper(SpecialModelRenderer<T> $$0, ModelRenderProperties $$1) {
        this.specialRenderer = $$0;
        this.properties = $$1;
    }

    @Override
    public void update(ItemStackRenderState $$0, ItemStack $$1, ItemModelResolver $$2, ItemDisplayContext $$3, @Nullable ClientLevel $$4, @Nullable LivingEntity $$5, int $$6) {
        $$0.appendModelIdentityElement(this);
        ItemStackRenderState.LayerRenderState $$7 = $$0.newLayer();
        if ($$1.hasFoil()) {
            ItemStackRenderState.FoilType $$8 = ItemStackRenderState.FoilType.STANDARD;
            $$7.setFoilType($$8);
            $$0.setAnimated();
            $$0.appendModelIdentityElement((Object)$$8);
        }
        T $$9 = this.specialRenderer.extractArgument($$1);
        $$7.setExtents(() -> {
            HashSet<Vector3f> $$0 = new HashSet<Vector3f>();
            this.specialRenderer.getExtents($$0);
            return $$0.toArray(new Vector3f[0]);
        });
        $$7.setupSpecialModel(this.specialRenderer, $$9);
        if ($$9 != null) {
            $$0.appendModelIdentityElement($$9);
        }
        this.properties.applyToLayer($$7, $$3);
    }

    public record Unbaked(ResourceLocation base, SpecialModelRenderer.Unbaked specialModel) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("base").forGetter(Unbaked::base), (App)SpecialModelRenderers.CODEC.fieldOf("model").forGetter(Unbaked::specialModel)).apply((Applicative)$$0, Unbaked::new));

        @Override
        public void resolveDependencies(ResolvableModel.Resolver $$0) {
            $$0.markDependency(this.base);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext $$0) {
            SpecialModelRenderer<?> $$1 = this.specialModel.bake($$0.entityModelSet());
            if ($$1 == null) {
                return $$0.missingItemModel();
            }
            ModelRenderProperties $$2 = this.getProperties($$0);
            return new SpecialModelWrapper($$1, $$2);
        }

        private ModelRenderProperties getProperties(ItemModel.BakingContext $$0) {
            ModelBaker $$1 = $$0.blockModelBaker();
            ResolvedModel $$2 = $$1.getModel(this.base);
            TextureSlots $$3 = $$2.getTopTextureSlots();
            return ModelRenderProperties.fromResolvedModel($$1, $$2, $$3);
        }

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}

