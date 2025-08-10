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
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record SetBlockProperties(BlockItemStateProperties properties, Vec3i offset, Optional<Holder<GameEvent>> triggerGameEvent) implements EnchantmentEntityEffect
{
    public static final MapCodec<SetBlockProperties> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)BlockItemStateProperties.CODEC.fieldOf("properties").forGetter(SetBlockProperties::properties), (App)Vec3i.CODEC.optionalFieldOf("offset", (Object)Vec3i.ZERO).forGetter(SetBlockProperties::offset), (App)GameEvent.CODEC.optionalFieldOf("trigger_game_event").forGetter(SetBlockProperties::triggerGameEvent)).apply((Applicative)$$0, SetBlockProperties::new));

    public SetBlockProperties(BlockItemStateProperties $$0) {
        this($$0, Vec3i.ZERO, Optional.of(GameEvent.BLOCK_CHANGE));
    }

    @Override
    public void apply(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$32, Vec3 $$4) {
        BlockState $$7;
        BlockPos $$5 = BlockPos.containing($$4).offset(this.offset);
        BlockState $$6 = $$32.level().getBlockState($$5);
        if ($$6 != ($$7 = this.properties.apply($$6)) && $$32.level().setBlock($$5, $$7, 3)) {
            this.triggerGameEvent.ifPresent($$3 -> $$0.gameEvent($$32, (Holder<GameEvent>)$$3, $$5));
        }
    }

    public MapCodec<SetBlockProperties> codec() {
        return CODEC;
    }
}

