/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.WeightedVariants;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockStateModel {
    public void collectParts(RandomSource var1, List<BlockModelPart> var2);

    default public List<BlockModelPart> collectParts(RandomSource $$0) {
        ObjectArrayList $$1 = new ObjectArrayList();
        this.collectParts($$0, (List<BlockModelPart>)$$1);
        return $$1;
    }

    public TextureAtlasSprite particleIcon();

    public static class SimpleCachedUnbakedRoot
    implements UnbakedRoot {
        final Unbaked contents;
        private final ModelBaker.SharedOperationKey<BlockStateModel> bakingKey = new ModelBaker.SharedOperationKey<BlockStateModel>(){

            @Override
            public BlockStateModel compute(ModelBaker $$0) {
                return contents.bake($$0);
            }

            @Override
            public /* synthetic */ Object compute(ModelBaker modelBaker) {
                return this.compute(modelBaker);
            }
        };

        public SimpleCachedUnbakedRoot(Unbaked $$0) {
            this.contents = $$0;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver $$0) {
            this.contents.resolveDependencies($$0);
        }

        @Override
        public BlockStateModel bake(BlockState $$0, ModelBaker $$1) {
            return $$1.compute(this.bakingKey);
        }

        @Override
        public Object visualEqualityGroup(BlockState $$0) {
            return this;
        }
    }

    public static interface Unbaked
    extends ResolvableModel {
        public static final Codec<Weighted<Variant>> ELEMENT_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Variant.MAP_CODEC.forGetter(Weighted::value), (App)ExtraCodecs.POSITIVE_INT.optionalFieldOf("weight", (Object)1).forGetter(Weighted::weight)).apply((Applicative)$$0, Weighted::new));
        public static final Codec<WeightedVariants.Unbaked> HARDCODED_WEIGHTED_CODEC = ExtraCodecs.nonEmptyList(ELEMENT_CODEC.listOf()).flatComapMap($$02 -> new WeightedVariants.Unbaked(WeightedList.of(Lists.transform($$02, $$0 -> $$0.map(SingleVariant.Unbaked::new)))), $$0 -> {
            List<Weighted<Unbaked>> $$1 = $$0.entries().unwrap();
            ArrayList<Weighted<Variant>> $$2 = new ArrayList<Weighted<Variant>>($$1.size());
            for (Weighted<Unbaked> $$3 : $$1) {
                Unbaked $$4 = $$3.value();
                if ($$4 instanceof SingleVariant.Unbaked) {
                    SingleVariant.Unbaked $$5 = (SingleVariant.Unbaked)$$4;
                    $$2.add(new Weighted<Variant>($$5.variant(), $$3.weight()));
                    continue;
                }
                return DataResult.error(() -> "Only single variants are supported");
            }
            return DataResult.success($$2);
        });
        public static final Codec<Unbaked> CODEC = Codec.either(HARDCODED_WEIGHTED_CODEC, SingleVariant.Unbaked.CODEC).flatComapMap($$02 -> (Unbaked)$$02.map($$0 -> $$0, $$0 -> $$0), $$0 -> {
            Unbaked unbaked = $$0;
            Objects.requireNonNull(unbaked);
            Unbaked $$1 = unbaked;
            int $$2 = 0;
            return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{SingleVariant.Unbaked.class, WeightedVariants.Unbaked.class}, (Object)$$1, (int)$$2)) {
                case 0 -> {
                    SingleVariant.Unbaked $$3 = (SingleVariant.Unbaked)$$1;
                    yield DataResult.success((Object)Either.right((Object)$$3));
                }
                case 1 -> {
                    WeightedVariants.Unbaked $$4 = (WeightedVariants.Unbaked)$$1;
                    yield DataResult.success((Object)Either.left((Object)$$4));
                }
                default -> DataResult.error(() -> "Only a single variant or a list of variants are supported");
            };
        });

        public BlockStateModel bake(ModelBaker var1);

        default public UnbakedRoot asRoot() {
            return new SimpleCachedUnbakedRoot(this);
        }
    }

    public static interface UnbakedRoot
    extends ResolvableModel {
        public BlockStateModel bake(BlockState var1, ModelBaker var2);

        public Object visualEqualityGroup(BlockState var1);
    }
}

