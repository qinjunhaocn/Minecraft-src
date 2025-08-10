/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;

public class ClientboundPlayerInfoUpdatePacket
implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayerInfoUpdatePacket> STREAM_CODEC = Packet.codec(ClientboundPlayerInfoUpdatePacket::write, ClientboundPlayerInfoUpdatePacket::new);
    private final EnumSet<Action> actions;
    private final List<Entry> entries;

    public ClientboundPlayerInfoUpdatePacket(EnumSet<Action> $$0, Collection<ServerPlayer> $$1) {
        this.actions = $$0;
        this.entries = $$1.stream().map(Entry::new).toList();
    }

    public ClientboundPlayerInfoUpdatePacket(Action $$0, ServerPlayer $$1) {
        this.actions = EnumSet.of($$0);
        this.entries = List.of((Object)((Object)new Entry($$1)));
    }

    public static ClientboundPlayerInfoUpdatePacket createPlayerInitializing(Collection<ServerPlayer> $$0) {
        EnumSet<Action[]> $$1 = EnumSet.of(Action.ADD_PLAYER, new Action[]{Action.INITIALIZE_CHAT, Action.UPDATE_GAME_MODE, Action.UPDATE_LISTED, Action.UPDATE_LATENCY, Action.UPDATE_DISPLAY_NAME, Action.UPDATE_HAT, Action.UPDATE_LIST_ORDER});
        return new ClientboundPlayerInfoUpdatePacket($$1, $$0);
    }

    private ClientboundPlayerInfoUpdatePacket(RegistryFriendlyByteBuf $$02) {
        this.actions = $$02.readEnumSet(Action.class);
        this.entries = $$02.readList($$0 -> {
            EntryBuilder $$1 = new EntryBuilder($$0.readUUID());
            for (Action $$2 : this.actions) {
                $$2.reader.read($$1, (RegistryFriendlyByteBuf)((Object)$$0));
            }
            return $$1.build();
        });
    }

    private void write(RegistryFriendlyByteBuf $$02) {
        $$02.writeEnumSet(this.actions, Action.class);
        $$02.writeCollection(this.entries, ($$0, $$1) -> {
            $$0.writeUUID($$1.profileId());
            for (Action $$2 : this.actions) {
                $$2.writer.write((RegistryFriendlyByteBuf)((Object)$$0), (Entry)((Object)$$1));
            }
        });
    }

    @Override
    public PacketType<ClientboundPlayerInfoUpdatePacket> type() {
        return GamePacketTypes.CLIENTBOUND_PLAYER_INFO_UPDATE;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handlePlayerInfoUpdate(this);
    }

    public EnumSet<Action> actions() {
        return this.actions;
    }

    public List<Entry> entries() {
        return this.entries;
    }

    public List<Entry> newEntries() {
        return this.actions.contains((Object)Action.ADD_PLAYER) ? this.entries : List.of();
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("actions", this.actions).add("entries", this.entries).toString();
    }

    public static final class Entry
    extends Record {
        private final UUID profileId;
        @Nullable
        private final GameProfile profile;
        private final boolean listed;
        private final int latency;
        private final GameType gameMode;
        @Nullable
        private final Component displayName;
        final boolean showHat;
        final int listOrder;
        @Nullable
        final RemoteChatSession.Data chatSession;

        Entry(ServerPlayer $$0) {
            this($$0.getUUID(), $$0.getGameProfile(), true, $$0.connection.latency(), $$0.gameMode(), $$0.getTabListDisplayName(), $$0.isModelPartShown(PlayerModelPart.HAT), $$0.getTabListOrder(), Optionull.map($$0.getChatSession(), RemoteChatSession::asData));
        }

        public Entry(UUID $$0, @Nullable GameProfile $$1, boolean $$2, int $$3, GameType $$4, @Nullable Component $$5, boolean $$6, int $$7, @Nullable RemoteChatSession.Data $$8) {
            this.profileId = $$0;
            this.profile = $$1;
            this.listed = $$2;
            this.latency = $$3;
            this.gameMode = $$4;
            this.displayName = $$5;
            this.showHat = $$6;
            this.listOrder = $$7;
            this.chatSession = $$8;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "profileId;profile;listed;latency;gameMode;displayName;showHat;listOrder;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "showHat", "listOrder", "chatSession"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "profileId;profile;listed;latency;gameMode;displayName;showHat;listOrder;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "showHat", "listOrder", "chatSession"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "profileId;profile;listed;latency;gameMode;displayName;showHat;listOrder;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "showHat", "listOrder", "chatSession"}, this, $$0);
        }

        public UUID profileId() {
            return this.profileId;
        }

        @Nullable
        public GameProfile profile() {
            return this.profile;
        }

        public boolean listed() {
            return this.listed;
        }

        public int latency() {
            return this.latency;
        }

        public GameType gameMode() {
            return this.gameMode;
        }

        @Nullable
        public Component displayName() {
            return this.displayName;
        }

        public boolean showHat() {
            return this.showHat;
        }

        public int listOrder() {
            return this.listOrder;
        }

        @Nullable
        public RemoteChatSession.Data chatSession() {
            return this.chatSession;
        }
    }

    public static final class Action
    extends Enum<Action> {
        public static final /* enum */ Action ADD_PLAYER = new Action(($$0, $$1) -> {
            GameProfile $$2 = new GameProfile($$0.profileId, $$1.readUtf(16));
            $$2.getProperties().putAll((Multimap)ByteBufCodecs.GAME_PROFILE_PROPERTIES.decode($$1));
            $$0.profile = $$2;
        }, ($$0, $$1) -> {
            GameProfile $$2 = Objects.requireNonNull($$1.profile());
            $$0.writeUtf($$2.getName(), 16);
            ByteBufCodecs.GAME_PROFILE_PROPERTIES.encode($$0, $$2.getProperties());
        });
        public static final /* enum */ Action INITIALIZE_CHAT = new Action(($$0, $$1) -> {
            $$0.chatSession = $$1.readNullable(RemoteChatSession.Data::read);
        }, ($$0, $$1) -> $$0.writeNullable($$1.chatSession, RemoteChatSession.Data::write));
        public static final /* enum */ Action UPDATE_GAME_MODE = new Action(($$0, $$1) -> {
            $$0.gameMode = GameType.byId($$1.readVarInt());
        }, ($$0, $$1) -> $$0.writeVarInt($$1.gameMode().getId()));
        public static final /* enum */ Action UPDATE_LISTED = new Action(($$0, $$1) -> {
            $$0.listed = $$1.readBoolean();
        }, ($$0, $$1) -> $$0.writeBoolean($$1.listed()));
        public static final /* enum */ Action UPDATE_LATENCY = new Action(($$0, $$1) -> {
            $$0.latency = $$1.readVarInt();
        }, ($$0, $$1) -> $$0.writeVarInt($$1.latency()));
        public static final /* enum */ Action UPDATE_DISPLAY_NAME = new Action(($$0, $$1) -> {
            $$0.displayName = FriendlyByteBuf.readNullable($$1, ComponentSerialization.TRUSTED_STREAM_CODEC);
        }, ($$0, $$1) -> FriendlyByteBuf.writeNullable($$0, $$1.displayName(), ComponentSerialization.TRUSTED_STREAM_CODEC));
        public static final /* enum */ Action UPDATE_LIST_ORDER = new Action(($$0, $$1) -> {
            $$0.listOrder = $$1.readVarInt();
        }, ($$0, $$1) -> $$0.writeVarInt($$1.listOrder));
        public static final /* enum */ Action UPDATE_HAT = new Action(($$0, $$1) -> {
            $$0.showHat = $$1.readBoolean();
        }, ($$0, $$1) -> $$0.writeBoolean($$1.showHat));
        final Reader reader;
        final Writer writer;
        private static final /* synthetic */ Action[] $VALUES;

        public static Action[] values() {
            return (Action[])$VALUES.clone();
        }

        public static Action valueOf(String $$0) {
            return Enum.valueOf(Action.class, $$0);
        }

        private Action(Reader $$0, Writer $$1) {
            this.reader = $$0;
            this.writer = $$1;
        }

        private static /* synthetic */ Action[] a() {
            return new Action[]{ADD_PLAYER, INITIALIZE_CHAT, UPDATE_GAME_MODE, UPDATE_LISTED, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, UPDATE_LIST_ORDER, UPDATE_HAT};
        }

        static {
            $VALUES = Action.a();
        }

        public static interface Reader {
            public void read(EntryBuilder var1, RegistryFriendlyByteBuf var2);
        }

        public static interface Writer {
            public void write(RegistryFriendlyByteBuf var1, Entry var2);
        }
    }

    static class EntryBuilder {
        final UUID profileId;
        @Nullable
        GameProfile profile;
        boolean listed;
        int latency;
        GameType gameMode = GameType.DEFAULT_MODE;
        @Nullable
        Component displayName;
        boolean showHat;
        int listOrder;
        @Nullable
        RemoteChatSession.Data chatSession;

        EntryBuilder(UUID $$0) {
            this.profileId = $$0;
        }

        Entry build() {
            return new Entry(this.profileId, this.profile, this.listed, this.latency, this.gameMode, this.displayName, this.showHat, this.listOrder, this.chatSession);
        }
    }
}

