/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap
 */
package net.minecraft.world.level.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class Block
extends BlockBehaviour
implements ItemLike {
    public static final MapCodec<Block> CODEC = Block.simpleCodec(Block::new);
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Holder.Reference<Block> builtInRegistryHolder = BuiltInRegistries.BLOCK.createIntrusiveHolder(this);
    public static final IdMapper<BlockState> BLOCK_STATE_REGISTRY = new IdMapper();
    private static final LoadingCache<VoxelShape, Boolean> SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader<VoxelShape, Boolean>(){

        @Override
        public Boolean load(VoxelShape $$0) {
            return !Shapes.joinIsNotEmpty(Shapes.block(), $$0, BooleanOp.NOT_SAME);
        }

        @Override
        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((VoxelShape)object);
        }
    });
    public static final int UPDATE_NEIGHBORS = 1;
    public static final int UPDATE_CLIENTS = 2;
    public static final int UPDATE_INVISIBLE = 4;
    public static final int UPDATE_IMMEDIATE = 8;
    public static final int UPDATE_KNOWN_SHAPE = 16;
    public static final int UPDATE_SUPPRESS_DROPS = 32;
    public static final int UPDATE_MOVE_BY_PISTON = 64;
    public static final int UPDATE_SKIP_SHAPE_UPDATE_ON_WIRE = 128;
    public static final int UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS = 256;
    public static final int UPDATE_SKIP_ON_PLACE = 512;
    public static final int UPDATE_NONE = 260;
    public static final int UPDATE_ALL = 3;
    public static final int UPDATE_ALL_IMMEDIATE = 11;
    public static final int UPDATE_SKIP_ALL_SIDEEFFECTS = 816;
    public static final float INDESTRUCTIBLE = -1.0f;
    public static final float INSTANT = 0.0f;
    public static final int UPDATE_LIMIT = 512;
    protected final StateDefinition<Block, BlockState> stateDefinition;
    private BlockState defaultBlockState;
    @Nullable
    private Item item;
    private static final int CACHE_SIZE = 256;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<ShapePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<ShapePairKey> $$0 = new Object2ByteLinkedOpenHashMap<ShapePairKey>(256, 0.25f){

            protected void rehash(int $$0) {
            }
        };
        $$0.defaultReturnValue((byte)127);
        return $$0;
    });

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public static int getId(@Nullable BlockState $$0) {
        if ($$0 == null) {
            return 0;
        }
        int $$1 = BLOCK_STATE_REGISTRY.getId($$0);
        return $$1 == -1 ? 0 : $$1;
    }

    public static BlockState stateById(int $$0) {
        BlockState $$1 = BLOCK_STATE_REGISTRY.byId($$0);
        return $$1 == null ? Blocks.AIR.defaultBlockState() : $$1;
    }

    public static Block byItem(@Nullable Item $$0) {
        if ($$0 instanceof BlockItem) {
            return ((BlockItem)$$0).getBlock();
        }
        return Blocks.AIR;
    }

    public static BlockState pushEntitiesUp(BlockState $$0, BlockState $$1, LevelAccessor $$2, BlockPos $$3) {
        VoxelShape $$4 = Shapes.joinUnoptimized($$0.getCollisionShape($$2, $$3), $$1.getCollisionShape($$2, $$3), BooleanOp.ONLY_SECOND).move($$3);
        if ($$4.isEmpty()) {
            return $$1;
        }
        List<Entity> $$5 = $$2.getEntities(null, $$4.bounds());
        for (Entity $$6 : $$5) {
            double $$7 = Shapes.collide(Direction.Axis.Y, $$6.getBoundingBox().move(0.0, 1.0, 0.0), List.of((Object)$$4), -1.0);
            $$6.teleportRelative(0.0, 1.0 + $$7, 0.0);
        }
        return $$1;
    }

    public static VoxelShape box(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        return Shapes.box($$0 / 16.0, $$1 / 16.0, $$2 / 16.0, $$3 / 16.0, $$4 / 16.0, $$5 / 16.0);
    }

    public static VoxelShape[] a(int $$0, IntFunction<VoxelShape> $$1) {
        return (VoxelShape[])IntStream.rangeClosed(0, $$0).mapToObj($$1).toArray(VoxelShape[]::new);
    }

    public static VoxelShape cube(double $$0) {
        return Block.cube($$0, $$0, $$0);
    }

    public static VoxelShape cube(double $$0, double $$1, double $$2) {
        double $$3 = $$1 / 2.0;
        return Block.column($$0, $$2, 8.0 - $$3, 8.0 + $$3);
    }

    public static VoxelShape column(double $$0, double $$1, double $$2) {
        return Block.column($$0, $$0, $$1, $$2);
    }

    public static VoxelShape column(double $$0, double $$1, double $$2, double $$3) {
        double $$4 = $$0 / 2.0;
        double $$5 = $$1 / 2.0;
        return Block.box(8.0 - $$4, $$2, 8.0 - $$5, 8.0 + $$4, $$3, 8.0 + $$5);
    }

    public static VoxelShape boxZ(double $$0, double $$1, double $$2) {
        return Block.boxZ($$0, $$0, $$1, $$2);
    }

    public static VoxelShape boxZ(double $$0, double $$1, double $$2, double $$3) {
        double $$4 = $$1 / 2.0;
        return Block.boxZ($$0, 8.0 - $$4, 8.0 + $$4, $$2, $$3);
    }

    public static VoxelShape boxZ(double $$0, double $$1, double $$2, double $$3, double $$4) {
        double $$5 = $$0 / 2.0;
        return Block.box(8.0 - $$5, $$1, $$3, 8.0 + $$5, $$2, $$4);
    }

    public static BlockState updateFromNeighbourShapes(BlockState $$0, LevelAccessor $$1, BlockPos $$2) {
        BlockState $$3 = $$0;
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (Direction $$5 : UPDATE_SHAPE_ORDER) {
            $$4.setWithOffset((Vec3i)$$2, $$5);
            $$3 = $$3.updateShape($$1, $$1, $$2, $$5, $$4, $$1.getBlockState($$4), $$1.getRandom());
        }
        return $$3;
    }

    public static void updateOrDestroy(BlockState $$0, BlockState $$1, LevelAccessor $$2, BlockPos $$3, int $$4) {
        Block.updateOrDestroy($$0, $$1, $$2, $$3, $$4, 512);
    }

    public static void updateOrDestroy(BlockState $$0, BlockState $$1, LevelAccessor $$2, BlockPos $$3, int $$4, int $$5) {
        if ($$1 != $$0) {
            if ($$1.isAir()) {
                if (!$$2.isClientSide()) {
                    $$2.destroyBlock($$3, ($$4 & 0x20) == 0, null, $$5);
                }
            } else {
                $$2.setBlock($$3, $$1, $$4 & 0xFFFFFFDF, $$5);
            }
        }
    }

    public Block(BlockBehaviour.Properties $$0) {
        super($$0);
        String $$2;
        StateDefinition.Builder<Block, BlockState> $$1 = new StateDefinition.Builder<Block, BlockState>(this);
        this.createBlockStateDefinition($$1);
        this.stateDefinition = $$1.create(Block::defaultBlockState, BlockState::new);
        this.registerDefaultState(this.stateDefinition.any());
        if (SharedConstants.IS_RUNNING_IN_IDE && !($$2 = this.getClass().getSimpleName()).endsWith("Block")) {
            LOGGER.error("Block classes should end with Block and {} doesn't.", (Object)$$2);
        }
    }

    public static boolean isExceptionForConnection(BlockState $$0) {
        return $$0.getBlock() instanceof LeavesBlock || $$0.is(Blocks.BARRIER) || $$0.is(Blocks.CARVED_PUMPKIN) || $$0.is(Blocks.JACK_O_LANTERN) || $$0.is(Blocks.MELON) || $$0.is(Blocks.PUMPKIN) || $$0.is(BlockTags.SHULKER_BOXES);
    }

    public static boolean shouldRenderFace(BlockState $$0, BlockState $$1, Direction $$2) {
        VoxelShape $$3 = $$1.getFaceOcclusionShape($$2.getOpposite());
        if ($$3 == Shapes.block()) {
            return false;
        }
        if ($$0.skipRendering($$1, $$2)) {
            return false;
        }
        if ($$3 == Shapes.empty()) {
            return true;
        }
        VoxelShape $$4 = $$0.getFaceOcclusionShape($$2);
        if ($$4 == Shapes.empty()) {
            return true;
        }
        ShapePairKey $$5 = new ShapePairKey($$4, $$3);
        Object2ByteLinkedOpenHashMap<ShapePairKey> $$6 = OCCLUSION_CACHE.get();
        byte $$7 = $$6.getAndMoveToFirst((Object)$$5);
        if ($$7 != 127) {
            return $$7 != 0;
        }
        boolean $$8 = Shapes.joinIsNotEmpty($$4, $$3, BooleanOp.ONLY_FIRST);
        if ($$6.size() == 256) {
            $$6.removeLastByte();
        }
        $$6.putAndMoveToFirst((Object)$$5, (byte)($$8 ? 1 : 0));
        return $$8;
    }

    public static boolean canSupportRigidBlock(BlockGetter $$0, BlockPos $$1) {
        return $$0.getBlockState($$1).isFaceSturdy($$0, $$1, Direction.UP, SupportType.RIGID);
    }

    public static boolean canSupportCenter(LevelReader $$0, BlockPos $$1, Direction $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        if ($$2 == Direction.DOWN && $$3.is(BlockTags.UNSTABLE_BOTTOM_CENTER)) {
            return false;
        }
        return $$3.isFaceSturdy($$0, $$1, $$2, SupportType.CENTER);
    }

    public static boolean isFaceFull(VoxelShape $$0, Direction $$1) {
        VoxelShape $$2 = $$0.getFaceShape($$1);
        return Block.isShapeFullBlock($$2);
    }

    public static boolean isShapeFullBlock(VoxelShape $$0) {
        return SHAPE_FULL_BLOCK_CACHE.getUnchecked($$0);
    }

    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
    }

    public void destroy(LevelAccessor $$0, BlockPos $$1, BlockState $$2) {
    }

    public static List<ItemStack> getDrops(BlockState $$0, ServerLevel $$1, BlockPos $$2, @Nullable BlockEntity $$3) {
        LootParams.Builder $$4 = new LootParams.Builder($$1).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf($$2)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, $$3);
        return $$0.getDrops($$4);
    }

    public static List<ItemStack> getDrops(BlockState $$0, ServerLevel $$1, BlockPos $$2, @Nullable BlockEntity $$3, @Nullable Entity $$4, ItemStack $$5) {
        LootParams.Builder $$6 = new LootParams.Builder($$1).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf($$2)).withParameter(LootContextParams.TOOL, $$5).withOptionalParameter(LootContextParams.THIS_ENTITY, $$4).withOptionalParameter(LootContextParams.BLOCK_ENTITY, $$3);
        return $$0.getDrops($$6);
    }

    public static void dropResources(BlockState $$0, Level $$1, BlockPos $$22) {
        if ($$1 instanceof ServerLevel) {
            Block.getDrops($$0, (ServerLevel)$$1, $$22, null).forEach($$2 -> Block.popResource($$1, $$22, $$2));
            $$0.spawnAfterBreak((ServerLevel)$$1, $$22, ItemStack.EMPTY, true);
        }
    }

    public static void dropResources(BlockState $$0, LevelAccessor $$1, BlockPos $$22, @Nullable BlockEntity $$3) {
        if ($$1 instanceof ServerLevel) {
            Block.getDrops($$0, (ServerLevel)$$1, $$22, $$3).forEach($$2 -> Block.popResource((Level)((ServerLevel)$$1), $$22, $$2));
            $$0.spawnAfterBreak((ServerLevel)$$1, $$22, ItemStack.EMPTY, true);
        }
    }

    public static void dropResources(BlockState $$0, Level $$1, BlockPos $$22, @Nullable BlockEntity $$3, @Nullable Entity $$4, ItemStack $$5) {
        if ($$1 instanceof ServerLevel) {
            Block.getDrops($$0, (ServerLevel)$$1, $$22, $$3, $$4, $$5).forEach($$2 -> Block.popResource($$1, $$22, $$2));
            $$0.spawnAfterBreak((ServerLevel)$$1, $$22, $$5, true);
        }
    }

    public static void popResource(Level $$0, BlockPos $$1, ItemStack $$2) {
        double $$3 = (double)EntityType.ITEM.getHeight() / 2.0;
        double $$4 = (double)$$1.getX() + 0.5 + Mth.nextDouble($$0.random, -0.25, 0.25);
        double $$5 = (double)$$1.getY() + 0.5 + Mth.nextDouble($$0.random, -0.25, 0.25) - $$3;
        double $$6 = (double)$$1.getZ() + 0.5 + Mth.nextDouble($$0.random, -0.25, 0.25);
        Block.popResource($$0, () -> new ItemEntity($$0, $$4, $$5, $$6, $$2), $$2);
    }

    public static void popResourceFromFace(Level $$0, BlockPos $$1, Direction $$2, ItemStack $$3) {
        int $$4 = $$2.getStepX();
        int $$5 = $$2.getStepY();
        int $$6 = $$2.getStepZ();
        double $$7 = (double)EntityType.ITEM.getWidth() / 2.0;
        double $$8 = (double)EntityType.ITEM.getHeight() / 2.0;
        double $$9 = (double)$$1.getX() + 0.5 + ($$4 == 0 ? Mth.nextDouble($$0.random, -0.25, 0.25) : (double)$$4 * (0.5 + $$7));
        double $$10 = (double)$$1.getY() + 0.5 + ($$5 == 0 ? Mth.nextDouble($$0.random, -0.25, 0.25) : (double)$$5 * (0.5 + $$8)) - $$8;
        double $$11 = (double)$$1.getZ() + 0.5 + ($$6 == 0 ? Mth.nextDouble($$0.random, -0.25, 0.25) : (double)$$6 * (0.5 + $$7));
        double $$12 = $$4 == 0 ? Mth.nextDouble($$0.random, -0.1, 0.1) : (double)$$4 * 0.1;
        double $$13 = $$5 == 0 ? Mth.nextDouble($$0.random, 0.0, 0.1) : (double)$$5 * 0.1 + 0.1;
        double $$14 = $$6 == 0 ? Mth.nextDouble($$0.random, -0.1, 0.1) : (double)$$6 * 0.1;
        Block.popResource($$0, () -> new ItemEntity($$0, $$9, $$10, $$11, $$3, $$12, $$13, $$14), $$3);
    }

    private static void popResource(Level $$0, Supplier<ItemEntity> $$1, ItemStack $$2) {
        block3: {
            block2: {
                if (!($$0 instanceof ServerLevel)) break block2;
                ServerLevel $$3 = (ServerLevel)$$0;
                if (!$$2.isEmpty() && $$3.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) break block3;
            }
            return;
        }
        ItemEntity $$5 = $$1.get();
        $$5.setDefaultPickUpDelay();
        $$0.addFreshEntity($$5);
    }

    protected void popExperience(ServerLevel $$0, BlockPos $$1, int $$2) {
        if ($$0.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            ExperienceOrb.award($$0, Vec3.atCenterOf($$1), $$2);
        }
    }

    public float getExplosionResistance() {
        return this.explosionResistance;
    }

    public void wasExploded(ServerLevel $$0, BlockPos $$1, Explosion $$2) {
    }

    public void stepOn(Level $$0, BlockPos $$1, BlockState $$2, Entity $$3) {
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return this.defaultBlockState();
    }

    public void playerDestroy(Level $$0, Player $$1, BlockPos $$2, BlockState $$3, @Nullable BlockEntity $$4, ItemStack $$5) {
        $$1.awardStat(Stats.BLOCK_MINED.get(this));
        $$1.causeFoodExhaustion(0.005f);
        Block.dropResources($$3, $$0, $$2, $$4, $$1, $$5);
    }

    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
    }

    public boolean isPossibleToRespawnInThis(BlockState $$0) {
        return !$$0.isSolid() && !$$0.liquid();
    }

    public MutableComponent getName() {
        return Component.translatable(this.getDescriptionId());
    }

    public void fallOn(Level $$0, BlockState $$1, BlockPos $$2, Entity $$3, double $$4) {
        $$3.causeFallDamage($$4, 1.0f, $$3.damageSources().fall());
    }

    public void updateEntityMovementAfterFallOn(BlockGetter $$0, Entity $$1) {
        $$1.setDeltaMovement($$1.getDeltaMovement().multiply(1.0, 0.0, 1.0));
    }

    public float getFriction() {
        return this.friction;
    }

    public float getSpeedFactor() {
        return this.speedFactor;
    }

    public float getJumpFactor() {
        return this.jumpFactor;
    }

    protected void spawnDestroyParticles(Level $$0, Player $$1, BlockPos $$2, BlockState $$3) {
        $$0.levelEvent($$1, 2001, $$2, Block.getId($$3));
    }

    public BlockState playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        this.spawnDestroyParticles($$0, $$3, $$1, $$2);
        if ($$2.is(BlockTags.GUARDED_BY_PIGLINS) && $$0 instanceof ServerLevel) {
            ServerLevel $$4 = (ServerLevel)$$0;
            PiglinAi.angerNearbyPiglins($$4, $$3, false);
        }
        $$0.gameEvent(GameEvent.BLOCK_DESTROY, $$1, GameEvent.Context.of($$3, $$2));
        return $$2;
    }

    public void handlePrecipitation(BlockState $$0, Level $$1, BlockPos $$2, Biome.Precipitation $$3) {
    }

    public boolean dropFromExplosion(Explosion $$0) {
        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
    }

    public StateDefinition<Block, BlockState> getStateDefinition() {
        return this.stateDefinition;
    }

    protected final void registerDefaultState(BlockState $$0) {
        this.defaultBlockState = $$0;
    }

    public final BlockState defaultBlockState() {
        return this.defaultBlockState;
    }

    public final BlockState withPropertiesOf(BlockState $$0) {
        BlockState $$1 = this.defaultBlockState();
        for (Property<?> $$2 : $$0.getBlock().getStateDefinition().getProperties()) {
            if (!$$1.hasProperty($$2)) continue;
            $$1 = Block.copyProperty($$0, $$1, $$2);
        }
        return $$1;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState $$0, BlockState $$1, Property<T> $$2) {
        return (BlockState)$$1.setValue($$2, $$0.getValue($$2));
    }

    @Override
    public Item asItem() {
        if (this.item == null) {
            this.item = Item.byBlock(this);
        }
        return this.item;
    }

    public boolean hasDynamicShape() {
        return this.dynamicShape;
    }

    public String toString() {
        return "Block{" + BuiltInRegistries.BLOCK.wrapAsHolder(this).getRegisteredName() + "}";
    }

    @Override
    protected Block asBlock() {
        return this;
    }

    protected Function<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> $$0) {
        return this.stateDefinition.getPossibleStates().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), $$0))::get;
    }

    protected Function<BlockState, VoxelShape> a(Function<BlockState, VoxelShape> $$02, Property<?> ... $$1) {
        Map<Property, Object> $$22 = Arrays.stream($$1).collect(Collectors.toMap($$0 -> $$0, $$0 -> $$0.getPossibleValues().getFirst()));
        ImmutableMap $$3 = this.stateDefinition.getPossibleStates().stream().filter($$12 -> $$22.entrySet().stream().allMatch($$1 -> $$12.getValue((Property)$$1.getKey()) == $$1.getValue())).collect(ImmutableMap.toImmutableMap(Function.identity(), $$02));
        return $$2 -> {
            for (Map.Entry $$3 : $$22.entrySet()) {
                $$2 = Block.setValueHelper($$2, (Property)$$3.getKey(), $$3.getValue());
            }
            return (VoxelShape)$$3.get($$2);
        };
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S $$0, Property<T> $$1, Object $$2) {
        return (S)((StateHolder)$$0.setValue($$1, (Comparable)((Comparable)$$2)));
    }

    @Deprecated
    public Holder.Reference<Block> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    protected void tryDropExperience(ServerLevel $$0, BlockPos $$1, ItemStack $$2, IntProvider $$3) {
        int $$4 = EnchantmentHelper.processBlockExperience($$0, $$2, $$3.sample($$0.getRandom()));
        if ($$4 > 0) {
            this.popExperience($$0, $$1, $$4);
        }
    }

    record ShapePairKey(VoxelShape first, VoxelShape second) {
        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public boolean equals(Object $$0) {
            if (!($$0 instanceof ShapePairKey)) return false;
            ShapePairKey $$1 = (ShapePairKey)((Object)$$0);
            if (this.first != $$1.first) return false;
            if (this.second != $$1.second) return false;
            return true;
        }

        public int hashCode() {
            return System.identityHashCode(this.first) * 31 + System.identityHashCode(this.second);
        }
    }
}

