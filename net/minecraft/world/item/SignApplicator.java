/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

public interface SignApplicator {
    public boolean tryApplyToSign(Level var1, SignBlockEntity var2, boolean var3, Player var4);

    default public boolean canApplyToSign(SignText $$0, Player $$1) {
        return $$0.hasMessage($$1);
    }
}

