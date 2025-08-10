/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import org.apache.commons.lang3.mutable.MutableObject;

public class CopyCustomDataFunction
extends LootItemConditionalFunction {
    public static final MapCodec<CopyCustomDataFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> CopyCustomDataFunction.commonFields($$02).and($$02.group((App)NbtProviders.CODEC.fieldOf("source").forGetter($$0 -> $$0.source), (App)CopyOperation.CODEC.listOf().fieldOf("ops").forGetter($$0 -> $$0.operations))).apply((Applicative)$$02, CopyCustomDataFunction::new));
    private final NbtProvider source;
    private final List<CopyOperation> operations;

    CopyCustomDataFunction(List<LootItemCondition> $$0, NbtProvider $$1, List<CopyOperation> $$2) {
        super($$0);
        this.source = $$1;
        this.operations = List.copyOf($$2);
    }

    public LootItemFunctionType<CopyCustomDataFunction> getType() {
        return LootItemFunctions.COPY_CUSTOM_DATA;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.source.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        Tag $$22 = this.source.get($$1);
        if ($$22 == null) {
            return $$0;
        }
        MutableObject $$3 = new MutableObject();
        Supplier<Tag> $$4 = () -> {
            if ($$3.getValue() == null) {
                $$3.setValue($$0.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
            }
            return (Tag)$$3.getValue();
        };
        this.operations.forEach($$2 -> $$2.apply($$4, $$22));
        CompoundTag $$5 = (CompoundTag)$$3.getValue();
        if ($$5 != null) {
            CustomData.set(DataComponents.CUSTOM_DATA, $$0, $$5);
        }
        return $$0;
    }

    @Deprecated
    public static Builder copyData(NbtProvider $$0) {
        return new Builder($$0);
    }

    public static Builder copyData(LootContext.EntityTarget $$0) {
        return new Builder(ContextNbtProvider.forContextEntity($$0));
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final NbtProvider source;
        private final List<CopyOperation> ops = Lists.newArrayList();

        Builder(NbtProvider $$0) {
            this.source = $$0;
        }

        public Builder copy(String $$0, String $$1, MergeStrategy $$2) {
            try {
                this.ops.add(new CopyOperation(NbtPathArgument.NbtPath.of($$0), NbtPathArgument.NbtPath.of($$1), $$2));
            } catch (CommandSyntaxException $$3) {
                throw new IllegalArgumentException($$3);
            }
            return this;
        }

        public Builder copy(String $$0, String $$1) {
            return this.copy($$0, $$1, MergeStrategy.REPLACE);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyCustomDataFunction(this.getConditions(), this.source, this.ops);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }

    record CopyOperation(NbtPathArgument.NbtPath sourcePath, NbtPathArgument.NbtPath targetPath, MergeStrategy op) {
        public static final Codec<CopyOperation> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)NbtPathArgument.NbtPath.CODEC.fieldOf("source").forGetter(CopyOperation::sourcePath), (App)NbtPathArgument.NbtPath.CODEC.fieldOf("target").forGetter(CopyOperation::targetPath), (App)MergeStrategy.CODEC.fieldOf("op").forGetter(CopyOperation::op)).apply((Applicative)$$0, CopyOperation::new));

        public void apply(Supplier<Tag> $$0, Tag $$1) {
            try {
                List<Tag> $$2 = this.sourcePath.get($$1);
                if (!$$2.isEmpty()) {
                    this.op.merge($$0.get(), this.targetPath, $$2);
                }
            } catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }
    }

    public static abstract sealed class MergeStrategy
    extends Enum<MergeStrategy>
    implements StringRepresentable {
        public static final /* enum */ MergeStrategy REPLACE = new MergeStrategy("replace"){

            @Override
            public void merge(Tag $$0, NbtPathArgument.NbtPath $$1, List<Tag> $$2) throws CommandSyntaxException {
                $$1.set($$0, Iterables.getLast($$2));
            }
        };
        public static final /* enum */ MergeStrategy APPEND = new MergeStrategy("append"){

            @Override
            public void merge(Tag $$0, NbtPathArgument.NbtPath $$1, List<Tag> $$2) throws CommandSyntaxException {
                List<Tag> $$3 = $$1.getOrCreate($$0, ListTag::new);
                $$3.forEach($$12 -> {
                    if ($$12 instanceof ListTag) {
                        $$2.forEach($$1 -> ((ListTag)$$12).add($$1.copy()));
                    }
                });
            }
        };
        public static final /* enum */ MergeStrategy MERGE = new MergeStrategy("merge"){

            @Override
            public void merge(Tag $$0, NbtPathArgument.NbtPath $$1, List<Tag> $$2) throws CommandSyntaxException {
                List<Tag> $$3 = $$1.getOrCreate($$0, CompoundTag::new);
                $$3.forEach($$12 -> {
                    if ($$12 instanceof CompoundTag) {
                        $$2.forEach($$1 -> {
                            if ($$1 instanceof CompoundTag) {
                                ((CompoundTag)$$12).merge((CompoundTag)$$1);
                            }
                        });
                    }
                });
            }
        };
        public static final Codec<MergeStrategy> CODEC;
        private final String name;
        private static final /* synthetic */ MergeStrategy[] $VALUES;

        public static MergeStrategy[] values() {
            return (MergeStrategy[])$VALUES.clone();
        }

        public static MergeStrategy valueOf(String $$0) {
            return Enum.valueOf(MergeStrategy.class, $$0);
        }

        public abstract void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException;

        MergeStrategy(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ MergeStrategy[] a() {
            return new MergeStrategy[]{REPLACE, APPEND, MERGE};
        }

        static {
            $VALUES = MergeStrategy.a();
            CODEC = StringRepresentable.fromEnum(MergeStrategy::values);
        }
    }
}

