/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.model.geom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class ModelPart {
    public static final float DEFAULT_SCALE = 1.0f;
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public float xScale = 1.0f;
    public float yScale = 1.0f;
    public float zScale = 1.0f;
    public boolean visible = true;
    public boolean skipDraw;
    private final List<Cube> cubes;
    private final Map<String, ModelPart> children;
    private PartPose initialPose = PartPose.ZERO;

    public ModelPart(List<Cube> $$0, Map<String, ModelPart> $$1) {
        this.cubes = $$0;
        this.children = $$1;
    }

    public PartPose storePose() {
        return PartPose.offsetAndRotation(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot);
    }

    public PartPose getInitialPose() {
        return this.initialPose;
    }

    public void setInitialPose(PartPose $$0) {
        this.initialPose = $$0;
    }

    public void resetPose() {
        this.loadPose(this.initialPose);
    }

    public void loadPose(PartPose $$0) {
        this.x = $$0.x();
        this.y = $$0.y();
        this.z = $$0.z();
        this.xRot = $$0.xRot();
        this.yRot = $$0.yRot();
        this.zRot = $$0.zRot();
        this.xScale = $$0.xScale();
        this.yScale = $$0.yScale();
        this.zScale = $$0.zScale();
    }

    public void copyFrom(ModelPart $$0) {
        this.xScale = $$0.xScale;
        this.yScale = $$0.yScale;
        this.zScale = $$0.zScale;
        this.xRot = $$0.xRot;
        this.yRot = $$0.yRot;
        this.zRot = $$0.zRot;
        this.x = $$0.x;
        this.y = $$0.y;
        this.z = $$0.z;
    }

    public boolean hasChild(String $$0) {
        return this.children.containsKey($$0);
    }

    public ModelPart getChild(String $$0) {
        ModelPart $$1 = this.children.get($$0);
        if ($$1 == null) {
            throw new NoSuchElementException("Can't find part " + $$0);
        }
        return $$1;
    }

    public void setPos(float $$0, float $$1, float $$2) {
        this.x = $$0;
        this.y = $$1;
        this.z = $$2;
    }

    public void setRotation(float $$0, float $$1, float $$2) {
        this.xRot = $$0;
        this.yRot = $$1;
        this.zRot = $$2;
    }

    public void render(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3) {
        this.render($$0, $$1, $$2, $$3, -1);
    }

    public void render(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, int $$4) {
        if (!this.visible) {
            return;
        }
        if (this.cubes.isEmpty() && this.children.isEmpty()) {
            return;
        }
        $$0.pushPose();
        this.translateAndRotate($$0);
        if (!this.skipDraw) {
            this.compile($$0.last(), $$1, $$2, $$3, $$4);
        }
        for (ModelPart $$5 : this.children.values()) {
            $$5.render($$0, $$1, $$2, $$3, $$4);
        }
        $$0.popPose();
    }

    public void rotateBy(Quaternionf $$0) {
        Matrix3f $$1 = new Matrix3f().rotationZYX(this.zRot, this.yRot, this.xRot);
        Matrix3f $$2 = $$1.rotate((Quaternionfc)$$0);
        Vector3f $$3 = $$2.getEulerAnglesZYX(new Vector3f());
        this.setRotation($$3.x, $$3.y, $$3.z);
    }

    public void getExtentsForGui(PoseStack $$0, Set<Vector3f> $$12) {
        this.visit($$0, ($$1, $$2, $$3, $$4) -> {
            for (Polygon $$5 : $$4.polygons) {
                for (Vertex $$6 : $$5.a()) {
                    float $$7 = $$6.pos().x() / 16.0f;
                    float $$8 = $$6.pos().y() / 16.0f;
                    float $$9 = $$6.pos().z() / 16.0f;
                    Vector3f $$10 = $$1.pose().transformPosition($$7, $$8, $$9, new Vector3f());
                    $$12.add($$10);
                }
            }
        });
    }

    public void visit(PoseStack $$0, Visitor $$1) {
        this.visit($$0, $$1, "");
    }

    private void visit(PoseStack $$0, Visitor $$1, String $$2) {
        if (this.cubes.isEmpty() && this.children.isEmpty()) {
            return;
        }
        $$0.pushPose();
        this.translateAndRotate($$0);
        PoseStack.Pose $$32 = $$0.last();
        for (int $$42 = 0; $$42 < this.cubes.size(); ++$$42) {
            $$1.visit($$32, $$2, $$42, this.cubes.get($$42));
        }
        String $$5 = $$2 + "/";
        this.children.forEach(($$3, $$4) -> $$4.visit($$0, $$1, $$5 + $$3));
        $$0.popPose();
    }

    public void translateAndRotate(PoseStack $$0) {
        $$0.translate(this.x / 16.0f, this.y / 16.0f, this.z / 16.0f);
        if (this.xRot != 0.0f || this.yRot != 0.0f || this.zRot != 0.0f) {
            $$0.mulPose((Quaternionfc)new Quaternionf().rotationZYX(this.zRot, this.yRot, this.xRot));
        }
        if (this.xScale != 1.0f || this.yScale != 1.0f || this.zScale != 1.0f) {
            $$0.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    private void compile(PoseStack.Pose $$0, VertexConsumer $$1, int $$2, int $$3, int $$4) {
        for (Cube $$5 : this.cubes) {
            $$5.compile($$0, $$1, $$2, $$3, $$4);
        }
    }

    public Cube getRandomCube(RandomSource $$0) {
        return this.cubes.get($$0.nextInt(this.cubes.size()));
    }

    public boolean isEmpty() {
        return this.cubes.isEmpty();
    }

    public void offsetPos(Vector3f $$0) {
        this.x += $$0.x();
        this.y += $$0.y();
        this.z += $$0.z();
    }

    public void offsetRotation(Vector3f $$0) {
        this.xRot += $$0.x();
        this.yRot += $$0.y();
        this.zRot += $$0.z();
    }

    public void offsetScale(Vector3f $$0) {
        this.xScale += $$0.x();
        this.yScale += $$0.y();
        this.zScale += $$0.z();
    }

    public List<ModelPart> getAllParts() {
        ArrayList<ModelPart> $$0 = new ArrayList<ModelPart>();
        $$0.add(this);
        this.addAllChildren(($$1, $$2) -> $$0.add((ModelPart)$$2));
        return List.copyOf($$0);
    }

    public Function<String, ModelPart> createPartLookup() {
        HashMap<String, ModelPart> $$0 = new HashMap<String, ModelPart>();
        $$0.put("root", this);
        this.addAllChildren($$0::putIfAbsent);
        return $$0::get;
    }

    private void addAllChildren(BiConsumer<String, ModelPart> $$0) {
        for (Map.Entry<String, ModelPart> $$1 : this.children.entrySet()) {
            $$0.accept($$1.getKey(), $$1.getValue());
        }
        for (ModelPart $$2 : this.children.values()) {
            $$2.addAllChildren($$0);
        }
    }

    @FunctionalInterface
    public static interface Visitor {
        public void visit(PoseStack.Pose var1, String var2, int var3, Cube var4);
    }

    public static class Cube {
        public final Polygon[] polygons;
        public final float minX;
        public final float minY;
        public final float minZ;
        public final float maxX;
        public final float maxY;
        public final float maxZ;

        public Cube(int $$0, int $$1, float $$2, float $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9, float $$10, boolean $$11, float $$12, float $$13, Set<Direction> $$14) {
            this.minX = $$2;
            this.minY = $$3;
            this.minZ = $$4;
            this.maxX = $$2 + $$5;
            this.maxY = $$3 + $$6;
            this.maxZ = $$4 + $$7;
            this.polygons = new Polygon[$$14.size()];
            float $$15 = $$2 + $$5;
            float $$16 = $$3 + $$6;
            float $$17 = $$4 + $$7;
            $$2 -= $$8;
            $$3 -= $$9;
            $$4 -= $$10;
            $$15 += $$8;
            $$16 += $$9;
            $$17 += $$10;
            if ($$11) {
                float $$18 = $$15;
                $$15 = $$2;
                $$2 = $$18;
            }
            Vertex $$19 = new Vertex($$2, $$3, $$4, 0.0f, 0.0f);
            Vertex $$20 = new Vertex($$15, $$3, $$4, 0.0f, 8.0f);
            Vertex $$21 = new Vertex($$15, $$16, $$4, 8.0f, 8.0f);
            Vertex $$22 = new Vertex($$2, $$16, $$4, 8.0f, 0.0f);
            Vertex $$23 = new Vertex($$2, $$3, $$17, 0.0f, 0.0f);
            Vertex $$24 = new Vertex($$15, $$3, $$17, 0.0f, 8.0f);
            Vertex $$25 = new Vertex($$15, $$16, $$17, 8.0f, 8.0f);
            Vertex $$26 = new Vertex($$2, $$16, $$17, 8.0f, 0.0f);
            float $$27 = $$0;
            float $$28 = (float)$$0 + $$7;
            float $$29 = (float)$$0 + $$7 + $$5;
            float $$30 = (float)$$0 + $$7 + $$5 + $$5;
            float $$31 = (float)$$0 + $$7 + $$5 + $$7;
            float $$32 = (float)$$0 + $$7 + $$5 + $$7 + $$5;
            float $$33 = $$1;
            float $$34 = (float)$$1 + $$7;
            float $$35 = (float)$$1 + $$7 + $$6;
            int $$36 = 0;
            if ($$14.contains(Direction.DOWN)) {
                this.polygons[$$36++] = new Polygon(new Vertex[]{$$24, $$23, $$19, $$20}, $$28, $$33, $$29, $$34, $$12, $$13, $$11, Direction.DOWN);
            }
            if ($$14.contains(Direction.UP)) {
                this.polygons[$$36++] = new Polygon(new Vertex[]{$$21, $$22, $$26, $$25}, $$29, $$34, $$30, $$33, $$12, $$13, $$11, Direction.UP);
            }
            if ($$14.contains(Direction.WEST)) {
                this.polygons[$$36++] = new Polygon(new Vertex[]{$$19, $$23, $$26, $$22}, $$27, $$34, $$28, $$35, $$12, $$13, $$11, Direction.WEST);
            }
            if ($$14.contains(Direction.NORTH)) {
                this.polygons[$$36++] = new Polygon(new Vertex[]{$$20, $$19, $$22, $$21}, $$28, $$34, $$29, $$35, $$12, $$13, $$11, Direction.NORTH);
            }
            if ($$14.contains(Direction.EAST)) {
                this.polygons[$$36++] = new Polygon(new Vertex[]{$$24, $$20, $$21, $$25}, $$29, $$34, $$31, $$35, $$12, $$13, $$11, Direction.EAST);
            }
            if ($$14.contains(Direction.SOUTH)) {
                this.polygons[$$36] = new Polygon(new Vertex[]{$$23, $$24, $$25, $$26}, $$31, $$34, $$32, $$35, $$12, $$13, $$11, Direction.SOUTH);
            }
        }

        public void compile(PoseStack.Pose $$0, VertexConsumer $$1, int $$2, int $$3, int $$4) {
            Matrix4f $$5 = $$0.pose();
            Vector3f $$6 = new Vector3f();
            for (Polygon $$7 : this.polygons) {
                Vector3f $$8 = $$0.transformNormal((Vector3fc)$$7.normal, $$6);
                float $$9 = $$8.x();
                float $$10 = $$8.y();
                float $$11 = $$8.z();
                for (Vertex $$12 : $$7.vertices) {
                    float $$13 = $$12.pos.x() / 16.0f;
                    float $$14 = $$12.pos.y() / 16.0f;
                    float $$15 = $$12.pos.z() / 16.0f;
                    Vector3f $$16 = $$5.transformPosition($$13, $$14, $$15, $$6);
                    $$1.addVertex($$16.x(), $$16.y(), $$16.z(), $$4, $$12.u, $$12.v, $$3, $$2, $$9, $$10, $$11);
                }
            }
        }
    }

    public static final class Polygon
    extends Record {
        final Vertex[] vertices;
        final Vector3f normal;

        public Polygon(Vertex[] $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6, boolean $$7, Direction $$8) {
            this($$0, $$8.step());
            float $$9 = 0.0f / $$5;
            float $$10 = 0.0f / $$6;
            $$0[0] = $$0[0].remap($$3 / $$5 - $$9, $$2 / $$6 + $$10);
            $$0[1] = $$0[1].remap($$1 / $$5 + $$9, $$2 / $$6 + $$10);
            $$0[2] = $$0[2].remap($$1 / $$5 + $$9, $$4 / $$6 - $$10);
            $$0[3] = $$0[3].remap($$3 / $$5 - $$9, $$4 / $$6 - $$10);
            if ($$7) {
                int $$11 = $$0.length;
                for (int $$12 = 0; $$12 < $$11 / 2; ++$$12) {
                    Vertex $$13 = $$0[$$12];
                    $$0[$$12] = $$0[$$11 - 1 - $$12];
                    $$0[$$11 - 1 - $$12] = $$13;
                }
            }
            if ($$7) {
                this.normal.mul(-1.0f, 1.0f, 1.0f);
            }
        }

        public Polygon(Vertex[] $$0, Vector3f $$1) {
            this.vertices = $$0;
            this.normal = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Polygon.class, "vertices;normal", "vertices", "normal"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Polygon.class, "vertices;normal", "vertices", "normal"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Polygon.class, "vertices;normal", "vertices", "normal"}, this, $$0);
        }

        public Vertex[] a() {
            return this.vertices;
        }

        public Vector3f normal() {
            return this.normal;
        }
    }

    public static final class Vertex
    extends Record {
        final Vector3f pos;
        final float u;
        final float v;

        public Vertex(float $$0, float $$1, float $$2, float $$3, float $$4) {
            this(new Vector3f($$0, $$1, $$2), $$3, $$4);
        }

        public Vertex(Vector3f $$0, float $$1, float $$2) {
            this.pos = $$0;
            this.u = $$1;
            this.v = $$2;
        }

        public Vertex remap(float $$0, float $$1) {
            return new Vertex(this.pos, $$0, $$1);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Vertex.class, "pos;u;v", "pos", "u", "v"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Vertex.class, "pos;u;v", "pos", "u", "v"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Vertex.class, "pos;u;v", "pos", "u", "v"}, this, $$0);
        }

        public Vector3f pos() {
            return this.pos;
        }

        public float u() {
            return this.u;
        }

        public float v() {
            return this.v;
        }
    }
}

