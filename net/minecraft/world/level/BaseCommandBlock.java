/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public abstract class BaseCommandBlock
implements CommandSource {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Component DEFAULT_NAME = Component.literal("@");
    private static final int NO_LAST_EXECUTION = -1;
    private long lastExecution = -1L;
    private boolean updateLastExecution = true;
    private int successCount;
    private boolean trackOutput = true;
    @Nullable
    private Component lastOutput;
    private String command = "";
    @Nullable
    private Component customName;

    public int getSuccessCount() {
        return this.successCount;
    }

    public void setSuccessCount(int $$0) {
        this.successCount = $$0;
    }

    public Component getLastOutput() {
        return this.lastOutput == null ? CommonComponents.EMPTY : this.lastOutput;
    }

    public void save(ValueOutput $$0) {
        $$0.putString("Command", this.command);
        $$0.putInt("SuccessCount", this.successCount);
        $$0.storeNullable("CustomName", ComponentSerialization.CODEC, this.customName);
        $$0.putBoolean("TrackOutput", this.trackOutput);
        if (this.trackOutput) {
            $$0.storeNullable("LastOutput", ComponentSerialization.CODEC, this.lastOutput);
        }
        $$0.putBoolean("UpdateLastExecution", this.updateLastExecution);
        if (this.updateLastExecution && this.lastExecution != -1L) {
            $$0.putLong("LastExecution", this.lastExecution);
        }
    }

    public void load(ValueInput $$0) {
        this.command = $$0.getStringOr("Command", "");
        this.successCount = $$0.getIntOr("SuccessCount", 0);
        this.setCustomName(BlockEntity.parseCustomNameSafe($$0, "CustomName"));
        this.trackOutput = $$0.getBooleanOr("TrackOutput", true);
        this.lastOutput = this.trackOutput ? BlockEntity.parseCustomNameSafe($$0, "LastOutput") : null;
        this.updateLastExecution = $$0.getBooleanOr("UpdateLastExecution", true);
        this.lastExecution = this.updateLastExecution ? $$0.getLongOr("LastExecution", -1L) : -1L;
    }

    public void setCommand(String $$0) {
        this.command = $$0;
        this.successCount = 0;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean performCommand(Level $$02) {
        if ($$02.isClientSide || $$02.getGameTime() == this.lastExecution) {
            return false;
        }
        if ("Searge".equalsIgnoreCase(this.command)) {
            this.lastOutput = Component.literal("#itzlipofutzli");
            this.successCount = 1;
            return true;
        }
        this.successCount = 0;
        MinecraftServer $$12 = this.getLevel().getServer();
        if ($$12.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
            try {
                this.lastOutput = null;
                CommandSourceStack $$2 = this.createCommandSourceStack().withCallback(($$0, $$1) -> {
                    if ($$0) {
                        ++this.successCount;
                    }
                });
                $$12.getCommands().performPrefixedCommand($$2, this.command);
            } catch (Throwable $$3) {
                CrashReport $$4 = CrashReport.forThrowable($$3, "Executing command block");
                CrashReportCategory $$5 = $$4.addCategory("Command to be executed");
                $$5.setDetail("Command", this::getCommand);
                $$5.setDetail("Name", () -> this.getName().getString());
                throw new ReportedException($$4);
            }
        }
        this.lastExecution = this.updateLastExecution ? $$02.getGameTime() : -1L;
        return true;
    }

    public Component getName() {
        return this.customName != null ? this.customName : DEFAULT_NAME;
    }

    @Nullable
    public Component getCustomName() {
        return this.customName;
    }

    public void setCustomName(@Nullable Component $$0) {
        this.customName = $$0;
    }

    @Override
    public void sendSystemMessage(Component $$0) {
        if (this.trackOutput) {
            this.lastOutput = Component.literal("[" + TIME_FORMAT.format(new Date()) + "] ").append($$0);
            this.onUpdated();
        }
    }

    public abstract ServerLevel getLevel();

    public abstract void onUpdated();

    public void setLastOutput(@Nullable Component $$0) {
        this.lastOutput = $$0;
    }

    public void setTrackOutput(boolean $$0) {
        this.trackOutput = $$0;
    }

    public boolean isTrackOutput() {
        return this.trackOutput;
    }

    public InteractionResult usedBy(Player $$0) {
        if (!$$0.canUseGameMasterBlocks()) {
            return InteractionResult.PASS;
        }
        if ($$0.level().isClientSide) {
            $$0.openMinecartCommandBlock(this);
        }
        return InteractionResult.SUCCESS;
    }

    public abstract Vec3 getPosition();

    public abstract CommandSourceStack createCommandSourceStack();

    @Override
    public boolean acceptsSuccess() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
    }

    @Override
    public boolean acceptsFailure() {
        return this.trackOutput;
    }

    @Override
    public boolean shouldInformAdmins() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
    }

    public abstract boolean isValid();
}

