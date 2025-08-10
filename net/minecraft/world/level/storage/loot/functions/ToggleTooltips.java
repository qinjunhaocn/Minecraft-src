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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ToggleTooltips
extends LootItemConditionalFunction {
    public static final MapCodec<ToggleTooltips> CODEC = RecordCodecBuilder.mapCodec($$02 -> ToggleTooltips.commonFields($$02).and((App)Codec.unboundedMap(DataComponentType.CODEC, (Codec)Codec.BOOL).fieldOf("toggles").forGetter($$0 -> $$0.values)).apply((Applicative)$$02, ToggleTooltips::new));
    private final Map<DataComponentType<?>, Boolean> values;

    private ToggleTooltips(List<LootItemCondition> $$0, Map<DataComponentType<?>, Boolean> $$1) {
        super($$0);
        this.values = $$1;
    }

    @Override
    protected ItemStack run(ItemStack $$02, LootContext $$1) {
        $$02.update(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT, $$0 -> {
            Iterator<Map.Entry<DataComponentType<?>, Boolean>> iterator = this.values.entrySet().iterator();
            while (iterator.hasNext()) {
                boolean $$2;
                Map.Entry<DataComponentType<?>, Boolean> $$1;
                $$0 = $$0.withHidden($$1.getKey(), !($$2 = ($$1 = iterator.next()).getValue().booleanValue()));
            }
            return $$0;
        });
        return $$02;
    }

    public LootItemFunctionType<ToggleTooltips> getType() {
        return LootItemFunctions.TOGGLE_TOOLTIPS;
    }
}

