/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;
import org.slf4j.Logger;

public class PrimaryLevelData
implements ServerLevelData,
WorldData {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String LEVEL_NAME = "LevelName";
    protected static final String PLAYER = "Player";
    protected static final String WORLD_GEN_SETTINGS = "WorldGenSettings";
    private LevelSettings settings;
    private final WorldOptions worldOptions;
    private final SpecialWorldProperty specialWorldProperty;
    private final Lifecycle worldGenSettingsLifecycle;
    private BlockPos spawnPos;
    private float spawnAngle;
    private long gameTime;
    private long dayTime;
    @Nullable
    private final CompoundTag loadedPlayerTag;
    private final int version;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private boolean initialized;
    private boolean difficultyLocked;
    private WorldBorder.Settings worldBorder;
    private EndDragonFight.Data endDragonFightData;
    @Nullable
    private CompoundTag customBossEvents;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    @Nullable
    private UUID wanderingTraderId;
    private final Set<String> knownServerBrands;
    private boolean wasModded;
    private final Set<String> removedFeatureFlags;
    private final TimerQueue<MinecraftServer> scheduledEvents;

    private PrimaryLevelData(@Nullable CompoundTag $$0, boolean $$1, BlockPos $$2, float $$3, long $$4, long $$5, int $$6, int $$7, int $$8, boolean $$9, int $$10, boolean $$11, boolean $$12, boolean $$13, WorldBorder.Settings $$14, int $$15, int $$16, @Nullable UUID $$17, Set<String> $$18, Set<String> $$19, TimerQueue<MinecraftServer> $$20, @Nullable CompoundTag $$21, EndDragonFight.Data $$22, LevelSettings $$23, WorldOptions $$24, SpecialWorldProperty $$25, Lifecycle $$26) {
        this.wasModded = $$1;
        this.spawnPos = $$2;
        this.spawnAngle = $$3;
        this.gameTime = $$4;
        this.dayTime = $$5;
        this.version = $$6;
        this.clearWeatherTime = $$7;
        this.rainTime = $$8;
        this.raining = $$9;
        this.thunderTime = $$10;
        this.thundering = $$11;
        this.initialized = $$12;
        this.difficultyLocked = $$13;
        this.worldBorder = $$14;
        this.wanderingTraderSpawnDelay = $$15;
        this.wanderingTraderSpawnChance = $$16;
        this.wanderingTraderId = $$17;
        this.knownServerBrands = $$18;
        this.removedFeatureFlags = $$19;
        this.loadedPlayerTag = $$0;
        this.scheduledEvents = $$20;
        this.customBossEvents = $$21;
        this.endDragonFightData = $$22;
        this.settings = $$23;
        this.worldOptions = $$24;
        this.specialWorldProperty = $$25;
        this.worldGenSettingsLifecycle = $$26;
    }

    public PrimaryLevelData(LevelSettings $$0, WorldOptions $$1, SpecialWorldProperty $$2, Lifecycle $$3) {
        this(null, false, BlockPos.ZERO, 0.0f, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_SETTINGS, 0, 0, null, Sets.newLinkedHashSet(), new HashSet<String>(), new TimerQueue<MinecraftServer>(TimerCallbacks.SERVER_CALLBACKS), null, EndDragonFight.Data.DEFAULT, $$0.copy(), $$1, $$2, $$3);
    }

    public static <T> PrimaryLevelData parse(Dynamic<T> $$02, LevelSettings $$1, SpecialWorldProperty $$2, WorldOptions $$3, Lifecycle $$4) {
        long $$5 = $$02.get("Time").asLong(0L);
        return new PrimaryLevelData($$02.get(PLAYER).flatMap(arg_0 -> CompoundTag.CODEC.parse(arg_0)).result().orElse(null), $$02.get("WasModded").asBoolean(false), new BlockPos($$02.get("SpawnX").asInt(0), $$02.get("SpawnY").asInt(0), $$02.get("SpawnZ").asInt(0)), $$02.get("SpawnAngle").asFloat(0.0f), $$5, $$02.get("DayTime").asLong($$5), LevelVersion.parse($$02).levelDataVersion(), $$02.get("clearWeatherTime").asInt(0), $$02.get("rainTime").asInt(0), $$02.get("raining").asBoolean(false), $$02.get("thunderTime").asInt(0), $$02.get("thundering").asBoolean(false), $$02.get("initialized").asBoolean(true), $$02.get("DifficultyLocked").asBoolean(false), WorldBorder.Settings.read($$02, WorldBorder.DEFAULT_SETTINGS), $$02.get("WanderingTraderSpawnDelay").asInt(0), $$02.get("WanderingTraderSpawnChance").asInt(0), $$02.get("WanderingTraderId").read(UUIDUtil.CODEC).result().orElse(null), $$02.get("ServerBrands").asStream().flatMap($$0 -> $$0.asString().result().stream()).collect(Collectors.toCollection(Sets::newLinkedHashSet)), $$02.get("removed_features").asStream().flatMap($$0 -> $$0.asString().result().stream()).collect(Collectors.toSet()), new TimerQueue<MinecraftServer>(TimerCallbacks.SERVER_CALLBACKS, $$02.get("ScheduledEvents").asStream()), (CompoundTag)$$02.get("CustomBossEvents").orElseEmptyMap().getValue(), $$02.get("DragonFight").read(EndDragonFight.Data.CODEC).resultOrPartial(LOGGER::error).orElse(EndDragonFight.Data.DEFAULT), $$1, $$3, $$2, $$4);
    }

    @Override
    public CompoundTag createTag(RegistryAccess $$0, @Nullable CompoundTag $$1) {
        if ($$1 == null) {
            $$1 = this.loadedPlayerTag;
        }
        CompoundTag $$2 = new CompoundTag();
        this.setTagData($$0, $$2, $$1);
        return $$2;
    }

    private void setTagData(RegistryAccess $$0, CompoundTag $$12, @Nullable CompoundTag $$2) {
        $$12.put("ServerBrands", PrimaryLevelData.stringCollectionToTag(this.knownServerBrands));
        $$12.putBoolean("WasModded", this.wasModded);
        if (!this.removedFeatureFlags.isEmpty()) {
            $$12.put("removed_features", PrimaryLevelData.stringCollectionToTag(this.removedFeatureFlags));
        }
        CompoundTag $$3 = new CompoundTag();
        $$3.putString("Name", SharedConstants.getCurrentVersion().name());
        $$3.putInt("Id", SharedConstants.getCurrentVersion().dataVersion().version());
        $$3.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().stable());
        $$3.putString("Series", SharedConstants.getCurrentVersion().dataVersion().series());
        $$12.put("Version", $$3);
        NbtUtils.addCurrentDataVersion($$12);
        RegistryOps<Tag> $$4 = $$0.createSerializationContext(NbtOps.INSTANCE);
        WorldGenSettings.encode($$4, this.worldOptions, $$0).resultOrPartial(Util.prefix("WorldGenSettings: ", LOGGER::error)).ifPresent($$1 -> $$12.put(WORLD_GEN_SETTINGS, (Tag)$$1));
        $$12.putInt("GameType", this.settings.gameType().getId());
        $$12.putInt("SpawnX", this.spawnPos.getX());
        $$12.putInt("SpawnY", this.spawnPos.getY());
        $$12.putInt("SpawnZ", this.spawnPos.getZ());
        $$12.putFloat("SpawnAngle", this.spawnAngle);
        $$12.putLong("Time", this.gameTime);
        $$12.putLong("DayTime", this.dayTime);
        $$12.putLong("LastPlayed", Util.getEpochMillis());
        $$12.putString(LEVEL_NAME, this.settings.levelName());
        $$12.putInt("version", 19133);
        $$12.putInt("clearWeatherTime", this.clearWeatherTime);
        $$12.putInt("rainTime", this.rainTime);
        $$12.putBoolean("raining", this.raining);
        $$12.putInt("thunderTime", this.thunderTime);
        $$12.putBoolean("thundering", this.thundering);
        $$12.putBoolean("hardcore", this.settings.hardcore());
        $$12.putBoolean("allowCommands", this.settings.allowCommands());
        $$12.putBoolean("initialized", this.initialized);
        this.worldBorder.write($$12);
        $$12.putByte("Difficulty", (byte)this.settings.difficulty().getId());
        $$12.putBoolean("DifficultyLocked", this.difficultyLocked);
        $$12.put("GameRules", this.settings.gameRules().createTag());
        $$12.store("DragonFight", EndDragonFight.Data.CODEC, this.endDragonFightData);
        if ($$2 != null) {
            $$12.put(PLAYER, $$2);
        }
        $$12.store(WorldDataConfiguration.MAP_CODEC, this.settings.getDataConfiguration());
        if (this.customBossEvents != null) {
            $$12.put("CustomBossEvents", this.customBossEvents);
        }
        $$12.put("ScheduledEvents", this.scheduledEvents.store());
        $$12.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        $$12.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        $$12.storeNullable("WanderingTraderId", UUIDUtil.CODEC, this.wanderingTraderId);
    }

    private static ListTag stringCollectionToTag(Set<String> $$0) {
        ListTag $$1 = new ListTag();
        $$0.stream().map(StringTag::valueOf).forEach($$1::add);
        return $$1;
    }

    @Override
    public BlockPos getSpawnPos() {
        return this.spawnPos;
    }

    @Override
    public float getSpawnAngle() {
        return this.spawnAngle;
    }

    @Override
    public long getGameTime() {
        return this.gameTime;
    }

    @Override
    public long getDayTime() {
        return this.dayTime;
    }

    @Override
    @Nullable
    public CompoundTag getLoadedPlayerTag() {
        return this.loadedPlayerTag;
    }

    @Override
    public void setGameTime(long $$0) {
        this.gameTime = $$0;
    }

    @Override
    public void setDayTime(long $$0) {
        this.dayTime = $$0;
    }

    @Override
    public void setSpawn(BlockPos $$0, float $$1) {
        this.spawnPos = $$0.immutable();
        this.spawnAngle = $$1;
    }

    @Override
    public String getLevelName() {
        return this.settings.levelName();
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public int getClearWeatherTime() {
        return this.clearWeatherTime;
    }

    @Override
    public void setClearWeatherTime(int $$0) {
        this.clearWeatherTime = $$0;
    }

    @Override
    public boolean isThundering() {
        return this.thundering;
    }

    @Override
    public void setThundering(boolean $$0) {
        this.thundering = $$0;
    }

    @Override
    public int getThunderTime() {
        return this.thunderTime;
    }

    @Override
    public void setThunderTime(int $$0) {
        this.thunderTime = $$0;
    }

    @Override
    public boolean isRaining() {
        return this.raining;
    }

    @Override
    public void setRaining(boolean $$0) {
        this.raining = $$0;
    }

    @Override
    public int getRainTime() {
        return this.rainTime;
    }

    @Override
    public void setRainTime(int $$0) {
        this.rainTime = $$0;
    }

    @Override
    public GameType getGameType() {
        return this.settings.gameType();
    }

    @Override
    public void setGameType(GameType $$0) {
        this.settings = this.settings.withGameType($$0);
    }

    @Override
    public boolean isHardcore() {
        return this.settings.hardcore();
    }

    @Override
    public boolean isAllowCommands() {
        return this.settings.allowCommands();
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void setInitialized(boolean $$0) {
        this.initialized = $$0;
    }

    @Override
    public GameRules getGameRules() {
        return this.settings.gameRules();
    }

    @Override
    public WorldBorder.Settings getWorldBorder() {
        return this.worldBorder;
    }

    @Override
    public void setWorldBorder(WorldBorder.Settings $$0) {
        this.worldBorder = $$0;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.settings.difficulty();
    }

    @Override
    public void setDifficulty(Difficulty $$0) {
        this.settings = this.settings.withDifficulty($$0);
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    @Override
    public void setDifficultyLocked(boolean $$0) {
        this.difficultyLocked = $$0;
    }

    @Override
    public TimerQueue<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }

    @Override
    public void fillCrashReportCategory(CrashReportCategory $$0, LevelHeightAccessor $$1) {
        ServerLevelData.super.fillCrashReportCategory($$0, $$1);
        WorldData.super.fillCrashReportCategory($$0);
    }

    @Override
    public WorldOptions worldGenOptions() {
        return this.worldOptions;
    }

    @Override
    public boolean isFlatWorld() {
        return this.specialWorldProperty == SpecialWorldProperty.FLAT;
    }

    @Override
    public boolean isDebugWorld() {
        return this.specialWorldProperty == SpecialWorldProperty.DEBUG;
    }

    @Override
    public Lifecycle worldGenSettingsLifecycle() {
        return this.worldGenSettingsLifecycle;
    }

    @Override
    public EndDragonFight.Data endDragonFightData() {
        return this.endDragonFightData;
    }

    @Override
    public void setEndDragonFightData(EndDragonFight.Data $$0) {
        this.endDragonFightData = $$0;
    }

    @Override
    public WorldDataConfiguration getDataConfiguration() {
        return this.settings.getDataConfiguration();
    }

    @Override
    public void setDataConfiguration(WorldDataConfiguration $$0) {
        this.settings = this.settings.withDataConfiguration($$0);
    }

    @Override
    @Nullable
    public CompoundTag getCustomBossEvents() {
        return this.customBossEvents;
    }

    @Override
    public void setCustomBossEvents(@Nullable CompoundTag $$0) {
        this.customBossEvents = $$0;
    }

    @Override
    public int getWanderingTraderSpawnDelay() {
        return this.wanderingTraderSpawnDelay;
    }

    @Override
    public void setWanderingTraderSpawnDelay(int $$0) {
        this.wanderingTraderSpawnDelay = $$0;
    }

    @Override
    public int getWanderingTraderSpawnChance() {
        return this.wanderingTraderSpawnChance;
    }

    @Override
    public void setWanderingTraderSpawnChance(int $$0) {
        this.wanderingTraderSpawnChance = $$0;
    }

    @Override
    @Nullable
    public UUID getWanderingTraderId() {
        return this.wanderingTraderId;
    }

    @Override
    public void setWanderingTraderId(UUID $$0) {
        this.wanderingTraderId = $$0;
    }

    @Override
    public void setModdedInfo(String $$0, boolean $$1) {
        this.knownServerBrands.add($$0);
        this.wasModded |= $$1;
    }

    @Override
    public boolean wasModded() {
        return this.wasModded;
    }

    @Override
    public Set<String> getKnownServerBrands() {
        return ImmutableSet.copyOf(this.knownServerBrands);
    }

    @Override
    public Set<String> getRemovedFeatureFlags() {
        return Set.copyOf(this.removedFeatureFlags);
    }

    @Override
    public ServerLevelData overworldData() {
        return this;
    }

    @Override
    public LevelSettings getLevelSettings() {
        return this.settings.copy();
    }

    @Deprecated
    public static final class SpecialWorldProperty
    extends Enum<SpecialWorldProperty> {
        public static final /* enum */ SpecialWorldProperty NONE = new SpecialWorldProperty();
        public static final /* enum */ SpecialWorldProperty FLAT = new SpecialWorldProperty();
        public static final /* enum */ SpecialWorldProperty DEBUG = new SpecialWorldProperty();
        private static final /* synthetic */ SpecialWorldProperty[] $VALUES;

        public static SpecialWorldProperty[] values() {
            return (SpecialWorldProperty[])$VALUES.clone();
        }

        public static SpecialWorldProperty valueOf(String $$0) {
            return Enum.valueOf(SpecialWorldProperty.class, $$0);
        }

        private static /* synthetic */ SpecialWorldProperty[] a() {
            return new SpecialWorldProperty[]{NONE, FLAT, DEBUG};
        }

        static {
            $VALUES = SpecialWorldProperty.a();
        }
    }
}

