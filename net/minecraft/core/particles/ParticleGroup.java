/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.core.particles;

public class ParticleGroup {
    private final int limit;
    public static final ParticleGroup SPORE_BLOSSOM = new ParticleGroup(1000);

    public ParticleGroup(int $$0) {
        this.limit = $$0;
    }

    public int getLimit() {
        return this.limit;
    }
}

