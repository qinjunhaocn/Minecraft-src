/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  java.lang.MatchException
 */
package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.PngInfo;
import org.slf4j.Logger;

public class ServerData {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_ICON_SIZE = 1024;
    public String name;
    public String ip;
    public Component status;
    public Component motd;
    @Nullable
    public ServerStatus.Players players;
    public long ping;
    public int protocol = SharedConstants.getCurrentVersion().protocolVersion();
    public Component version = Component.literal(SharedConstants.getCurrentVersion().name());
    public List<Component> playerList = Collections.emptyList();
    private ServerPackStatus packStatus = ServerPackStatus.PROMPT;
    @Nullable
    private byte[] iconBytes;
    private Type type;
    private State state = State.INITIAL;

    public ServerData(String $$0, String $$1, Type $$2) {
        this.name = $$0;
        this.ip = $$1;
        this.type = $$2;
    }

    public CompoundTag write() {
        CompoundTag $$0 = new CompoundTag();
        $$0.putString("name", this.name);
        $$0.putString("ip", this.ip);
        $$0.storeNullable("icon", ExtraCodecs.BASE64_STRING, this.iconBytes);
        $$0.store(ServerPackStatus.FIELD_CODEC, this.packStatus);
        return $$0;
    }

    public ServerPackStatus getResourcePackStatus() {
        return this.packStatus;
    }

    public void setResourcePackStatus(ServerPackStatus $$0) {
        this.packStatus = $$0;
    }

    public static ServerData read(CompoundTag $$0) {
        ServerData $$1 = new ServerData($$0.getStringOr("name", ""), $$0.getStringOr("ip", ""), Type.OTHER);
        $$1.a($$0.read("icon", ExtraCodecs.BASE64_STRING).orElse(null));
        $$1.setResourcePackStatus($$0.read(ServerPackStatus.FIELD_CODEC).orElse(ServerPackStatus.PROMPT));
        return $$1;
    }

    @Nullable
    public byte[] c() {
        return this.iconBytes;
    }

    public void a(@Nullable byte[] $$0) {
        this.iconBytes = $$0;
    }

    public boolean isLan() {
        return this.type == Type.LAN;
    }

    public boolean isRealm() {
        return this.type == Type.REALM;
    }

    public Type type() {
        return this.type;
    }

    public void copyNameIconFrom(ServerData $$0) {
        this.ip = $$0.ip;
        this.name = $$0.name;
        this.iconBytes = $$0.iconBytes;
    }

    public void copyFrom(ServerData $$0) {
        this.copyNameIconFrom($$0);
        this.setResourcePackStatus($$0.getResourcePackStatus());
        this.type = $$0.type;
    }

    public State state() {
        return this.state;
    }

    public void setState(State $$0) {
        this.state = $$0;
    }

    @Nullable
    public static byte[] b(@Nullable byte[] $$0) {
        if ($$0 != null) {
            try {
                PngInfo $$1 = PngInfo.a($$0);
                if ($$1.width() <= 1024 && $$1.height() <= 1024) {
                    return $$0;
                }
            } catch (IOException $$2) {
                LOGGER.warn("Failed to decode server icon", $$2);
            }
        }
        return null;
    }

    public static final class ServerPackStatus
    extends Enum<ServerPackStatus> {
        public static final /* enum */ ServerPackStatus ENABLED = new ServerPackStatus("enabled");
        public static final /* enum */ ServerPackStatus DISABLED = new ServerPackStatus("disabled");
        public static final /* enum */ ServerPackStatus PROMPT = new ServerPackStatus("prompt");
        public static final MapCodec<ServerPackStatus> FIELD_CODEC;
        private final Component name;
        private static final /* synthetic */ ServerPackStatus[] $VALUES;

        public static ServerPackStatus[] values() {
            return (ServerPackStatus[])$VALUES.clone();
        }

        public static ServerPackStatus valueOf(String $$0) {
            return Enum.valueOf(ServerPackStatus.class, $$0);
        }

        private ServerPackStatus(String $$0) {
            this.name = Component.translatable("addServer.resourcePack." + $$0);
        }

        public Component getName() {
            return this.name;
        }

        private static /* synthetic */ ServerPackStatus[] b() {
            return new ServerPackStatus[]{ENABLED, DISABLED, PROMPT};
        }

        static {
            $VALUES = ServerPackStatus.b();
            FIELD_CODEC = Codec.BOOL.optionalFieldOf("acceptTextures").xmap($$02 -> $$02.map($$0 -> $$0 != false ? ENABLED : DISABLED).orElse(PROMPT), $$0 -> switch ($$0.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> Optional.of(true);
                case 1 -> Optional.of(false);
                case 2 -> Optional.empty();
            });
        }
    }

    public static final class State
    extends Enum<State> {
        public static final /* enum */ State INITIAL = new State();
        public static final /* enum */ State PINGING = new State();
        public static final /* enum */ State UNREACHABLE = new State();
        public static final /* enum */ State INCOMPATIBLE = new State();
        public static final /* enum */ State SUCCESSFUL = new State();
        private static final /* synthetic */ State[] $VALUES;

        public static State[] values() {
            return (State[])$VALUES.clone();
        }

        public static State valueOf(String $$0) {
            return Enum.valueOf(State.class, $$0);
        }

        private static /* synthetic */ State[] a() {
            return new State[]{INITIAL, PINGING, UNREACHABLE, INCOMPATIBLE, SUCCESSFUL};
        }

        static {
            $VALUES = State.a();
        }
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type LAN = new Type();
        public static final /* enum */ Type REALM = new Type();
        public static final /* enum */ Type OTHER = new Type();
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private static /* synthetic */ Type[] a() {
            return new Type[]{LAN, REALM, OTHER};
        }

        static {
            $VALUES = Type.a();
        }
    }
}

