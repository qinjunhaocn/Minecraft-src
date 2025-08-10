/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.client.waypoints;

import com.mojang.datafixers.util.Either;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.TrackedWaypointManager;
import net.minecraft.world.waypoints.Waypoint;

public class ClientWaypointManager
implements TrackedWaypointManager {
    private final Map<Either<UUID, String>, TrackedWaypoint> waypoints = new ConcurrentHashMap<Either<UUID, String>, TrackedWaypoint>();

    @Override
    public void trackWaypoint(TrackedWaypoint $$0) {
        this.waypoints.put($$0.id(), $$0);
    }

    @Override
    public void updateWaypoint(TrackedWaypoint $$0) {
        this.waypoints.get($$0.id()).update($$0);
    }

    @Override
    public void untrackWaypoint(TrackedWaypoint $$0) {
        this.waypoints.remove($$0.id());
    }

    public boolean hasWaypoints() {
        return !this.waypoints.isEmpty();
    }

    public void forEachWaypoint(Entity $$0, Consumer<TrackedWaypoint> $$12) {
        this.waypoints.values().stream().sorted(Comparator.comparingDouble($$1 -> $$1.distanceSquared($$0)).reversed()).forEachOrdered($$12);
    }

    @Override
    public /* synthetic */ void untrackWaypoint(Waypoint waypoint) {
        this.untrackWaypoint((TrackedWaypoint)waypoint);
    }

    @Override
    public /* synthetic */ void trackWaypoint(Waypoint waypoint) {
        this.trackWaypoint((TrackedWaypoint)waypoint);
    }
}

