/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.crafting;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record TransmuteResult(Holder<Item> item, int count, DataComponentPatch components) {
    private static final Codec<TransmuteResult> FULL_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Item.CODEC.fieldOf("id").forGetter(TransmuteResult::item), (App)ExtraCodecs.intRange(1, 99).optionalFieldOf("count", (Object)1).forGetter(TransmuteResult::count), (App)DataComponentPatch.CODEC.optionalFieldOf("components", (Object)DataComponentPatch.EMPTY).forGetter(TransmuteResult::components)).apply((Applicative)$$0, TransmuteResult::new));
    public static final Codec<TransmuteResult> CODEC = Codec.withAlternative(FULL_CODEC, Item.CODEC, $$0 -> new TransmuteResult((Item)$$0.value())).validate(TransmuteResult::validate);
    public static final StreamCodec<RegistryFriendlyByteBuf, TransmuteResult> STREAM_CODEC = StreamCodec.composite(Item.STREAM_CODEC, TransmuteResult::item, ByteBufCodecs.VAR_INT, TransmuteResult::count, DataComponentPatch.STREAM_CODEC, TransmuteResult::components, TransmuteResult::new);

    public TransmuteResult(Item $$0) {
        this($$0.builtInRegistryHolder(), 1, DataComponentPatch.EMPTY);
    }

    private static DataResult<TransmuteResult> validate(TransmuteResult $$0) {
        return ItemStack.validateStrict(new ItemStack($$0.item, $$0.count, $$0.components)).map($$1 -> $$0);
    }

    public ItemStack apply(ItemStack $$0) {
        ItemStack $$1 = $$0.transmuteCopy(this.item.value(), this.count);
        $$1.applyComponents(this.components);
        return $$1;
    }

    public boolean isResultUnchanged(ItemStack $$0) {
        ItemStack $$1 = this.apply($$0);
        return $$1.getCount() == 1 && ItemStack.isSameItemSameComponents($$0, $$1);
    }

    public SlotDisplay display() {
        return new SlotDisplay.ItemStackSlotDisplay(new ItemStack(this.item, this.count, this.components));
    }
}

