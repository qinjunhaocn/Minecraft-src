/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;

public class DecoratedPotBlockEntity
extends BlockEntity
implements RandomizableContainer,
ContainerSingleItem.BlockContainerSingleItem {
    public static final String TAG_SHERDS = "sherds";
    public static final String TAG_ITEM = "item";
    public static final int EVENT_POT_WOBBLES = 1;
    public long wobbleStartedAtTick;
    @Nullable
    public WobbleStyle lastWobbleStyle;
    private PotDecorations decorations;
    private ItemStack item = ItemStack.EMPTY;
    @Nullable
    protected ResourceKey<LootTable> lootTable;
    protected long lootTableSeed;

    public DecoratedPotBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.DECORATED_POT, $$0, $$1);
        this.decorations = PotDecorations.EMPTY;
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        if (!this.decorations.equals(PotDecorations.EMPTY)) {
            $$0.store(TAG_SHERDS, PotDecorations.CODEC, this.decorations);
        }
        if (!this.trySaveLootTable($$0) && !this.item.isEmpty()) {
            $$0.store(TAG_ITEM, ItemStack.CODEC, this.item);
        }
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        this.decorations = $$0.read(TAG_SHERDS, PotDecorations.CODEC).orElse(PotDecorations.EMPTY);
        this.item = !this.tryLoadLootTable($$0) ? $$0.read(TAG_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider $$0) {
        return this.saveCustomOnly($$0);
    }

    public Direction getDirection() {
        return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    public PotDecorations getDecorations() {
        return this.decorations;
    }

    public static ItemStack createDecoratedPotItem(PotDecorations $$0) {
        ItemStack $$1 = Items.DECORATED_POT.getDefaultInstance();
        $$1.set(DataComponents.POT_DECORATIONS, $$0);
        return $$1;
    }

    @Override
    @Nullable
    public ResourceKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable ResourceKey<LootTable> $$0) {
        this.lootTable = $$0;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long $$0) {
        this.lootTableSeed = $$0;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder $$0) {
        super.collectImplicitComponents($$0);
        $$0.set(DataComponents.POT_DECORATIONS, this.decorations);
        $$0.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of((Object)this.item)));
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter $$0) {
        super.applyImplicitComponents($$0);
        this.decorations = $$0.getOrDefault(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY);
        this.item = $$0.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyOne();
    }

    @Override
    public void removeComponentsFromTag(ValueOutput $$0) {
        super.removeComponentsFromTag($$0);
        $$0.discard(TAG_SHERDS);
        $$0.discard(TAG_ITEM);
    }

    @Override
    public ItemStack getTheItem() {
        this.unpackLootTable(null);
        return this.item;
    }

    @Override
    public ItemStack splitTheItem(int $$0) {
        this.unpackLootTable(null);
        ItemStack $$1 = this.item.split($$0);
        if (this.item.isEmpty()) {
            this.item = ItemStack.EMPTY;
        }
        return $$1;
    }

    @Override
    public void setTheItem(ItemStack $$0) {
        this.unpackLootTable(null);
        this.item = $$0;
    }

    @Override
    public BlockEntity getContainerBlockEntity() {
        return this;
    }

    public void wobble(WobbleStyle $$0) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 1, $$0.ordinal());
    }

    @Override
    public boolean triggerEvent(int $$0, int $$1) {
        if (this.level != null && $$0 == 1 && $$1 >= 0 && $$1 < WobbleStyle.values().length) {
            this.wobbleStartedAtTick = this.level.getGameTime();
            this.lastWobbleStyle = WobbleStyle.values()[$$1];
            return true;
        }
        return super.triggerEvent($$0, $$1);
    }

    public /* synthetic */ Packet getUpdatePacket() {
        return this.getUpdatePacket();
    }

    public static final class WobbleStyle
    extends Enum<WobbleStyle> {
        public static final /* enum */ WobbleStyle POSITIVE = new WobbleStyle(7);
        public static final /* enum */ WobbleStyle NEGATIVE = new WobbleStyle(10);
        public final int duration;
        private static final /* synthetic */ WobbleStyle[] $VALUES;

        public static WobbleStyle[] values() {
            return (WobbleStyle[])$VALUES.clone();
        }

        public static WobbleStyle valueOf(String $$0) {
            return Enum.valueOf(WobbleStyle.class, $$0);
        }

        private WobbleStyle(int $$0) {
            this.duration = $$0;
        }

        private static /* synthetic */ WobbleStyle[] a() {
            return new WobbleStyle[]{POSITIVE, NEGATIVE};
        }

        static {
            $VALUES = WobbleStyle.a();
        }
    }
}

