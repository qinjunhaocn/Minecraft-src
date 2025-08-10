/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public final class TerrainAdjustment
extends Enum<TerrainAdjustment>
implements StringRepresentable {
    public static final /* enum */ TerrainAdjustment NONE = new TerrainAdjustment("none");
    public static final /* enum */ TerrainAdjustment BURY = new TerrainAdjustment("bury");
    public static final /* enum */ TerrainAdjustment BEARD_THIN = new TerrainAdjustment("beard_thin");
    public static final /* enum */ TerrainAdjustment BEARD_BOX = new TerrainAdjustment("beard_box");
    public static final /* enum */ TerrainAdjustment ENCAPSULATE = new TerrainAdjustment("encapsulate");
    public static final Codec<TerrainAdjustment> CODEC;
    private final String id;
    private static final /* synthetic */ TerrainAdjustment[] $VALUES;

    public static TerrainAdjustment[] values() {
        return (TerrainAdjustment[])$VALUES.clone();
    }

    public static TerrainAdjustment valueOf(String $$0) {
        return Enum.valueOf(TerrainAdjustment.class, $$0);
    }

    private TerrainAdjustment(String $$0) {
        this.id = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    private static /* synthetic */ TerrainAdjustment[] a() {
        return new TerrainAdjustment[]{NONE, BURY, BEARD_THIN, BEARD_BOX, ENCAPSULATE};
    }

    static {
        $VALUES = TerrainAdjustment.a();
        CODEC = StringRepresentable.fromEnum(TerrainAdjustment::values);
    }
}

