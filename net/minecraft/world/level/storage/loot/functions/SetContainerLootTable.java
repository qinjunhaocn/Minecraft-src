/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerLootTable
extends LootItemConditionalFunction {
    public static final MapCodec<SetContainerLootTable> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetContainerLootTable.commonFields($$02).and($$02.group((App)LootTable.KEY_CODEC.fieldOf("name").forGetter($$0 -> $$0.name), (App)Codec.LONG.optionalFieldOf("seed", (Object)0L).forGetter($$0 -> $$0.seed), (App)BuiltInRegistries.BLOCK_ENTITY_TYPE.holderByNameCodec().fieldOf("type").forGetter($$0 -> $$0.type))).apply((Applicative)$$02, SetContainerLootTable::new));
    private final ResourceKey<LootTable> name;
    private final long seed;
    private final Holder<BlockEntityType<?>> type;

    private SetContainerLootTable(List<LootItemCondition> $$0, ResourceKey<LootTable> $$1, long $$2, Holder<BlockEntityType<?>> $$3) {
        super($$0);
        this.name = $$1;
        this.seed = $$2;
        this.type = $$3;
    }

    public LootItemFunctionType<SetContainerLootTable> getType() {
        return LootItemFunctions.SET_LOOT_TABLE;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        if ($$0.isEmpty()) {
            return $$0;
        }
        $$0.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.name, this.seed));
        return $$0;
    }

    @Override
    public void validate(ValidationContext $$0) {
        super.validate($$0);
        if (!$$0.allowsReferences()) {
            $$0.reportProblem(new ValidationContext.ReferenceNotAllowedProblem(this.name));
            return;
        }
        if ($$0.resolver().get(this.name).isEmpty()) {
            $$0.reportProblem(new ValidationContext.MissingReferenceProblem(this.name));
        }
    }

    public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> $$0, ResourceKey<LootTable> $$1) {
        return SetContainerLootTable.simpleBuilder($$2 -> new SetContainerLootTable((List<LootItemCondition>)$$2, $$1, 0L, (Holder<BlockEntityType<?>>)$$0.builtInRegistryHolder()));
    }

    public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> $$0, ResourceKey<LootTable> $$1, long $$2) {
        return SetContainerLootTable.simpleBuilder($$3 -> new SetContainerLootTable((List<LootItemCondition>)$$3, $$1, $$2, (Holder<BlockEntityType<?>>)$$0.builtInRegistryHolder()));
    }
}

