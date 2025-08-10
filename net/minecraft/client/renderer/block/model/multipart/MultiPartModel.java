/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class MultiPartModel
implements BlockStateModel {
    private final SharedBakedState shared;
    private final BlockState blockState;
    @Nullable
    private List<BlockStateModel> models;

    MultiPartModel(SharedBakedState $$0, BlockState $$1) {
        this.shared = $$0;
        this.blockState = $$1;
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return this.shared.particleIcon;
    }

    @Override
    public void collectParts(RandomSource $$0, List<BlockModelPart> $$1) {
        if (this.models == null) {
            this.models = this.shared.selectModels(this.blockState);
        }
        long $$2 = $$0.nextLong();
        for (BlockStateModel $$3 : this.models) {
            $$0.setSeed($$2);
            $$3.collectParts($$0, $$1);
        }
    }

    static final class SharedBakedState {
        private final List<Selector<BlockStateModel>> selectors;
        final TextureAtlasSprite particleIcon;
        private final Map<BitSet, List<BlockStateModel>> subsets = new ConcurrentHashMap<BitSet, List<BlockStateModel>>();

        private static BlockStateModel getFirstModel(List<Selector<BlockStateModel>> $$0) {
            if ($$0.isEmpty()) {
                throw new IllegalArgumentException("Model must have at least one selector");
            }
            return (BlockStateModel)((Selector)((Object)$$0.getFirst())).model();
        }

        public SharedBakedState(List<Selector<BlockStateModel>> $$0) {
            this.selectors = $$0;
            BlockStateModel $$1 = SharedBakedState.getFirstModel($$0);
            this.particleIcon = $$1.particleIcon();
        }

        public List<BlockStateModel> selectModels(BlockState $$02) {
            BitSet $$1 = new BitSet();
            for (int $$2 = 0; $$2 < this.selectors.size(); ++$$2) {
                if (!this.selectors.get((int)$$2).condition.test($$02)) continue;
                $$1.set($$2);
            }
            return this.subsets.computeIfAbsent($$1, $$0 -> {
                ImmutableList.Builder $$1 = ImmutableList.builder();
                for (int $$2 = 0; $$2 < this.selectors.size(); ++$$2) {
                    if (!$$0.get($$2)) continue;
                    $$1.add((BlockStateModel)this.selectors.get((int)$$2).model);
                }
                return $$1.build();
            });
        }
    }

    public static class Unbaked
    implements BlockStateModel.UnbakedRoot {
        final List<Selector<BlockStateModel.Unbaked>> selectors;
        private final ModelBaker.SharedOperationKey<SharedBakedState> sharedStateKey = new ModelBaker.SharedOperationKey<SharedBakedState>(){

            @Override
            public SharedBakedState compute(ModelBaker $$0) {
                ImmutableList.Builder $$1 = ImmutableList.builderWithExpectedSize(selectors.size());
                for (Selector<BlockStateModel.Unbaked> $$2 : selectors) {
                    $$1.add($$2.with(((BlockStateModel.Unbaked)$$2.model).bake($$0)));
                }
                return new SharedBakedState((List<Selector<BlockStateModel>>)((Object)$$1.build()));
            }

            @Override
            public /* synthetic */ Object compute(ModelBaker modelBaker) {
                return this.compute(modelBaker);
            }
        };

        public Unbaked(List<Selector<BlockStateModel.Unbaked>> $$0) {
            this.selectors = $$0;
        }

        @Override
        public Object visualEqualityGroup(BlockState $$0) {
            IntArrayList $$1 = new IntArrayList();
            for (int $$2 = 0; $$2 < this.selectors.size(); ++$$2) {
                if (!this.selectors.get((int)$$2).condition.test($$0)) continue;
                $$1.add($$2);
            }
            record Key(Unbaked model, IntList selectors) {
            }
            return new Key(this, (IntList)$$1);
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver $$0) {
            this.selectors.forEach($$1 -> ((BlockStateModel.Unbaked)$$1.model).resolveDependencies($$0));
        }

        @Override
        public BlockStateModel bake(BlockState $$0, ModelBaker $$1) {
            SharedBakedState $$2 = $$1.compute(this.sharedStateKey);
            return new MultiPartModel($$2, $$0);
        }
    }

    public static final class Selector<T>
    extends Record {
        final Predicate<BlockState> condition;
        final T model;

        public Selector(Predicate<BlockState> $$0, T $$1) {
            this.condition = $$0;
            this.model = $$1;
        }

        public <S> Selector<S> with(S $$0) {
            return new Selector<S>(this.condition, $$0);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Selector.class, "condition;model", "condition", "model"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Selector.class, "condition;model", "condition", "model"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Selector.class, "condition;model", "condition", "model"}, this, $$0);
        }

        public Predicate<BlockState> condition() {
            return this.condition;
        }

        public T model() {
            return this.model;
        }
    }
}

