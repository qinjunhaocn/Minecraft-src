/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity;

public final class EntitySpawnReason
extends Enum<EntitySpawnReason> {
    public static final /* enum */ EntitySpawnReason NATURAL = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason CHUNK_GENERATION = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason SPAWNER = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason STRUCTURE = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason BREEDING = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason MOB_SUMMONED = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason JOCKEY = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason EVENT = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason CONVERSION = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason REINFORCEMENT = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason TRIGGERED = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason BUCKET = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason SPAWN_ITEM_USE = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason COMMAND = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason DISPENSER = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason PATROL = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason TRIAL_SPAWNER = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason LOAD = new EntitySpawnReason();
    public static final /* enum */ EntitySpawnReason DIMENSION_TRAVEL = new EntitySpawnReason();
    private static final /* synthetic */ EntitySpawnReason[] $VALUES;

    public static EntitySpawnReason[] values() {
        return (EntitySpawnReason[])$VALUES.clone();
    }

    public static EntitySpawnReason valueOf(String $$0) {
        return Enum.valueOf(EntitySpawnReason.class, $$0);
    }

    public static boolean isSpawner(EntitySpawnReason $$0) {
        return $$0 == SPAWNER || $$0 == TRIAL_SPAWNER;
    }

    public static boolean ignoresLightRequirements(EntitySpawnReason $$0) {
        return $$0 == TRIAL_SPAWNER;
    }

    private static /* synthetic */ EntitySpawnReason[] a() {
        return new EntitySpawnReason[]{NATURAL, CHUNK_GENERATION, SPAWNER, STRUCTURE, BREEDING, MOB_SUMMONED, JOCKEY, EVENT, CONVERSION, REINFORCEMENT, TRIGGERED, BUCKET, SPAWN_ITEM_USE, COMMAND, DISPENSER, PATROL, TRIAL_SPAWNER, LOAD, DIMENSION_TRAVEL};
    }

    static {
        $VALUES = EntitySpawnReason.a();
    }
}

