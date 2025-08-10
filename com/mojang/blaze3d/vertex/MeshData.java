/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntConsumer
 *  java.lang.MatchException
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexSorting;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;
import org.apache.commons.lang3.mutable.MutableLong;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class MeshData
implements AutoCloseable {
    private final ByteBufferBuilder.Result vertexBuffer;
    @Nullable
    private ByteBufferBuilder.Result indexBuffer;
    private final DrawState drawState;

    public MeshData(ByteBufferBuilder.Result $$0, DrawState $$1) {
        this.vertexBuffer = $$0;
        this.drawState = $$1;
    }

    private static Vector3f[] a(ByteBuffer $$0, int $$1, VertexFormat $$2) {
        int $$3 = $$2.getOffset(VertexFormatElement.POSITION);
        if ($$3 == -1) {
            throw new IllegalArgumentException("Cannot identify quad centers with no position element");
        }
        FloatBuffer $$4 = $$0.asFloatBuffer();
        int $$5 = $$2.getVertexSize() / 4;
        int $$6 = $$5 * 4;
        int $$7 = $$1 / 4;
        Vector3f[] $$8 = new Vector3f[$$7];
        for (int $$9 = 0; $$9 < $$7; ++$$9) {
            int $$10 = $$9 * $$6 + $$3;
            int $$11 = $$10 + $$5 * 2;
            float $$12 = $$4.get($$10 + 0);
            float $$13 = $$4.get($$10 + 1);
            float $$14 = $$4.get($$10 + 2);
            float $$15 = $$4.get($$11 + 0);
            float $$16 = $$4.get($$11 + 1);
            float $$17 = $$4.get($$11 + 2);
            $$8[$$9] = new Vector3f(($$12 + $$15) / 2.0f, ($$13 + $$16) / 2.0f, ($$14 + $$17) / 2.0f);
        }
        return $$8;
    }

    public ByteBuffer vertexBuffer() {
        return this.vertexBuffer.byteBuffer();
    }

    @Nullable
    public ByteBuffer indexBuffer() {
        return this.indexBuffer != null ? this.indexBuffer.byteBuffer() : null;
    }

    public DrawState drawState() {
        return this.drawState;
    }

    @Nullable
    public SortState sortQuads(ByteBufferBuilder $$0, VertexSorting $$1) {
        if (this.drawState.mode() != VertexFormat.Mode.QUADS) {
            return null;
        }
        Vector3f[] $$2 = MeshData.a(this.vertexBuffer.byteBuffer(), this.drawState.vertexCount(), this.drawState.format());
        SortState $$3 = new SortState($$2, this.drawState.indexType());
        this.indexBuffer = $$3.buildSortedIndexBuffer($$0, $$1);
        return $$3;
    }

    @Override
    public void close() {
        this.vertexBuffer.close();
        if (this.indexBuffer != null) {
            this.indexBuffer.close();
        }
    }

    public record DrawState(VertexFormat format, int vertexCount, int indexCount, VertexFormat.Mode mode, VertexFormat.IndexType indexType) {
    }

    public static final class SortState
    extends Record {
        private final Vector3f[] centroids;
        private final VertexFormat.IndexType indexType;

        public SortState(Vector3f[] $$0, VertexFormat.IndexType $$1) {
            this.centroids = $$0;
            this.indexType = $$1;
        }

        @Nullable
        public ByteBufferBuilder.Result buildSortedIndexBuffer(ByteBufferBuilder $$0, VertexSorting $$1) {
            int[] $$2 = $$1.sort(this.centroids);
            long $$3 = $$0.reserve($$2.length * 6 * this.indexType.bytes);
            IntConsumer $$4 = this.indexWriter($$3, this.indexType);
            for (int $$5 : $$2) {
                $$4.accept($$5 * 4 + 0);
                $$4.accept($$5 * 4 + 1);
                $$4.accept($$5 * 4 + 2);
                $$4.accept($$5 * 4 + 2);
                $$4.accept($$5 * 4 + 3);
                $$4.accept($$5 * 4 + 0);
            }
            return $$0.build();
        }

        private IntConsumer indexWriter(long $$0, VertexFormat.IndexType $$12) {
            MutableLong $$2 = new MutableLong($$0);
            return switch ($$12) {
                default -> throw new MatchException(null, null);
                case VertexFormat.IndexType.SHORT -> $$1 -> MemoryUtil.memPutShort((long)$$2.getAndAdd(2L), (short)((short)$$1));
                case VertexFormat.IndexType.INT -> $$1 -> MemoryUtil.memPutInt((long)$$2.getAndAdd(4L), (int)$$1);
            };
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{SortState.class, "centroids;indexType", "centroids", "indexType"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SortState.class, "centroids;indexType", "centroids", "indexType"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SortState.class, "centroids;indexType", "centroids", "indexType"}, this, $$0);
        }

        public Vector3f[] a() {
            return this.centroids;
        }

        public VertexFormat.IndexType indexType() {
            return this.indexType;
        }
    }
}

