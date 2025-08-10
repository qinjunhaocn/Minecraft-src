/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap
 */
package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSelector;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public interface VibrationSystem {
    public static final List<ResourceKey<GameEvent>> RESONANCE_EVENTS = List.of((Object[])new ResourceKey[]{GameEvent.RESONATE_1.key(), GameEvent.RESONATE_2.key(), GameEvent.RESONATE_3.key(), GameEvent.RESONATE_4.key(), GameEvent.RESONATE_5.key(), GameEvent.RESONATE_6.key(), GameEvent.RESONATE_7.key(), GameEvent.RESONATE_8.key(), GameEvent.RESONATE_9.key(), GameEvent.RESONATE_10.key(), GameEvent.RESONATE_11.key(), GameEvent.RESONATE_12.key(), GameEvent.RESONATE_13.key(), GameEvent.RESONATE_14.key(), GameEvent.RESONATE_15.key()});
    public static final int NO_VIBRATION_FREQUENCY = 0;
    public static final ToIntFunction<ResourceKey<GameEvent>> VIBRATION_FREQUENCY_FOR_EVENT = (ToIntFunction)Util.make(new Reference2IntOpenHashMap(), $$0 -> {
        $$0.defaultReturnValue(0);
        $$0.put(GameEvent.STEP.key(), 1);
        $$0.put(GameEvent.SWIM.key(), 1);
        $$0.put(GameEvent.FLAP.key(), 1);
        $$0.put(GameEvent.PROJECTILE_LAND.key(), 2);
        $$0.put(GameEvent.HIT_GROUND.key(), 2);
        $$0.put(GameEvent.SPLASH.key(), 2);
        $$0.put(GameEvent.ITEM_INTERACT_FINISH.key(), 3);
        $$0.put(GameEvent.PROJECTILE_SHOOT.key(), 3);
        $$0.put(GameEvent.INSTRUMENT_PLAY.key(), 3);
        $$0.put(GameEvent.ENTITY_ACTION.key(), 4);
        $$0.put(GameEvent.ELYTRA_GLIDE.key(), 4);
        $$0.put(GameEvent.UNEQUIP.key(), 4);
        $$0.put(GameEvent.ENTITY_DISMOUNT.key(), 5);
        $$0.put(GameEvent.EQUIP.key(), 5);
        $$0.put(GameEvent.ENTITY_INTERACT.key(), 6);
        $$0.put(GameEvent.SHEAR.key(), 6);
        $$0.put(GameEvent.ENTITY_MOUNT.key(), 6);
        $$0.put(GameEvent.ENTITY_DAMAGE.key(), 7);
        $$0.put(GameEvent.DRINK.key(), 8);
        $$0.put(GameEvent.EAT.key(), 8);
        $$0.put(GameEvent.CONTAINER_CLOSE.key(), 9);
        $$0.put(GameEvent.BLOCK_CLOSE.key(), 9);
        $$0.put(GameEvent.BLOCK_DEACTIVATE.key(), 9);
        $$0.put(GameEvent.BLOCK_DETACH.key(), 9);
        $$0.put(GameEvent.CONTAINER_OPEN.key(), 10);
        $$0.put(GameEvent.BLOCK_OPEN.key(), 10);
        $$0.put(GameEvent.BLOCK_ACTIVATE.key(), 10);
        $$0.put(GameEvent.BLOCK_ATTACH.key(), 10);
        $$0.put(GameEvent.PRIME_FUSE.key(), 10);
        $$0.put(GameEvent.NOTE_BLOCK_PLAY.key(), 10);
        $$0.put(GameEvent.BLOCK_CHANGE.key(), 11);
        $$0.put(GameEvent.BLOCK_DESTROY.key(), 12);
        $$0.put(GameEvent.FLUID_PICKUP.key(), 12);
        $$0.put(GameEvent.BLOCK_PLACE.key(), 13);
        $$0.put(GameEvent.FLUID_PLACE.key(), 13);
        $$0.put(GameEvent.ENTITY_PLACE.key(), 14);
        $$0.put(GameEvent.LIGHTNING_STRIKE.key(), 14);
        $$0.put(GameEvent.TELEPORT.key(), 14);
        $$0.put(GameEvent.ENTITY_DIE.key(), 15);
        $$0.put(GameEvent.EXPLODE.key(), 15);
        for (int $$1 = 1; $$1 <= 15; ++$$1) {
            $$0.put(VibrationSystem.getResonanceEventByFrequency($$1), $$1);
        }
    });

    public Data getVibrationData();

    public User getVibrationUser();

    public static int getGameEventFrequency(Holder<GameEvent> $$0) {
        return $$0.unwrapKey().map(VibrationSystem::getGameEventFrequency).orElse(0);
    }

    public static int getGameEventFrequency(ResourceKey<GameEvent> $$0) {
        return VIBRATION_FREQUENCY_FOR_EVENT.applyAsInt($$0);
    }

    public static ResourceKey<GameEvent> getResonanceEventByFrequency(int $$0) {
        return RESONANCE_EVENTS.get($$0 - 1);
    }

    public static int getRedstoneStrengthForDistance(float $$0, int $$1) {
        double $$2 = 15.0 / (double)$$1;
        return Math.max(1, 15 - Mth.floor($$2 * (double)$$0));
    }

    public static interface User {
        public int getListenerRadius();

        public PositionSource getPositionSource();

        public boolean canReceiveVibration(ServerLevel var1, BlockPos var2, Holder<GameEvent> var3, GameEvent.Context var4);

        public void onReceiveVibration(ServerLevel var1, BlockPos var2, Holder<GameEvent> var3, @Nullable Entity var4, @Nullable Entity var5, float var6);

        default public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.VIBRATIONS;
        }

        default public boolean canTriggerAvoidVibration() {
            return false;
        }

        default public boolean requiresAdjacentChunksToBeTicking() {
            return false;
        }

        default public int calculateTravelTimeInTicks(float $$0) {
            return Mth.floor($$0);
        }

        default public boolean isValidVibration(Holder<GameEvent> $$0, GameEvent.Context $$1) {
            if (!$$0.is(this.getListenableEvents())) {
                return false;
            }
            Entity $$2 = $$1.sourceEntity();
            if ($$2 != null) {
                if ($$2.isSpectator()) {
                    return false;
                }
                if ($$2.isSteppingCarefully() && $$0.is(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                    if (this.canTriggerAvoidVibration() && $$2 instanceof ServerPlayer) {
                        ServerPlayer $$3 = (ServerPlayer)$$2;
                        CriteriaTriggers.AVOID_VIBRATION.trigger($$3);
                    }
                    return false;
                }
                if ($$2.dampensVibrations()) {
                    return false;
                }
            }
            if ($$1.affectedState() != null) {
                return !$$1.affectedState().is(BlockTags.DAMPENS_VIBRATIONS);
            }
            return true;
        }

        default public void onDataChanged() {
        }
    }

    public static interface Ticker {
        /*
         * WARNING - void declaration
         */
        public static void tick(Level $$0, Data $$1, User $$2) {
            void $$4;
            if (!($$0 instanceof ServerLevel)) {
                return;
            }
            ServerLevel $$3 = (ServerLevel)$$0;
            if ($$1.currentVibration == null) {
                Ticker.trySelectAndScheduleVibration((ServerLevel)$$4, $$1, $$2);
            }
            if ($$1.currentVibration == null) {
                return;
            }
            boolean $$5 = $$1.getTravelTimeInTicks() > 0;
            Ticker.tryReloadVibrationParticle((ServerLevel)$$4, $$1, $$2);
            $$1.decrementTravelTime();
            if ($$1.getTravelTimeInTicks() <= 0) {
                $$5 = Ticker.receiveVibration((ServerLevel)$$4, $$1, $$2, $$1.currentVibration);
            }
            if ($$5) {
                $$2.onDataChanged();
            }
        }

        private static void trySelectAndScheduleVibration(ServerLevel $$0, Data $$1, User $$2) {
            $$1.getSelectionStrategy().chosenCandidate($$0.getGameTime()).ifPresent($$3 -> {
                $$1.setCurrentVibration((VibrationInfo)((Object)$$3));
                Vec3 $$4 = $$3.pos();
                $$1.setTravelTimeInTicks($$2.calculateTravelTimeInTicks($$3.distance()));
                $$0.sendParticles(new VibrationParticleOption($$2.getPositionSource(), $$1.getTravelTimeInTicks()), $$4.x, $$4.y, $$4.z, 1, 0.0, 0.0, 0.0, 0.0);
                $$2.onDataChanged();
                $$1.getSelectionStrategy().startOver();
            });
        }

        private static void tryReloadVibrationParticle(ServerLevel $$0, Data $$1, User $$2) {
            double $$11;
            double $$10;
            int $$7;
            double $$8;
            double $$9;
            boolean $$12;
            if (!$$1.shouldReloadVibrationParticle()) {
                return;
            }
            if ($$1.currentVibration == null) {
                $$1.setReloadVibrationParticle(false);
                return;
            }
            Vec3 $$3 = $$1.currentVibration.pos();
            PositionSource $$4 = $$2.getPositionSource();
            Vec3 $$5 = $$4.getPosition($$0).orElse($$3);
            int $$6 = $$1.getTravelTimeInTicks();
            boolean bl = $$12 = $$0.sendParticles(new VibrationParticleOption($$4, $$6), $$9 = Mth.lerp($$8 = 1.0 - (double)$$6 / (double)($$7 = $$2.calculateTravelTimeInTicks($$1.currentVibration.distance())), $$3.x, $$5.x), $$10 = Mth.lerp($$8, $$3.y, $$5.y), $$11 = Mth.lerp($$8, $$3.z, $$5.z), 1, 0.0, 0.0, 0.0, 0.0) > 0;
            if ($$12) {
                $$1.setReloadVibrationParticle(false);
            }
        }

        private static boolean receiveVibration(ServerLevel $$0, Data $$1, User $$2, VibrationInfo $$3) {
            BlockPos $$4 = BlockPos.containing($$3.pos());
            BlockPos $$5 = $$2.getPositionSource().getPosition($$0).map(BlockPos::containing).orElse($$4);
            if ($$2.requiresAdjacentChunksToBeTicking() && !Ticker.areAdjacentChunksTicking($$0, $$5)) {
                return false;
            }
            $$2.onReceiveVibration($$0, $$4, $$3.gameEvent(), $$3.getEntity($$0).orElse(null), $$3.getProjectileOwner($$0).orElse(null), Listener.distanceBetweenInBlocks($$4, $$5));
            $$1.setCurrentVibration(null);
            return true;
        }

        private static boolean areAdjacentChunksTicking(Level $$0, BlockPos $$1) {
            ChunkPos $$2 = new ChunkPos($$1);
            for (int $$3 = $$2.x - 1; $$3 <= $$2.x + 1; ++$$3) {
                for (int $$4 = $$2.z - 1; $$4 <= $$2.z + 1; ++$$4) {
                    if ($$0.shouldTickBlocksAt(ChunkPos.asLong($$3, $$4)) && $$0.getChunkSource().getChunkNow($$3, $$4) != null) continue;
                    return false;
                }
            }
            return true;
        }
    }

    public static class Listener
    implements GameEventListener {
        private final VibrationSystem system;

        public Listener(VibrationSystem $$0) {
            this.system = $$0;
        }

        @Override
        public PositionSource getListenerSource() {
            return this.system.getVibrationUser().getPositionSource();
        }

        @Override
        public int getListenerRadius() {
            return this.system.getVibrationUser().getListenerRadius();
        }

        @Override
        public boolean handleGameEvent(ServerLevel $$0, Holder<GameEvent> $$1, GameEvent.Context $$2, Vec3 $$3) {
            Data $$4 = this.system.getVibrationData();
            User $$5 = this.system.getVibrationUser();
            if ($$4.getCurrentVibration() != null) {
                return false;
            }
            if (!$$5.isValidVibration($$1, $$2)) {
                return false;
            }
            Optional<Vec3> $$6 = $$5.getPositionSource().getPosition($$0);
            if ($$6.isEmpty()) {
                return false;
            }
            Vec3 $$7 = $$6.get();
            if (!$$5.canReceiveVibration($$0, BlockPos.containing($$3), $$1, $$2)) {
                return false;
            }
            if (Listener.isOccluded($$0, $$3, $$7)) {
                return false;
            }
            this.scheduleVibration($$0, $$4, $$1, $$2, $$3, $$7);
            return true;
        }

        public void forceScheduleVibration(ServerLevel $$0, Holder<GameEvent> $$1, GameEvent.Context $$2, Vec3 $$3) {
            this.system.getVibrationUser().getPositionSource().getPosition($$0).ifPresent($$4 -> this.scheduleVibration($$0, this.system.getVibrationData(), $$1, $$2, $$3, (Vec3)$$4));
        }

        private void scheduleVibration(ServerLevel $$0, Data $$1, Holder<GameEvent> $$2, GameEvent.Context $$3, Vec3 $$4, Vec3 $$5) {
            $$1.selectionStrategy.addCandidate(new VibrationInfo($$2, (float)$$4.distanceTo($$5), $$4, $$3.sourceEntity()), $$0.getGameTime());
        }

        public static float distanceBetweenInBlocks(BlockPos $$0, BlockPos $$1) {
            return (float)Math.sqrt($$0.distSqr($$1));
        }

        private static boolean isOccluded(Level $$02, Vec3 $$1, Vec3 $$2) {
            Vec3 $$3 = new Vec3((double)Mth.floor($$1.x) + 0.5, (double)Mth.floor($$1.y) + 0.5, (double)Mth.floor($$1.z) + 0.5);
            Vec3 $$4 = new Vec3((double)Mth.floor($$2.x) + 0.5, (double)Mth.floor($$2.y) + 0.5, (double)Mth.floor($$2.z) + 0.5);
            for (Direction $$5 : Direction.values()) {
                Vec3 $$6 = $$3.relative($$5, 1.0E-5f);
                if ($$02.isBlockInLine(new ClipBlockStateContext($$6, $$4, $$0 -> $$0.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType() == HitResult.Type.BLOCK) continue;
                return false;
            }
            return true;
        }
    }

    public static final class Data {
        public static Codec<Data> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)VibrationInfo.CODEC.lenientOptionalFieldOf("event").forGetter($$0 -> Optional.ofNullable($$0.currentVibration)), (App)VibrationSelector.CODEC.fieldOf("selector").forGetter(Data::getSelectionStrategy), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse((Object)0).forGetter(Data::getTravelTimeInTicks)).apply((Applicative)$$02, ($$0, $$1, $$2) -> new Data($$0.orElse(null), (VibrationSelector)$$1, (int)$$2, true)));
        public static final String NBT_TAG_KEY = "listener";
        @Nullable
        VibrationInfo currentVibration;
        private int travelTimeInTicks;
        final VibrationSelector selectionStrategy;
        private boolean reloadVibrationParticle;

        private Data(@Nullable VibrationInfo $$0, VibrationSelector $$1, int $$2, boolean $$3) {
            this.currentVibration = $$0;
            this.travelTimeInTicks = $$2;
            this.selectionStrategy = $$1;
            this.reloadVibrationParticle = $$3;
        }

        public Data() {
            this(null, new VibrationSelector(), 0, false);
        }

        public VibrationSelector getSelectionStrategy() {
            return this.selectionStrategy;
        }

        @Nullable
        public VibrationInfo getCurrentVibration() {
            return this.currentVibration;
        }

        public void setCurrentVibration(@Nullable VibrationInfo $$0) {
            this.currentVibration = $$0;
        }

        public int getTravelTimeInTicks() {
            return this.travelTimeInTicks;
        }

        public void setTravelTimeInTicks(int $$0) {
            this.travelTimeInTicks = $$0;
        }

        public void decrementTravelTime() {
            this.travelTimeInTicks = Math.max(0, this.travelTimeInTicks - 1);
        }

        public boolean shouldReloadVibrationParticle() {
            return this.reloadVibrationParticle;
        }

        public void setReloadVibrationParticle(boolean $$0) {
            this.reloadVibrationParticle = $$0;
        }
    }
}

