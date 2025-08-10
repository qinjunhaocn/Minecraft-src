/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface WeatheringCopper
extends ChangeOverTimeBlock<WeatherState> {
    public static final Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> ((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)ImmutableBiMap.builder().put(Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER)).put(Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER)).put(Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER)).put(Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER)).put(Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER)).put(Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER)).put(Blocks.CHISELED_COPPER, Blocks.EXPOSED_CHISELED_COPPER)).put(Blocks.EXPOSED_CHISELED_COPPER, Blocks.WEATHERED_CHISELED_COPPER)).put(Blocks.WEATHERED_CHISELED_COPPER, Blocks.OXIDIZED_CHISELED_COPPER)).put(Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB)).put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB)).put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB)).put(Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS)).put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS)).put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS)).put(Blocks.COPPER_DOOR, Blocks.EXPOSED_COPPER_DOOR)).put(Blocks.EXPOSED_COPPER_DOOR, Blocks.WEATHERED_COPPER_DOOR)).put(Blocks.WEATHERED_COPPER_DOOR, Blocks.OXIDIZED_COPPER_DOOR)).put(Blocks.COPPER_TRAPDOOR, Blocks.EXPOSED_COPPER_TRAPDOOR)).put(Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WEATHERED_COPPER_TRAPDOOR)).put(Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.OXIDIZED_COPPER_TRAPDOOR)).put(Blocks.COPPER_GRATE, Blocks.EXPOSED_COPPER_GRATE)).put(Blocks.EXPOSED_COPPER_GRATE, Blocks.WEATHERED_COPPER_GRATE)).put(Blocks.WEATHERED_COPPER_GRATE, Blocks.OXIDIZED_COPPER_GRATE)).put(Blocks.COPPER_BULB, Blocks.EXPOSED_COPPER_BULB)).put(Blocks.EXPOSED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB)).put(Blocks.WEATHERED_COPPER_BULB, Blocks.OXIDIZED_COPPER_BULB)).build());
    public static final Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> NEXT_BY_BLOCK.get().inverse());

    public static Optional<Block> getPrevious(Block $$0) {
        return Optional.ofNullable((Block)PREVIOUS_BY_BLOCK.get().get($$0));
    }

    public static Block getFirst(Block $$0) {
        Block $$1 = $$0;
        Block $$2 = (Block)PREVIOUS_BY_BLOCK.get().get($$1);
        while ($$2 != null) {
            $$1 = $$2;
            $$2 = (Block)PREVIOUS_BY_BLOCK.get().get($$1);
        }
        return $$1;
    }

    public static Optional<BlockState> getPrevious(BlockState $$0) {
        return WeatheringCopper.getPrevious($$0.getBlock()).map($$1 -> $$1.withPropertiesOf($$0));
    }

    public static Optional<Block> getNext(Block $$0) {
        return Optional.ofNullable((Block)NEXT_BY_BLOCK.get().get($$0));
    }

    public static BlockState getFirst(BlockState $$0) {
        return WeatheringCopper.getFirst($$0.getBlock()).withPropertiesOf($$0);
    }

    @Override
    default public Optional<BlockState> getNext(BlockState $$0) {
        return WeatheringCopper.getNext($$0.getBlock()).map($$1 -> $$1.withPropertiesOf($$0));
    }

    @Override
    default public float getChanceModifier() {
        if (this.getAge() == WeatherState.UNAFFECTED) {
            return 0.75f;
        }
        return 1.0f;
    }

    public static final class WeatherState
    extends Enum<WeatherState>
    implements StringRepresentable {
        public static final /* enum */ WeatherState UNAFFECTED = new WeatherState("unaffected");
        public static final /* enum */ WeatherState EXPOSED = new WeatherState("exposed");
        public static final /* enum */ WeatherState WEATHERED = new WeatherState("weathered");
        public static final /* enum */ WeatherState OXIDIZED = new WeatherState("oxidized");
        public static final Codec<WeatherState> CODEC;
        private final String name;
        private static final /* synthetic */ WeatherState[] $VALUES;

        public static WeatherState[] values() {
            return (WeatherState[])$VALUES.clone();
        }

        public static WeatherState valueOf(String $$0) {
            return Enum.valueOf(WeatherState.class, $$0);
        }

        private WeatherState(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ WeatherState[] a() {
            return new WeatherState[]{UNAFFECTED, EXPOSED, WEATHERED, OXIDIZED};
        }

        static {
            $VALUES = WeatherState.a();
            CODEC = StringRepresentable.fromEnum(WeatherState::values);
        }
    }
}

