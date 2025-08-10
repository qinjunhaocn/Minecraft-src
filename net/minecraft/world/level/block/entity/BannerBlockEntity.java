/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BannerBlockEntity
extends BlockEntity
implements Nameable {
    public static final int MAX_PATTERNS = 6;
    private static final String TAG_PATTERNS = "patterns";
    @Nullable
    private Component name;
    private final DyeColor baseColor;
    private BannerPatternLayers patterns = BannerPatternLayers.EMPTY;

    public BannerBlockEntity(BlockPos $$0, BlockState $$1) {
        this($$0, $$1, ((AbstractBannerBlock)$$1.getBlock()).getColor());
    }

    public BannerBlockEntity(BlockPos $$0, BlockState $$1, DyeColor $$2) {
        super(BlockEntityType.BANNER, $$0, $$1);
        this.baseColor = $$2;
    }

    @Override
    public Component getName() {
        if (this.name != null) {
            return this.name;
        }
        return Component.translatable("block.minecraft.banner");
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.name;
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        if (!this.patterns.equals(BannerPatternLayers.EMPTY)) {
            $$0.store(TAG_PATTERNS, BannerPatternLayers.CODEC, this.patterns);
        }
        $$0.storeNullable("CustomName", ComponentSerialization.CODEC, this.name);
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        this.name = BannerBlockEntity.parseCustomNameSafe($$0, "CustomName");
        this.patterns = $$0.read(TAG_PATTERNS, BannerPatternLayers.CODEC).orElse(BannerPatternLayers.EMPTY);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider $$0) {
        return this.saveWithoutMetadata($$0);
    }

    public BannerPatternLayers getPatterns() {
        return this.patterns;
    }

    public ItemStack getItem() {
        ItemStack $$0 = new ItemStack(BannerBlock.byColor(this.baseColor));
        $$0.applyComponents(this.collectComponents());
        return $$0;
    }

    public DyeColor getBaseColor() {
        return this.baseColor;
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter $$0) {
        super.applyImplicitComponents($$0);
        this.patterns = $$0.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
        this.name = $$0.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder $$0) {
        super.collectImplicitComponents($$0);
        $$0.set(DataComponents.BANNER_PATTERNS, this.patterns);
        $$0.set(DataComponents.CUSTOM_NAME, this.name);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput $$0) {
        $$0.discard(TAG_PATTERNS);
        $$0.discard("CustomName");
    }

    public /* synthetic */ Packet getUpdatePacket() {
        return this.getUpdatePacket();
    }
}

