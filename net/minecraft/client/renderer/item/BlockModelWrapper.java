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

import com.google.common.base.Suppliers;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector3f;

public class BlockModelWrapper
implements ItemModel {
    private final List<ItemTintSource> tints;
    private final List<BakedQuad> quads;
    private final Supplier<Vector3f[]> extents;
    private final ModelRenderProperties properties;
    private final boolean animated;

    public BlockModelWrapper(List<ItemTintSource> $$0, List<BakedQuad> $$1, ModelRenderProperties $$2) {
        this.tints = $$0;
        this.quads = $$1;
        this.properties = $$2;
        this.extents = Suppliers.memoize(() -> BlockModelWrapper.a(this.quads));
        boolean $$3 = false;
        for (BakedQuad $$4 : $$1) {
            if (!$$4.sprite().isAnimated()) continue;
            $$3 = true;
            break;
        }
        this.animated = $$3;
    }

    public static Vector3f[] a(List<BakedQuad> $$0) {
        HashSet $$1 = new HashSet();
        for (BakedQuad $$2 : $$0) {
            FaceBakery.a($$2.b(), $$1::add);
        }
        return (Vector3f[])$$1.toArray(Vector3f[]::new);
    }

    @Override
    public void update(ItemStackRenderState $$0, ItemStack $$1, ItemModelResolver $$2, ItemDisplayContext $$3, @Nullable ClientLevel $$4, @Nullable LivingEntity $$5, int $$6) {
        $$0.appendModelIdentityElement(this);
        ItemStackRenderState.LayerRenderState $$7 = $$0.newLayer();
        if ($$1.hasFoil()) {
            ItemStackRenderState.FoilType $$8 = BlockModelWrapper.hasSpecialAnimatedTexture($$1) ? ItemStackRenderState.FoilType.SPECIAL : ItemStackRenderState.FoilType.STANDARD;
            $$7.setFoilType($$8);
            $$0.setAnimated();
            $$0.appendModelIdentityElement((Object)$$8);
        }
        int $$9 = this.tints.size();
        int[] $$10 = $$7.a($$9);
        for (int $$11 = 0; $$11 < $$9; ++$$11) {
            int $$12;
            $$10[$$11] = $$12 = this.tints.get($$11).calculate($$1, $$4, $$5);
            $$0.appendModelIdentityElement($$12);
        }
        $$7.setExtents(this.extents);
        $$7.setRenderType(ItemBlockRenderTypes.getRenderType($$1));
        this.properties.applyToLayer($$7, $$3);
        $$7.prepareQuadList().addAll(this.quads);
        if (this.animated) {
            $$0.setAnimated();
        }
    }

    private static boolean hasSpecialAnimatedTexture(ItemStack $$0) {
        return $$0.is(ItemTags.COMPASSES) || $$0.is(Items.CLOCK);
    }

    public record Unbaked(ResourceLocation model, List<ItemTintSource> tints) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("model").forGetter(Unbaked::model), (App)ItemTintSources.CODEC.listOf().optionalFieldOf("tints", (Object)List.of()).forGetter(Unbaked::tints)).apply((Applicative)$$0, Unbaked::new));

        @Override
        public void resolveDependencies(ResolvableModel.Resolver $$0) {
            $$0.markDependency(this.model);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext $$0) {
            ModelBaker $$1 = $$0.blockModelBaker();
            ResolvedModel $$2 = $$1.getModel(this.model);
            TextureSlots $$3 = $$2.getTopTextureSlots();
            List<BakedQuad> $$4 = $$2.bakeTopGeometry($$3, $$1, BlockModelRotation.X0_Y0).getAll();
            ModelRenderProperties $$5 = ModelRenderProperties.fromResolvedModel($$1, $$2, $$3);
            return new BlockModelWrapper(this.tints, $$4, $$5);
        }

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}

