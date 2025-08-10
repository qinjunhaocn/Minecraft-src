/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.item.trading;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public class MerchantOffers
extends ArrayList<MerchantOffer> {
    public static final Codec<MerchantOffers> CODEC = MerchantOffer.CODEC.listOf().optionalFieldOf("Recipes", (Object)List.of()).xmap(MerchantOffers::new, Function.identity()).codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, MerchantOffers> STREAM_CODEC = MerchantOffer.STREAM_CODEC.apply(ByteBufCodecs.collection(MerchantOffers::new));

    public MerchantOffers() {
    }

    private MerchantOffers(int $$0) {
        super($$0);
    }

    private MerchantOffers(Collection<MerchantOffer> $$0) {
        super($$0);
    }

    @Nullable
    public MerchantOffer getRecipeFor(ItemStack $$0, ItemStack $$1, int $$2) {
        if ($$2 > 0 && $$2 < this.size()) {
            MerchantOffer $$3 = (MerchantOffer)this.get($$2);
            if ($$3.satisfiedBy($$0, $$1)) {
                return $$3;
            }
            return null;
        }
        for (int $$4 = 0; $$4 < this.size(); ++$$4) {
            MerchantOffer $$5 = (MerchantOffer)this.get($$4);
            if (!$$5.satisfiedBy($$0, $$1)) continue;
            return $$5;
        }
        return null;
    }

    public MerchantOffers copy() {
        MerchantOffers $$0 = new MerchantOffers(this.size());
        for (MerchantOffer $$1 : this) {
            $$0.add($$1.copy());
        }
        return $$0;
    }
}

