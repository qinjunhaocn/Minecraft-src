/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RangeSelectItemModel
implements ItemModel {
    private static final int LINEAR_SEARCH_THRESHOLD = 16;
    private final RangeSelectItemModelProperty property;
    private final float scale;
    private final float[] thresholds;
    private final ItemModel[] models;
    private final ItemModel fallback;

    RangeSelectItemModel(RangeSelectItemModelProperty $$0, float $$1, float[] $$2, ItemModel[] $$3, ItemModel $$4) {
        this.property = $$0;
        this.thresholds = $$2;
        this.models = $$3;
        this.fallback = $$4;
        this.scale = $$1;
    }

    private static int a(float[] $$0, float $$1) {
        if ($$0.length < 16) {
            for (int $$2 = 0; $$2 < $$0.length; ++$$2) {
                if (!($$0[$$2] > $$1)) continue;
                return $$2 - 1;
            }
            return $$0.length - 1;
        }
        int $$3 = Arrays.binarySearch($$0, $$1);
        if ($$3 < 0) {
            int $$4 = ~$$3;
            return $$4 - 1;
        }
        return $$3;
    }

    @Override
    public void update(ItemStackRenderState $$0, ItemStack $$1, ItemModelResolver $$2, ItemDisplayContext $$3, @Nullable ClientLevel $$4, @Nullable LivingEntity $$5, int $$6) {
        ItemModel $$10;
        $$0.appendModelIdentityElement(this);
        float $$7 = this.property.get($$1, $$4, $$5, $$6) * this.scale;
        if (Float.isNaN($$7)) {
            ItemModel $$8 = this.fallback;
        } else {
            int $$9 = RangeSelectItemModel.a(this.thresholds, $$7);
            $$10 = $$9 == -1 ? this.fallback : this.models[$$9];
        }
        $$10.update($$0, $$1, $$2, $$3, $$4, $$5, $$6);
    }

    public static final class Entry
    extends Record {
        final float threshold;
        final ItemModel.Unbaked model;
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.FLOAT.fieldOf("threshold").forGetter(Entry::threshold), (App)ItemModels.CODEC.fieldOf("model").forGetter(Entry::model)).apply((Applicative)$$0, Entry::new));
        public static final Comparator<Entry> BY_THRESHOLD = Comparator.comparingDouble(Entry::threshold);

        public Entry(float $$0, ItemModel.Unbaked $$1) {
            this.threshold = $$0;
            this.model = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "threshold;model", "threshold", "model"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "threshold;model", "threshold", "model"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "threshold;model", "threshold", "model"}, this, $$0);
        }

        public float threshold() {
            return this.threshold;
        }

        public ItemModel.Unbaked model() {
            return this.model;
        }
    }

    public record Unbaked(RangeSelectItemModelProperty property, float scale, List<Entry> entries, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)RangeSelectItemModelProperties.MAP_CODEC.forGetter(Unbaked::property), (App)Codec.FLOAT.optionalFieldOf("scale", (Object)Float.valueOf(1.0f)).forGetter(Unbaked::scale), (App)Entry.CODEC.listOf().fieldOf("entries").forGetter(Unbaked::entries), (App)ItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply((Applicative)$$0, Unbaked::new));

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext $$0) {
            float[] $$12 = new float[this.entries.size()];
            ItemModel[] $$2 = new ItemModel[this.entries.size()];
            ArrayList<Entry> $$3 = new ArrayList<Entry>(this.entries);
            $$3.sort(Entry.BY_THRESHOLD);
            for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
                Entry $$5 = (Entry)((Object)$$3.get($$4));
                $$12[$$4] = $$5.threshold;
                $$2[$$4] = $$5.model.bake($$0);
            }
            ItemModel $$6 = this.fallback.map($$1 -> $$1.bake($$0)).orElse($$0.missingItemModel());
            return new RangeSelectItemModel(this.property, this.scale, $$12, $$2, $$6);
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver $$0) {
            this.fallback.ifPresent($$1 -> $$1.resolveDependencies($$0));
            this.entries.forEach($$1 -> $$1.model.resolveDependencies($$0));
        }
    }
}

