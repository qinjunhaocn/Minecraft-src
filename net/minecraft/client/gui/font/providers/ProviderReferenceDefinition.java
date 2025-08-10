/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.gui.font.providers;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.resources.ResourceLocation;

public record ProviderReferenceDefinition(ResourceLocation id) implements GlyphProviderDefinition
{
    public static final MapCodec<ProviderReferenceDefinition> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("id").forGetter(ProviderReferenceDefinition::id)).apply((Applicative)$$0, ProviderReferenceDefinition::new));

    @Override
    public GlyphProviderType type() {
        return GlyphProviderType.REFERENCE;
    }

    @Override
    public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
        return Either.right((Object)((Object)new GlyphProviderDefinition.Reference(this.id)));
    }
}

