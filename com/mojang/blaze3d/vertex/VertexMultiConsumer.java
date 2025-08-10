/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Consumer;

public class VertexMultiConsumer {
    public static VertexConsumer create() {
        throw new IllegalArgumentException();
    }

    public static VertexConsumer create(VertexConsumer $$0) {
        return $$0;
    }

    public static VertexConsumer create(VertexConsumer $$0, VertexConsumer $$1) {
        return new Double($$0, $$1);
    }

    public static VertexConsumer a(VertexConsumer ... $$0) {
        return new Multiple($$0);
    }

    static class Double
    implements VertexConsumer {
        private final VertexConsumer first;
        private final VertexConsumer second;

        public Double(VertexConsumer $$0, VertexConsumer $$1) {
            if ($$0 == $$1) {
                throw new IllegalArgumentException("Duplicate delegates");
            }
            this.first = $$0;
            this.second = $$1;
        }

        @Override
        public VertexConsumer addVertex(float $$0, float $$1, float $$2) {
            this.first.addVertex($$0, $$1, $$2);
            this.second.addVertex($$0, $$1, $$2);
            return this;
        }

        @Override
        public VertexConsumer setColor(int $$0, int $$1, int $$2, int $$3) {
            this.first.setColor($$0, $$1, $$2, $$3);
            this.second.setColor($$0, $$1, $$2, $$3);
            return this;
        }

        @Override
        public VertexConsumer setUv(float $$0, float $$1) {
            this.first.setUv($$0, $$1);
            this.second.setUv($$0, $$1);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int $$0, int $$1) {
            this.first.setUv1($$0, $$1);
            this.second.setUv1($$0, $$1);
            return this;
        }

        @Override
        public VertexConsumer setUv2(int $$0, int $$1) {
            this.first.setUv2($$0, $$1);
            this.second.setUv2($$0, $$1);
            return this;
        }

        @Override
        public VertexConsumer setNormal(float $$0, float $$1, float $$2) {
            this.first.setNormal($$0, $$1, $$2);
            this.second.setNormal($$0, $$1, $$2);
            return this;
        }

        @Override
        public void addVertex(float $$0, float $$1, float $$2, int $$3, float $$4, float $$5, int $$6, int $$7, float $$8, float $$9, float $$10) {
            this.first.addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10);
            this.second.addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10);
        }
    }

    static final class Multiple
    extends Record
    implements VertexConsumer {
        private final VertexConsumer[] delegates;

        Multiple(VertexConsumer[] $$0) {
            for (int $$1 = 0; $$1 < $$0.length; ++$$1) {
                for (int $$2 = $$1 + 1; $$2 < $$0.length; ++$$2) {
                    if ($$0[$$1] != $$0[$$2]) continue;
                    throw new IllegalArgumentException("Duplicate delegates");
                }
            }
            this.delegates = $$0;
        }

        private void forEach(Consumer<VertexConsumer> $$0) {
            for (VertexConsumer $$1 : this.delegates) {
                $$0.accept($$1);
            }
        }

        @Override
        public VertexConsumer addVertex(float $$0, float $$1, float $$2) {
            this.forEach($$3 -> $$3.addVertex($$0, $$1, $$2));
            return this;
        }

        @Override
        public VertexConsumer setColor(int $$0, int $$1, int $$2, int $$3) {
            this.forEach($$4 -> $$4.setColor($$0, $$1, $$2, $$3));
            return this;
        }

        @Override
        public VertexConsumer setUv(float $$0, float $$1) {
            this.forEach($$2 -> $$2.setUv($$0, $$1));
            return this;
        }

        @Override
        public VertexConsumer setUv1(int $$0, int $$1) {
            this.forEach($$2 -> $$2.setUv1($$0, $$1));
            return this;
        }

        @Override
        public VertexConsumer setUv2(int $$0, int $$1) {
            this.forEach($$2 -> $$2.setUv2($$0, $$1));
            return this;
        }

        @Override
        public VertexConsumer setNormal(float $$0, float $$1, float $$2) {
            this.forEach($$3 -> $$3.setNormal($$0, $$1, $$2));
            return this;
        }

        @Override
        public void addVertex(float $$0, float $$1, float $$2, int $$3, float $$4, float $$5, int $$6, int $$7, float $$8, float $$9, float $$10) {
            this.forEach($$11 -> $$11.addVertex($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10));
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Multiple.class, "delegates", "delegates"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Multiple.class, "delegates", "delegates"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Multiple.class, "delegates", "delegates"}, this, $$0);
        }

        public VertexConsumer[] a() {
            return this.delegates;
        }
    }
}

