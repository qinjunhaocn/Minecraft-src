/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.slf4j.Logger;

public final class BannerPatternLayers
extends Record
implements TooltipProvider {
    final List<Layer> layers;
    static final Logger LOGGER = LogUtils.getLogger();
    public static final BannerPatternLayers EMPTY = new BannerPatternLayers(List.of());
    public static final Codec<BannerPatternLayers> CODEC = Layer.CODEC.listOf().xmap(BannerPatternLayers::new, BannerPatternLayers::layers);
    public static final StreamCodec<RegistryFriendlyByteBuf, BannerPatternLayers> STREAM_CODEC = Layer.STREAM_CODEC.apply(ByteBufCodecs.list()).map(BannerPatternLayers::new, BannerPatternLayers::layers);

    public BannerPatternLayers(List<Layer> $$0) {
        this.layers = $$0;
    }

    public BannerPatternLayers removeLast() {
        return new BannerPatternLayers(List.copyOf(this.layers.subList(0, this.layers.size() - 1)));
    }

    @Override
    public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
        for (int $$4 = 0; $$4 < Math.min(this.layers().size(), 6); ++$$4) {
            $$1.accept(this.layers().get($$4).description().withStyle(ChatFormatting.GRAY));
        }
    }

    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BannerPatternLayers.class, "layers", "layers"}, this);
    }

    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BannerPatternLayers.class, "layers", "layers"}, this);
    }

    public final boolean equals(Object $$0) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BannerPatternLayers.class, "layers", "layers"}, this, $$0);
    }

    public List<Layer> layers() {
        return this.layers;
    }

    public record Layer(Holder<BannerPattern> pattern, DyeColor color) {
        public static final Codec<Layer> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)BannerPattern.CODEC.fieldOf("pattern").forGetter(Layer::pattern), (App)DyeColor.CODEC.fieldOf("color").forGetter(Layer::color)).apply((Applicative)$$0, Layer::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Layer> STREAM_CODEC = StreamCodec.composite(BannerPattern.STREAM_CODEC, Layer::pattern, DyeColor.STREAM_CODEC, Layer::color, Layer::new);

        public MutableComponent description() {
            String $$0 = this.pattern.value().translationKey();
            return Component.translatable($$0 + "." + this.color.getName());
        }
    }

    public static class Builder {
        private final ImmutableList.Builder<Layer> layers = ImmutableList.builder();

        @Deprecated
        public Builder addIfRegistered(HolderGetter<BannerPattern> $$0, ResourceKey<BannerPattern> $$1, DyeColor $$2) {
            Optional<Holder.Reference<BannerPattern>> $$3 = $$0.get($$1);
            if ($$3.isEmpty()) {
                LOGGER.warn("Unable to find banner pattern with id: '{}'", (Object)$$1.location());
                return this;
            }
            return this.add((Holder<BannerPattern>)$$3.get(), $$2);
        }

        public Builder add(Holder<BannerPattern> $$0, DyeColor $$1) {
            return this.add(new Layer($$0, $$1));
        }

        public Builder add(Layer $$0) {
            this.layers.add((Object)$$0);
            return this;
        }

        public Builder addAll(BannerPatternLayers $$0) {
            this.layers.addAll($$0.layers);
            return this;
        }

        public BannerPatternLayers build() {
            return new BannerPatternLayers((List<Layer>)((Object)this.layers.build()));
        }
    }
}

