/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer.item.properties.select;

import com.google.common.collect.HashMultiset;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface SelectItemModelProperty<T> {
    @Nullable
    public T get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5);

    public Codec<T> valueCodec();

    public Type<? extends SelectItemModelProperty<T>, T> type();

    public record Type<P extends SelectItemModelProperty<T>, T>(MapCodec<SelectItemModel.UnbakedSwitch<P, T>> switchCodec) {
        public static <P extends SelectItemModelProperty<T>, T> Type<P, T> create(MapCodec<P> $$0, Codec<T> $$1) {
            MapCodec $$22 = RecordCodecBuilder.mapCodec($$2 -> $$2.group((App)$$0.forGetter(SelectItemModel.UnbakedSwitch::property), (App)Type.createCasesFieldCodec($$1).forGetter(SelectItemModel.UnbakedSwitch::cases)).apply((Applicative)$$2, SelectItemModel.UnbakedSwitch::new));
            return new Type<P, T>($$22);
        }

        public static <T> MapCodec<List<SelectItemModel.SwitchCase<T>>> createCasesFieldCodec(Codec<T> $$0) {
            return SelectItemModel.SwitchCase.codec($$0).listOf().validate(Type::validateCases).fieldOf("cases");
        }

        private static <T> DataResult<List<SelectItemModel.SwitchCase<T>>> validateCases(List<SelectItemModel.SwitchCase<T>> $$0) {
            if ($$0.isEmpty()) {
                return DataResult.error(() -> "Empty case list");
            }
            HashMultiset $$1 = HashMultiset.create();
            for (SelectItemModel.SwitchCase<T> $$2 : $$0) {
                $$1.addAll($$2.values());
            }
            if ($$1.size() != $$1.entrySet().size()) {
                return DataResult.error(() -> "Duplicate case conditions: " + $$1.entrySet().stream().filter($$0 -> $$0.getCount() > 1).map($$0 -> $$0.getElement().toString()).collect(Collectors.joining(", ")));
            }
            return DataResult.success($$0);
        }
    }
}

