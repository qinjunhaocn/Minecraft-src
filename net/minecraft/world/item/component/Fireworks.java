/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.item.component;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.TooltipProvider;

public record Fireworks(int flightDuration, List<FireworkExplosion> explosions) implements TooltipProvider
{
    public static final int MAX_EXPLOSIONS = 256;
    public static final Codec<Fireworks> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.UNSIGNED_BYTE.optionalFieldOf("flight_duration", (Object)0).forGetter(Fireworks::flightDuration), (App)FireworkExplosion.CODEC.sizeLimitedListOf(256).optionalFieldOf("explosions", (Object)List.of()).forGetter(Fireworks::explosions)).apply((Applicative)$$0, Fireworks::new));
    public static final StreamCodec<ByteBuf, Fireworks> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Fireworks::flightDuration, FireworkExplosion.STREAM_CODEC.apply(ByteBufCodecs.list(256)), Fireworks::explosions, Fireworks::new);

    public Fireworks {
        if ($$1.size() > 256) {
            throw new IllegalArgumentException("Got " + $$1.size() + " explosions, but maximum is 256");
        }
    }

    @Override
    public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
        if (this.flightDuration > 0) {
            $$1.accept(Component.translatable("item.minecraft.firework_rocket.flight").append(CommonComponents.SPACE).append(String.valueOf(this.flightDuration)).withStyle(ChatFormatting.GRAY));
        }
        FireworkExplosion $$4 = null;
        int $$5 = 0;
        for (FireworkExplosion $$6 : this.explosions) {
            if ($$4 == null) {
                $$4 = $$6;
                $$5 = 1;
                continue;
            }
            if ($$4.equals($$6)) {
                ++$$5;
                continue;
            }
            Fireworks.addExplosionTooltip($$1, $$4, $$5);
            $$4 = $$6;
            $$5 = 1;
        }
        if ($$4 != null) {
            Fireworks.addExplosionTooltip($$1, $$4, $$5);
        }
    }

    private static void addExplosionTooltip(Consumer<Component> $$0, FireworkExplosion $$12, int $$2) {
        MutableComponent $$3 = $$12.shape().getName();
        if ($$2 == 1) {
            $$0.accept(Component.a("item.minecraft.firework_rocket.single_star", $$3).withStyle(ChatFormatting.GRAY));
        } else {
            $$0.accept(Component.a("item.minecraft.firework_rocket.multiple_stars", $$2, $$3).withStyle(ChatFormatting.GRAY));
        }
        $$12.addAdditionalTooltip($$1 -> $$0.accept(Component.literal("  ").append((Component)$$1)));
    }
}

