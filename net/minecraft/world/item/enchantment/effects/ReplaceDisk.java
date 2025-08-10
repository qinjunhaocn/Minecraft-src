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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.Vec3;

public record ReplaceDisk(LevelBasedValue radius, LevelBasedValue height, Vec3i offset, Optional<BlockPredicate> predicate, BlockStateProvider blockState, Optional<Holder<GameEvent>> triggerGameEvent) implements EnchantmentEntityEffect
{
    public static final MapCodec<ReplaceDisk> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)LevelBasedValue.CODEC.fieldOf("radius").forGetter(ReplaceDisk::radius), (App)LevelBasedValue.CODEC.fieldOf("height").forGetter(ReplaceDisk::height), (App)Vec3i.CODEC.optionalFieldOf("offset", (Object)Vec3i.ZERO).forGetter(ReplaceDisk::offset), (App)BlockPredicate.CODEC.optionalFieldOf("predicate").forGetter(ReplaceDisk::predicate), (App)BlockStateProvider.CODEC.fieldOf("block_state").forGetter(ReplaceDisk::blockState), (App)GameEvent.CODEC.optionalFieldOf("trigger_game_event").forGetter(ReplaceDisk::triggerGameEvent)).apply((Applicative)$$0, ReplaceDisk::new));

    @Override
    public void apply(ServerLevel $$0, int $$1, EnchantedItemInUse $$22, Entity $$32, Vec3 $$4) {
        BlockPos $$5 = BlockPos.containing($$4).offset(this.offset);
        RandomSource $$6 = $$32.getRandom();
        int $$7 = (int)this.radius.calculate($$1);
        int $$8 = (int)this.height.calculate($$1);
        for (BlockPos $$9 : BlockPos.betweenClosed($$5.offset(-$$7, 0, -$$7), $$5.offset($$7, Math.min($$8 - 1, 0), $$7))) {
            if (!($$9.distToCenterSqr($$4.x(), (double)$$9.getY() + 0.5, $$4.z()) < (double)Mth.square($$7)) || !this.predicate.map($$2 -> $$2.test($$0, $$9)).orElse(true).booleanValue() || !$$0.setBlockAndUpdate($$9, this.blockState.getState($$6, $$9))) continue;
            this.triggerGameEvent.ifPresent($$3 -> $$0.gameEvent($$32, (Holder<GameEvent>)$$3, $$9));
        }
    }

    public MapCodec<ReplaceDisk> codec() {
        return CODEC;
    }
}

