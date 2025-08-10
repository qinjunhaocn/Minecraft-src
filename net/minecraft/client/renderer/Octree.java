/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class Octree {
    private final Branch root;
    final BlockPos cameraSectionCenter;

    public Octree(SectionPos $$0, int $$1, int $$2, int $$3) {
        int $$4 = $$1 * 2 + 1;
        int $$5 = Mth.smallestEncompassingPowerOfTwo($$4);
        int $$6 = $$1 * 16;
        BlockPos $$7 = $$0.origin();
        this.cameraSectionCenter = $$0.center();
        int $$8 = $$7.getX() - $$6;
        int $$9 = $$8 + $$5 * 16 - 1;
        int $$10 = $$5 >= $$2 ? $$3 : $$7.getY() - $$6;
        int $$11 = $$10 + $$5 * 16 - 1;
        int $$12 = $$7.getZ() - $$6;
        int $$13 = $$12 + $$5 * 16 - 1;
        this.root = new Branch(new BoundingBox($$8, $$10, $$12, $$9, $$11, $$13));
    }

    public boolean add(SectionRenderDispatcher.RenderSection $$0) {
        return this.root.add($$0);
    }

    public void visitNodes(OctreeVisitor $$0, Frustum $$1, int $$2) {
        this.root.visitNodes($$0, false, $$1, 0, $$2, true);
    }

    boolean isClose(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5, int $$6) {
        int $$7 = this.cameraSectionCenter.getX();
        int $$8 = this.cameraSectionCenter.getY();
        int $$9 = this.cameraSectionCenter.getZ();
        return (double)$$7 > $$0 - (double)$$6 && (double)$$7 < $$3 + (double)$$6 && (double)$$8 > $$1 - (double)$$6 && (double)$$8 < $$4 + (double)$$6 && (double)$$9 > $$2 - (double)$$6 && (double)$$9 < $$5 + (double)$$6;
    }

    class Branch
    implements Node {
        private final Node[] nodes = new Node[8];
        private final BoundingBox boundingBox;
        private final int bbCenterX;
        private final int bbCenterY;
        private final int bbCenterZ;
        private final AxisSorting sorting;
        private final boolean cameraXDiffNegative;
        private final boolean cameraYDiffNegative;
        private final boolean cameraZDiffNegative;

        public Branch(BoundingBox $$0) {
            this.boundingBox = $$0;
            this.bbCenterX = this.boundingBox.minX() + this.boundingBox.getXSpan() / 2;
            this.bbCenterY = this.boundingBox.minY() + this.boundingBox.getYSpan() / 2;
            this.bbCenterZ = this.boundingBox.minZ() + this.boundingBox.getZSpan() / 2;
            int $$1 = Octree.this.cameraSectionCenter.getX() - this.bbCenterX;
            int $$2 = Octree.this.cameraSectionCenter.getY() - this.bbCenterY;
            int $$3 = Octree.this.cameraSectionCenter.getZ() - this.bbCenterZ;
            this.sorting = AxisSorting.getAxisSorting(Math.abs($$1), Math.abs($$2), Math.abs($$3));
            this.cameraXDiffNegative = $$1 < 0;
            this.cameraYDiffNegative = $$2 < 0;
            this.cameraZDiffNegative = $$3 < 0;
        }

        public boolean add(SectionRenderDispatcher.RenderSection $$0) {
            long $$1 = $$0.getSectionNode();
            boolean $$2 = SectionPos.sectionToBlockCoord(SectionPos.x($$1)) - this.bbCenterX < 0;
            boolean $$3 = SectionPos.sectionToBlockCoord(SectionPos.y($$1)) - this.bbCenterY < 0;
            boolean $$4 = SectionPos.sectionToBlockCoord(SectionPos.z($$1)) - this.bbCenterZ < 0;
            boolean $$5 = $$2 != this.cameraXDiffNegative;
            boolean $$6 = $$3 != this.cameraYDiffNegative;
            boolean $$7 = $$4 != this.cameraZDiffNegative;
            int $$8 = Branch.getNodeIndex(this.sorting, $$5, $$6, $$7);
            if (this.areChildrenLeaves()) {
                boolean $$9 = this.nodes[$$8] != null;
                this.nodes[$$8] = new Leaf($$0);
                return !$$9;
            }
            if (this.nodes[$$8] != null) {
                Branch $$10 = (Branch)this.nodes[$$8];
                return $$10.add($$0);
            }
            BoundingBox $$11 = this.createChildBoundingBox($$2, $$3, $$4);
            Branch $$12 = new Branch($$11);
            this.nodes[$$8] = $$12;
            return $$12.add($$0);
        }

        private static int getNodeIndex(AxisSorting $$0, boolean $$1, boolean $$2, boolean $$3) {
            int $$4 = 0;
            if ($$1) {
                $$4 += $$0.xShift;
            }
            if ($$2) {
                $$4 += $$0.yShift;
            }
            if ($$3) {
                $$4 += $$0.zShift;
            }
            return $$4;
        }

        private boolean areChildrenLeaves() {
            return this.boundingBox.getXSpan() == 32;
        }

        private BoundingBox createChildBoundingBox(boolean $$0, boolean $$1, boolean $$2) {
            int $$14;
            int $$13;
            int $$10;
            int $$9;
            int $$6;
            int $$5;
            if ($$0) {
                int $$3 = this.boundingBox.minX();
                int $$4 = this.bbCenterX - 1;
            } else {
                $$5 = this.bbCenterX;
                $$6 = this.boundingBox.maxX();
            }
            if ($$1) {
                int $$7 = this.boundingBox.minY();
                int $$8 = this.bbCenterY - 1;
            } else {
                $$9 = this.bbCenterY;
                $$10 = this.boundingBox.maxY();
            }
            if ($$2) {
                int $$11 = this.boundingBox.minZ();
                int $$12 = this.bbCenterZ - 1;
            } else {
                $$13 = this.bbCenterZ;
                $$14 = this.boundingBox.maxZ();
            }
            return new BoundingBox($$5, $$9, $$13, $$6, $$10, $$14);
        }

        @Override
        public void visitNodes(OctreeVisitor $$0, boolean $$1, Frustum $$2, int $$3, int $$4, boolean $$5) {
            boolean $$6 = $$1;
            if (!$$1) {
                int $$7 = $$2.cubeInFrustum(this.boundingBox);
                $$1 = $$7 == -2;
                boolean bl = $$6 = $$7 == -2 || $$7 == -1;
            }
            if ($$6) {
                $$5 = $$5 && Octree.this.isClose(this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ(), $$4);
                $$0.visit(this, $$1, $$3, $$5);
                for (Node $$8 : this.nodes) {
                    if ($$8 == null) continue;
                    $$8.visitNodes($$0, $$1, $$2, $$3 + 1, $$4, $$5);
                }
            }
        }

        @Override
        @Nullable
        public SectionRenderDispatcher.RenderSection getSection() {
            return null;
        }

        @Override
        public AABB getAABB() {
            return new AABB(this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX() + 1, this.boundingBox.maxY() + 1, this.boundingBox.maxZ() + 1);
        }
    }

    @FunctionalInterface
    public static interface OctreeVisitor {
        public void visit(Node var1, boolean var2, int var3, boolean var4);
    }

    static final class AxisSorting
    extends Enum<AxisSorting> {
        public static final /* enum */ AxisSorting XYZ = new AxisSorting(4, 2, 1);
        public static final /* enum */ AxisSorting XZY = new AxisSorting(4, 1, 2);
        public static final /* enum */ AxisSorting YXZ = new AxisSorting(2, 4, 1);
        public static final /* enum */ AxisSorting YZX = new AxisSorting(1, 4, 2);
        public static final /* enum */ AxisSorting ZXY = new AxisSorting(2, 1, 4);
        public static final /* enum */ AxisSorting ZYX = new AxisSorting(1, 2, 4);
        final int xShift;
        final int yShift;
        final int zShift;
        private static final /* synthetic */ AxisSorting[] $VALUES;

        public static AxisSorting[] values() {
            return (AxisSorting[])$VALUES.clone();
        }

        public static AxisSorting valueOf(String $$0) {
            return Enum.valueOf(AxisSorting.class, $$0);
        }

        private AxisSorting(int $$0, int $$1, int $$2) {
            this.xShift = $$0;
            this.yShift = $$1;
            this.zShift = $$2;
        }

        public static AxisSorting getAxisSorting(int $$0, int $$1, int $$2) {
            if ($$0 > $$1 && $$0 > $$2) {
                if ($$1 > $$2) {
                    return XYZ;
                }
                return XZY;
            }
            if ($$1 > $$0 && $$1 > $$2) {
                if ($$0 > $$2) {
                    return YXZ;
                }
                return YZX;
            }
            if ($$0 > $$1) {
                return ZXY;
            }
            return ZYX;
        }

        private static /* synthetic */ AxisSorting[] a() {
            return new AxisSorting[]{XYZ, XZY, YXZ, YZX, ZXY, ZYX};
        }

        static {
            $VALUES = AxisSorting.a();
        }
    }

    public static interface Node {
        public void visitNodes(OctreeVisitor var1, boolean var2, Frustum var3, int var4, int var5, boolean var6);

        @Nullable
        public SectionRenderDispatcher.RenderSection getSection();

        public AABB getAABB();
    }

    final class Leaf
    implements Node {
        private final SectionRenderDispatcher.RenderSection section;

        Leaf(SectionRenderDispatcher.RenderSection $$0) {
            this.section = $$0;
        }

        @Override
        public void visitNodes(OctreeVisitor $$0, boolean $$1, Frustum $$2, int $$3, int $$4, boolean $$5) {
            AABB $$6 = this.section.getBoundingBox();
            if ($$1 || $$2.isVisible(this.getSection().getBoundingBox())) {
                $$5 = $$5 && Octree.this.isClose($$6.minX, $$6.minY, $$6.minZ, $$6.maxX, $$6.maxY, $$6.maxZ, $$4);
                $$0.visit(this, $$1, $$3, $$5);
            }
        }

        @Override
        public SectionRenderDispatcher.RenderSection getSection() {
            return this.section;
        }

        @Override
        public AABB getAABB() {
            return this.section.getBoundingBox();
        }
    }
}

