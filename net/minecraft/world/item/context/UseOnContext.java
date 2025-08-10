/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.context;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class UseOnContext {
    @Nullable
    private final Player player;
    private final InteractionHand hand;
    private final BlockHitResult hitResult;
    private final Level level;
    private final ItemStack itemStack;

    public UseOnContext(Player $$0, InteractionHand $$1, BlockHitResult $$2) {
        this($$0.level(), $$0, $$1, $$0.getItemInHand($$1), $$2);
    }

    protected UseOnContext(Level $$0, @Nullable Player $$1, InteractionHand $$2, ItemStack $$3, BlockHitResult $$4) {
        this.player = $$1;
        this.hand = $$2;
        this.hitResult = $$4;
        this.itemStack = $$3;
        this.level = $$0;
    }

    protected final BlockHitResult getHitResult() {
        return this.hitResult;
    }

    public BlockPos getClickedPos() {
        return this.hitResult.getBlockPos();
    }

    public Direction getClickedFace() {
        return this.hitResult.getDirection();
    }

    public Vec3 getClickLocation() {
        return this.hitResult.getLocation();
    }

    public boolean isInside() {
        return this.hitResult.isInside();
    }

    public ItemStack getItemInHand() {
        return this.itemStack;
    }

    @Nullable
    public Player getPlayer() {
        return this.player;
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public Level getLevel() {
        return this.level;
    }

    public Direction getHorizontalDirection() {
        return this.player == null ? Direction.NORTH : this.player.getDirection();
    }

    public boolean isSecondaryUseActive() {
        return this.player != null && this.player.isSecondaryUseActive();
    }

    public float getRotation() {
        return this.player == null ? 0.0f : this.player.getYRot();
    }
}

