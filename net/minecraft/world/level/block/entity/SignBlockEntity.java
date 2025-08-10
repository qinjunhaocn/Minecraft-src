/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.FilteredText;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class SignBlockEntity
extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_TEXT_LINE_WIDTH = 90;
    private static final int TEXT_LINE_HEIGHT = 10;
    private static final boolean DEFAULT_IS_WAXED = false;
    @Nullable
    private UUID playerWhoMayEdit;
    private SignText frontText = this.createDefaultSignText();
    private SignText backText = this.createDefaultSignText();
    private boolean isWaxed = false;

    public SignBlockEntity(BlockPos $$0, BlockState $$1) {
        this((BlockEntityType)BlockEntityType.SIGN, $$0, $$1);
    }

    public SignBlockEntity(BlockEntityType $$0, BlockPos $$1, BlockState $$2) {
        super($$0, $$1, $$2);
    }

    protected SignText createDefaultSignText() {
        return new SignText();
    }

    public boolean isFacingFrontText(Player $$0) {
        Block block = this.getBlockState().getBlock();
        if (block instanceof SignBlock) {
            float $$6;
            SignBlock $$1 = (SignBlock)block;
            Vec3 $$2 = $$1.getSignHitboxCenterPosition(this.getBlockState());
            double $$3 = $$0.getX() - ((double)this.getBlockPos().getX() + $$2.x);
            double $$4 = $$0.getZ() - ((double)this.getBlockPos().getZ() + $$2.z);
            float $$5 = $$1.getYRotationDegrees(this.getBlockState());
            return Mth.degreesDifferenceAbs($$5, $$6 = (float)(Mth.atan2($$4, $$3) * 57.2957763671875) - 90.0f) <= 90.0f;
        }
        return false;
    }

    public SignText getText(boolean $$0) {
        return $$0 ? this.frontText : this.backText;
    }

    public SignText getFrontText() {
        return this.frontText;
    }

    public SignText getBackText() {
        return this.backText;
    }

    public int getTextLineHeight() {
        return 10;
    }

    public int getMaxTextLineWidth() {
        return 90;
    }

    @Override
    protected void saveAdditional(ValueOutput $$0) {
        super.saveAdditional($$0);
        $$0.store("front_text", SignText.DIRECT_CODEC, this.frontText);
        $$0.store("back_text", SignText.DIRECT_CODEC, this.backText);
        $$0.putBoolean("is_waxed", this.isWaxed);
    }

    @Override
    protected void loadAdditional(ValueInput $$0) {
        super.loadAdditional($$0);
        this.frontText = $$0.read("front_text", SignText.DIRECT_CODEC).map(this::loadLines).orElseGet(SignText::new);
        this.backText = $$0.read("back_text", SignText.DIRECT_CODEC).map(this::loadLines).orElseGet(SignText::new);
        this.isWaxed = $$0.getBooleanOr("is_waxed", false);
    }

    private SignText loadLines(SignText $$0) {
        for (int $$1 = 0; $$1 < 4; ++$$1) {
            Component $$2 = this.loadLine($$0.getMessage($$1, false));
            Component $$3 = this.loadLine($$0.getMessage($$1, true));
            $$0 = $$0.setMessage($$1, $$2, $$3);
        }
        return $$0;
    }

    private Component loadLine(Component $$0) {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel $$1 = (ServerLevel)level;
            try {
                return ComponentUtils.updateForEntity(SignBlockEntity.createCommandSourceStack(null, $$1, this.worldPosition), $$0, null, 0);
            } catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }
        return $$0;
    }

    public void updateSignText(Player $$0, boolean $$1, List<FilteredText> $$22) {
        if (this.isWaxed() || !$$0.getUUID().equals(this.getPlayerWhoMayEdit()) || this.level == null) {
            LOGGER.warn("Player {} just tried to change non-editable sign", (Object)$$0.getName().getString());
            return;
        }
        this.updateText($$2 -> this.setMessages($$0, $$22, (SignText)$$2), $$1);
        this.setAllowedPlayerEditor(null);
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public boolean updateText(UnaryOperator<SignText> $$0, boolean $$1) {
        SignText $$2 = this.getText($$1);
        return this.setText((SignText)$$0.apply($$2), $$1);
    }

    private SignText setMessages(Player $$0, List<FilteredText> $$1, SignText $$2) {
        for (int $$3 = 0; $$3 < $$1.size(); ++$$3) {
            FilteredText $$4 = $$1.get($$3);
            Style $$5 = $$2.getMessage($$3, $$0.isTextFilteringEnabled()).getStyle();
            $$2 = $$0.isTextFilteringEnabled() ? $$2.setMessage($$3, Component.literal($$4.filteredOrEmpty()).setStyle($$5)) : $$2.setMessage($$3, Component.literal($$4.raw()).setStyle($$5), Component.literal($$4.filteredOrEmpty()).setStyle($$5));
        }
        return $$2;
    }

    public boolean setText(SignText $$0, boolean $$1) {
        return $$1 ? this.setFrontText($$0) : this.setBackText($$0);
    }

    private boolean setBackText(SignText $$0) {
        if ($$0 != this.backText) {
            this.backText = $$0;
            this.markUpdated();
            return true;
        }
        return false;
    }

    private boolean setFrontText(SignText $$0) {
        if ($$0 != this.frontText) {
            this.frontText = $$0;
            this.markUpdated();
            return true;
        }
        return false;
    }

    public boolean canExecuteClickCommands(boolean $$0, Player $$1) {
        return this.isWaxed() && this.getText($$0).hasAnyClickCommands($$1);
    }

    public boolean executeClickCommandsIfPresent(ServerLevel $$0, Player $$1, BlockPos $$2, boolean $$3) {
        boolean $$4 = false;
        block5: for (Component $$5 : this.getText($$3).b($$1.isTextFilteringEnabled())) {
            ClickEvent $$7;
            Style $$6 = $$5.getStyle();
            ClickEvent clickEvent = $$7 = $$6.getClickEvent();
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.RunCommand.class, ClickEvent.ShowDialog.class, ClickEvent.Custom.class}, (Object)clickEvent, (int)n)) {
                case 0: {
                    ClickEvent.RunCommand $$8 = (ClickEvent.RunCommand)clickEvent;
                    $$0.getServer().getCommands().performPrefixedCommand(SignBlockEntity.createCommandSourceStack($$1, $$0, $$2), $$8.command());
                    $$4 = true;
                    continue block5;
                }
                case 1: {
                    ClickEvent.ShowDialog $$9 = (ClickEvent.ShowDialog)clickEvent;
                    $$1.openDialog($$9.dialog());
                    $$4 = true;
                    continue block5;
                }
                case 2: {
                    ClickEvent.Custom $$10 = (ClickEvent.Custom)clickEvent;
                    $$0.getServer().handleCustomClickAction($$10.id(), $$10.payload());
                    $$4 = true;
                    continue block5;
                }
            }
        }
        return $$4;
    }

    private static CommandSourceStack createCommandSourceStack(@Nullable Player $$0, ServerLevel $$1, BlockPos $$2) {
        String $$3 = $$0 == null ? "Sign" : $$0.getName().getString();
        Component $$4 = $$0 == null ? Component.literal("Sign") : $$0.getDisplayName();
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf($$2), Vec2.ZERO, $$1, 2, $$3, $$4, $$1.getServer(), $$0);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider $$0) {
        return this.saveCustomOnly($$0);
    }

    public void setAllowedPlayerEditor(@Nullable UUID $$0) {
        this.playerWhoMayEdit = $$0;
    }

    @Nullable
    public UUID getPlayerWhoMayEdit() {
        return this.playerWhoMayEdit;
    }

    private void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public boolean isWaxed() {
        return this.isWaxed;
    }

    public boolean setWaxed(boolean $$0) {
        if (this.isWaxed != $$0) {
            this.isWaxed = $$0;
            this.markUpdated();
            return true;
        }
        return false;
    }

    public boolean playerIsTooFarAwayToEdit(UUID $$0) {
        Player $$1 = this.level.getPlayerByUUID($$0);
        return $$1 == null || !$$1.canInteractWithBlock(this.getBlockPos(), 4.0);
    }

    public static void tick(Level $$0, BlockPos $$1, BlockState $$2, SignBlockEntity $$3) {
        UUID $$4 = $$3.getPlayerWhoMayEdit();
        if ($$4 != null) {
            $$3.clearInvalidPlayerWhoMayEdit($$3, $$0, $$4);
        }
    }

    private void clearInvalidPlayerWhoMayEdit(SignBlockEntity $$0, Level $$1, UUID $$2) {
        if ($$0.playerIsTooFarAwayToEdit($$2)) {
            $$0.setAllowedPlayerEditor(null);
        }
    }

    public SoundEvent getSignInteractionFailedSoundEvent() {
        return SoundEvents.WAXED_SIGN_INTERACT_FAIL;
    }

    public /* synthetic */ Packet getUpdatePacket() {
        return this.getUpdatePacket();
    }
}

