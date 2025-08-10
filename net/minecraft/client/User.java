/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.util.UndashedUuid
 */
package net.minecraft.client;

import com.mojang.util.UndashedUuid;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class User {
    private final String name;
    private final UUID uuid;
    private final String accessToken;
    private final Optional<String> xuid;
    private final Optional<String> clientId;
    private final Type type;

    public User(String $$0, UUID $$1, String $$2, Optional<String> $$3, Optional<String> $$4, Type $$5) {
        this.name = $$0;
        this.uuid = $$1;
        this.accessToken = $$2;
        this.xuid = $$3;
        this.clientId = $$4;
        this.type = $$5;
    }

    public String getSessionId() {
        return "token:" + this.accessToken + ":" + UndashedUuid.toString((UUID)this.uuid);
    }

    public UUID getProfileId() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public Optional<String> getClientId() {
        return this.clientId;
    }

    public Optional<String> getXuid() {
        return this.xuid;
    }

    public Type getType() {
        return this.type;
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type LEGACY = new Type("legacy");
        public static final /* enum */ Type MOJANG = new Type("mojang");
        public static final /* enum */ Type MSA = new Type("msa");
        private static final Map<String, Type> BY_NAME;
        private final String name;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private Type(String $$0) {
            this.name = $$0;
        }

        @Nullable
        public static Type byName(String $$0) {
            return BY_NAME.get($$0.toLowerCase(Locale.ROOT));
        }

        public String getName() {
            return this.name;
        }

        private static /* synthetic */ Type[] b() {
            return new Type[]{LEGACY, MOJANG, MSA};
        }

        static {
            $VALUES = Type.b();
            BY_NAME = Arrays.stream(Type.values()).collect(Collectors.toMap($$0 -> $$0.name, Function.identity()));
        }
    }
}

