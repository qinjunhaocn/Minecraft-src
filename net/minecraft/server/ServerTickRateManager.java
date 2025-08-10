/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundTickingStatePacket;
import net.minecraft.network.protocol.game.ClientboundTickingStepPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.TickRateManager;

public class ServerTickRateManager
extends TickRateManager {
    private long remainingSprintTicks = 0L;
    private long sprintTickStartTime = 0L;
    private long sprintTimeSpend = 0L;
    private long scheduledCurrentSprintTicks = 0L;
    private boolean previousIsFrozen = false;
    private final MinecraftServer server;

    public ServerTickRateManager(MinecraftServer $$0) {
        this.server = $$0;
    }

    public boolean isSprinting() {
        return this.scheduledCurrentSprintTicks > 0L;
    }

    @Override
    public void setFrozen(boolean $$0) {
        super.setFrozen($$0);
        this.updateStateToClients();
    }

    private void updateStateToClients() {
        this.server.getPlayerList().broadcastAll(ClientboundTickingStatePacket.from(this));
    }

    private void updateStepTicks() {
        this.server.getPlayerList().broadcastAll(ClientboundTickingStepPacket.from(this));
    }

    public boolean stepGameIfPaused(int $$0) {
        if (!this.isFrozen()) {
            return false;
        }
        this.frozenTicksToRun = $$0;
        this.updateStepTicks();
        return true;
    }

    public boolean stopStepping() {
        if (this.frozenTicksToRun > 0) {
            this.frozenTicksToRun = 0;
            this.updateStepTicks();
            return true;
        }
        return false;
    }

    public boolean stopSprinting() {
        if (this.remainingSprintTicks > 0L) {
            this.finishTickSprint();
            return true;
        }
        return false;
    }

    public boolean requestGameToSprint(int $$0) {
        boolean $$1 = this.remainingSprintTicks > 0L;
        this.sprintTimeSpend = 0L;
        this.scheduledCurrentSprintTicks = $$0;
        this.remainingSprintTicks = $$0;
        this.previousIsFrozen = this.isFrozen();
        this.setFrozen(false);
        return $$1;
    }

    private void finishTickSprint() {
        long $$0 = this.scheduledCurrentSprintTicks - this.remainingSprintTicks;
        double $$1 = Math.max(1.0, (double)this.sprintTimeSpend) / (double)TimeUtil.NANOSECONDS_PER_MILLISECOND;
        int $$2 = (int)((double)(TimeUtil.MILLISECONDS_PER_SECOND * $$0) / $$1);
        String $$3 = String.format("%.2f", $$0 == 0L ? (double)this.millisecondsPerTick() : $$1 / (double)$$0);
        this.scheduledCurrentSprintTicks = 0L;
        this.sprintTimeSpend = 0L;
        this.server.createCommandSourceStack().sendSuccess(() -> Component.a("commands.tick.sprint.report", $$2, $$3), true);
        this.remainingSprintTicks = 0L;
        this.setFrozen(this.previousIsFrozen);
        this.server.onTickRateChanged();
    }

    public boolean checkShouldSprintThisTick() {
        if (!this.runGameElements) {
            return false;
        }
        if (this.remainingSprintTicks > 0L) {
            this.sprintTickStartTime = System.nanoTime();
            --this.remainingSprintTicks;
            return true;
        }
        this.finishTickSprint();
        return false;
    }

    public void endTickWork() {
        this.sprintTimeSpend += System.nanoTime() - this.sprintTickStartTime;
    }

    @Override
    public void setTickRate(float $$0) {
        super.setTickRate($$0);
        this.server.onTickRateChanged();
        this.updateStateToClients();
    }

    public void updateJoiningPlayer(ServerPlayer $$0) {
        $$0.connection.send(ClientboundTickingStatePacket.from(this));
        $$0.connection.send(ClientboundTickingStepPacket.from(this));
    }
}

