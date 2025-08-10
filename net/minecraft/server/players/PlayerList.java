/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class PlayerList {
    public static final File USERBANLIST_FILE = new File("banned-players.json");
    public static final File IPBANLIST_FILE = new File("banned-ips.json");
    public static final File OPLIST_FILE = new File("ops.json");
    public static final File WHITELIST_FILE = new File("whitelist.json");
    public static final Component CHAT_FILTERED_FULL = Component.translatable("chat.filtered_full");
    public static final Component DUPLICATE_LOGIN_DISCONNECT_MESSAGE = Component.translatable("multiplayer.disconnect.duplicate_login");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SEND_PLAYER_INFO_INTERVAL = 600;
    private static final SimpleDateFormat BAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    private final MinecraftServer server;
    private final List<ServerPlayer> players = Lists.newArrayList();
    private final Map<UUID, ServerPlayer> playersByUUID = Maps.newHashMap();
    private final UserBanList bans = new UserBanList(USERBANLIST_FILE);
    private final IpBanList ipBans = new IpBanList(IPBANLIST_FILE);
    private final ServerOpList ops = new ServerOpList(OPLIST_FILE);
    private final UserWhiteList whitelist = new UserWhiteList(WHITELIST_FILE);
    private final Map<UUID, ServerStatsCounter> stats = Maps.newHashMap();
    private final Map<UUID, PlayerAdvancements> advancements = Maps.newHashMap();
    private final PlayerDataStorage playerIo;
    private boolean doWhiteList;
    private final LayeredRegistryAccess<RegistryLayer> registries;
    protected final int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private boolean allowCommandsForAllPlayers;
    private static final boolean ALLOW_LOGOUTIVATOR = false;
    private int sendAllPlayerInfoIn;

    public PlayerList(MinecraftServer $$0, LayeredRegistryAccess<RegistryLayer> $$1, PlayerDataStorage $$2, int $$3) {
        this.server = $$0;
        this.registries = $$1;
        this.maxPlayers = $$3;
        this.playerIo = $$2;
    }

    public void placeNewPlayer(Connection $$02, ServerPlayer $$12, CommonListenerCookie $$2) {
        String $$7;
        GameProfile $$3 = $$12.getGameProfile();
        GameProfileCache $$4 = this.server.getProfileCache();
        if ($$4 != null) {
            Optional<GameProfile> $$5 = $$4.get($$3.getId());
            String $$6 = $$5.map(GameProfile::getName).orElse($$3.getName());
            $$4.add($$3);
        } else {
            $$7 = $$3.getName();
        }
        try (ProblemReporter.ScopedCollector $$8 = new ProblemReporter.ScopedCollector($$12.problemPath(), LOGGER);){
            MutableComponent $$23;
            ServerLevel $$13;
            Optional<ValueInput> $$9 = this.load($$12, $$8);
            ResourceKey<Level> $$10 = $$9.flatMap($$0 -> $$0.read("Dimension", Level.RESOURCE_KEY_CODEC)).orElse(Level.OVERWORLD);
            ServerLevel $$11 = this.server.getLevel($$10);
            if ($$11 == null) {
                LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", (Object)$$10);
                ServerLevel $$122 = this.server.overworld();
            } else {
                $$13 = $$11;
            }
            $$12.setServerLevel($$13);
            if ($$9.isEmpty()) {
                $$12.snapTo($$12.adjustSpawnLocation($$13, $$13.getSharedSpawnPos()).getBottomCenter(), $$13.getSharedSpawnAngle(), 0.0f);
            }
            $$13.waitForChunkAndEntities($$12.chunkPosition(), 1);
            String $$14 = $$02.getLoggableAddress(this.server.logIPs());
            LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", $$12.getName().getString(), $$14, $$12.getId(), $$12.getX(), $$12.getY(), $$12.getZ());
            LevelData $$15 = $$13.getLevelData();
            $$12.loadGameTypes($$9.orElse(null));
            ServerGamePacketListenerImpl $$16 = new ServerGamePacketListenerImpl(this.server, $$02, $$12, $$2);
            $$02.setupInboundProtocol(GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(this.server.registryAccess()), $$16), $$16);
            GameRules $$17 = $$13.getGameRules();
            boolean $$18 = $$17.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
            boolean $$19 = $$17.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);
            boolean $$20 = $$17.getBoolean(GameRules.RULE_LIMITED_CRAFTING);
            $$16.send(new ClientboundLoginPacket($$12.getId(), $$15.isHardcore(), this.server.levelKeys(), this.getMaxPlayers(), this.viewDistance, this.simulationDistance, $$19, !$$18, $$20, $$12.createCommonSpawnInfo($$13), this.server.enforceSecureProfile()));
            $$16.send(new ClientboundChangeDifficultyPacket($$15.getDifficulty(), $$15.isDifficultyLocked()));
            $$16.send(new ClientboundPlayerAbilitiesPacket($$12.getAbilities()));
            $$16.send(new ClientboundSetHeldSlotPacket($$12.getInventory().getSelectedSlot()));
            RecipeManager $$21 = this.server.getRecipeManager();
            $$16.send(new ClientboundUpdateRecipesPacket($$21.getSynchronizedItemProperties(), $$21.getSynchronizedStonecutterRecipes()));
            this.sendPlayerPermissionLevel($$12);
            $$12.getStats().markAllDirty();
            $$12.getRecipeBook().sendInitialRecipeBook($$12);
            this.updateEntireScoreboard($$13.getScoreboard(), $$12);
            this.server.invalidateStatus();
            if ($$12.getGameProfile().getName().equalsIgnoreCase($$7)) {
                MutableComponent $$22 = Component.a("multiplayer.player.joined", $$12.getDisplayName());
            } else {
                $$23 = Component.a("multiplayer.player.joined.renamed", $$12.getDisplayName(), $$7);
            }
            this.broadcastSystemMessage($$23.withStyle(ChatFormatting.YELLOW), false);
            $$16.teleport($$12.getX(), $$12.getY(), $$12.getZ(), $$12.getYRot(), $$12.getXRot());
            ServerStatus $$24 = this.server.getStatus();
            if ($$24 != null && !$$2.transferred()) {
                $$12.sendServerStatus($$24);
            }
            $$12.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(this.players));
            this.players.add($$12);
            this.playersByUUID.put($$12.getUUID(), $$12);
            this.broadcastAll(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of((Object)$$12)));
            this.sendLevelInfo($$12, $$13);
            $$13.addNewPlayer($$12);
            this.server.getCustomBossEvents().onPlayerConnect($$12);
            this.sendActivePlayerEffects($$12);
            $$9.ifPresent($$1 -> {
                $$12.loadAndSpawnEnderPearls((ValueInput)$$1);
                $$12.loadAndSpawnParentVehicle((ValueInput)$$1);
            });
            $$12.initInventoryMenu();
        }
    }

    protected void updateEntireScoreboard(ServerScoreboard $$0, ServerPlayer $$1) {
        HashSet<Objective> $$2 = Sets.newHashSet();
        for (PlayerTeam $$3 : $$0.getPlayerTeams()) {
            $$1.connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket($$3, true));
        }
        for (DisplaySlot $$4 : DisplaySlot.values()) {
            Objective $$5 = $$0.getDisplayObjective($$4);
            if ($$5 == null || $$2.contains($$5)) continue;
            List<Packet<?>> $$6 = $$0.getStartTrackingPackets($$5);
            for (Packet<?> $$7 : $$6) {
                $$1.connection.send($$7);
            }
            $$2.add($$5);
        }
    }

    public void addWorldborderListener(ServerLevel $$0) {
        $$0.getWorldBorder().addListener(new BorderChangeListener(){

            @Override
            public void onBorderSizeSet(WorldBorder $$0, double $$1) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderSizePacket($$0));
            }

            @Override
            public void onBorderSizeLerping(WorldBorder $$0, double $$1, double $$2, long $$3) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderLerpSizePacket($$0));
            }

            @Override
            public void onBorderCenterSet(WorldBorder $$0, double $$1, double $$2) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderCenterPacket($$0));
            }

            @Override
            public void onBorderSetWarningTime(WorldBorder $$0, int $$1) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderWarningDelayPacket($$0));
            }

            @Override
            public void onBorderSetWarningBlocks(WorldBorder $$0, int $$1) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderWarningDistancePacket($$0));
            }

            @Override
            public void onBorderSetDamagePerBlock(WorldBorder $$0, double $$1) {
            }

            @Override
            public void onBorderSetDamageSafeZOne(WorldBorder $$0, double $$1) {
            }
        });
    }

    public Optional<ValueInput> load(ServerPlayer $$0, ProblemReporter $$1) {
        Optional<ValueInput> $$5;
        CompoundTag $$2 = this.server.getWorldData().getLoadedPlayerTag();
        if (this.server.isSingleplayerOwner($$0.getGameProfile()) && $$2 != null) {
            ValueInput $$3 = TagValueInput.create($$1, (HolderLookup.Provider)$$0.registryAccess(), $$2);
            Optional<ValueInput> $$4 = Optional.of($$3);
            $$0.load($$3);
            LOGGER.debug("loading single player");
        } else {
            $$5 = this.playerIo.load((Player)$$0, $$1);
        }
        return $$5;
    }

    protected void save(ServerPlayer $$0) {
        PlayerAdvancements $$2;
        this.playerIo.save($$0);
        ServerStatsCounter $$1 = this.stats.get($$0.getUUID());
        if ($$1 != null) {
            $$1.save();
        }
        if (($$2 = this.advancements.get($$0.getUUID())) != null) {
            $$2.save();
        }
    }

    public void remove(ServerPlayer $$02) {
        Object $$2;
        ServerLevel $$1 = $$02.level();
        $$02.awardStat(Stats.LEAVE_GAME);
        this.save($$02);
        if ($$02.isPassenger() && ((Entity)($$2 = $$02.getRootVehicle())).hasExactlyOnePlayerPassenger()) {
            LOGGER.debug("Removing player mount");
            $$02.stopRiding();
            ((Entity)$$2).getPassengersAndSelf().forEach($$0 -> $$0.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER));
        }
        $$02.unRide();
        for (ThrownEnderpearl $$3 : $$02.getEnderPearls()) {
            $$3.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
        $$1.removePlayerImmediately($$02, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        $$02.getAdvancements().stopListening();
        this.players.remove($$02);
        this.server.getCustomBossEvents().onPlayerDisconnect($$02);
        UUID $$4 = $$02.getUUID();
        ServerPlayer $$5 = this.playersByUUID.get($$4);
        if ($$5 == $$02) {
            this.playersByUUID.remove($$4);
            this.stats.remove($$4);
            this.advancements.remove($$4);
        }
        this.broadcastAll(new ClientboundPlayerInfoRemovePacket(List.of((Object)$$02.getUUID())));
    }

    @Nullable
    public Component canPlayerLogin(SocketAddress $$0, GameProfile $$1) {
        if (this.bans.isBanned($$1)) {
            UserBanListEntry $$2 = (UserBanListEntry)this.bans.get($$1);
            MutableComponent $$3 = Component.a("multiplayer.disconnect.banned.reason", $$2.getReason());
            if ($$2.getExpires() != null) {
                $$3.append(Component.a("multiplayer.disconnect.banned.expiration", BAN_DATE_FORMAT.format($$2.getExpires())));
            }
            return $$3;
        }
        if (!this.isWhiteListed($$1)) {
            return Component.translatable("multiplayer.disconnect.not_whitelisted");
        }
        if (this.ipBans.isBanned($$0)) {
            IpBanListEntry $$4 = this.ipBans.get($$0);
            MutableComponent $$5 = Component.a("multiplayer.disconnect.banned_ip.reason", $$4.getReason());
            if ($$4.getExpires() != null) {
                $$5.append(Component.a("multiplayer.disconnect.banned_ip.expiration", BAN_DATE_FORMAT.format($$4.getExpires())));
            }
            return $$5;
        }
        if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit($$1)) {
            return Component.translatable("multiplayer.disconnect.server_full");
        }
        return null;
    }

    public boolean disconnectAllPlayersWithProfile(GameProfile $$0) {
        UUID $$1 = $$0.getId();
        Set<ServerPlayer> $$2 = Sets.newIdentityHashSet();
        for (ServerPlayer $$3 : this.players) {
            if (!$$3.getUUID().equals($$1)) continue;
            $$2.add($$3);
        }
        ServerPlayer $$4 = this.playersByUUID.get($$0.getId());
        if ($$4 != null) {
            $$2.add($$4);
        }
        for (ServerPlayer $$5 : $$2) {
            $$5.connection.disconnect(DUPLICATE_LOGIN_DISCONNECT_MESSAGE);
        }
        return !$$2.isEmpty();
    }

    public ServerPlayer respawn(ServerPlayer $$0, boolean $$1, Entity.RemovalReason $$2) {
        BlockPos $$13;
        BlockState $$14;
        ServerLevel $$12;
        this.players.remove($$0);
        $$0.level().removePlayerImmediately($$0, $$2);
        TeleportTransition $$3 = $$0.findRespawnPositionAndUseSpawnBlock(!$$1, TeleportTransition.DO_NOTHING);
        ServerLevel $$4 = $$3.newLevel();
        ServerPlayer $$5 = new ServerPlayer(this.server, $$4, $$0.getGameProfile(), $$0.clientInformation());
        $$5.connection = $$0.connection;
        $$5.restoreFrom($$0, $$1);
        $$5.setId($$0.getId());
        $$5.setMainArm($$0.getMainArm());
        if (!$$3.missingRespawnBlock()) {
            $$5.copyRespawnPosition($$0);
        }
        for (String $$6 : $$0.getTags()) {
            $$5.addTag($$6);
        }
        Vec3 $$7 = $$3.position();
        $$5.snapTo($$7.x, $$7.y, $$7.z, $$3.yRot(), $$3.xRot());
        if ($$3.missingRespawnBlock()) {
            $$5.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0f));
        }
        byte $$8 = $$1 ? (byte)1 : 0;
        ServerLevel $$9 = $$5.level();
        LevelData $$10 = $$9.getLevelData();
        $$5.connection.send(new ClientboundRespawnPacket($$5.createCommonSpawnInfo($$9), $$8));
        $$5.connection.teleport($$5.getX(), $$5.getY(), $$5.getZ(), $$5.getYRot(), $$5.getXRot());
        $$5.connection.send(new ClientboundSetDefaultSpawnPositionPacket($$4.getSharedSpawnPos(), $$4.getSharedSpawnAngle()));
        $$5.connection.send(new ClientboundChangeDifficultyPacket($$10.getDifficulty(), $$10.isDifficultyLocked()));
        $$5.connection.send(new ClientboundSetExperiencePacket($$5.experienceProgress, $$5.totalExperience, $$5.experienceLevel));
        this.sendActivePlayerEffects($$5);
        this.sendLevelInfo($$5, $$4);
        this.sendPlayerPermissionLevel($$5);
        $$4.addRespawnedPlayer($$5);
        this.players.add($$5);
        this.playersByUUID.put($$5.getUUID(), $$5);
        $$5.initInventoryMenu();
        $$5.setHealth($$5.getHealth());
        ServerPlayer.RespawnConfig $$11 = $$5.getRespawnConfig();
        if (!$$1 && $$11 != null && ($$12 = this.server.getLevel($$11.dimension())) != null && ($$14 = $$12.getBlockState($$13 = $$11.pos())).is(Blocks.RESPAWN_ANCHOR)) {
            $$5.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, $$13.getX(), $$13.getY(), $$13.getZ(), 1.0f, 1.0f, $$4.getRandom().nextLong()));
        }
        return $$5;
    }

    public void sendActivePlayerEffects(ServerPlayer $$0) {
        this.sendActiveEffects($$0, $$0.connection);
    }

    public void sendActiveEffects(LivingEntity $$0, ServerGamePacketListenerImpl $$1) {
        for (MobEffectInstance $$2 : $$0.getActiveEffects()) {
            $$1.send(new ClientboundUpdateMobEffectPacket($$0.getId(), $$2, false));
        }
    }

    public void sendPlayerPermissionLevel(ServerPlayer $$0) {
        GameProfile $$1 = $$0.getGameProfile();
        int $$2 = this.server.getProfilePermissions($$1);
        this.sendPlayerPermissionLevel($$0, $$2);
    }

    public void tick() {
        if (++this.sendAllPlayerInfoIn > 600) {
            this.broadcastAll(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY), this.players));
            this.sendAllPlayerInfoIn = 0;
        }
    }

    public void broadcastAll(Packet<?> $$0) {
        for (ServerPlayer $$1 : this.players) {
            $$1.connection.send($$0);
        }
    }

    public void broadcastAll(Packet<?> $$0, ResourceKey<Level> $$1) {
        for (ServerPlayer $$2 : this.players) {
            if ($$2.level().dimension() != $$1) continue;
            $$2.connection.send($$0);
        }
    }

    public void broadcastSystemToTeam(Player $$0, Component $$1) {
        PlayerTeam $$2 = $$0.getTeam();
        if ($$2 == null) {
            return;
        }
        Collection<String> $$3 = ((Team)$$2).getPlayers();
        for (String $$4 : $$3) {
            ServerPlayer $$5 = this.getPlayerByName($$4);
            if ($$5 == null || $$5 == $$0) continue;
            $$5.sendSystemMessage($$1);
        }
    }

    public void broadcastSystemToAllExceptTeam(Player $$0, Component $$1) {
        PlayerTeam $$2 = $$0.getTeam();
        if ($$2 == null) {
            this.broadcastSystemMessage($$1, false);
            return;
        }
        for (int $$3 = 0; $$3 < this.players.size(); ++$$3) {
            ServerPlayer $$4 = this.players.get($$3);
            if ($$4.getTeam() == $$2) continue;
            $$4.sendSystemMessage($$1);
        }
    }

    public String[] e() {
        String[] $$0 = new String[this.players.size()];
        for (int $$1 = 0; $$1 < this.players.size(); ++$$1) {
            $$0[$$1] = this.players.get($$1).getGameProfile().getName();
        }
        return $$0;
    }

    public UserBanList getBans() {
        return this.bans;
    }

    public IpBanList getIpBans() {
        return this.ipBans;
    }

    public void op(GameProfile $$0) {
        this.ops.add(new ServerOpListEntry($$0, this.server.getOperatorUserPermissionLevel(), this.ops.canBypassPlayerLimit($$0)));
        ServerPlayer $$1 = this.getPlayer($$0.getId());
        if ($$1 != null) {
            this.sendPlayerPermissionLevel($$1);
        }
    }

    public void deop(GameProfile $$0) {
        this.ops.remove($$0);
        ServerPlayer $$1 = this.getPlayer($$0.getId());
        if ($$1 != null) {
            this.sendPlayerPermissionLevel($$1);
        }
    }

    private void sendPlayerPermissionLevel(ServerPlayer $$0, int $$1) {
        if ($$0.connection != null) {
            byte $$4;
            if ($$1 <= 0) {
                int $$2 = 24;
            } else if ($$1 >= 4) {
                int $$3 = 28;
            } else {
                $$4 = (byte)(24 + $$1);
            }
            $$0.connection.send(new ClientboundEntityEventPacket($$0, $$4));
        }
        this.server.getCommands().sendCommands($$0);
    }

    public boolean isWhiteListed(GameProfile $$0) {
        return !this.doWhiteList || this.ops.contains($$0) || this.whitelist.contains($$0);
    }

    public boolean isOp(GameProfile $$0) {
        return this.ops.contains($$0) || this.server.isSingleplayerOwner($$0) && this.server.getWorldData().isAllowCommands() || this.allowCommandsForAllPlayers;
    }

    @Nullable
    public ServerPlayer getPlayerByName(String $$0) {
        int $$1 = this.players.size();
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            ServerPlayer $$3 = this.players.get($$2);
            if (!$$3.getGameProfile().getName().equalsIgnoreCase($$0)) continue;
            return $$3;
        }
        return null;
    }

    public void broadcast(@Nullable Player $$0, double $$1, double $$2, double $$3, double $$4, ResourceKey<Level> $$5, Packet<?> $$6) {
        for (int $$7 = 0; $$7 < this.players.size(); ++$$7) {
            double $$11;
            double $$10;
            double $$9;
            ServerPlayer $$8 = this.players.get($$7);
            if ($$8 == $$0 || $$8.level().dimension() != $$5 || !(($$9 = $$1 - $$8.getX()) * $$9 + ($$10 = $$2 - $$8.getY()) * $$10 + ($$11 = $$3 - $$8.getZ()) * $$11 < $$4 * $$4)) continue;
            $$8.connection.send($$6);
        }
    }

    public void saveAll() {
        for (int $$0 = 0; $$0 < this.players.size(); ++$$0) {
            this.save(this.players.get($$0));
        }
    }

    public UserWhiteList getWhiteList() {
        return this.whitelist;
    }

    public String[] j() {
        return this.whitelist.a();
    }

    public ServerOpList getOps() {
        return this.ops;
    }

    public String[] l() {
        return this.ops.a();
    }

    public void reloadWhiteList() {
    }

    public void sendLevelInfo(ServerPlayer $$0, ServerLevel $$1) {
        WorldBorder $$2 = this.server.overworld().getWorldBorder();
        $$0.connection.send(new ClientboundInitializeBorderPacket($$2));
        $$0.connection.send(new ClientboundSetTimePacket($$1.getGameTime(), $$1.getDayTime(), $$1.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
        $$0.connection.send(new ClientboundSetDefaultSpawnPositionPacket($$1.getSharedSpawnPos(), $$1.getSharedSpawnAngle()));
        if ($$1.isRaining()) {
            $$0.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0f));
            $$0.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, $$1.getRainLevel(1.0f)));
            $$0.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, $$1.getThunderLevel(1.0f)));
        }
        $$0.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START, 0.0f));
        this.server.tickRateManager().updateJoiningPlayer($$0);
    }

    public void sendAllPlayerInfo(ServerPlayer $$0) {
        $$0.inventoryMenu.sendAllDataToRemote();
        $$0.resetSentInfo();
        $$0.connection.send(new ClientboundSetHeldSlotPacket($$0.getInventory().getSelectedSlot()));
    }

    public int getPlayerCount() {
        return this.players.size();
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public boolean isUsingWhitelist() {
        return this.doWhiteList;
    }

    public void setUsingWhiteList(boolean $$0) {
        this.doWhiteList = $$0;
    }

    public List<ServerPlayer> getPlayersWithAddress(String $$0) {
        ArrayList<ServerPlayer> $$1 = Lists.newArrayList();
        for (ServerPlayer $$2 : this.players) {
            if (!$$2.getIpAddress().equals($$0)) continue;
            $$1.add($$2);
        }
        return $$1;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public int getSimulationDistance() {
        return this.simulationDistance;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    @Nullable
    public CompoundTag getSingleplayerData() {
        return null;
    }

    public void setAllowCommandsForAllPlayers(boolean $$0) {
        this.allowCommandsForAllPlayers = $$0;
    }

    public void removeAll() {
        for (int $$0 = 0; $$0 < this.players.size(); ++$$0) {
            this.players.get((int)$$0).connection.disconnect(Component.translatable("multiplayer.disconnect.server_shutdown"));
        }
    }

    public void broadcastSystemMessage(Component $$0, boolean $$12) {
        this.broadcastSystemMessage($$0, $$1 -> $$0, $$12);
    }

    public void broadcastSystemMessage(Component $$0, Function<ServerPlayer, Component> $$1, boolean $$2) {
        this.server.sendSystemMessage($$0);
        for (ServerPlayer $$3 : this.players) {
            Component $$4 = $$1.apply($$3);
            if ($$4 == null) continue;
            $$3.sendSystemMessage($$4, $$2);
        }
    }

    public void broadcastChatMessage(PlayerChatMessage $$0, CommandSourceStack $$1, ChatType.Bound $$2) {
        this.broadcastChatMessage($$0, $$1::shouldFilterMessageTo, $$1.getPlayer(), $$2);
    }

    public void broadcastChatMessage(PlayerChatMessage $$0, ServerPlayer $$1, ChatType.Bound $$2) {
        this.broadcastChatMessage($$0, $$1::shouldFilterMessageTo, $$1, $$2);
    }

    private void broadcastChatMessage(PlayerChatMessage $$0, Predicate<ServerPlayer> $$1, @Nullable ServerPlayer $$2, ChatType.Bound $$3) {
        boolean $$4 = this.verifyChatTrusted($$0);
        this.server.logChatMessage($$0.decoratedContent(), $$3, $$4 ? null : "Not Secure");
        OutgoingChatMessage $$5 = OutgoingChatMessage.create($$0);
        boolean $$6 = false;
        for (ServerPlayer $$7 : this.players) {
            boolean $$8 = $$1.test($$7);
            $$7.sendChatMessage($$5, $$8, $$3);
            $$6 |= $$8 && $$0.isFullyFiltered();
        }
        if ($$6 && $$2 != null) {
            $$2.sendSystemMessage(CHAT_FILTERED_FULL);
        }
    }

    private boolean verifyChatTrusted(PlayerChatMessage $$0) {
        return $$0.hasSignature() && !$$0.hasExpiredServer(Instant.now());
    }

    public ServerStatsCounter getPlayerStats(Player $$0) {
        UUID $$1 = $$0.getUUID();
        ServerStatsCounter $$2 = this.stats.get($$1);
        if ($$2 == null) {
            File $$5;
            Path $$6;
            File $$3 = this.server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile();
            File $$4 = new File($$3, String.valueOf($$1) + ".json");
            if (!$$4.exists() && FileUtil.isPathNormalized($$6 = ($$5 = new File($$3, $$0.getName().getString() + ".json")).toPath()) && FileUtil.isPathPortable($$6) && $$6.startsWith($$3.getPath()) && $$5.isFile()) {
                $$5.renameTo($$4);
            }
            $$2 = new ServerStatsCounter(this.server, $$4);
            this.stats.put($$1, $$2);
        }
        return $$2;
    }

    public PlayerAdvancements getPlayerAdvancements(ServerPlayer $$0) {
        UUID $$1 = $$0.getUUID();
        PlayerAdvancements $$2 = this.advancements.get($$1);
        if ($$2 == null) {
            Path $$3 = this.server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).resolve(String.valueOf($$1) + ".json");
            $$2 = new PlayerAdvancements(this.server.getFixerUpper(), this, this.server.getAdvancements(), $$3, $$0);
            this.advancements.put($$1, $$2);
        }
        $$2.setPlayer($$0);
        return $$2;
    }

    public void setViewDistance(int $$0) {
        this.viewDistance = $$0;
        this.broadcastAll(new ClientboundSetChunkCacheRadiusPacket($$0));
        for (ServerLevel $$1 : this.server.getAllLevels()) {
            if ($$1 == null) continue;
            $$1.getChunkSource().setViewDistance($$0);
        }
    }

    public void setSimulationDistance(int $$0) {
        this.simulationDistance = $$0;
        this.broadcastAll(new ClientboundSetSimulationDistancePacket($$0));
        for (ServerLevel $$1 : this.server.getAllLevels()) {
            if ($$1 == null) continue;
            $$1.getChunkSource().setSimulationDistance($$0);
        }
    }

    public List<ServerPlayer> getPlayers() {
        return this.players;
    }

    @Nullable
    public ServerPlayer getPlayer(UUID $$0) {
        return this.playersByUUID.get($$0);
    }

    public boolean canBypassPlayerLimit(GameProfile $$0) {
        return false;
    }

    public void reloadResources() {
        for (PlayerAdvancements $$0 : this.advancements.values()) {
            $$0.reload(this.server.getAdvancements());
        }
        this.broadcastAll(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(this.registries)));
        RecipeManager $$1 = this.server.getRecipeManager();
        ClientboundUpdateRecipesPacket $$2 = new ClientboundUpdateRecipesPacket($$1.getSynchronizedItemProperties(), $$1.getSynchronizedStonecutterRecipes());
        for (ServerPlayer $$3 : this.players) {
            $$3.connection.send($$2);
            $$3.getRecipeBook().sendInitialRecipeBook($$3);
        }
    }

    public boolean isAllowCommandsForAllPlayers() {
        return this.allowCommandsForAllPlayers;
    }
}

