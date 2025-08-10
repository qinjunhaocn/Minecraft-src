/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.animation;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class AnimationChannel
extends Record {
    private final Target target;
    private final Keyframe[] keyframes;

    public AnimationChannel(Target $$0, Keyframe ... $$1) {
        this.target = $$0;
        this.keyframes = $$1;
    }

    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{AnimationChannel.class, "target;keyframes", "target", "keyframes"}, this);
    }

    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AnimationChannel.class, "target;keyframes", "target", "keyframes"}, this);
    }

    public final boolean equals(Object $$0) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AnimationChannel.class, "target;keyframes", "target", "keyframes"}, this, $$0);
    }

    public Target target() {
        return this.target;
    }

    public Keyframe[] b() {
        return this.keyframes;
    }

    public static interface Target {
        public void apply(ModelPart var1, Vector3f var2);
    }

    public static class Interpolations {
        public static final Interpolation LINEAR = ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            Vector3f $$6 = $$2[$$3].target();
            Vector3f $$7 = $$2[$$4].target();
            return $$6.lerp((Vector3fc)$$7, $$1, $$0).mul($$5);
        };
        public static final Interpolation CATMULLROM = ($$0, $$1, $$2, $$3, $$4, $$5) -> {
            Vector3f $$6 = $$2[Math.max(0, $$3 - 1)].target();
            Vector3f $$7 = $$2[$$3].target();
            Vector3f $$8 = $$2[$$4].target();
            Vector3f $$9 = $$2[Math.min($$2.length - 1, $$4 + 1)].target();
            $$0.set(Mth.catmullrom($$1, $$6.x(), $$7.x(), $$8.x(), $$9.x()) * $$5, Mth.catmullrom($$1, $$6.y(), $$7.y(), $$8.y(), $$9.y()) * $$5, Mth.catmullrom($$1, $$6.z(), $$7.z(), $$8.z(), $$9.z()) * $$5);
            return $$0;
        };
    }

    public static class Targets {
        public static final Target POSITION = ModelPart::offsetPos;
        public static final Target ROTATION = ModelPart::offsetRotation;
        public static final Target SCALE = ModelPart::offsetScale;
    }

    public static interface Interpolation {
        public Vector3f apply(Vector3f var1, float var2, Keyframe[] var3, int var4, int var5, float var6);
    }
}

