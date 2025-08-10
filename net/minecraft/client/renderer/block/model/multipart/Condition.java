/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Keyable
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.renderer.block.model.multipart;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Keyable;
import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.client.renderer.block.model.multipart.CombinedCondition;
import net.minecraft.client.renderer.block.model.multipart.KeyValueCondition;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

@FunctionalInterface
public interface Condition {
    public static final Codec<Condition> CODEC = Codec.recursive((String)"condition", $$03 -> {
        Codec $$1 = Codec.simpleMap(CombinedCondition.Operation.CODEC, (Codec)$$03.listOf(), (Keyable)StringRepresentable.a(CombinedCondition.Operation.values())).codec().comapFlatMap($$0 -> {
            if ($$0.size() != 1) {
                return DataResult.error(() -> "Invalid map size for combiner condition, expected exactly one element");
            }
            Map.Entry $$1 = $$0.entrySet().iterator().next();
            return DataResult.success((Object)new CombinedCondition((CombinedCondition.Operation)$$1.getKey(), (List)$$1.getValue()));
        }, $$0 -> Map.of((Object)$$0.operation(), $$0.terms()));
        return Codec.either((Codec)$$1, KeyValueCondition.CODEC).flatComapMap($$02 -> (Condition)$$02.map($$0 -> $$0, $$0 -> $$0), $$0 -> {
            Condition condition = $$0;
            Objects.requireNonNull(condition);
            Condition $$1 = condition;
            int $$2 = 0;
            DataResult $$5 = switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{CombinedCondition.class, KeyValueCondition.class}, (Object)$$1, (int)$$2)) {
                case 0 -> {
                    CombinedCondition $$3 = (CombinedCondition)$$1;
                    yield DataResult.success((Object)Either.left((Object)$$3));
                }
                case 1 -> {
                    KeyValueCondition $$4 = (KeyValueCondition)$$1;
                    yield DataResult.success((Object)Either.right((Object)$$4));
                }
                default -> DataResult.error(() -> "Unrecognized condition");
            };
            return $$5;
        });
    });

    public <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> var1);
}

