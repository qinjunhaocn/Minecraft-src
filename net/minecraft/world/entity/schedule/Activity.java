/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.schedule;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class Activity {
    public static final Activity CORE = Activity.register("core");
    public static final Activity IDLE = Activity.register("idle");
    public static final Activity WORK = Activity.register("work");
    public static final Activity PLAY = Activity.register("play");
    public static final Activity REST = Activity.register("rest");
    public static final Activity MEET = Activity.register("meet");
    public static final Activity PANIC = Activity.register("panic");
    public static final Activity RAID = Activity.register("raid");
    public static final Activity PRE_RAID = Activity.register("pre_raid");
    public static final Activity HIDE = Activity.register("hide");
    public static final Activity FIGHT = Activity.register("fight");
    public static final Activity CELEBRATE = Activity.register("celebrate");
    public static final Activity ADMIRE_ITEM = Activity.register("admire_item");
    public static final Activity AVOID = Activity.register("avoid");
    public static final Activity RIDE = Activity.register("ride");
    public static final Activity PLAY_DEAD = Activity.register("play_dead");
    public static final Activity LONG_JUMP = Activity.register("long_jump");
    public static final Activity RAM = Activity.register("ram");
    public static final Activity TONGUE = Activity.register("tongue");
    public static final Activity SWIM = Activity.register("swim");
    public static final Activity LAY_SPAWN = Activity.register("lay_spawn");
    public static final Activity SNIFF = Activity.register("sniff");
    public static final Activity INVESTIGATE = Activity.register("investigate");
    public static final Activity ROAR = Activity.register("roar");
    public static final Activity EMERGE = Activity.register("emerge");
    public static final Activity DIG = Activity.register("dig");
    private final String name;
    private final int hashCode;

    private Activity(String $$0) {
        this.name = $$0;
        this.hashCode = $$0.hashCode();
    }

    public String getName() {
        return this.name;
    }

    private static Activity register(String $$0) {
        return Registry.register(BuiltInRegistries.ACTIVITY, $$0, new Activity($$0));
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        Activity $$1 = (Activity)$$0;
        return this.name.equals($$1.name);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return this.getName();
    }
}

