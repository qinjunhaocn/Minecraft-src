/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.logging.LogUtils
 *  com.mojang.util.UUIDTypeAdapter
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.Exclude;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.RegionSelectionPreferenceDto;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.util.UUIDTypeAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;

public class RealmsServer
extends ValueObject
implements ReflectionBasedSerialization {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int NO_VALUE = -1;
    public static final Component WORLD_CLOSED_COMPONENT = Component.translatable("mco.play.button.realm.closed");
    @SerializedName(value="id")
    public long id = -1L;
    @Nullable
    @SerializedName(value="remoteSubscriptionId")
    public String remoteSubscriptionId;
    @Nullable
    @SerializedName(value="name")
    public String name;
    @SerializedName(value="motd")
    public String motd = "";
    @SerializedName(value="state")
    public State state = State.CLOSED;
    @Nullable
    @SerializedName(value="owner")
    public String owner;
    @SerializedName(value="ownerUUID")
    @JsonAdapter(value=UUIDTypeAdapter.class)
    public UUID ownerUUID = Util.NIL_UUID;
    @SerializedName(value="players")
    public List<PlayerInfo> players = Lists.newArrayList();
    @SerializedName(value="slots")
    private List<RealmsSlot> slotList = RealmsServer.createEmptySlots();
    @Exclude
    public Map<Integer, RealmsSlot> slots = new HashMap<Integer, RealmsSlot>();
    @SerializedName(value="expired")
    public boolean expired;
    @SerializedName(value="expiredTrial")
    public boolean expiredTrial = false;
    @SerializedName(value="daysLeft")
    public int daysLeft;
    @SerializedName(value="worldType")
    public WorldType worldType = WorldType.NORMAL;
    @SerializedName(value="isHardcore")
    public boolean isHardcore = false;
    @SerializedName(value="gameMode")
    public int gameMode = -1;
    @SerializedName(value="activeSlot")
    public int activeSlot = -1;
    @Nullable
    @SerializedName(value="minigameName")
    public String minigameName;
    @SerializedName(value="minigameId")
    public int minigameId = -1;
    @Nullable
    @SerializedName(value="minigameImage")
    public String minigameImage;
    @SerializedName(value="parentWorldId")
    public long parentRealmId = -1L;
    @Nullable
    @SerializedName(value="parentWorldName")
    public String parentWorldName;
    @SerializedName(value="activeVersion")
    public String activeVersion = "";
    @SerializedName(value="compatibility")
    public Compatibility compatibility = Compatibility.UNVERIFIABLE;
    @Nullable
    @SerializedName(value="regionSelectionPreference")
    public RegionSelectionPreferenceDto regionSelectionPreference;

    public String getDescription() {
        return this.motd;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nullable
    public String getMinigameName() {
        return this.minigameName;
    }

    public void setName(String $$0) {
        this.name = $$0;
    }

    public void setDescription(String $$0) {
        this.motd = $$0;
    }

    public static RealmsServer parse(GuardedSerializer $$0, String $$1) {
        try {
            RealmsServer $$2 = $$0.fromJson($$1, RealmsServer.class);
            if ($$2 == null) {
                LOGGER.error("Could not parse McoServer: {}", (Object)$$1);
                return new RealmsServer();
            }
            RealmsServer.finalize($$2);
            return $$2;
        } catch (Exception $$3) {
            LOGGER.error("Could not parse McoServer: {}", (Object)$$3.getMessage());
            return new RealmsServer();
        }
    }

    public static void finalize(RealmsServer $$0) {
        if ($$0.players == null) {
            $$0.players = Lists.newArrayList();
        }
        if ($$0.slotList == null) {
            $$0.slotList = RealmsServer.createEmptySlots();
        }
        if ($$0.slots == null) {
            $$0.slots = new HashMap<Integer, RealmsSlot>();
        }
        if ($$0.worldType == null) {
            $$0.worldType = WorldType.NORMAL;
        }
        if ($$0.activeVersion == null) {
            $$0.activeVersion = "";
        }
        if ($$0.compatibility == null) {
            $$0.compatibility = Compatibility.UNVERIFIABLE;
        }
        if ($$0.regionSelectionPreference == null) {
            $$0.regionSelectionPreference = RegionSelectionPreferenceDto.DEFAULT;
        }
        RealmsServer.sortInvited($$0);
        RealmsServer.finalizeSlots($$0);
    }

    private static void sortInvited(RealmsServer $$02) {
        $$02.players.sort(($$0, $$1) -> ComparisonChain.start().compareFalseFirst($$1.getAccepted(), $$0.getAccepted()).compare((Comparable<?>)((Object)$$0.getName().toLowerCase(Locale.ROOT)), (Comparable<?>)((Object)$$1.getName().toLowerCase(Locale.ROOT))).result());
    }

    private static void finalizeSlots(RealmsServer $$0) {
        $$0.slotList.forEach($$1 -> $$0.slots.put($$1.slotId, (RealmsSlot)$$1));
        for (int $$12 = 1; $$12 <= 3; ++$$12) {
            if ($$0.slots.containsKey($$12)) continue;
            $$0.slots.put($$12, RealmsSlot.defaults($$12));
        }
    }

    private static List<RealmsSlot> createEmptySlots() {
        ArrayList<RealmsSlot> $$0 = new ArrayList<RealmsSlot>();
        $$0.add(RealmsSlot.defaults(1));
        $$0.add(RealmsSlot.defaults(2));
        $$0.add(RealmsSlot.defaults(3));
        return $$0;
    }

    public boolean isCompatible() {
        return this.compatibility.isCompatible();
    }

    public boolean needsUpgrade() {
        return this.compatibility.needsUpgrade();
    }

    public boolean needsDowngrade() {
        return this.compatibility.needsDowngrade();
    }

    public boolean shouldPlayButtonBeActive() {
        boolean $$0 = !this.expired && this.state == State.OPEN;
        return $$0 && (this.isCompatible() || this.needsUpgrade() || this.isSelfOwnedServer());
    }

    private boolean isSelfOwnedServer() {
        return Minecraft.getInstance().isLocalPlayer(this.ownerUUID);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.name, this.motd, this.state, this.owner, this.expired});
    }

    public boolean equals(Object $$0) {
        if ($$0 == null) {
            return false;
        }
        if ($$0 == this) {
            return true;
        }
        if ($$0.getClass() != this.getClass()) {
            return false;
        }
        RealmsServer $$1 = (RealmsServer)$$0;
        return new EqualsBuilder().append(this.id, $$1.id).append(this.name, $$1.name).append(this.motd, $$1.motd).append((Object)this.state, (Object)$$1.state).append(this.owner, $$1.owner).append(this.expired, $$1.expired).append((Object)this.worldType, (Object)this.worldType).isEquals();
    }

    public RealmsServer clone() {
        RealmsServer $$0 = new RealmsServer();
        $$0.id = this.id;
        $$0.remoteSubscriptionId = this.remoteSubscriptionId;
        $$0.name = this.name;
        $$0.motd = this.motd;
        $$0.state = this.state;
        $$0.owner = this.owner;
        $$0.players = this.players;
        $$0.slotList = this.slotList.stream().map(RealmsSlot::clone).toList();
        $$0.slots = this.cloneSlots(this.slots);
        $$0.expired = this.expired;
        $$0.expiredTrial = this.expiredTrial;
        $$0.daysLeft = this.daysLeft;
        $$0.worldType = this.worldType;
        $$0.isHardcore = this.isHardcore;
        $$0.gameMode = this.gameMode;
        $$0.ownerUUID = this.ownerUUID;
        $$0.minigameName = this.minigameName;
        $$0.activeSlot = this.activeSlot;
        $$0.minigameId = this.minigameId;
        $$0.minigameImage = this.minigameImage;
        $$0.parentWorldName = this.parentWorldName;
        $$0.parentRealmId = this.parentRealmId;
        $$0.activeVersion = this.activeVersion;
        $$0.compatibility = this.compatibility;
        $$0.regionSelectionPreference = this.regionSelectionPreference != null ? this.regionSelectionPreference.clone() : null;
        return $$0;
    }

    public Map<Integer, RealmsSlot> cloneSlots(Map<Integer, RealmsSlot> $$0) {
        HashMap<Integer, RealmsSlot> $$1 = Maps.newHashMap();
        for (Map.Entry<Integer, RealmsSlot> $$2 : $$0.entrySet()) {
            $$1.put($$2.getKey(), new RealmsSlot($$2.getKey(), $$2.getValue().options.clone(), $$2.getValue().settings));
        }
        return $$1;
    }

    public boolean isSnapshotRealm() {
        return this.parentRealmId != -1L;
    }

    public boolean isMinigameActive() {
        return this.worldType == WorldType.MINIGAME;
    }

    public String getWorldName(int $$0) {
        if (this.name == null) {
            return this.slots.get((Object)Integer.valueOf((int)$$0)).options.getSlotName($$0);
        }
        return this.name + " (" + this.slots.get((Object)Integer.valueOf((int)$$0)).options.getSlotName($$0) + ")";
    }

    public ServerData toServerData(String $$0) {
        return new ServerData((String)Objects.requireNonNullElse((Object)this.name, (Object)"unknown server"), $$0, ServerData.Type.REALM);
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return this.clone();
    }

    public static final class State
    extends Enum<State> {
        public static final /* enum */ State CLOSED = new State();
        public static final /* enum */ State OPEN = new State();
        public static final /* enum */ State UNINITIALIZED = new State();
        private static final /* synthetic */ State[] $VALUES;

        public static State[] values() {
            return (State[])$VALUES.clone();
        }

        public static State valueOf(String $$0) {
            return Enum.valueOf(State.class, $$0);
        }

        private static /* synthetic */ State[] a() {
            return new State[]{CLOSED, OPEN, UNINITIALIZED};
        }

        static {
            $VALUES = State.a();
        }
    }

    public static final class WorldType
    extends Enum<WorldType> {
        public static final /* enum */ WorldType NORMAL = new WorldType();
        public static final /* enum */ WorldType MINIGAME = new WorldType();
        public static final /* enum */ WorldType ADVENTUREMAP = new WorldType();
        public static final /* enum */ WorldType EXPERIENCE = new WorldType();
        public static final /* enum */ WorldType INSPIRATION = new WorldType();
        private static final /* synthetic */ WorldType[] $VALUES;

        public static WorldType[] values() {
            return (WorldType[])$VALUES.clone();
        }

        public static WorldType valueOf(String $$0) {
            return Enum.valueOf(WorldType.class, $$0);
        }

        private static /* synthetic */ WorldType[] a() {
            return new WorldType[]{NORMAL, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION};
        }

        static {
            $VALUES = WorldType.a();
        }
    }

    public static final class Compatibility
    extends Enum<Compatibility> {
        public static final /* enum */ Compatibility UNVERIFIABLE = new Compatibility();
        public static final /* enum */ Compatibility INCOMPATIBLE = new Compatibility();
        public static final /* enum */ Compatibility RELEASE_TYPE_INCOMPATIBLE = new Compatibility();
        public static final /* enum */ Compatibility NEEDS_DOWNGRADE = new Compatibility();
        public static final /* enum */ Compatibility NEEDS_UPGRADE = new Compatibility();
        public static final /* enum */ Compatibility COMPATIBLE = new Compatibility();
        private static final /* synthetic */ Compatibility[] $VALUES;

        public static Compatibility[] values() {
            return (Compatibility[])$VALUES.clone();
        }

        public static Compatibility valueOf(String $$0) {
            return Enum.valueOf(Compatibility.class, $$0);
        }

        public boolean isCompatible() {
            return this == COMPATIBLE;
        }

        public boolean needsUpgrade() {
            return this == NEEDS_UPGRADE;
        }

        public boolean needsDowngrade() {
            return this == NEEDS_DOWNGRADE;
        }

        private static /* synthetic */ Compatibility[] d() {
            return new Compatibility[]{UNVERIFIABLE, INCOMPATIBLE, RELEASE_TYPE_INCOMPATIBLE, NEEDS_DOWNGRADE, NEEDS_UPGRADE, COMPATIBLE};
        }

        static {
            $VALUES = Compatibility.d();
        }
    }

    public static class McoServerComparator
    implements Comparator<RealmsServer> {
        private final String refOwner;

        public McoServerComparator(String $$0) {
            this.refOwner = $$0;
        }

        @Override
        public int compare(RealmsServer $$0, RealmsServer $$1) {
            return ComparisonChain.start().compareTrueFirst($$0.isSnapshotRealm(), $$1.isSnapshotRealm()).compareTrueFirst($$0.state == State.UNINITIALIZED, $$1.state == State.UNINITIALIZED).compareTrueFirst($$0.expiredTrial, $$1.expiredTrial).compareTrueFirst(Objects.equals($$0.owner, this.refOwner), Objects.equals($$1.owner, this.refOwner)).compareFalseFirst($$0.expired, $$1.expired).compareTrueFirst($$0.state == State.OPEN, $$1.state == State.OPEN).compare($$0.id, $$1.id).result();
        }

        @Override
        public /* synthetic */ int compare(Object object, Object object2) {
            return this.compare((RealmsServer)object, (RealmsServer)object2);
        }
    }
}

