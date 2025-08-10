/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.VisibleForTesting
 */
package net.minecraft.network.protocol.game;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.VisibleForTesting;

public class VecDeltaCodec {
    private static final double TRUNCATION_STEPS = 4096.0;
    private Vec3 base = Vec3.ZERO;

    @VisibleForTesting
    static long encode(double $$0) {
        return Math.round($$0 * 4096.0);
    }

    @VisibleForTesting
    static double decode(long $$0) {
        return (double)$$0 / 4096.0;
    }

    public Vec3 decode(long $$0, long $$1, long $$2) {
        if ($$0 == 0L && $$1 == 0L && $$2 == 0L) {
            return this.base;
        }
        double $$3 = $$0 == 0L ? this.base.x : VecDeltaCodec.decode(VecDeltaCodec.encode(this.base.x) + $$0);
        double $$4 = $$1 == 0L ? this.base.y : VecDeltaCodec.decode(VecDeltaCodec.encode(this.base.y) + $$1);
        double $$5 = $$2 == 0L ? this.base.z : VecDeltaCodec.decode(VecDeltaCodec.encode(this.base.z) + $$2);
        return new Vec3($$3, $$4, $$5);
    }

    public long encodeX(Vec3 $$0) {
        return VecDeltaCodec.encode($$0.x) - VecDeltaCodec.encode(this.base.x);
    }

    public long encodeY(Vec3 $$0) {
        return VecDeltaCodec.encode($$0.y) - VecDeltaCodec.encode(this.base.y);
    }

    public long encodeZ(Vec3 $$0) {
        return VecDeltaCodec.encode($$0.z) - VecDeltaCodec.encode(this.base.z);
    }

    public Vec3 delta(Vec3 $$0) {
        return $$0.subtract(this.base);
    }

    public void setBase(Vec3 $$0) {
        this.base = $$0;
    }

    public Vec3 getBase() {
        return this.base;
    }
}

