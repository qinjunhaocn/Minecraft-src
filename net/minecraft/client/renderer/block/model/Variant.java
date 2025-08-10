/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer.block.model;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.math.Quadrant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.ResourceLocation;

public record Variant(ResourceLocation modelLocation, SimpleModelState modelState) implements BlockModelPart.Unbaked
{
    public static final MapCodec<Variant> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("model").forGetter(Variant::modelLocation), (App)SimpleModelState.MAP_CODEC.forGetter(Variant::modelState)).apply((Applicative)$$0, Variant::new));
    public static final Codec<Variant> CODEC = MAP_CODEC.codec();

    public Variant(ResourceLocation $$0) {
        this($$0, SimpleModelState.DEFAULT);
    }

    public Variant withXRot(Quadrant $$0) {
        return this.withState(this.modelState.withX($$0));
    }

    public Variant withYRot(Quadrant $$0) {
        return this.withState(this.modelState.withY($$0));
    }

    public Variant withUvLock(boolean $$0) {
        return this.withState(this.modelState.withUvLock($$0));
    }

    public Variant withModel(ResourceLocation $$0) {
        return new Variant($$0, this.modelState);
    }

    public Variant withState(SimpleModelState $$0) {
        return new Variant(this.modelLocation, $$0);
    }

    public Variant with(VariantMutator $$0) {
        return (Variant)$$0.apply(this);
    }

    @Override
    public BlockModelPart bake(ModelBaker $$0) {
        return SimpleModelWrapper.bake($$0, this.modelLocation, this.modelState.asModelState());
    }

    @Override
    public void resolveDependencies(ResolvableModel.Resolver $$0) {
        $$0.markDependency(this.modelLocation);
    }

    public record SimpleModelState(Quadrant x, Quadrant y, boolean uvLock) {
        public static final MapCodec<SimpleModelState> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Quadrant.CODEC.optionalFieldOf("x", (Object)Quadrant.R0).forGetter(SimpleModelState::x), (App)Quadrant.CODEC.optionalFieldOf("y", (Object)Quadrant.R0).forGetter(SimpleModelState::y), (App)Codec.BOOL.optionalFieldOf("uvlock", (Object)false).forGetter(SimpleModelState::uvLock)).apply((Applicative)$$0, SimpleModelState::new));
        public static final SimpleModelState DEFAULT = new SimpleModelState(Quadrant.R0, Quadrant.R0, false);

        public ModelState asModelState() {
            BlockModelRotation $$0 = BlockModelRotation.by(this.x, this.y);
            return this.uvLock ? $$0.withUvLock() : $$0;
        }

        public SimpleModelState withX(Quadrant $$0) {
            return new SimpleModelState($$0, this.y, this.uvLock);
        }

        public SimpleModelState withY(Quadrant $$0) {
            return new SimpleModelState(this.x, $$0, this.uvLock);
        }

        public SimpleModelState withUvLock(boolean $$0) {
            return new SimpleModelState(this.x, this.y, $$0);
        }
    }
}

