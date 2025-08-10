/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.primitives.Shorts;
import com.google.common.primitives.SignedBytes;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.HashedStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerSlotStateChangedPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromBlockPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromEntityPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class MultiPlayerGameMode {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    private final ClientPacketListener connection;
    private BlockPos destroyBlockPos = new BlockPos(-1, -1, -1);
    private ItemStack destroyingItem = ItemStack.EMPTY;
    private float destroyProgress;
    private float destroyTicks;
    private int destroyDelay;
    private boolean isDestroying;
    private GameType localPlayerMode = GameType.DEFAULT_MODE;
    @Nullable
    private GameType previousLocalPlayerMode;
    private int carriedIndex;

    public MultiPlayerGameMode(Minecraft $$0, ClientPacketListener $$1) {
        this.minecraft = $$0;
        this.connection = $$1;
    }

    public void adjustPlayer(Player $$0) {
        this.localPlayerMode.updatePlayerAbilities($$0.getAbilities());
    }

    public void setLocalMode(GameType $$0, @Nullable GameType $$1) {
        this.localPlayerMode = $$0;
        this.previousLocalPlayerMode = $$1;
        this.localPlayerMode.updatePlayerAbilities(this.minecraft.player.getAbilities());
    }

    public void setLocalMode(GameType $$0) {
        if ($$0 != this.localPlayerMode) {
            this.previousLocalPlayerMode = this.localPlayerMode;
        }
        this.localPlayerMode = $$0;
        this.localPlayerMode.updatePlayerAbilities(this.minecraft.player.getAbilities());
    }

    public boolean canHurtPlayer() {
        return this.localPlayerMode.isSurvival();
    }

    public boolean destroyBlock(BlockPos $$0) {
        if (this.minecraft.player.blockActionRestricted(this.minecraft.level, $$0, this.localPlayerMode)) {
            return false;
        }
        ClientLevel $$1 = this.minecraft.level;
        BlockState $$2 = $$1.getBlockState($$0);
        if (!this.minecraft.player.getMainHandItem().canDestroyBlock($$2, $$1, $$0, this.minecraft.player)) {
            return false;
        }
        Block $$3 = $$2.getBlock();
        if ($$3 instanceof GameMasterBlock && !this.minecraft.player.canUseGameMasterBlocks()) {
            return false;
        }
        if ($$2.isAir()) {
            return false;
        }
        $$3.playerWillDestroy($$1, $$0, $$2, this.minecraft.player);
        FluidState $$4 = $$1.getFluidState($$0);
        boolean $$5 = $$1.setBlock($$0, $$4.createLegacyBlock(), 11);
        if ($$5) {
            $$3.destroy($$1, $$0, $$2);
        }
        return $$5;
    }

    public boolean startDestroyBlock(BlockPos $$0, Direction $$1) {
        if (this.minecraft.player.blockActionRestricted(this.minecraft.level, $$0, this.localPlayerMode)) {
            return false;
        }
        if (!this.minecraft.level.getWorldBorder().isWithinBounds($$0)) {
            return false;
        }
        if (this.minecraft.player.getAbilities().instabuild) {
            BlockState $$22 = this.minecraft.level.getBlockState($$0);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, $$0, $$22, 1.0f);
            this.startPrediction(this.minecraft.level, $$2 -> {
                this.destroyBlock($$0);
                return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, $$0, $$1, $$2);
            });
            this.destroyDelay = 5;
        } else if (!this.isDestroying || !this.sameDestroyTarget($$0)) {
            if (this.isDestroying) {
                this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, $$1));
            }
            BlockState $$32 = this.minecraft.level.getBlockState($$0);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, $$0, $$32, 0.0f);
            this.startPrediction(this.minecraft.level, $$3 -> {
                boolean $$4;
                boolean bl = $$4 = !$$32.isAir();
                if ($$4 && this.destroyProgress == 0.0f) {
                    $$32.attack(this.minecraft.level, $$0, this.minecraft.player);
                }
                if ($$4 && $$32.getDestroyProgress(this.minecraft.player, this.minecraft.player.level(), $$0) >= 1.0f) {
                    this.destroyBlock($$0);
                } else {
                    this.isDestroying = true;
                    this.destroyBlockPos = $$0;
                    this.destroyingItem = this.minecraft.player.getMainHandItem();
                    this.destroyProgress = 0.0f;
                    this.destroyTicks = 0.0f;
                    this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, this.getDestroyStage());
                }
                return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, $$0, $$1, $$3);
            });
        }
        return true;
    }

    public void stopDestroyBlock() {
        if (this.isDestroying) {
            BlockState $$0 = this.minecraft.level.getBlockState(this.destroyBlockPos);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, this.destroyBlockPos, $$0, -1.0f);
            this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, Direction.DOWN));
            this.isDestroying = false;
            this.destroyProgress = 0.0f;
            this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, -1);
            this.minecraft.player.resetAttackStrengthTicker();
        }
    }

    public boolean continueDestroyBlock(BlockPos $$0, Direction $$1) {
        this.ensureHasSentCarriedItem();
        if (this.destroyDelay > 0) {
            --this.destroyDelay;
            return true;
        }
        if (this.minecraft.player.getAbilities().instabuild && this.minecraft.level.getWorldBorder().isWithinBounds($$0)) {
            this.destroyDelay = 5;
            BlockState $$22 = this.minecraft.level.getBlockState($$0);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, $$0, $$22, 1.0f);
            this.startPrediction(this.minecraft.level, $$2 -> {
                this.destroyBlock($$0);
                return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, $$0, $$1, $$2);
            });
            return true;
        }
        if (this.sameDestroyTarget($$0)) {
            BlockState $$3 = this.minecraft.level.getBlockState($$0);
            if ($$3.isAir()) {
                this.isDestroying = false;
                return false;
            }
            this.destroyProgress += $$3.getDestroyProgress(this.minecraft.player, this.minecraft.player.level(), $$0);
            if (this.destroyTicks % 4.0f == 0.0f) {
                SoundType $$4 = $$3.getSoundType();
                this.minecraft.getSoundManager().play(new SimpleSoundInstance($$4.getHitSound(), SoundSource.BLOCKS, ($$4.getVolume() + 1.0f) / 8.0f, $$4.getPitch() * 0.5f, SoundInstance.createUnseededRandom(), $$0));
            }
            this.destroyTicks += 1.0f;
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, $$0, $$3, Mth.clamp(this.destroyProgress, 0.0f, 1.0f));
            if (this.destroyProgress >= 1.0f) {
                this.isDestroying = false;
                this.startPrediction(this.minecraft.level, $$2 -> {
                    this.destroyBlock($$0);
                    return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, $$0, $$1, $$2);
                });
                this.destroyProgress = 0.0f;
                this.destroyTicks = 0.0f;
                this.destroyDelay = 5;
            }
        } else {
            return this.startDestroyBlock($$0, $$1);
        }
        this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, this.getDestroyStage());
        return true;
    }

    private void startPrediction(ClientLevel $$0, PredictiveAction $$1) {
        try (BlockStatePredictionHandler $$2 = $$0.getBlockStatePredictionHandler().startPredicting();){
            int $$3 = $$2.currentSequence();
            Packet<ServerGamePacketListener> $$4 = $$1.predict($$3);
            this.connection.send($$4);
        }
    }

    public void tick() {
        this.ensureHasSentCarriedItem();
        if (this.connection.getConnection().isConnected()) {
            this.connection.getConnection().tick();
        } else {
            this.connection.getConnection().handleDisconnection();
        }
    }

    private boolean sameDestroyTarget(BlockPos $$0) {
        ItemStack $$1 = this.minecraft.player.getMainHandItem();
        return $$0.equals(this.destroyBlockPos) && ItemStack.isSameItemSameComponents($$1, this.destroyingItem);
    }

    private void ensureHasSentCarriedItem() {
        int $$0 = this.minecraft.player.getInventory().getSelectedSlot();
        if ($$0 != this.carriedIndex) {
            this.carriedIndex = $$0;
            this.connection.send(new ServerboundSetCarriedItemPacket(this.carriedIndex));
        }
    }

    public InteractionResult useItemOn(LocalPlayer $$0, InteractionHand $$1, BlockHitResult $$2) {
        this.ensureHasSentCarriedItem();
        if (!this.minecraft.level.getWorldBorder().isWithinBounds($$2.getBlockPos())) {
            return InteractionResult.FAIL;
        }
        MutableObject $$3 = new MutableObject();
        this.startPrediction(this.minecraft.level, $$4 -> {
            $$3.setValue(this.performUseItemOn($$0, $$1, $$2));
            return new ServerboundUseItemOnPacket($$1, $$2, $$4);
        });
        return (InteractionResult)$$3.getValue();
    }

    private InteractionResult performUseItemOn(LocalPlayer $$0, InteractionHand $$1, BlockHitResult $$2) {
        InteractionResult $$13;
        boolean $$6;
        BlockPos $$3 = $$2.getBlockPos();
        ItemStack $$4 = $$0.getItemInHand($$1);
        if (this.localPlayerMode == GameType.SPECTATOR) {
            return InteractionResult.CONSUME;
        }
        boolean $$5 = !$$0.getMainHandItem().isEmpty() || !$$0.getOffhandItem().isEmpty();
        boolean bl = $$6 = $$0.isSecondaryUseActive() && $$5;
        if (!$$6) {
            InteractionResult $$9;
            BlockState $$7 = this.minecraft.level.getBlockState($$3);
            if (!this.connection.isFeatureEnabled($$7.getBlock().requiredFeatures())) {
                return InteractionResult.FAIL;
            }
            InteractionResult $$8 = $$7.useItemOn($$0.getItemInHand($$1), this.minecraft.level, $$0, $$1, $$2);
            if ($$8.consumesAction()) {
                return $$8;
            }
            if ($$8 instanceof InteractionResult.TryEmptyHandInteraction && $$1 == InteractionHand.MAIN_HAND && ($$9 = $$7.useWithoutItem(this.minecraft.level, $$0, $$2)).consumesAction()) {
                return $$9;
            }
        }
        if ($$4.isEmpty() || $$0.getCooldowns().isOnCooldown($$4)) {
            return InteractionResult.PASS;
        }
        UseOnContext $$10 = new UseOnContext($$0, $$1, $$2);
        if ($$0.hasInfiniteMaterials()) {
            int $$11 = $$4.getCount();
            InteractionResult $$12 = $$4.useOn($$10);
            $$4.setCount($$11);
        } else {
            $$13 = $$4.useOn($$10);
        }
        return $$13;
    }

    public InteractionResult useItem(Player $$0, InteractionHand $$1) {
        if (this.localPlayerMode == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        this.ensureHasSentCarriedItem();
        MutableObject $$2 = new MutableObject();
        this.startPrediction(this.minecraft.level, $$3 -> {
            ItemStack $$9;
            ServerboundUseItemPacket $$4 = new ServerboundUseItemPacket($$1, $$3, $$0.getYRot(), $$0.getXRot());
            ItemStack $$5 = $$0.getItemInHand($$1);
            if ($$0.getCooldowns().isOnCooldown($$5)) {
                $$2.setValue(InteractionResult.PASS);
                return $$4;
            }
            InteractionResult $$6 = $$5.use(this.minecraft.level, $$0, $$1);
            if ($$6 instanceof InteractionResult.Success) {
                InteractionResult.Success $$7 = (InteractionResult.Success)$$6;
                ItemStack $$8 = (ItemStack)Objects.requireNonNullElseGet((Object)$$7.heldItemTransformedTo(), () -> $$0.getItemInHand($$1));
            } else {
                $$9 = $$0.getItemInHand($$1);
            }
            if ($$9 != $$5) {
                $$0.setItemInHand($$1, $$9);
            }
            $$2.setValue($$6);
            return $$4;
        });
        return (InteractionResult)$$2.getValue();
    }

    public LocalPlayer createPlayer(ClientLevel $$0, StatsCounter $$1, ClientRecipeBook $$2) {
        return this.createPlayer($$0, $$1, $$2, Input.EMPTY, false);
    }

    public LocalPlayer createPlayer(ClientLevel $$0, StatsCounter $$1, ClientRecipeBook $$2, Input $$3, boolean $$4) {
        return new LocalPlayer(this.minecraft, $$0, this.connection, $$1, $$2, $$3, $$4);
    }

    public void attack(Player $$0, Entity $$1) {
        this.ensureHasSentCarriedItem();
        this.connection.send(ServerboundInteractPacket.createAttackPacket($$1, $$0.isShiftKeyDown()));
        if (this.localPlayerMode != GameType.SPECTATOR) {
            $$0.attack($$1);
            $$0.resetAttackStrengthTicker();
        }
    }

    public InteractionResult interact(Player $$0, Entity $$1, InteractionHand $$2) {
        this.ensureHasSentCarriedItem();
        this.connection.send(ServerboundInteractPacket.createInteractionPacket($$1, $$0.isShiftKeyDown(), $$2));
        if (this.localPlayerMode == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        return $$0.interactOn($$1, $$2);
    }

    public InteractionResult interactAt(Player $$0, Entity $$1, EntityHitResult $$2, InteractionHand $$3) {
        this.ensureHasSentCarriedItem();
        Vec3 $$4 = $$2.getLocation().subtract($$1.getX(), $$1.getY(), $$1.getZ());
        this.connection.send(ServerboundInteractPacket.createInteractionPacket($$1, $$0.isShiftKeyDown(), $$3, $$4));
        if (this.localPlayerMode == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        return $$1.interactAt($$0, $$4, $$3);
    }

    public void handleInventoryMouseClick(int $$0, int $$1, int $$2, ClickType $$3, Player $$4) {
        AbstractContainerMenu $$5 = $$4.containerMenu;
        if ($$0 != $$5.containerId) {
            LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", (Object)$$0, (Object)$$5.containerId);
            return;
        }
        NonNullList<Slot> $$6 = $$5.slots;
        int $$7 = $$6.size();
        ArrayList<ItemStack> $$8 = Lists.newArrayListWithCapacity($$7);
        for (Slot $$9 : $$6) {
            $$8.add($$9.getItem().copy());
        }
        $$5.clicked($$1, $$2, $$3, $$4);
        Int2ObjectOpenHashMap $$10 = new Int2ObjectOpenHashMap();
        for (int $$11 = 0; $$11 < $$7; ++$$11) {
            ItemStack $$13;
            ItemStack $$12 = (ItemStack)$$8.get($$11);
            if (ItemStack.matches($$12, $$13 = $$6.get($$11).getItem())) continue;
            $$10.put($$11, (Object)HashedStack.create($$13, this.connection.decoratedHashOpsGenenerator()));
        }
        HashedStack $$14 = HashedStack.create($$5.getCarried(), this.connection.decoratedHashOpsGenenerator());
        this.connection.send(new ServerboundContainerClickPacket($$0, $$5.getStateId(), Shorts.checkedCast($$1), SignedBytes.checkedCast($$2), $$3, (Int2ObjectMap<HashedStack>)$$10, $$14));
    }

    public void handlePlaceRecipe(int $$0, RecipeDisplayId $$1, boolean $$2) {
        this.connection.send(new ServerboundPlaceRecipePacket($$0, $$1, $$2));
    }

    public void handleInventoryButtonClick(int $$0, int $$1) {
        this.connection.send(new ServerboundContainerButtonClickPacket($$0, $$1));
    }

    public void handleCreativeModeItemAdd(ItemStack $$0, int $$1) {
        if (this.minecraft.player.hasInfiniteMaterials() && this.connection.isFeatureEnabled($$0.getItem().requiredFeatures())) {
            this.connection.send(new ServerboundSetCreativeModeSlotPacket($$1, $$0));
        }
    }

    public void handleCreativeModeItemDrop(ItemStack $$0) {
        boolean $$1;
        boolean bl = $$1 = this.minecraft.screen instanceof AbstractContainerScreen && !(this.minecraft.screen instanceof CreativeModeInventoryScreen);
        if (this.minecraft.player.hasInfiniteMaterials() && !$$1 && !$$0.isEmpty() && this.connection.isFeatureEnabled($$0.getItem().requiredFeatures())) {
            this.connection.send(new ServerboundSetCreativeModeSlotPacket(-1, $$0));
            this.minecraft.player.getDropSpamThrottler().increment();
        }
    }

    public void releaseUsingItem(Player $$0) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
        $$0.releaseUsingItem();
    }

    public boolean hasExperience() {
        return this.localPlayerMode.isSurvival();
    }

    public boolean hasMissTime() {
        return !this.localPlayerMode.isCreative();
    }

    public boolean isServerControlledInventory() {
        return this.minecraft.player.isPassenger() && this.minecraft.player.getVehicle() instanceof HasCustomInventoryScreen;
    }

    public boolean isAlwaysFlying() {
        return this.localPlayerMode == GameType.SPECTATOR;
    }

    @Nullable
    public GameType getPreviousPlayerMode() {
        return this.previousLocalPlayerMode;
    }

    public GameType getPlayerMode() {
        return this.localPlayerMode;
    }

    public boolean isDestroying() {
        return this.isDestroying;
    }

    public int getDestroyStage() {
        return this.destroyProgress > 0.0f ? (int)(this.destroyProgress * 10.0f) : -1;
    }

    public void handlePickItemFromBlock(BlockPos $$0, boolean $$1) {
        this.connection.send(new ServerboundPickItemFromBlockPacket($$0, $$1));
    }

    public void handlePickItemFromEntity(Entity $$0, boolean $$1) {
        this.connection.send(new ServerboundPickItemFromEntityPacket($$0.getId(), $$1));
    }

    public void handleSlotStateChanged(int $$0, int $$1, boolean $$2) {
        this.connection.send(new ServerboundContainerSlotStateChangedPacket($$0, $$1, $$2));
    }
}

