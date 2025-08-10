/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntIterator
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.opengl.DirectStateAccess;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import javax.annotation.Nullable;

public class GlTexture
extends GpuTexture {
    protected final int id;
    private final Int2IntMap fboCache = new Int2IntOpenHashMap();
    protected boolean closed;
    protected boolean modesDirty = true;
    private int views;

    protected GlTexture(int $$0, String $$1, TextureFormat $$2, int $$3, int $$4, int $$5, int $$6, int $$7) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        this.id = $$7;
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.views == 0) {
            this.destroyImmediately();
        }
    }

    private void destroyImmediately() {
        GlStateManager._deleteTexture(this.id);
        IntIterator intIterator = this.fboCache.values().iterator();
        while (intIterator.hasNext()) {
            int $$0 = (Integer)intIterator.next();
            GlStateManager._glDeleteFramebuffers($$0);
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    public int getFbo(DirectStateAccess $$0, @Nullable GpuTexture $$1) {
        int $$22 = $$1 == null ? 0 : ((GlTexture)$$1).id;
        return this.fboCache.computeIfAbsent($$22, $$2 -> {
            int $$3 = $$0.createFrameBufferObject();
            $$0.bindFrameBufferTextures($$3, this.id, $$22, 0, 0);
            return $$3;
        });
    }

    public void flushModeChanges(int $$0) {
        if (this.modesDirty) {
            GlStateManager._texParameter($$0, 10242, GlConst.toGl(this.addressModeU));
            GlStateManager._texParameter($$0, 10243, GlConst.toGl(this.addressModeV));
            switch (this.minFilter) {
                case NEAREST: {
                    GlStateManager._texParameter($$0, 10241, this.useMipmaps ? 9986 : 9728);
                    break;
                }
                case LINEAR: {
                    GlStateManager._texParameter($$0, 10241, this.useMipmaps ? 9987 : 9729);
                }
            }
            switch (this.magFilter) {
                case NEAREST: {
                    GlStateManager._texParameter($$0, 10240, 9728);
                    break;
                }
                case LINEAR: {
                    GlStateManager._texParameter($$0, 10240, 9729);
                }
            }
            this.modesDirty = false;
        }
    }

    public int glId() {
        return this.id;
    }

    @Override
    public void setAddressMode(AddressMode $$0, AddressMode $$1) {
        super.setAddressMode($$0, $$1);
        this.modesDirty = true;
    }

    @Override
    public void setTextureFilter(FilterMode $$0, FilterMode $$1, boolean $$2) {
        super.setTextureFilter($$0, $$1, $$2);
        this.modesDirty = true;
    }

    @Override
    public void setUseMipmaps(boolean $$0) {
        super.setUseMipmaps($$0);
        this.modesDirty = true;
    }

    public void addViews() {
        ++this.views;
    }

    public void removeViews() {
        --this.views;
        if (this.closed && this.views == 0) {
            this.destroyImmediately();
        }
    }
}

