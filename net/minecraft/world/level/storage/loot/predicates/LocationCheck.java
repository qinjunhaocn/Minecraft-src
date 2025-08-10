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
package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.phys.Vec3;

public record LocationCheck(Optional<LocationPredicate> predicate, BlockPos offset) implements LootItemCondition
{
    private static final MapCodec<BlockPos> OFFSET_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.INT.optionalFieldOf("offsetX", (Object)0).forGetter(Vec3i::getX), (App)Codec.INT.optionalFieldOf("offsetY", (Object)0).forGetter(Vec3i::getY), (App)Codec.INT.optionalFieldOf("offsetZ", (Object)0).forGetter(Vec3i::getZ)).apply((Applicative)$$0, BlockPos::new));
    public static final MapCodec<LocationCheck> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)LocationPredicate.CODEC.optionalFieldOf("predicate").forGetter(LocationCheck::predicate), (App)OFFSET_CODEC.forGetter(LocationCheck::offset)).apply((Applicative)$$0, LocationCheck::new));

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.LOCATION_CHECK;
    }

    @Override
    public boolean test(LootContext $$0) {
        Vec3 $$1 = $$0.getOptionalParameter(LootContextParams.ORIGIN);
        return $$1 != null && (this.predicate.isEmpty() || this.predicate.get().matches($$0.getLevel(), $$1.x() + (double)this.offset.getX(), $$1.y() + (double)this.offset.getY(), $$1.z() + (double)this.offset.getZ()));
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.ORIGIN);
    }

    public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder $$0) {
        return () -> new LocationCheck(Optional.of($$0.build()), BlockPos.ZERO);
    }

    public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder $$0, BlockPos $$1) {
        return () -> new LocationCheck(Optional.of($$0.build()), $$1);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }
}

