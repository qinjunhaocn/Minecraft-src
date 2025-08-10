/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public record SingleFile(ResourceLocation resourceId, Optional<ResourceLocation> spriteId) implements SpriteSource
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<SingleFile> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("resource").forGetter(SingleFile::resourceId), (App)ResourceLocation.CODEC.optionalFieldOf("sprite").forGetter(SingleFile::spriteId)).apply((Applicative)$$0, SingleFile::new));

    public SingleFile(ResourceLocation $$0) {
        this($$0, Optional.empty());
    }

    @Override
    public void run(ResourceManager $$0, SpriteSource.Output $$1) {
        ResourceLocation $$2 = TEXTURE_ID_CONVERTER.idToFile(this.resourceId);
        Optional<Resource> $$3 = $$0.getResource($$2);
        if ($$3.isPresent()) {
            $$1.add(this.spriteId.orElse(this.resourceId), $$3.get());
        } else {
            LOGGER.warn("Missing sprite: {}", (Object)$$2);
        }
    }

    public MapCodec<SingleFile> codec() {
        return MAP_CODEC;
    }
}

