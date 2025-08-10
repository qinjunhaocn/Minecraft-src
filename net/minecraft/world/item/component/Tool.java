/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.item.component;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record Tool(List<Rule> rules, float defaultMiningSpeed, int damagePerBlock, boolean canDestroyBlocksInCreative) {
    public static final Codec<Tool> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Rule.CODEC.listOf().fieldOf("rules").forGetter(Tool::rules), (App)Codec.FLOAT.optionalFieldOf("default_mining_speed", (Object)Float.valueOf(1.0f)).forGetter(Tool::defaultMiningSpeed), (App)ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("damage_per_block", (Object)1).forGetter(Tool::damagePerBlock), (App)Codec.BOOL.optionalFieldOf("can_destroy_blocks_in_creative", (Object)true).forGetter(Tool::canDestroyBlocksInCreative)).apply((Applicative)$$0, Tool::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, Tool> STREAM_CODEC = StreamCodec.composite(Rule.STREAM_CODEC.apply(ByteBufCodecs.list()), Tool::rules, ByteBufCodecs.FLOAT, Tool::defaultMiningSpeed, ByteBufCodecs.VAR_INT, Tool::damagePerBlock, ByteBufCodecs.BOOL, Tool::canDestroyBlocksInCreative, Tool::new);

    public float getMiningSpeed(BlockState $$0) {
        for (Rule $$1 : this.rules) {
            if (!$$1.speed.isPresent() || !$$0.is($$1.blocks)) continue;
            return $$1.speed.get().floatValue();
        }
        return this.defaultMiningSpeed;
    }

    public boolean isCorrectForDrops(BlockState $$0) {
        for (Rule $$1 : this.rules) {
            if (!$$1.correctForDrops.isPresent() || !$$0.is($$1.blocks)) continue;
            return $$1.correctForDrops.get();
        }
        return false;
    }

    public static final class Rule
    extends Record {
        final HolderSet<Block> blocks;
        final Optional<Float> speed;
        final Optional<Boolean> correctForDrops;
        public static final Codec<Rule> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(Rule::blocks), (App)ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("speed").forGetter(Rule::speed), (App)Codec.BOOL.optionalFieldOf("correct_for_drops").forGetter(Rule::correctForDrops)).apply((Applicative)$$0, Rule::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Rule> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderSet(Registries.BLOCK), Rule::blocks, ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional), Rule::speed, ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional), Rule::correctForDrops, Rule::new);

        public Rule(HolderSet<Block> $$0, Optional<Float> $$1, Optional<Boolean> $$2) {
            this.blocks = $$0;
            this.speed = $$1;
            this.correctForDrops = $$2;
        }

        public static Rule minesAndDrops(HolderSet<Block> $$0, float $$1) {
            return new Rule($$0, Optional.of(Float.valueOf($$1)), Optional.of(true));
        }

        public static Rule deniesDrops(HolderSet<Block> $$0) {
            return new Rule($$0, Optional.empty(), Optional.of(false));
        }

        public static Rule overrideSpeed(HolderSet<Block> $$0, float $$1) {
            return new Rule($$0, Optional.of(Float.valueOf($$1)), Optional.empty());
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Rule.class, "blocks;speed;correctForDrops", "blocks", "speed", "correctForDrops"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Rule.class, "blocks;speed;correctForDrops", "blocks", "speed", "correctForDrops"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Rule.class, "blocks;speed;correctForDrops", "blocks", "speed", "correctForDrops"}, this, $$0);
        }

        public HolderSet<Block> blocks() {
            return this.blocks;
        }

        public Optional<Float> speed() {
            return this.speed;
        }

        public Optional<Boolean> correctForDrops() {
            return this.correctForDrops;
        }
    }
}

