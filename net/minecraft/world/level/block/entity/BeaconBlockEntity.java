/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBeamOwner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public class BeaconBlockEntity
extends BlockEntity
implements MenuProvider,
Nameable,
BeaconBeamOwner {
    private static final int MAX_LEVELS = 4;
    public static final List<List<Holder<MobEffect>>> BEACON_EFFECTS = List.of((Object)List.of(MobEffects.SPEED, MobEffects.HASTE), (Object)List.of(MobEffects.RESISTANCE, MobEffects.JUMP_BOOST), (Object)List.of(MobEffects.STRENGTH), (Object)List.of(MobEffects.REGENERATION));
    private static final Set<Holder<MobEffect>> VALID_EFFECTS = BEACON_EFFECTS.stream().flatMap(Collection::stream).collect(Collectors.toSet());
    public static final int DATA_LEVELS = 0;
    public static final int DATA_PRIMARY = 1;
    public static final int DATA_SECONDARY = 2;
    public static final int NUM_DATA_VALUES = 3;
    private static final int BLOCKS_CHECK_PER_TICK = 10;
    private static final Component DEFAULT_NAME = Component.translatable("container.beacon");
    private static final String TAG_PRIMARY = "primary_effect";
    private static final String TAG_SECONDARY = "secondary_effect";
    List<BeaconBeamOwner.Section> beamSections = new ArrayList<BeaconBeamOwner.Section>();
    private List<BeaconBeamOwner.Section> checkingBeamSections = new ArrayList<BeaconBeamOwner.Section>();
    int levels;
    private int lastCheckY;
    @Nullable
    Holder<MobEffect> primaryPower;
    @Nullable
    Holder<MobEffect> secondaryPower;
    @Nullable
    private Component name;
    private LockCode lockKey = LockCode.NO_LOCK;
    private final ContainerData dataAccess = new ContainerData(){

        @Override
        public int get(int $$0) {
            return switch ($$0) {
                case 0 -> BeaconBlockEntity.this.levels;
                case 1 -> BeaconMenu.encodeEffect(BeaconBlockEntity.this.primaryPower);
                case 2 -> BeaconMenu.encodeEffect(BeaconBlockEntity.this.secondaryPower);
                default -> 0;
            };
        }

        @Override
        public void set(int $$0, int $$1) {
            switch ($$0) {
                case 0: {
                    BeaconBlockEntity.this.levels = $$1;
                    break;
                }
                case 1: {
                    if (!BeaconBlockEntity.this.level.isClientSide && !BeaconBlockEntity.this.beamSections.isEmpty()) {
                        BeaconBlockEntity.playSound(BeaconBlockEntity.this.level, BeaconBlockEntity.this.worldPosition, SoundEvents.BEACON_POWER_SELECT);
                    }
                    BeaconBlockEntity.this.primaryPower = BeaconBlockEntity.filterEffect(BeaconMenu.decodeEffect($$1));
                    break;
                }
                case 2: {
                    BeaconBlockEntity.this.secondaryPower = BeaconBlockEntity.filterEffect(BeaconMenu.decodeEffect($$1));
                }
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    @Nullable
    static Holder<MobEffect> filterEffect(@Nullable Holder<MobEffect> $$0) {
        return VALID_EFFECTS.contains($$0) ? $$0 : null;
    }

    public BeaconBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.BEACON, $$0, $$1);
    }

    public static void tick(Level $$0, BlockPos $$1, BlockState $$2, BeaconBlockEntity $$3) {
        BlockPos $$8;
        int $$4 = $$1.getX();
        int $$5 = $$1.getY();
        int $$6 = $$1.getZ();
        if ($$3.lastCheckY < $$5) {
            BlockPos $$7 = $$1;
            $$3.checkingBeamSections = Lists.newArrayList();
            $$3.lastCheckY = $$7.getY() - 1;
        } else {
            $$8 = new BlockPos($$4, $$3.lastCheckY + 1, $$6);
        }
        BeaconBeamOwner.Section $$9 = $$3.checkingBeamSections.isEmpty() ? null : $$3.checkingBeamSections.get($$3.checkingBeamSections.size() - 1);
        int $$10 = $$0.getHeight(Heightmap.Types.WORLD_SURFACE, $$4, $$6);
        for (int $$11 = 0; $$11 < 10 && $$8.getY() <= $$10; ++$$11) {
            block18: {
                BlockState $$12;
                block16: {
                    int $$15;
                    block17: {
                        $$12 = $$0.getBlockState($$8);
                        Block $$13 = $$12.getBlock();
                        if (!($$13 instanceof BeaconBeamBlock)) break block16;
                        BeaconBeamBlock $$14 = (BeaconBeamBlock)((Object)$$13);
                        $$15 = $$14.getColor().getTextureDiffuseColor();
                        if ($$3.checkingBeamSections.size() > 1) break block17;
                        $$9 = new BeaconBeamOwner.Section($$15);
                        $$3.checkingBeamSections.add($$9);
                        break block18;
                    }
                    if ($$9 == null) break block18;
                    if ($$15 == $$9.getColor()) {
                        $$9.increaseHeight();
                    } else {
                        $$9 = new BeaconBeamOwner.Section(ARGB.average($$9.getColor(), $$15));
                        $$3.checkingBeamSections.add($$9);
                    }
                    break block18;
                }
                if ($$9 != null && ($$12.getLightBlock() < 15 || $$12.is(Blocks.BEDROCK))) {
                    $$9.increaseHeight();
                } else {
                    $$3.checkingBeamSections.clear();
                    $$3.lastCheckY = $$10;
                    break;
                }
            }
            $$8 = $$8.above();
            ++$$3.lastCheckY;
        }
        int $$16 = $$3.levels;
        if ($$0.getGameTime() % 80L == 0L) {
            if (!$$3.beamSections.isEmpty()) {
                $$3.levels = BeaconBlockEntity.updateBase($$0, $$4, $$5, $$6);
            }
            if ($$3.levels > 0 && !$$3.beamSections.isEmpty()) {
                BeaconBlockEntity.applyEffects($$0, $$1, $$3.levels, $$3.primaryPower, $$3.secondaryPower);
                BeaconBlockEntity.playSound($$0, $$1, SoundEvents.BEACON_AMBIENT);
            }
        }
        if ($$3.lastCheckY >= $$10) {
            $$3.lastCheckY = $$0.getMinY() - 1;
            boolean $$17 = $$16 > 0;
            $$3.beamSections = $$3.checkingBeamSections;
            if (!$$0.isClientSide) {
                boolean $$18;
                boolean bl = $$18 = $$3.levels > 0;
                if (!$$17 && $$18) {
                    BeaconBlockEntity.playSound($$0, $$1, SoundEvents.BEACON_ACTIVATE);
                    for (ServerPlayer $$19 : $$0.getEntitiesOfClass(ServerPlayer.class, new AABB($$4, $$5, $$6, $$4, $$5 - 4, $$6).inflate(10.0, 5.0, 10.0))) {
                        CriteriaTriggers.CONSTRUCT_BEACON.trigger($$19, $$3.levels);
                    }
                } else if ($$17 && !$$18) {
                    BeaconBlockEntity.playSound($$0, $$1, SoundEvents.BEACON_DEACTIVATE);
                }
            }
        }
    }

    private static int updateBase(Level $$0, int $$1, int $$2, int $$3) {
        int $$6;
        int $$4 = 0;
        int $$5 = 1;
        while ($$5 <= 4 && ($$6 = $$2 - $$5) >= $$0.getMinY()) {
            boolean $$7 = true;
            block1: for (int $$8 = $$1 - $$5; $$8 <= $$1 + $$5 && $$7; ++$$8) {
                for (int $$9 = $$3 - $$5; $$9 <= $$3 + $$5; ++$$9) {
                    if ($$0.getBlockState(new BlockPos($$8, $$6, $$9)).is(BlockTags.BEACON_BASE_BLOCKS)) continue;
                    $$7 = false;
                    continue block1;
                }
            }
            if (!$$7) break;
            $$4 = $$5++;
        }
        return $$4;
    }

    @Override
    public void setRemoved() {
        BeaconBlockEntity.playSound(this.level, this.worldPosition, SoundEvents.BEACON_DEACTIVATE);
        super.setRemoved();
    }

    private static void applyEffects(Level $$0, BlockPos $$1, int $$2, @Nullable Holder<MobEffect> $$3, @Nullable Holder<MobEffect> $$4) {
        if ($$0.isClientSide || $$3 == null) {
            return;
        }
        double $$5 = $$2 * 10 + 10;
        int $$6 = 0;
        if ($$2 >= 4 && Objects.equals($$3, $$4)) {
            $$6 = 1;
        }
        int $$7 = (9 + $$2 * 2) * 20;
        AABB $$8 = new AABB($$1).inflate($$5).expandTowards(0.0, $$0.getHeight(), 0.0);
        List<Player> $$9 = $$0.getEntitiesOfClass(Player.class, $$8);
        for (Player $$10 : $$9) {
            $$10.addEffect(new MobEffectInstance($$3, $$7, $$6, true, true));
        }
        if ($$2 >= 4 && !Objects.equals($$3, $$4) && $$4 != null) {
            for (Player $$11 : $$9) {
                $$11.addEffect(new MobEffectInstance($$4, $$7, 0, true, true));
            }
        }
    }

    public static void playSound(Level $$0, BlockPos $$1, SoundEvent $$2) {
        $$0.playSound(null, $$1, $$2, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public List<BeaconBeamOwner.Section> getBeamSections() {
        return this.levels == 0 ? ImmutableList.of() : this.beamSections;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider $$0) {
        return this.saveCustomOnly($$0);
    }

    private static void storeEffect(ValueOutput $$0, String $$1, @Nullable Holder<MobEffect> $$22) {
        if ($$22 != null) {
            $$22.unwrapKey().ifPresent($$2 -> $$0.putString($$1, $$2.location().toString()));
        }
    }

    @Nullable
    private static Holder<MobEffect> loadEffect(ValueInput $$0, String $$1) {
        return $$0.read($$1, BuiltInRegistries.MOB_EFFECT.holderByNameCodec()).filter(VALID_EFFECTS::contains).orElse(null);
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        this.primaryPower = BeaconBlockEntity.loadEffect($$0, TAG_PRIMARY);
        this.secondaryPower = BeaconBlockEntity.loadEffect($$0, TAG_SECONDARY);
        this.name = BeaconBlockEntity.parseCustomNameSafe($$0, "CustomName");
        this.lockKey = LockCode.fromTag($$0);
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        BeaconBlockEntity.storeEffect($$0, TAG_PRIMARY, this.primaryPower);
        BeaconBlockEntity.storeEffect($$0, TAG_SECONDARY, this.secondaryPower);
        $$0.putInt("Levels", this.levels);
        $$0.storeNullable("CustomName", ComponentSerialization.CODEC, this.name);
        this.lockKey.addToTag($$0);
    }

    public void setCustomName(@Nullable Component $$0) {
        this.name = $$0;
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.name;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int $$0, Inventory $$1, Player $$2) {
        if (BaseContainerBlockEntity.canUnlock($$2, this.lockKey, this.getDisplayName())) {
            return new BeaconMenu($$0, $$1, this.dataAccess, ContainerLevelAccess.create(this.level, this.getBlockPos()));
        }
        return null;
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    public Component getName() {
        if (this.name != null) {
            return this.name;
        }
        return DEFAULT_NAME;
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter $$0) {
        super.applyImplicitComponents($$0);
        this.name = $$0.get(DataComponents.CUSTOM_NAME);
        this.lockKey = $$0.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder $$0) {
        super.collectImplicitComponents($$0);
        $$0.set(DataComponents.CUSTOM_NAME, this.name);
        if (!this.lockKey.equals((Object)LockCode.NO_LOCK)) {
            $$0.set(DataComponents.LOCK, this.lockKey);
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput $$0) {
        $$0.discard("CustomName");
        $$0.discard("lock");
    }

    @Override
    public void setLevel(Level $$0) {
        super.setLevel($$0);
        this.lastCheckY = $$0.getMinY() - 1;
    }

    public /* synthetic */ Packet getUpdatePacket() {
        return this.getUpdatePacket();
    }
}

