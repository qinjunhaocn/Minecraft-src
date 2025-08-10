/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record ComponentContents<T>(DataComponentType<T> componentType) implements SelectItemModelProperty<T>
{
    private static final SelectItemModelProperty.Type<? extends ComponentContents<?>, ?> TYPE = ComponentContents.createType();

    private static <T> SelectItemModelProperty.Type<ComponentContents<T>, T> createType() {
        Codec $$02;
        Codec $$1 = $$02 = BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().validate($$0 -> {
            if ($$0.isTransient()) {
                return DataResult.error(() -> "Component can't be serialized");
            }
            return DataResult.success((Object)$$0);
        });
        MapCodec $$2 = $$1.dispatchMap("component", $$0 -> ((ComponentContents)$$0.property()).componentType, $$0 -> SelectItemModelProperty.Type.createCasesFieldCodec($$0.codecOrThrow()).xmap($$1 -> new SelectItemModel.UnbakedSwitch(new ComponentContents($$0), $$1), SelectItemModel.UnbakedSwitch::cases));
        return new SelectItemModelProperty.Type($$2);
    }

    public static <T> SelectItemModelProperty.Type<ComponentContents<T>, T> castType() {
        return TYPE;
    }

    @Override
    @Nullable
    public T get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3, ItemDisplayContext $$4) {
        return $$0.get(this.componentType);
    }

    @Override
    public SelectItemModelProperty.Type<ComponentContents<T>, T> type() {
        return ComponentContents.castType();
    }

    @Override
    public Codec<T> valueCodec() {
        return this.componentType.codecOrThrow();
    }
}

