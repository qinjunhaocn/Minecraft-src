/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.DemoIntroScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.inventory.TestInstanceBlockEditScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.CacheSlot;
import net.minecraft.client.multiplayer.ChunkBatchSizeCalculator;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientRecipeContainer;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.multiplayer.DebugSampleSubscriber;
import net.minecraft.client.multiplayer.LevelLoadStatusManager;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.PingDebugMonitor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.debug.BrainDebugRenderer;
import net.minecraft.client.renderer.debug.VillageSectionsDebugRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptRenderer;
import net.minecraft.client.resources.sounds.BeeAggressiveSoundInstance;
import net.minecraft.client.resources.sounds.BeeFlyingSoundInstance;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.resources.sounds.SnifferSoundInstance;
import net.minecraft.client.waypoints.ClientWaypointManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessagesTracker;
import net.minecraft.network.chat.LocalChatSession;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.chat.SignedMessageLink;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.network.protocol.common.custom.BeeDebugPayload;
import net.minecraft.network.protocol.common.custom.BrainDebugPayload;
import net.minecraft.network.protocol.common.custom.BreezeDebugPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.GameEventDebugPayload;
import net.minecraft.network.protocol.common.custom.GameEventListenerDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestAddMarkerDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestClearMarkersDebugPayload;
import net.minecraft.network.protocol.common.custom.GoalDebugPayload;
import net.minecraft.network.protocol.common.custom.HiveDebugPayload;
import net.minecraft.network.protocol.common.custom.NeighborUpdatesDebugPayload;
import net.minecraft.network.protocol.common.custom.PathfindingDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiAddedDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiRemovedDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiTicketCountDebugPayload;
import net.minecraft.network.protocol.common.custom.RaidsDebugPayload;
import net.minecraft.network.protocol.common.custom.RedstoneWireOrientationsDebugPayload;
import net.minecraft.network.protocol.common.custom.StructuresDebugPayload;
import net.minecraft.network.protocol.common.custom.VillageSectionsDebugPayload;
import net.minecraft.network.protocol.common.custom.WorldGenAttemptDebugPayload;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBatchFinishedPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBatchStartPacket;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.network.protocol.game.ClientboundDeleteChatPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import net.minecraft.network.protocol.game.ClientboundProjectilePowerPacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookAddPacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookRemovePacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookSettingsPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTestInstanceBlockStatus;
import net.minecraft.network.protocol.game.ClientboundTickingStatePacket;
import net.minecraft.network.protocol.game.ClientboundTickingStepPacket;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundChunkBatchReceivedPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundConfigurationAcknowledgedPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.Crypt;
import net.minecraft.util.HashOps;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.slf4j.Logger;

public class ClientPacketListener
extends ClientCommonPacketListenerImpl
implements ClientGamePacketListener,
TickablePacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component UNSECURE_SERVER_TOAST_TITLE = Component.translatable("multiplayer.unsecureserver.toast.title");
    private static final Component UNSERURE_SERVER_TOAST = Component.translatable("multiplayer.unsecureserver.toast");
    private static final Component INVALID_PACKET = Component.translatable("multiplayer.disconnect.invalid_packet");
    private static final Component RECONFIGURE_SCREEN_MESSAGE = Component.translatable("connect.reconfiguring");
    private static final Component BAD_CHAT_INDEX = Component.translatable("multiplayer.disconnect.bad_chat_index");
    private static final Component COMMAND_SEND_CONFIRM_TITLE = Component.translatable("multiplayer.confirm_command.title");
    private static final int PENDING_OFFSET_THRESHOLD = 64;
    public static final int TELEPORT_INTERPOLATION_THRESHOLD = 64;
    private static final ClientboundCommandsPacket.NodeBuilder<ClientSuggestionProvider> COMMAND_NODE_BUILDER = new ClientboundCommandsPacket.NodeBuilder<ClientSuggestionProvider>(){

        @Override
        public ArgumentBuilder<ClientSuggestionProvider, ?> createLiteral(String $$0) {
            return LiteralArgumentBuilder.literal((String)$$0);
        }

        @Override
        public ArgumentBuilder<ClientSuggestionProvider, ?> createArgument(String $$0, ArgumentType<?> $$1, @Nullable ResourceLocation $$2) {
            RequiredArgumentBuilder $$3 = RequiredArgumentBuilder.argument((String)$$0, $$1);
            if ($$2 != null) {
                $$3.suggests(SuggestionProviders.getProvider($$2));
            }
            return $$3;
        }

        @Override
        public ArgumentBuilder<ClientSuggestionProvider, ?> configure(ArgumentBuilder<ClientSuggestionProvider, ?> $$02, boolean $$1, boolean $$2) {
            if ($$1) {
                $$02.executes($$0 -> 0);
            }
            if ($$2) {
                $$02.requires(ClientSuggestionProvider::allowsRestrictedCommands);
            }
            return $$02;
        }
    };
    private final GameProfile localGameProfile;
    private ClientLevel level;
    private ClientLevel.ClientLevelData levelData;
    private final Map<UUID, PlayerInfo> playerInfoMap = Maps.newHashMap();
    private final Set<PlayerInfo> listedPlayers = new ReferenceOpenHashSet();
    private final ClientAdvancements advancements;
    private final ClientSuggestionProvider suggestionsProvider;
    private final ClientSuggestionProvider restrictedSuggestionsProvider;
    private final DebugQueryHandler debugQueryHandler = new DebugQueryHandler(this);
    private int serverChunkRadius = 3;
    private int serverSimulationDistance = 3;
    private final RandomSource random = RandomSource.createThreadSafe();
    private CommandDispatcher<ClientSuggestionProvider> commands = new CommandDispatcher();
    private ClientRecipeContainer recipes = new ClientRecipeContainer(Map.of(), SelectableRecipe.SingleInputSet.empty());
    private final UUID id = UUID.randomUUID();
    private Set<ResourceKey<Level>> levels;
    private final RegistryAccess.Frozen registryAccess;
    private final FeatureFlagSet enabledFeatures;
    private final PotionBrewing potionBrewing;
    private FuelValues fuelValues;
    private final HashedPatchMap.HashGenerator decoratedHashOpsGenerator;
    private OptionalInt removedPlayerVehicleId = OptionalInt.empty();
    @Nullable
    private LocalChatSession chatSession;
    private SignedMessageChain.Encoder signedMessageEncoder = SignedMessageChain.Encoder.UNSIGNED;
    private int nextChatIndex;
    private LastSeenMessagesTracker lastSeenMessages = new LastSeenMessagesTracker(20);
    private MessageSignatureCache messageSignatureCache = MessageSignatureCache.createDefault();
    @Nullable
    private CompletableFuture<Optional<ProfileKeyPair>> keyPairFuture;
    @Nullable
    private ClientInformation remoteClientInformation;
    private final ChunkBatchSizeCalculator chunkBatchSizeCalculator = new ChunkBatchSizeCalculator();
    private final PingDebugMonitor pingDebugMonitor;
    private final DebugSampleSubscriber debugSampleSubscriber;
    @Nullable
    private LevelLoadStatusManager levelLoadStatusManager;
    private boolean serverEnforcesSecureChat;
    private boolean seenInsecureChatWarning = false;
    private volatile boolean closed;
    private final Scoreboard scoreboard = new Scoreboard();
    private final ClientWaypointManager waypointManager = new ClientWaypointManager();
    private final SessionSearchTrees searchTrees = new SessionSearchTrees();
    private final List<WeakReference<CacheSlot<?, ?>>> cacheSlots = new ArrayList();

    public ClientPacketListener(Minecraft $$0, Connection $$1, CommonListenerCookie $$2) {
        super($$0, $$1, $$2);
        this.localGameProfile = $$2.localGameProfile();
        this.registryAccess = $$2.receivedRegistries();
        RegistryOps<HashCode> $$3 = this.registryAccess.createSerializationContext(HashOps.CRC32C_INSTANCE);
        this.decoratedHashOpsGenerator = $$12 -> ((HashCode)$$12.encodeValue($$3).getOrThrow($$1 -> new IllegalArgumentException("Failed to hash " + String.valueOf($$12) + ": " + $$1))).asInt();
        this.enabledFeatures = $$2.enabledFeatures();
        this.advancements = new ClientAdvancements($$0, this.telemetryManager);
        this.suggestionsProvider = new ClientSuggestionProvider(this, $$0, true);
        this.restrictedSuggestionsProvider = new ClientSuggestionProvider(this, $$0, false);
        this.pingDebugMonitor = new PingDebugMonitor(this, $$0.getDebugOverlay().getPingLogger());
        this.debugSampleSubscriber = new DebugSampleSubscriber(this, $$0.getDebugOverlay());
        if ($$2.chatState() != null) {
            $$0.gui.getChat().restoreState($$2.chatState());
        }
        this.potionBrewing = PotionBrewing.bootstrap(this.enabledFeatures);
        this.fuelValues = FuelValues.vanillaBurnTimes($$2.receivedRegistries(), this.enabledFeatures);
    }

    public ClientSuggestionProvider getSuggestionsProvider() {
        return this.suggestionsProvider;
    }

    public void close() {
        this.closed = true;
        this.clearLevel();
        this.telemetryManager.onDisconnect();
    }

    public void clearLevel() {
        this.clearCacheSlots();
        this.level = null;
        this.levelLoadStatusManager = null;
    }

    private void clearCacheSlots() {
        for (WeakReference<CacheSlot<?, ?>> $$0 : this.cacheSlots) {
            CacheSlot $$1 = (CacheSlot)$$0.get();
            if ($$1 == null) continue;
            $$1.clear();
        }
        this.cacheSlots.clear();
    }

    public RecipeAccess recipes() {
        return this.recipes;
    }

    @Override
    public void handleLogin(ClientboundLoginPacket $$0) {
        ClientLevel.ClientLevelData $$8;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, this);
        CommonPlayerSpawnInfo $$1 = $$0.commonPlayerSpawnInfo();
        ArrayList<ResourceKey<Level>> $$2 = Lists.newArrayList($$0.levels());
        Collections.shuffle($$2);
        this.levels = Sets.newLinkedHashSet($$2);
        ResourceKey<Level> $$3 = $$1.dimension();
        Holder<DimensionType> $$4 = $$1.dimensionType();
        this.serverChunkRadius = $$0.chunkRadius();
        this.serverSimulationDistance = $$0.simulationDistance();
        boolean $$5 = $$1.isDebug();
        boolean $$6 = $$1.isFlat();
        int $$7 = $$1.seaLevel();
        this.levelData = $$8 = new ClientLevel.ClientLevelData(Difficulty.NORMAL, $$0.hardcore(), $$6);
        this.level = new ClientLevel(this, $$8, $$3, $$4, this.serverChunkRadius, this.serverSimulationDistance, this.minecraft.levelRenderer, $$5, $$1.seed(), $$7);
        this.minecraft.setLevel(this.level, ReceivingLevelScreen.Reason.OTHER);
        if (this.minecraft.player == null) {
            this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook());
            this.minecraft.player.setYRot(-180.0f);
            if (this.minecraft.getSingleplayerServer() != null) {
                this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
            }
        }
        this.minecraft.debugRenderer.clear();
        this.minecraft.player.resetPos();
        this.minecraft.player.setId($$0.playerId());
        this.level.addEntity(this.minecraft.player);
        this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
        this.minecraft.cameraEntity = this.minecraft.player;
        this.startWaitingForNewLevel(this.minecraft.player, this.level, ReceivingLevelScreen.Reason.OTHER);
        this.minecraft.player.setReducedDebugInfo($$0.reducedDebugInfo());
        this.minecraft.player.setShowDeathScreen($$0.showDeathScreen());
        this.minecraft.player.setDoLimitedCrafting($$0.doLimitedCrafting());
        this.minecraft.player.setLastDeathLocation($$1.lastDeathLocation());
        this.minecraft.player.setPortalCooldown($$1.portalCooldown());
        this.minecraft.gameMode.setLocalMode($$1.gameType(), $$1.previousGameType());
        this.minecraft.options.setServerRenderDistance($$0.chunkRadius());
        this.chatSession = null;
        this.signedMessageEncoder = SignedMessageChain.Encoder.UNSIGNED;
        this.nextChatIndex = 0;
        this.lastSeenMessages = new LastSeenMessagesTracker(20);
        this.messageSignatureCache = MessageSignatureCache.createDefault();
        if (this.connection.isEncrypted()) {
            this.prepareKeyPair();
        }
        this.telemetryManager.onPlayerInfoReceived($$1.gameType(), $$0.hardcore());
        this.minecraft.quickPlayLog().log(this.minecraft);
        this.serverEnforcesSecureChat = $$0.enforcesSecureChat();
        if (this.serverData != null && !this.seenInsecureChatWarning && !this.enforcesSecureChat()) {
            SystemToast $$9 = SystemToast.multiline(this.minecraft, SystemToast.SystemToastId.UNSECURE_SERVER_WARNING, UNSECURE_SERVER_TOAST_TITLE, UNSERURE_SERVER_TOAST);
            this.minecraft.getToastManager().addToast($$9);
            this.seenInsecureChatWarning = true;
        }
    }

    @Override
    public void handleAddEntity(ClientboundAddEntityPacket $$0) {
        Entity $$1;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == $$0.getId()) {
            this.removedPlayerVehicleId = OptionalInt.empty();
        }
        if (($$1 = this.createEntityFromPacket($$0)) != null) {
            $$1.recreateFromPacket($$0);
            this.level.addEntity($$1);
            this.postAddEntitySoundInstance($$1);
        } else {
            LOGGER.warn("Skipping Entity with id {}", (Object)$$0.getType());
        }
    }

    @Nullable
    private Entity createEntityFromPacket(ClientboundAddEntityPacket $$0) {
        EntityType<?> $$1 = $$0.getType();
        if ($$1 == EntityType.PLAYER) {
            PlayerInfo $$2 = this.getPlayerInfo($$0.getUUID());
            if ($$2 == null) {
                LOGGER.warn("Server attempted to add player prior to sending player info (Player id {})", (Object)$$0.getUUID());
                return null;
            }
            return new RemotePlayer(this.level, $$2.getProfile());
        }
        return $$1.create(this.level, EntitySpawnReason.LOAD);
    }

    private void postAddEntitySoundInstance(Entity $$0) {
        if ($$0 instanceof AbstractMinecart) {
            AbstractMinecart $$1 = (AbstractMinecart)$$0;
            this.minecraft.getSoundManager().play(new MinecartSoundInstance($$1));
        } else if ($$0 instanceof Bee) {
            BeeFlyingSoundInstance $$5;
            Bee $$2 = (Bee)$$0;
            boolean $$3 = $$2.isAngry();
            if ($$3) {
                BeeAggressiveSoundInstance $$4 = new BeeAggressiveSoundInstance($$2);
            } else {
                $$5 = new BeeFlyingSoundInstance($$2);
            }
            this.minecraft.getSoundManager().queueTickingSound($$5);
        }
    }

    @Override
    public void handleSetEntityMotion(ClientboundSetEntityMotionPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getId());
        if ($$1 == null) {
            return;
        }
        $$1.lerpMotion($$0.getXa(), $$0.getYa(), $$0.getZa());
    }

    @Override
    public void handleSetEntityData(ClientboundSetEntityDataPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.id());
        if ($$1 != null) {
            $$1.getEntityData().assignValues($$0.packedItems());
        }
    }

    @Override
    public void handleEntityPositionSync(ClientboundEntityPositionSyncPacket $$0) {
        boolean $$5;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.id());
        if ($$1 == null) {
            return;
        }
        Vec3 $$2 = $$0.values().position();
        $$1.getPositionCodec().setBase($$2);
        if ($$1.isLocalInstanceAuthoritative()) {
            return;
        }
        float $$3 = $$0.values().yRot();
        float $$4 = $$0.values().xRot();
        boolean bl = $$5 = $$1.position().distanceToSqr($$2) > 4096.0;
        if (this.level.isTickingEntity($$1) && !$$5) {
            $$1.moveOrInterpolateTo($$2, $$3, $$4);
        } else {
            $$1.snapTo($$2, $$3, $$4);
        }
        if (!$$1.isInterpolating() && $$1.hasIndirectPassenger(this.minecraft.player)) {
            $$1.positionRider(this.minecraft.player);
            this.minecraft.player.setOldPosAndRot();
        }
        $$1.setOnGround($$0.onGround());
    }

    @Override
    public void handleTeleportEntity(ClientboundTeleportEntityPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.id());
        if ($$1 == null) {
            if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == $$0.id()) {
                LOGGER.debug("Trying to teleport entity with id {}, that was formerly player vehicle, applying teleport to player instead", (Object)$$0.id());
                ClientPacketListener.setValuesFromPositionPacket($$0.change(), $$0.relatives(), this.minecraft.player, false);
                this.connection.send(new ServerboundMovePlayerPacket.PosRot(this.minecraft.player.getX(), this.minecraft.player.getY(), this.minecraft.player.getZ(), this.minecraft.player.getYRot(), this.minecraft.player.getXRot(), false, false));
            }
            return;
        }
        boolean $$2 = $$0.relatives().contains((Object)Relative.X) || $$0.relatives().contains((Object)Relative.Y) || $$0.relatives().contains((Object)Relative.Z);
        boolean $$3 = this.level.isTickingEntity($$1) || !$$1.isLocalInstanceAuthoritative() || $$2;
        boolean $$4 = ClientPacketListener.setValuesFromPositionPacket($$0.change(), $$0.relatives(), $$1, $$3);
        $$1.setOnGround($$0.onGround());
        if (!$$4 && $$1.hasIndirectPassenger(this.minecraft.player)) {
            $$1.positionRider(this.minecraft.player);
            this.minecraft.player.setOldPosAndRot();
            if ($$1.isLocalInstanceAuthoritative()) {
                this.connection.send(ServerboundMoveVehiclePacket.fromEntity($$1));
            }
        }
    }

    @Override
    public void handleTickingState(ClientboundTickingStatePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if (this.minecraft.level == null) {
            return;
        }
        TickRateManager $$1 = this.minecraft.level.tickRateManager();
        $$1.setTickRate($$0.tickRate());
        $$1.setFrozen($$0.isFrozen());
    }

    @Override
    public void handleTickingStep(ClientboundTickingStepPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if (this.minecraft.level == null) {
            return;
        }
        TickRateManager $$1 = this.minecraft.level.tickRateManager();
        $$1.setFrozenTicksToRun($$0.tickSteps());
    }

    @Override
    public void handleSetHeldSlot(ClientboundSetHeldSlotPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if (Inventory.isHotbarSlot($$0.slot())) {
            this.minecraft.player.getInventory().setSelectedSlot($$0.slot());
        }
    }

    @Override
    public void handleMoveEntity(ClientboundMoveEntityPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = $$0.getEntity(this.level);
        if ($$1 == null) {
            return;
        }
        if ($$1.isLocalInstanceAuthoritative()) {
            VecDeltaCodec $$2 = $$1.getPositionCodec();
            Vec3 $$3 = $$2.decode($$0.getXa(), $$0.getYa(), $$0.getZa());
            $$2.setBase($$3);
            return;
        }
        if ($$0.hasPosition()) {
            VecDeltaCodec $$4 = $$1.getPositionCodec();
            Vec3 $$5 = $$4.decode($$0.getXa(), $$0.getYa(), $$0.getZa());
            $$4.setBase($$5);
            if ($$0.hasRotation()) {
                $$1.moveOrInterpolateTo($$5, $$0.getYRot(), $$0.getXRot());
            } else {
                $$1.moveOrInterpolateTo($$5, $$1.getYRot(), $$1.getXRot());
            }
        } else if ($$0.hasRotation()) {
            $$1.moveOrInterpolateTo($$1.position(), $$0.getYRot(), $$0.getXRot());
        }
        $$1.setOnGround($$0.isOnGround());
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void handleMinecartAlongTrack(ClientboundMoveMinecartPacket $$0) {
        void $$3;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = $$0.getEntity(this.level);
        if (!($$1 instanceof AbstractMinecart)) {
            return;
        }
        AbstractMinecart $$2 = (AbstractMinecart)$$1;
        MinecartBehavior minecartBehavior = $$3.getBehavior();
        if (minecartBehavior instanceof NewMinecartBehavior) {
            NewMinecartBehavior $$4 = (NewMinecartBehavior)minecartBehavior;
            $$4.lerpSteps.addAll($$0.lerpSteps());
        }
    }

    @Override
    public void handleRotateMob(ClientboundRotateHeadPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = $$0.getEntity(this.level);
        if ($$1 == null) {
            return;
        }
        $$1.lerpHeadTo($$0.getYHeadRot(), 3);
    }

    @Override
    public void handleRemoveEntities(ClientboundRemoveEntitiesPacket $$02) {
        PacketUtils.ensureRunningOnSameThread($$02, this, this.minecraft);
        $$02.getEntityIds().forEach($$0 -> {
            Entity $$1 = this.level.getEntity($$0);
            if ($$1 == null) {
                return;
            }
            if ($$1.hasIndirectPassenger(this.minecraft.player)) {
                LOGGER.debug("Remove entity {}:{} that has player as passenger", (Object)$$1.getType(), (Object)$$0);
                this.removedPlayerVehicleId = OptionalInt.of($$0);
            }
            this.level.removeEntity($$0, Entity.RemovalReason.DISCARDED);
        });
    }

    @Override
    public void handleMovePlayer(ClientboundPlayerPositionPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        if (!$$1.isPassenger()) {
            ClientPacketListener.setValuesFromPositionPacket($$0.change(), $$0.relatives(), $$1, false);
        }
        this.connection.send(new ServerboundAcceptTeleportationPacket($$0.id()));
        this.connection.send(new ServerboundMovePlayerPacket.PosRot($$1.getX(), $$1.getY(), $$1.getZ(), $$1.getYRot(), $$1.getXRot(), false, false));
    }

    private static boolean setValuesFromPositionPacket(PositionMoveRotation $$0, Set<Relative> $$1, Entity $$2, boolean $$3) {
        boolean $$6;
        PositionMoveRotation $$4 = PositionMoveRotation.of($$2);
        PositionMoveRotation $$5 = PositionMoveRotation.calculateAbsolute($$4, $$0, $$1);
        boolean bl = $$6 = $$4.position().distanceToSqr($$5.position()) > 4096.0;
        if ($$3 && !$$6) {
            $$2.moveOrInterpolateTo($$5.position(), $$5.yRot(), $$5.xRot());
            $$2.setDeltaMovement($$5.deltaMovement());
            return true;
        }
        $$2.setPos($$5.position());
        $$2.setDeltaMovement($$5.deltaMovement());
        $$2.setYRot($$5.yRot());
        $$2.setXRot($$5.xRot());
        PositionMoveRotation $$7 = new PositionMoveRotation($$2.oldPosition(), Vec3.ZERO, $$2.yRotO, $$2.xRotO);
        PositionMoveRotation $$8 = PositionMoveRotation.calculateAbsolute($$7, $$0, $$1);
        $$2.setOldPosAndRot($$8.position(), $$8.yRot(), $$8.xRot());
        return false;
    }

    @Override
    public void handleRotatePlayer(ClientboundPlayerRotationPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        $$1.setYRot($$0.yRot());
        $$1.setXRot($$0.xRot());
        $$1.setOldRot();
        this.connection.send(new ServerboundMovePlayerPacket.Rot($$1.getYRot(), $$1.getXRot(), false, false));
    }

    @Override
    public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket $$02) {
        PacketUtils.ensureRunningOnSameThread($$02, this, this.minecraft);
        $$02.runUpdates(($$0, $$1) -> this.level.setServerVerifiedBlockState((BlockPos)$$0, (BlockState)$$1, 19));
    }

    @Override
    public void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        int $$1 = $$0.getX();
        int $$2 = $$0.getZ();
        this.updateLevelChunk($$1, $$2, $$0.getChunkData());
        ClientboundLightUpdatePacketData $$3 = $$0.getLightData();
        this.level.queueLightUpdate(() -> {
            this.applyLightData($$1, $$2, $$3, false);
            LevelChunk $$3 = this.level.getChunkSource().getChunk($$1, $$2, false);
            if ($$3 != null) {
                this.enableChunkLight($$3, $$1, $$2);
                this.minecraft.levelRenderer.onChunkReadyToRender($$3.getPos());
            }
        });
    }

    @Override
    public void handleChunksBiomes(ClientboundChunksBiomesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        for (ClientboundChunksBiomesPacket.ChunkBiomeData $$1 : $$0.chunkBiomeData()) {
            this.level.getChunkSource().replaceBiomes($$1.pos().x, $$1.pos().z, $$1.getReadBuffer());
        }
        for (ClientboundChunksBiomesPacket.ChunkBiomeData $$2 : $$0.chunkBiomeData()) {
            this.level.onChunkLoaded(new ChunkPos($$2.pos().x, $$2.pos().z));
        }
        for (ClientboundChunksBiomesPacket.ChunkBiomeData $$3 : $$0.chunkBiomeData()) {
            for (int $$4 = -1; $$4 <= 1; ++$$4) {
                for (int $$5 = -1; $$5 <= 1; ++$$5) {
                    for (int $$6 = this.level.getMinSectionY(); $$6 <= this.level.getMaxSectionY(); ++$$6) {
                        this.minecraft.levelRenderer.setSectionDirty($$3.pos().x + $$4, $$6, $$3.pos().z + $$5);
                    }
                }
            }
        }
    }

    private void updateLevelChunk(int $$0, int $$1, ClientboundLevelChunkPacketData $$2) {
        this.level.getChunkSource().replaceWithPacketData($$0, $$1, $$2.getReadBuffer(), $$2.getHeightmaps(), $$2.getBlockEntitiesTagsConsumer($$0, $$1));
    }

    private void enableChunkLight(LevelChunk $$0, int $$1, int $$2) {
        LevelLightEngine $$3 = this.level.getChunkSource().getLightEngine();
        LevelChunkSection[] $$4 = $$0.d();
        ChunkPos $$5 = $$0.getPos();
        for (int $$6 = 0; $$6 < $$4.length; ++$$6) {
            LevelChunkSection $$7 = $$4[$$6];
            int $$8 = this.level.getSectionYFromSectionIndex($$6);
            $$3.updateSectionStatus(SectionPos.of($$5, $$8), $$7.hasOnlyAir());
        }
        this.level.setSectionRangeDirty($$1 - 1, this.level.getMinSectionY(), $$2 - 1, $$1 + 1, this.level.getMaxSectionY(), $$2 + 1);
    }

    @Override
    public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getChunkSource().drop($$0.pos());
        this.queueLightRemoval($$0);
    }

    private void queueLightRemoval(ClientboundForgetLevelChunkPacket $$0) {
        ChunkPos $$1 = $$0.pos();
        this.level.queueLightUpdate(() -> {
            LevelLightEngine $$1 = this.level.getLightEngine();
            $$1.setLightEnabled($$1, false);
            for (int $$2 = $$1.getMinLightSection(); $$2 < $$1.getMaxLightSection(); ++$$2) {
                SectionPos $$3 = SectionPos.of($$1, $$2);
                $$1.queueSectionData(LightLayer.BLOCK, $$3, null);
                $$1.queueSectionData(LightLayer.SKY, $$3, null);
            }
            for (int $$4 = this.level.getMinSectionY(); $$4 <= this.level.getMaxSectionY(); ++$$4) {
                $$1.updateSectionStatus(SectionPos.of($$1, $$4), true);
            }
        });
    }

    @Override
    public void handleBlockUpdate(ClientboundBlockUpdatePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.setServerVerifiedBlockState($$0.getPos(), $$0.getBlockState(), 19);
    }

    @Override
    public void handleConfigurationStart(ClientboundStartConfigurationPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.getChatListener().clearQueue();
        this.sendChatAcknowledgement();
        ChatComponent.State $$1 = this.minecraft.gui.getChat().storeState();
        this.minecraft.clearClientLevel(new ServerReconfigScreen(RECONFIGURE_SCREEN_MESSAGE, this.connection));
        this.connection.setupInboundProtocol(ConfigurationProtocols.CLIENTBOUND, new ClientConfigurationPacketListenerImpl(this.minecraft, this.connection, new CommonListenerCookie(this.localGameProfile, this.telemetryManager, this.registryAccess, this.enabledFeatures, this.serverBrand, this.serverData, this.postDisconnectScreen, this.serverCookies, $$1, this.customReportDetails, this.serverLinks())));
        this.send(ServerboundConfigurationAcknowledgedPacket.INSTANCE);
        this.connection.setupOutboundProtocol(ConfigurationProtocols.SERVERBOUND);
    }

    @Override
    public void handleTakeItemEntity(ClientboundTakeItemEntityPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getItemId());
        LivingEntity $$2 = (LivingEntity)this.level.getEntity($$0.getPlayerId());
        if ($$2 == null) {
            $$2 = this.minecraft.player;
        }
        if ($$1 != null) {
            if ($$1 instanceof ExperienceOrb) {
                this.level.playLocalSound($$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1f, (this.random.nextFloat() - this.random.nextFloat()) * 0.35f + 0.9f, false);
            } else {
                this.level.playLocalSound($$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, (this.random.nextFloat() - this.random.nextFloat()) * 1.4f + 2.0f, false);
            }
            this.minecraft.particleEngine.add(new ItemPickupParticle(this.minecraft.getEntityRenderDispatcher(), this.level, $$1, $$2));
            if ($$1 instanceof ItemEntity) {
                ItemEntity $$3 = (ItemEntity)$$1;
                ItemStack $$4 = $$3.getItem();
                if (!$$4.isEmpty()) {
                    $$4.shrink($$0.getAmount());
                }
                if ($$4.isEmpty()) {
                    this.level.removeEntity($$0.getItemId(), Entity.RemovalReason.DISCARDED);
                }
            } else if (!($$1 instanceof ExperienceOrb)) {
                this.level.removeEntity($$0.getItemId(), Entity.RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public void handleSystemChat(ClientboundSystemChatPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.getChatListener().handleSystemMessage($$0.content(), $$0.overlay());
    }

    @Override
    public void handlePlayerChat(ClientboundPlayerChatPacket $$0) {
        SignedMessageLink $$7;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        int $$1 = this.nextChatIndex++;
        if ($$0.globalIndex() != $$1) {
            LOGGER.error("Missing or out-of-order chat message from server, expected index {} but got {}", (Object)$$1, (Object)$$0.globalIndex());
            this.connection.disconnect(BAD_CHAT_INDEX);
            return;
        }
        Optional<SignedMessageBody> $$2 = $$0.body().unpack(this.messageSignatureCache);
        if ($$2.isEmpty()) {
            LOGGER.error("Message from player with ID {} referenced unrecognized signature id", (Object)$$0.sender());
            this.connection.disconnect(INVALID_PACKET);
            return;
        }
        this.messageSignatureCache.push($$2.get(), $$0.signature());
        UUID $$3 = $$0.sender();
        PlayerInfo $$4 = this.getPlayerInfo($$3);
        if ($$4 == null) {
            LOGGER.error("Received player chat packet for unknown player with ID: {}", (Object)$$3);
            this.minecraft.getChatListener().handleChatMessageError($$3, $$0.signature(), $$0.chatType());
            return;
        }
        RemoteChatSession $$5 = $$4.getChatSession();
        if ($$5 != null) {
            SignedMessageLink $$6 = new SignedMessageLink($$0.index(), $$3, $$5.sessionId());
        } else {
            $$7 = SignedMessageLink.unsigned($$3);
        }
        PlayerChatMessage $$8 = new PlayerChatMessage($$7, $$0.signature(), $$2.get(), $$0.unsignedContent(), $$0.filterMask());
        $$8 = $$4.getMessageValidator().updateAndValidate($$8);
        if ($$8 != null) {
            this.minecraft.getChatListener().handlePlayerChatMessage($$8, $$4.getProfile(), $$0.chatType());
        } else {
            this.minecraft.getChatListener().handleChatMessageError($$3, $$0.signature(), $$0.chatType());
        }
    }

    @Override
    public void handleDisguisedChat(ClientboundDisguisedChatPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.getChatListener().handleDisguisedChatMessage($$0.message(), $$0.chatType());
    }

    @Override
    public void handleDeleteChat(ClientboundDeleteChatPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Optional<MessageSignature> $$1 = $$0.messageSignature().unpack(this.messageSignatureCache);
        if ($$1.isEmpty()) {
            this.connection.disconnect(INVALID_PACKET);
            return;
        }
        this.lastSeenMessages.ignorePending($$1.get());
        if (!this.minecraft.getChatListener().removeFromDelayedMessageQueue($$1.get())) {
            this.minecraft.gui.getChat().deleteMessage($$1.get());
        }
    }

    @Override
    public void handleAnimate(ClientboundAnimatePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getId());
        if ($$1 == null) {
            return;
        }
        if ($$0.getAction() == 0) {
            LivingEntity $$2 = (LivingEntity)$$1;
            $$2.swing(InteractionHand.MAIN_HAND);
        } else if ($$0.getAction() == 3) {
            LivingEntity $$3 = (LivingEntity)$$1;
            $$3.swing(InteractionHand.OFF_HAND);
        } else if ($$0.getAction() == 2) {
            Player $$4 = (Player)$$1;
            $$4.stopSleepInBed(false, false);
        } else if ($$0.getAction() == 4) {
            this.minecraft.particleEngine.createTrackingEmitter($$1, ParticleTypes.CRIT);
        } else if ($$0.getAction() == 5) {
            this.minecraft.particleEngine.createTrackingEmitter($$1, ParticleTypes.ENCHANTED_HIT);
        }
    }

    @Override
    public void handleHurtAnimation(ClientboundHurtAnimationPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.id());
        if ($$1 == null) {
            return;
        }
        $$1.animateHurt($$0.yaw());
    }

    @Override
    public void handleSetTime(ClientboundSetTimePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.setTimeFromServer($$0.gameTime(), $$0.dayTime(), $$0.tickDayTime());
        this.telemetryManager.setTime($$0.gameTime());
    }

    @Override
    public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.level.setDefaultSpawnPos($$0.getPos(), $$0.getAngle());
    }

    @Override
    public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getVehicle());
        if ($$1 == null) {
            LOGGER.warn("Received passengers for unknown entity");
            return;
        }
        boolean $$2 = $$1.hasIndirectPassenger(this.minecraft.player);
        $$1.ejectPassengers();
        for (int $$3 : $$0.b()) {
            Entity $$4 = this.level.getEntity($$3);
            if ($$4 == null) continue;
            $$4.startRiding($$1, true);
            if ($$4 != this.minecraft.player) continue;
            this.removedPlayerVehicleId = OptionalInt.empty();
            if ($$2) continue;
            if ($$1 instanceof AbstractBoat) {
                this.minecraft.player.yRotO = $$1.getYRot();
                this.minecraft.player.setYRot($$1.getYRot());
                this.minecraft.player.setYHeadRot($$1.getYRot());
            }
            MutableComponent $$5 = Component.a("mount.onboard", this.minecraft.options.keyShift.getTranslatedKeyMessage());
            this.minecraft.gui.setOverlayMessage($$5, false);
            this.minecraft.getNarrator().saySystemNow($$5);
        }
    }

    @Override
    public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getSourceId());
        if ($$1 instanceof Leashable) {
            Leashable $$2 = (Leashable)((Object)$$1);
            $$2.setDelayedLeashHolderId($$0.getDestId());
        }
    }

    private static ItemStack findTotem(Player $$0) {
        for (InteractionHand $$1 : InteractionHand.values()) {
            ItemStack $$2 = $$0.getItemInHand($$1);
            if (!$$2.has(DataComponents.DEATH_PROTECTION)) continue;
            return $$2;
        }
        return new ItemStack(Items.TOTEM_OF_UNDYING);
    }

    @Override
    public void handleEntityEvent(ClientboundEntityEventPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = $$0.getEntity(this.level);
        if ($$1 != null) {
            switch ($$0.getEventId()) {
                case 63: {
                    this.minecraft.getSoundManager().play(new SnifferSoundInstance((Sniffer)$$1));
                    break;
                }
                case 21: {
                    this.minecraft.getSoundManager().play(new GuardianAttackSoundInstance((Guardian)$$1));
                    break;
                }
                case 35: {
                    int $$2 = 40;
                    this.minecraft.particleEngine.createTrackingEmitter($$1, ParticleTypes.TOTEM_OF_UNDYING, 30);
                    this.level.playLocalSound($$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.TOTEM_USE, $$1.getSoundSource(), 1.0f, 1.0f, false);
                    if ($$1 != this.minecraft.player) break;
                    this.minecraft.gameRenderer.displayItemActivation(ClientPacketListener.findTotem(this.minecraft.player));
                    break;
                }
                default: {
                    $$1.handleEntityEvent($$0.getEventId());
                }
            }
        }
    }

    @Override
    public void handleDamageEvent(ClientboundDamageEventPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.entityId());
        if ($$1 == null) {
            return;
        }
        $$1.handleDamageEvent($$0.getSource(this.level));
    }

    @Override
    public void handleSetHealth(ClientboundSetHealthPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.player.hurtTo($$0.getHealth());
        this.minecraft.player.getFoodData().setFoodLevel($$0.getFood());
        this.minecraft.player.getFoodData().setSaturation($$0.getSaturation());
    }

    @Override
    public void handleSetExperience(ClientboundSetExperiencePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.player.setExperienceValues($$0.getExperienceProgress(), $$0.getTotalExperience(), $$0.getExperienceLevel());
    }

    @Override
    public void handleRespawn(ClientboundRespawnPacket $$0) {
        LocalPlayer $$14;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        CommonPlayerSpawnInfo $$1 = $$0.commonPlayerSpawnInfo();
        ResourceKey<Level> $$2 = $$1.dimension();
        Holder<DimensionType> $$3 = $$1.dimensionType();
        LocalPlayer $$4 = this.minecraft.player;
        ResourceKey<Level> $$5 = $$4.level().dimension();
        boolean $$6 = $$2 != $$5;
        ReceivingLevelScreen.Reason $$7 = this.determineLevelLoadingReason($$4.isDeadOrDying(), $$2, $$5);
        if ($$6) {
            ClientLevel.ClientLevelData $$12;
            Map<MapId, MapItemSavedData> $$8 = this.level.getAllMapData();
            boolean $$9 = $$1.isDebug();
            boolean $$10 = $$1.isFlat();
            int $$11 = $$1.seaLevel();
            this.levelData = $$12 = new ClientLevel.ClientLevelData(this.levelData.getDifficulty(), this.levelData.isHardcore(), $$10);
            this.level = new ClientLevel(this, $$12, $$2, $$3, this.serverChunkRadius, this.serverSimulationDistance, this.minecraft.levelRenderer, $$9, $$1.seed(), $$11);
            this.level.addMapData($$8);
            this.minecraft.setLevel(this.level, $$7);
        }
        this.minecraft.cameraEntity = null;
        if ($$4.hasContainerOpen()) {
            $$4.closeContainer();
        }
        if ($$0.shouldKeep((byte)2)) {
            LocalPlayer $$13 = this.minecraft.gameMode.createPlayer(this.level, $$4.getStats(), $$4.getRecipeBook(), $$4.getLastSentInput(), $$4.isSprinting());
        } else {
            $$14 = this.minecraft.gameMode.createPlayer(this.level, $$4.getStats(), $$4.getRecipeBook());
        }
        this.startWaitingForNewLevel($$14, this.level, $$7);
        $$14.setId($$4.getId());
        this.minecraft.player = $$14;
        if ($$6) {
            this.minecraft.getMusicManager().stopPlaying();
        }
        this.minecraft.cameraEntity = $$14;
        if ($$0.shouldKeep((byte)2)) {
            List<SynchedEntityData.DataValue<?>> $$15 = $$4.getEntityData().getNonDefaultValues();
            if ($$15 != null) {
                $$14.getEntityData().assignValues($$15);
            }
            $$14.setDeltaMovement($$4.getDeltaMovement());
            $$14.setYRot($$4.getYRot());
            $$14.setXRot($$4.getXRot());
        } else {
            $$14.resetPos();
            $$14.setYRot(-180.0f);
        }
        if ($$0.shouldKeep((byte)1)) {
            $$14.getAttributes().assignAllValues($$4.getAttributes());
        } else {
            $$14.getAttributes().assignBaseValues($$4.getAttributes());
        }
        this.level.addEntity($$14);
        $$14.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer($$14);
        $$14.setReducedDebugInfo($$4.isReducedDebugInfo());
        $$14.setShowDeathScreen($$4.shouldShowDeathScreen());
        $$14.setLastDeathLocation($$1.lastDeathLocation());
        $$14.setPortalCooldown($$1.portalCooldown());
        $$14.portalEffectIntensity = $$4.portalEffectIntensity;
        $$14.oPortalEffectIntensity = $$4.oPortalEffectIntensity;
        if (this.minecraft.screen instanceof DeathScreen || this.minecraft.screen instanceof DeathScreen.TitleConfirmScreen) {
            this.minecraft.setScreen(null);
        }
        this.minecraft.gameMode.setLocalMode($$1.gameType(), $$1.previousGameType());
    }

    private ReceivingLevelScreen.Reason determineLevelLoadingReason(boolean $$0, ResourceKey<Level> $$1, ResourceKey<Level> $$2) {
        ReceivingLevelScreen.Reason $$3 = ReceivingLevelScreen.Reason.OTHER;
        if (!$$0) {
            if ($$1 == Level.NETHER || $$2 == Level.NETHER) {
                $$3 = ReceivingLevelScreen.Reason.NETHER_PORTAL;
            } else if ($$1 == Level.END || $$2 == Level.END) {
                $$3 = ReceivingLevelScreen.Reason.END_PORTAL;
            }
        }
        return $$3;
    }

    @Override
    public void handleExplosion(ClientboundExplodePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Vec3 $$1 = $$0.center();
        this.minecraft.level.playLocalSound($$1.x(), $$1.y(), $$1.z(), $$0.explosionSound().value(), SoundSource.BLOCKS, 4.0f, (1.0f + (this.minecraft.level.random.nextFloat() - this.minecraft.level.random.nextFloat()) * 0.2f) * 0.7f, false);
        this.minecraft.level.addParticle($$0.explosionParticle(), $$1.x(), $$1.y(), $$1.z(), 1.0, 0.0, 0.0);
        $$0.playerKnockback().ifPresent(this.minecraft.player::addDeltaMovement);
    }

    @Override
    public void handleHorseScreenOpen(ClientboundHorseScreenOpenPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getEntityId());
        if ($$1 instanceof AbstractHorse) {
            AbstractHorse $$2 = (AbstractHorse)$$1;
            LocalPlayer $$3 = this.minecraft.player;
            int $$4 = $$0.getInventoryColumns();
            SimpleContainer $$5 = new SimpleContainer(AbstractHorse.getInventorySize($$4));
            HorseInventoryMenu $$6 = new HorseInventoryMenu($$0.getContainerId(), $$3.getInventory(), $$5, $$2, $$4);
            $$3.containerMenu = $$6;
            this.minecraft.setScreen(new HorseInventoryScreen($$6, $$3.getInventory(), $$2, $$4));
        }
    }

    @Override
    public void handleOpenScreen(ClientboundOpenScreenPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        MenuScreens.create($$0.getType(), this.minecraft, $$0.getContainerId(), $$0.getTitle());
    }

    @Override
    public void handleContainerSetSlot(ClientboundContainerSetSlotPacket $$0) {
        boolean $$6;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        ItemStack $$2 = $$0.getItem();
        int $$3 = $$0.getSlot();
        this.minecraft.getTutorial().onGetItem($$2);
        Screen screen = this.minecraft.screen;
        if (screen instanceof CreativeModeInventoryScreen) {
            CreativeModeInventoryScreen $$4 = (CreativeModeInventoryScreen)screen;
            boolean $$5 = !$$4.isInventoryOpen();
        } else {
            $$6 = false;
        }
        if ($$0.getContainerId() == 0) {
            ItemStack $$7;
            if (InventoryMenu.isHotbarSlot($$3) && !$$2.isEmpty() && (($$7 = $$1.inventoryMenu.getSlot($$3).getItem()).isEmpty() || $$7.getCount() < $$2.getCount())) {
                $$2.setPopTime(5);
            }
            $$1.inventoryMenu.setItem($$3, $$0.getStateId(), $$2);
        } else if (!($$0.getContainerId() != $$1.containerMenu.containerId || $$0.getContainerId() == 0 && $$6)) {
            $$1.containerMenu.setItem($$3, $$0.getStateId(), $$2);
        }
        if (this.minecraft.screen instanceof CreativeModeInventoryScreen) {
            $$1.inventoryMenu.setRemoteSlot($$3, $$2);
            $$1.inventoryMenu.broadcastChanges();
        }
    }

    @Override
    public void handleSetCursorItem(ClientboundSetCursorItemPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.getTutorial().onGetItem($$0.contents());
        if (!(this.minecraft.screen instanceof CreativeModeInventoryScreen)) {
            this.minecraft.player.containerMenu.setCarried($$0.contents());
        }
    }

    @Override
    public void handleSetPlayerInventory(ClientboundSetPlayerInventoryPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.getTutorial().onGetItem($$0.contents());
        this.minecraft.player.getInventory().setItem($$0.slot(), $$0.contents());
    }

    @Override
    public void handleContainerContent(ClientboundContainerSetContentPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        if ($$0.containerId() == 0) {
            $$1.inventoryMenu.initializeContents($$0.stateId(), $$0.items(), $$0.carriedItem());
        } else if ($$0.containerId() == $$1.containerMenu.containerId) {
            $$1.containerMenu.initializeContents($$0.stateId(), $$0.items(), $$0.carriedItem());
        }
    }

    @Override
    public void handleOpenSignEditor(ClientboundOpenSignEditorPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        BlockPos $$1 = $$0.getPos();
        BlockEntity blockEntity = this.level.getBlockEntity($$1);
        if (blockEntity instanceof SignBlockEntity) {
            SignBlockEntity $$2 = (SignBlockEntity)blockEntity;
            this.minecraft.player.openTextEdit($$2, $$0.isFrontText());
        } else {
            LOGGER.warn("Ignoring openTextEdit on an invalid entity: {} at pos {}", (Object)this.level.getBlockEntity($$1), (Object)$$1);
        }
    }

    @Override
    public void handleBlockEntityData(ClientboundBlockEntityDataPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        BlockPos $$12 = $$0.getPos();
        this.minecraft.level.getBlockEntity($$12, $$0.getType()).ifPresent($$1 -> {
            try (ProblemReporter.ScopedCollector $$2 = new ProblemReporter.ScopedCollector($$1.problemPath(), LOGGER);){
                $$1.loadWithComponents(TagValueInput.create((ProblemReporter)$$2, (HolderLookup.Provider)this.registryAccess, $$0.getTag()));
            }
            if ($$1 instanceof CommandBlockEntity && this.minecraft.screen instanceof CommandBlockEditScreen) {
                ((CommandBlockEditScreen)this.minecraft.screen).updateGui();
            }
        });
    }

    @Override
    public void handleContainerSetData(ClientboundContainerSetDataPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        if ($$1.containerMenu != null && $$1.containerMenu.containerId == $$0.getContainerId()) {
            $$1.containerMenu.setData($$0.getId(), $$0.getValue());
        }
    }

    @Override
    public void handleSetEquipment(ClientboundSetEquipmentPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$12 = this.level.getEntity($$0.getEntity());
        if ($$12 instanceof LivingEntity) {
            LivingEntity $$2 = (LivingEntity)$$12;
            $$0.getSlots().forEach($$1 -> $$2.setItemSlot((EquipmentSlot)$$1.getFirst(), (ItemStack)$$1.getSecond()));
        }
    }

    @Override
    public void handleContainerClose(ClientboundContainerClosePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.player.clientSideCloseContainer();
    }

    @Override
    public void handleBlockEvent(ClientboundBlockEventPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.level.blockEvent($$0.getPos(), $$0.getBlock(), $$0.getB0(), $$0.getB1());
    }

    @Override
    public void handleBlockDestruction(ClientboundBlockDestructionPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.level.destroyBlockProgress($$0.getId(), $$0.getPos(), $$0.getProgress());
    }

    @Override
    public void handleGameEvent(ClientboundGameEventPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        ClientboundGameEventPacket.Type $$2 = $$0.getEvent();
        float $$3 = $$0.getParam();
        int $$4 = Mth.floor($$3 + 0.5f);
        if ($$2 == ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE) {
            ((Player)$$1).displayClientMessage(Component.translatable("block.minecraft.spawn.not_valid"), false);
        } else if ($$2 == ClientboundGameEventPacket.START_RAINING) {
            this.level.getLevelData().setRaining(true);
            this.level.setRainLevel(0.0f);
        } else if ($$2 == ClientboundGameEventPacket.STOP_RAINING) {
            this.level.getLevelData().setRaining(false);
            this.level.setRainLevel(1.0f);
        } else if ($$2 == ClientboundGameEventPacket.CHANGE_GAME_MODE) {
            this.minecraft.gameMode.setLocalMode(GameType.byId($$4));
        } else if ($$2 == ClientboundGameEventPacket.WIN_GAME) {
            this.minecraft.setScreen(new WinScreen(true, () -> {
                this.minecraft.player.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
                this.minecraft.setScreen(null);
            }));
        } else if ($$2 == ClientboundGameEventPacket.DEMO_EVENT) {
            Options $$5 = this.minecraft.options;
            MutableComponent $$6 = null;
            if ($$3 == 0.0f) {
                this.minecraft.setScreen(new DemoIntroScreen());
            } else if ($$3 == 101.0f) {
                $$6 = Component.a("demo.help.movement", $$5.keyUp.getTranslatedKeyMessage(), $$5.keyLeft.getTranslatedKeyMessage(), $$5.keyDown.getTranslatedKeyMessage(), $$5.keyRight.getTranslatedKeyMessage());
            } else if ($$3 == 102.0f) {
                $$6 = Component.a("demo.help.jump", $$5.keyJump.getTranslatedKeyMessage());
            } else if ($$3 == 103.0f) {
                $$6 = Component.a("demo.help.inventory", $$5.keyInventory.getTranslatedKeyMessage());
            } else if ($$3 == 104.0f) {
                $$6 = Component.a("demo.day.6", $$5.keyScreenshot.getTranslatedKeyMessage());
            }
            if ($$6 != null) {
                this.minecraft.gui.getChat().addMessage($$6);
                this.minecraft.getNarrator().saySystemQueued($$6);
            }
        } else if ($$2 == ClientboundGameEventPacket.PLAY_ARROW_HIT_SOUND) {
            this.level.playSound((Entity)$$1, $$1.getX(), $$1.getEyeY(), $$1.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.18f, 0.45f);
        } else if ($$2 == ClientboundGameEventPacket.RAIN_LEVEL_CHANGE) {
            this.level.setRainLevel($$3);
        } else if ($$2 == ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE) {
            this.level.setThunderLevel($$3);
        } else if ($$2 == ClientboundGameEventPacket.PUFFER_FISH_STING) {
            this.level.playSound((Entity)$$1, $$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.PUFFER_FISH_STING, SoundSource.NEUTRAL, 1.0f, 1.0f);
        } else if ($$2 == ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT) {
            this.level.addParticle(ParticleTypes.ELDER_GUARDIAN, $$1.getX(), $$1.getY(), $$1.getZ(), 0.0, 0.0, 0.0);
            if ($$4 == 1) {
                this.level.playSound((Entity)$$1, $$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0f, 1.0f);
            }
        } else if ($$2 == ClientboundGameEventPacket.IMMEDIATE_RESPAWN) {
            this.minecraft.player.setShowDeathScreen($$3 == 0.0f);
        } else if ($$2 == ClientboundGameEventPacket.LIMITED_CRAFTING) {
            this.minecraft.player.setDoLimitedCrafting($$3 == 1.0f);
        } else if ($$2 == ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START && this.levelLoadStatusManager != null) {
            this.levelLoadStatusManager.loadingPacketsReceived();
        }
    }

    private void startWaitingForNewLevel(LocalPlayer $$0, ClientLevel $$1, ReceivingLevelScreen.Reason $$2) {
        this.levelLoadStatusManager = new LevelLoadStatusManager($$0, $$1, this.minecraft.levelRenderer);
        this.minecraft.setScreen(new ReceivingLevelScreen(this.levelLoadStatusManager::levelReady, $$2));
    }

    @Override
    public void handleMapItemData(ClientboundMapItemDataPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        MapId $$1 = $$0.mapId();
        MapItemSavedData $$2 = this.minecraft.level.getMapData($$1);
        if ($$2 == null) {
            $$2 = MapItemSavedData.createForClient($$0.scale(), $$0.locked(), this.minecraft.level.dimension());
            this.minecraft.level.overrideMapData($$1, $$2);
        }
        $$0.applyToMap($$2);
        this.minecraft.getMapTextureManager().update($$1, $$2);
    }

    @Override
    public void handleLevelEvent(ClientboundLevelEventPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if ($$0.isGlobalEvent()) {
            this.minecraft.level.globalLevelEvent($$0.getType(), $$0.getPos(), $$0.getData());
        } else {
            this.minecraft.level.levelEvent($$0.getType(), $$0.getPos(), $$0.getData());
        }
    }

    @Override
    public void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.advancements.update($$0);
    }

    @Override
    public void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ResourceLocation $$1 = $$0.getTab();
        if ($$1 == null) {
            this.advancements.setSelectedTab(null, false);
        } else {
            AdvancementHolder $$2 = this.advancements.get($$1);
            this.advancements.setSelectedTab($$2, false);
        }
    }

    @Override
    public void handleCommands(ClientboundCommandsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.commands = new CommandDispatcher($$0.getRoot(CommandBuildContext.simple(this.registryAccess, this.enabledFeatures), COMMAND_NODE_BUILDER));
    }

    @Override
    public void handleStopSoundEvent(ClientboundStopSoundPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.getSoundManager().stop($$0.getName(), $$0.getSource());
    }

    @Override
    public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.suggestionsProvider.completeCustomSuggestions($$0.id(), $$0.toSuggestions());
    }

    @Override
    public void handleUpdateRecipes(ClientboundUpdateRecipesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.recipes = new ClientRecipeContainer($$0.itemSets(), $$0.stonecutterRecipes());
    }

    @Override
    public void handleLookAt(ClientboundPlayerLookAtPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Vec3 $$1 = $$0.getPosition(this.level);
        if ($$1 != null) {
            this.minecraft.player.lookAt($$0.getFromAnchor(), $$1);
        }
    }

    @Override
    public void handleTagQueryPacket(ClientboundTagQueryPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if (!this.debugQueryHandler.handleResponse($$0.getTransactionId(), $$0.getTag())) {
            LOGGER.debug("Got unhandled response to tag query {}", (Object)$$0.getTransactionId());
        }
    }

    @Override
    public void handleAwardStats(ClientboundAwardStatsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        for (Object2IntMap.Entry $$1 : $$0.stats().object2IntEntrySet()) {
            Stat $$2 = (Stat)$$1.getKey();
            int $$3 = $$1.getIntValue();
            this.minecraft.player.getStats().setValue(this.minecraft.player, $$2, $$3);
        }
        Screen screen = this.minecraft.screen;
        if (screen instanceof StatsScreen) {
            StatsScreen $$4 = (StatsScreen)screen;
            $$4.onStatsUpdated();
        }
    }

    @Override
    public void handleRecipeBookAdd(ClientboundRecipeBookAddPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ClientRecipeBook $$1 = this.minecraft.player.getRecipeBook();
        if ($$0.replace()) {
            $$1.clear();
        }
        for (ClientboundRecipeBookAddPacket.Entry $$2 : $$0.entries()) {
            $$1.add($$2.contents());
            if ($$2.highlight()) {
                $$1.addHighlight($$2.contents().id());
            }
            if (!$$2.notification()) continue;
            RecipeToast.addOrUpdate(this.minecraft.getToastManager(), $$2.contents().display());
        }
        this.refreshRecipeBook($$1);
    }

    @Override
    public void handleRecipeBookRemove(ClientboundRecipeBookRemovePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ClientRecipeBook $$1 = this.minecraft.player.getRecipeBook();
        for (RecipeDisplayId $$2 : $$0.recipes()) {
            $$1.remove($$2);
        }
        this.refreshRecipeBook($$1);
    }

    @Override
    public void handleRecipeBookSettings(ClientboundRecipeBookSettingsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ClientRecipeBook $$1 = this.minecraft.player.getRecipeBook();
        $$1.setBookSettings($$0.bookSettings());
        this.refreshRecipeBook($$1);
    }

    private void refreshRecipeBook(ClientRecipeBook $$0) {
        $$0.rebuildCollections();
        this.searchTrees.updateRecipes($$0, this.level);
        Screen screen = this.minecraft.screen;
        if (screen instanceof RecipeUpdateListener) {
            RecipeUpdateListener $$1 = (RecipeUpdateListener)((Object)screen);
            $$1.recipesUpdated();
        }
    }

    @Override
    public void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getEntityId());
        if (!($$1 instanceof LivingEntity)) {
            return;
        }
        Holder<MobEffect> $$2 = $$0.getEffect();
        MobEffectInstance $$3 = new MobEffectInstance($$2, $$0.getEffectDurationTicks(), $$0.getEffectAmplifier(), $$0.isEffectAmbient(), $$0.isEffectVisible(), $$0.effectShowsIcon(), null);
        if (!$$0.shouldBlend()) {
            $$3.skipBlending();
        }
        ((LivingEntity)$$1).forceAddEffect($$3, null);
    }

    private <T> Registry.PendingTags<T> updateTags(ResourceKey<? extends Registry<? extends T>> $$0, TagNetworkSerialization.NetworkPayload $$1) {
        HolderLookup.RegistryLookup $$2 = this.registryAccess.lookupOrThrow((ResourceKey)$$0);
        return $$2.prepareTagReload($$1.resolve($$2));
    }

    @Override
    public void handleUpdateTags(ClientboundUpdateTagsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ArrayList $$1 = new ArrayList($$0.getTags().size());
        boolean $$22 = this.connection.isMemoryConnection();
        $$0.getTags().forEach(($$2, $$3) -> {
            if (!$$22 || RegistrySynchronization.isNetworkable($$2)) {
                $$1.add(this.updateTags((ResourceKey)$$2, (TagNetworkSerialization.NetworkPayload)$$3));
            }
        });
        $$1.forEach(Registry.PendingTags::apply);
        this.fuelValues = FuelValues.vanillaBurnTimes(this.registryAccess, this.enabledFeatures);
        List $$32 = List.copyOf(CreativeModeTabs.searchTab().getDisplayItems());
        this.searchTrees.updateCreativeTags($$32);
    }

    @Override
    public void handlePlayerCombatEnd(ClientboundPlayerCombatEndPacket $$0) {
    }

    @Override
    public void handlePlayerCombatEnter(ClientboundPlayerCombatEnterPacket $$0) {
    }

    @Override
    public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.playerId());
        if ($$1 == this.minecraft.player) {
            if (this.minecraft.player.shouldShowDeathScreen()) {
                this.minecraft.setScreen(new DeathScreen($$0.message(), this.level.getLevelData().isHardcore()));
            } else {
                this.minecraft.player.respawn();
            }
        }
    }

    @Override
    public void handleChangeDifficulty(ClientboundChangeDifficultyPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.levelData.setDifficulty($$0.difficulty());
        this.levelData.setDifficultyLocked($$0.locked());
    }

    @Override
    public void handleSetCamera(ClientboundSetCameraPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = $$0.getEntity(this.level);
        if ($$1 != null) {
            this.minecraft.setCameraEntity($$1);
        }
    }

    @Override
    public void handleInitializeBorder(ClientboundInitializeBorderPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        WorldBorder $$1 = this.level.getWorldBorder();
        $$1.setCenter($$0.getNewCenterX(), $$0.getNewCenterZ());
        long $$2 = $$0.getLerpTime();
        if ($$2 > 0L) {
            $$1.lerpSizeBetween($$0.getOldSize(), $$0.getNewSize(), $$2);
        } else {
            $$1.setSize($$0.getNewSize());
        }
        $$1.setAbsoluteMaxSize($$0.getNewAbsoluteMaxSize());
        $$1.setWarningBlocks($$0.getWarningBlocks());
        $$1.setWarningTime($$0.getWarningTime());
    }

    @Override
    public void handleSetBorderCenter(ClientboundSetBorderCenterPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getWorldBorder().setCenter($$0.getNewCenterX(), $$0.getNewCenterZ());
    }

    @Override
    public void handleSetBorderLerpSize(ClientboundSetBorderLerpSizePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getWorldBorder().lerpSizeBetween($$0.getOldSize(), $$0.getNewSize(), $$0.getLerpTime());
    }

    @Override
    public void handleSetBorderSize(ClientboundSetBorderSizePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getWorldBorder().setSize($$0.getSize());
    }

    @Override
    public void handleSetBorderWarningDistance(ClientboundSetBorderWarningDistancePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getWorldBorder().setWarningBlocks($$0.getWarningBlocks());
    }

    @Override
    public void handleSetBorderWarningDelay(ClientboundSetBorderWarningDelayPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getWorldBorder().setWarningTime($$0.getWarningDelay());
    }

    @Override
    public void handleTitlesClear(ClientboundClearTitlesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.clearTitles();
        if ($$0.shouldResetTimes()) {
            this.minecraft.gui.resetTitleTimes();
        }
    }

    @Override
    public void handleServerData(ClientboundServerDataPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if (this.serverData == null) {
            return;
        }
        this.serverData.motd = $$0.motd();
        $$0.iconBytes().map(ServerData::b).ifPresent(this.serverData::a);
        ServerList.saveSingleServer(this.serverData);
    }

    @Override
    public void handleCustomChatCompletions(ClientboundCustomChatCompletionsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.suggestionsProvider.modifyCustomCompletions($$0.action(), $$0.entries());
    }

    @Override
    public void setActionBarText(ClientboundSetActionBarTextPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.setOverlayMessage($$0.text(), false);
    }

    @Override
    public void setTitleText(ClientboundSetTitleTextPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.setTitle($$0.text());
    }

    @Override
    public void setSubtitleText(ClientboundSetSubtitleTextPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.setSubtitle($$0.text());
    }

    @Override
    public void setTitlesAnimation(ClientboundSetTitlesAnimationPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.setTimes($$0.getFadeIn(), $$0.getStay(), $$0.getFadeOut());
    }

    @Override
    public void handleTabListCustomisation(ClientboundTabListPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.getTabList().setHeader($$0.header().getString().isEmpty() ? null : $$0.header());
        this.minecraft.gui.getTabList().setFooter($$0.footer().getString().isEmpty() ? null : $$0.footer());
    }

    @Override
    public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity entity = $$0.getEntity(this.level);
        if (entity instanceof LivingEntity) {
            LivingEntity $$1 = (LivingEntity)entity;
            $$1.removeEffectNoUpdate($$0.effect());
        }
    }

    @Override
    public void handlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        for (UUID $$1 : $$0.profileIds()) {
            this.minecraft.getPlayerSocialManager().removePlayer($$1);
            PlayerInfo $$2 = this.playerInfoMap.remove($$1);
            if ($$2 == null) continue;
            this.listedPlayers.remove($$2);
        }
    }

    @Override
    public void handlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        for (ClientboundPlayerInfoUpdatePacket.Entry $$1 : $$0.newEntries()) {
            PlayerInfo $$2 = new PlayerInfo(Objects.requireNonNull($$1.profile()), this.enforcesSecureChat());
            if (this.playerInfoMap.putIfAbsent($$1.profileId(), $$2) != null) continue;
            this.minecraft.getPlayerSocialManager().addPlayer($$2);
        }
        for (ClientboundPlayerInfoUpdatePacket.Entry $$3 : $$0.entries()) {
            PlayerInfo $$4 = this.playerInfoMap.get($$3.profileId());
            if ($$4 == null) {
                LOGGER.warn("Ignoring player info update for unknown player {} ({})", (Object)$$3.profileId(), (Object)$$0.actions());
                continue;
            }
            for (ClientboundPlayerInfoUpdatePacket.Action $$5 : $$0.actions()) {
                this.applyPlayerInfoUpdate($$5, $$3, $$4);
            }
        }
    }

    private void applyPlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket.Action $$0, ClientboundPlayerInfoUpdatePacket.Entry $$1, PlayerInfo $$2) {
        switch ($$0) {
            case INITIALIZE_CHAT: {
                this.initializeChatSession($$1, $$2);
                break;
            }
            case UPDATE_GAME_MODE: {
                if ($$2.getGameMode() != $$1.gameMode() && this.minecraft.player != null && this.minecraft.player.getUUID().equals($$1.profileId())) {
                    this.minecraft.player.onGameModeChanged($$1.gameMode());
                }
                $$2.setGameMode($$1.gameMode());
                break;
            }
            case UPDATE_LISTED: {
                if ($$1.listed()) {
                    this.listedPlayers.add($$2);
                    break;
                }
                this.listedPlayers.remove($$2);
                break;
            }
            case UPDATE_LATENCY: {
                $$2.setLatency($$1.latency());
                break;
            }
            case UPDATE_DISPLAY_NAME: {
                $$2.setTabListDisplayName($$1.displayName());
                break;
            }
            case UPDATE_HAT: {
                $$2.setShowHat($$1.showHat());
                break;
            }
            case UPDATE_LIST_ORDER: {
                $$2.setTabListOrder($$1.listOrder());
            }
        }
    }

    private void initializeChatSession(ClientboundPlayerInfoUpdatePacket.Entry $$0, PlayerInfo $$1) {
        GameProfile $$2 = $$1.getProfile();
        SignatureValidator $$3 = this.minecraft.getProfileKeySignatureValidator();
        if ($$3 == null) {
            LOGGER.warn("Ignoring chat session from {} due to missing Services public key", (Object)$$2.getName());
            $$1.clearChatSession(this.enforcesSecureChat());
            return;
        }
        RemoteChatSession.Data $$4 = $$0.chatSession();
        if ($$4 != null) {
            try {
                RemoteChatSession $$5 = $$4.validate($$2, $$3);
                $$1.setChatSession($$5);
            } catch (ProfilePublicKey.ValidationException $$6) {
                LOGGER.error("Failed to validate profile key for player: '{}'", (Object)$$2.getName(), (Object)$$6);
                $$1.clearChatSession(this.enforcesSecureChat());
            }
        } else {
            $$1.clearChatSession(this.enforcesSecureChat());
        }
    }

    private boolean enforcesSecureChat() {
        return this.minecraft.canValidateProfileKeys() && this.serverEnforcesSecureChat;
    }

    @Override
    public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        $$1.getAbilities().flying = $$0.isFlying();
        $$1.getAbilities().instabuild = $$0.canInstabuild();
        $$1.getAbilities().invulnerable = $$0.isInvulnerable();
        $$1.getAbilities().mayfly = $$0.canFly();
        $$1.getAbilities().setFlyingSpeed($$0.getFlyingSpeed());
        $$1.getAbilities().setWalkingSpeed($$0.getWalkingSpeed());
    }

    @Override
    public void handleSoundEvent(ClientboundSoundPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.level.playSeededSound((Entity)this.minecraft.player, $$0.getX(), $$0.getY(), $$0.getZ(), $$0.getSound(), $$0.getSource(), $$0.getVolume(), $$0.getPitch(), $$0.getSeed());
    }

    @Override
    public void handleSoundEntityEvent(ClientboundSoundEntityPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getId());
        if ($$1 == null) {
            return;
        }
        this.minecraft.level.playSeededSound(this.minecraft.player, $$1, $$0.getSound(), $$0.getSource(), $$0.getVolume(), $$0.getPitch(), $$0.getSeed());
    }

    @Override
    public void handleBossUpdate(ClientboundBossEventPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.getBossOverlay().update($$0);
    }

    @Override
    public void handleItemCooldown(ClientboundCooldownPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if ($$0.duration() == 0) {
            this.minecraft.player.getCooldowns().removeCooldown($$0.cooldownGroup());
        } else {
            this.minecraft.player.getCooldowns().addCooldown($$0.cooldownGroup(), $$0.duration());
        }
    }

    @Override
    public void handleMoveVehicle(ClientboundMoveVehiclePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.minecraft.player.getRootVehicle();
        if ($$1 != this.minecraft.player && $$1.isLocalInstanceAuthoritative()) {
            Vec3 $$4;
            Vec3 $$2 = $$0.position();
            if ($$1.isInterpolating()) {
                Vec3 $$3 = $$1.getInterpolation().position();
            } else {
                $$4 = $$1.position();
            }
            if ($$2.distanceTo($$4) > (double)1.0E-5f) {
                if ($$1.isInterpolating()) {
                    $$1.getInterpolation().cancel();
                }
                $$1.absSnapTo($$2.x(), $$2.y(), $$2.z(), $$0.yRot(), $$0.xRot());
            }
            this.connection.send(ServerboundMoveVehiclePacket.fromEntity($$1));
        }
    }

    @Override
    public void handleOpenBook(ClientboundOpenBookPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ItemStack $$1 = this.minecraft.player.getItemInHand($$0.getHand());
        BookViewScreen.BookAccess $$2 = BookViewScreen.BookAccess.fromItem($$1);
        if ($$2 != null) {
            this.minecraft.setScreen(new BookViewScreen($$2));
        }
    }

    @Override
    public void handleCustomPayload(CustomPacketPayload $$0) {
        if ($$0 instanceof PathfindingDebugPayload) {
            PathfindingDebugPayload $$1 = (PathfindingDebugPayload)$$0;
            this.minecraft.debugRenderer.pathfindingRenderer.addPath($$1.entityId(), $$1.path(), $$1.maxNodeDistance());
        } else if ($$0 instanceof NeighborUpdatesDebugPayload) {
            NeighborUpdatesDebugPayload $$2 = (NeighborUpdatesDebugPayload)$$0;
            this.minecraft.debugRenderer.neighborsUpdateRenderer.addUpdate($$2.time(), $$2.pos());
        } else if ($$0 instanceof RedstoneWireOrientationsDebugPayload) {
            RedstoneWireOrientationsDebugPayload $$3 = (RedstoneWireOrientationsDebugPayload)$$0;
            this.minecraft.debugRenderer.redstoneWireOrientationsRenderer.addWireOrientations($$3);
        } else if ($$0 instanceof StructuresDebugPayload) {
            StructuresDebugPayload $$4 = (StructuresDebugPayload)$$0;
            this.minecraft.debugRenderer.structureRenderer.addBoundingBox($$4.mainBB(), $$4.pieces(), $$4.dimension());
        } else if ($$0 instanceof WorldGenAttemptDebugPayload) {
            WorldGenAttemptDebugPayload $$5 = (WorldGenAttemptDebugPayload)$$0;
            ((WorldGenAttemptRenderer)this.minecraft.debugRenderer.worldGenAttemptRenderer).addPos($$5.pos(), $$5.scale(), $$5.red(), $$5.green(), $$5.blue(), $$5.alpha());
        } else if ($$0 instanceof PoiTicketCountDebugPayload) {
            PoiTicketCountDebugPayload $$6 = (PoiTicketCountDebugPayload)$$0;
            this.minecraft.debugRenderer.brainDebugRenderer.setFreeTicketCount($$6.pos(), $$6.freeTicketCount());
        } else if ($$0 instanceof PoiAddedDebugPayload) {
            PoiAddedDebugPayload $$7 = (PoiAddedDebugPayload)$$0;
            BrainDebugRenderer.PoiInfo $$8 = new BrainDebugRenderer.PoiInfo($$7.pos(), $$7.poiType(), $$7.freeTicketCount());
            this.minecraft.debugRenderer.brainDebugRenderer.addPoi($$8);
        } else if ($$0 instanceof PoiRemovedDebugPayload) {
            PoiRemovedDebugPayload $$9 = (PoiRemovedDebugPayload)$$0;
            this.minecraft.debugRenderer.brainDebugRenderer.removePoi($$9.pos());
        } else if ($$0 instanceof VillageSectionsDebugPayload) {
            VillageSectionsDebugPayload $$10 = (VillageSectionsDebugPayload)$$0;
            VillageSectionsDebugRenderer $$11 = this.minecraft.debugRenderer.villageSectionsDebugRenderer;
            $$10.villageChunks().forEach($$11::setVillageSection);
            $$10.notVillageChunks().forEach($$11::setNotVillageSection);
        } else if ($$0 instanceof GoalDebugPayload) {
            GoalDebugPayload $$12 = (GoalDebugPayload)$$0;
            this.minecraft.debugRenderer.goalSelectorRenderer.addGoalSelector($$12.entityId(), $$12.pos(), $$12.goals());
        } else if ($$0 instanceof BrainDebugPayload) {
            BrainDebugPayload $$13 = (BrainDebugPayload)$$0;
            this.minecraft.debugRenderer.brainDebugRenderer.addOrUpdateBrainDump($$13.brainDump());
        } else if ($$0 instanceof BeeDebugPayload) {
            BeeDebugPayload $$14 = (BeeDebugPayload)$$0;
            this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateBeeInfo($$14.beeInfo());
        } else if ($$0 instanceof HiveDebugPayload) {
            HiveDebugPayload $$15 = (HiveDebugPayload)$$0;
            this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateHiveInfo($$15.hiveInfo(), this.level.getGameTime());
        } else if ($$0 instanceof GameTestAddMarkerDebugPayload) {
            GameTestAddMarkerDebugPayload $$16 = (GameTestAddMarkerDebugPayload)$$0;
            this.minecraft.debugRenderer.gameTestDebugRenderer.addMarker($$16.pos(), $$16.color(), $$16.text(), $$16.durationMs());
        } else if ($$0 instanceof GameTestClearMarkersDebugPayload) {
            this.minecraft.debugRenderer.gameTestDebugRenderer.clear();
        } else if ($$0 instanceof RaidsDebugPayload) {
            RaidsDebugPayload $$17 = (RaidsDebugPayload)$$0;
            this.minecraft.debugRenderer.raidDebugRenderer.setRaidCenters($$17.raidCenters());
        } else if ($$0 instanceof GameEventDebugPayload) {
            GameEventDebugPayload $$18 = (GameEventDebugPayload)$$0;
            this.minecraft.debugRenderer.gameEventListenerRenderer.trackGameEvent($$18.gameEventType(), $$18.pos());
        } else if ($$0 instanceof GameEventListenerDebugPayload) {
            GameEventListenerDebugPayload $$19 = (GameEventListenerDebugPayload)$$0;
            this.minecraft.debugRenderer.gameEventListenerRenderer.trackListener($$19.listenerPos(), $$19.listenerRange());
        } else if ($$0 instanceof BreezeDebugPayload) {
            BreezeDebugPayload $$20 = (BreezeDebugPayload)$$0;
            this.minecraft.debugRenderer.breezeDebugRenderer.add($$20.breezeInfo());
        } else {
            this.handleUnknownCustomPayload($$0);
        }
    }

    private void handleUnknownCustomPayload(CustomPacketPayload $$0) {
        LOGGER.warn("Unknown custom packet payload: {}", (Object)$$0.type().id());
    }

    @Override
    public void handleAddObjective(ClientboundSetObjectivePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        String $$1 = $$0.getObjectiveName();
        if ($$0.getMethod() == 0) {
            this.scoreboard.addObjective($$1, ObjectiveCriteria.DUMMY, $$0.getDisplayName(), $$0.getRenderType(), false, $$0.getNumberFormat().orElse(null));
        } else {
            Objective $$2 = this.scoreboard.getObjective($$1);
            if ($$2 != null) {
                if ($$0.getMethod() == 1) {
                    this.scoreboard.removeObjective($$2);
                } else if ($$0.getMethod() == 2) {
                    $$2.setRenderType($$0.getRenderType());
                    $$2.setDisplayName($$0.getDisplayName());
                    $$2.setNumberFormat($$0.getNumberFormat().orElse(null));
                }
            }
        }
    }

    @Override
    public void handleSetScore(ClientboundSetScorePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        String $$1 = $$0.objectiveName();
        ScoreHolder $$2 = ScoreHolder.forNameOnly($$0.owner());
        Objective $$3 = this.scoreboard.getObjective($$1);
        if ($$3 != null) {
            ScoreAccess $$4 = this.scoreboard.getOrCreatePlayerScore($$2, $$3, true);
            $$4.set($$0.score());
            $$4.display($$0.display().orElse(null));
            $$4.numberFormatOverride($$0.numberFormat().orElse(null));
        } else {
            LOGGER.warn("Received packet for unknown scoreboard objective: {}", (Object)$$1);
        }
    }

    @Override
    public void handleResetScore(ClientboundResetScorePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        String $$1 = $$0.objectiveName();
        ScoreHolder $$2 = ScoreHolder.forNameOnly($$0.owner());
        if ($$1 == null) {
            this.scoreboard.resetAllPlayerScores($$2);
        } else {
            Objective $$3 = this.scoreboard.getObjective($$1);
            if ($$3 != null) {
                this.scoreboard.resetSinglePlayerScore($$2, $$3);
            } else {
                LOGGER.warn("Received packet for unknown scoreboard objective: {}", (Object)$$1);
            }
        }
    }

    @Override
    public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        String $$1 = $$0.getObjectiveName();
        Objective $$2 = $$1 == null ? null : this.scoreboard.getObjective($$1);
        this.scoreboard.setDisplayObjective($$0.getSlot(), $$2);
    }

    @Override
    public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket $$0) {
        PlayerTeam $$3;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ClientboundSetPlayerTeamPacket.Action $$12 = $$0.getTeamAction();
        if ($$12 == ClientboundSetPlayerTeamPacket.Action.ADD) {
            PlayerTeam $$2 = this.scoreboard.addPlayerTeam($$0.getName());
        } else {
            $$3 = this.scoreboard.getPlayerTeam($$0.getName());
            if ($$3 == null) {
                LOGGER.warn("Received packet for unknown team {}: team action: {}, player action: {}", new Object[]{$$0.getName(), $$0.getTeamAction(), $$0.getPlayerAction()});
                return;
            }
        }
        Optional<ClientboundSetPlayerTeamPacket.Parameters> $$4 = $$0.getParameters();
        $$4.ifPresent($$1 -> {
            $$3.setDisplayName($$1.getDisplayName());
            $$3.setColor($$1.getColor());
            $$3.unpackOptions($$1.getOptions());
            $$3.setNameTagVisibility($$1.getNametagVisibility());
            $$3.setCollisionRule($$1.getCollisionRule());
            $$3.setPlayerPrefix($$1.getPlayerPrefix());
            $$3.setPlayerSuffix($$1.getPlayerSuffix());
        });
        ClientboundSetPlayerTeamPacket.Action $$5 = $$0.getPlayerAction();
        if ($$5 == ClientboundSetPlayerTeamPacket.Action.ADD) {
            for (String $$6 : $$0.getPlayers()) {
                this.scoreboard.addPlayerToTeam($$6, $$3);
            }
        } else if ($$5 == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
            for (String $$7 : $$0.getPlayers()) {
                this.scoreboard.removePlayerFromTeam($$7, $$3);
            }
        }
        if ($$12 == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
            this.scoreboard.removePlayerTeam($$3);
        }
    }

    @Override
    public void handleParticleEvent(ClientboundLevelParticlesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if ($$0.getCount() == 0) {
            double $$1 = $$0.getMaxSpeed() * $$0.getXDist();
            double $$2 = $$0.getMaxSpeed() * $$0.getYDist();
            double $$3 = $$0.getMaxSpeed() * $$0.getZDist();
            try {
                this.level.addParticle($$0.getParticle(), $$0.isOverrideLimiter(), $$0.alwaysShow(), $$0.getX(), $$0.getY(), $$0.getZ(), $$1, $$2, $$3);
            } catch (Throwable $$4) {
                LOGGER.warn("Could not spawn particle effect {}", (Object)$$0.getParticle());
            }
        } else {
            for (int $$5 = 0; $$5 < $$0.getCount(); ++$$5) {
                double $$6 = this.random.nextGaussian() * (double)$$0.getXDist();
                double $$7 = this.random.nextGaussian() * (double)$$0.getYDist();
                double $$8 = this.random.nextGaussian() * (double)$$0.getZDist();
                double $$9 = this.random.nextGaussian() * (double)$$0.getMaxSpeed();
                double $$10 = this.random.nextGaussian() * (double)$$0.getMaxSpeed();
                double $$11 = this.random.nextGaussian() * (double)$$0.getMaxSpeed();
                try {
                    this.level.addParticle($$0.getParticle(), $$0.isOverrideLimiter(), $$0.alwaysShow(), $$0.getX() + $$6, $$0.getY() + $$7, $$0.getZ() + $$8, $$9, $$10, $$11);
                    continue;
                } catch (Throwable $$12) {
                    LOGGER.warn("Could not spawn particle effect {}", (Object)$$0.getParticle());
                    return;
                }
            }
        }
    }

    @Override
    public void handleUpdateAttributes(ClientboundUpdateAttributesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getEntityId());
        if ($$1 == null) {
            return;
        }
        if (!($$1 instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + String.valueOf($$1) + ")");
        }
        AttributeMap $$2 = ((LivingEntity)$$1).getAttributes();
        for (ClientboundUpdateAttributesPacket.AttributeSnapshot $$3 : $$0.getValues()) {
            AttributeInstance $$4 = $$2.getInstance($$3.attribute());
            if ($$4 == null) {
                LOGGER.warn("Entity {} does not have attribute {}", (Object)$$1, (Object)$$3.attribute().getRegisteredName());
                continue;
            }
            $$4.setBaseValue($$3.base());
            $$4.removeModifiers();
            for (AttributeModifier $$5 : $$3.modifiers()) {
                $$4.addTransientModifier($$5);
            }
        }
    }

    @Override
    public void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        AbstractContainerMenu $$1 = this.minecraft.player.containerMenu;
        if ($$1.containerId != $$0.containerId()) {
            return;
        }
        Screen screen = this.minecraft.screen;
        if (screen instanceof RecipeUpdateListener) {
            RecipeUpdateListener $$2 = (RecipeUpdateListener)((Object)screen);
            $$2.fillGhostRecipe($$0.recipeDisplay());
        }
    }

    @Override
    public void handleLightUpdatePacket(ClientboundLightUpdatePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        int $$1 = $$0.getX();
        int $$2 = $$0.getZ();
        ClientboundLightUpdatePacketData $$3 = $$0.getLightData();
        this.level.queueLightUpdate(() -> this.applyLightData($$1, $$2, $$3, true));
    }

    private void applyLightData(int $$0, int $$1, ClientboundLightUpdatePacketData $$2, boolean $$3) {
        LevelLightEngine $$4 = this.level.getChunkSource().getLightEngine();
        BitSet $$5 = $$2.getSkyYMask();
        BitSet $$6 = $$2.getEmptySkyYMask();
        Iterator<byte[]> $$7 = $$2.getSkyUpdates().iterator();
        this.readSectionList($$0, $$1, $$4, LightLayer.SKY, $$5, $$6, $$7, $$3);
        BitSet $$8 = $$2.getBlockYMask();
        BitSet $$9 = $$2.getEmptyBlockYMask();
        Iterator<byte[]> $$10 = $$2.getBlockUpdates().iterator();
        this.readSectionList($$0, $$1, $$4, LightLayer.BLOCK, $$8, $$9, $$10, $$3);
        $$4.setLightEnabled(new ChunkPos($$0, $$1), true);
    }

    @Override
    public void handleMerchantOffers(ClientboundMerchantOffersPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        AbstractContainerMenu $$1 = this.minecraft.player.containerMenu;
        if ($$0.getContainerId() == $$1.containerId && $$1 instanceof MerchantMenu) {
            MerchantMenu $$2 = (MerchantMenu)$$1;
            $$2.setOffers($$0.getOffers());
            $$2.setXp($$0.getVillagerXp());
            $$2.setMerchantLevel($$0.getVillagerLevel());
            $$2.setShowProgressBar($$0.showProgress());
            $$2.setCanRestock($$0.canRestock());
        }
    }

    @Override
    public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.serverChunkRadius = $$0.getRadius();
        this.minecraft.options.setServerRenderDistance(this.serverChunkRadius);
        this.level.getChunkSource().updateViewRadius($$0.getRadius());
    }

    @Override
    public void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.serverSimulationDistance = $$0.simulationDistance();
        this.level.setServerSimulationDistance(this.serverSimulationDistance);
    }

    @Override
    public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getChunkSource().updateViewCenter($$0.getX(), $$0.getZ());
    }

    @Override
    public void handleBlockChangedAck(ClientboundBlockChangedAckPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.handleBlockChangedAck($$0.sequence());
    }

    @Override
    public void handleBundlePacket(ClientboundBundlePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        for (Packet<ClientPacketListener> packet : $$0.subPackets()) {
            packet.handle(this);
        }
    }

    @Override
    public void handleProjectilePowerPacket(ClientboundProjectilePowerPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getId());
        if ($$1 instanceof AbstractHurtingProjectile) {
            AbstractHurtingProjectile $$2 = (AbstractHurtingProjectile)$$1;
            $$2.accelerationPower = $$0.getAccelerationPower();
        }
    }

    @Override
    public void handleChunkBatchStart(ClientboundChunkBatchStartPacket $$0) {
        this.chunkBatchSizeCalculator.onBatchStart();
    }

    @Override
    public void handleChunkBatchFinished(ClientboundChunkBatchFinishedPacket $$0) {
        this.chunkBatchSizeCalculator.onBatchFinished($$0.batchSize());
        this.send(new ServerboundChunkBatchReceivedPacket(this.chunkBatchSizeCalculator.getDesiredChunksPerTick()));
    }

    @Override
    public void handleDebugSample(ClientboundDebugSamplePacket $$0) {
        this.minecraft.getDebugOverlay().a($$0.b(), $$0.debugSampleType());
    }

    @Override
    public void handlePongResponse(ClientboundPongResponsePacket $$0) {
        this.pingDebugMonitor.onPongReceived($$0);
    }

    @Override
    public void handleTestInstanceBlockStatus(ClientboundTestInstanceBlockStatus $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Screen screen = this.minecraft.screen;
        if (screen instanceof TestInstanceBlockEditScreen) {
            TestInstanceBlockEditScreen $$1 = (TestInstanceBlockEditScreen)screen;
            $$1.setStatus($$0.status(), $$0.size());
        }
    }

    @Override
    public void handleWaypoint(ClientboundTrackedWaypointPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        $$0.apply(this.waypointManager);
    }

    private void readSectionList(int $$0, int $$1, LevelLightEngine $$2, LightLayer $$3, BitSet $$4, BitSet $$5, Iterator<byte[]> $$6, boolean $$7) {
        for (int $$8 = 0; $$8 < $$2.getLightSectionCount(); ++$$8) {
            int $$9 = $$2.getMinLightSection() + $$8;
            boolean $$10 = $$4.get($$8);
            boolean $$11 = $$5.get($$8);
            if (!$$10 && !$$11) continue;
            $$2.queueSectionData($$3, SectionPos.of($$0, $$9, $$1), $$10 ? new DataLayer((byte[])$$6.next().clone()) : new DataLayer());
            if (!$$7) continue;
            this.level.setSectionDirtyWithNeighbors($$0, $$9, $$1);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected() && !this.closed;
    }

    public Collection<PlayerInfo> getListedOnlinePlayers() {
        return this.listedPlayers;
    }

    public Collection<PlayerInfo> getOnlinePlayers() {
        return this.playerInfoMap.values();
    }

    public Collection<UUID> getOnlinePlayerIds() {
        return this.playerInfoMap.keySet();
    }

    @Nullable
    public PlayerInfo getPlayerInfo(UUID $$0) {
        return this.playerInfoMap.get($$0);
    }

    @Nullable
    public PlayerInfo getPlayerInfo(String $$0) {
        for (PlayerInfo $$1 : this.playerInfoMap.values()) {
            if (!$$1.getProfile().getName().equals($$0)) continue;
            return $$1;
        }
        return null;
    }

    public GameProfile getLocalGameProfile() {
        return this.localGameProfile;
    }

    public ClientAdvancements getAdvancements() {
        return this.advancements;
    }

    public CommandDispatcher<ClientSuggestionProvider> getCommands() {
        return this.commands;
    }

    public ClientLevel getLevel() {
        return this.level;
    }

    public DebugQueryHandler getDebugQueryHandler() {
        return this.debugQueryHandler;
    }

    public UUID getId() {
        return this.id;
    }

    public Set<ResourceKey<Level>> levels() {
        return this.levels;
    }

    public RegistryAccess.Frozen registryAccess() {
        return this.registryAccess;
    }

    public void markMessageAsProcessed(MessageSignature $$0, boolean $$1) {
        if (this.lastSeenMessages.addPending($$0, $$1) && this.lastSeenMessages.offset() > 64) {
            this.sendChatAcknowledgement();
        }
    }

    private void sendChatAcknowledgement() {
        int $$0 = this.lastSeenMessages.getAndClearOffset();
        if ($$0 > 0) {
            this.send(new ServerboundChatAckPacket($$0));
        }
    }

    public void sendChat(String $$0) {
        Instant $$1 = Instant.now();
        long $$2 = Crypt.SaltSupplier.getLong();
        LastSeenMessagesTracker.Update $$3 = this.lastSeenMessages.generateAndApplyUpdate();
        MessageSignature $$4 = this.signedMessageEncoder.pack(new SignedMessageBody($$0, $$1, $$2, $$3.lastSeen()));
        this.send(new ServerboundChatPacket($$0, $$1, $$2, $$4, $$3.update()));
    }

    public void sendCommand(String $$0) {
        SignableCommand $$1 = SignableCommand.of(this.commands.parse($$0, (Object)this.suggestionsProvider));
        if ($$1.arguments().isEmpty()) {
            this.send(new ServerboundChatCommandPacket($$0));
            return;
        }
        Instant $$2 = Instant.now();
        long $$32 = Crypt.SaltSupplier.getLong();
        LastSeenMessagesTracker.Update $$4 = this.lastSeenMessages.generateAndApplyUpdate();
        ArgumentSignatures $$5 = ArgumentSignatures.signCommand($$1, $$3 -> {
            SignedMessageBody $$4 = new SignedMessageBody($$3, $$2, $$32, $$4.lastSeen());
            return this.signedMessageEncoder.pack($$4);
        });
        this.send(new ServerboundChatCommandSignedPacket($$0, $$2, $$32, $$5, $$4.update()));
    }

    public void sendUnattendedCommand(String $$0, @Nullable Screen $$1) {
        switch (this.verifyCommand($$0).ordinal()) {
            case 0: {
                this.send(new ServerboundChatCommandPacket($$0));
                this.minecraft.setScreen($$1);
                break;
            }
            case 1: {
                this.openCommandSendConfirmationWindow($$0, "multiplayer.confirm_command.parse_errors", $$1);
                break;
            }
            case 3: {
                this.openCommandSendConfirmationWindow($$0, "multiplayer.confirm_command.permissions_required", $$1);
                break;
            }
            case 2: {
                LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", (Object)$$0);
            }
        }
    }

    private CommandCheckResult verifyCommand(String $$0) {
        ParseResults $$1 = this.commands.parse($$0, (Object)this.suggestionsProvider);
        if (!ClientPacketListener.isValidCommand($$1)) {
            return CommandCheckResult.PARSE_ERRORS;
        }
        if (SignableCommand.hasSignableArguments($$1)) {
            return CommandCheckResult.SIGNATURE_REQUIRED;
        }
        ParseResults $$2 = this.commands.parse($$0, (Object)this.restrictedSuggestionsProvider);
        if (!ClientPacketListener.isValidCommand($$2)) {
            return CommandCheckResult.PERMISSIONS_REQUIRED;
        }
        return CommandCheckResult.NO_ISSUES;
    }

    private static boolean isValidCommand(ParseResults<?> $$0) {
        return !$$0.getReader().canRead() && $$0.getExceptions().isEmpty() && $$0.getContext().getLastChild().getCommand() != null;
    }

    private void openCommandSendConfirmationWindow(String $$0, String $$1, @Nullable Screen $$2) {
        Screen $$32 = this.minecraft.screen;
        this.minecraft.setScreen(new ConfirmScreen($$3 -> {
            if ($$3) {
                this.send(new ServerboundChatCommandPacket($$0));
            }
            if ($$3) {
                this.minecraft.setScreen($$2);
            } else {
                this.minecraft.setScreen($$32);
            }
        }, COMMAND_SEND_CONFIRM_TITLE, Component.a($$1, Component.literal($$0).withStyle(ChatFormatting.YELLOW))));
    }

    public void broadcastClientInformation(ClientInformation $$0) {
        if (!$$0.equals((Object)this.remoteClientInformation)) {
            this.send(new ServerboundClientInformationPacket($$0));
            this.remoteClientInformation = $$0;
        }
    }

    @Override
    public void tick() {
        if (this.chatSession != null && this.minecraft.getProfileKeyPairManager().shouldRefreshKeyPair()) {
            this.prepareKeyPair();
        }
        if (this.keyPairFuture != null && this.keyPairFuture.isDone()) {
            this.keyPairFuture.join().ifPresent(this::setKeyPair);
            this.keyPairFuture = null;
        }
        this.sendDeferredPackets();
        if (this.minecraft.getDebugOverlay().showNetworkCharts()) {
            this.pingDebugMonitor.tick();
        }
        this.debugSampleSubscriber.tick();
        this.telemetryManager.tick();
        if (this.levelLoadStatusManager != null) {
            this.levelLoadStatusManager.tick();
            if (this.levelLoadStatusManager.levelReady() && !this.minecraft.player.hasClientLoaded()) {
                this.connection.send(new ServerboundPlayerLoadedPacket());
                this.minecraft.player.setClientLoaded(true);
            }
        }
    }

    public void prepareKeyPair() {
        this.keyPairFuture = this.minecraft.getProfileKeyPairManager().prepareKeyPair();
    }

    private void setKeyPair(ProfileKeyPair $$0) {
        if (!this.minecraft.isLocalPlayer(this.localGameProfile.getId())) {
            return;
        }
        if (this.chatSession != null && this.chatSession.keyPair().equals((Object)$$0)) {
            return;
        }
        this.chatSession = LocalChatSession.create($$0);
        this.signedMessageEncoder = this.chatSession.createMessageEncoder(this.localGameProfile.getId());
        this.send(new ServerboundChatSessionUpdatePacket(this.chatSession.asRemote().asData()));
    }

    @Override
    protected DialogConnectionAccess createDialogAccess() {
        return new DialogConnectionAccess(){

            @Override
            public void disconnect(Component $$0) {
                ClientPacketListener.this.getConnection().disconnect($$0);
            }

            @Override
            public void runCommand(String $$0, @Nullable Screen $$1) {
                ClientPacketListener.this.sendUnattendedCommand($$0, $$1);
            }

            @Override
            public void openDialog(Holder<Dialog> $$0, @Nullable Screen $$1) {
                ClientPacketListener.this.showDialog($$0, this, $$1);
            }

            @Override
            public void sendCustomAction(ResourceLocation $$0, Optional<Tag> $$1) {
                ClientPacketListener.this.send(new ServerboundCustomClickActionPacket($$0, $$1));
            }

            @Override
            public ServerLinks serverLinks() {
                return ClientPacketListener.this.serverLinks();
            }
        };
    }

    @Nullable
    public ServerData getServerData() {
        return this.serverData;
    }

    public FeatureFlagSet enabledFeatures() {
        return this.enabledFeatures;
    }

    public boolean isFeatureEnabled(FeatureFlagSet $$0) {
        return $$0.isSubsetOf(this.enabledFeatures());
    }

    public Scoreboard scoreboard() {
        return this.scoreboard;
    }

    public PotionBrewing potionBrewing() {
        return this.potionBrewing;
    }

    public FuelValues fuelValues() {
        return this.fuelValues;
    }

    public void updateSearchTrees() {
        this.searchTrees.rebuildAfterLanguageChange();
    }

    public SessionSearchTrees searchTrees() {
        return this.searchTrees;
    }

    public void registerForCleaning(CacheSlot<?, ?> $$0) {
        this.cacheSlots.add(new WeakReference($$0));
    }

    public HashedPatchMap.HashGenerator decoratedHashOpsGenenerator() {
        return this.decoratedHashOpsGenerator;
    }

    public ClientWaypointManager getWaypointManager() {
        return this.waypointManager;
    }

    static final class CommandCheckResult
    extends Enum<CommandCheckResult> {
        public static final /* enum */ CommandCheckResult NO_ISSUES = new CommandCheckResult();
        public static final /* enum */ CommandCheckResult PARSE_ERRORS = new CommandCheckResult();
        public static final /* enum */ CommandCheckResult SIGNATURE_REQUIRED = new CommandCheckResult();
        public static final /* enum */ CommandCheckResult PERMISSIONS_REQUIRED = new CommandCheckResult();
        private static final /* synthetic */ CommandCheckResult[] $VALUES;

        public static CommandCheckResult[] values() {
            return (CommandCheckResult[])$VALUES.clone();
        }

        public static CommandCheckResult valueOf(String $$0) {
            return Enum.valueOf(CommandCheckResult.class, $$0);
        }

        private static /* synthetic */ CommandCheckResult[] a() {
            return new CommandCheckResult[]{NO_ISSUES, PARSE_ERRORS, SIGNATURE_REQUIRED, PERMISSIONS_REQUIRED};
        }

        static {
            $VALUES = CommandCheckResult.a();
        }
    }
}

