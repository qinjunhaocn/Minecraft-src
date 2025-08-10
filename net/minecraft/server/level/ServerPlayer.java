/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.server.level;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashCode;
import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.waypoints.ServerWaypointManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.HashOps;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.RemoteSlot;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.ServerItemCooldowns;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.slf4j.Logger;

public class ServerPlayer
extends Player {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int NEUTRAL_MOB_DEATH_NOTIFICATION_RADII_XZ = 32;
    private static final int NEUTRAL_MOB_DEATH_NOTIFICATION_RADII_Y = 10;
    private static final int FLY_STAT_RECORDING_SPEED = 25;
    public static final double BLOCK_INTERACTION_DISTANCE_VERIFICATION_BUFFER = 1.0;
    public static final double ENTITY_INTERACTION_DISTANCE_VERIFICATION_BUFFER = 3.0;
    public static final int ENDER_PEARL_TICKET_RADIUS = 2;
    public static final String ENDER_PEARLS_TAG = "ender_pearls";
    public static final String ENDER_PEARL_DIMENSION_TAG = "ender_pearl_dimension";
    public static final String TAG_DIMENSION = "Dimension";
    private static final AttributeModifier CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER = new AttributeModifier(ResourceLocation.withDefaultNamespace("creative_mode_block_range"), 0.5, AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeModifier CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER = new AttributeModifier(ResourceLocation.withDefaultNamespace("creative_mode_entity_range"), 2.0, AttributeModifier.Operation.ADD_VALUE);
    private static final Component SPAWN_SET_MESSAGE = Component.translatable("block.minecraft.set_spawn");
    private static final AttributeModifier WAYPOINT_TRANSMIT_RANGE_CROUCH_MODIFIER = new AttributeModifier(ResourceLocation.withDefaultNamespace("waypoint_transmit_range_crouch"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    private static final boolean DEFAULT_SEEN_CREDITS = false;
    private static final boolean DEFAULT_SPAWN_EXTRA_PARTICLES_ON_FALL = false;
    public ServerGamePacketListenerImpl connection;
    private final MinecraftServer server;
    public final ServerPlayerGameMode gameMode;
    private final PlayerAdvancements advancements;
    private final ServerStatsCounter stats;
    private float lastRecordedHealthAndAbsorption = Float.MIN_VALUE;
    private int lastRecordedFoodLevel = Integer.MIN_VALUE;
    private int lastRecordedAirLevel = Integer.MIN_VALUE;
    private int lastRecordedArmor = Integer.MIN_VALUE;
    private int lastRecordedLevel = Integer.MIN_VALUE;
    private int lastRecordedExperience = Integer.MIN_VALUE;
    private float lastSentHealth = -1.0E8f;
    private int lastSentFood = -99999999;
    private boolean lastFoodSaturationZero = true;
    private int lastSentExp = -99999999;
    private ChatVisiblity chatVisibility = ChatVisiblity.FULL;
    private ParticleStatus particleStatus = ParticleStatus.ALL;
    private boolean canChatColor = true;
    private long lastActionTime = Util.getMillis();
    @Nullable
    private Entity camera;
    private boolean isChangingDimension;
    public boolean seenCredits = false;
    private final ServerRecipeBook recipeBook;
    @Nullable
    private Vec3 levitationStartPos;
    private int levitationStartTime;
    private boolean disconnected;
    private int requestedViewDistance = 2;
    private String language = "en_us";
    @Nullable
    private Vec3 startingToFallPosition;
    @Nullable
    private Vec3 enteredNetherPosition;
    @Nullable
    private Vec3 enteredLavaOnVehiclePosition;
    private SectionPos lastSectionPos = SectionPos.of(0, 0, 0);
    private ChunkTrackingView chunkTrackingView = ChunkTrackingView.EMPTY;
    @Nullable
    private RespawnConfig respawnConfig;
    private final TextFilter textFilter;
    private boolean textFilteringEnabled;
    private boolean allowsListing;
    private boolean spawnExtraParticlesOnFall = false;
    private WardenSpawnTracker wardenSpawnTracker = new WardenSpawnTracker();
    @Nullable
    private BlockPos raidOmenPosition;
    private Vec3 lastKnownClientMovement = Vec3.ZERO;
    private Input lastClientInput = Input.EMPTY;
    private final Set<ThrownEnderpearl> enderPearls = new HashSet<ThrownEnderpearl>();
    private final ContainerSynchronizer containerSynchronizer = new ContainerSynchronizer(){
        private final LoadingCache<TypedDataComponent<?>, Integer> cache = CacheBuilder.newBuilder().maximumSize(256L).build(new CacheLoader<TypedDataComponent<?>, Integer>(){
            private final DynamicOps<HashCode> registryHashOps;
            {
                this.registryHashOps = ServerPlayer.this.registryAccess().createSerializationContext(HashOps.CRC32C_INSTANCE);
            }

            @Override
            public Integer load(TypedDataComponent<?> $$0) {
                return ((HashCode)$$0.encodeValue(this.registryHashOps).getOrThrow($$1 -> new IllegalArgumentException("Failed to hash " + String.valueOf($$0) + ": " + $$1))).asInt();
            }

            @Override
            public /* synthetic */ Object load(Object object) throws Exception {
                return this.load((TypedDataComponent)((Object)object));
            }
        });

        @Override
        public void a(AbstractContainerMenu $$0, List<ItemStack> $$1, ItemStack $$2, int[] $$3) {
            ServerPlayer.this.connection.send(new ClientboundContainerSetContentPacket($$0.containerId, $$0.incrementStateId(), $$1, $$2));
            for (int $$4 = 0; $$4 < $$3.length; ++$$4) {
                this.broadcastDataValue($$0, $$4, $$3[$$4]);
            }
        }

        @Override
        public void sendSlotChange(AbstractContainerMenu $$0, int $$1, ItemStack $$2) {
            ServerPlayer.this.connection.send(new ClientboundContainerSetSlotPacket($$0.containerId, $$0.incrementStateId(), $$1, $$2));
        }

        @Override
        public void sendCarriedChange(AbstractContainerMenu $$0, ItemStack $$1) {
            ServerPlayer.this.connection.send(new ClientboundSetCursorItemPacket($$1));
        }

        @Override
        public void sendDataChange(AbstractContainerMenu $$0, int $$1, int $$2) {
            this.broadcastDataValue($$0, $$1, $$2);
        }

        private void broadcastDataValue(AbstractContainerMenu $$0, int $$1, int $$2) {
            ServerPlayer.this.connection.send(new ClientboundContainerSetDataPacket($$0.containerId, $$1, $$2));
        }

        @Override
        public RemoteSlot createSlot() {
            return new RemoteSlot.Synchronized(this.cache::getUnchecked);
        }
    };
    private final ContainerListener containerListener = new ContainerListener(){

        @Override
        public void slotChanged(AbstractContainerMenu $$0, int $$1, ItemStack $$2) {
            Slot $$3 = $$0.getSlot($$1);
            if ($$3 instanceof ResultSlot) {
                return;
            }
            if ($$3.container == ServerPlayer.this.getInventory()) {
                CriteriaTriggers.INVENTORY_CHANGED.trigger(ServerPlayer.this, ServerPlayer.this.getInventory(), $$2);
            }
        }

        @Override
        public void dataChanged(AbstractContainerMenu $$0, int $$1, int $$2) {
        }
    };
    @Nullable
    private RemoteChatSession chatSession;
    @Nullable
    public final Object object;
    private final CommandSource commandSource = new CommandSource(){

        @Override
        public boolean acceptsSuccess() {
            return ServerPlayer.this.level().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
        }

        @Override
        public boolean acceptsFailure() {
            return true;
        }

        @Override
        public boolean shouldInformAdmins() {
            return true;
        }

        @Override
        public void sendSystemMessage(Component $$0) {
            ServerPlayer.this.sendSystemMessage($$0);
        }
    };
    private int containerCounter;
    public boolean wonGame;

    public ServerPlayer(MinecraftServer $$0, ServerLevel $$12, GameProfile $$22, ClientInformation $$3) {
        super($$12, $$22);
        this.textFilter = $$0.createTextFilterForPlayer(this);
        this.gameMode = $$0.createGameModeForPlayer(this);
        this.recipeBook = new ServerRecipeBook(($$1, $$2) -> $$0.getRecipeManager().listDisplaysForRecipe($$1, $$2));
        this.server = $$0;
        this.stats = $$0.getPlayerList().getPlayerStats(this);
        this.advancements = $$0.getPlayerList().getPlayerAdvancements(this);
        this.updateOptions($$3);
        this.object = null;
    }

    @Override
    public BlockPos adjustSpawnLocation(ServerLevel $$0, BlockPos $$1) {
        AABB $$2 = this.getDimensions(Pose.STANDING).makeBoundingBox(Vec3.ZERO);
        BlockPos $$3 = $$1;
        if ($$0.dimensionType().hasSkyLight() && $$0.getServer().getWorldData().getGameType() != GameType.ADVENTURE) {
            long $$6;
            long $$7;
            int $$4 = Math.max(0, this.server.getSpawnRadius($$0));
            int $$5 = Mth.floor($$0.getWorldBorder().getDistanceToBorder($$1.getX(), $$1.getZ()));
            if ($$5 < $$4) {
                $$4 = $$5;
            }
            if ($$5 <= 1) {
                $$4 = 1;
            }
            int $$8 = ($$7 = ($$6 = (long)($$4 * 2 + 1)) * $$6) > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)$$7;
            int $$9 = this.getCoprime($$8);
            int $$10 = RandomSource.create().nextInt($$8);
            for (int $$11 = 0; $$11 < $$8; ++$$11) {
                int $$12 = ($$10 + $$9 * $$11) % $$8;
                int $$13 = $$12 % ($$4 * 2 + 1);
                int $$14 = $$12 / ($$4 * 2 + 1);
                int $$15 = $$1.getX() + $$13 - $$4;
                int $$16 = $$1.getZ() + $$14 - $$4;
                try {
                    $$3 = PlayerRespawnLogic.getOverworldRespawnPos($$0, $$15, $$16);
                    if ($$3 == null || !this.noCollisionNoLiquid($$0, $$2.move($$3.getBottomCenter()))) continue;
                    return $$3;
                } catch (Exception $$17) {
                    int $$18 = $$11;
                    int $$19 = $$4;
                    CrashReport $$20 = CrashReport.forThrowable($$17, "Searching for spawn");
                    CrashReportCategory $$21 = $$20.addCategory("Spawn Lookup");
                    $$21.setDetail("Origin", $$1::toString);
                    $$21.setDetail("Radius", () -> Integer.toString($$19));
                    $$21.setDetail("Candidate", () -> "[" + $$15 + "," + $$16 + "]");
                    $$21.setDetail("Progress", () -> $$18 + " out of " + $$8);
                    throw new ReportedException($$20);
                }
            }
            $$3 = $$1;
        }
        while (!this.noCollisionNoLiquid($$0, $$2.move($$3.getBottomCenter())) && $$3.getY() < $$0.getMaxY()) {
            $$3 = $$3.above();
        }
        while (this.noCollisionNoLiquid($$0, $$2.move($$3.below().getBottomCenter())) && $$3.getY() > $$0.getMinY() + 1) {
            $$3 = $$3.below();
        }
        return $$3;
    }

    private boolean noCollisionNoLiquid(ServerLevel $$0, AABB $$1) {
        return $$0.noCollision(this, $$1, true);
    }

    private int getCoprime(int $$0) {
        return $$0 <= 16 ? $$0 - 1 : 17;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput $$0) {
        super.readAdditionalSaveData($$0);
        this.wardenSpawnTracker = $$0.read("warden_spawn_tracker", WardenSpawnTracker.CODEC).orElseGet(WardenSpawnTracker::new);
        this.enteredNetherPosition = $$0.read("entered_nether_pos", Vec3.CODEC).orElse(null);
        this.seenCredits = $$0.getBooleanOr("seenCredits", false);
        $$0.read("recipeBook", ServerRecipeBook.Packed.CODEC).ifPresent($$02 -> this.recipeBook.loadUntrusted((ServerRecipeBook.Packed)((Object)$$02), $$0 -> this.server.getRecipeManager().byKey((ResourceKey<Recipe<?>>)$$0).isPresent()));
        if (this.isSleeping()) {
            this.stopSleeping();
        }
        this.respawnConfig = $$0.read("respawn", RespawnConfig.CODEC).orElse(null);
        this.spawnExtraParticlesOnFall = $$0.getBooleanOr("spawn_extra_particles_on_fall", false);
        this.raidOmenPosition = $$0.read("raid_omen_position", BlockPos.CODEC).orElse(null);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput $$0) {
        super.addAdditionalSaveData($$0);
        $$0.store("warden_spawn_tracker", WardenSpawnTracker.CODEC, this.wardenSpawnTracker);
        this.storeGameTypes($$0);
        $$0.putBoolean("seenCredits", this.seenCredits);
        $$0.storeNullable("entered_nether_pos", Vec3.CODEC, this.enteredNetherPosition);
        this.saveParentVehicle($$0);
        $$0.store("recipeBook", ServerRecipeBook.Packed.CODEC, this.recipeBook.pack());
        $$0.putString(TAG_DIMENSION, this.level().dimension().location().toString());
        $$0.storeNullable("respawn", RespawnConfig.CODEC, this.respawnConfig);
        $$0.putBoolean("spawn_extra_particles_on_fall", this.spawnExtraParticlesOnFall);
        $$0.storeNullable("raid_omen_position", BlockPos.CODEC, this.raidOmenPosition);
        this.saveEnderPearls($$0);
    }

    private void saveParentVehicle(ValueOutput $$0) {
        Entity $$1 = this.getRootVehicle();
        Entity $$2 = this.getVehicle();
        if ($$2 != null && $$1 != this && $$1.hasExactlyOnePlayerPassenger()) {
            ValueOutput $$3 = $$0.child("RootVehicle");
            $$3.store("Attach", UUIDUtil.CODEC, $$2.getUUID());
            $$1.save($$3.child("Entity"));
        }
    }

    public void loadAndSpawnParentVehicle(ValueInput $$0) {
        Optional<ValueInput> $$12 = $$0.child("RootVehicle");
        if ($$12.isEmpty()) {
            return;
        }
        ServerLevel $$2 = this.level();
        Entity $$3 = EntityType.loadEntityRecursive($$12.get().childOrEmpty("Entity"), (Level)$$2, EntitySpawnReason.LOAD, $$1 -> {
            if (!$$2.addWithUUID((Entity)$$1)) {
                return null;
            }
            return $$1;
        });
        if ($$3 == null) {
            return;
        }
        UUID $$4 = $$12.get().read("Attach", UUIDUtil.CODEC).orElse(null);
        if ($$3.getUUID().equals($$4)) {
            this.startRiding($$3, true);
        } else {
            for (Entity $$5 : $$3.getIndirectPassengers()) {
                if (!$$5.getUUID().equals($$4)) continue;
                this.startRiding($$5, true);
                break;
            }
        }
        if (!this.isPassenger()) {
            LOGGER.warn("Couldn't reattach entity to player");
            $$3.discard();
            for (Entity $$6 : $$3.getIndirectPassengers()) {
                $$6.discard();
            }
        }
    }

    private void saveEnderPearls(ValueOutput $$0) {
        if (!this.enderPearls.isEmpty()) {
            ValueOutput.ValueOutputList $$1 = $$0.childrenList(ENDER_PEARLS_TAG);
            for (ThrownEnderpearl $$2 : this.enderPearls) {
                if ($$2.isRemoved()) {
                    LOGGER.warn("Trying to save removed ender pearl, skipping");
                    continue;
                }
                ValueOutput $$3 = $$1.addChild();
                $$2.save($$3);
                $$3.store(ENDER_PEARL_DIMENSION_TAG, Level.RESOURCE_KEY_CODEC, $$2.level().dimension());
            }
        }
    }

    public void loadAndSpawnEnderPearls(ValueInput $$0) {
        $$0.childrenListOrEmpty(ENDER_PEARLS_TAG).forEach(this::loadAndSpawnEnderPearl);
    }

    private void loadAndSpawnEnderPearl(ValueInput $$0) {
        Optional<ResourceKey<Level>> $$12 = $$0.read(ENDER_PEARL_DIMENSION_TAG, Level.RESOURCE_KEY_CODEC);
        if ($$12.isEmpty()) {
            return;
        }
        ServerLevel $$2 = this.level().getServer().getLevel($$12.get());
        if ($$2 != null) {
            Entity $$3 = EntityType.loadEntityRecursive($$0, (Level)$$2, EntitySpawnReason.LOAD, $$1 -> {
                if (!$$2.addWithUUID((Entity)$$1)) {
                    return null;
                }
                return $$1;
            });
            if ($$3 != null) {
                ServerPlayer.placeEnderPearlTicket($$2, $$3.chunkPosition());
            } else {
                LOGGER.warn("Failed to spawn player ender pearl in level ({}), skipping", (Object)$$12.get());
            }
        } else {
            LOGGER.warn("Trying to load ender pearl without level ({}) being loaded, skipping", (Object)$$12.get());
        }
    }

    public void setExperiencePoints(int $$0) {
        float $$1 = this.getXpNeededForNextLevel();
        float $$2 = ($$1 - 1.0f) / $$1;
        this.experienceProgress = Mth.clamp((float)$$0 / $$1, 0.0f, $$2);
        this.lastSentExp = -1;
    }

    public void setExperienceLevels(int $$0) {
        this.experienceLevel = $$0;
        this.lastSentExp = -1;
    }

    @Override
    public void giveExperienceLevels(int $$0) {
        super.giveExperienceLevels($$0);
        this.lastSentExp = -1;
    }

    @Override
    public void onEnchantmentPerformed(ItemStack $$0, int $$1) {
        super.onEnchantmentPerformed($$0, $$1);
        this.lastSentExp = -1;
    }

    private void initMenu(AbstractContainerMenu $$0) {
        $$0.addSlotListener(this.containerListener);
        $$0.setSynchronizer(this.containerSynchronizer);
    }

    public void initInventoryMenu() {
        this.initMenu(this.inventoryMenu);
    }

    @Override
    public void onEnterCombat() {
        super.onEnterCombat();
        this.connection.send(ClientboundPlayerCombatEnterPacket.INSTANCE);
    }

    @Override
    public void onLeaveCombat() {
        super.onLeaveCombat();
        this.connection.send(new ClientboundPlayerCombatEndPacket(this.getCombatTracker()));
    }

    @Override
    public void onInsideBlock(BlockState $$0) {
        CriteriaTriggers.ENTER_BLOCK.trigger(this, $$0);
    }

    @Override
    protected ItemCooldowns createItemCooldowns() {
        return new ServerItemCooldowns(this);
    }

    @Override
    public void tick() {
        Entity $$0;
        this.tickClientLoadTimeout();
        this.gameMode.tick();
        this.wardenSpawnTracker.tick();
        if (this.invulnerableTime > 0) {
            --this.invulnerableTime;
        }
        this.containerMenu.broadcastChanges();
        if (!this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }
        if (($$0 = this.getCamera()) != this) {
            if ($$0.isAlive()) {
                this.absSnapTo($$0.getX(), $$0.getY(), $$0.getZ(), $$0.getYRot(), $$0.getXRot());
                this.level().getChunkSource().move(this);
                if (this.wantsToStopRiding()) {
                    this.setCamera(this);
                }
            } else {
                this.setCamera(this);
            }
        }
        CriteriaTriggers.TICK.trigger(this);
        if (this.levitationStartPos != null) {
            CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.tickCount - this.levitationStartTime);
        }
        this.trackStartFallingPosition();
        this.trackEnteredOrExitedLavaOnVehicle();
        this.updatePlayerAttributes();
        this.advancements.flushDirty(this, true);
    }

    private void updatePlayerAttributes() {
        AttributeInstance $$2;
        AttributeInstance $$1;
        AttributeInstance $$0 = this.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if ($$0 != null) {
            if (this.isCreative()) {
                $$0.addOrUpdateTransientModifier(CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER);
            } else {
                $$0.removeModifier(CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER);
            }
        }
        if (($$1 = this.getAttribute(Attributes.ENTITY_INTERACTION_RANGE)) != null) {
            if (this.isCreative()) {
                $$1.addOrUpdateTransientModifier(CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER);
            } else {
                $$1.removeModifier(CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER);
            }
        }
        if (($$2 = this.getAttribute(Attributes.WAYPOINT_TRANSMIT_RANGE)) != null) {
            if (this.isCrouching()) {
                $$2.addOrUpdateTransientModifier(WAYPOINT_TRANSMIT_RANGE_CROUCH_MODIFIER);
            } else {
                $$2.removeModifier(WAYPOINT_TRANSMIT_RANGE_CROUCH_MODIFIER);
            }
        }
    }

    public void doTick() {
        try {
            if (!this.isSpectator() || !this.touchingUnloadedChunk()) {
                super.tick();
            }
            for (int $$0 = 0; $$0 < this.getInventory().getContainerSize(); ++$$0) {
                ItemStack $$1 = this.getInventory().getItem($$0);
                if ($$1.isEmpty()) continue;
                this.synchronizeSpecialItemUpdates($$1);
            }
            if (this.getHealth() != this.lastSentHealth || this.lastSentFood != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0f != this.lastFoodSaturationZero) {
                this.connection.send(new ClientboundSetHealthPacket(this.getHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
                this.lastSentHealth = this.getHealth();
                this.lastSentFood = this.foodData.getFoodLevel();
                boolean bl = this.lastFoodSaturationZero = this.foodData.getSaturationLevel() == 0.0f;
            }
            if (this.getHealth() + this.getAbsorptionAmount() != this.lastRecordedHealthAndAbsorption) {
                this.lastRecordedHealthAndAbsorption = this.getHealth() + this.getAbsorptionAmount();
                this.updateScoreForCriteria(ObjectiveCriteria.HEALTH, Mth.ceil(this.lastRecordedHealthAndAbsorption));
            }
            if (this.foodData.getFoodLevel() != this.lastRecordedFoodLevel) {
                this.lastRecordedFoodLevel = this.foodData.getFoodLevel();
                this.updateScoreForCriteria(ObjectiveCriteria.FOOD, Mth.ceil(this.lastRecordedFoodLevel));
            }
            if (this.getAirSupply() != this.lastRecordedAirLevel) {
                this.lastRecordedAirLevel = this.getAirSupply();
                this.updateScoreForCriteria(ObjectiveCriteria.AIR, Mth.ceil(this.lastRecordedAirLevel));
            }
            if (this.getArmorValue() != this.lastRecordedArmor) {
                this.lastRecordedArmor = this.getArmorValue();
                this.updateScoreForCriteria(ObjectiveCriteria.ARMOR, Mth.ceil(this.lastRecordedArmor));
            }
            if (this.totalExperience != this.lastRecordedExperience) {
                this.lastRecordedExperience = this.totalExperience;
                this.updateScoreForCriteria(ObjectiveCriteria.EXPERIENCE, Mth.ceil(this.lastRecordedExperience));
            }
            if (this.experienceLevel != this.lastRecordedLevel) {
                this.lastRecordedLevel = this.experienceLevel;
                this.updateScoreForCriteria(ObjectiveCriteria.LEVEL, Mth.ceil(this.lastRecordedLevel));
            }
            if (this.totalExperience != this.lastSentExp) {
                this.lastSentExp = this.totalExperience;
                this.connection.send(new ClientboundSetExperiencePacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
            }
            if (this.tickCount % 20 == 0) {
                CriteriaTriggers.LOCATION.trigger(this);
            }
        } catch (Throwable $$2) {
            CrashReport $$3 = CrashReport.forThrowable($$2, "Ticking player");
            CrashReportCategory $$4 = $$3.addCategory("Player being ticked");
            this.fillCrashReportCategory($$4);
            throw new ReportedException($$3);
        }
    }

    private void synchronizeSpecialItemUpdates(ItemStack $$0) {
        Packet<?> $$3;
        MapId $$1 = $$0.get(DataComponents.MAP_ID);
        MapItemSavedData $$2 = MapItem.getSavedData($$1, (Level)this.level());
        if ($$2 != null && ($$3 = $$2.getUpdatePacket($$1, this)) != null) {
            this.connection.send($$3);
        }
    }

    @Override
    protected void tickRegeneration() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            if (this.tickCount % 20 == 0) {
                float $$0;
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(1.0f);
                }
                if (($$0 = this.foodData.getSaturationLevel()) < 20.0f) {
                    this.foodData.setSaturation($$0 + 1.0f);
                }
            }
            if (this.tickCount % 10 == 0 && this.foodData.needsFood()) {
                this.foodData.setFoodLevel(this.foodData.getFoodLevel() + 1);
            }
        }
    }

    @Override
    public void resetFallDistance() {
        if (this.getHealth() > 0.0f && this.startingToFallPosition != null) {
            CriteriaTriggers.FALL_FROM_HEIGHT.trigger(this, this.startingToFallPosition);
        }
        this.startingToFallPosition = null;
        super.resetFallDistance();
    }

    public void trackStartFallingPosition() {
        if (this.fallDistance > 0.0 && this.startingToFallPosition == null) {
            this.startingToFallPosition = this.position();
            if (this.currentImpulseImpactPos != null && this.currentImpulseImpactPos.y <= this.startingToFallPosition.y) {
                CriteriaTriggers.FALL_AFTER_EXPLOSION.trigger(this, this.currentImpulseImpactPos, this.currentExplosionCause);
            }
        }
    }

    public void trackEnteredOrExitedLavaOnVehicle() {
        if (this.getVehicle() != null && this.getVehicle().isInLava()) {
            if (this.enteredLavaOnVehiclePosition == null) {
                this.enteredLavaOnVehiclePosition = this.position();
            } else {
                CriteriaTriggers.RIDE_ENTITY_IN_LAVA_TRIGGER.trigger(this, this.enteredLavaOnVehiclePosition);
            }
        }
        if (!(this.enteredLavaOnVehiclePosition == null || this.getVehicle() != null && this.getVehicle().isInLava())) {
            this.enteredLavaOnVehiclePosition = null;
        }
    }

    private void updateScoreForCriteria(ObjectiveCriteria $$0, int $$12) {
        this.getScoreboard().forAllObjectives($$0, this, $$1 -> $$1.set($$12));
    }

    @Override
    public void die(DamageSource $$0) {
        this.gameEvent(GameEvent.ENTITY_DIE);
        boolean $$1 = this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
        if ($$1) {
            Component $$2 = this.getCombatTracker().getDeathMessage();
            this.connection.send(new ClientboundPlayerCombatKillPacket(this.getId(), $$2), PacketSendListener.exceptionallySend(() -> {
                int $$12 = 256;
                String $$2 = $$2.getString(256);
                MutableComponent $$3 = Component.a("death.attack.message_too_long", Component.literal($$2).withStyle(ChatFormatting.YELLOW));
                MutableComponent $$4 = Component.a("death.attack.even_more_magic", this.getDisplayName()).withStyle($$1 -> $$1.withHoverEvent(new HoverEvent.ShowText($$3)));
                return new ClientboundPlayerCombatKillPacket(this.getId(), $$4);
            }));
            PlayerTeam $$3 = this.getTeam();
            if ($$3 == null || ((Team)$$3).getDeathMessageVisibility() == Team.Visibility.ALWAYS) {
                this.server.getPlayerList().broadcastSystemMessage($$2, false);
            } else if (((Team)$$3).getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
                this.server.getPlayerList().broadcastSystemToTeam(this, $$2);
            } else if (((Team)$$3).getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
                this.server.getPlayerList().broadcastSystemToAllExceptTeam(this, $$2);
            }
        } else {
            this.connection.send(new ClientboundPlayerCombatKillPacket(this.getId(), CommonComponents.EMPTY));
        }
        this.removeEntitiesOnShoulder();
        if (this.level().getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            this.tellNeutralMobsThatIDied();
        }
        if (!this.isSpectator()) {
            this.dropAllDeathLoot(this.level(), $$0);
        }
        this.getScoreboard().forAllObjectives(ObjectiveCriteria.DEATH_COUNT, this, ScoreAccess::increment);
        LivingEntity $$4 = this.getKillCredit();
        if ($$4 != null) {
            this.awardStat(Stats.ENTITY_KILLED_BY.get($$4.getType()));
            $$4.awardKillScore(this, $$0);
            this.createWitherRose($$4);
        }
        this.level().broadcastEntityEvent(this, (byte)3);
        this.awardStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        this.clearFire();
        this.setTicksFrozen(0);
        this.setSharedFlagOnFire(false);
        this.getCombatTracker().recheckStatus();
        this.setLastDeathLocation(Optional.of(GlobalPos.of(this.level().dimension(), this.blockPosition())));
        this.setClientLoaded(false);
    }

    private void tellNeutralMobsThatIDied() {
        AABB $$02 = new AABB(this.blockPosition()).inflate(32.0, 10.0, 32.0);
        this.level().getEntitiesOfClass(Mob.class, $$02, EntitySelector.NO_SPECTATORS).stream().filter($$0 -> $$0 instanceof NeutralMob).forEach($$0 -> ((NeutralMob)((Object)$$0)).playerDied(this.level(), this));
    }

    @Override
    public void awardKillScore(Entity $$0, DamageSource $$1) {
        if ($$0 == this) {
            return;
        }
        super.awardKillScore($$0, $$1);
        this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_ALL, this, ScoreAccess::increment);
        if ($$0 instanceof Player) {
            this.awardStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_PLAYERS, this, ScoreAccess::increment);
        } else {
            this.awardStat(Stats.MOB_KILLS);
        }
        this.a(this, $$0, ObjectiveCriteria.TEAM_KILL);
        this.a($$0, this, ObjectiveCriteria.KILLED_BY_TEAM);
        CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, $$0, $$1);
    }

    private void a(ScoreHolder $$0, ScoreHolder $$1, ObjectiveCriteria[] $$2) {
        int $$4;
        PlayerTeam $$3 = this.getScoreboard().getPlayersTeam($$1.getScoreboardName());
        if ($$3 != null && ($$4 = $$3.getColor().getId()) >= 0 && $$4 < $$2.length) {
            this.getScoreboard().forAllObjectives($$2[$$4], $$0, ScoreAccess::increment);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel $$0, DamageSource $$1, float $$2) {
        Player $$7;
        AbstractArrow $$5;
        Entity $$6;
        Player $$4;
        if (this.isInvulnerableTo($$0, $$1)) {
            return false;
        }
        Entity $$3 = $$1.getEntity();
        if ($$3 instanceof Player && !this.canHarmPlayer($$4 = (Player)$$3)) {
            return false;
        }
        if ($$3 instanceof AbstractArrow && ($$6 = ($$5 = (AbstractArrow)$$3).getOwner()) instanceof Player && !this.canHarmPlayer($$7 = (Player)$$6)) {
            return false;
        }
        return super.hurtServer($$0, $$1, $$2);
    }

    @Override
    public boolean canHarmPlayer(Player $$0) {
        if (!this.isPvpAllowed()) {
            return false;
        }
        return super.canHarmPlayer($$0);
    }

    private boolean isPvpAllowed() {
        return this.server.isPvpAllowed();
    }

    public TeleportTransition findRespawnPositionAndUseSpawnBlock(boolean $$0, TeleportTransition.PostTeleportTransition $$1) {
        RespawnConfig $$2 = this.getRespawnConfig();
        ServerLevel $$3 = this.server.getLevel(RespawnConfig.getDimensionOrDefault($$2));
        if ($$3 != null && $$2 != null) {
            Optional<RespawnPosAngle> $$4 = ServerPlayer.findRespawnAndUseSpawnBlock($$3, $$2, $$0);
            if ($$4.isPresent()) {
                RespawnPosAngle $$5 = $$4.get();
                return new TeleportTransition($$3, $$5.position(), Vec3.ZERO, $$5.yaw(), 0.0f, $$1);
            }
            return TeleportTransition.missingRespawnBlock(this.server.overworld(), this, $$1);
        }
        return new TeleportTransition(this.server.overworld(), this, $$1);
    }

    public boolean isReceivingWaypoints() {
        return this.getAttributeValue(Attributes.WAYPOINT_RECEIVE_RANGE) > 0.0;
    }

    @Override
    protected void onAttributeUpdated(Holder<Attribute> $$0) {
        if ($$0.is(Attributes.WAYPOINT_RECEIVE_RANGE)) {
            ServerWaypointManager $$1 = this.level().getWaypointManager();
            if (this.getAttributes().getValue($$0) > 0.0) {
                $$1.addPlayer(this);
            } else {
                $$1.removePlayer(this);
            }
        }
        super.onAttributeUpdated($$0);
    }

    private static Optional<RespawnPosAngle> findRespawnAndUseSpawnBlock(ServerLevel $$0, RespawnConfig $$12, boolean $$2) {
        BlockPos $$3 = $$12.pos;
        float $$4 = $$12.angle;
        boolean $$5 = $$12.forced;
        BlockState $$6 = $$0.getBlockState($$3);
        Block $$7 = $$6.getBlock();
        if ($$7 instanceof RespawnAnchorBlock && ($$5 || $$6.getValue(RespawnAnchorBlock.CHARGE) > 0) && RespawnAnchorBlock.canSetSpawn($$0)) {
            Optional<Vec3> $$8 = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, $$0, $$3);
            if (!$$5 && $$2 && $$8.isPresent()) {
                $$0.setBlock($$3, (BlockState)$$6.setValue(RespawnAnchorBlock.CHARGE, $$6.getValue(RespawnAnchorBlock.CHARGE) - 1), 3);
            }
            return $$8.map($$1 -> RespawnPosAngle.of($$1, $$3));
        }
        if ($$7 instanceof BedBlock && BedBlock.canSetSpawn($$0)) {
            return BedBlock.findStandUpPosition(EntityType.PLAYER, $$0, $$3, (Direction)$$6.getValue(BedBlock.FACING), $$4).map($$1 -> RespawnPosAngle.of($$1, $$3));
        }
        if (!$$5) {
            return Optional.empty();
        }
        boolean $$9 = $$7.isPossibleToRespawnInThis($$6);
        BlockState $$10 = $$0.getBlockState($$3.above());
        boolean $$11 = $$10.getBlock().isPossibleToRespawnInThis($$10);
        if ($$9 && $$11) {
            return Optional.of(new RespawnPosAngle(new Vec3((double)$$3.getX() + 0.5, (double)$$3.getY() + 0.1, (double)$$3.getZ() + 0.5), $$4));
        }
        return Optional.empty();
    }

    public void showEndCredits() {
        this.unRide();
        this.level().removePlayerImmediately(this, Entity.RemovalReason.CHANGED_DIMENSION);
        if (!this.wonGame) {
            this.wonGame = true;
            this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0.0f));
            this.seenCredits = true;
        }
    }

    @Override
    @Nullable
    public ServerPlayer teleport(TeleportTransition $$0) {
        if (this.isRemoved()) {
            return null;
        }
        if ($$0.missingRespawnBlock()) {
            this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0f));
        }
        ServerLevel $$1 = $$0.newLevel();
        ServerLevel $$2 = this.level();
        ResourceKey<Level> $$3 = $$2.dimension();
        if (!$$0.asPassenger()) {
            this.removeVehicle();
        }
        if ($$1.dimension() == $$3) {
            this.connection.teleport(PositionMoveRotation.of($$0), $$0.relatives());
            this.connection.resetPosition();
            $$0.postTeleportTransition().onTransition(this);
            return this;
        }
        this.isChangingDimension = true;
        LevelData $$4 = $$1.getLevelData();
        this.connection.send(new ClientboundRespawnPacket(this.createCommonSpawnInfo($$1), 3));
        this.connection.send(new ClientboundChangeDifficultyPacket($$4.getDifficulty(), $$4.isDifficultyLocked()));
        PlayerList $$5 = this.server.getPlayerList();
        $$5.sendPlayerPermissionLevel(this);
        $$2.removePlayerImmediately(this, Entity.RemovalReason.CHANGED_DIMENSION);
        this.unsetRemoved();
        ProfilerFiller $$6 = Profiler.get();
        $$6.push("moving");
        if ($$3 == Level.OVERWORLD && $$1.dimension() == Level.NETHER) {
            this.enteredNetherPosition = this.position();
        }
        $$6.pop();
        $$6.push("placing");
        this.setServerLevel($$1);
        this.connection.teleport(PositionMoveRotation.of($$0), $$0.relatives());
        this.connection.resetPosition();
        $$1.addDuringTeleport(this);
        $$6.pop();
        this.triggerDimensionChangeTriggers($$2);
        this.stopUsingItem();
        this.connection.send(new ClientboundPlayerAbilitiesPacket(this.getAbilities()));
        $$5.sendLevelInfo(this, $$1);
        $$5.sendAllPlayerInfo(this);
        $$5.sendActivePlayerEffects(this);
        $$0.postTeleportTransition().onTransition(this);
        this.lastSentExp = -1;
        this.lastSentHealth = -1.0f;
        this.lastSentFood = -1;
        this.teleportSpectators($$0, $$2);
        return this;
    }

    @Override
    public void forceSetRotation(float $$0, float $$1) {
        this.connection.send(new ClientboundPlayerRotationPacket($$0, $$1));
    }

    private void triggerDimensionChangeTriggers(ServerLevel $$0) {
        ResourceKey<Level> $$1 = $$0.dimension();
        ResourceKey<Level> $$2 = this.level().dimension();
        CriteriaTriggers.CHANGED_DIMENSION.trigger(this, $$1, $$2);
        if ($$1 == Level.NETHER && $$2 == Level.OVERWORLD && this.enteredNetherPosition != null) {
            CriteriaTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
        }
        if ($$2 != Level.NETHER) {
            this.enteredNetherPosition = null;
        }
    }

    @Override
    public boolean broadcastToPlayer(ServerPlayer $$0) {
        if ($$0.isSpectator()) {
            return this.getCamera() == this;
        }
        if (this.isSpectator()) {
            return false;
        }
        return super.broadcastToPlayer($$0);
    }

    @Override
    public void take(Entity $$0, int $$1) {
        super.take($$0, $$1);
        this.containerMenu.broadcastChanges();
    }

    @Override
    public Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos $$02) {
        Direction $$1 = this.level().getBlockState($$02).getValue(HorizontalDirectionalBlock.FACING);
        if (this.isSleeping() || !this.isAlive()) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.OTHER_PROBLEM));
        }
        if (!this.level().dimensionType().natural()) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.NOT_POSSIBLE_HERE));
        }
        if (!this.bedInRange($$02, $$1)) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.TOO_FAR_AWAY));
        }
        if (this.bedBlocked($$02, $$1)) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.OBSTRUCTED));
        }
        this.setRespawnPosition(new RespawnConfig(this.level().dimension(), $$02, this.getYRot(), false), true);
        if (this.level().isBrightOutside()) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.NOT_POSSIBLE_NOW));
        }
        if (!this.isCreative()) {
            double $$2 = 8.0;
            double $$3 = 5.0;
            Vec3 $$4 = Vec3.atBottomCenterOf($$02);
            List<Monster> $$5 = this.level().getEntitiesOfClass(Monster.class, new AABB($$4.x() - 8.0, $$4.y() - 5.0, $$4.z() - 8.0, $$4.x() + 8.0, $$4.y() + 5.0, $$4.z() + 8.0), $$0 -> $$0.isPreventingPlayerRest(this.level(), this));
            if (!$$5.isEmpty()) {
                return Either.left((Object)((Object)Player.BedSleepingProblem.NOT_SAFE));
            }
        }
        Either $$6 = super.startSleepInBed($$02).ifRight($$0 -> {
            this.awardStat(Stats.SLEEP_IN_BED);
            CriteriaTriggers.SLEPT_IN_BED.trigger(this);
        });
        if (!this.level().canSleepThroughNights()) {
            this.displayClientMessage(Component.translatable("sleep.not_possible"), true);
        }
        this.level().updateSleepingPlayerList();
        return $$6;
    }

    @Override
    public void startSleeping(BlockPos $$0) {
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        super.startSleeping($$0);
    }

    private boolean bedInRange(BlockPos $$0, Direction $$1) {
        return this.isReachableBedBlock($$0) || this.isReachableBedBlock($$0.relative($$1.getOpposite()));
    }

    private boolean isReachableBedBlock(BlockPos $$0) {
        Vec3 $$1 = Vec3.atBottomCenterOf($$0);
        return Math.abs(this.getX() - $$1.x()) <= 3.0 && Math.abs(this.getY() - $$1.y()) <= 2.0 && Math.abs(this.getZ() - $$1.z()) <= 3.0;
    }

    private boolean bedBlocked(BlockPos $$0, Direction $$1) {
        BlockPos $$2 = $$0.above();
        return !this.freeAt($$2) || !this.freeAt($$2.relative($$1.getOpposite()));
    }

    @Override
    public void stopSleepInBed(boolean $$0, boolean $$1) {
        if (this.isSleeping()) {
            this.level().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(this, 2));
        }
        super.stopSleepInBed($$0, $$1);
        if (this.connection != null) {
            this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel $$0, DamageSource $$1) {
        return super.isInvulnerableTo($$0, $$1) || this.isChangingDimension() && !$$1.is(DamageTypes.ENDER_PEARL) || !this.hasClientLoaded();
    }

    @Override
    protected void onChangedBlock(ServerLevel $$0, BlockPos $$1) {
        if (!this.isSpectator()) {
            super.onChangedBlock($$0, $$1);
        }
    }

    @Override
    protected void checkFallDamage(double $$0, boolean $$1, BlockState $$2, BlockPos $$3) {
        if (this.spawnExtraParticlesOnFall && $$1 && this.fallDistance > 0.0) {
            Vec3 $$4 = $$3.getCenter().add(0.0, 0.5, 0.0);
            int $$5 = (int)Mth.clamp(50.0 * this.fallDistance, 0.0, 200.0);
            this.level().sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, $$2), $$4.x, $$4.y, $$4.z, $$5, 0.3f, 0.3f, 0.3f, 0.15f);
            this.spawnExtraParticlesOnFall = false;
        }
        super.checkFallDamage($$0, $$1, $$2, $$3);
    }

    @Override
    public void onExplosionHit(@Nullable Entity $$0) {
        super.onExplosionHit($$0);
        this.currentImpulseImpactPos = this.position();
        this.currentExplosionCause = $$0;
        this.setIgnoreFallDamageFromCurrentImpulse($$0 != null && $$0.getType() == EntityType.WIND_CHARGE);
    }

    @Override
    protected void pushEntities() {
        if (this.level().tickRateManager().runsNormally()) {
            super.pushEntities();
        }
    }

    @Override
    public void openTextEdit(SignBlockEntity $$0, boolean $$1) {
        this.connection.send(new ClientboundBlockUpdatePacket(this.level(), $$0.getBlockPos()));
        this.connection.send(new ClientboundOpenSignEditorPacket($$0.getBlockPos(), $$1));
    }

    @Override
    public void openDialog(Holder<Dialog> $$0) {
        this.connection.send(new ClientboundShowDialogPacket($$0));
    }

    private void nextContainerCounter() {
        this.containerCounter = this.containerCounter % 100 + 1;
    }

    @Override
    public OptionalInt openMenu(@Nullable MenuProvider $$0) {
        if ($$0 == null) {
            return OptionalInt.empty();
        }
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        AbstractContainerMenu $$1 = $$0.createMenu(this.containerCounter, this.getInventory(), this);
        if ($$1 == null) {
            if (this.isSpectator()) {
                this.displayClientMessage(Component.translatable("container.spectatorCantOpen").withStyle(ChatFormatting.RED), true);
            }
            return OptionalInt.empty();
        }
        this.connection.send(new ClientboundOpenScreenPacket($$1.containerId, $$1.getType(), $$0.getDisplayName()));
        this.initMenu($$1);
        this.containerMenu = $$1;
        return OptionalInt.of(this.containerCounter);
    }

    @Override
    public void sendMerchantOffers(int $$0, MerchantOffers $$1, int $$2, int $$3, boolean $$4, boolean $$5) {
        this.connection.send(new ClientboundMerchantOffersPacket($$0, $$1, $$2, $$3, $$4, $$5));
    }

    @Override
    public void openHorseInventory(AbstractHorse $$0, Container $$1) {
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        int $$2 = $$0.getInventoryColumns();
        this.connection.send(new ClientboundHorseScreenOpenPacket(this.containerCounter, $$2, $$0.getId()));
        this.containerMenu = new HorseInventoryMenu(this.containerCounter, this.getInventory(), $$1, $$0, $$2);
        this.initMenu(this.containerMenu);
    }

    @Override
    public void openItemGui(ItemStack $$0, InteractionHand $$1) {
        if ($$0.has(DataComponents.WRITTEN_BOOK_CONTENT)) {
            if (WrittenBookContent.resolveForItem($$0, this.createCommandSourceStack(), this)) {
                this.containerMenu.broadcastChanges();
            }
            this.connection.send(new ClientboundOpenBookPacket($$1));
        }
    }

    @Override
    public void openCommandBlock(CommandBlockEntity $$0) {
        this.connection.send(ClientboundBlockEntityDataPacket.create($$0, BlockEntity::saveCustomOnly));
    }

    @Override
    public void closeContainer() {
        this.connection.send(new ClientboundContainerClosePacket(this.containerMenu.containerId));
        this.doCloseContainer();
    }

    @Override
    public void doCloseContainer() {
        this.containerMenu.removed(this);
        this.inventoryMenu.transferState(this.containerMenu);
        this.containerMenu = this.inventoryMenu;
    }

    @Override
    public void rideTick() {
        double $$0 = this.getX();
        double $$1 = this.getY();
        double $$2 = this.getZ();
        super.rideTick();
        this.checkRidingStatistics(this.getX() - $$0, this.getY() - $$1, this.getZ() - $$2);
    }

    public void checkMovementStatistics(double $$0, double $$1, double $$2) {
        if (this.isPassenger() || ServerPlayer.didNotMove($$0, $$1, $$2)) {
            return;
        }
        if (this.isSwimming()) {
            int $$3 = Math.round((float)Math.sqrt($$0 * $$0 + $$1 * $$1 + $$2 * $$2) * 100.0f);
            if ($$3 > 0) {
                this.awardStat(Stats.SWIM_ONE_CM, $$3);
                this.causeFoodExhaustion(0.01f * (float)$$3 * 0.01f);
            }
        } else if (this.isEyeInFluid(FluidTags.WATER)) {
            int $$4 = Math.round((float)Math.sqrt($$0 * $$0 + $$1 * $$1 + $$2 * $$2) * 100.0f);
            if ($$4 > 0) {
                this.awardStat(Stats.WALK_UNDER_WATER_ONE_CM, $$4);
                this.causeFoodExhaustion(0.01f * (float)$$4 * 0.01f);
            }
        } else if (this.isInWater()) {
            int $$5 = Math.round((float)Math.sqrt($$0 * $$0 + $$2 * $$2) * 100.0f);
            if ($$5 > 0) {
                this.awardStat(Stats.WALK_ON_WATER_ONE_CM, $$5);
                this.causeFoodExhaustion(0.01f * (float)$$5 * 0.01f);
            }
        } else if (this.onClimbable()) {
            if ($$1 > 0.0) {
                this.awardStat(Stats.CLIMB_ONE_CM, (int)Math.round($$1 * 100.0));
            }
        } else if (this.onGround()) {
            int $$6 = Math.round((float)Math.sqrt($$0 * $$0 + $$2 * $$2) * 100.0f);
            if ($$6 > 0) {
                if (this.isSprinting()) {
                    this.awardStat(Stats.SPRINT_ONE_CM, $$6);
                    this.causeFoodExhaustion(0.1f * (float)$$6 * 0.01f);
                } else if (this.isCrouching()) {
                    this.awardStat(Stats.CROUCH_ONE_CM, $$6);
                    this.causeFoodExhaustion(0.0f * (float)$$6 * 0.01f);
                } else {
                    this.awardStat(Stats.WALK_ONE_CM, $$6);
                    this.causeFoodExhaustion(0.0f * (float)$$6 * 0.01f);
                }
            }
        } else if (this.isFallFlying()) {
            int $$7 = Math.round((float)Math.sqrt($$0 * $$0 + $$1 * $$1 + $$2 * $$2) * 100.0f);
            this.awardStat(Stats.AVIATE_ONE_CM, $$7);
        } else {
            int $$8 = Math.round((float)Math.sqrt($$0 * $$0 + $$2 * $$2) * 100.0f);
            if ($$8 > 25) {
                this.awardStat(Stats.FLY_ONE_CM, $$8);
            }
        }
    }

    private void checkRidingStatistics(double $$0, double $$1, double $$2) {
        if (!this.isPassenger() || ServerPlayer.didNotMove($$0, $$1, $$2)) {
            return;
        }
        int $$3 = Math.round((float)Math.sqrt($$0 * $$0 + $$1 * $$1 + $$2 * $$2) * 100.0f);
        Entity $$4 = this.getVehicle();
        if ($$4 instanceof AbstractMinecart) {
            this.awardStat(Stats.MINECART_ONE_CM, $$3);
        } else if ($$4 instanceof AbstractBoat) {
            this.awardStat(Stats.BOAT_ONE_CM, $$3);
        } else if ($$4 instanceof Pig) {
            this.awardStat(Stats.PIG_ONE_CM, $$3);
        } else if ($$4 instanceof AbstractHorse) {
            this.awardStat(Stats.HORSE_ONE_CM, $$3);
        } else if ($$4 instanceof Strider) {
            this.awardStat(Stats.STRIDER_ONE_CM, $$3);
        } else if ($$4 instanceof HappyGhast) {
            this.awardStat(Stats.HAPPY_GHAST_ONE_CM, $$3);
        }
    }

    private static boolean didNotMove(double $$0, double $$1, double $$2) {
        return $$0 == 0.0 && $$1 == 0.0 && $$2 == 0.0;
    }

    @Override
    public void awardStat(Stat<?> $$0, int $$12) {
        this.stats.increment(this, $$0, $$12);
        this.getScoreboard().forAllObjectives($$0, this, $$1 -> $$1.add($$12));
    }

    @Override
    public void resetStat(Stat<?> $$0) {
        this.stats.setValue(this, $$0, 0);
        this.getScoreboard().forAllObjectives($$0, this, ScoreAccess::reset);
    }

    @Override
    public int awardRecipes(Collection<RecipeHolder<?>> $$0) {
        return this.recipeBook.addRecipes($$0, this);
    }

    @Override
    public void triggerRecipeCrafted(RecipeHolder<?> $$0, List<ItemStack> $$1) {
        CriteriaTriggers.RECIPE_CRAFTED.trigger(this, $$0.id(), $$1);
    }

    @Override
    public void awardRecipesByKey(List<ResourceKey<Recipe<?>>> $$02) {
        List<RecipeHolder<?>> $$1 = $$02.stream().flatMap($$0 -> this.server.getRecipeManager().byKey((ResourceKey<Recipe<?>>)$$0).stream()).collect(Collectors.toList());
        this.awardRecipes($$1);
    }

    @Override
    public int resetRecipes(Collection<RecipeHolder<?>> $$0) {
        return this.recipeBook.removeRecipes($$0, this);
    }

    @Override
    public void jumpFromGround() {
        super.jumpFromGround();
        this.awardStat(Stats.JUMP);
        if (this.isSprinting()) {
            this.causeFoodExhaustion(0.2f);
        } else {
            this.causeFoodExhaustion(0.05f);
        }
    }

    @Override
    public void giveExperiencePoints(int $$0) {
        super.giveExperiencePoints($$0);
        this.lastSentExp = -1;
    }

    public void disconnect() {
        this.disconnected = true;
        this.ejectPassengers();
        if (this.isSleeping()) {
            this.stopSleepInBed(true, false);
        }
    }

    public boolean hasDisconnected() {
        return this.disconnected;
    }

    public void resetSentInfo() {
        this.lastSentHealth = -1.0E8f;
    }

    @Override
    public void displayClientMessage(Component $$0, boolean $$1) {
        this.sendSystemMessage($$0, $$1);
    }

    @Override
    protected void completeUsingItem() {
        if (!this.useItem.isEmpty() && this.isUsingItem()) {
            this.connection.send(new ClientboundEntityEventPacket(this, 9));
            super.completeUsingItem();
        }
    }

    @Override
    public void lookAt(EntityAnchorArgument.Anchor $$0, Vec3 $$1) {
        super.lookAt($$0, $$1);
        this.connection.send(new ClientboundPlayerLookAtPacket($$0, $$1.x, $$1.y, $$1.z));
    }

    public void lookAt(EntityAnchorArgument.Anchor $$0, Entity $$1, EntityAnchorArgument.Anchor $$2) {
        Vec3 $$3 = $$2.apply($$1);
        super.lookAt($$0, $$3);
        this.connection.send(new ClientboundPlayerLookAtPacket($$0, $$1, $$2));
    }

    public void restoreFrom(ServerPlayer $$0, boolean $$1) {
        this.wardenSpawnTracker = $$0.wardenSpawnTracker;
        this.chatSession = $$0.chatSession;
        this.gameMode.setGameModeForPlayer($$0.gameMode.getGameModeForPlayer(), $$0.gameMode.getPreviousGameModeForPlayer());
        this.onUpdateAbilities();
        if ($$1) {
            this.getAttributes().assignBaseValues($$0.getAttributes());
            this.getAttributes().assignPermanentModifiers($$0.getAttributes());
            this.setHealth($$0.getHealth());
            this.foodData = $$0.foodData;
            for (MobEffectInstance $$2 : $$0.getActiveEffects()) {
                this.addEffect(new MobEffectInstance($$2));
            }
            this.getInventory().replaceWith($$0.getInventory());
            this.experienceLevel = $$0.experienceLevel;
            this.totalExperience = $$0.totalExperience;
            this.experienceProgress = $$0.experienceProgress;
            this.setScore($$0.getScore());
            this.portalProcess = $$0.portalProcess;
        } else {
            this.getAttributes().assignBaseValues($$0.getAttributes());
            this.setHealth(this.getMaxHealth());
            if (this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || $$0.isSpectator()) {
                this.getInventory().replaceWith($$0.getInventory());
                this.experienceLevel = $$0.experienceLevel;
                this.totalExperience = $$0.totalExperience;
                this.experienceProgress = $$0.experienceProgress;
                this.setScore($$0.getScore());
            }
        }
        this.enchantmentSeed = $$0.enchantmentSeed;
        this.enderChestInventory = $$0.enderChestInventory;
        this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (Byte)$$0.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION));
        this.lastSentExp = -1;
        this.lastSentHealth = -1.0f;
        this.lastSentFood = -1;
        this.recipeBook.copyOverData($$0.recipeBook);
        this.seenCredits = $$0.seenCredits;
        this.enteredNetherPosition = $$0.enteredNetherPosition;
        this.chunkTrackingView = $$0.chunkTrackingView;
        this.setShoulderEntityLeft($$0.getShoulderEntityLeft());
        this.setShoulderEntityRight($$0.getShoulderEntityRight());
        this.setLastDeathLocation($$0.getLastDeathLocation());
    }

    @Override
    protected void onEffectAdded(MobEffectInstance $$0, @Nullable Entity $$1) {
        super.onEffectAdded($$0, $$1);
        this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), $$0, true));
        if ($$0.is(MobEffects.LEVITATION)) {
            this.levitationStartTime = this.tickCount;
            this.levitationStartPos = this.position();
        }
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this, $$1);
    }

    @Override
    protected void onEffectUpdated(MobEffectInstance $$0, boolean $$1, @Nullable Entity $$2) {
        super.onEffectUpdated($$0, $$1, $$2);
        this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), $$0, false));
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this, $$2);
    }

    @Override
    protected void onEffectsRemoved(Collection<MobEffectInstance> $$0) {
        super.onEffectsRemoved($$0);
        for (MobEffectInstance $$1 : $$0) {
            this.connection.send(new ClientboundRemoveMobEffectPacket(this.getId(), $$1.getEffect()));
            if (!$$1.is(MobEffects.LEVITATION)) continue;
            this.levitationStartPos = null;
        }
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this, (Entity)null);
    }

    @Override
    public void teleportTo(double $$0, double $$1, double $$2) {
        this.connection.teleport(new PositionMoveRotation(new Vec3($$0, $$1, $$2), Vec3.ZERO, 0.0f, 0.0f), Relative.a(Relative.DELTA, Relative.ROTATION));
    }

    @Override
    public void teleportRelative(double $$0, double $$1, double $$2) {
        this.connection.teleport(new PositionMoveRotation(new Vec3($$0, $$1, $$2), Vec3.ZERO, 0.0f, 0.0f), Relative.ALL);
    }

    @Override
    public boolean teleportTo(ServerLevel $$0, double $$1, double $$2, double $$3, Set<Relative> $$4, float $$5, float $$6, boolean $$7) {
        boolean $$8;
        if (this.isSleeping()) {
            this.stopSleepInBed(true, true);
        }
        if ($$7) {
            this.setCamera(this);
        }
        if ($$8 = super.teleportTo($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7)) {
            this.setYHeadRot($$4.contains((Object)Relative.Y_ROT) ? this.getYHeadRot() + $$5 : $$5);
        }
        return $$8;
    }

    @Override
    public void snapTo(double $$0, double $$1, double $$2) {
        super.snapTo($$0, $$1, $$2);
        this.connection.resetPosition();
    }

    @Override
    public void crit(Entity $$0) {
        this.level().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket($$0, 4));
    }

    @Override
    public void magicCrit(Entity $$0) {
        this.level().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket($$0, 5));
    }

    @Override
    public void onUpdateAbilities() {
        if (this.connection == null) {
            return;
        }
        this.connection.send(new ClientboundPlayerAbilitiesPacket(this.getAbilities()));
        this.updateInvisibilityStatus();
    }

    @Override
    public ServerLevel level() {
        return (ServerLevel)super.level();
    }

    public boolean setGameMode(GameType $$0) {
        boolean $$1 = this.isSpectator();
        if (!this.gameMode.changeGameModeForPlayer($$0)) {
            return false;
        }
        this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, $$0.getId()));
        if ($$0 == GameType.SPECTATOR) {
            this.removeEntitiesOnShoulder();
            this.stopRiding();
            EnchantmentHelper.stopLocationBasedEffects(this);
        } else {
            this.setCamera(this);
            if ($$1) {
                EnchantmentHelper.runLocationChangedEffects(this.level(), this);
            }
        }
        this.onUpdateAbilities();
        this.updateEffectVisibility();
        return true;
    }

    @Override
    @Nonnull
    public GameType gameMode() {
        return this.gameMode.getGameModeForPlayer();
    }

    public CommandSource commandSource() {
        return this.commandSource;
    }

    public CommandSourceStack createCommandSourceStack() {
        return new CommandSourceStack(this.commandSource(), this.position(), this.getRotationVector(), this.level(), this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.server, this);
    }

    public void sendSystemMessage(Component $$0) {
        this.sendSystemMessage($$0, false);
    }

    public void sendSystemMessage(Component $$0, boolean $$1) {
        if (!this.acceptsSystemMessages($$1)) {
            return;
        }
        this.connection.send(new ClientboundSystemChatPacket($$0, $$1), PacketSendListener.exceptionallySend(() -> {
            if (this.acceptsSystemMessages(false)) {
                int $$1 = 256;
                String $$2 = $$0.getString(256);
                MutableComponent $$3 = Component.literal($$2).withStyle(ChatFormatting.YELLOW);
                return new ClientboundSystemChatPacket(Component.a("multiplayer.message_not_delivered", $$3).withStyle(ChatFormatting.RED), false);
            }
            return null;
        }));
    }

    public void sendChatMessage(OutgoingChatMessage $$0, boolean $$1, ChatType.Bound $$2) {
        if (this.acceptsChatMessages()) {
            $$0.sendToPlayer(this, $$1, $$2);
        }
    }

    public String getIpAddress() {
        SocketAddress $$0 = this.connection.getRemoteAddress();
        if ($$0 instanceof InetSocketAddress) {
            InetSocketAddress $$1 = (InetSocketAddress)$$0;
            return InetAddresses.toAddrString($$1.getAddress());
        }
        return "<unknown>";
    }

    public void updateOptions(ClientInformation $$0) {
        this.language = $$0.language();
        this.requestedViewDistance = $$0.viewDistance();
        this.chatVisibility = $$0.chatVisibility();
        this.canChatColor = $$0.chatColors();
        this.textFilteringEnabled = $$0.textFilteringEnabled();
        this.allowsListing = $$0.allowsListing();
        this.particleStatus = $$0.particleStatus();
        this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte)$$0.modelCustomisation());
        this.getEntityData().set(DATA_PLAYER_MAIN_HAND, (byte)$$0.mainHand().getId());
    }

    public ClientInformation clientInformation() {
        byte $$0 = (Byte)this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION);
        HumanoidArm $$1 = HumanoidArm.BY_ID.apply(((Byte)this.getEntityData().get(DATA_PLAYER_MAIN_HAND)).byteValue());
        return new ClientInformation(this.language, this.requestedViewDistance, this.chatVisibility, this.canChatColor, $$0, $$1, this.textFilteringEnabled, this.allowsListing, this.particleStatus);
    }

    public boolean canChatInColor() {
        return this.canChatColor;
    }

    public ChatVisiblity getChatVisibility() {
        return this.chatVisibility;
    }

    private boolean acceptsSystemMessages(boolean $$0) {
        if (this.chatVisibility == ChatVisiblity.HIDDEN) {
            return $$0;
        }
        return true;
    }

    private boolean acceptsChatMessages() {
        return this.chatVisibility == ChatVisiblity.FULL;
    }

    public int requestedViewDistance() {
        return this.requestedViewDistance;
    }

    public void sendServerStatus(ServerStatus $$0) {
        this.connection.send(new ClientboundServerDataPacket($$0.description(), $$0.favicon().map(ServerStatus.Favicon::a)));
    }

    @Override
    public int getPermissionLevel() {
        return this.server.getProfilePermissions(this.getGameProfile());
    }

    public void resetLastActionTime() {
        this.lastActionTime = Util.getMillis();
    }

    public ServerStatsCounter getStats() {
        return this.stats;
    }

    public ServerRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    @Override
    protected void updateInvisibilityStatus() {
        if (this.isSpectator()) {
            this.removeEffectParticles();
            this.setInvisible(true);
        } else {
            super.updateInvisibilityStatus();
        }
    }

    public Entity getCamera() {
        return this.camera == null ? this : this.camera;
    }

    public void setCamera(@Nullable Entity $$0) {
        Entity $$1 = this.getCamera();
        Entity entity = this.camera = $$0 == null ? this : $$0;
        if ($$1 != this.camera) {
            Level level = this.camera.level();
            if (level instanceof ServerLevel) {
                ServerLevel $$2 = (ServerLevel)level;
                this.teleportTo($$2, this.camera.getX(), this.camera.getY(), this.camera.getZ(), Set.of(), this.getYRot(), this.getXRot(), false);
            }
            if ($$0 != null) {
                this.level().getChunkSource().move(this);
            }
            this.connection.send(new ClientboundSetCameraPacket(this.camera));
            this.connection.resetPosition();
        }
    }

    @Override
    protected void processPortalCooldown() {
        if (!this.isChangingDimension) {
            super.processPortalCooldown();
        }
    }

    @Override
    public void attack(Entity $$0) {
        if (this.isSpectator()) {
            this.setCamera($$0);
        } else {
            super.attack($$0);
        }
    }

    public long getLastActionTime() {
        return this.lastActionTime;
    }

    @Nullable
    public Component getTabListDisplayName() {
        return null;
    }

    public int getTabListOrder() {
        return 0;
    }

    @Override
    public void swing(InteractionHand $$0) {
        super.swing($$0);
        this.resetAttackStrengthTicker();
    }

    public boolean isChangingDimension() {
        return this.isChangingDimension;
    }

    public void hasChangedDimension() {
        this.isChangingDimension = false;
    }

    public PlayerAdvancements getAdvancements() {
        return this.advancements;
    }

    @Nullable
    public RespawnConfig getRespawnConfig() {
        return this.respawnConfig;
    }

    public void copyRespawnPosition(ServerPlayer $$0) {
        this.setRespawnPosition($$0.respawnConfig, false);
    }

    public void setRespawnPosition(@Nullable RespawnConfig $$0, boolean $$1) {
        if ($$1 && $$0 != null && !$$0.isSamePosition(this.respawnConfig)) {
            this.sendSystemMessage(SPAWN_SET_MESSAGE);
        }
        this.respawnConfig = $$0;
    }

    public SectionPos getLastSectionPos() {
        return this.lastSectionPos;
    }

    public void setLastSectionPos(SectionPos $$0) {
        this.lastSectionPos = $$0;
    }

    public ChunkTrackingView getChunkTrackingView() {
        return this.chunkTrackingView;
    }

    public void setChunkTrackingView(ChunkTrackingView $$0) {
        this.chunkTrackingView = $$0;
    }

    @Override
    public void playNotifySound(SoundEvent $$0, SoundSource $$1, float $$2, float $$3) {
        this.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder($$0), $$1, this.getX(), this.getY(), this.getZ(), $$2, $$3, this.random.nextLong()));
    }

    @Override
    public ItemEntity drop(ItemStack $$0, boolean $$1, boolean $$2) {
        ItemEntity $$3 = super.drop($$0, $$1, $$2);
        if ($$2) {
            ItemStack $$4;
            ItemStack itemStack = $$4 = $$3 != null ? $$3.getItem() : ItemStack.EMPTY;
            if (!$$4.isEmpty()) {
                this.awardStat(Stats.ITEM_DROPPED.get($$4.getItem()), $$0.getCount());
                this.awardStat(Stats.DROP);
            }
        }
        return $$3;
    }

    public TextFilter getTextFilter() {
        return this.textFilter;
    }

    public void setServerLevel(ServerLevel $$0) {
        this.setLevel($$0);
        this.gameMode.setLevel($$0);
    }

    @Nullable
    private static GameType readPlayerMode(@Nullable ValueInput $$0, String $$1) {
        return $$0 != null ? (GameType)$$0.read($$1, GameType.LEGACY_ID_CODEC).orElse(null) : null;
    }

    private GameType calculateGameModeForNewPlayer(@Nullable GameType $$0) {
        GameType $$1 = this.server.getForcedGameType();
        if ($$1 != null) {
            return $$1;
        }
        return $$0 != null ? $$0 : this.server.getDefaultGameType();
    }

    public void loadGameTypes(@Nullable ValueInput $$0) {
        this.gameMode.setGameModeForPlayer(this.calculateGameModeForNewPlayer(ServerPlayer.readPlayerMode($$0, "playerGameType")), ServerPlayer.readPlayerMode($$0, "previousPlayerGameType"));
    }

    private void storeGameTypes(ValueOutput $$0) {
        $$0.store("playerGameType", GameType.LEGACY_ID_CODEC, this.gameMode.getGameModeForPlayer());
        GameType $$1 = this.gameMode.getPreviousGameModeForPlayer();
        $$0.storeNullable("previousPlayerGameType", GameType.LEGACY_ID_CODEC, $$1);
    }

    @Override
    public boolean isTextFilteringEnabled() {
        return this.textFilteringEnabled;
    }

    public boolean shouldFilterMessageTo(ServerPlayer $$0) {
        if ($$0 == this) {
            return false;
        }
        return this.textFilteringEnabled || $$0.textFilteringEnabled;
    }

    @Override
    public boolean mayInteract(ServerLevel $$0, BlockPos $$1) {
        return super.mayInteract($$0, $$1) && $$0.mayInteract(this, $$1);
    }

    @Override
    protected void updateUsingItem(ItemStack $$0) {
        CriteriaTriggers.USING_ITEM.trigger(this, $$0);
        super.updateUsingItem($$0);
    }

    public boolean drop(boolean $$0) {
        Inventory $$12 = this.getInventory();
        ItemStack $$2 = $$12.removeFromSelected($$0);
        this.containerMenu.findSlot($$12, $$12.getSelectedSlot()).ifPresent($$1 -> this.containerMenu.setRemoteSlot($$1, $$12.getSelectedItem()));
        return this.drop($$2, false, true) != null;
    }

    @Override
    public void handleExtraItemsCreatedOnUse(ItemStack $$0) {
        if (!this.getInventory().add($$0)) {
            this.drop($$0, false);
        }
    }

    public boolean allowsListing() {
        return this.allowsListing;
    }

    @Override
    public Optional<WardenSpawnTracker> getWardenSpawnTracker() {
        return Optional.of(this.wardenSpawnTracker);
    }

    public void setSpawnExtraParticlesOnFall(boolean $$0) {
        this.spawnExtraParticlesOnFall = $$0;
    }

    @Override
    public void onItemPickup(ItemEntity $$0) {
        super.onItemPickup($$0);
        Entity $$1 = $$0.getOwner();
        if ($$1 != null) {
            CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_PLAYER.trigger(this, $$0.getItem(), $$1);
        }
    }

    public void setChatSession(RemoteChatSession $$0) {
        this.chatSession = $$0;
    }

    @Nullable
    public RemoteChatSession getChatSession() {
        if (this.chatSession != null && this.chatSession.hasExpired()) {
            return null;
        }
        return this.chatSession;
    }

    @Override
    public void indicateDamage(double $$0, double $$1) {
        this.hurtDir = (float)(Mth.atan2($$1, $$0) * 57.2957763671875 - (double)this.getYRot());
        this.connection.send(new ClientboundHurtAnimationPacket(this));
    }

    @Override
    public boolean startRiding(Entity $$0, boolean $$1) {
        if (super.startRiding($$0, $$1)) {
            $$0.positionRider(this);
            this.connection.teleport(new PositionMoveRotation(this.position(), Vec3.ZERO, 0.0f, 0.0f), Relative.ROTATION);
            if ($$0 instanceof LivingEntity) {
                LivingEntity $$2 = (LivingEntity)$$0;
                this.server.getPlayerList().sendActiveEffects($$2, this.connection);
            }
            this.connection.send(new ClientboundSetPassengersPacket($$0));
            return true;
        }
        return false;
    }

    @Override
    public void removeVehicle() {
        Entity $$0 = this.getVehicle();
        super.removeVehicle();
        if ($$0 instanceof LivingEntity) {
            LivingEntity $$1 = (LivingEntity)$$0;
            for (MobEffectInstance $$2 : $$1.getActiveEffects()) {
                this.connection.send(new ClientboundRemoveMobEffectPacket($$0.getId(), $$2.getEffect()));
            }
        }
        if ($$0 != null) {
            this.connection.send(new ClientboundSetPassengersPacket($$0));
        }
    }

    public CommonPlayerSpawnInfo createCommonSpawnInfo(ServerLevel $$0) {
        return new CommonPlayerSpawnInfo($$0.dimensionTypeRegistration(), $$0.dimension(), BiomeManager.obfuscateSeed($$0.getSeed()), this.gameMode.getGameModeForPlayer(), this.gameMode.getPreviousGameModeForPlayer(), $$0.isDebug(), $$0.isFlat(), this.getLastDeathLocation(), this.getPortalCooldown(), $$0.getSeaLevel());
    }

    public void setRaidOmenPosition(BlockPos $$0) {
        this.raidOmenPosition = $$0;
    }

    public void clearRaidOmenPosition() {
        this.raidOmenPosition = null;
    }

    @Nullable
    public BlockPos getRaidOmenPosition() {
        return this.raidOmenPosition;
    }

    @Override
    public Vec3 getKnownMovement() {
        Entity $$0 = this.getVehicle();
        if ($$0 != null && $$0.getControllingPassenger() != this) {
            return $$0.getKnownMovement();
        }
        return this.lastKnownClientMovement;
    }

    public void setKnownMovement(Vec3 $$0) {
        this.lastKnownClientMovement = $$0;
    }

    @Override
    protected float getEnchantedDamage(Entity $$0, float $$1, DamageSource $$2) {
        return EnchantmentHelper.modifyDamage(this.level(), this.getWeaponItem(), $$0, $$2, $$1);
    }

    @Override
    public void onEquippedItemBroken(Item $$0, EquipmentSlot $$1) {
        super.onEquippedItemBroken($$0, $$1);
        this.awardStat(Stats.ITEM_BROKEN.get($$0));
    }

    public Input getLastClientInput() {
        return this.lastClientInput;
    }

    public void setLastClientInput(Input $$0) {
        this.lastClientInput = $$0;
    }

    public Vec3 getLastClientMoveIntent() {
        float $$0;
        float f = this.lastClientInput.left() == this.lastClientInput.right() ? 0.0f : ($$0 = this.lastClientInput.left() ? 1.0f : -1.0f);
        float $$1 = this.lastClientInput.forward() == this.lastClientInput.backward() ? 0.0f : (this.lastClientInput.forward() ? 1.0f : -1.0f);
        return ServerPlayer.getInputVector(new Vec3($$0, 0.0, $$1), 1.0f, this.getYRot());
    }

    public void registerEnderPearl(ThrownEnderpearl $$0) {
        this.enderPearls.add($$0);
    }

    public void deregisterEnderPearl(ThrownEnderpearl $$0) {
        this.enderPearls.remove($$0);
    }

    public Set<ThrownEnderpearl> getEnderPearls() {
        return this.enderPearls;
    }

    public long registerAndUpdateEnderPearlTicket(ThrownEnderpearl $$0) {
        Level level = $$0.level();
        if (level instanceof ServerLevel) {
            ServerLevel $$1 = (ServerLevel)level;
            ChunkPos $$2 = $$0.chunkPosition();
            this.registerEnderPearl($$0);
            $$1.resetEmptyTime();
            return ServerPlayer.placeEnderPearlTicket($$1, $$2) - 1L;
        }
        return 0L;
    }

    public static long placeEnderPearlTicket(ServerLevel $$0, ChunkPos $$1) {
        $$0.getChunkSource().addTicketWithRadius(TicketType.ENDER_PEARL, $$1, 2);
        return TicketType.ENDER_PEARL.timeout();
    }

    @Override
    public /* synthetic */ Level level() {
        return this.level();
    }

    @Override
    @Nullable
    public /* synthetic */ Entity teleport(TeleportTransition teleportTransition) {
        return this.teleport(teleportTransition);
    }

    public static final class RespawnConfig
    extends Record {
        private final ResourceKey<Level> dimension;
        final BlockPos pos;
        final float angle;
        final boolean forced;
        public static final Codec<RespawnConfig> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Level.RESOURCE_KEY_CODEC.optionalFieldOf("dimension", Level.OVERWORLD).forGetter(RespawnConfig::dimension), (App)BlockPos.CODEC.fieldOf("pos").forGetter(RespawnConfig::pos), (App)Codec.FLOAT.optionalFieldOf("angle", (Object)Float.valueOf(0.0f)).forGetter(RespawnConfig::angle), (App)Codec.BOOL.optionalFieldOf("forced", (Object)false).forGetter(RespawnConfig::forced)).apply((Applicative)$$0, RespawnConfig::new));

        public RespawnConfig(ResourceKey<Level> $$0, BlockPos $$1, float $$2, boolean $$3) {
            this.dimension = $$0;
            this.pos = $$1;
            this.angle = $$2;
            this.forced = $$3;
        }

        static ResourceKey<Level> getDimensionOrDefault(@Nullable RespawnConfig $$0) {
            return $$0 != null ? $$0.dimension() : Level.OVERWORLD;
        }

        public boolean isSamePosition(@Nullable RespawnConfig $$0) {
            return $$0 != null && this.dimension == $$0.dimension && this.pos.equals($$0.pos);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RespawnConfig.class, "dimension;pos;angle;forced", "dimension", "pos", "angle", "forced"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RespawnConfig.class, "dimension;pos;angle;forced", "dimension", "pos", "angle", "forced"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RespawnConfig.class, "dimension;pos;angle;forced", "dimension", "pos", "angle", "forced"}, this, $$0);
        }

        public ResourceKey<Level> dimension() {
            return this.dimension;
        }

        public BlockPos pos() {
            return this.pos;
        }

        public float angle() {
            return this.angle;
        }

        public boolean forced() {
            return this.forced;
        }
    }

    record RespawnPosAngle(Vec3 position, float yaw) {
        public static RespawnPosAngle of(Vec3 $$0, BlockPos $$1) {
            return new RespawnPosAngle($$0, RespawnPosAngle.calculateLookAtYaw($$0, $$1));
        }

        private static float calculateLookAtYaw(Vec3 $$0, BlockPos $$1) {
            Vec3 $$2 = Vec3.atBottomCenterOf($$1).subtract($$0).normalize();
            return (float)Mth.wrapDegrees(Mth.atan2($$2.z, $$2.x) * 57.2957763671875 - 90.0);
        }
    }
}

