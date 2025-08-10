/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.file.Path;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.resources.ResourceLocation;

public class FontTexture
extends AbstractTexture
implements Dumpable {
    private static final int SIZE = 256;
    private final GlyphRenderTypes renderTypes;
    private final boolean colored;
    private final Node root;

    public FontTexture(Supplier<String> $$0, GlyphRenderTypes $$1, boolean $$2) {
        this.colored = $$2;
        this.root = new Node(0, 0, 256, 256);
        GpuDevice $$3 = RenderSystem.getDevice();
        this.texture = $$3.createTexture($$0, 7, $$2 ? TextureFormat.RGBA8 : TextureFormat.RED8, 256, 256, 1, 1);
        this.texture.setTextureFilter(FilterMode.NEAREST, false);
        this.textureView = $$3.createTextureView(this.texture);
        this.renderTypes = $$1;
    }

    @Nullable
    public BakedGlyph add(SheetGlyphInfo $$0) {
        if ($$0.isColored() != this.colored) {
            return null;
        }
        Node $$1 = this.root.insert($$0);
        if ($$1 != null && this.texture != null && this.textureView != null) {
            $$0.upload($$1.x, $$1.y, this.texture);
            float $$2 = 256.0f;
            float $$3 = 256.0f;
            float $$4 = 0.01f;
            return new BakedGlyph(this.renderTypes, this.textureView, ((float)$$1.x + 0.01f) / 256.0f, ((float)$$1.x - 0.01f + (float)$$0.getPixelWidth()) / 256.0f, ((float)$$1.y + 0.01f) / 256.0f, ((float)$$1.y - 0.01f + (float)$$0.getPixelHeight()) / 256.0f, $$0.getLeft(), $$0.getRight(), $$0.getTop(), $$0.getBottom());
        }
        return null;
    }

    @Override
    public void dumpContents(ResourceLocation $$02, Path $$1) {
        if (this.texture == null) {
            return;
        }
        String $$2 = $$02.toDebugFileName();
        TextureUtil.writeAsPNG($$1, $$2, this.texture, 0, $$0 -> ($$0 & 0xFF000000) == 0 ? -16777216 : $$0);
    }

    static class Node {
        final int x;
        final int y;
        private final int width;
        private final int height;
        @Nullable
        private Node left;
        @Nullable
        private Node right;
        private boolean occupied;

        Node(int $$0, int $$1, int $$2, int $$3) {
            this.x = $$0;
            this.y = $$1;
            this.width = $$2;
            this.height = $$3;
        }

        @Nullable
        Node insert(SheetGlyphInfo $$0) {
            if (this.left != null && this.right != null) {
                Node $$1 = this.left.insert($$0);
                if ($$1 == null) {
                    $$1 = this.right.insert($$0);
                }
                return $$1;
            }
            if (this.occupied) {
                return null;
            }
            int $$2 = $$0.getPixelWidth();
            int $$3 = $$0.getPixelHeight();
            if ($$2 > this.width || $$3 > this.height) {
                return null;
            }
            if ($$2 == this.width && $$3 == this.height) {
                this.occupied = true;
                return this;
            }
            int $$4 = this.width - $$2;
            int $$5 = this.height - $$3;
            if ($$4 > $$5) {
                this.left = new Node(this.x, this.y, $$2, this.height);
                this.right = new Node(this.x + $$2 + 1, this.y, this.width - $$2 - 1, this.height);
            } else {
                this.left = new Node(this.x, this.y, this.width, $$3);
                this.right = new Node(this.x, this.y + $$3 + 1, this.width, this.height - $$3 - 1);
            }
            return this.left.insert($$0);
        }
    }
}

