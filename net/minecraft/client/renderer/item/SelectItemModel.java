/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.CacheSlot;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RegistryContextSwapper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SelectItemModel<T>
implements ItemModel {
    private final SelectItemModelProperty<T> property;
    private final ModelSelector<T> models;

    public SelectItemModel(SelectItemModelProperty<T> $$0, ModelSelector<T> $$1) {
        this.property = $$0;
        this.models = $$1;
    }

    @Override
    public void update(ItemStackRenderState $$0, ItemStack $$1, ItemModelResolver $$2, ItemDisplayContext $$3, @Nullable ClientLevel $$4, @Nullable LivingEntity $$5, int $$6) {
        $$0.appendModelIdentityElement(this);
        T $$7 = this.property.get($$1, $$4, $$5, $$6, $$3);
        ItemModel $$8 = this.models.get($$7, $$4);
        if ($$8 != null) {
            $$8.update($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        }
    }

    @FunctionalInterface
    public static interface ModelSelector<T> {
        @Nullable
        public ItemModel get(@Nullable T var1, @Nullable ClientLevel var2);
    }

    public static final class SwitchCase<T>
    extends Record {
        final List<T> values;
        final ItemModel.Unbaked model;

        public SwitchCase(List<T> $$0, ItemModel.Unbaked $$1) {
            this.values = $$0;
            this.model = $$1;
        }

        public static <T> Codec<SwitchCase<T>> codec(Codec<T> $$0) {
            return RecordCodecBuilder.create($$1 -> $$1.group((App)ExtraCodecs.nonEmptyList(ExtraCodecs.compactListCodec($$0)).fieldOf("when").forGetter(SwitchCase::values), (App)ItemModels.CODEC.fieldOf("model").forGetter(SwitchCase::model)).apply((Applicative)$$1, SwitchCase::new));
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{SwitchCase.class, "values;model", "values", "model"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SwitchCase.class, "values;model", "values", "model"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SwitchCase.class, "values;model", "values", "model"}, this, $$0);
        }

        public List<T> values() {
            return this.values;
        }

        public ItemModel.Unbaked model() {
            return this.model;
        }
    }

    public record UnbakedSwitch<P extends SelectItemModelProperty<T>, T>(P property, List<SwitchCase<T>> cases) {
        public static final MapCodec<UnbakedSwitch<?, ?>> MAP_CODEC = SelectItemModelProperties.CODEC.dispatchMap("property", $$0 -> $$0.property().type(), SelectItemModelProperty.Type::switchCodec);

        public ItemModel bake(ItemModel.BakingContext $$0, ItemModel $$1) {
            Object2ObjectOpenHashMap $$2 = new Object2ObjectOpenHashMap();
            for (SwitchCase<T> $$3 : this.cases) {
                ItemModel.Unbaked $$4 = $$3.model;
                ItemModel $$5 = $$4.bake($$0);
                for (Object $$6 : $$3.values) {
                    $$2.put($$6, (Object)$$5);
                }
            }
            $$2.defaultReturnValue((Object)$$1);
            return new SelectItemModel<T>(this.property, this.createModelGetter((Object2ObjectMap<T, ItemModel>)$$2, $$0.contextSwapper()));
        }

        private ModelSelector<T> createModelGetter(Object2ObjectMap<T, ItemModel> $$0, @Nullable RegistryContextSwapper $$12) {
            if ($$12 == null) {
                return ($$1, $$2) -> (ItemModel)$$0.get($$1);
            }
            ItemModel $$22 = (ItemModel)$$0.defaultReturnValue();
            CacheSlot<ClientLevel, Object2ObjectMap> $$32 = new CacheSlot<ClientLevel, Object2ObjectMap>($$3 -> {
                Object2ObjectOpenHashMap $$4 = new Object2ObjectOpenHashMap($$0.size());
                $$4.defaultReturnValue((Object)$$22);
                $$0.forEach((arg_0, arg_1) -> this.lambda$createModelGetter$3($$12, $$3, (Object2ObjectMap)$$4, arg_0, arg_1));
                return $$4;
            });
            return ($$3, $$4) -> {
                if ($$4 == null) {
                    return (ItemModel)$$0.get($$3);
                }
                if ($$3 == null) {
                    return $$22;
                }
                return (ItemModel)((Object2ObjectMap)$$32.compute($$4)).get($$3);
            };
        }

        public void resolveDependencies(ResolvableModel.Resolver $$0) {
            for (SwitchCase<T> $$1 : this.cases) {
                $$1.model.resolveDependencies($$0);
            }
        }

        private /* synthetic */ void lambda$createModelGetter$3(RegistryContextSwapper $$0, ClientLevel $$1, Object2ObjectMap $$22, Object $$3, ItemModel $$4) {
            $$0.swapTo(this.property.valueCodec(), $$3, $$1.registryAccess()).ifSuccess($$2 -> $$22.put($$2, (Object)$$4));
        }
    }

    public record Unbaked(UnbakedSwitch<?, ?> unbakedSwitch, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)UnbakedSwitch.MAP_CODEC.forGetter(Unbaked::unbakedSwitch), (App)ItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply((Applicative)$$0, Unbaked::new));

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext $$0) {
            ItemModel $$12 = this.fallback.map($$1 -> $$1.bake($$0)).orElse($$0.missingItemModel());
            return this.unbakedSwitch.bake($$0, $$12);
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver $$0) {
            this.unbakedSwitch.resolveDependencies($$0);
            this.fallback.ifPresent($$1 -> $$1.resolveDependencies($$0));
        }
    }
}

