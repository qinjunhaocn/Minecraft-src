/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.saveddata.maps;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public record MapDecoration(Holder<MapDecorationType> type, byte x, byte y, byte rot, Optional<Component> name) {
    public static final StreamCodec<RegistryFriendlyByteBuf, MapDecoration> STREAM_CODEC = StreamCodec.composite(MapDecorationType.STREAM_CODEC, MapDecoration::type, ByteBufCodecs.BYTE, MapDecoration::x, ByteBufCodecs.BYTE, MapDecoration::y, ByteBufCodecs.BYTE, MapDecoration::rot, ComponentSerialization.OPTIONAL_STREAM_CODEC, MapDecoration::name, MapDecoration::new);

    public MapDecoration {
        $$3 = (byte)($$3 & 0xF);
    }

    public ResourceLocation getSpriteLocation() {
        return this.type.value().assetId();
    }

    public boolean renderOnFrame() {
        return this.type.value().showOnItemFrame();
    }
}

