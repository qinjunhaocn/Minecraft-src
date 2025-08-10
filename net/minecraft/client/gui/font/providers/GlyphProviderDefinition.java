/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import net.minecraft.client.gui.font.FontOption;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public interface GlyphProviderDefinition {
    public static final MapCodec<GlyphProviderDefinition> MAP_CODEC = GlyphProviderType.CODEC.dispatchMap(GlyphProviderDefinition::type, GlyphProviderType::mapCodec);

    public GlyphProviderType type();

    public Either<Loader, Reference> unpack();

    public record Conditional(GlyphProviderDefinition definition, FontOption.Filter filter) {
        public static final Codec<Conditional> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)MAP_CODEC.forGetter(Conditional::definition), (App)FontOption.Filter.CODEC.optionalFieldOf("filter", (Object)FontOption.Filter.ALWAYS_PASS).forGetter(Conditional::filter)).apply((Applicative)$$0, Conditional::new));
    }

    public record Reference(ResourceLocation id) {
    }

    public static interface Loader {
        public GlyphProvider load(ResourceManager var1) throws IOException;
    }
}

