/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.structure;

public final class StructureCheckResult
extends Enum<StructureCheckResult> {
    public static final /* enum */ StructureCheckResult START_PRESENT = new StructureCheckResult();
    public static final /* enum */ StructureCheckResult START_NOT_PRESENT = new StructureCheckResult();
    public static final /* enum */ StructureCheckResult CHUNK_LOAD_NEEDED = new StructureCheckResult();
    private static final /* synthetic */ StructureCheckResult[] $VALUES;

    public static StructureCheckResult[] values() {
        return (StructureCheckResult[])$VALUES.clone();
    }

    public static StructureCheckResult valueOf(String $$0) {
        return Enum.valueOf(StructureCheckResult.class, $$0);
    }

    private static /* synthetic */ StructureCheckResult[] a() {
        return new StructureCheckResult[]{START_PRESENT, START_NOT_PRESENT, CHUNK_LOAD_NEEDED};
    }

    static {
        $VALUES = StructureCheckResult.a();
    }
}

