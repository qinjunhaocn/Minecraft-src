/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class TextureAtlas
extends AbstractTexture
implements Dumpable,
Tickable {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Deprecated
    public static final ResourceLocation LOCATION_BLOCKS = ResourceLocation.withDefaultNamespace("textures/atlas/blocks.png");
    @Deprecated
    public static final ResourceLocation LOCATION_PARTICLES = ResourceLocation.withDefaultNamespace("textures/atlas/particles.png");
    private List<SpriteContents> sprites = List.of();
    private List<TextureAtlasSprite.Ticker> animatedTextures = List.of();
    private Map<ResourceLocation, TextureAtlasSprite> texturesByName = Map.of();
    @Nullable
    private TextureAtlasSprite missingSprite;
    private final ResourceLocation location;
    private final int maxSupportedTextureSize;
    private int width;
    private int height;
    private int mipLevel;

    public TextureAtlas(ResourceLocation $$0) {
        this.location = $$0;
        this.maxSupportedTextureSize = RenderSystem.getDevice().getMaxTextureSize();
    }

    private void createTexture(int $$0, int $$1, int $$2) {
        LOGGER.info("Created: {}x{}x{} {}-atlas", $$0, $$1, $$2, this.location);
        GpuDevice $$3 = RenderSystem.getDevice();
        this.close();
        this.texture = $$3.createTexture(this.location::toString, 7, TextureFormat.RGBA8, $$0, $$1, 1, $$2 + 1);
        this.textureView = $$3.createTextureView(this.texture);
        this.width = $$0;
        this.height = $$1;
        this.mipLevel = $$2;
    }

    public void upload(SpriteLoader.Preparations $$0) {
        this.createTexture($$0.width(), $$0.height(), $$0.mipLevel());
        this.clearTextureData();
        this.setFilter(false, this.mipLevel > 1);
        this.texturesByName = Map.copyOf($$0.regions());
        this.missingSprite = this.texturesByName.get(MissingTextureAtlasSprite.getLocation());
        if (this.missingSprite == null) {
            throw new IllegalStateException("Atlas '" + String.valueOf(this.location) + "' (" + this.texturesByName.size() + " sprites) has no missing texture sprite");
        }
        ArrayList<SpriteContents> $$1 = new ArrayList<SpriteContents>();
        ArrayList<TextureAtlasSprite.Ticker> $$2 = new ArrayList<TextureAtlasSprite.Ticker>();
        for (TextureAtlasSprite $$3 : $$0.regions().values()) {
            $$1.add($$3.contents());
            try {
                $$3.uploadFirstFrame(this.texture);
            } catch (Throwable $$4) {
                CrashReport $$5 = CrashReport.forThrowable($$4, "Stitching texture atlas");
                CrashReportCategory $$6 = $$5.addCategory("Texture being stitched together");
                $$6.setDetail("Atlas path", this.location);
                $$6.setDetail("Sprite", $$3);
                throw new ReportedException($$5);
            }
            TextureAtlasSprite.Ticker $$7 = $$3.createTicker();
            if ($$7 == null) continue;
            $$2.add($$7);
        }
        this.sprites = List.copyOf($$1);
        this.animatedTextures = List.copyOf($$2);
    }

    @Override
    public void dumpContents(ResourceLocation $$02, Path $$1) throws IOException {
        String $$2 = $$02.toDebugFileName();
        TextureUtil.writeAsPNG($$1, $$2, this.getTexture(), this.mipLevel, $$0 -> $$0);
        TextureAtlas.dumpSpriteNames($$1, $$2, this.texturesByName);
    }

    private static void dumpSpriteNames(Path $$0, String $$1, Map<ResourceLocation, TextureAtlasSprite> $$2) {
        Path $$3 = $$0.resolve($$1 + ".txt");
        try (BufferedWriter $$4 = Files.newBufferedWriter($$3, new OpenOption[0]);){
            for (Map.Entry $$5 : $$2.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
                TextureAtlasSprite $$6 = (TextureAtlasSprite)$$5.getValue();
                $$4.write(String.format(Locale.ROOT, "%s\tx=%d\ty=%d\tw=%d\th=%d%n", $$5.getKey(), $$6.getX(), $$6.getY(), $$6.contents().width(), $$6.contents().height()));
            }
        } catch (IOException $$7) {
            LOGGER.warn("Failed to write file {}", (Object)$$3, (Object)$$7);
        }
    }

    public void cycleAnimationFrames() {
        if (this.texture == null) {
            return;
        }
        for (TextureAtlasSprite.Ticker $$0 : this.animatedTextures) {
            $$0.tickAndUpload(this.texture);
        }
    }

    @Override
    public void tick() {
        this.cycleAnimationFrames();
    }

    public TextureAtlasSprite getSprite(ResourceLocation $$0) {
        TextureAtlasSprite $$1 = this.texturesByName.getOrDefault($$0, this.missingSprite);
        if ($$1 == null) {
            throw new IllegalStateException("Tried to lookup sprite, but atlas is not initialized");
        }
        return $$1;
    }

    public void clearTextureData() {
        this.sprites.forEach(SpriteContents::close);
        this.animatedTextures.forEach(TextureAtlasSprite.Ticker::close);
        this.sprites = List.of();
        this.animatedTextures = List.of();
        this.texturesByName = Map.of();
        this.missingSprite = null;
    }

    public ResourceLocation location() {
        return this.location;
    }

    public int maxSupportedTextureSize() {
        return this.maxSupportedTextureSize;
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }
}

