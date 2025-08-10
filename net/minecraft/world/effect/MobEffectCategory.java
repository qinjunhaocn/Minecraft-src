/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.effect;

import net.minecraft.ChatFormatting;

public final class MobEffectCategory
extends Enum<MobEffectCategory> {
    public static final /* enum */ MobEffectCategory BENEFICIAL = new MobEffectCategory(ChatFormatting.BLUE);
    public static final /* enum */ MobEffectCategory HARMFUL = new MobEffectCategory(ChatFormatting.RED);
    public static final /* enum */ MobEffectCategory NEUTRAL = new MobEffectCategory(ChatFormatting.BLUE);
    private final ChatFormatting tooltipFormatting;
    private static final /* synthetic */ MobEffectCategory[] $VALUES;

    public static MobEffectCategory[] values() {
        return (MobEffectCategory[])$VALUES.clone();
    }

    public static MobEffectCategory valueOf(String $$0) {
        return Enum.valueOf(MobEffectCategory.class, $$0);
    }

    private MobEffectCategory(ChatFormatting $$0) {
        this.tooltipFormatting = $$0;
    }

    public ChatFormatting getTooltipFormatting() {
        return this.tooltipFormatting;
    }

    private static /* synthetic */ MobEffectCategory[] b() {
        return new MobEffectCategory[]{BENEFICIAL, HARMFUL, NEUTRAL};
    }

    static {
        $VALUES = MobEffectCategory.b();
    }
}

