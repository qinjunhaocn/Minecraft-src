/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyBonusCount
extends LootItemConditionalFunction {
    private static final Map<ResourceLocation, FormulaType> FORMULAS = Stream.of(BinomialWithBonusCount.TYPE, OreDrops.TYPE, UniformBonusCount.TYPE).collect(Collectors.toMap(FormulaType::id, Function.identity()));
    private static final Codec<FormulaType> FORMULA_TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap($$0 -> {
        FormulaType $$1 = FORMULAS.get($$0);
        if ($$1 != null) {
            return DataResult.success((Object)((Object)$$1));
        }
        return DataResult.error(() -> "No formula type with id: '" + String.valueOf($$0) + "'");
    }, FormulaType::id);
    private static final MapCodec<Formula> FORMULA_CODEC = ExtraCodecs.dispatchOptionalValue("formula", "parameters", FORMULA_TYPE_CODEC, Formula::getType, FormulaType::codec);
    public static final MapCodec<ApplyBonusCount> CODEC = RecordCodecBuilder.mapCodec($$02 -> ApplyBonusCount.commonFields($$02).and($$02.group((App)Enchantment.CODEC.fieldOf("enchantment").forGetter($$0 -> $$0.enchantment), (App)FORMULA_CODEC.forGetter($$0 -> $$0.formula))).apply((Applicative)$$02, ApplyBonusCount::new));
    private final Holder<Enchantment> enchantment;
    private final Formula formula;

    private ApplyBonusCount(List<LootItemCondition> $$0, Holder<Enchantment> $$1, Formula $$2) {
        super($$0);
        this.enchantment = $$1;
        this.formula = $$2;
    }

    public LootItemFunctionType<ApplyBonusCount> getType() {
        return LootItemFunctions.APPLY_BONUS;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.TOOL);
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        ItemStack $$2 = $$1.getOptionalParameter(LootContextParams.TOOL);
        if ($$2 != null) {
            int $$3 = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, $$2);
            int $$4 = this.formula.calculateNewCount($$1.getRandom(), $$0.getCount(), $$3);
            $$0.setCount($$4);
        }
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> addBonusBinomialDistributionCount(Holder<Enchantment> $$0, float $$1, int $$2) {
        return ApplyBonusCount.simpleBuilder($$3 -> new ApplyBonusCount((List<LootItemCondition>)$$3, $$0, new BinomialWithBonusCount($$2, $$1)));
    }

    public static LootItemConditionalFunction.Builder<?> addOreBonusCount(Holder<Enchantment> $$0) {
        return ApplyBonusCount.simpleBuilder($$1 -> new ApplyBonusCount((List<LootItemCondition>)$$1, $$0, new OreDrops()));
    }

    public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Holder<Enchantment> $$0) {
        return ApplyBonusCount.simpleBuilder($$1 -> new ApplyBonusCount((List<LootItemCondition>)$$1, $$0, new UniformBonusCount(1)));
    }

    public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Holder<Enchantment> $$0, int $$1) {
        return ApplyBonusCount.simpleBuilder($$2 -> new ApplyBonusCount((List<LootItemCondition>)$$2, $$0, new UniformBonusCount($$1)));
    }

    static interface Formula {
        public int calculateNewCount(RandomSource var1, int var2, int var3);

        public FormulaType getType();
    }

    record UniformBonusCount(int bonusMultiplier) implements Formula
    {
        public static final Codec<UniformBonusCount> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.INT.fieldOf("bonusMultiplier").forGetter(UniformBonusCount::bonusMultiplier)).apply((Applicative)$$0, UniformBonusCount::new));
        public static final FormulaType TYPE = new FormulaType(ResourceLocation.withDefaultNamespace("uniform_bonus_count"), CODEC);

        @Override
        public int calculateNewCount(RandomSource $$0, int $$1, int $$2) {
            return $$1 + $$0.nextInt(this.bonusMultiplier * $$2 + 1);
        }

        @Override
        public FormulaType getType() {
            return TYPE;
        }
    }

    record OreDrops() implements Formula
    {
        public static final Codec<OreDrops> CODEC = Codec.unit(OreDrops::new);
        public static final FormulaType TYPE = new FormulaType(ResourceLocation.withDefaultNamespace("ore_drops"), CODEC);

        @Override
        public int calculateNewCount(RandomSource $$0, int $$1, int $$2) {
            if ($$2 > 0) {
                int $$3 = $$0.nextInt($$2 + 2) - 1;
                if ($$3 < 0) {
                    $$3 = 0;
                }
                return $$1 * ($$3 + 1);
            }
            return $$1;
        }

        @Override
        public FormulaType getType() {
            return TYPE;
        }
    }

    record BinomialWithBonusCount(int extraRounds, float probability) implements Formula
    {
        private static final Codec<BinomialWithBonusCount> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.INT.fieldOf("extra").forGetter(BinomialWithBonusCount::extraRounds), (App)Codec.FLOAT.fieldOf("probability").forGetter(BinomialWithBonusCount::probability)).apply((Applicative)$$0, BinomialWithBonusCount::new));
        public static final FormulaType TYPE = new FormulaType(ResourceLocation.withDefaultNamespace("binomial_with_bonus_count"), CODEC);

        @Override
        public int calculateNewCount(RandomSource $$0, int $$1, int $$2) {
            for (int $$3 = 0; $$3 < $$2 + this.extraRounds; ++$$3) {
                if (!($$0.nextFloat() < this.probability)) continue;
                ++$$1;
            }
            return $$1;
        }

        @Override
        public FormulaType getType() {
            return TYPE;
        }
    }

    record FormulaType(ResourceLocation id, Codec<? extends Formula> codec) {
    }
}

