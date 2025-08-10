/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.block.state.properties;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public final class StructureMode
extends Enum<StructureMode>
implements StringRepresentable {
    public static final /* enum */ StructureMode SAVE = new StructureMode("save");
    public static final /* enum */ StructureMode LOAD = new StructureMode("load");
    public static final /* enum */ StructureMode CORNER = new StructureMode("corner");
    public static final /* enum */ StructureMode DATA = new StructureMode("data");
    @Deprecated
    public static final Codec<StructureMode> LEGACY_CODEC;
    private final String name;
    private final Component displayName;
    private static final /* synthetic */ StructureMode[] $VALUES;

    public static StructureMode[] values() {
        return (StructureMode[])$VALUES.clone();
    }

    public static StructureMode valueOf(String $$0) {
        return Enum.valueOf(StructureMode.class, $$0);
    }

    private StructureMode(String $$0) {
        this.name = $$0;
        this.displayName = Component.translatable("structure_block.mode_info." + $$0);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    private static /* synthetic */ StructureMode[] b() {
        return new StructureMode[]{SAVE, LOAD, CORNER, DATA};
    }

    static {
        $VALUES = StructureMode.b();
        LEGACY_CODEC = ExtraCodecs.legacyEnum(StructureMode::valueOf);
    }
}

