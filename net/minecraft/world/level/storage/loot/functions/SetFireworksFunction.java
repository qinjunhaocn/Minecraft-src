/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ListOperation;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetFireworksFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetFireworksFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetFireworksFunction.commonFields($$02).and($$02.group((App)ListOperation.StandAlone.codec(FireworkExplosion.CODEC, 256).optionalFieldOf("explosions").forGetter($$0 -> $$0.explosions), (App)ExtraCodecs.UNSIGNED_BYTE.optionalFieldOf("flight_duration").forGetter($$0 -> $$0.flightDuration))).apply((Applicative)$$02, SetFireworksFunction::new));
    public static final Fireworks DEFAULT_VALUE = new Fireworks(0, List.of());
    private final Optional<ListOperation.StandAlone<FireworkExplosion>> explosions;
    private final Optional<Integer> flightDuration;

    protected SetFireworksFunction(List<LootItemCondition> $$0, Optional<ListOperation.StandAlone<FireworkExplosion>> $$1, Optional<Integer> $$2) {
        super($$0);
        this.explosions = $$1;
        this.flightDuration = $$2;
    }

    @Override
    protected ItemStack run(ItemStack $$0, LootContext $$1) {
        $$0.update(DataComponents.FIREWORKS, DEFAULT_VALUE, this::apply);
        return $$0;
    }

    private Fireworks apply(Fireworks $$0) {
        return new Fireworks(this.flightDuration.orElseGet($$0::flightDuration), this.explosions.map($$1 -> $$1.apply($$0.explosions())).orElse($$0.explosions()));
    }

    public LootItemFunctionType<SetFireworksFunction> getType() {
        return LootItemFunctions.SET_FIREWORKS;
    }
}

