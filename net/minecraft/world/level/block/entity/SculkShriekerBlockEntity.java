/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.world.level.block.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class SculkShriekerBlockEntity
extends BlockEntity
implements GameEventListener.Provider<VibrationSystem.Listener>,
VibrationSystem {
    private static final int WARNING_SOUND_RADIUS = 10;
    private static final int WARDEN_SPAWN_ATTEMPTS = 20;
    private static final int WARDEN_SPAWN_RANGE_XZ = 5;
    private static final int WARDEN_SPAWN_RANGE_Y = 6;
    private static final int DARKNESS_RADIUS = 40;
    private static final int SHRIEKING_TICKS = 90;
    private static final Int2ObjectMap<SoundEvent> SOUND_BY_LEVEL = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), $$0 -> {
        $$0.put(1, (Object)SoundEvents.WARDEN_NEARBY_CLOSE);
        $$0.put(2, (Object)SoundEvents.WARDEN_NEARBY_CLOSER);
        $$0.put(3, (Object)SoundEvents.WARDEN_NEARBY_CLOSEST);
        $$0.put(4, (Object)SoundEvents.WARDEN_LISTENING_ANGRY);
    });
    private static final int DEFAULT_WARNING_LEVEL = 0;
    private int warningLevel = 0;
    private final VibrationSystem.User vibrationUser = new VibrationUser();
    private VibrationSystem.Data vibrationData = new VibrationSystem.Data();
    private final VibrationSystem.Listener vibrationListener = new VibrationSystem.Listener(this);

    public SculkShriekerBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.SCULK_SHRIEKER, $$0, $$1);
    }

    @Override
    public VibrationSystem.Data getVibrationData() {
        return this.vibrationData;
    }

    @Override
    public VibrationSystem.User getVibrationUser() {
        return this.vibrationUser;
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        this.warningLevel = $$0.getIntOr("warning_level", 0);
        this.vibrationData = $$0.read("listener", VibrationSystem.Data.CODEC).orElseGet(VibrationSystem.Data::new);
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        $$0.putInt("warning_level", this.warningLevel);
        $$0.store("listener", VibrationSystem.Data.CODEC, this.vibrationData);
    }

    @Nullable
    public static ServerPlayer tryGetPlayer(@Nullable Entity $$0) {
        ItemEntity $$5;
        Projectile $$3;
        Entity entity;
        LivingEntity livingEntity;
        if ($$0 instanceof ServerPlayer) {
            ServerPlayer $$1 = (ServerPlayer)$$0;
            return $$1;
        }
        if ($$0 != null && (livingEntity = $$0.getControllingPassenger()) instanceof ServerPlayer) {
            ServerPlayer $$2 = (ServerPlayer)livingEntity;
            return $$2;
        }
        if ($$0 instanceof Projectile && (entity = ($$3 = (Projectile)$$0).getOwner()) instanceof ServerPlayer) {
            ServerPlayer $$4 = (ServerPlayer)entity;
            return $$4;
        }
        if ($$0 instanceof ItemEntity && (entity = ($$5 = (ItemEntity)$$0).getOwner()) instanceof ServerPlayer) {
            ServerPlayer $$6 = (ServerPlayer)entity;
            return $$6;
        }
        return null;
    }

    public void tryShriek(ServerLevel $$0, @Nullable ServerPlayer $$1) {
        if ($$1 == null) {
            return;
        }
        BlockState $$2 = this.getBlockState();
        if ($$2.getValue(SculkShriekerBlock.SHRIEKING).booleanValue()) {
            return;
        }
        this.warningLevel = 0;
        if (this.canRespond($$0) && !this.tryToWarn($$0, $$1)) {
            return;
        }
        this.shriek($$0, $$1);
    }

    private boolean tryToWarn(ServerLevel $$02, ServerPlayer $$1) {
        OptionalInt $$2 = WardenSpawnTracker.tryWarn($$02, this.getBlockPos(), $$1);
        $$2.ifPresent($$0 -> {
            this.warningLevel = $$0;
        });
        return $$2.isPresent();
    }

    private void shriek(ServerLevel $$0, @Nullable Entity $$1) {
        BlockPos $$2 = this.getBlockPos();
        BlockState $$3 = this.getBlockState();
        $$0.setBlock($$2, (BlockState)$$3.setValue(SculkShriekerBlock.SHRIEKING, true), 2);
        $$0.scheduleTick($$2, $$3.getBlock(), 90);
        $$0.levelEvent(3007, $$2, 0);
        $$0.gameEvent(GameEvent.SHRIEK, $$2, GameEvent.Context.of($$1));
    }

    private boolean canRespond(ServerLevel $$0) {
        return this.getBlockState().getValue(SculkShriekerBlock.CAN_SUMMON) != false && $$0.getDifficulty() != Difficulty.PEACEFUL && $$0.getGameRules().getBoolean(GameRules.RULE_DO_WARDEN_SPAWNING);
    }

    @Override
    public void preRemoveSideEffects(BlockPos $$0, BlockState $$1) {
        Level level;
        if ($$1.getValue(SculkShriekerBlock.SHRIEKING).booleanValue() && (level = this.level) instanceof ServerLevel) {
            ServerLevel $$2 = (ServerLevel)level;
            this.tryRespond($$2);
        }
    }

    public void tryRespond(ServerLevel $$0) {
        if (this.canRespond($$0) && this.warningLevel > 0) {
            if (!this.trySummonWarden($$0)) {
                this.playWardenReplySound($$0);
            }
            Warden.applyDarknessAround($$0, Vec3.atCenterOf(this.getBlockPos()), null, 40);
        }
    }

    private void playWardenReplySound(Level $$0) {
        SoundEvent $$1 = (SoundEvent)((Object)SOUND_BY_LEVEL.get(this.warningLevel));
        if ($$1 != null) {
            BlockPos $$2 = this.getBlockPos();
            int $$3 = $$2.getX() + Mth.randomBetweenInclusive($$0.random, -10, 10);
            int $$4 = $$2.getY() + Mth.randomBetweenInclusive($$0.random, -10, 10);
            int $$5 = $$2.getZ() + Mth.randomBetweenInclusive($$0.random, -10, 10);
            $$0.playSound(null, (double)$$3, (double)$$4, (double)$$5, $$1, SoundSource.HOSTILE, 5.0f, 1.0f);
        }
    }

    private boolean trySummonWarden(ServerLevel $$0) {
        if (this.warningLevel < 4) {
            return false;
        }
        return SpawnUtil.trySpawnMob(EntityType.WARDEN, EntitySpawnReason.TRIGGERED, $$0, this.getBlockPos(), 20, 5, 6, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER, false).isPresent();
    }

    @Override
    public VibrationSystem.Listener getListener() {
        return this.vibrationListener;
    }

    @Override
    public /* synthetic */ GameEventListener getListener() {
        return this.getListener();
    }

    class VibrationUser
    implements VibrationSystem.User {
        private static final int LISTENER_RADIUS = 8;
        private final PositionSource positionSource;

        public VibrationUser() {
            this.positionSource = new BlockPositionSource(SculkShriekerBlockEntity.this.worldPosition);
        }

        @Override
        public int getListenerRadius() {
            return 8;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.SHRIEKER_CAN_LISTEN;
        }

        @Override
        public boolean canReceiveVibration(ServerLevel $$0, BlockPos $$1, Holder<GameEvent> $$2, GameEvent.Context $$3) {
            return SculkShriekerBlockEntity.this.getBlockState().getValue(SculkShriekerBlock.SHRIEKING) == false && SculkShriekerBlockEntity.tryGetPlayer($$3.sourceEntity()) != null;
        }

        @Override
        public void onReceiveVibration(ServerLevel $$0, BlockPos $$1, Holder<GameEvent> $$2, @Nullable Entity $$3, @Nullable Entity $$4, float $$5) {
            SculkShriekerBlockEntity.this.tryShriek($$0, SculkShriekerBlockEntity.tryGetPlayer($$4 != null ? $$4 : $$3));
        }

        @Override
        public void onDataChanged() {
            SculkShriekerBlockEntity.this.setChanged();
        }

        @Override
        public boolean requiresAdjacentChunksToBeTicking() {
            return true;
        }
    }
}

