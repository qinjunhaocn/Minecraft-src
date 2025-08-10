/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetFireworkExplosionFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetFireworkExplosionFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetFireworkExplosionFunction.commonFields($$02).and($$02.group((App)FireworkExplosion.Shape.CODEC.optionalFieldOf("shape").forGetter($$0 -> $$0.shape), (App)FireworkExplosion.COLOR_LIST_CODEC.optionalFieldOf("colors").forGetter($$0 -> $$0.colors), (App)FireworkExplosion.COLOR_LIST_CODEC.optionalFieldOf("fade_colors").forGetter($$0 -> $$0.fadeColors), (App)Codec.BOOL.optionalFieldOf("trail").forGetter($$0 -> $$0.trail), (App)Codec.BOOL.optionalFieldOf("twinkle").forGetter($$0 -> $$0.twinkle))).apply((Applicative)$$02, SetFireworkExplosionFunction::new));
    public static final FireworkExplosion DEFAULT_VALUE = new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(), IntList.of(), false, false);
    final Optional<FireworkExplosion.Shape> shape;
    final Optional<IntList> colors;
    final Optional<IntList> fadeColors;
    final Optional<Boolean> trail;
    final Optional<Boolean> twinkle;

    public SetFireworkExplosionFunction(List<LootItemCondition> $$0, Optional<FireworkExplosion.Shape> $$1, Optional<IntList> $$2, Optional<IntList> $$3, Optional<Boolean> $$4, Optional<Boolean> $$5) {
        super($$0);
        this.shape = $$1;
        this.colors = $$2;
        this.fadeColors = $$3;
        this.trail = $$4;
        this.twinkle = $$5;
    }

    @Override
    protected ItemStack run(ItemStack $$0, LootContext $$1) {
        $$0.update(DataComponents.FIREWORK_EXPLOSION, DEFAULT_VALUE, this::apply);
        return $$0;
    }

    private FireworkExplosion apply(FireworkExplosion $$0) {
        return new FireworkExplosion(this.shape.orElseGet($$0::shape), this.colors.orElseGet($$0::colors), this.fadeColors.orElseGet($$0::fadeColors), this.trail.orElseGet($$0::hasTrail), this.twinkle.orElseGet($$0::hasTwinkle));
    }

    public LootItemFunctionType<SetFireworkExplosionFunction> getType() {
        return LootItemFunctions.SET_FIREWORK_EXPLOSION;
    }
}

