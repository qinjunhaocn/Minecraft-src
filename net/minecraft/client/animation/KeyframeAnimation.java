/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.joml.Vector3f
 */
package net.minecraft.client.animation;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import org.joml.Vector3f;

public class KeyframeAnimation {
    private final AnimationDefinition definition;
    private final List<Entry> entries;
    private final Vector3f scratchVector = new Vector3f();

    private KeyframeAnimation(AnimationDefinition $$0, List<Entry> $$1) {
        this.definition = $$0;
        this.entries = $$1;
    }

    static KeyframeAnimation bake(ModelPart $$0, AnimationDefinition $$1) {
        ArrayList<Entry> $$2 = new ArrayList<Entry>();
        Function<String, ModelPart> $$3 = $$0.createPartLookup();
        for (Map.Entry<String, List<AnimationChannel>> $$4 : $$1.boneAnimations().entrySet()) {
            String $$5 = $$4.getKey();
            List<AnimationChannel> $$6 = $$4.getValue();
            ModelPart $$7 = $$3.apply($$5);
            if ($$7 == null) {
                throw new IllegalArgumentException("Cannot animate " + $$5 + ", which does not exist in model");
            }
            for (AnimationChannel $$8 : $$6) {
                $$2.add(new Entry($$7, $$8.target(), $$8.b()));
            }
        }
        return new KeyframeAnimation($$1, List.copyOf($$2));
    }

    public void applyStatic() {
        this.apply(0L, 1.0f);
    }

    public void applyWalk(float $$0, float $$1, float $$2, float $$3) {
        long $$4 = (long)($$0 * 50.0f * $$2);
        float $$5 = Math.min($$1 * $$3, 1.0f);
        this.apply($$4, $$5);
    }

    public void apply(AnimationState $$0, float $$1) {
        this.apply($$0, $$1, 1.0f);
    }

    public void apply(AnimationState $$0, float $$1, float $$22) {
        $$0.ifStarted($$2 -> this.apply((long)((float)$$2.getTimeInMillis($$1) * $$22), 1.0f));
    }

    public void apply(long $$0, float $$1) {
        float $$2 = this.getElapsedSeconds($$0);
        for (Entry $$3 : this.entries) {
            $$3.apply($$2, $$1, this.scratchVector);
        }
    }

    private float getElapsedSeconds(long $$0) {
        float $$1 = (float)$$0 / 1000.0f;
        return this.definition.looping() ? $$1 % this.definition.lengthInSeconds() : $$1;
    }

    static final class Entry
    extends Record {
        private final ModelPart part;
        private final AnimationChannel.Target target;
        private final Keyframe[] keyframes;

        Entry(ModelPart $$0, AnimationChannel.Target $$1, Keyframe[] $$2) {
            this.part = $$0;
            this.target = $$1;
            this.keyframes = $$2;
        }

        public void apply(float $$0, float $$12, Vector3f $$2) {
            float $$9;
            int $$3 = Math.max(0, Mth.binarySearch(0, this.keyframes.length, $$1 -> $$0 <= this.keyframes[$$1].timestamp()) - 1);
            int $$4 = Math.min(this.keyframes.length - 1, $$3 + 1);
            Keyframe $$5 = this.keyframes[$$3];
            Keyframe $$6 = this.keyframes[$$4];
            float $$7 = $$0 - $$5.timestamp();
            if ($$4 != $$3) {
                float $$8 = Mth.clamp($$7 / ($$6.timestamp() - $$5.timestamp()), 0.0f, 1.0f);
            } else {
                $$9 = 0.0f;
            }
            $$6.interpolation().apply($$2, $$9, this.keyframes, $$3, $$4, $$12);
            this.target.apply(this.part, $$2);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "part;target;keyframes", "part", "target", "keyframes"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "part;target;keyframes", "part", "target", "keyframes"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "part;target;keyframes", "part", "target", "keyframes"}, this, $$0);
        }

        public ModelPart part() {
            return this.part;
        }

        public AnimationChannel.Target target() {
            return this.target;
        }

        public Keyframe[] c() {
            return this.keyframes;
        }
    }
}

