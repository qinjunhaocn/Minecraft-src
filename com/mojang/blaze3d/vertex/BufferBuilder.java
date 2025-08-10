/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteOrder;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.lwjgl.system.MemoryUtil;

public class BufferBuilder
implements VertexConsumer {
    private static final int MAX_VERTEX_COUNT = 0xFFFFFF;
    private static final long NOT_BUILDING = -1L;
    private static final long UNKNOWN_ELEMENT = -1L;
    private static final boolean IS_LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    private final ByteBufferBuilder buffer;
    private long vertexPointer = -1L;
    private int vertices;
    private final VertexFormat format;
    private final VertexFormat.Mode mode;
    private final boolean fastFormat;
    private final boolean fullFormat;
    private final int vertexSize;
    private final int initialElementsToFill;
    private final int[] offsetsByElement;
    private int elementsToFill;
    private boolean building = true;

    public BufferBuilder(ByteBufferBuilder $$0, VertexFormat.Mode $$1, VertexFormat $$2) {
        if (!$$2.contains(VertexFormatElement.POSITION)) {
            throw new IllegalArgumentException("Cannot build mesh with no position element");
        }
        this.buffer = $$0;
        this.mode = $$1;
        this.format = $$2;
        this.vertexSize = $$2.getVertexSize();
        this.initialElementsToFill = $$2.getElementsMask() & ~VertexFormatElement.POSITION.mask();
        this.offsetsByElement = $$2.getOffsetsByElement();
        boolean $$3 = $$2 == DefaultVertexFormat.NEW_ENTITY;
        boolean $$4 = $$2 == DefaultVertexFormat.BLOCK;
        this.fastFormat = $$3 || $$4;
        this.fullFormat = $$3;
    }

    @Nullable
    public MeshData build() {
        this.ensureBuilding();
        this.endLastVertex();
        MeshData $$0 = this.storeMesh();
        this.building = false;
        this.vertexPointer = -1L;
        return $$0;
    }

    public MeshData buildOrThrow() {
        MeshData $$0 = this.build();
        if ($$0 == null) {
            throw new IllegalStateException("BufferBuilder was empty");
        }
        return $$0;
    }

    private void ensureBuilding() {
        if (!this.building) {
            throw new IllegalStateException("Not building!");
        }
    }

    @Nullable
    private MeshData storeMesh() {
        if (this.vertices == 0) {
            return null;
        }
        ByteBufferBuilder.Result $$0 = this.buffer.build();
        if ($$0 == null) {
            return null;
        }
        int $$1 = this.mode.indexCount(this.vertices);
        VertexFormat.IndexType $$2 = VertexFormat.IndexType.least(this.vertices);
        return new MeshData($$0, new MeshData.DrawState(this.format, this.vertices, $$1, this.mode, $$2));
    }

    private long beginVertex() {
        long $$0;
        this.ensureBuilding();
        this.endLastVertex();
        if (this.vertices >= 0xFFFFFF) {
            throw new IllegalStateException("Trying to write too many vertices (>16777215) into BufferBuilder");
        }
        ++this.vertices;
        this.vertexPointer = $$0 = this.buffer.reserve(this.vertexSize);
        return $$0;
    }

    private long beginElement(VertexFormatElement $$0) {
        int $$1 = this.elementsToFill;
        int $$2 = $$1 & ~$$0.mask();
        if ($$2 == $$1) {
            return -1L;
        }
        this.elementsToFill = $$2;
        long $$3 = this.vertexPointer;
        if ($$3 == -1L) {
            throw new IllegalArgumentException("Not currently building vertex");
        }
        return $$3 + (long)this.offsetsByElement[$$0.id()];
    }

    private void endLastVertex() {
        if (this.vertices == 0) {
            return;
        }
        if (this.elementsToFill != 0) {
            String $$0 = VertexFormatElement.elementsFromMask(this.elementsToFill).map(this.format::getElementName).collect(Collectors.joining(", "));
            throw new IllegalStateException("Missing elements in vertex: " + $$0);
        }
        if (this.mode == VertexFormat.Mode.LINES || this.mode == VertexFormat.Mode.LINE_STRIP) {
            long $$1 = this.buffer.reserve(this.vertexSize);
            MemoryUtil.memCopy((long)($$1 - (long)this.vertexSize), (long)$$1, (long)this.vertexSize);
            ++this.vertices;
        }
    }

    private static void putRgba(long $$0, int $$1) {
        int $$2 = ARGB.toABGR($$1);
        MemoryUtil.memPutInt((long)$$0, (int)(IS_LITTLE_ENDIAN ? $$2 : Integer.reverseBytes($$2)));
    }

    private static void putPackedUv(long $$0, int $$1) {
        if (IS_LITTLE_ENDIAN) {
            MemoryUtil.memPutInt((long)$$0, (int)$$1);
        } else {
            MemoryUtil.memPutShort((long)$$0, (short)((short)($$1 & 0xFFFF)));
            MemoryUtil.memPutShort((long)($$0 + 2L), (short)((short)($$1 >> 16 & 0xFFFF)));
        }
    }

    @Override
    public VertexConsumer addVertex(float $$0, float $$1, float $$2) {
        long $$3 = this.beginVertex() + (long)this.offsetsByElement[VertexFormatElement.POSITION.id()];
        this.elementsToFill = this.initialElementsToFill;
        MemoryUtil.memPutFloat((long)$$3, (float)$$0);
        MemoryUtil.memPutFloat((long)($$3 + 4L), (float)$$1);
        MemoryUtil.memPutFloat((long)($$3 + 8L), (float)$$2);
        return this;
    }

    @Override
    public VertexConsumer setColor(int $$0, int $$1, int $$2, int $$3) {
        long $$4 = this.beginElement(VertexFormatElement.COLOR);
        if ($$4 != -1L) {
            MemoryUtil.memPutByte((long)$$4, (byte)((byte)$$0));
            MemoryUtil.memPutByte((long)($$4 + 1L), (byte)((byte)$$1));
            MemoryUtil.memPutByte((long)($$4 + 2L), (byte)((byte)$$2));
            MemoryUtil.memPutByte((long)($$4 + 3L), (byte)((byte)$$3));
        }
        return this;
    }

    @Override
    public VertexConsumer setColor(int $$0) {
        long $$1 = this.beginElement(VertexFormatElement.COLOR);
        if ($$1 != -1L) {
            BufferBuilder.putRgba($$1, $$0);
        }
        return this;
    }

    @Override
    public VertexConsumer setUv(float $$0, float $$1) {
        long $$2 = this.beginElement(VertexFormatElement.UV0);
        if ($$2 != -1L) {
            MemoryUtil.memPutFloat((long)$$2, (float)$$0);
            MemoryUtil.memPutFloat((long)($$2 + 4L), (float)$$1);
        }
        return this;
    }

    @Override
    public VertexConsumer setUv1(int $$0, int $$1) {
        return this.uvShort((short)$$0, (short)$$1, VertexFormatElement.UV1);
    }

    @Override
    public VertexConsumer setOverlay(int $$0) {
        long $$1 = this.beginElement(VertexFormatElement.UV1);
        if ($$1 != -1L) {
            BufferBuilder.putPackedUv($$1, $$0);
        }
        return this;
    }

    @Override
    public VertexConsumer setUv2(int $$0, int $$1) {
        return this.uvShort((short)$$0, (short)$$1, VertexFormatElement.UV2);
    }

    @Override
    public VertexConsumer setLight(int $$0) {
        long $$1 = this.beginElement(VertexFormatElement.UV2);
        if ($$1 != -1L) {
            BufferBuilder.putPackedUv($$1, $$0);
        }
        return this;
    }

    private VertexConsumer uvShort(short $$0, short $$1, VertexFormatElement $$2) {
        long $$3 = this.beginElement($$2);
        if ($$3 != -1L) {
            MemoryUtil.memPutShort((long)$$3, (short)$$0);
            MemoryUtil.memPutShort((long)($$3 + 2L), (short)$$1);
        }
        return this;
    }

    @Override
    public VertexConsumer setNormal(float $$0, float $$1, float $$2) {
        long $$3 = this.beginElement(VertexFormatElement.NORMAL);
        if ($$3 != -1L) {
            MemoryUtil.memPutByte((long)$$3, (byte)BufferBuilder.normalIntValue($$0));
            MemoryUtil.memPutByte((long)($$3 + 1L), (byte)BufferBuilder.normalIntValue($$1));
            MemoryUtil.memPutByte((long)($$3 + 2L), (byte)BufferBuilder.normalIntValue($$2));
        }
        return this;
    }

    private static byte normalIntValue(float $$0) {
        return (byte)((int)(Mth.clamp($$0, -1.0f, 1.0f) * 127.0f) & 0xFF);
    }

    @Override
    public void addVertex(float $$0, float $$1, float $$2, int $$3, float $$4, float $$5, int $$6, int $$7, float $$8, float $$9, float $$10) {
        if (this.fastFormat) {
            long $$13;
            long $$11 = this.beginVertex();
            MemoryUtil.memPutFloat((long)($$11 + 0L), (float)$$0);
            MemoryUtil.memPutFloat((long)($$11 + 4L), (float)$$1);
            MemoryUtil.memPutFloat((long)($$11 + 8L), (float)$$2);
            BufferBuilder.putRgba($$11 + 12L, $$3);
            MemoryUtil.memPutFloat((long)($$11 + 16L), (float)$$4);
            MemoryUtil.memPutFloat((long)($$11 + 20L), (float)$$5);
            if (this.fullFormat) {
                BufferBuilder.putPackedUv($$11 + 24L, $$6);
                long $$12 = $$11 + 28L;
            } else {
                $$13 = $$11 + 24L;
            }
            BufferBuilder.putPackedUv($$13 + 0L, $$7);
            MemoryUtil.memPutByte((long)($$13 + 4L), (byte)BufferBuilder.normalIntValue($$8));
            MemoryUtil.memPutByte((long)($$13 + 5L), (byte)BufferBuilder.normalIntValue($$9));
            MemoryUtil.memPutByte((long)($$13 + 6L), (byte)BufferBuilder.normalIntValue($$10));
            return;
        }
        VertexConsumer.super.addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10);
    }
}

