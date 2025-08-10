/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.animation;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelPart;

public record AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AnimationChannel>> boneAnimations) {
    public KeyframeAnimation bake(ModelPart $$0) {
        return KeyframeAnimation.bake($$0, this);
    }

    public static class Builder {
        private final float length;
        private final Map<String, List<AnimationChannel>> animationByBone = Maps.newHashMap();
        private boolean looping;

        public static Builder withLength(float $$0) {
            return new Builder($$0);
        }

        private Builder(float $$0) {
            this.length = $$0;
        }

        public Builder looping() {
            this.looping = true;
            return this;
        }

        public Builder addAnimation(String $$02, AnimationChannel $$1) {
            this.animationByBone.computeIfAbsent($$02, $$0 -> new ArrayList()).add($$1);
            return this;
        }

        public AnimationDefinition build() {
            return new AnimationDefinition(this.length, this.looping, this.animationByBone);
        }
    }
}

