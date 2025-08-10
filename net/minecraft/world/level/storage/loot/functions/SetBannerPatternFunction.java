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
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBannerPatternFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetBannerPatternFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetBannerPatternFunction.commonFields($$02).and($$02.group((App)BannerPatternLayers.CODEC.fieldOf("patterns").forGetter($$0 -> $$0.patterns), (App)Codec.BOOL.fieldOf("append").forGetter($$0 -> $$0.append))).apply((Applicative)$$02, SetBannerPatternFunction::new));
    private final BannerPatternLayers patterns;
    private final boolean append;

    SetBannerPatternFunction(List<LootItemCondition> $$0, BannerPatternLayers $$1, boolean $$2) {
        super($$0);
        this.patterns = $$1;
        this.append = $$2;
    }

    @Override
    protected ItemStack run(ItemStack $$02, LootContext $$12) {
        if (this.append) {
            $$02.update(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY, this.patterns, ($$0, $$1) -> new BannerPatternLayers.Builder().addAll((BannerPatternLayers)$$0).addAll((BannerPatternLayers)$$1).build());
        } else {
            $$02.set(DataComponents.BANNER_PATTERNS, this.patterns);
        }
        return $$02;
    }

    public LootItemFunctionType<SetBannerPatternFunction> getType() {
        return LootItemFunctions.SET_BANNER_PATTERN;
    }

    public static Builder setBannerPattern(boolean $$0) {
        return new Builder($$0);
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final BannerPatternLayers.Builder patterns = new BannerPatternLayers.Builder();
        private final boolean append;

        Builder(boolean $$0) {
            this.append = $$0;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetBannerPatternFunction(this.getConditions(), this.patterns.build(), this.append);
        }

        public Builder addPattern(Holder<BannerPattern> $$0, DyeColor $$1) {
            this.patterns.add($$0, $$1);
            return this;
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }
}

