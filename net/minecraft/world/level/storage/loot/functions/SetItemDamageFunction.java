/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.slf4j.Logger;

public class SetItemDamageFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<SetItemDamageFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetItemDamageFunction.commonFields($$02).and($$02.group((App)NumberProviders.CODEC.fieldOf("damage").forGetter($$0 -> $$0.damage), (App)Codec.BOOL.fieldOf("add").orElse((Object)false).forGetter($$0 -> $$0.add))).apply((Applicative)$$02, SetItemDamageFunction::new));
    private final NumberProvider damage;
    private final boolean add;

    private SetItemDamageFunction(List<LootItemCondition> $$0, NumberProvider $$1, boolean $$2) {
        super($$0);
        this.damage = $$1;
        this.add = $$2;
    }

    public LootItemFunctionType<SetItemDamageFunction> getType() {
        return LootItemFunctions.SET_DAMAGE;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.damage.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        if ($$0.isDamageableItem()) {
            int $$2 = $$0.getMaxDamage();
            float $$3 = this.add ? 1.0f - (float)$$0.getDamageValue() / (float)$$2 : 0.0f;
            float $$4 = 1.0f - Mth.clamp(this.damage.getFloat($$1) + $$3, 0.0f, 1.0f);
            $$0.setDamageValue(Mth.floor($$4 * (float)$$2));
        } else {
            LOGGER.warn("Couldn't set damage of loot item {}", (Object)$$0);
        }
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> setDamage(NumberProvider $$0) {
        return SetItemDamageFunction.simpleBuilder($$1 -> new SetItemDamageFunction((List<LootItemCondition>)$$1, $$0, false));
    }

    public static LootItemConditionalFunction.Builder<?> setDamage(NumberProvider $$0, boolean $$1) {
        return SetItemDamageFunction.simpleBuilder($$2 -> new SetItemDamageFunction((List<LootItemCondition>)$$2, $$0, $$1));
    }
}

