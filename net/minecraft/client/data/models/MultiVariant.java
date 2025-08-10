/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models;

import java.util.List;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.resources.model.WeightedVariants;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

public record MultiVariant(WeightedList<Variant> variants) {
    public MultiVariant {
        if ($$0.isEmpty()) {
            throw new IllegalArgumentException("Variant list must contain at least one element");
        }
    }

    public MultiVariant with(VariantMutator $$0) {
        return new MultiVariant(this.variants.map($$0));
    }

    public BlockStateModel.Unbaked toUnbaked() {
        List<Weighted<Variant>> $$0 = this.variants.unwrap();
        return $$0.size() == 1 ? new SingleVariant.Unbaked((Variant)((Weighted)((Object)$$0.getFirst())).value()) : new WeightedVariants.Unbaked(this.variants.map(SingleVariant.Unbaked::new));
    }
}

