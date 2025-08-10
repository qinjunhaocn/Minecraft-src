/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.profiling.jfr;

import net.minecraft.server.MinecraftServer;

public final class Environment
extends Enum<Environment> {
    public static final /* enum */ Environment CLIENT = new Environment("client");
    public static final /* enum */ Environment SERVER = new Environment("server");
    private final String description;
    private static final /* synthetic */ Environment[] $VALUES;

    public static Environment[] values() {
        return (Environment[])$VALUES.clone();
    }

    public static Environment valueOf(String $$0) {
        return Enum.valueOf(Environment.class, $$0);
    }

    private Environment(String $$0) {
        this.description = $$0;
    }

    public static Environment from(MinecraftServer $$0) {
        return $$0.isDedicatedServer() ? SERVER : CLIENT;
    }

    public String getDescription() {
        return this.description;
    }

    private static /* synthetic */ Environment[] b() {
        return new Environment[]{CLIENT, SERVER};
    }

    static {
        $VALUES = Environment.b();
    }
}

