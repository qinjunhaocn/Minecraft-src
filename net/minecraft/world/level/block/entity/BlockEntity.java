/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.slf4j.Logger;

public abstract class BlockEntity {
    private static final Codec<BlockEntityType<?>> TYPE_CODEC = BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BlockEntityType<?> type;
    @Nullable
    protected Level level;
    protected final BlockPos worldPosition;
    protected boolean remove;
    private BlockState blockState;
    private DataComponentMap components = DataComponentMap.EMPTY;

    public BlockEntity(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2) {
        this.type = $$0;
        this.worldPosition = $$1.immutable();
        this.validateBlockState($$2);
        this.blockState = $$2;
    }

    private void validateBlockState(BlockState $$0) {
        if (!this.isValidBlockState($$0)) {
            throw new IllegalStateException("Invalid block entity " + this.getNameForReporting() + " state at " + String.valueOf(this.worldPosition) + ", got " + String.valueOf($$0));
        }
    }

    public boolean isValidBlockState(BlockState $$0) {
        return this.type.isValid($$0);
    }

    public static BlockPos getPosFromTag(ChunkPos $$0, CompoundTag $$1) {
        int $$2 = $$1.getIntOr("x", 0);
        int $$3 = $$1.getIntOr("y", 0);
        int $$4 = $$1.getIntOr("z", 0);
        int $$5 = SectionPos.blockToSectionCoord($$2);
        int $$6 = SectionPos.blockToSectionCoord($$4);
        if ($$5 != $$0.x || $$6 != $$0.z) {
            LOGGER.warn("Block entity {} found in a wrong chunk, expected position from chunk {}", (Object)$$1, (Object)$$0);
            $$2 = $$0.getBlockX(SectionPos.sectionRelative($$2));
            $$4 = $$0.getBlockZ(SectionPos.sectionRelative($$4));
        }
        return new BlockPos($$2, $$3, $$4);
    }

    @Nullable
    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level $$0) {
        this.level = $$0;
    }

    public boolean hasLevel() {
        return this.level != null;
    }

    protected void loadAdditional(ValueInput $$0) {
    }

    public final void loadWithComponents(ValueInput $$0) {
        this.loadAdditional($$0);
        this.components = $$0.read("components", DataComponentMap.CODEC).orElse(DataComponentMap.EMPTY);
    }

    public final void loadCustomOnly(ValueInput $$0) {
        this.loadAdditional($$0);
    }

    protected void saveAdditional(ValueOutput $$0) {
    }

    public final CompoundTag saveWithFullMetadata(HolderLookup.Provider $$0) {
        try (ProblemReporter.ScopedCollector $$1 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER);){
            TagValueOutput $$2 = TagValueOutput.createWithContext($$1, $$0);
            this.saveWithFullMetadata($$2);
            CompoundTag compoundTag = $$2.buildResult();
            return compoundTag;
        }
    }

    public void saveWithFullMetadata(ValueOutput $$0) {
        this.saveWithoutMetadata($$0);
        this.saveMetadata($$0);
    }

    public void saveWithId(ValueOutput $$0) {
        this.saveWithoutMetadata($$0);
        this.saveId($$0);
    }

    public final CompoundTag saveWithoutMetadata(HolderLookup.Provider $$0) {
        try (ProblemReporter.ScopedCollector $$1 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER);){
            TagValueOutput $$2 = TagValueOutput.createWithContext($$1, $$0);
            this.saveWithoutMetadata($$2);
            CompoundTag compoundTag = $$2.buildResult();
            return compoundTag;
        }
    }

    public void saveWithoutMetadata(ValueOutput $$0) {
        this.saveAdditional($$0);
        $$0.store("components", DataComponentMap.CODEC, this.components);
    }

    public final CompoundTag saveCustomOnly(HolderLookup.Provider $$0) {
        try (ProblemReporter.ScopedCollector $$1 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER);){
            TagValueOutput $$2 = TagValueOutput.createWithContext($$1, $$0);
            this.saveCustomOnly($$2);
            CompoundTag compoundTag = $$2.buildResult();
            return compoundTag;
        }
    }

    public void saveCustomOnly(ValueOutput $$0) {
        this.saveAdditional($$0);
    }

    private void saveId(ValueOutput $$0) {
        BlockEntity.addEntityType($$0, this.getType());
    }

    public static void addEntityType(ValueOutput $$0, BlockEntityType<?> $$1) {
        $$0.store("id", TYPE_CODEC, $$1);
    }

    private void saveMetadata(ValueOutput $$0) {
        this.saveId($$0);
        $$0.putInt("x", this.worldPosition.getX());
        $$0.putInt("y", this.worldPosition.getY());
        $$0.putInt("z", this.worldPosition.getZ());
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    public static BlockEntity loadStatic(BlockPos $$0, BlockState $$1, CompoundTag $$2, HolderLookup.Provider $$3) {
        void $$7;
        BlockEntityType $$4 = $$2.read("id", TYPE_CODEC).orElse(null);
        if ($$4 == null) {
            LOGGER.error("Skipping block entity with invalid type: {}", (Object)$$2.get("id"));
            return null;
        }
        try {
            Object $$5 = $$4.create($$0, $$1);
        } catch (Throwable $$6) {
            LOGGER.error("Failed to create block entity {} for block {} at position {} ", $$4, $$0, $$1, $$6);
            return null;
        }
        ProblemReporter.ScopedCollector $$8 = new ProblemReporter.ScopedCollector($$7.problemPath(), LOGGER);
        try {
            $$7.loadWithComponents(TagValueInput.create((ProblemReporter)$$8, $$3, $$2));
            void var7_9 = $$7;
            $$8.close();
            return var7_9;
        } catch (Throwable throwable) {
            try {
                try {
                    $$8.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            } catch (Throwable $$9) {
                LOGGER.error("Failed to load data for block entity {} for block {} at position {}", $$4, $$0, $$1, $$9);
                return null;
            }
        }
    }

    public void setChanged() {
        if (this.level != null) {
            BlockEntity.setChanged(this.level, this.worldPosition, this.blockState);
        }
    }

    protected static void setChanged(Level $$0, BlockPos $$1, BlockState $$2) {
        $$0.blockEntityChanged($$1);
        if (!$$2.isAir()) {
            $$0.updateNeighbourForOutputSignal($$1, $$2.getBlock());
        }
    }

    public BlockPos getBlockPos() {
        return this.worldPosition;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return null;
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider $$0) {
        return new CompoundTag();
    }

    public boolean isRemoved() {
        return this.remove;
    }

    public void setRemoved() {
        this.remove = true;
    }

    public void clearRemoved() {
        this.remove = false;
    }

    public void preRemoveSideEffects(BlockPos $$0, BlockState $$1) {
        BlockEntity blockEntity = this;
        if (blockEntity instanceof Container) {
            Container $$2 = (Container)((Object)blockEntity);
            if (this.level != null) {
                Containers.dropContents(this.level, $$0, $$2);
            }
        }
    }

    public boolean triggerEvent(int $$0, int $$1) {
        return false;
    }

    public void fillCrashReportCategory(CrashReportCategory $$0) {
        $$0.setDetail("Name", this::getNameForReporting);
        $$0.setDetail("Cached block", this.getBlockState()::toString);
        if (this.level == null) {
            $$0.setDetail("Block location", () -> String.valueOf(this.worldPosition) + " (world missing)");
        } else {
            $$0.setDetail("Actual block", this.level.getBlockState(this.worldPosition)::toString);
            CrashReportCategory.populateBlockLocationDetails($$0, this.level, this.worldPosition);
        }
    }

    public String getNameForReporting() {
        return String.valueOf(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(this.getType())) + " // " + this.getClass().getCanonicalName();
    }

    public BlockEntityType<?> getType() {
        return this.type;
    }

    @Deprecated
    public void setBlockState(BlockState $$0) {
        this.validateBlockState($$0);
        this.blockState = $$0;
    }

    protected void applyImplicitComponents(DataComponentGetter $$0) {
    }

    public final void applyComponentsFromItemStack(ItemStack $$0) {
        this.applyComponents($$0.getPrototype(), $$0.getComponentsPatch());
    }

    public final void applyComponents(DataComponentMap $$0, DataComponentPatch $$1) {
        final HashSet<DataComponentType<Object>> $$2 = new HashSet<DataComponentType<Object>>();
        $$2.add(DataComponents.BLOCK_ENTITY_DATA);
        $$2.add(DataComponents.BLOCK_STATE);
        final PatchedDataComponentMap $$3 = PatchedDataComponentMap.fromPatch($$0, $$1);
        this.applyImplicitComponents(new DataComponentGetter(){

            @Override
            @Nullable
            public <T> T get(DataComponentType<? extends T> $$0) {
                $$2.add($$0);
                return $$3.get($$0);
            }

            @Override
            public <T> T getOrDefault(DataComponentType<? extends T> $$0, T $$1) {
                $$2.add($$0);
                return $$3.getOrDefault($$0, $$1);
            }
        });
        DataComponentPatch $$4 = $$1.forget($$2::contains);
        this.components = $$4.split().added();
    }

    protected void collectImplicitComponents(DataComponentMap.Builder $$0) {
    }

    @Deprecated
    public void removeComponentsFromTag(ValueOutput $$0) {
    }

    public final DataComponentMap collectComponents() {
        DataComponentMap.Builder $$0 = DataComponentMap.builder();
        $$0.addAll(this.components);
        this.collectImplicitComponents($$0);
        return $$0.build();
    }

    public DataComponentMap components() {
        return this.components;
    }

    public void setComponents(DataComponentMap $$0) {
        this.components = $$0;
    }

    @Nullable
    public static Component parseCustomNameSafe(ValueInput $$0, String $$1) {
        return $$0.read($$1, ComponentSerialization.CODEC).orElse(null);
    }

    public ProblemReporter.PathElement problemPath() {
        return new BlockEntityPathElement(this);
    }

    record BlockEntityPathElement(BlockEntity blockEntity) implements ProblemReporter.PathElement
    {
        @Override
        public String get() {
            return this.blockEntity.getNameForReporting() + "@" + String.valueOf(this.blockEntity.getBlockPos());
        }
    }
}

