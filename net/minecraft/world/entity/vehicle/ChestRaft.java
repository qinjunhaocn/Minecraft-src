/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.vehicle;

import java.util.function.Supplier;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ChestRaft
extends AbstractChestBoat {
    public ChestRaft(EntityType<? extends ChestRaft> $$0, Level $$1, Supplier<Item> $$2) {
        super((EntityType<? extends AbstractChestBoat>)$$0, $$1, $$2);
    }

    @Override
    protected double rideHeight(EntityDimensions $$0) {
        return $$0.height() * 0.8888889f;
    }
}

