/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class WindChargeItem
extends Item
implements ProjectileItem {
    public static float PROJECTILE_SHOOT_POWER = 1.5f;

    public WindChargeItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult use(Level $$0, Player $$1, InteractionHand $$22) {
        ItemStack $$32 = $$1.getItemInHand($$22);
        if ($$0 instanceof ServerLevel) {
            ServerLevel $$42 = (ServerLevel)$$0;
            Projectile.spawnProjectileFromRotation(($$2, $$3, $$4) -> new WindCharge($$1, $$0, $$1.position().x(), $$1.getEyePosition().y(), $$1.position().z()), $$42, $$32, $$1, 0.0f, PROJECTILE_SHOOT_POWER, 1.0f);
        }
        $$0.playSound(null, $$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.WIND_CHARGE_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / ($$0.getRandom().nextFloat() * 0.4f + 0.8f));
        $$1.awardStat(Stats.ITEM_USED.get(this));
        $$32.consume(1, $$1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Projectile asProjectile(Level $$0, Position $$1, ItemStack $$2, Direction $$3) {
        RandomSource $$4 = $$0.getRandom();
        double $$5 = $$4.triangle((double)$$3.getStepX(), 0.11485000000000001);
        double $$6 = $$4.triangle((double)$$3.getStepY(), 0.11485000000000001);
        double $$7 = $$4.triangle((double)$$3.getStepZ(), 0.11485000000000001);
        Vec3 $$8 = new Vec3($$5, $$6, $$7);
        WindCharge $$9 = new WindCharge($$0, $$1.x(), $$1.y(), $$1.z(), $$8);
        $$9.setDeltaMovement($$8);
        return $$9;
    }

    @Override
    public void shoot(Projectile $$0, double $$1, double $$2, double $$3, float $$4, float $$5) {
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder().positionFunction(($$0, $$1) -> DispenserBlock.getDispensePosition($$0, 1.0, Vec3.ZERO)).uncertainty(6.6666665f).power(1.0f).overrideDispenseEvent(1051).build();
    }
}

