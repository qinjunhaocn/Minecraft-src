/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.block.model;

import com.mojang.math.Quadrant;
import java.util.function.UnaryOperator;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface VariantMutator
extends UnaryOperator<Variant> {
    public static final VariantProperty<Quadrant> X_ROT = Variant::withXRot;
    public static final VariantProperty<Quadrant> Y_ROT = Variant::withYRot;
    public static final VariantProperty<ResourceLocation> MODEL = Variant::withModel;
    public static final VariantProperty<Boolean> UV_LOCK = Variant::withUvLock;

    default public VariantMutator then(VariantMutator $$0) {
        return $$1 -> (Variant)$$0.apply((Variant)this.apply($$1));
    }

    @FunctionalInterface
    public static interface VariantProperty<T> {
        public Variant apply(Variant var1, T var2);

        default public VariantMutator withValue(T $$0) {
            return $$1 -> this.apply((Variant)$$1, $$0);
        }
    }
}

