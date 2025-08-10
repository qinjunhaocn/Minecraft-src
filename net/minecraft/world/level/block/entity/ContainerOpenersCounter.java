/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public abstract class ContainerOpenersCounter {
    private static final int CHECK_TICK_DELAY = 5;
    private int openCount;
    private double maxInteractionRange;

    protected abstract void onOpen(Level var1, BlockPos var2, BlockState var3);

    protected abstract void onClose(Level var1, BlockPos var2, BlockState var3);

    protected abstract void openerCountChanged(Level var1, BlockPos var2, BlockState var3, int var4, int var5);

    protected abstract boolean isOwnContainer(Player var1);

    public void incrementOpeners(Player $$0, Level $$1, BlockPos $$2, BlockState $$3) {
        int $$4;
        if (($$4 = this.openCount++) == 0) {
            this.onOpen($$1, $$2, $$3);
            $$1.gameEvent((Entity)$$0, GameEvent.CONTAINER_OPEN, $$2);
            ContainerOpenersCounter.scheduleRecheck($$1, $$2, $$3);
        }
        this.openerCountChanged($$1, $$2, $$3, $$4, this.openCount);
        this.maxInteractionRange = Math.max($$0.blockInteractionRange(), this.maxInteractionRange);
    }

    public void decrementOpeners(Player $$0, Level $$1, BlockPos $$2, BlockState $$3) {
        int $$4 = this.openCount--;
        if (this.openCount == 0) {
            this.onClose($$1, $$2, $$3);
            $$1.gameEvent((Entity)$$0, GameEvent.CONTAINER_CLOSE, $$2);
            this.maxInteractionRange = 0.0;
        }
        this.openerCountChanged($$1, $$2, $$3, $$4, this.openCount);
    }

    private List<Player> getPlayersWithContainerOpen(Level $$0, BlockPos $$1) {
        double $$2 = this.maxInteractionRange + 4.0;
        AABB $$3 = new AABB($$1).inflate($$2);
        return $$0.getEntities(EntityTypeTest.forClass(Player.class), $$3, this::isOwnContainer);
    }

    public void recheckOpeners(Level $$0, BlockPos $$1, BlockState $$2) {
        List<Player> $$3 = this.getPlayersWithContainerOpen($$0, $$1);
        this.maxInteractionRange = 0.0;
        for (Player $$4 : $$3) {
            this.maxInteractionRange = Math.max($$4.blockInteractionRange(), this.maxInteractionRange);
        }
        int $$6 = this.openCount;
        int $$5 = $$3.size();
        if ($$6 != $$5) {
            boolean $$8;
            boolean $$7 = $$5 != 0;
            boolean bl = $$8 = $$6 != 0;
            if ($$7 && !$$8) {
                this.onOpen($$0, $$1, $$2);
                $$0.gameEvent(null, GameEvent.CONTAINER_OPEN, $$1);
            } else if (!$$7) {
                this.onClose($$0, $$1, $$2);
                $$0.gameEvent(null, GameEvent.CONTAINER_CLOSE, $$1);
            }
            this.openCount = $$5;
        }
        this.openerCountChanged($$0, $$1, $$2, $$6, $$5);
        if ($$5 > 0) {
            ContainerOpenersCounter.scheduleRecheck($$0, $$1, $$2);
        }
    }

    public int getOpenerCount() {
        return this.openCount;
    }

    private static void scheduleRecheck(Level $$0, BlockPos $$1, BlockState $$2) {
        $$0.scheduleTick($$1, $$2.getBlock(), 5);
    }
}

