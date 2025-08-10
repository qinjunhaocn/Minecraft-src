/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public final class LiquidSettings
extends Enum<LiquidSettings>
implements StringRepresentable {
    public static final /* enum */ LiquidSettings IGNORE_WATERLOGGING = new LiquidSettings("ignore_waterlogging");
    public static final /* enum */ LiquidSettings APPLY_WATERLOGGING = new LiquidSettings("apply_waterlogging");
    public static Codec<LiquidSettings> CODEC;
    private final String name;
    private static final /* synthetic */ LiquidSettings[] $VALUES;

    public static LiquidSettings[] values() {
        return (LiquidSettings[])$VALUES.clone();
    }

    public static LiquidSettings valueOf(String $$0) {
        return Enum.valueOf(LiquidSettings.class, $$0);
    }

    private LiquidSettings(String $$0) {
        this.name = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ LiquidSettings[] a() {
        return new LiquidSettings[]{IGNORE_WATERLOGGING, APPLY_WATERLOGGING};
    }

    static {
        $VALUES = LiquidSettings.a();
        CODEC = StringRepresentable.fromValues(LiquidSettings::values);
    }
}

