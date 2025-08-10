/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

public class GameEventListenerRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int LISTENER_RENDER_DIST = 32;
    private static final float BOX_HEIGHT = 1.0f;
    private final List<TrackedGameEvent> trackedGameEvents = Lists.newArrayList();
    private final List<TrackedListener> trackedListeners = Lists.newArrayList();

    public GameEventListenerRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$22, double $$3, double $$4) {
        ClientLevel $$52 = this.minecraft.level;
        if ($$52 == null) {
            this.trackedGameEvents.clear();
            this.trackedListeners.clear();
            return;
        }
        Vec3 $$62 = new Vec3($$22, 0.0, $$4);
        this.trackedGameEvents.removeIf(TrackedGameEvent::isExpired);
        this.trackedListeners.removeIf($$2 -> $$2.isExpired($$52, $$62));
        VertexConsumer $$7 = $$1.getBuffer(RenderType.lines());
        for (TrackedListener $$8 : this.trackedListeners) {
            $$8.getPosition($$52).ifPresent($$6 -> {
                double $$7 = $$6.x() - (double)$$8.getListenerRadius();
                double $$8 = $$6.y() - (double)$$8.getListenerRadius();
                double $$9 = $$6.z() - (double)$$8.getListenerRadius();
                double $$10 = $$6.x() + (double)$$8.getListenerRadius();
                double $$11 = $$6.y() + (double)$$8.getListenerRadius();
                double $$12 = $$6.z() + (double)$$8.getListenerRadius();
                DebugRenderer.renderVoxelShape($$0, $$7, Shapes.create(new AABB($$7, $$8, $$9, $$10, $$11, $$12)), -$$22, -$$3, -$$4, 1.0f, 1.0f, 0.0f, 0.35f, true);
            });
        }
        VertexConsumer $$9 = $$1.getBuffer(RenderType.debugFilledBox());
        for (TrackedListener $$10 : this.trackedListeners) {
            $$10.getPosition($$52).ifPresent($$5 -> ShapeRenderer.addChainedFilledBoxVertices($$0, $$9, $$5.x() - 0.25 - $$22, $$5.y() - $$3, $$5.z() - 0.25 - $$4, $$5.x() + 0.25 - $$22, $$5.y() - $$3 + 1.0, $$5.z() + 0.25 - $$4, 1.0f, 1.0f, 0.0f, 0.35f));
        }
        for (TrackedListener $$11 : this.trackedListeners) {
            $$11.getPosition($$52).ifPresent($$2 -> {
                DebugRenderer.renderFloatingText($$0, $$1, "Listener Origin", $$2.x(), $$2.y() + (double)1.8f, $$2.z(), -1, 0.025f);
                DebugRenderer.renderFloatingText($$0, $$1, BlockPos.containing($$2).toString(), $$2.x(), $$2.y() + 1.5, $$2.z(), -6959665, 0.025f);
            });
        }
        for (TrackedGameEvent $$12 : this.trackedGameEvents) {
            Vec3 $$13 = $$12.position;
            double $$14 = 0.2f;
            double $$15 = $$13.x - (double)0.2f;
            double $$16 = $$13.y - (double)0.2f;
            double $$17 = $$13.z - (double)0.2f;
            double $$18 = $$13.x + (double)0.2f;
            double $$19 = $$13.y + (double)0.2f + 0.5;
            double $$20 = $$13.z + (double)0.2f;
            GameEventListenerRenderer.renderFilledBox($$0, $$1, new AABB($$15, $$16, $$17, $$18, $$19, $$20), 1.0f, 1.0f, 1.0f, 0.2f);
            DebugRenderer.renderFloatingText($$0, $$1, $$12.gameEvent.location().toString(), $$13.x, $$13.y + (double)0.85f, $$13.z, -7564911, 0.0075f);
        }
    }

    private static void renderFilledBox(PoseStack $$0, MultiBufferSource $$1, AABB $$2, float $$3, float $$4, float $$5, float $$6) {
        Camera $$7 = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!$$7.isInitialized()) {
            return;
        }
        Vec3 $$8 = $$7.getPosition().reverse();
        DebugRenderer.renderFilledBox($$0, $$1, $$2.move($$8), $$3, $$4, $$5, $$6);
    }

    public void trackGameEvent(ResourceKey<GameEvent> $$0, Vec3 $$1) {
        this.trackedGameEvents.add(new TrackedGameEvent(Util.getMillis(), $$0, $$1));
    }

    public void trackListener(PositionSource $$0, int $$1) {
        this.trackedListeners.add(new TrackedListener($$0, $$1));
    }

    static class TrackedListener
    implements GameEventListener {
        public final PositionSource listenerSource;
        public final int listenerRange;

        public TrackedListener(PositionSource $$0, int $$1) {
            this.listenerSource = $$0;
            this.listenerRange = $$1;
        }

        public boolean isExpired(Level $$0, Vec3 $$12) {
            return this.listenerSource.getPosition($$0).filter($$1 -> $$1.distanceToSqr($$12) <= 1024.0).isPresent();
        }

        public Optional<Vec3> getPosition(Level $$0) {
            return this.listenerSource.getPosition($$0);
        }

        @Override
        public PositionSource getListenerSource() {
            return this.listenerSource;
        }

        @Override
        public int getListenerRadius() {
            return this.listenerRange;
        }

        @Override
        public boolean handleGameEvent(ServerLevel $$0, Holder<GameEvent> $$1, GameEvent.Context $$2, Vec3 $$3) {
            return false;
        }
    }

    static final class TrackedGameEvent
    extends Record {
        private final long timeStamp;
        final ResourceKey<GameEvent> gameEvent;
        final Vec3 position;

        TrackedGameEvent(long $$0, ResourceKey<GameEvent> $$1, Vec3 $$2) {
            this.timeStamp = $$0;
            this.gameEvent = $$1;
            this.position = $$2;
        }

        public boolean isExpired() {
            return Util.getMillis() - this.timeStamp > 3000L;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TrackedGameEvent.class, "timeStamp;gameEvent;position", "timeStamp", "gameEvent", "position"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TrackedGameEvent.class, "timeStamp;gameEvent;position", "timeStamp", "gameEvent", "position"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TrackedGameEvent.class, "timeStamp;gameEvent;position", "timeStamp", "gameEvent", "position"}, this, $$0);
        }

        public long timeStamp() {
            return this.timeStamp;
        }

        public ResourceKey<GameEvent> gameEvent() {
            return this.gameEvent;
        }

        public Vec3 position() {
            return this.position;
        }
    }
}

