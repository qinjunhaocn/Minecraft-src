/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.pathfinder;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.phys.Vec3;

public class Path {
    private final List<Node> nodes;
    @Nullable
    private DebugData debugData;
    private int nextNodeIndex;
    private final BlockPos target;
    private final float distToTarget;
    private final boolean reached;

    public Path(List<Node> $$0, BlockPos $$1, boolean $$2) {
        this.nodes = $$0;
        this.target = $$1;
        this.distToTarget = $$0.isEmpty() ? Float.MAX_VALUE : this.nodes.get(this.nodes.size() - 1).distanceManhattan(this.target);
        this.reached = $$2;
    }

    public void advance() {
        ++this.nextNodeIndex;
    }

    public boolean notStarted() {
        return this.nextNodeIndex <= 0;
    }

    public boolean isDone() {
        return this.nextNodeIndex >= this.nodes.size();
    }

    @Nullable
    public Node getEndNode() {
        if (!this.nodes.isEmpty()) {
            return this.nodes.get(this.nodes.size() - 1);
        }
        return null;
    }

    public Node getNode(int $$0) {
        return this.nodes.get($$0);
    }

    public void truncateNodes(int $$0) {
        if (this.nodes.size() > $$0) {
            this.nodes.subList($$0, this.nodes.size()).clear();
        }
    }

    public void replaceNode(int $$0, Node $$1) {
        this.nodes.set($$0, $$1);
    }

    public int getNodeCount() {
        return this.nodes.size();
    }

    public int getNextNodeIndex() {
        return this.nextNodeIndex;
    }

    public void setNextNodeIndex(int $$0) {
        this.nextNodeIndex = $$0;
    }

    public Vec3 getEntityPosAtNode(Entity $$0, int $$1) {
        Node $$2 = this.nodes.get($$1);
        double $$3 = (double)$$2.x + (double)((int)($$0.getBbWidth() + 1.0f)) * 0.5;
        double $$4 = $$2.y;
        double $$5 = (double)$$2.z + (double)((int)($$0.getBbWidth() + 1.0f)) * 0.5;
        return new Vec3($$3, $$4, $$5);
    }

    public BlockPos getNodePos(int $$0) {
        return this.nodes.get($$0).asBlockPos();
    }

    public Vec3 getNextEntityPos(Entity $$0) {
        return this.getEntityPosAtNode($$0, this.nextNodeIndex);
    }

    public BlockPos getNextNodePos() {
        return this.nodes.get(this.nextNodeIndex).asBlockPos();
    }

    public Node getNextNode() {
        return this.nodes.get(this.nextNodeIndex);
    }

    @Nullable
    public Node getPreviousNode() {
        return this.nextNodeIndex > 0 ? this.nodes.get(this.nextNodeIndex - 1) : null;
    }

    public boolean sameAs(@Nullable Path $$0) {
        if ($$0 == null) {
            return false;
        }
        if ($$0.nodes.size() != this.nodes.size()) {
            return false;
        }
        for (int $$1 = 0; $$1 < this.nodes.size(); ++$$1) {
            Node $$2 = this.nodes.get($$1);
            Node $$3 = $$0.nodes.get($$1);
            if ($$2.x == $$3.x && $$2.y == $$3.y && $$2.z == $$3.z) continue;
            return false;
        }
        return true;
    }

    public boolean canReach() {
        return this.reached;
    }

    @VisibleForDebug
    void a(Node[] $$0, Node[] $$1, Set<Target> $$2) {
        this.debugData = new DebugData($$0, $$1, $$2);
    }

    @Nullable
    public DebugData debugData() {
        return this.debugData;
    }

    public void writeToStream(FriendlyByteBuf $$02) {
        if (this.debugData == null || this.debugData.targetNodes.isEmpty()) {
            return;
        }
        $$02.writeBoolean(this.reached);
        $$02.writeInt(this.nextNodeIndex);
        $$02.writeBlockPos(this.target);
        $$02.writeCollection(this.nodes, ($$0, $$1) -> $$1.writeToStream((FriendlyByteBuf)((Object)$$0)));
        this.debugData.write($$02);
    }

    public static Path createFromStream(FriendlyByteBuf $$0) {
        boolean $$1 = $$0.readBoolean();
        int $$2 = $$0.readInt();
        BlockPos $$3 = $$0.readBlockPos();
        List<Node> $$4 = $$0.readList(Node::createFromStream);
        DebugData $$5 = DebugData.read($$0);
        Path $$6 = new Path($$4, $$3, $$1);
        $$6.debugData = $$5;
        $$6.nextNodeIndex = $$2;
        return $$6;
    }

    public String toString() {
        return "Path(length=" + this.nodes.size() + ")";
    }

    public BlockPos getTarget() {
        return this.target;
    }

    public float getDistToTarget() {
        return this.distToTarget;
    }

    static Node[] c(FriendlyByteBuf $$0) {
        Node[] $$1 = new Node[$$0.readVarInt()];
        for (int $$2 = 0; $$2 < $$1.length; ++$$2) {
            $$1[$$2] = Node.createFromStream($$0);
        }
        return $$1;
    }

    static void a(FriendlyByteBuf $$0, Node[] $$1) {
        $$0.writeVarInt($$1.length);
        for (Node $$2 : $$1) {
            $$2.writeToStream($$0);
        }
    }

    public Path copy() {
        Path $$0 = new Path(this.nodes, this.target, this.reached);
        $$0.debugData = this.debugData;
        $$0.nextNodeIndex = this.nextNodeIndex;
        return $$0;
    }

    public static final class DebugData
    extends Record {
        private final Node[] openSet;
        private final Node[] closedSet;
        final Set<Target> targetNodes;

        public DebugData(Node[] $$0, Node[] $$1, Set<Target> $$2) {
            this.openSet = $$0;
            this.closedSet = $$1;
            this.targetNodes = $$2;
        }

        public void write(FriendlyByteBuf $$02) {
            $$02.writeCollection(this.targetNodes, ($$0, $$1) -> $$1.writeToStream((FriendlyByteBuf)((Object)$$0)));
            Path.a($$02, this.openSet);
            Path.a($$02, this.closedSet);
        }

        public static DebugData read(FriendlyByteBuf $$0) {
            HashSet $$1 = $$0.readCollection(HashSet::new, Target::createFromStream);
            Node[] $$2 = Path.c($$0);
            Node[] $$3 = Path.c($$0);
            return new DebugData($$2, $$3, $$1);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{DebugData.class, "openSet;closedSet;targetNodes", "openSet", "closedSet", "targetNodes"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DebugData.class, "openSet;closedSet;targetNodes", "openSet", "closedSet", "targetNodes"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DebugData.class, "openSet;closedSet;targetNodes", "openSet", "closedSet", "targetNodes"}, this, $$0);
        }

        public Node[] a() {
            return this.openSet;
        }

        public Node[] b() {
            return this.closedSet;
        }

        public Set<Target> targetNodes() {
            return this.targetNodes;
        }
    }
}

