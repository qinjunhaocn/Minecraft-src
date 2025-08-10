/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.SpaceProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.ProviderReferenceDefinition;
import net.minecraft.client.gui.font.providers.TrueTypeGlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.UnihexProvider;
import net.minecraft.util.StringRepresentable;

public final class GlyphProviderType
extends Enum<GlyphProviderType>
implements StringRepresentable {
    public static final /* enum */ GlyphProviderType BITMAP = new GlyphProviderType("bitmap", BitmapProvider.Definition.CODEC);
    public static final /* enum */ GlyphProviderType TTF = new GlyphProviderType("ttf", TrueTypeGlyphProviderDefinition.CODEC);
    public static final /* enum */ GlyphProviderType SPACE = new GlyphProviderType("space", SpaceProvider.Definition.CODEC);
    public static final /* enum */ GlyphProviderType UNIHEX = new GlyphProviderType("unihex", UnihexProvider.Definition.CODEC);
    public static final /* enum */ GlyphProviderType REFERENCE = new GlyphProviderType("reference", ProviderReferenceDefinition.CODEC);
    public static final Codec<GlyphProviderType> CODEC;
    private final String name;
    private final MapCodec<? extends GlyphProviderDefinition> codec;
    private static final /* synthetic */ GlyphProviderType[] $VALUES;

    public static GlyphProviderType[] values() {
        return (GlyphProviderType[])$VALUES.clone();
    }

    public static GlyphProviderType valueOf(String $$0) {
        return Enum.valueOf(GlyphProviderType.class, $$0);
    }

    private GlyphProviderType(String $$0, MapCodec<? extends GlyphProviderDefinition> $$1) {
        this.name = $$0;
        this.codec = $$1;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public MapCodec<? extends GlyphProviderDefinition> mapCodec() {
        return this.codec;
    }

    private static /* synthetic */ GlyphProviderType[] b() {
        return new GlyphProviderType[]{BITMAP, TTF, SPACE, UNIHEX, REFERENCE};
    }

    static {
        $VALUES = GlyphProviderType.b();
        CODEC = StringRepresentable.fromEnum(GlyphProviderType::values);
    }
}

