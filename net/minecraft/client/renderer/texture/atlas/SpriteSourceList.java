/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 */
package net.minecraft.client.renderer.texture.atlas;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;
import org.slf4j.Logger;

public class SpriteSourceList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter ATLAS_INFO_CONVERTER = new FileToIdConverter("atlases", ".json");
    private final List<SpriteSource> sources;

    private SpriteSourceList(List<SpriteSource> $$0) {
        this.sources = $$0;
    }

    public List<Function<SpriteResourceLoader, SpriteContents>> list(ResourceManager $$02) {
        final HashMap $$1 = new HashMap();
        SpriteSource.Output $$22 = new SpriteSource.Output(){

            @Override
            public void add(ResourceLocation $$0, SpriteSource.SpriteSupplier $$12) {
                SpriteSource.SpriteSupplier $$2 = $$1.put($$0, $$12);
                if ($$2 != null) {
                    $$2.discard();
                }
            }

            @Override
            public void removeAll(Predicate<ResourceLocation> $$0) {
                Iterator $$12 = $$1.entrySet().iterator();
                while ($$12.hasNext()) {
                    Map.Entry $$2 = $$12.next();
                    if (!$$0.test((ResourceLocation)$$2.getKey())) continue;
                    ((SpriteSource.SpriteSupplier)$$2.getValue()).discard();
                    $$12.remove();
                }
            }
        };
        this.sources.forEach($$2 -> $$2.run($$02, $$22));
        ImmutableList.Builder $$3 = ImmutableList.builder();
        $$3.add($$0 -> MissingTextureAtlasSprite.create());
        $$3.addAll((Iterable)$$1.values());
        return $$3.build();
    }

    public static SpriteSourceList load(ResourceManager $$0, ResourceLocation $$1) {
        ResourceLocation $$2 = ATLAS_INFO_CONVERTER.idToFile($$1);
        ArrayList<SpriteSource> $$3 = new ArrayList<SpriteSource>();
        for (Resource $$4 : $$0.getResourceStack($$2)) {
            try {
                BufferedReader $$5 = $$4.openAsReader();
                try {
                    Dynamic $$6 = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)StrictJsonParser.parse($$5));
                    $$3.addAll((Collection)SpriteSources.FILE_CODEC.parse($$6).getOrThrow());
                } finally {
                    if ($$5 == null) continue;
                    $$5.close();
                }
            } catch (Exception $$7) {
                LOGGER.error("Failed to parse atlas definition {} in pack {}", $$2, $$4.sourcePackId(), $$7);
            }
        }
        return new SpriteSourceList($$3);
    }
}

