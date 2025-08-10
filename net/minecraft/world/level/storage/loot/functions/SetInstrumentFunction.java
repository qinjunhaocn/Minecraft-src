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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetInstrumentFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetInstrumentFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetInstrumentFunction.commonFields($$02).and((App)TagKey.hashedCodec(Registries.INSTRUMENT).fieldOf("options").forGetter($$0 -> $$0.options)).apply((Applicative)$$02, SetInstrumentFunction::new));
    private final TagKey<Instrument> options;

    private SetInstrumentFunction(List<LootItemCondition> $$0, TagKey<Instrument> $$1) {
        super($$0);
        this.options = $$1;
    }

    public LootItemFunctionType<SetInstrumentFunction> getType() {
        return LootItemFunctions.SET_INSTRUMENT;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        HolderLookup.RegistryLookup $$2 = $$1.getLevel().registryAccess().lookupOrThrow(Registries.INSTRUMENT);
        Optional<Holder<Instrument>> $$3 = $$2.getRandomElementOf(this.options, $$1.getRandom());
        if ($$3.isPresent()) {
            $$0.set(DataComponents.INSTRUMENT, new InstrumentComponent($$3.get()));
        }
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> setInstrumentOptions(TagKey<Instrument> $$0) {
        return SetInstrumentFunction.simpleBuilder($$1 -> new SetInstrumentFunction((List<LootItemCondition>)$$1, $$0));
    }
}

