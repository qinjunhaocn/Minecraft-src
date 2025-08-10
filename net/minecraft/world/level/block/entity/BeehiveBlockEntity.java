/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.slf4j.Logger;

public class BeehiveBlockEntity
extends BlockEntity {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG_FLOWER_POS = "flower_pos";
    private static final String BEES = "bees";
    static final List<String> IGNORED_BEE_TAGS = Arrays.asList("Air", "drop_chances", "equipment", "Brain", "CanPickUpLoot", "DeathTime", "fall_distance", "FallFlying", "Fire", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "sleeping_pos", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "hive_pos", "Passengers", "leash", "UUID");
    public static final int MAX_OCCUPANTS = 3;
    private static final int MIN_TICKS_BEFORE_REENTERING_HIVE = 400;
    private static final int MIN_OCCUPATION_TICKS_NECTAR = 2400;
    public static final int MIN_OCCUPATION_TICKS_NECTARLESS = 600;
    private final List<BeeData> stored = Lists.newArrayList();
    @Nullable
    private BlockPos savedFlowerPos;

    public BeehiveBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.BEEHIVE, $$0, $$1);
    }

    @Override
    public void setChanged() {
        if (this.isFireNearby()) {
            this.emptyAllLivingFromHive(null, this.level.getBlockState(this.getBlockPos()), BeeReleaseStatus.EMERGENCY);
        }
        super.setChanged();
    }

    public boolean isFireNearby() {
        if (this.level == null) {
            return false;
        }
        for (BlockPos $$0 : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
            if (!(this.level.getBlockState($$0).getBlock() instanceof FireBlock)) continue;
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return this.stored.isEmpty();
    }

    public boolean isFull() {
        return this.stored.size() == 3;
    }

    public void emptyAllLivingFromHive(@Nullable Player $$0, BlockState $$1, BeeReleaseStatus $$2) {
        List<Entity> $$3 = this.releaseAllOccupants($$1, $$2);
        if ($$0 != null) {
            for (Entity $$4 : $$3) {
                if (!($$4 instanceof Bee)) continue;
                Bee $$5 = (Bee)$$4;
                if (!($$0.position().distanceToSqr($$4.position()) <= 16.0)) continue;
                if (!this.isSedated()) {
                    $$5.setTarget($$0);
                    continue;
                }
                $$5.setStayOutOfHiveCountdown(400);
            }
        }
    }

    private List<Entity> releaseAllOccupants(BlockState $$0, BeeReleaseStatus $$1) {
        ArrayList<Entity> $$2 = Lists.newArrayList();
        this.stored.removeIf($$3 -> BeehiveBlockEntity.releaseOccupant(this.level, this.worldPosition, $$0, $$3.toOccupant(), $$2, $$1, this.savedFlowerPos));
        if (!$$2.isEmpty()) {
            super.setChanged();
        }
        return $$2;
    }

    @VisibleForDebug
    public int getOccupantCount() {
        return this.stored.size();
    }

    public static int getHoneyLevel(BlockState $$0) {
        return $$0.getValue(BeehiveBlock.HONEY_LEVEL);
    }

    @VisibleForDebug
    public boolean isSedated() {
        return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
    }

    public void addOccupant(Bee $$0) {
        if (this.stored.size() >= 3) {
            return;
        }
        $$0.stopRiding();
        $$0.ejectPassengers();
        $$0.dropLeash();
        this.storeBee(Occupant.of($$0));
        if (this.level != null) {
            if ($$0.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                this.savedFlowerPos = $$0.getSavedFlowerPos();
            }
            BlockPos $$1 = this.getBlockPos();
            this.level.playSound(null, (double)$$1.getX(), (double)$$1.getY(), (double)$$1.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0f, 1.0f);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, $$1, GameEvent.Context.of($$0, this.getBlockState()));
        }
        $$0.discard();
        super.setChanged();
    }

    public void storeBee(Occupant $$0) {
        this.stored.add(new BeeData($$0));
    }

    private static boolean releaseOccupant(Level $$02, BlockPos $$1, BlockState $$2, Occupant $$3, @Nullable List<Entity> $$4, BeeReleaseStatus $$5, @Nullable BlockPos $$6) {
        boolean $$9;
        if (Bee.isNightOrRaining($$02) && $$5 != BeeReleaseStatus.EMERGENCY) {
            return false;
        }
        Direction $$7 = $$2.getValue(BeehiveBlock.FACING);
        BlockPos $$8 = $$1.relative($$7);
        boolean bl = $$9 = !$$02.getBlockState($$8).getCollisionShape($$02, $$8).isEmpty();
        if ($$9 && $$5 != BeeReleaseStatus.EMERGENCY) {
            return false;
        }
        Entity $$10 = $$3.createEntity($$02, $$1);
        if ($$10 != null) {
            if ($$10 instanceof Bee) {
                Bee $$11 = (Bee)$$10;
                if ($$6 != null && !$$11.hasSavedFlowerPos() && $$02.random.nextFloat() < 0.9f) {
                    $$11.setSavedFlowerPos($$6);
                }
                if ($$5 == BeeReleaseStatus.HONEY_DELIVERED) {
                    int $$12;
                    $$11.dropOffNectar();
                    if ($$2.is(BlockTags.BEEHIVES, $$0 -> $$0.hasProperty(BeehiveBlock.HONEY_LEVEL)) && ($$12 = BeehiveBlockEntity.getHoneyLevel($$2)) < 5) {
                        int $$13;
                        int n = $$13 = $$02.random.nextInt(100) == 0 ? 2 : 1;
                        if ($$12 + $$13 > 5) {
                            --$$13;
                        }
                        $$02.setBlockAndUpdate($$1, (BlockState)$$2.setValue(BeehiveBlock.HONEY_LEVEL, $$12 + $$13));
                    }
                }
                if ($$4 != null) {
                    $$4.add($$11);
                }
                float $$14 = $$10.getBbWidth();
                double $$15 = $$9 ? 0.0 : 0.55 + (double)($$14 / 2.0f);
                double $$16 = (double)$$1.getX() + 0.5 + $$15 * (double)$$7.getStepX();
                double $$17 = (double)$$1.getY() + 0.5 - (double)($$10.getBbHeight() / 2.0f);
                double $$18 = (double)$$1.getZ() + 0.5 + $$15 * (double)$$7.getStepZ();
                $$10.snapTo($$16, $$17, $$18, $$10.getYRot(), $$10.getXRot());
            }
            $$02.playSound(null, $$1, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$02.gameEvent(GameEvent.BLOCK_CHANGE, $$1, GameEvent.Context.of($$10, $$02.getBlockState($$1)));
            return $$02.addFreshEntity($$10);
        }
        return false;
    }

    private boolean hasSavedFlowerPos() {
        return this.savedFlowerPos != null;
    }

    private static void tickOccupants(Level $$0, BlockPos $$1, BlockState $$2, List<BeeData> $$3, @Nullable BlockPos $$4) {
        boolean $$5 = false;
        Iterator<BeeData> $$6 = $$3.iterator();
        while ($$6.hasNext()) {
            BeeReleaseStatus $$8;
            BeeData $$7 = $$6.next();
            if (!$$7.tick()) continue;
            BeeReleaseStatus beeReleaseStatus = $$8 = $$7.hasNectar() ? BeeReleaseStatus.HONEY_DELIVERED : BeeReleaseStatus.BEE_RELEASED;
            if (!BeehiveBlockEntity.releaseOccupant($$0, $$1, $$2, $$7.toOccupant(), null, $$8, $$4)) continue;
            $$5 = true;
            $$6.remove();
        }
        if ($$5) {
            BeehiveBlockEntity.setChanged($$0, $$1, $$2);
        }
    }

    public static void serverTick(Level $$0, BlockPos $$1, BlockState $$2, BeehiveBlockEntity $$3) {
        BeehiveBlockEntity.tickOccupants($$0, $$1, $$2, $$3.stored, $$3.savedFlowerPos);
        if (!$$3.stored.isEmpty() && $$0.getRandom().nextDouble() < 0.005) {
            double $$4 = (double)$$1.getX() + 0.5;
            double $$5 = $$1.getY();
            double $$6 = (double)$$1.getZ() + 0.5;
            $$0.playSound(null, $$4, $$5, $$6, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        DebugPackets.sendHiveInfo($$0, $$1, $$2, $$3);
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        this.stored.clear();
        $$0.read(BEES, Occupant.LIST_CODEC).orElse(List.of()).forEach(this::storeBee);
        this.savedFlowerPos = $$0.read(TAG_FLOWER_POS, BlockPos.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        $$0.store(BEES, Occupant.LIST_CODEC, this.getBees());
        $$0.storeNullable(TAG_FLOWER_POS, BlockPos.CODEC, this.savedFlowerPos);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter $$0) {
        super.applyImplicitComponents($$0);
        this.stored.clear();
        List<Occupant> $$1 = $$0.getOrDefault(DataComponents.BEES, Bees.EMPTY).bees();
        $$1.forEach(this::storeBee);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder $$0) {
        super.collectImplicitComponents($$0);
        $$0.set(DataComponents.BEES, new Bees(this.getBees()));
    }

    @Override
    public void removeComponentsFromTag(ValueOutput $$0) {
        super.removeComponentsFromTag($$0);
        $$0.discard(BEES);
    }

    private List<Occupant> getBees() {
        return this.stored.stream().map(BeeData::toOccupant).toList();
    }

    public static final class BeeReleaseStatus
    extends Enum<BeeReleaseStatus> {
        public static final /* enum */ BeeReleaseStatus HONEY_DELIVERED = new BeeReleaseStatus();
        public static final /* enum */ BeeReleaseStatus BEE_RELEASED = new BeeReleaseStatus();
        public static final /* enum */ BeeReleaseStatus EMERGENCY = new BeeReleaseStatus();
        private static final /* synthetic */ BeeReleaseStatus[] $VALUES;

        public static BeeReleaseStatus[] values() {
            return (BeeReleaseStatus[])$VALUES.clone();
        }

        public static BeeReleaseStatus valueOf(String $$0) {
            return Enum.valueOf(BeeReleaseStatus.class, $$0);
        }

        private static /* synthetic */ BeeReleaseStatus[] a() {
            return new BeeReleaseStatus[]{HONEY_DELIVERED, BEE_RELEASED, EMERGENCY};
        }

        static {
            $VALUES = BeeReleaseStatus.a();
        }
    }

    public static final class Occupant
    extends Record {
        final CustomData entityData;
        private final int ticksInHive;
        final int minTicksInHive;
        public static final Codec<Occupant> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)CustomData.CODEC.optionalFieldOf("entity_data", (Object)CustomData.EMPTY).forGetter(Occupant::entityData), (App)Codec.INT.fieldOf("ticks_in_hive").forGetter(Occupant::ticksInHive), (App)Codec.INT.fieldOf("min_ticks_in_hive").forGetter(Occupant::minTicksInHive)).apply((Applicative)$$0, Occupant::new));
        public static final Codec<List<Occupant>> LIST_CODEC = CODEC.listOf();
        public static final StreamCodec<ByteBuf, Occupant> STREAM_CODEC = StreamCodec.composite(CustomData.STREAM_CODEC, Occupant::entityData, ByteBufCodecs.VAR_INT, Occupant::ticksInHive, ByteBufCodecs.VAR_INT, Occupant::minTicksInHive, Occupant::new);

        public Occupant(CustomData $$0, int $$1, int $$2) {
            this.entityData = $$0;
            this.ticksInHive = $$1;
            this.minTicksInHive = $$2;
        }

        public static Occupant of(Entity $$0) {
            try (ProblemReporter.ScopedCollector $$1 = new ProblemReporter.ScopedCollector($$0.problemPath(), LOGGER);){
                TagValueOutput $$2 = TagValueOutput.createWithContext($$1, $$0.registryAccess());
                $$0.save($$2);
                IGNORED_BEE_TAGS.forEach($$2::discard);
                CompoundTag $$3 = $$2.buildResult();
                boolean $$4 = $$3.getBooleanOr("HasNectar", false);
                Occupant occupant = new Occupant(CustomData.of($$3), 0, $$4 ? 2400 : 600);
                return occupant;
            }
        }

        public static Occupant create(int $$0) {
            CompoundTag $$1 = new CompoundTag();
            $$1.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.BEE).toString());
            return new Occupant(CustomData.of($$1), $$0, 600);
        }

        @Nullable
        public Entity createEntity(Level $$02, BlockPos $$1) {
            CompoundTag $$2 = this.entityData.copyTag();
            IGNORED_BEE_TAGS.forEach($$2::remove);
            Entity $$3 = EntityType.loadEntityRecursive($$2, $$02, EntitySpawnReason.LOAD, $$0 -> $$0);
            if ($$3 == null || !$$3.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                return null;
            }
            $$3.setNoGravity(true);
            if ($$3 instanceof Bee) {
                Bee $$4 = (Bee)$$3;
                $$4.setHivePos($$1);
                Occupant.setBeeReleaseData(this.ticksInHive, $$4);
            }
            return $$3;
        }

        private static void setBeeReleaseData(int $$0, Bee $$1) {
            int $$2 = $$1.getAge();
            if ($$2 < 0) {
                $$1.setAge(Math.min(0, $$2 + $$0));
            } else if ($$2 > 0) {
                $$1.setAge(Math.max(0, $$2 - $$0));
            }
            $$1.setInLoveTime(Math.max(0, $$1.getInLoveTime() - $$0));
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Occupant.class, "entityData;ticksInHive;minTicksInHive", "entityData", "ticksInHive", "minTicksInHive"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Occupant.class, "entityData;ticksInHive;minTicksInHive", "entityData", "ticksInHive", "minTicksInHive"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Occupant.class, "entityData;ticksInHive;minTicksInHive", "entityData", "ticksInHive", "minTicksInHive"}, this, $$0);
        }

        public CustomData entityData() {
            return this.entityData;
        }

        public int ticksInHive() {
            return this.ticksInHive;
        }

        public int minTicksInHive() {
            return this.minTicksInHive;
        }
    }

    static class BeeData {
        private final Occupant occupant;
        private int ticksInHive;

        BeeData(Occupant $$0) {
            this.occupant = $$0;
            this.ticksInHive = $$0.ticksInHive();
        }

        public boolean tick() {
            return this.ticksInHive++ > this.occupant.minTicksInHive;
        }

        public Occupant toOccupant() {
            return new Occupant(this.occupant.entityData, this.ticksInHive, this.occupant.minTicksInHive);
        }

        public boolean hasNectar() {
            return this.occupant.entityData.getUnsafe().getBooleanOr("HasNectar", false);
        }
    }
}

