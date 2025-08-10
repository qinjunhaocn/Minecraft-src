/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.entity.animal;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

public record PigVariant(ModelAndTexture<ModelType> modelAndTexture, SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition>
{
    public static final Codec<PigVariant> DIRECT_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ModelAndTexture.codec(ModelType.CODEC, ModelType.NORMAL).forGetter(PigVariant::modelAndTexture), (App)SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(PigVariant::spawnConditions)).apply((Applicative)$$0, PigVariant::new));
    public static final Codec<PigVariant> NETWORK_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ModelAndTexture.codec(ModelType.CODEC, ModelType.NORMAL).forGetter(PigVariant::modelAndTexture)).apply((Applicative)$$0, PigVariant::new));
    public static final Codec<Holder<PigVariant>> CODEC = RegistryFixedCodec.create(Registries.PIG_VARIANT);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<PigVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.PIG_VARIANT);

    private PigVariant(ModelAndTexture<ModelType> $$0) {
        this($$0, SpawnPrioritySelectors.EMPTY);
    }

    @Override
    public List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors() {
        return this.spawnConditions.selectors();
    }

    public static final class ModelType
    extends Enum<ModelType>
    implements StringRepresentable {
        public static final /* enum */ ModelType NORMAL = new ModelType("normal");
        public static final /* enum */ ModelType COLD = new ModelType("cold");
        public static final Codec<ModelType> CODEC;
        private final String name;
        private static final /* synthetic */ ModelType[] $VALUES;

        public static ModelType[] values() {
            return (ModelType[])$VALUES.clone();
        }

        public static ModelType valueOf(String $$0) {
            return Enum.valueOf(ModelType.class, $$0);
        }

        private ModelType(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ ModelType[] a() {
            return new ModelType[]{NORMAL, COLD};
        }

        static {
            $VALUES = ModelType.a();
            CODEC = StringRepresentable.fromEnum(ModelType::values);
        }
    }
}

