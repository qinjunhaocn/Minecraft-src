/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.renderer;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.renderer.UniformValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public record PostChainConfig(Map<ResourceLocation, InternalTarget> internalTargets, List<Pass> passes) {
    public static final Codec<PostChainConfig> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.unboundedMap(ResourceLocation.CODEC, InternalTarget.CODEC).optionalFieldOf("targets", (Object)Map.of()).forGetter(PostChainConfig::internalTargets), (App)Pass.CODEC.listOf().optionalFieldOf("passes", (Object)List.of()).forGetter(PostChainConfig::passes)).apply((Applicative)$$0, PostChainConfig::new));

    public record InternalTarget(Optional<Integer> width, Optional<Integer> height, boolean persistent, int clearColor) {
        public static final Codec<InternalTarget> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.POSITIVE_INT.optionalFieldOf("width").forGetter(InternalTarget::width), (App)ExtraCodecs.POSITIVE_INT.optionalFieldOf("height").forGetter(InternalTarget::height), (App)Codec.BOOL.optionalFieldOf("persistent", (Object)false).forGetter(InternalTarget::persistent), (App)ExtraCodecs.ARGB_COLOR_CODEC.optionalFieldOf("clear_color", (Object)0).forGetter(InternalTarget::clearColor)).apply((Applicative)$$0, InternalTarget::new));
    }

    public record Pass(ResourceLocation vertexShaderId, ResourceLocation fragmentShaderId, List<Input> inputs, ResourceLocation outputTarget, Map<String, List<UniformValue>> uniforms) {
        private static final Codec<List<Input>> INPUTS_CODEC = Input.CODEC.listOf().validate($$0 -> {
            ObjectArraySet $$1 = new ObjectArraySet($$0.size());
            for (Input $$2 : $$0) {
                if ($$1.add($$2.samplerName())) continue;
                return DataResult.error(() -> "Encountered repeated sampler name: " + $$2.samplerName());
            }
            return DataResult.success((Object)$$0);
        });
        private static final Codec<Map<String, List<UniformValue>>> UNIFORM_BLOCKS_CODEC = Codec.unboundedMap((Codec)Codec.STRING, (Codec)UniformValue.CODEC.listOf());
        public static final Codec<Pass> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("vertex_shader").forGetter(Pass::vertexShaderId), (App)ResourceLocation.CODEC.fieldOf("fragment_shader").forGetter(Pass::fragmentShaderId), (App)INPUTS_CODEC.optionalFieldOf("inputs", (Object)List.of()).forGetter(Pass::inputs), (App)ResourceLocation.CODEC.fieldOf("output").forGetter(Pass::outputTarget), (App)UNIFORM_BLOCKS_CODEC.optionalFieldOf("uniforms", (Object)Map.of()).forGetter(Pass::uniforms)).apply((Applicative)$$0, Pass::new));

        public Stream<ResourceLocation> referencedTargets() {
            Stream $$02 = this.inputs.stream().flatMap($$0 -> $$0.referencedTargets().stream());
            return Stream.concat($$02, Stream.of(this.outputTarget));
        }
    }

    public record TargetInput(String samplerName, ResourceLocation targetId, boolean useDepthBuffer, boolean bilinear) implements Input
    {
        public static final Codec<TargetInput> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.STRING.fieldOf("sampler_name").forGetter(TargetInput::samplerName), (App)ResourceLocation.CODEC.fieldOf("target").forGetter(TargetInput::targetId), (App)Codec.BOOL.optionalFieldOf("use_depth_buffer", (Object)false).forGetter(TargetInput::useDepthBuffer), (App)Codec.BOOL.optionalFieldOf("bilinear", (Object)false).forGetter(TargetInput::bilinear)).apply((Applicative)$$0, TargetInput::new));

        @Override
        public Set<ResourceLocation> referencedTargets() {
            return Set.of((Object)this.targetId);
        }
    }

    public record TextureInput(String samplerName, ResourceLocation location, int width, int height, boolean bilinear) implements Input
    {
        public static final Codec<TextureInput> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.STRING.fieldOf("sampler_name").forGetter(TextureInput::samplerName), (App)ResourceLocation.CODEC.fieldOf("location").forGetter(TextureInput::location), (App)ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(TextureInput::width), (App)ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(TextureInput::height), (App)Codec.BOOL.optionalFieldOf("bilinear", (Object)false).forGetter(TextureInput::bilinear)).apply((Applicative)$$0, TextureInput::new));

        @Override
        public Set<ResourceLocation> referencedTargets() {
            return Set.of();
        }
    }

    public static sealed interface Input
    permits TextureInput, TargetInput {
        public static final Codec<Input> CODEC = Codec.xor(TextureInput.CODEC, TargetInput.CODEC).xmap($$0 -> (Input)$$0.map(Function.identity(), Function.identity()), $$0 -> {
            Input input = $$0;
            Objects.requireNonNull(input);
            Input $$1 = input;
            int $$2 = 0;
            return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{TextureInput.class, TargetInput.class}, (Object)$$1, (int)$$2)) {
                default -> throw new MatchException(null, null);
                case 0 -> {
                    TextureInput $$3 = (TextureInput)$$1;
                    yield Either.left((Object)$$3);
                }
                case 1 -> {
                    TargetInput $$4 = (TargetInput)$$1;
                    yield Either.right((Object)$$4);
                }
            };
        });

        public String samplerName();

        public Set<ResourceLocation> referencedTargets();
    }
}

