/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CompositeModel
implements ItemModel {
    private final List<ItemModel> models;

    public CompositeModel(List<ItemModel> $$0) {
        this.models = $$0;
    }

    @Override
    public void update(ItemStackRenderState $$0, ItemStack $$1, ItemModelResolver $$2, ItemDisplayContext $$3, @Nullable ClientLevel $$4, @Nullable LivingEntity $$5, int $$6) {
        $$0.appendModelIdentityElement(this);
        $$0.ensureCapacity(this.models.size());
        for (ItemModel $$7 : this.models) {
            $$7.update($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        }
    }

    public record Unbaked(List<ItemModel.Unbaked> models) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ItemModels.CODEC.listOf().fieldOf("models").forGetter(Unbaked::models)).apply((Applicative)$$0, Unbaked::new));

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver $$0) {
            for (ItemModel.Unbaked $$1 : this.models) {
                $$1.resolveDependencies($$0);
            }
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext $$0) {
            return new CompositeModel(this.models.stream().map($$1 -> $$1.bake($$0)).toList());
        }
    }
}

