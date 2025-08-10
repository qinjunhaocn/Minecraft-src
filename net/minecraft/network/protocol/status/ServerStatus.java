/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.network.protocol.status;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public record ServerStatus(Component description, Optional<Players> players, Optional<Version> version, Optional<Favicon> favicon, boolean enforcesSecureChat) {
    public static final Codec<ServerStatus> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ComponentSerialization.CODEC.lenientOptionalFieldOf("description", (Object)CommonComponents.EMPTY).forGetter(ServerStatus::description), (App)Players.CODEC.lenientOptionalFieldOf("players").forGetter(ServerStatus::players), (App)Version.CODEC.lenientOptionalFieldOf("version").forGetter(ServerStatus::version), (App)Favicon.CODEC.lenientOptionalFieldOf("favicon").forGetter(ServerStatus::favicon), (App)Codec.BOOL.lenientOptionalFieldOf("enforcesSecureChat", (Object)false).forGetter(ServerStatus::enforcesSecureChat)).apply((Applicative)$$0, ServerStatus::new));

    public record Players(int max, int online, List<GameProfile> sample) {
        private static final Codec<GameProfile> PROFILE_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(GameProfile::getId), (App)Codec.STRING.fieldOf("name").forGetter(GameProfile::getName)).apply((Applicative)$$0, GameProfile::new));
        public static final Codec<Players> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.INT.fieldOf("max").forGetter(Players::max), (App)Codec.INT.fieldOf("online").forGetter(Players::online), (App)PROFILE_CODEC.listOf().lenientOptionalFieldOf("sample", (Object)List.of()).forGetter(Players::sample)).apply((Applicative)$$0, Players::new));
    }

    public record Version(String name, int protocol) {
        public static final Codec<Version> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.STRING.fieldOf("name").forGetter(Version::name), (App)Codec.INT.fieldOf("protocol").forGetter(Version::protocol)).apply((Applicative)$$0, Version::new));

        public static Version current() {
            WorldVersion $$0 = SharedConstants.getCurrentVersion();
            return new Version($$0.name(), $$0.protocolVersion());
        }
    }

    public static final class Favicon
    extends Record {
        private final byte[] iconBytes;
        private static final String PREFIX = "data:image/png;base64,";
        public static final Codec<Favicon> CODEC = Codec.STRING.comapFlatMap($$0 -> {
            if (!$$0.startsWith(PREFIX)) {
                return DataResult.error(() -> "Unknown format");
            }
            try {
                String $$1 = $$0.substring(PREFIX.length()).replaceAll("\n", "");
                byte[] $$2 = Base64.getDecoder().decode($$1.getBytes(StandardCharsets.UTF_8));
                return DataResult.success((Object)((Object)new Favicon($$2)));
            } catch (IllegalArgumentException $$3) {
                return DataResult.error(() -> "Malformed base64 server icon");
            }
        }, $$0 -> PREFIX + new String(Base64.getEncoder().encode($$0.iconBytes), StandardCharsets.UTF_8));

        public Favicon(byte[] $$0) {
            this.iconBytes = $$0;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Favicon.class, "iconBytes", "iconBytes"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Favicon.class, "iconBytes", "iconBytes"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Favicon.class, "iconBytes", "iconBytes"}, this, $$0);
        }

        public byte[] a() {
            return this.iconBytes;
        }
    }
}

