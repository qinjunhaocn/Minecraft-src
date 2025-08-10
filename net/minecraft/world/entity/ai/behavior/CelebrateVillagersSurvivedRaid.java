/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.MoveToSkySeeingSpot;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;

public class CelebrateVillagersSurvivedRaid
extends Behavior<Villager> {
    @Nullable
    private Raid currentRaid;

    public CelebrateVillagersSurvivedRaid(int $$0, int $$1) {
        super(ImmutableMap.of(), $$0, $$1);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, Villager $$1) {
        BlockPos $$2 = $$1.blockPosition();
        this.currentRaid = $$0.getRaidAt($$2);
        return this.currentRaid != null && this.currentRaid.isVictory() && MoveToSkySeeingSpot.hasNoBlocksAbove($$0, $$1, $$2);
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Villager $$1, long $$2) {
        return this.currentRaid != null && !this.currentRaid.isStopped();
    }

    @Override
    protected void stop(ServerLevel $$0, Villager $$1, long $$2) {
        this.currentRaid = null;
        $$1.getBrain().updateActivityFromSchedule($$0.getDayTime(), $$0.getGameTime());
    }

    @Override
    protected void tick(ServerLevel $$0, Villager $$1, long $$2) {
        RandomSource $$3 = $$1.getRandom();
        if ($$3.nextInt(100) == 0) {
            $$1.playCelebrateSound();
        }
        if ($$3.nextInt(200) == 0 && MoveToSkySeeingSpot.hasNoBlocksAbove($$0, $$1, $$1.blockPosition())) {
            DyeColor $$4 = Util.a(DyeColor.values(), $$3);
            int $$5 = $$3.nextInt(3);
            ItemStack $$6 = this.getFirework($$4, $$5);
            Projectile.spawnProjectile(new FireworkRocketEntity($$1.level(), $$1, $$1.getX(), $$1.getEyeY(), $$1.getZ(), $$6), $$0, $$6);
        }
    }

    private ItemStack getFirework(DyeColor $$0, int $$1) {
        ItemStack $$2 = new ItemStack(Items.FIREWORK_ROCKET);
        $$2.set(DataComponents.FIREWORKS, new Fireworks((byte)$$1, List.of((Object)new FireworkExplosion(FireworkExplosion.Shape.BURST, IntList.of((int)$$0.getFireworkColor()), IntList.of(), false, false))));
        return $$2;
    }
}

