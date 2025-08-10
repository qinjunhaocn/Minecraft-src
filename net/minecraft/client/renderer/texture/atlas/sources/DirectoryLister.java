/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public record DirectoryLister(String sourcePath, String idPrefix) implements SpriteSource
{
    public static final MapCodec<DirectoryLister> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.STRING.fieldOf("source").forGetter(DirectoryLister::sourcePath), (App)Codec.STRING.fieldOf("prefix").forGetter(DirectoryLister::idPrefix)).apply((Applicative)$$0, DirectoryLister::new));

    @Override
    public void run(ResourceManager $$0, SpriteSource.Output $$1) {
        FileToIdConverter $$22 = new FileToIdConverter("textures/" + this.sourcePath, ".png");
        $$22.listMatchingResources($$0).forEach(($$2, $$3) -> {
            ResourceLocation $$4 = $$22.fileToId((ResourceLocation)$$2).withPrefix(this.idPrefix);
            $$1.add($$4, (Resource)$$3);
        });
    }

    public MapCodec<DirectoryLister> codec() {
        return MAP_CODEC;
    }
}

