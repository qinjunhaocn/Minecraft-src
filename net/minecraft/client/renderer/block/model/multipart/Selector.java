/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

public record Selector(Optional<Condition> condition, BlockStateModel.Unbaked variant) {
    public static final Codec<Selector> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Condition.CODEC.optionalFieldOf("when").forGetter(Selector::condition), (App)BlockStateModel.Unbaked.CODEC.fieldOf("apply").forGetter(Selector::variant)).apply((Applicative)$$0, Selector::new));

    public <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> $$02) {
        return this.condition.map($$1 -> $$1.instantiate($$02)).orElse($$0 -> true);
    }
}

