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
package net.minecraft.world.entity.variant;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ModelAndTexture<T>(T model, ClientAsset asset) {
    public ModelAndTexture(T $$0, ResourceLocation $$1) {
        this($$0, new ClientAsset($$1));
    }

    public static <T> MapCodec<ModelAndTexture<T>> codec(Codec<T> $$0, T $$1) {
        return RecordCodecBuilder.mapCodec($$2 -> $$2.group((App)$$0.optionalFieldOf("model", $$1).forGetter(ModelAndTexture::model), (App)ClientAsset.DEFAULT_FIELD_CODEC.forGetter(ModelAndTexture::asset)).apply((Applicative)$$2, ModelAndTexture::new));
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, ModelAndTexture<T>> streamCodec(StreamCodec<? super RegistryFriendlyByteBuf, T> $$0) {
        return StreamCodec.composite($$0, ModelAndTexture::model, ClientAsset.STREAM_CODEC, ModelAndTexture::asset, ModelAndTexture::new);
    }
}

