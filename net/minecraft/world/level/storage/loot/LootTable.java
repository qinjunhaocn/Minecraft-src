/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class LootTable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<ResourceKey<LootTable>> KEY_CODEC = ResourceKey.codec(Registries.LOOT_TABLE);
    public static final ContextKeySet DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
    public static final long RANDOMIZE_SEED = 0L;
    public static final Codec<LootTable> DIRECT_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create($$02 -> $$02.group((App)LootContextParamSets.CODEC.lenientOptionalFieldOf("type", (Object)DEFAULT_PARAM_SET).forGetter($$0 -> $$0.paramSet), (App)ResourceLocation.CODEC.optionalFieldOf("random_sequence").forGetter($$0 -> $$0.randomSequence), (App)LootPool.CODEC.listOf().optionalFieldOf("pools", (Object)List.of()).forGetter($$0 -> $$0.pools), (App)LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", (Object)List.of()).forGetter($$0 -> $$0.functions)).apply((Applicative)$$02, LootTable::new)));
    public static final Codec<Holder<LootTable>> CODEC = RegistryFileCodec.create(Registries.LOOT_TABLE, DIRECT_CODEC);
    public static final LootTable EMPTY = new LootTable(LootContextParamSets.EMPTY, Optional.empty(), List.of(), List.of());
    private final ContextKeySet paramSet;
    private final Optional<ResourceLocation> randomSequence;
    private final List<LootPool> pools;
    private final List<LootItemFunction> functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

    LootTable(ContextKeySet $$0, Optional<ResourceLocation> $$1, List<LootPool> $$2, List<LootItemFunction> $$3) {
        this.paramSet = $$0;
        this.randomSequence = $$1;
        this.pools = $$2;
        this.functions = $$3;
        this.compositeFunction = LootItemFunctions.compose($$3);
    }

    public static Consumer<ItemStack> createStackSplitter(ServerLevel $$0, Consumer<ItemStack> $$1) {
        return $$2 -> {
            if (!$$2.isItemEnabled($$0.enabledFeatures())) {
                return;
            }
            if ($$2.getCount() < $$2.getMaxStackSize()) {
                $$1.accept((ItemStack)$$2);
            } else {
                ItemStack $$4;
                for (int $$3 = $$2.getCount(); $$3 > 0; $$3 -= $$4.getCount()) {
                    $$4 = $$2.copyWithCount(Math.min($$2.getMaxStackSize(), $$3));
                    $$1.accept($$4);
                }
            }
        };
    }

    public void getRandomItemsRaw(LootParams $$0, Consumer<ItemStack> $$1) {
        this.getRandomItemsRaw(new LootContext.Builder($$0).create(this.randomSequence), $$1);
    }

    public void getRandomItemsRaw(LootContext $$0, Consumer<ItemStack> $$1) {
        LootContext.VisitedEntry<LootTable> $$2 = LootContext.createVisitedEntry(this);
        if ($$0.pushVisitedElement($$2)) {
            Consumer<ItemStack> $$3 = LootItemFunction.decorate(this.compositeFunction, $$1, $$0);
            for (LootPool $$4 : this.pools) {
                $$4.addRandomItems($$3, $$0);
            }
            $$0.popVisitedElement($$2);
        } else {
            LOGGER.warn("Detected infinite loop in loot tables");
        }
    }

    public void getRandomItems(LootParams $$0, long $$1, Consumer<ItemStack> $$2) {
        this.getRandomItemsRaw(new LootContext.Builder($$0).withOptionalRandomSeed($$1).create(this.randomSequence), LootTable.createStackSplitter($$0.getLevel(), $$2));
    }

    public void getRandomItems(LootParams $$0, Consumer<ItemStack> $$1) {
        this.getRandomItemsRaw($$0, LootTable.createStackSplitter($$0.getLevel(), $$1));
    }

    public void getRandomItems(LootContext $$0, Consumer<ItemStack> $$1) {
        this.getRandomItemsRaw($$0, LootTable.createStackSplitter($$0.getLevel(), $$1));
    }

    public ObjectArrayList<ItemStack> getRandomItems(LootParams $$0, RandomSource $$1) {
        return this.getRandomItems(new LootContext.Builder($$0).withOptionalRandomSource($$1).create(this.randomSequence));
    }

    public ObjectArrayList<ItemStack> getRandomItems(LootParams $$0, long $$1) {
        return this.getRandomItems(new LootContext.Builder($$0).withOptionalRandomSeed($$1).create(this.randomSequence));
    }

    public ObjectArrayList<ItemStack> getRandomItems(LootParams $$0) {
        return this.getRandomItems(new LootContext.Builder($$0).create(this.randomSequence));
    }

    private ObjectArrayList<ItemStack> getRandomItems(LootContext $$0) {
        ObjectArrayList $$1 = new ObjectArrayList();
        this.getRandomItems($$0, arg_0 -> ((ObjectArrayList)$$1).add(arg_0));
        return $$1;
    }

    public ContextKeySet getParamSet() {
        return this.paramSet;
    }

    public void validate(ValidationContext $$0) {
        for (int $$1 = 0; $$1 < this.pools.size(); ++$$1) {
            this.pools.get($$1).validate($$0.forChild(new ProblemReporter.IndexedFieldPathElement("pools", $$1)));
        }
        for (int $$2 = 0; $$2 < this.functions.size(); ++$$2) {
            this.functions.get($$2).validate($$0.forChild(new ProblemReporter.IndexedFieldPathElement("functions", $$2)));
        }
    }

    public void fill(Container $$0, LootParams $$1, long $$2) {
        LootContext $$3 = new LootContext.Builder($$1).withOptionalRandomSeed($$2).create(this.randomSequence);
        ObjectArrayList<ItemStack> $$4 = this.getRandomItems($$3);
        RandomSource $$5 = $$3.getRandom();
        List<Integer> $$6 = this.getAvailableSlots($$0, $$5);
        this.shuffleAndSplitItems($$4, $$6.size(), $$5);
        for (ItemStack $$7 : $$4) {
            if ($$6.isEmpty()) {
                LOGGER.warn("Tried to over-fill a container");
                return;
            }
            if ($$7.isEmpty()) {
                $$0.setItem($$6.remove($$6.size() - 1), ItemStack.EMPTY);
                continue;
            }
            $$0.setItem($$6.remove($$6.size() - 1), $$7);
        }
    }

    private void shuffleAndSplitItems(ObjectArrayList<ItemStack> $$0, int $$1, RandomSource $$2) {
        ArrayList<ItemStack> $$3 = Lists.newArrayList();
        ObjectListIterator $$4 = $$0.iterator();
        while ($$4.hasNext()) {
            ItemStack $$5 = (ItemStack)$$4.next();
            if ($$5.isEmpty()) {
                $$4.remove();
                continue;
            }
            if ($$5.getCount() <= 1) continue;
            $$3.add($$5);
            $$4.remove();
        }
        while ($$1 - $$0.size() - $$3.size() > 0 && !$$3.isEmpty()) {
            ItemStack $$6 = (ItemStack)$$3.remove(Mth.nextInt($$2, 0, $$3.size() - 1));
            int $$7 = Mth.nextInt($$2, 1, $$6.getCount() / 2);
            ItemStack $$8 = $$6.split($$7);
            if ($$6.getCount() > 1 && $$2.nextBoolean()) {
                $$3.add($$6);
            } else {
                $$0.add((Object)$$6);
            }
            if ($$8.getCount() > 1 && $$2.nextBoolean()) {
                $$3.add($$8);
                continue;
            }
            $$0.add((Object)$$8);
        }
        $$0.addAll($$3);
        Util.shuffle($$0, $$2);
    }

    private List<Integer> getAvailableSlots(Container $$0, RandomSource $$1) {
        ObjectArrayList $$2 = new ObjectArrayList();
        for (int $$3 = 0; $$3 < $$0.getContainerSize(); ++$$3) {
            if (!$$0.getItem($$3).isEmpty()) continue;
            $$2.add((Object)$$3);
        }
        Util.shuffle($$2, $$1);
        return $$2;
    }

    public static Builder lootTable() {
        return new Builder();
    }

    public static class Builder
    implements FunctionUserBuilder<Builder> {
        private final ImmutableList.Builder<LootPool> pools = ImmutableList.builder();
        private final ImmutableList.Builder<LootItemFunction> functions = ImmutableList.builder();
        private ContextKeySet paramSet = DEFAULT_PARAM_SET;
        private Optional<ResourceLocation> randomSequence = Optional.empty();

        public Builder withPool(LootPool.Builder $$0) {
            this.pools.add((Object)$$0.build());
            return this;
        }

        public Builder setParamSet(ContextKeySet $$0) {
            this.paramSet = $$0;
            return this;
        }

        public Builder setRandomSequence(ResourceLocation $$0) {
            this.randomSequence = Optional.of($$0);
            return this;
        }

        @Override
        public Builder apply(LootItemFunction.Builder $$0) {
            this.functions.add((Object)$$0.build());
            return this;
        }

        @Override
        public Builder unwrap() {
            return this;
        }

        public LootTable build() {
            return new LootTable(this.paramSet, this.randomSequence, (List<LootPool>)((Object)this.pools.build()), (List<LootItemFunction>)((Object)this.functions.build()));
        }

        @Override
        public /* synthetic */ FunctionUserBuilder unwrap() {
            return this.unwrap();
        }

        @Override
        public /* synthetic */ FunctionUserBuilder apply(LootItemFunction.Builder builder) {
            return this.apply(builder);
        }
    }
}

