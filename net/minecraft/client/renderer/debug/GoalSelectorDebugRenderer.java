/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.GoalDebugPayload;

public class GoalSelectorDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final int MAX_RENDER_DIST = 160;
    private final Minecraft minecraft;
    private final Int2ObjectMap<EntityGoalInfo> goalSelectors = new Int2ObjectOpenHashMap();

    @Override
    public void clear() {
        this.goalSelectors.clear();
    }

    public void addGoalSelector(int $$0, BlockPos $$1, List<GoalDebugPayload.DebugGoal> $$2) {
        this.goalSelectors.put($$0, (Object)new EntityGoalInfo($$1, $$2));
    }

    public void removeGoalSelector(int $$0) {
        this.goalSelectors.remove($$0);
    }

    public GoalSelectorDebugRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        Camera $$5 = this.minecraft.gameRenderer.getMainCamera();
        BlockPos $$6 = BlockPos.containing($$5.getPosition().x, 0.0, $$5.getPosition().z);
        for (EntityGoalInfo $$7 : this.goalSelectors.values()) {
            BlockPos $$8 = $$7.entityPos;
            if (!$$6.closerThan($$8, 160.0)) continue;
            for (int $$9 = 0; $$9 < $$7.goals.size(); ++$$9) {
                GoalDebugPayload.DebugGoal $$10 = $$7.goals.get($$9);
                double $$11 = (double)$$8.getX() + 0.5;
                double $$12 = (double)$$8.getY() + 2.0 + (double)$$9 * 0.25;
                double $$13 = (double)$$8.getZ() + 0.5;
                int $$14 = $$10.isRunning() ? -16711936 : -3355444;
                DebugRenderer.renderFloatingText($$0, $$1, $$10.name(), $$11, $$12, $$13, $$14);
            }
        }
    }

    static final class EntityGoalInfo
    extends Record {
        final BlockPos entityPos;
        final List<GoalDebugPayload.DebugGoal> goals;

        EntityGoalInfo(BlockPos $$0, List<GoalDebugPayload.DebugGoal> $$1) {
            this.entityPos = $$0;
            this.goals = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{EntityGoalInfo.class, "entityPos;goals", "entityPos", "goals"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EntityGoalInfo.class, "entityPos;goals", "entityPos", "goals"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EntityGoalInfo.class, "entityPos;goals", "entityPos", "goals"}, this, $$0);
        }

        public BlockPos entityPos() {
            return this.entityPos;
        }

        public List<GoalDebugPayload.DebugGoal> goals() {
            return this.goals;
        }
    }
}

