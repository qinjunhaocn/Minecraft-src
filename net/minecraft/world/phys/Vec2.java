/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.phys;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.util.Mth;

public class Vec2 {
    public static final Vec2 ZERO = new Vec2(0.0f, 0.0f);
    public static final Vec2 ONE = new Vec2(1.0f, 1.0f);
    public static final Vec2 UNIT_X = new Vec2(1.0f, 0.0f);
    public static final Vec2 NEG_UNIT_X = new Vec2(-1.0f, 0.0f);
    public static final Vec2 UNIT_Y = new Vec2(0.0f, 1.0f);
    public static final Vec2 NEG_UNIT_Y = new Vec2(0.0f, -1.0f);
    public static final Vec2 MAX = new Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
    public static final Vec2 MIN = new Vec2(Float.MIN_VALUE, Float.MIN_VALUE);
    public static final Codec<Vec2> CODEC = Codec.FLOAT.listOf().comapFlatMap($$02 -> Util.fixedSize($$02, 2).map($$0 -> new Vec2(((Float)$$0.get(0)).floatValue(), ((Float)$$0.get(1)).floatValue())), $$0 -> List.of((Object)Float.valueOf($$0.x), (Object)Float.valueOf($$0.y)));
    public final float x;
    public final float y;

    public Vec2(float $$0, float $$1) {
        this.x = $$0;
        this.y = $$1;
    }

    public Vec2 scale(float $$0) {
        return new Vec2(this.x * $$0, this.y * $$0);
    }

    public float dot(Vec2 $$0) {
        return this.x * $$0.x + this.y * $$0.y;
    }

    public Vec2 add(Vec2 $$0) {
        return new Vec2(this.x + $$0.x, this.y + $$0.y);
    }

    public Vec2 add(float $$0) {
        return new Vec2(this.x + $$0, this.y + $$0);
    }

    public boolean equals(Vec2 $$0) {
        return this.x == $$0.x && this.y == $$0.y;
    }

    public Vec2 normalized() {
        float $$0 = Mth.sqrt(this.x * this.x + this.y * this.y);
        return $$0 < 1.0E-4f ? ZERO : new Vec2(this.x / $$0, this.y / $$0);
    }

    public float length() {
        return Mth.sqrt(this.x * this.x + this.y * this.y);
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public float distanceToSqr(Vec2 $$0) {
        float $$1 = $$0.x - this.x;
        float $$2 = $$0.y - this.y;
        return $$1 * $$1 + $$2 * $$2;
    }

    public Vec2 negated() {
        return new Vec2(-this.x, -this.y);
    }
}

