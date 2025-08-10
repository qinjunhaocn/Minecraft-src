/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.world.level.block.entity;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.CreakingHeartState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;

public class CreakingHeartBlockEntity
extends BlockEntity {
    private static final int PLAYER_DETECTION_RANGE = 32;
    public static final int CREAKING_ROAMING_RADIUS = 32;
    private static final int DISTANCE_CREAKING_TOO_FAR = 34;
    private static final int SPAWN_RANGE_XZ = 16;
    private static final int SPAWN_RANGE_Y = 8;
    private static final int ATTEMPTS_PER_SPAWN = 5;
    private static final int UPDATE_TICKS = 20;
    private static final int UPDATE_TICKS_VARIANCE = 5;
    private static final int HURT_CALL_TOTAL_TICKS = 100;
    private static final int NUMBER_OF_HURT_CALLS = 10;
    private static final int HURT_CALL_INTERVAL = 10;
    private static final int HURT_CALL_PARTICLE_TICKS = 50;
    private static final int MAX_DEPTH = 2;
    private static final int MAX_COUNT = 64;
    private static final int TICKS_GRACE_PERIOD = 30;
    private static final Optional<Creaking> NO_CREAKING = Optional.empty();
    @Nullable
    private Either<Creaking, UUID> creakingInfo;
    private long ticksExisted;
    private int ticker;
    private int emitter;
    @Nullable
    private Vec3 emitterTarget;
    private int outputSignal;

    public CreakingHeartBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.CREAKING_HEART, $$0, $$1);
    }

    /*
     * WARNING - void declaration
     */
    public static void serverTick(Level $$0, BlockPos $$12, BlockState $$2, CreakingHeartBlockEntity $$3) {
        Creaking $$16;
        void $$5;
        ++$$3.ticksExisted;
        if (!($$0 instanceof ServerLevel)) {
            return;
        }
        ServerLevel $$4 = (ServerLevel)$$0;
        int $$6 = $$3.computeAnalogOutputSignal();
        if ($$3.outputSignal != $$6) {
            $$3.outputSignal = $$6;
            $$0.updateNeighbourForOutputSignal($$12, Blocks.CREAKING_HEART);
        }
        if ($$3.emitter > 0) {
            if ($$3.emitter > 50) {
                $$3.emitParticles((ServerLevel)$$5, 1, true);
                $$3.emitParticles((ServerLevel)$$5, 1, false);
            }
            if ($$3.emitter % 10 == 0 && $$3.emitterTarget != null) {
                $$3.getCreakingProtector().ifPresent($$1 -> {
                    $$0.emitterTarget = $$1.getBoundingBox().getCenter();
                });
                Vec3 $$7 = Vec3.atCenterOf($$12);
                float $$8 = 0.2f + 0.8f * (float)(100 - $$3.emitter) / 100.0f;
                Vec3 $$9 = $$7.subtract($$3.emitterTarget).scale($$8).add($$3.emitterTarget);
                BlockPos $$10 = BlockPos.containing($$9);
                float $$11 = (float)$$3.emitter / 2.0f / 100.0f + 0.5f;
                $$5.playSound(null, $$10, SoundEvents.CREAKING_HEART_HURT, SoundSource.BLOCKS, $$11, 1.0f);
            }
            --$$3.emitter;
        }
        if ($$3.ticker-- >= 0) {
            return;
        }
        $$3.ticker = $$3.level == null ? 20 : $$3.level.random.nextInt(5) + 20;
        BlockState $$122 = CreakingHeartBlockEntity.updateCreakingState($$0, $$2, $$12, $$3);
        if ($$122 != $$2) {
            $$0.setBlock($$12, $$122, 3);
            if ($$122.getValue(CreakingHeartBlock.STATE) == CreakingHeartState.UPROOTED) {
                return;
            }
        }
        if ($$3.creakingInfo != null) {
            Optional<Creaking> $$13 = $$3.getCreakingProtector();
            if ($$13.isPresent()) {
                Creaking $$14 = $$13.get();
                if (!CreakingHeartBlock.isNaturalNight($$0) && !$$14.isPersistenceRequired() || $$3.distanceToCreaking() > 34.0 || $$14.playerIsStuckInYou()) {
                    $$3.removeProtector(null);
                }
            }
            return;
        }
        if ($$122.getValue(CreakingHeartBlock.STATE) != CreakingHeartState.AWAKE) {
            return;
        }
        if ($$0.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (!$$5.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return;
        }
        Player $$15 = $$0.getNearestPlayer((double)$$12.getX(), (double)$$12.getY(), (double)$$12.getZ(), 32.0, false);
        if ($$15 != null && ($$16 = CreakingHeartBlockEntity.spawnProtector((ServerLevel)$$5, $$3)) != null) {
            $$3.setCreakingInfo($$16);
            $$16.makeSound(SoundEvents.CREAKING_SPAWN);
            $$0.playSound(null, $$3.getBlockPos(), SoundEvents.CREAKING_HEART_SPAWN, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    private static BlockState updateCreakingState(Level $$0, BlockState $$1, BlockPos $$2, CreakingHeartBlockEntity $$3) {
        if (!CreakingHeartBlock.hasRequiredLogs($$1, $$0, $$2) && $$3.creakingInfo == null) {
            return (BlockState)$$1.setValue(CreakingHeartBlock.STATE, CreakingHeartState.UPROOTED);
        }
        boolean $$4 = CreakingHeartBlock.isNaturalNight($$0);
        return (BlockState)$$1.setValue(CreakingHeartBlock.STATE, $$4 ? CreakingHeartState.AWAKE : CreakingHeartState.DORMANT);
    }

    private double distanceToCreaking() {
        return this.getCreakingProtector().map($$0 -> Math.sqrt($$0.distanceToSqr(Vec3.atBottomCenterOf(this.getBlockPos())))).orElse(0.0);
    }

    private void clearCreakingInfo() {
        this.creakingInfo = null;
        this.setChanged();
    }

    public void setCreakingInfo(Creaking $$0) {
        this.creakingInfo = Either.left((Object)$$0);
        this.setChanged();
    }

    public void setCreakingInfo(UUID $$0) {
        this.creakingInfo = Either.right((Object)$$0);
        this.ticksExisted = 0L;
        this.setChanged();
    }

    private Optional<Creaking> getCreakingProtector() {
        Level level;
        if (this.creakingInfo == null) {
            return NO_CREAKING;
        }
        if (this.creakingInfo.left().isPresent()) {
            Creaking $$0 = (Creaking)this.creakingInfo.left().get();
            if (!$$0.isRemoved()) {
                return Optional.of($$0);
            }
            this.setCreakingInfo($$0.getUUID());
        }
        if ((level = this.level) instanceof ServerLevel) {
            ServerLevel $$1 = (ServerLevel)level;
            if (this.creakingInfo.right().isPresent()) {
                UUID $$2 = (UUID)this.creakingInfo.right().get();
                Entity $$3 = $$1.getEntity($$2);
                if ($$3 instanceof Creaking) {
                    Creaking $$4 = (Creaking)$$3;
                    this.setCreakingInfo($$4);
                    return Optional.of($$4);
                }
                if (this.ticksExisted >= 30L) {
                    this.clearCreakingInfo();
                }
                return NO_CREAKING;
            }
        }
        return NO_CREAKING;
    }

    @Nullable
    private static Creaking spawnProtector(ServerLevel $$0, CreakingHeartBlockEntity $$1) {
        BlockPos $$2 = $$1.getBlockPos();
        Optional<Creaking> $$3 = SpawnUtil.trySpawnMob(EntityType.CREAKING, EntitySpawnReason.SPAWNER, $$0, $$2, 5, 16, 8, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER_NO_LEAVES, true);
        if ($$3.isEmpty()) {
            return null;
        }
        Creaking $$4 = $$3.get();
        $$0.gameEvent((Entity)$$4, GameEvent.ENTITY_PLACE, $$4.position());
        $$0.broadcastEntityEvent($$4, (byte)60);
        $$4.setTransient($$2);
        return $$4;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider $$0) {
        return this.saveCustomOnly($$0);
    }

    /*
     * WARNING - void declaration
     */
    public void creakingHurt() {
        void $$1;
        void $$3;
        Object var2_1 = this.getCreakingProtector().orElse(null);
        if (!(var2_1 instanceof Creaking)) {
            return;
        }
        Creaking $$02 = var2_1;
        Level level = this.level;
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel $$2 = (ServerLevel)level;
        if (this.emitter > 0) {
            return;
        }
        this.emitParticles((ServerLevel)$$3, 20, false);
        if (this.getBlockState().getValue(CreakingHeartBlock.STATE) == CreakingHeartState.AWAKE) {
            int $$4 = this.level.getRandom().nextIntBetweenInclusive(2, 3);
            for (int $$5 = 0; $$5 < $$4; ++$$5) {
                this.spreadResin().ifPresent($$0 -> {
                    this.level.playSound(null, (BlockPos)$$0, SoundEvents.RESIN_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    this.level.gameEvent((Holder<GameEvent>)GameEvent.BLOCK_PLACE, (BlockPos)$$0, GameEvent.Context.of(this.getBlockState()));
                });
            }
        }
        this.emitter = 100;
        this.emitterTarget = $$1.getBoundingBox().getCenter();
    }

    private Optional<BlockPos> spreadResin() {
        MutableObject<Object> $$02 = new MutableObject<Object>(null);
        BlockPos.breadthFirstTraversal(this.worldPosition, 2, 64, ($$0, $$1) -> {
            for (Direction $$2 : Util.b(Direction.values(), this.level.random)) {
                BlockPos $$3 = $$0.relative($$2);
                if (!this.level.getBlockState($$3).is(BlockTags.PALE_OAK_LOGS)) continue;
                $$1.accept($$3);
            }
        }, $$1 -> {
            if (!this.level.getBlockState((BlockPos)$$1).is(BlockTags.PALE_OAK_LOGS)) {
                return BlockPos.TraversalNodeStatus.ACCEPT;
            }
            for (Direction $$2 : Util.b(Direction.values(), this.level.random)) {
                BlockPos $$3 = $$1.relative($$2);
                BlockState $$4 = this.level.getBlockState($$3);
                Direction $$5 = $$2.getOpposite();
                if ($$4.isAir()) {
                    $$4 = Blocks.RESIN_CLUMP.defaultBlockState();
                } else if ($$4.is(Blocks.WATER) && $$4.getFluidState().isSource()) {
                    $$4 = (BlockState)Blocks.RESIN_CLUMP.defaultBlockState().setValue(MultifaceBlock.WATERLOGGED, true);
                }
                if (!$$4.is(Blocks.RESIN_CLUMP) || MultifaceBlock.hasFace($$4, $$5)) continue;
                this.level.setBlock($$3, (BlockState)$$4.setValue(MultifaceBlock.getFaceProperty($$5), true), 3);
                $$02.setValue($$3);
                return BlockPos.TraversalNodeStatus.STOP;
            }
            return BlockPos.TraversalNodeStatus.ACCEPT;
        });
        return Optional.ofNullable((BlockPos)$$02.getValue());
    }

    /*
     * WARNING - void declaration
     */
    private void emitParticles(ServerLevel $$0, int $$1, boolean $$2) {
        Object var5_4 = this.getCreakingProtector().orElse(null);
        if (!(var5_4 instanceof Creaking)) {
            return;
        }
        Creaking $$3 = var5_4;
        int $$5 = $$2 ? 16545810 : 0x5F5F5F;
        RandomSource $$6 = $$0.random;
        for (double $$7 = 0.0; $$7 < (double)$$1; $$7 += 1.0) {
            void $$4;
            AABB $$8 = $$4.getBoundingBox();
            Vec3 $$9 = $$8.getMinPosition().add($$6.nextDouble() * $$8.getXsize(), $$6.nextDouble() * $$8.getYsize(), $$6.nextDouble() * $$8.getZsize());
            Vec3 $$10 = Vec3.atLowerCornerOf(this.getBlockPos()).add($$6.nextDouble(), $$6.nextDouble(), $$6.nextDouble());
            if ($$2) {
                Vec3 $$11 = $$9;
                $$9 = $$10;
                $$10 = $$11;
            }
            TrailParticleOption $$12 = new TrailParticleOption($$10, $$5, $$6.nextInt(40) + 10);
            $$0.sendParticles($$12, true, true, $$9.x, $$9.y, $$9.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos $$0, BlockState $$1) {
        this.removeProtector(null);
    }

    public void removeProtector(@Nullable DamageSource $$0) {
        Object var3_2 = this.getCreakingProtector().orElse(null);
        if (var3_2 instanceof Creaking) {
            Creaking $$1 = var3_2;
            if ($$0 == null) {
                $$1.tearDown();
            } else {
                $$1.creakingDeathEffects($$0);
                $$1.setTearingDown();
                $$1.setHealth(0.0f);
            }
            this.clearCreakingInfo();
        }
    }

    public boolean isProtector(Creaking $$0) {
        return this.getCreakingProtector().map($$1 -> $$1 == $$0).orElse(false);
    }

    public int getAnalogOutputSignal() {
        return this.outputSignal;
    }

    public int computeAnalogOutputSignal() {
        if (this.creakingInfo == null || this.getCreakingProtector().isEmpty()) {
            return 0;
        }
        double $$0 = this.distanceToCreaking();
        double $$1 = Math.clamp((double)$$0, (double)0.0, (double)32.0) / 32.0;
        return 15 - (int)Math.floor($$1 * 15.0);
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        $$0.read("creaking", UUIDUtil.CODEC).ifPresentOrElse(this::setCreakingInfo, this::clearCreakingInfo);
    }

    @Override
    protected void saveAdditional(ValueOutput $$02) {
        super.saveAdditional($$02);
        if (this.creakingInfo != null) {
            $$02.store("creaking", UUIDUtil.CODEC, (UUID)this.creakingInfo.map(Entity::getUUID, $$0 -> $$0));
        }
    }

    public /* synthetic */ Packet getUpdatePacket() {
        return this.getUpdatePacket();
    }
}

