/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.GraphicsWorkarounds;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;

@DontObfuscate
public class VertexFormat {
    public static final int UNKNOWN_ELEMENT = -1;
    private static final boolean USE_STAGING_BUFFER_WORKAROUND = Util.getPlatform() == Util.OS.WINDOWS && Util.isAarch64();
    @Nullable
    private static GpuBuffer UPLOAD_STAGING_BUFFER;
    private final List<VertexFormatElement> elements;
    private final List<String> names;
    private final int vertexSize;
    private final int elementsMask;
    private final int[] offsetsByElement = new int[32];
    @Nullable
    private GpuBuffer immediateDrawVertexBuffer;
    @Nullable
    private GpuBuffer immediateDrawIndexBuffer;

    VertexFormat(List<VertexFormatElement> $$02, List<String> $$12, IntList $$2, int $$3) {
        this.elements = $$02;
        this.names = $$12;
        this.vertexSize = $$3;
        this.elementsMask = $$02.stream().mapToInt(VertexFormatElement::mask).reduce(0, ($$0, $$1) -> $$0 | $$1);
        for (int $$4 = 0; $$4 < this.offsetsByElement.length; ++$$4) {
            VertexFormatElement $$5 = VertexFormatElement.byId($$4);
            int $$6 = $$5 != null ? $$02.indexOf((Object)$$5) : -1;
            this.offsetsByElement[$$4] = $$6 != -1 ? $$2.getInt($$6) : -1;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String toString() {
        return "VertexFormat" + String.valueOf(this.names);
    }

    public int getVertexSize() {
        return this.vertexSize;
    }

    public List<VertexFormatElement> getElements() {
        return this.elements;
    }

    public List<String> getElementAttributeNames() {
        return this.names;
    }

    public int[] getOffsetsByElement() {
        return this.offsetsByElement;
    }

    public int getOffset(VertexFormatElement $$0) {
        return this.offsetsByElement[$$0.id()];
    }

    public boolean contains(VertexFormatElement $$0) {
        return (this.elementsMask & $$0.mask()) != 0;
    }

    public int getElementsMask() {
        return this.elementsMask;
    }

    public String getElementName(VertexFormatElement $$0) {
        int $$1 = this.elements.indexOf((Object)$$0);
        if ($$1 == -1) {
            throw new IllegalArgumentException(String.valueOf((Object)$$0) + " is not contained in format");
        }
        return this.names.get($$1);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof VertexFormat)) return false;
        VertexFormat $$1 = (VertexFormat)$$0;
        if (this.elementsMask != $$1.elementsMask) return false;
        if (this.vertexSize != $$1.vertexSize) return false;
        if (!this.names.equals($$1.names)) return false;
        if (!Arrays.equals(this.offsetsByElement, $$1.offsetsByElement)) return false;
        return true;
    }

    public int hashCode() {
        return this.elementsMask * 31 + Arrays.hashCode(this.offsetsByElement);
    }

    private static GpuBuffer uploadToBuffer(@Nullable GpuBuffer $$0, ByteBuffer $$1, int $$2, Supplier<String> $$3) {
        GpuDevice $$4 = RenderSystem.getDevice();
        if ($$0 == null) {
            $$0 = $$4.createBuffer($$3, $$2, $$1);
        } else {
            CommandEncoder $$5 = $$4.createCommandEncoder();
            if ($$0.size() < $$1.remaining()) {
                $$0.close();
                $$0 = $$4.createBuffer($$3, $$2, $$1);
            } else {
                $$5.writeToBuffer($$0.slice(), $$1);
            }
        }
        return $$0;
    }

    private GpuBuffer uploadToBufferWithWorkaround(@Nullable GpuBuffer $$0, ByteBuffer $$1, int $$2, Supplier<String> $$3) {
        GpuDevice $$4 = RenderSystem.getDevice();
        if (USE_STAGING_BUFFER_WORKAROUND) {
            if ($$0 == null) {
                $$0 = $$4.createBuffer($$3, $$2, $$1);
            } else {
                CommandEncoder $$5 = $$4.createCommandEncoder();
                if ($$0.size() < $$1.remaining()) {
                    $$0.close();
                    $$0 = $$4.createBuffer($$3, $$2, $$1);
                } else {
                    UPLOAD_STAGING_BUFFER = VertexFormat.uploadToBuffer(UPLOAD_STAGING_BUFFER, $$1, $$2, $$3);
                    $$5.copyToBuffer(UPLOAD_STAGING_BUFFER.slice(0, $$1.remaining()), $$0.slice(0, $$1.remaining()));
                }
            }
            return $$0;
        }
        if (GraphicsWorkarounds.get($$4).alwaysCreateFreshImmediateBuffer()) {
            if ($$0 != null) {
                $$0.close();
            }
            return $$4.createBuffer($$3, $$2, $$1);
        }
        return VertexFormat.uploadToBuffer($$0, $$1, $$2, $$3);
    }

    public GpuBuffer uploadImmediateVertexBuffer(ByteBuffer $$0) {
        this.immediateDrawVertexBuffer = this.uploadToBufferWithWorkaround(this.immediateDrawVertexBuffer, $$0, 40, () -> "Immediate vertex buffer for " + String.valueOf(this));
        return this.immediateDrawVertexBuffer;
    }

    public GpuBuffer uploadImmediateIndexBuffer(ByteBuffer $$0) {
        this.immediateDrawIndexBuffer = this.uploadToBufferWithWorkaround(this.immediateDrawIndexBuffer, $$0, 72, () -> "Immediate index buffer for " + String.valueOf(this));
        return this.immediateDrawIndexBuffer;
    }

    @DontObfuscate
    public static class Builder {
        private final ImmutableMap.Builder<String, VertexFormatElement> elements = ImmutableMap.builder();
        private final IntList offsets = new IntArrayList();
        private int offset;

        Builder() {
        }

        public Builder add(String $$0, VertexFormatElement $$1) {
            this.elements.put($$0, $$1);
            this.offsets.add(this.offset);
            this.offset += $$1.byteSize();
            return this;
        }

        public Builder padding(int $$0) {
            this.offset += $$0;
            return this;
        }

        public VertexFormat build() {
            ImmutableMap $$0 = this.elements.buildOrThrow();
            ImmutableList<VertexFormatElement> $$1 = ((ImmutableCollection)$$0.values()).asList();
            ImmutableList<String> $$2 = ((ImmutableSet)$$0.keySet()).asList();
            return new VertexFormat($$1, $$2, this.offsets, this.offset);
        }
    }

    public static final class Mode
    extends Enum<Mode> {
        public static final /* enum */ Mode LINES = new Mode(2, 2, false);
        public static final /* enum */ Mode LINE_STRIP = new Mode(2, 1, true);
        public static final /* enum */ Mode DEBUG_LINES = new Mode(2, 2, false);
        public static final /* enum */ Mode DEBUG_LINE_STRIP = new Mode(2, 1, true);
        public static final /* enum */ Mode TRIANGLES = new Mode(3, 3, false);
        public static final /* enum */ Mode TRIANGLE_STRIP = new Mode(3, 1, true);
        public static final /* enum */ Mode TRIANGLE_FAN = new Mode(3, 1, true);
        public static final /* enum */ Mode QUADS = new Mode(4, 4, false);
        public final int primitiveLength;
        public final int primitiveStride;
        public final boolean connectedPrimitives;
        private static final /* synthetic */ Mode[] $VALUES;

        public static Mode[] values() {
            return (Mode[])$VALUES.clone();
        }

        public static Mode valueOf(String $$0) {
            return Enum.valueOf(Mode.class, $$0);
        }

        private Mode(int $$0, int $$1, boolean $$2) {
            this.primitiveLength = $$0;
            this.primitiveStride = $$1;
            this.connectedPrimitives = $$2;
        }

        public int indexCount(int $$0) {
            int $$3;
            switch (this.ordinal()) {
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: {
                    int $$1 = $$0;
                    break;
                }
                case 0: 
                case 7: {
                    int $$2 = $$0 / 4 * 6;
                    break;
                }
                default: {
                    $$3 = 0;
                }
            }
            return $$3;
        }

        private static /* synthetic */ Mode[] a() {
            return new Mode[]{LINES, LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, QUADS};
        }

        static {
            $VALUES = Mode.a();
        }
    }

    public static final class IndexType
    extends Enum<IndexType> {
        public static final /* enum */ IndexType SHORT = new IndexType(2);
        public static final /* enum */ IndexType INT = new IndexType(4);
        public final int bytes;
        private static final /* synthetic */ IndexType[] $VALUES;

        public static IndexType[] values() {
            return (IndexType[])$VALUES.clone();
        }

        public static IndexType valueOf(String $$0) {
            return Enum.valueOf(IndexType.class, $$0);
        }

        private IndexType(int $$0) {
            this.bytes = $$0;
        }

        public static IndexType least(int $$0) {
            if (($$0 & 0xFFFF0000) != 0) {
                return INT;
            }
            return SHORT;
        }

        private static /* synthetic */ IndexType[] a() {
            return new IndexType[]{SHORT, INT};
        }

        static {
            $VALUES = IndexType.a();
        }
    }
}

