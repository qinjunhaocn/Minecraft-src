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
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FillPlayerHead
extends LootItemConditionalFunction {
    public static final MapCodec<FillPlayerHead> CODEC = RecordCodecBuilder.mapCodec($$02 -> FillPlayerHead.commonFields($$02).and((App)LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter($$0 -> $$0.entityTarget)).apply((Applicative)$$02, FillPlayerHead::new));
    private final LootContext.EntityTarget entityTarget;

    public FillPlayerHead(List<LootItemCondition> $$0, LootContext.EntityTarget $$1) {
        super($$0);
        this.entityTarget = $$1;
    }

    public LootItemFunctionType<FillPlayerHead> getType() {
        return LootItemFunctions.FILL_PLAYER_HEAD;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(this.entityTarget.getParam());
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        Entity entity;
        if ($$0.is(Items.PLAYER_HEAD) && (entity = $$1.getOptionalParameter(this.entityTarget.getParam())) instanceof Player) {
            Player $$2 = (Player)entity;
            $$0.set(DataComponents.PROFILE, new ResolvableProfile($$2.getGameProfile()));
        }
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> fillPlayerHead(LootContext.EntityTarget $$0) {
        return FillPlayerHead.simpleBuilder($$1 -> new FillPlayerHead((List<LootItemCondition>)$$1, $$0));
    }
}

