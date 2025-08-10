/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public record EquipmentClientInfo(Map<LayerType, List<Layer>> layers) {
    private static final Codec<List<Layer>> LAYER_LIST_CODEC = ExtraCodecs.nonEmptyList(Layer.CODEC.listOf());
    public static final Codec<EquipmentClientInfo> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.nonEmptyMap(Codec.unboundedMap(LayerType.CODEC, LAYER_LIST_CODEC)).fieldOf("layers").forGetter(EquipmentClientInfo::layers)).apply((Applicative)$$0, EquipmentClientInfo::new));

    public static Builder builder() {
        return new Builder();
    }

    public List<Layer> getLayers(LayerType $$0) {
        return this.layers.getOrDefault($$0, List.of());
    }

    public static class Builder {
        private final Map<LayerType, List<Layer>> layersByType = new EnumMap<LayerType, List<Layer>>(LayerType.class);

        Builder() {
        }

        public Builder addHumanoidLayers(ResourceLocation $$0) {
            return this.addHumanoidLayers($$0, false);
        }

        public Builder addHumanoidLayers(ResourceLocation $$0, boolean $$1) {
            this.a(LayerType.HUMANOID_LEGGINGS, Layer.leatherDyeable($$0, $$1));
            this.addMainHumanoidLayer($$0, $$1);
            return this;
        }

        public Builder addMainHumanoidLayer(ResourceLocation $$0, boolean $$1) {
            return this.a(LayerType.HUMANOID, Layer.leatherDyeable($$0, $$1));
        }

        public Builder a(LayerType $$02, Layer ... $$1) {
            Collections.addAll(this.layersByType.computeIfAbsent($$02, $$0 -> new ArrayList()), $$1);
            return this;
        }

        public EquipmentClientInfo build() {
            return new EquipmentClientInfo((Map<LayerType, List<Layer>>)this.layersByType.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, $$0 -> List.copyOf((Collection)((Collection)$$0.getValue())))));
        }
    }

    public static final class LayerType
    extends Enum<LayerType>
    implements StringRepresentable {
        public static final /* enum */ LayerType HUMANOID = new LayerType("humanoid");
        public static final /* enum */ LayerType HUMANOID_LEGGINGS = new LayerType("humanoid_leggings");
        public static final /* enum */ LayerType WINGS = new LayerType("wings");
        public static final /* enum */ LayerType WOLF_BODY = new LayerType("wolf_body");
        public static final /* enum */ LayerType HORSE_BODY = new LayerType("horse_body");
        public static final /* enum */ LayerType LLAMA_BODY = new LayerType("llama_body");
        public static final /* enum */ LayerType PIG_SADDLE = new LayerType("pig_saddle");
        public static final /* enum */ LayerType STRIDER_SADDLE = new LayerType("strider_saddle");
        public static final /* enum */ LayerType CAMEL_SADDLE = new LayerType("camel_saddle");
        public static final /* enum */ LayerType HORSE_SADDLE = new LayerType("horse_saddle");
        public static final /* enum */ LayerType DONKEY_SADDLE = new LayerType("donkey_saddle");
        public static final /* enum */ LayerType MULE_SADDLE = new LayerType("mule_saddle");
        public static final /* enum */ LayerType ZOMBIE_HORSE_SADDLE = new LayerType("zombie_horse_saddle");
        public static final /* enum */ LayerType SKELETON_HORSE_SADDLE = new LayerType("skeleton_horse_saddle");
        public static final /* enum */ LayerType HAPPY_GHAST_BODY = new LayerType("happy_ghast_body");
        public static final Codec<LayerType> CODEC;
        private final String id;
        private static final /* synthetic */ LayerType[] $VALUES;

        public static LayerType[] values() {
            return (LayerType[])$VALUES.clone();
        }

        public static LayerType valueOf(String $$0) {
            return Enum.valueOf(LayerType.class, $$0);
        }

        private LayerType(String $$0) {
            this.id = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }

        public String trimAssetPrefix() {
            return "trims/entity/" + this.id;
        }

        private static /* synthetic */ LayerType[] b() {
            return new LayerType[]{HUMANOID, HUMANOID_LEGGINGS, WINGS, WOLF_BODY, HORSE_BODY, LLAMA_BODY, PIG_SADDLE, STRIDER_SADDLE, CAMEL_SADDLE, HORSE_SADDLE, DONKEY_SADDLE, MULE_SADDLE, ZOMBIE_HORSE_SADDLE, SKELETON_HORSE_SADDLE, HAPPY_GHAST_BODY};
        }

        static {
            $VALUES = LayerType.b();
            CODEC = StringRepresentable.fromEnum(LayerType::values);
        }
    }

    public record Layer(ResourceLocation textureId, Optional<Dyeable> dyeable, boolean usePlayerTexture) {
        public static final Codec<Layer> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("texture").forGetter(Layer::textureId), (App)Dyeable.CODEC.optionalFieldOf("dyeable").forGetter(Layer::dyeable), (App)Codec.BOOL.optionalFieldOf("use_player_texture", (Object)false).forGetter(Layer::usePlayerTexture)).apply((Applicative)$$0, Layer::new));

        public Layer(ResourceLocation $$0) {
            this($$0, Optional.empty(), false);
        }

        public static Layer leatherDyeable(ResourceLocation $$0, boolean $$1) {
            return new Layer($$0, $$1 ? Optional.of(new Dyeable(Optional.of(-6265536))) : Optional.empty(), false);
        }

        public static Layer onlyIfDyed(ResourceLocation $$0, boolean $$1) {
            return new Layer($$0, $$1 ? Optional.of(new Dyeable(Optional.empty())) : Optional.empty(), false);
        }

        public ResourceLocation getTextureLocation(LayerType $$0) {
            return this.textureId.withPath($$1 -> "textures/entity/equipment/" + $$0.getSerializedName() + "/" + $$1 + ".png");
        }
    }

    public record Dyeable(Optional<Integer> colorWhenUndyed) {
        public static final Codec<Dyeable> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color_when_undyed").forGetter(Dyeable::colorWhenUndyed)).apply((Applicative)$$0, Dyeable::new));
    }
}

