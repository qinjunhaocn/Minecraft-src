/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  java.util.SequencedMap
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SequencedMap;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.ModelBakery;

public class RenderBuffers {
    private final SectionBufferBuilderPack fixedBufferPack = new SectionBufferBuilderPack();
    private final SectionBufferBuilderPool sectionBufferPool;
    private final MultiBufferSource.BufferSource bufferSource;
    private final MultiBufferSource.BufferSource crumblingBufferSource;
    private final OutlineBufferSource outlineBufferSource;

    public RenderBuffers(int $$02) {
        this.sectionBufferPool = SectionBufferBuilderPool.allocate($$02);
        SequencedMap $$1 = (SequencedMap)Util.make(new Object2ObjectLinkedOpenHashMap(), $$0 -> {
            $$0.put((Object)Sheets.solidBlockSheet(), (Object)this.fixedBufferPack.buffer(ChunkSectionLayer.SOLID));
            $$0.put((Object)Sheets.cutoutBlockSheet(), (Object)this.fixedBufferPack.buffer(ChunkSectionLayer.CUTOUT));
            $$0.put((Object)Sheets.bannerSheet(), (Object)this.fixedBufferPack.buffer(ChunkSectionLayer.CUTOUT_MIPPED));
            $$0.put((Object)Sheets.translucentItemSheet(), (Object)this.fixedBufferPack.buffer(ChunkSectionLayer.TRANSLUCENT));
            RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>)$$0, Sheets.shieldSheet());
            RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>)$$0, Sheets.bedSheet());
            RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>)$$0, Sheets.shulkerBoxSheet());
            RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>)$$0, Sheets.signSheet());
            RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>)$$0, Sheets.hangingSignSheet());
            $$0.put((Object)Sheets.chestSheet(), (Object)new ByteBufferBuilder(786432));
            RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>)$$0, RenderType.armorEntityGlint());
            RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>)$$0, RenderType.glint());
            RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>)$$0, RenderType.glintTranslucent());
            RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>)$$0, RenderType.entityGlint());
            RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>)$$0, RenderType.waterMask());
        });
        this.bufferSource = MultiBufferSource.immediateWithBuffers((SequencedMap<RenderType, ByteBufferBuilder>)$$1, new ByteBufferBuilder(786432));
        this.outlineBufferSource = new OutlineBufferSource(this.bufferSource);
        SequencedMap $$2 = (SequencedMap)Util.make(new Object2ObjectLinkedOpenHashMap(), $$0 -> ModelBakery.DESTROY_TYPES.forEach($$1 -> RenderBuffers.put((Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder>)$$0, $$1)));
        this.crumblingBufferSource = MultiBufferSource.immediateWithBuffers((SequencedMap<RenderType, ByteBufferBuilder>)$$2, new ByteBufferBuilder(0));
    }

    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> $$0, RenderType $$1) {
        $$0.put((Object)$$1, (Object)new ByteBufferBuilder($$1.bufferSize()));
    }

    public SectionBufferBuilderPack fixedBufferPack() {
        return this.fixedBufferPack;
    }

    public SectionBufferBuilderPool sectionBufferPool() {
        return this.sectionBufferPool;
    }

    public MultiBufferSource.BufferSource bufferSource() {
        return this.bufferSource;
    }

    public MultiBufferSource.BufferSource crumblingBufferSource() {
        return this.crumblingBufferSource;
    }

    public OutlineBufferSource outlineBufferSource() {
        return this.outlineBufferSource;
    }
}

