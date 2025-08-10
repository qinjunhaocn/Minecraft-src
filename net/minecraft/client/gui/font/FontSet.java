/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 */
package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.FontOption;
import net.minecraft.client.gui.font.FontTexture;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

public class FontSet
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RandomSource RANDOM = RandomSource.create();
    private static final float LARGE_FORWARD_ADVANCE = 32.0f;
    private final TextureManager textureManager;
    private final ResourceLocation name;
    private BakedGlyph missingGlyph;
    private BakedGlyph whiteGlyph;
    private List<GlyphProvider.Conditional> allProviders = List.of();
    private List<GlyphProvider> activeProviders = List.of();
    private final CodepointMap<BakedGlyph> glyphs = new CodepointMap(BakedGlyph[]::new, $$0 -> new BakedGlyph[$$0][]);
    private final CodepointMap<GlyphInfoFilter> glyphInfos = new CodepointMap(GlyphInfoFilter[]::new, $$0 -> new GlyphInfoFilter[$$0][]);
    private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap();
    private final List<FontTexture> textures = Lists.newArrayList();
    private final IntFunction<GlyphInfoFilter> glyphInfoGetter = this::computeGlyphInfo;
    private final IntFunction<BakedGlyph> glyphGetter = this::computeBakedGlyph;

    public FontSet(TextureManager $$02, ResourceLocation $$1) {
        this.textureManager = $$02;
        this.name = $$1;
    }

    public void reload(List<GlyphProvider.Conditional> $$0, Set<FontOption> $$1) {
        this.allProviders = $$0;
        this.reload($$1);
    }

    public void reload(Set<FontOption> $$0) {
        this.activeProviders = List.of();
        this.resetTextures();
        this.activeProviders = this.selectProviders(this.allProviders, $$0);
    }

    private void resetTextures() {
        this.textures.clear();
        this.glyphs.clear();
        this.glyphInfos.clear();
        this.glyphsByWidth.clear();
        this.missingGlyph = SpecialGlyphs.MISSING.bake(this::stitch);
        this.whiteGlyph = SpecialGlyphs.WHITE.bake(this::stitch);
    }

    private List<GlyphProvider> selectProviders(List<GlyphProvider.Conditional> $$0, Set<FontOption> $$1) {
        IntOpenHashSet $$22 = new IntOpenHashSet();
        ArrayList<GlyphProvider> $$3 = new ArrayList<GlyphProvider>();
        for (GlyphProvider.Conditional $$4 : $$0) {
            if (!$$4.filter().apply($$1)) continue;
            $$3.add($$4.provider());
            $$22.addAll((IntCollection)$$4.provider().getSupportedGlyphs());
        }
        HashSet $$5 = Sets.newHashSet();
        $$22.forEach($$2 -> {
            for (GlyphProvider $$3 : $$3) {
                GlyphInfo $$4 = $$3.getGlyph($$2);
                if ($$4 == null) continue;
                $$5.add($$3);
                if ($$4 == SpecialGlyphs.MISSING) break;
                ((IntList)this.glyphsByWidth.computeIfAbsent(Mth.ceil($$4.getAdvance(false)), $$0 -> new IntArrayList())).add($$2);
                break;
            }
        });
        return $$3.stream().filter($$5::contains).toList();
    }

    @Override
    public void close() {
        this.textures.clear();
    }

    private static boolean hasFishyAdvance(GlyphInfo $$0) {
        float $$1 = $$0.getAdvance(false);
        if ($$1 < 0.0f || $$1 > 32.0f) {
            return true;
        }
        float $$2 = $$0.getAdvance(true);
        return $$2 < 0.0f || $$2 > 32.0f;
    }

    private GlyphInfoFilter computeGlyphInfo(int $$0) {
        GlyphInfo $$1 = null;
        for (GlyphProvider $$2 : this.activeProviders) {
            GlyphInfo $$3 = $$2.getGlyph($$0);
            if ($$3 == null) continue;
            if ($$1 == null) {
                $$1 = $$3;
            }
            if (FontSet.hasFishyAdvance($$3)) continue;
            return new GlyphInfoFilter($$1, $$3);
        }
        if ($$1 != null) {
            return new GlyphInfoFilter($$1, SpecialGlyphs.MISSING);
        }
        return GlyphInfoFilter.MISSING;
    }

    public GlyphInfo getGlyphInfo(int $$0, boolean $$1) {
        return this.glyphInfos.computeIfAbsent($$0, this.glyphInfoGetter).select($$1);
    }

    private BakedGlyph computeBakedGlyph(int $$0) {
        for (GlyphProvider $$1 : this.activeProviders) {
            GlyphInfo $$2 = $$1.getGlyph($$0);
            if ($$2 == null) continue;
            return $$2.bake(this::stitch);
        }
        LOGGER.warn("Couldn't find glyph for character {} (\\u{})", (Object)Character.toString((int)$$0), (Object)String.format("%04x", $$0));
        return this.missingGlyph;
    }

    public BakedGlyph getGlyph(int $$0) {
        return this.glyphs.computeIfAbsent($$0, this.glyphGetter);
    }

    private BakedGlyph stitch(SheetGlyphInfo $$0) {
        for (FontTexture $$1 : this.textures) {
            BakedGlyph $$2 = $$1.add($$0);
            if ($$2 == null) continue;
            return $$2;
        }
        ResourceLocation $$3 = this.name.withSuffix("/" + this.textures.size());
        boolean $$4 = $$0.isColored();
        GlyphRenderTypes $$5 = $$4 ? GlyphRenderTypes.createForColorTexture($$3) : GlyphRenderTypes.createForIntensityTexture($$3);
        FontTexture $$6 = new FontTexture($$3::toString, $$5, $$4);
        this.textures.add($$6);
        this.textureManager.register($$3, $$6);
        BakedGlyph $$7 = $$6.add($$0);
        return $$7 == null ? this.missingGlyph : $$7;
    }

    public BakedGlyph getRandomGlyph(GlyphInfo $$0) {
        IntList $$1 = (IntList)this.glyphsByWidth.get(Mth.ceil($$0.getAdvance(false)));
        if ($$1 != null && !$$1.isEmpty()) {
            return this.getGlyph($$1.getInt(RANDOM.nextInt($$1.size())));
        }
        return this.missingGlyph;
    }

    public ResourceLocation name() {
        return this.name;
    }

    public BakedGlyph whiteGlyph() {
        return this.whiteGlyph;
    }

    record GlyphInfoFilter(GlyphInfo glyphInfo, GlyphInfo glyphInfoNotFishy) {
        static final GlyphInfoFilter MISSING = new GlyphInfoFilter(SpecialGlyphs.MISSING, SpecialGlyphs.MISSING);

        GlyphInfo select(boolean $$0) {
            return $$0 ? this.glyphInfoNotFishy : this.glyphInfo;
        }
    }
}

