/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.enchantment.effects;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.Vec3;

public record ReplaceBlock(Vec3i offset, Optional<BlockPredicate> predicate, BlockStateProvider blockState, Optional<Holder<GameEvent>> triggerGameEvent) implements EnchantmentEntityEffect
{
    public static final MapCodec<ReplaceBlock> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Vec3i.CODEC.optionalFieldOf("offset", (Object)Vec3i.ZERO).forGetter(ReplaceBlock::offset), (App)BlockPredicate.CODEC.optionalFieldOf("predicate").forGetter(ReplaceBlock::predicate), (App)BlockStateProvider.CODEC.fieldOf("block_state").forGetter(ReplaceBlock::blockState), (App)GameEvent.CODEC.optionalFieldOf("trigger_game_event").forGetter(ReplaceBlock::triggerGameEvent)).apply((Applicative)$$0, ReplaceBlock::new));

    @Override
    public void apply(ServerLevel $$0, int $$1, EnchantedItemInUse $$22, Entity $$32, Vec3 $$4) {
        BlockPos $$5 = BlockPos.containing($$4).offset(this.offset);
        if (this.predicate.map($$2 -> $$2.test($$0, $$5)).orElse(true).booleanValue() && $$0.setBlockAndUpdate($$5, this.blockState.getState($$32.getRandom(), $$5))) {
            this.triggerGameEvent.ifPresent($$3 -> $$0.gameEvent($$32, (Holder<GameEvent>)$$3, $$5));
        }
    }

    public MapCodec<ReplaceBlock> codec() {
        return CODEC;
    }
}

