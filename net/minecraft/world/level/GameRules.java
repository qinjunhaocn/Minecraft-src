/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicLike
 */
package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.waypoints.ServerWaypointManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.slf4j.Logger;

public class GameRules {
    public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Key<?>, Type<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing($$0 -> $$0.id));
    public static final Key<BooleanValue> RULE_DOFIRETICK = GameRules.register("doFireTick", Category.UPDATES, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_ALLOWFIRETICKAWAYFROMPLAYERS = GameRules.register("allowFireTicksAwayFromPlayer", Category.UPDATES, BooleanValue.create(false));
    public static final Key<BooleanValue> RULE_MOBGRIEFING = GameRules.register("mobGriefing", Category.MOBS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_KEEPINVENTORY = GameRules.register("keepInventory", Category.PLAYER, BooleanValue.create(false));
    public static final Key<BooleanValue> RULE_DOMOBSPAWNING = GameRules.register("doMobSpawning", Category.SPAWNING, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DOMOBLOOT = GameRules.register("doMobLoot", Category.DROPS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_PROJECTILESCANBREAKBLOCKS = GameRules.register("projectilesCanBreakBlocks", Category.DROPS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DOBLOCKDROPS = GameRules.register("doTileDrops", Category.DROPS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DOENTITYDROPS = GameRules.register("doEntityDrops", Category.DROPS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_COMMANDBLOCKOUTPUT = GameRules.register("commandBlockOutput", Category.CHAT, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_NATURAL_REGENERATION = GameRules.register("naturalRegeneration", Category.PLAYER, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DAYLIGHT = GameRules.register("doDaylightCycle", Category.UPDATES, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_LOGADMINCOMMANDS = GameRules.register("logAdminCommands", Category.CHAT, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_SHOWDEATHMESSAGES = GameRules.register("showDeathMessages", Category.CHAT, BooleanValue.create(true));
    public static final Key<IntegerValue> RULE_RANDOMTICKING = GameRules.register("randomTickSpeed", Category.UPDATES, IntegerValue.create(3));
    public static final Key<BooleanValue> RULE_SENDCOMMANDFEEDBACK = GameRules.register("sendCommandFeedback", Category.CHAT, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_REDUCEDDEBUGINFO = GameRules.register("reducedDebugInfo", Category.MISC, BooleanValue.create(false, ($$0, $$1) -> {
        byte $$2 = $$1.get() ? (byte)22 : (byte)23;
        for (ServerPlayer $$3 : $$0.getPlayerList().getPlayers()) {
            $$3.connection.send(new ClientboundEntityEventPacket($$3, $$2));
        }
    }));
    public static final Key<BooleanValue> RULE_SPECTATORSGENERATECHUNKS = GameRules.register("spectatorsGenerateChunks", Category.PLAYER, BooleanValue.create(true));
    public static final Key<IntegerValue> RULE_SPAWN_RADIUS = GameRules.register("spawnRadius", Category.PLAYER, IntegerValue.create(10));
    public static final Key<BooleanValue> RULE_DISABLE_PLAYER_MOVEMENT_CHECK = GameRules.register("disablePlayerMovementCheck", Category.PLAYER, BooleanValue.create(false));
    public static final Key<BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.register("disableElytraMovementCheck", Category.PLAYER, BooleanValue.create(false));
    public static final Key<IntegerValue> RULE_MAX_ENTITY_CRAMMING = GameRules.register("maxEntityCramming", Category.MOBS, IntegerValue.create(24));
    public static final Key<BooleanValue> RULE_WEATHER_CYCLE = GameRules.register("doWeatherCycle", Category.UPDATES, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_LIMITED_CRAFTING = GameRules.register("doLimitedCrafting", Category.PLAYER, BooleanValue.create(false, ($$0, $$1) -> {
        for (ServerPlayer $$2 : $$0.getPlayerList().getPlayers()) {
            $$2.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.LIMITED_CRAFTING, $$1.get() ? 1.0f : 0.0f));
        }
    }));
    public static final Key<IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH = GameRules.register("maxCommandChainLength", Category.MISC, IntegerValue.create(65536));
    public static final Key<IntegerValue> RULE_MAX_COMMAND_FORK_COUNT = GameRules.register("maxCommandForkCount", Category.MISC, IntegerValue.create(65536));
    public static final Key<IntegerValue> RULE_COMMAND_MODIFICATION_BLOCK_LIMIT = GameRules.register("commandModificationBlockLimit", Category.MISC, IntegerValue.create(32768));
    public static final Key<BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS = GameRules.register("announceAdvancements", Category.CHAT, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DISABLE_RAIDS = GameRules.register("disableRaids", Category.MOBS, BooleanValue.create(false));
    public static final Key<BooleanValue> RULE_DOINSOMNIA = GameRules.register("doInsomnia", Category.SPAWNING, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DO_IMMEDIATE_RESPAWN = GameRules.register("doImmediateRespawn", Category.PLAYER, BooleanValue.create(false, ($$0, $$1) -> {
        for (ServerPlayer $$2 : $$0.getPlayerList().getPlayers()) {
            $$2.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.IMMEDIATE_RESPAWN, $$1.get() ? 1.0f : 0.0f));
        }
    }));
    public static final Key<IntegerValue> RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY = GameRules.register("playersNetherPortalDefaultDelay", Category.PLAYER, IntegerValue.create(80));
    public static final Key<IntegerValue> RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY = GameRules.register("playersNetherPortalCreativeDelay", Category.PLAYER, IntegerValue.create(0));
    public static final Key<BooleanValue> RULE_DROWNING_DAMAGE = GameRules.register("drowningDamage", Category.PLAYER, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_FALL_DAMAGE = GameRules.register("fallDamage", Category.PLAYER, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_FIRE_DAMAGE = GameRules.register("fireDamage", Category.PLAYER, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_FREEZE_DAMAGE = GameRules.register("freezeDamage", Category.PLAYER, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DO_PATROL_SPAWNING = GameRules.register("doPatrolSpawning", Category.SPAWNING, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DO_TRADER_SPAWNING = GameRules.register("doTraderSpawning", Category.SPAWNING, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DO_WARDEN_SPAWNING = GameRules.register("doWardenSpawning", Category.SPAWNING, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_FORGIVE_DEAD_PLAYERS = GameRules.register("forgiveDeadPlayers", Category.MOBS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_UNIVERSAL_ANGER = GameRules.register("universalAnger", Category.MOBS, BooleanValue.create(false));
    public static final Key<IntegerValue> RULE_PLAYERS_SLEEPING_PERCENTAGE = GameRules.register("playersSleepingPercentage", Category.PLAYER, IntegerValue.create(100));
    public static final Key<BooleanValue> RULE_BLOCK_EXPLOSION_DROP_DECAY = GameRules.register("blockExplosionDropDecay", Category.DROPS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_MOB_EXPLOSION_DROP_DECAY = GameRules.register("mobExplosionDropDecay", Category.DROPS, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_TNT_EXPLOSION_DROP_DECAY = GameRules.register("tntExplosionDropDecay", Category.DROPS, BooleanValue.create(false));
    public static final Key<IntegerValue> RULE_SNOW_ACCUMULATION_HEIGHT = GameRules.register("snowAccumulationHeight", Category.UPDATES, IntegerValue.create(1));
    public static final Key<BooleanValue> RULE_WATER_SOURCE_CONVERSION = GameRules.register("waterSourceConversion", Category.UPDATES, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_LAVA_SOURCE_CONVERSION = GameRules.register("lavaSourceConversion", Category.UPDATES, BooleanValue.create(false));
    public static final Key<BooleanValue> RULE_GLOBAL_SOUND_EVENTS = GameRules.register("globalSoundEvents", Category.MISC, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_DO_VINES_SPREAD = GameRules.register("doVinesSpread", Category.UPDATES, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_ENDER_PEARLS_VANISH_ON_DEATH = GameRules.register("enderPearlsVanishOnDeath", Category.PLAYER, BooleanValue.create(true));
    public static final Key<IntegerValue> RULE_MINECART_MAX_SPEED = GameRules.register("minecartMaxSpeed", Category.MISC, IntegerValue.create(8, 1, 1000, FeatureFlagSet.of(FeatureFlags.MINECART_IMPROVEMENTS), ($$0, $$1) -> {}));
    public static final Key<IntegerValue> RULE_SPAWN_CHUNK_RADIUS = GameRules.register("spawnChunkRadius", Category.MISC, IntegerValue.create(2, 0, 32, FeatureFlagSet.of(), ($$0, $$1) -> {
        ServerLevel $$2 = $$0.overworld();
        $$2.setDefaultSpawnPos($$2.getSharedSpawnPos(), $$2.getSharedSpawnAngle());
    }));
    public static final Key<BooleanValue> RULE_TNT_EXPLODES = GameRules.register("tntExplodes", Category.MISC, BooleanValue.create(true));
    public static final Key<BooleanValue> RULE_LOCATOR_BAR = GameRules.register("locatorBar", Category.PLAYER, BooleanValue.create(true, ($$0, $$12) -> $$0.getAllLevels().forEach($$1 -> {
        ServerWaypointManager $$2 = $$1.getWaypointManager();
        if ($$12.get()) {
            $$1.players().forEach($$2::updatePlayer);
        } else {
            $$2.breakAllConnections();
        }
    })));
    private final Map<Key<?>, Value<?>> rules;
    private final FeatureFlagSet enabledFeatures;

    public static <T extends Value<T>> Type<T> getType(Key<T> $$0) {
        return GAME_RULE_TYPES.get($$0);
    }

    public static <T extends Value<T>> Codec<Key<T>> keyCodec(Class<T> $$0) {
        return Codec.STRING.comapFlatMap($$12 -> GAME_RULE_TYPES.entrySet().stream().filter($$1 -> ((Type)$$1.getValue()).valueClass == $$0).map(Map.Entry::getKey).filter($$1 -> $$1.getId().equals($$12)).map($$0 -> $$0).findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Invalid game rule ID for type: " + $$12)), Key::getId);
    }

    private static <T extends Value<T>> Key<T> register(String $$0, Category $$1, Type<T> $$2) {
        Key $$3 = new Key($$0, $$1);
        Type<T> $$4 = GAME_RULE_TYPES.put($$3, $$2);
        if ($$4 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + $$0);
        }
        return $$3;
    }

    public GameRules(FeatureFlagSet $$0, DynamicLike<?> $$1) {
        this($$0);
        this.loadFromTag($$1);
    }

    public GameRules(FeatureFlagSet $$02) {
        this((Map)GameRules.availableRules($$02).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, $$0 -> ((Type)$$0.getValue()).createRule())), $$02);
    }

    private static Stream<Map.Entry<Key<?>, Type<?>>> availableRules(FeatureFlagSet $$0) {
        return GAME_RULE_TYPES.entrySet().stream().filter($$1 -> ((Type)$$1.getValue()).requiredFeatures.isSubsetOf($$0));
    }

    private GameRules(Map<Key<?>, Value<?>> $$0, FeatureFlagSet $$1) {
        this.rules = $$0;
        this.enabledFeatures = $$1;
    }

    public <T extends Value<T>> T getRule(Key<T> $$0) {
        Value<?> $$1 = this.rules.get($$0);
        if ($$1 == null) {
            throw new IllegalArgumentException("Tried to access invalid game rule");
        }
        return (T)$$1;
    }

    public CompoundTag createTag() {
        CompoundTag $$0 = new CompoundTag();
        this.rules.forEach(($$1, $$2) -> $$0.putString($$1.id, $$2.serialize()));
        return $$0;
    }

    private void loadFromTag(DynamicLike<?> $$0) {
        this.rules.forEach(($$1, $$2) -> $$0.get($$1.id).asString().ifSuccess($$2::deserialize));
    }

    public GameRules copy(FeatureFlagSet $$02) {
        return new GameRules((Map)GameRules.availableRules($$02).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, $$0 -> this.rules.containsKey($$0.getKey()) ? this.rules.get($$0.getKey()).copy() : ((Type)$$0.getValue()).createRule())), $$02);
    }

    public void visitGameRuleTypes(GameRuleTypeVisitor $$0) {
        GAME_RULE_TYPES.forEach(($$1, $$2) -> this.callVisitorCap($$0, (Key<?>)$$1, (Type<?>)$$2));
    }

    private <T extends Value<T>> void callVisitorCap(GameRuleTypeVisitor $$0, Key<?> $$1, Type<?> $$2) {
        Key<?> $$3 = $$1;
        Type<?> $$4 = $$2;
        if ($$4.requiredFeatures.isSubsetOf(this.enabledFeatures)) {
            $$0.visit($$3, $$4);
            $$4.callVisitor($$0, $$3);
        }
    }

    public void assignFrom(GameRules $$0, @Nullable MinecraftServer $$1) {
        $$0.rules.keySet().forEach($$2 -> this.assignCap((Key)$$2, $$0, $$1));
    }

    private <T extends Value<T>> void assignCap(Key<T> $$0, GameRules $$1, @Nullable MinecraftServer $$2) {
        T $$3 = $$1.getRule($$0);
        ((Value)this.getRule($$0)).setFrom($$3, $$2);
    }

    public boolean getBoolean(Key<BooleanValue> $$0) {
        return this.getRule($$0).get();
    }

    public int getInt(Key<IntegerValue> $$0) {
        return this.getRule($$0).get();
    }

    public static class Type<T extends Value<T>> {
        final Supplier<ArgumentType<?>> argument;
        private final Function<Type<T>, T> constructor;
        final BiConsumer<MinecraftServer, T> callback;
        private final VisitorCaller<T> visitorCaller;
        final Class<T> valueClass;
        final FeatureFlagSet requiredFeatures;

        Type(Supplier<ArgumentType<?>> $$0, Function<Type<T>, T> $$1, BiConsumer<MinecraftServer, T> $$2, VisitorCaller<T> $$3, Class<T> $$4, FeatureFlagSet $$5) {
            this.argument = $$0;
            this.constructor = $$1;
            this.callback = $$2;
            this.visitorCaller = $$3;
            this.valueClass = $$4;
            this.requiredFeatures = $$5;
        }

        public RequiredArgumentBuilder<CommandSourceStack, ?> createArgument(String $$0) {
            return Commands.argument($$0, this.argument.get());
        }

        public T createRule() {
            return (T)((Value)this.constructor.apply(this));
        }

        public void callVisitor(GameRuleTypeVisitor $$0, Key<T> $$1) {
            this.visitorCaller.call($$0, $$1, this);
        }

        public FeatureFlagSet requiredFeatures() {
            return this.requiredFeatures;
        }
    }

    public static final class Key<T extends Value<T>> {
        final String id;
        private final Category category;

        public Key(String $$0, Category $$1) {
            this.id = $$0;
            this.category = $$1;
        }

        public String toString() {
            return this.id;
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            return $$0 instanceof Key && ((Key)$$0).id.equals(this.id);
        }

        public int hashCode() {
            return this.id.hashCode();
        }

        public String getId() {
            return this.id;
        }

        public String getDescriptionId() {
            return "gamerule." + this.id;
        }

        public Category getCategory() {
            return this.category;
        }
    }

    public static final class Category
    extends Enum<Category> {
        public static final /* enum */ Category PLAYER = new Category("gamerule.category.player");
        public static final /* enum */ Category MOBS = new Category("gamerule.category.mobs");
        public static final /* enum */ Category SPAWNING = new Category("gamerule.category.spawning");
        public static final /* enum */ Category DROPS = new Category("gamerule.category.drops");
        public static final /* enum */ Category UPDATES = new Category("gamerule.category.updates");
        public static final /* enum */ Category CHAT = new Category("gamerule.category.chat");
        public static final /* enum */ Category MISC = new Category("gamerule.category.misc");
        private final String descriptionId;
        private static final /* synthetic */ Category[] $VALUES;

        public static Category[] values() {
            return (Category[])$VALUES.clone();
        }

        public static Category valueOf(String $$0) {
            return Enum.valueOf(Category.class, $$0);
        }

        private Category(String $$0) {
            this.descriptionId = $$0;
        }

        public String getDescriptionId() {
            return this.descriptionId;
        }

        private static /* synthetic */ Category[] b() {
            return new Category[]{PLAYER, MOBS, SPAWNING, DROPS, UPDATES, CHAT, MISC};
        }

        static {
            $VALUES = Category.b();
        }
    }

    public static abstract class Value<T extends Value<T>> {
        protected final Type<T> type;

        public Value(Type<T> $$0) {
            this.type = $$0;
        }

        protected abstract void updateFromArgument(CommandContext<CommandSourceStack> var1, String var2);

        public void setFromArgument(CommandContext<CommandSourceStack> $$0, String $$1) {
            this.updateFromArgument($$0, $$1);
            this.onChanged(((CommandSourceStack)$$0.getSource()).getServer());
        }

        protected void onChanged(@Nullable MinecraftServer $$0) {
            if ($$0 != null) {
                this.type.callback.accept($$0, (MinecraftServer)this.getSelf());
            }
        }

        protected abstract void deserialize(String var1);

        public abstract String serialize();

        public String toString() {
            return this.serialize();
        }

        public abstract int getCommandResult();

        protected abstract T getSelf();

        protected abstract T copy();

        public abstract void setFrom(T var1, @Nullable MinecraftServer var2);
    }

    public static interface GameRuleTypeVisitor {
        default public <T extends Value<T>> void visit(Key<T> $$0, Type<T> $$1) {
        }

        default public void visitBoolean(Key<BooleanValue> $$0, Type<BooleanValue> $$1) {
        }

        default public void visitInteger(Key<IntegerValue> $$0, Type<IntegerValue> $$1) {
        }
    }

    public static class BooleanValue
    extends Value<BooleanValue> {
        private boolean value;

        private static Type<BooleanValue> create(boolean $$0, BiConsumer<MinecraftServer, BooleanValue> $$12, FeatureFlagSet $$2) {
            return new Type<BooleanValue>(BoolArgumentType::bool, $$1 -> new BooleanValue((Type<BooleanValue>)$$1, $$0), $$12, GameRuleTypeVisitor::visitBoolean, BooleanValue.class, $$2);
        }

        static Type<BooleanValue> create(boolean $$0, BiConsumer<MinecraftServer, BooleanValue> $$12) {
            return new Type<BooleanValue>(BoolArgumentType::bool, $$1 -> new BooleanValue((Type<BooleanValue>)$$1, $$0), $$12, GameRuleTypeVisitor::visitBoolean, BooleanValue.class, FeatureFlagSet.of());
        }

        static Type<BooleanValue> create(boolean $$02) {
            return BooleanValue.create($$02, ($$0, $$1) -> {});
        }

        public BooleanValue(Type<BooleanValue> $$0, boolean $$1) {
            super($$0);
            this.value = $$1;
        }

        @Override
        protected void updateFromArgument(CommandContext<CommandSourceStack> $$0, String $$1) {
            this.value = BoolArgumentType.getBool($$0, (String)$$1);
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean $$0, @Nullable MinecraftServer $$1) {
            this.value = $$0;
            this.onChanged($$1);
        }

        @Override
        public String serialize() {
            return Boolean.toString(this.value);
        }

        @Override
        protected void deserialize(String $$0) {
            this.value = Boolean.parseBoolean($$0);
        }

        @Override
        public int getCommandResult() {
            return this.value ? 1 : 0;
        }

        @Override
        protected BooleanValue getSelf() {
            return this;
        }

        @Override
        protected BooleanValue copy() {
            return new BooleanValue(this.type, this.value);
        }

        @Override
        public void setFrom(BooleanValue $$0, @Nullable MinecraftServer $$1) {
            this.value = $$0.value;
            this.onChanged($$1);
        }

        @Override
        protected /* synthetic */ Value copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Value getSelf() {
            return this.getSelf();
        }
    }

    public static class IntegerValue
    extends Value<IntegerValue> {
        private int value;

        private static Type<IntegerValue> create(int $$0, BiConsumer<MinecraftServer, IntegerValue> $$12) {
            return new Type<IntegerValue>(IntegerArgumentType::integer, $$1 -> new IntegerValue((Type<IntegerValue>)$$1, $$0), $$12, GameRuleTypeVisitor::visitInteger, IntegerValue.class, FeatureFlagSet.of());
        }

        static Type<IntegerValue> create(int $$0, int $$12, int $$2, FeatureFlagSet $$3, BiConsumer<MinecraftServer, IntegerValue> $$4) {
            return new Type<IntegerValue>(() -> IntegerArgumentType.integer((int)$$12, (int)$$2), $$1 -> new IntegerValue((Type<IntegerValue>)$$1, $$0), $$4, GameRuleTypeVisitor::visitInteger, IntegerValue.class, $$3);
        }

        static Type<IntegerValue> create(int $$02) {
            return IntegerValue.create($$02, ($$0, $$1) -> {});
        }

        public IntegerValue(Type<IntegerValue> $$0, int $$1) {
            super($$0);
            this.value = $$1;
        }

        @Override
        protected void updateFromArgument(CommandContext<CommandSourceStack> $$0, String $$1) {
            this.value = IntegerArgumentType.getInteger($$0, (String)$$1);
        }

        public int get() {
            return this.value;
        }

        public void set(int $$0, @Nullable MinecraftServer $$1) {
            this.value = $$0;
            this.onChanged($$1);
        }

        @Override
        public String serialize() {
            return Integer.toString(this.value);
        }

        @Override
        protected void deserialize(String $$0) {
            this.value = IntegerValue.safeParse($$0);
        }

        public boolean tryDeserialize(String $$0) {
            try {
                StringReader $$1 = new StringReader($$0);
                this.value = (Integer)this.type.argument.get().parse($$1);
                return !$$1.canRead();
            } catch (CommandSyntaxException commandSyntaxException) {
                return false;
            }
        }

        private static int safeParse(String $$0) {
            if (!$$0.isEmpty()) {
                try {
                    return Integer.parseInt($$0);
                } catch (NumberFormatException $$1) {
                    LOGGER.warn("Failed to parse integer {}", (Object)$$0);
                }
            }
            return 0;
        }

        @Override
        public int getCommandResult() {
            return this.value;
        }

        @Override
        protected IntegerValue getSelf() {
            return this;
        }

        @Override
        protected IntegerValue copy() {
            return new IntegerValue(this.type, this.value);
        }

        @Override
        public void setFrom(IntegerValue $$0, @Nullable MinecraftServer $$1) {
            this.value = $$0.value;
            this.onChanged($$1);
        }

        @Override
        protected /* synthetic */ Value copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Value getSelf() {
            return this.getSelf();
        }
    }

    static interface VisitorCaller<T extends Value<T>> {
        public void call(GameRuleTypeVisitor var1, Key<T> var2, Type<T> var3);
    }
}

