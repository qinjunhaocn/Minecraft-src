/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.conditional.Broken;
import net.minecraft.client.renderer.item.properties.conditional.BundleHasSelectedItem;
import net.minecraft.client.renderer.item.properties.conditional.ComponentMatches;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.conditional.CustomModelDataProperty;
import net.minecraft.client.renderer.item.properties.conditional.Damaged;
import net.minecraft.client.renderer.item.properties.conditional.ExtendedView;
import net.minecraft.client.renderer.item.properties.conditional.FishingRodCast;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.client.renderer.item.properties.conditional.IsCarried;
import net.minecraft.client.renderer.item.properties.conditional.IsKeybindDown;
import net.minecraft.client.renderer.item.properties.conditional.IsSelected;
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem;
import net.minecraft.client.renderer.item.properties.conditional.IsViewEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class ConditionalItemModelProperties {
    private static final ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends ConditionalItemModelProperty>> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper();
    public static final MapCodec<ConditionalItemModelProperty> MAP_CODEC = ID_MAPPER.codec(ResourceLocation.CODEC).dispatchMap("property", ConditionalItemModelProperty::type, $$0 -> $$0);

    public static void bootstrap() {
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("custom_model_data"), CustomModelDataProperty.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("using_item"), IsUsingItem.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("broken"), Broken.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("damaged"), Damaged.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("fishing_rod/cast"), FishingRodCast.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("has_component"), HasComponent.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("bundle/has_selected_item"), BundleHasSelectedItem.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("selected"), IsSelected.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("carried"), IsCarried.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("extended_view"), ExtendedView.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("keybind_down"), IsKeybindDown.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("view_entity"), IsViewEntity.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("component"), ComponentMatches.MAP_CODEC);
    }
}

