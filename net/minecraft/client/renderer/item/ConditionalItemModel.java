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
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.CacheSlot;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.conditional.ItemModelPropertyTest;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.RegistryContextSwapper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ConditionalItemModel
implements ItemModel {
    private final ItemModelPropertyTest property;
    private final ItemModel onTrue;
    private final ItemModel onFalse;

    public ConditionalItemModel(ItemModelPropertyTest $$0, ItemModel $$1, ItemModel $$2) {
        this.property = $$0;
        this.onTrue = $$1;
        this.onFalse = $$2;
    }

    @Override
    public void update(ItemStackRenderState $$0, ItemStack $$1, ItemModelResolver $$2, ItemDisplayContext $$3, @Nullable ClientLevel $$4, @Nullable LivingEntity $$5, int $$6) {
        $$0.appendModelIdentityElement(this);
        (this.property.get($$1, $$4, $$5, $$6, $$3) ? this.onTrue : this.onFalse).update($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    public record Unbaked(ConditionalItemModelProperty property, ItemModel.Unbaked onTrue, ItemModel.Unbaked onFalse) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ConditionalItemModelProperties.MAP_CODEC.forGetter(Unbaked::property), (App)ItemModels.CODEC.fieldOf("on_true").forGetter(Unbaked::onTrue), (App)ItemModels.CODEC.fieldOf("on_false").forGetter(Unbaked::onFalse)).apply((Applicative)$$0, Unbaked::new));

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext $$0) {
            return new ConditionalItemModel(this.adaptProperty(this.property, $$0.contextSwapper()), this.onTrue.bake($$0), this.onFalse.bake($$0));
        }

        private ItemModelPropertyTest adaptProperty(ConditionalItemModelProperty $$0, @Nullable RegistryContextSwapper $$1) {
            if ($$1 == null) {
                return $$0;
            }
            CacheSlot<ClientLevel, ItemModelPropertyTest> $$22 = new CacheSlot<ClientLevel, ItemModelPropertyTest>($$2 -> Unbaked.swapContext($$0, $$1, $$2));
            return ($$2, $$3, $$4, $$5, $$6) -> {
                ConditionalItemModelProperty $$7 = $$3 == null ? $$0 : (ItemModelPropertyTest)$$22.compute($$3);
                return $$7.get($$2, $$3, $$4, $$5, $$6);
            };
        }

        private static <T extends ConditionalItemModelProperty> T swapContext(T $$0, RegistryContextSwapper $$1, ClientLevel $$2) {
            return $$1.swapTo($$0.type().codec(), $$0, $$2.registryAccess()).result().orElse($$0);
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver $$0) {
            this.onTrue.resolveDependencies($$0);
            this.onFalse.resolveDependencies($$0);
        }
    }
}

