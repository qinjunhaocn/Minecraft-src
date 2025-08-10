/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.world.entity.ConversionType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.scores.PlayerTeam;

public record ConversionParams(ConversionType type, boolean keepEquipment, boolean preserveCanPickUpLoot, @Nullable PlayerTeam team) {
    public static ConversionParams single(Mob $$0, boolean $$1, boolean $$2) {
        return new ConversionParams(ConversionType.SINGLE, $$1, $$2, $$0.getTeam());
    }

    @Nullable
    public PlayerTeam team() {
        return this.team;
    }

    @FunctionalInterface
    public static interface AfterConversion<T extends Mob> {
        public void finalizeConversion(T var1);
    }
}

