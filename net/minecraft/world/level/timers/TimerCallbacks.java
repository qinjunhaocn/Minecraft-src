/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.timers;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.timers.FunctionCallback;
import net.minecraft.world.level.timers.FunctionTagCallback;
import net.minecraft.world.level.timers.TimerCallback;

public class TimerCallbacks<C> {
    public static final TimerCallbacks<MinecraftServer> SERVER_CALLBACKS = new TimerCallbacks().register(ResourceLocation.withDefaultNamespace("function"), FunctionCallback.CODEC).register(ResourceLocation.withDefaultNamespace("function_tag"), FunctionTagCallback.CODEC);
    private final ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends TimerCallback<C>>> idMapper = new ExtraCodecs.LateBoundIdMapper();
    private final Codec<TimerCallback<C>> codec = this.idMapper.codec(ResourceLocation.CODEC).dispatch("Type", TimerCallback::codec, Function.identity());

    @VisibleForTesting
    public TimerCallbacks() {
    }

    public TimerCallbacks<C> register(ResourceLocation $$0, MapCodec<? extends TimerCallback<C>> $$1) {
        this.idMapper.put($$0, $$1);
        return this;
    }

    public Codec<TimerCallback<C>> codec() {
        return this.codec;
    }
}

