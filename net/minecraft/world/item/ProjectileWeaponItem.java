/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public abstract class ProjectileWeaponItem
extends Item {
    public static final Predicate<ItemStack> ARROW_ONLY = $$0 -> $$0.is(ItemTags.ARROWS);
    public static final Predicate<ItemStack> ARROW_OR_FIREWORK = ARROW_ONLY.or($$0 -> $$0.is(Items.FIREWORK_ROCKET));

    public ProjectileWeaponItem(Item.Properties $$0) {
        super($$0);
    }

    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return this.getAllSupportedProjectiles();
    }

    public abstract Predicate<ItemStack> getAllSupportedProjectiles();

    public static ItemStack getHeldProjectile(LivingEntity $$0, Predicate<ItemStack> $$1) {
        if ($$1.test($$0.getItemInHand(InteractionHand.OFF_HAND))) {
            return $$0.getItemInHand(InteractionHand.OFF_HAND);
        }
        if ($$1.test($$0.getItemInHand(InteractionHand.MAIN_HAND))) {
            return $$0.getItemInHand(InteractionHand.MAIN_HAND);
        }
        return ItemStack.EMPTY;
    }

    public abstract int getDefaultProjectileRange();

    protected void shoot(ServerLevel $$0, LivingEntity $$1, InteractionHand $$2, ItemStack $$3, List<ItemStack> $$4, float $$5, float $$62, boolean $$7, @Nullable LivingEntity $$8) {
        float $$9 = EnchantmentHelper.processProjectileSpread($$0, $$3, $$1, 0.0f);
        float $$10 = $$4.size() == 1 ? 0.0f : 2.0f * $$9 / (float)($$4.size() - 1);
        float $$11 = (float)(($$4.size() - 1) % 2) * $$10 / 2.0f;
        float $$12 = 1.0f;
        for (int $$13 = 0; $$13 < $$4.size(); ++$$13) {
            ItemStack $$14 = $$4.get($$13);
            if ($$14.isEmpty()) continue;
            float $$15 = $$11 + $$12 * (float)(($$13 + 1) / 2) * $$10;
            $$12 = -$$12;
            int $$16 = $$13;
            Projectile.spawnProjectile(this.createProjectile($$0, $$1, $$3, $$14, $$7), $$0, $$14, $$6 -> this.shootProjectile($$1, (Projectile)$$6, $$16, $$5, $$62, $$15, $$8));
            $$3.hurtAndBreak(this.getDurabilityUse($$14), $$1, LivingEntity.getSlotForHand($$2));
            if ($$3.isEmpty()) break;
        }
    }

    protected int getDurabilityUse(ItemStack $$0) {
        return 1;
    }

    protected abstract void shootProjectile(LivingEntity var1, Projectile var2, int var3, float var4, float var5, float var6, @Nullable LivingEntity var7);

    protected Projectile createProjectile(Level $$0, LivingEntity $$1, ItemStack $$2, ItemStack $$3, boolean $$4) {
        ArrowItem $$5;
        Item item = $$3.getItem();
        ArrowItem $$6 = item instanceof ArrowItem ? ($$5 = (ArrowItem)item) : (ArrowItem)Items.ARROW;
        AbstractArrow $$7 = $$6.createArrow($$0, $$3, $$1, $$2);
        if ($$4) {
            $$7.setCritArrow(true);
        }
        return $$7;
    }

    protected static List<ItemStack> draw(ItemStack $$0, ItemStack $$1, LivingEntity $$2) {
        int n;
        if ($$1.isEmpty()) {
            return List.of();
        }
        Level level = $$2.level();
        if (level instanceof ServerLevel) {
            ServerLevel $$3 = (ServerLevel)level;
            n = EnchantmentHelper.processProjectileCount($$3, $$0, $$2, 1);
        } else {
            n = 1;
        }
        int $$4 = n;
        ArrayList<ItemStack> $$5 = new ArrayList<ItemStack>($$4);
        ItemStack $$6 = $$1.copy();
        for (int $$7 = 0; $$7 < $$4; ++$$7) {
            ItemStack $$8 = ProjectileWeaponItem.useAmmo($$0, $$7 == 0 ? $$1 : $$6, $$2, $$7 > 0);
            if ($$8.isEmpty()) continue;
            $$5.add($$8);
        }
        return $$5;
    }

    protected static ItemStack useAmmo(ItemStack $$0, ItemStack $$1, LivingEntity $$2, boolean $$3) {
        int $$5;
        Level level;
        if (!$$3 && !$$2.hasInfiniteMaterials() && (level = $$2.level()) instanceof ServerLevel) {
            ServerLevel $$4 = (ServerLevel)level;
            v0 = EnchantmentHelper.processAmmoUse($$4, $$0, $$1, 1);
        } else {
            v0 = $$5 = 0;
        }
        if ($$5 > $$1.getCount()) {
            return ItemStack.EMPTY;
        }
        if ($$5 == 0) {
            ItemStack $$6 = $$1.copyWithCount(1);
            $$6.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
            return $$6;
        }
        ItemStack $$7 = $$1.split($$5);
        if ($$1.isEmpty() && $$2 instanceof Player) {
            Player $$8 = (Player)$$2;
            $$8.getInventory().removeItem($$1);
        }
        return $$7;
    }
}

