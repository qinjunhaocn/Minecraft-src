/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.item.component;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public record ResolvableProfile(Optional<String> name, Optional<UUID> id, PropertyMap properties, GameProfile gameProfile) {
    private static final Codec<ResolvableProfile> FULL_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.PLAYER_NAME.optionalFieldOf("name").forGetter(ResolvableProfile::name), (App)UUIDUtil.CODEC.optionalFieldOf("id").forGetter(ResolvableProfile::id), (App)ExtraCodecs.PROPERTY_MAP.optionalFieldOf("properties", (Object)new PropertyMap()).forGetter(ResolvableProfile::properties)).apply((Applicative)$$0, ResolvableProfile::new));
    public static final Codec<ResolvableProfile> CODEC = Codec.withAlternative(FULL_CODEC, ExtraCodecs.PLAYER_NAME, $$0 -> new ResolvableProfile(Optional.of($$0), Optional.empty(), new PropertyMap()));
    public static final StreamCodec<ByteBuf, ResolvableProfile> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.stringUtf8(16).apply(ByteBufCodecs::optional), ResolvableProfile::name, UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional), ResolvableProfile::id, ByteBufCodecs.GAME_PROFILE_PROPERTIES, ResolvableProfile::properties, ResolvableProfile::new);

    public ResolvableProfile(Optional<String> $$0, Optional<UUID> $$1, PropertyMap $$2) {
        this($$0, $$1, $$2, ResolvableProfile.createGameProfile($$1, $$0, $$2));
    }

    public ResolvableProfile(GameProfile $$0) {
        this(Optional.of($$0.getName()), Optional.of($$0.getId()), $$0.getProperties(), $$0);
    }

    @Nullable
    public ResolvableProfile pollResolve() {
        Optional $$1;
        if (this.isResolved()) {
            return this;
        }
        if (this.id.isPresent()) {
            Optional $$0 = SkullBlockEntity.fetchGameProfile(this.id.get()).getNow(null);
        } else {
            $$1 = SkullBlockEntity.fetchGameProfile((String)this.name.orElseThrow()).getNow(null);
        }
        if ($$1 != null) {
            return this.createProfile($$1);
        }
        return null;
    }

    public CompletableFuture<ResolvableProfile> resolve() {
        if (this.isResolved()) {
            return CompletableFuture.completedFuture(this);
        }
        if (this.id.isPresent()) {
            return SkullBlockEntity.fetchGameProfile(this.id.get()).thenApply(this::createProfile);
        }
        return SkullBlockEntity.fetchGameProfile((String)this.name.orElseThrow()).thenApply(this::createProfile);
    }

    private ResolvableProfile createProfile(Optional<GameProfile> $$0) {
        return new ResolvableProfile($$0.orElseGet(() -> ResolvableProfile.createGameProfile(this.id, this.name)));
    }

    private static GameProfile createGameProfile(Optional<UUID> $$0, Optional<String> $$1) {
        return new GameProfile($$0.orElse(Util.NIL_UUID), $$1.orElse(""));
    }

    private static GameProfile createGameProfile(Optional<UUID> $$0, Optional<String> $$1, PropertyMap $$2) {
        GameProfile $$3 = ResolvableProfile.createGameProfile($$0, $$1);
        $$3.getProperties().putAll((Multimap)$$2);
        return $$3;
    }

    public boolean isResolved() {
        if (!this.properties.isEmpty()) {
            return true;
        }
        return this.id.isPresent() == this.name.isPresent();
    }
}

