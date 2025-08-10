/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.packs.repository;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.InclusiveRange;

public final class PackCompatibility
extends Enum<PackCompatibility> {
    public static final /* enum */ PackCompatibility TOO_OLD = new PackCompatibility("old");
    public static final /* enum */ PackCompatibility TOO_NEW = new PackCompatibility("new");
    public static final /* enum */ PackCompatibility COMPATIBLE = new PackCompatibility("compatible");
    private final Component description;
    private final Component confirmation;
    private static final /* synthetic */ PackCompatibility[] $VALUES;

    public static PackCompatibility[] values() {
        return (PackCompatibility[])$VALUES.clone();
    }

    public static PackCompatibility valueOf(String $$0) {
        return Enum.valueOf(PackCompatibility.class, $$0);
    }

    private PackCompatibility(String $$0) {
        this.description = Component.translatable("pack.incompatible." + $$0).withStyle(ChatFormatting.GRAY);
        this.confirmation = Component.translatable("pack.incompatible.confirm." + $$0);
    }

    public boolean isCompatible() {
        return this == COMPATIBLE;
    }

    public static PackCompatibility forVersion(InclusiveRange<Integer> $$0, int $$1) {
        if ($$0.maxInclusive() < $$1) {
            return TOO_OLD;
        }
        if ($$1 < $$0.minInclusive()) {
            return TOO_NEW;
        }
        return COMPATIBLE;
    }

    public Component getDescription() {
        return this.description;
    }

    public Component getConfirmation() {
        return this.confirmation;
    }

    private static /* synthetic */ PackCompatibility[] d() {
        return new PackCompatibility[]{TOO_OLD, TOO_NEW, COMPATIBLE};
    }

    static {
        $VALUES = PackCompatibility.d();
    }
}

