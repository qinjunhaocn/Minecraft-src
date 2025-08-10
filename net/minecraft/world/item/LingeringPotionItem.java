/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.ThrownLingeringPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.level.Level;

public class LingeringPotionItem
extends ThrowablePotionItem {
    public LingeringPotionItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult use(Level $$0, Player $$1, InteractionHand $$2) {
        $$0.playSound(null, $$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.LINGERING_POTION_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / ($$0.getRandom().nextFloat() * 0.4f + 0.8f));
        return super.use($$0, $$1, $$2);
    }

    @Override
    protected AbstractThrownPotion createPotion(ServerLevel $$0, LivingEntity $$1, ItemStack $$2) {
        return new ThrownLingeringPotion($$0, $$1, $$2);
    }

    @Override
    protected AbstractThrownPotion createPotion(Level $$0, Position $$1, ItemStack $$2) {
        return new ThrownLingeringPotion($$0, $$1.x(), $$1.y(), $$1.z(), $$2);
    }
}

