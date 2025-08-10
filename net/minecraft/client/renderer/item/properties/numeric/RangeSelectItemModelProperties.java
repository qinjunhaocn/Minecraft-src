/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.numeric.BundleFullness;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngle;
import net.minecraft.client.renderer.item.properties.numeric.Cooldown;
import net.minecraft.client.renderer.item.properties.numeric.Count;
import net.minecraft.client.renderer.item.properties.numeric.CrossbowPull;
import net.minecraft.client.renderer.item.properties.numeric.CustomModelDataProperty;
import net.minecraft.client.renderer.item.properties.numeric.Damage;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.Time;
import net.minecraft.client.renderer.item.properties.numeric.UseCycle;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class RangeSelectItemModelProperties {
    private static final ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends RangeSelectItemModelProperty>> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper();
    public static final MapCodec<RangeSelectItemModelProperty> MAP_CODEC = ID_MAPPER.codec(ResourceLocation.CODEC).dispatchMap("property", RangeSelectItemModelProperty::type, $$0 -> $$0);

    public static void bootstrap() {
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("custom_model_data"), CustomModelDataProperty.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("bundle/fullness"), BundleFullness.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("damage"), Damage.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("cooldown"), Cooldown.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("time"), Time.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("compass"), CompassAngle.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("crossbow/pull"), CrossbowPull.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("use_cycle"), UseCycle.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("use_duration"), UseDuration.MAP_CODEC);
        ID_MAPPER.put(ResourceLocation.withDefaultNamespace("count"), Count.MAP_CODEC);
    }
}

