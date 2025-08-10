/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SmeltItemFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<SmeltItemFunction> CODEC = RecordCodecBuilder.mapCodec($$0 -> SmeltItemFunction.commonFields($$0).apply((Applicative)$$0, SmeltItemFunction::new));

    private SmeltItemFunction(List<LootItemCondition> $$0) {
        super($$0);
    }

    public LootItemFunctionType<SmeltItemFunction> getType() {
        return LootItemFunctions.FURNACE_SMELT;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        ItemStack $$4;
        if ($$0.isEmpty()) {
            return $$0;
        }
        SingleRecipeInput $$2 = new SingleRecipeInput($$0);
        Optional<RecipeHolder<SmeltingRecipe>> $$3 = $$1.getLevel().recipeAccess().getRecipeFor(RecipeType.SMELTING, $$2, $$1.getLevel());
        if ($$3.isPresent() && !($$4 = $$3.get().value().assemble($$2, (HolderLookup.Provider)$$1.getLevel().registryAccess())).isEmpty()) {
            return $$4.copyWithCount($$0.getCount());
        }
        LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)$$0);
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> smelted() {
        return SmeltItemFunction.simpleBuilder(SmeltItemFunction::new);
    }
}

