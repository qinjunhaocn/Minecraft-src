/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.ContextDimension;
import net.minecraft.client.renderer.item.properties.select.ItemBlockState;
import net.minecraft.client.renderer.item.properties.select.LocalTime;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;

public class ItemModelUtils {
    public static ItemModel.Unbaked plainModel(ResourceLocation $$0) {
        return new BlockModelWrapper.Unbaked($$0, List.of());
    }

    public static ItemModel.Unbaked a(ResourceLocation $$0, ItemTintSource ... $$1) {
        return new BlockModelWrapper.Unbaked($$0, List.of((Object[])$$1));
    }

    public static ItemTintSource constantTint(int $$0) {
        return new Constant($$0);
    }

    public static ItemModel.Unbaked a(ItemModel.Unbaked ... $$0) {
        return new CompositeModel.Unbaked(List.of((Object[])$$0));
    }

    public static ItemModel.Unbaked specialModel(ResourceLocation $$0, SpecialModelRenderer.Unbaked $$1) {
        return new SpecialModelWrapper.Unbaked($$0, $$1);
    }

    public static RangeSelectItemModel.Entry override(ItemModel.Unbaked $$0, float $$1) {
        return new RangeSelectItemModel.Entry($$1, $$0);
    }

    public static ItemModel.Unbaked a(RangeSelectItemModelProperty $$0, ItemModel.Unbaked $$1, RangeSelectItemModel.Entry ... $$2) {
        return new RangeSelectItemModel.Unbaked($$0, 1.0f, List.of((Object[])$$2), Optional.of($$1));
    }

    public static ItemModel.Unbaked a(RangeSelectItemModelProperty $$0, float $$1, ItemModel.Unbaked $$2, RangeSelectItemModel.Entry ... $$3) {
        return new RangeSelectItemModel.Unbaked($$0, $$1, List.of((Object[])$$3), Optional.of($$2));
    }

    public static ItemModel.Unbaked rangeSelect(RangeSelectItemModelProperty $$0, ItemModel.Unbaked $$1, List<RangeSelectItemModel.Entry> $$2) {
        return new RangeSelectItemModel.Unbaked($$0, 1.0f, $$2, Optional.of($$1));
    }

    public static ItemModel.Unbaked rangeSelect(RangeSelectItemModelProperty $$0, List<RangeSelectItemModel.Entry> $$1) {
        return new RangeSelectItemModel.Unbaked($$0, 1.0f, $$1, Optional.empty());
    }

    public static ItemModel.Unbaked rangeSelect(RangeSelectItemModelProperty $$0, float $$1, List<RangeSelectItemModel.Entry> $$2) {
        return new RangeSelectItemModel.Unbaked($$0, $$1, $$2, Optional.empty());
    }

    public static ItemModel.Unbaked conditional(ConditionalItemModelProperty $$0, ItemModel.Unbaked $$1, ItemModel.Unbaked $$2) {
        return new ConditionalItemModel.Unbaked($$0, $$1, $$2);
    }

    public static <T> SelectItemModel.SwitchCase<T> when(T $$0, ItemModel.Unbaked $$1) {
        return new SelectItemModel.SwitchCase(List.of($$0), $$1);
    }

    public static <T> SelectItemModel.SwitchCase<T> when(List<T> $$0, ItemModel.Unbaked $$1) {
        return new SelectItemModel.SwitchCase<T>($$0, $$1);
    }

    @SafeVarargs
    public static <T> ItemModel.Unbaked a(SelectItemModelProperty<T> $$0, ItemModel.Unbaked $$1, SelectItemModel.SwitchCase<T> ... $$2) {
        return ItemModelUtils.select($$0, $$1, List.of($$2));
    }

    public static <T> ItemModel.Unbaked select(SelectItemModelProperty<T> $$0, ItemModel.Unbaked $$1, List<SelectItemModel.SwitchCase<T>> $$2) {
        return new SelectItemModel.Unbaked(new SelectItemModel.UnbakedSwitch<SelectItemModelProperty<T>, T>($$0, $$2), Optional.of($$1));
    }

    @SafeVarargs
    public static <T> ItemModel.Unbaked a(SelectItemModelProperty<T> $$0, SelectItemModel.SwitchCase<T> ... $$1) {
        return ItemModelUtils.select($$0, List.of($$1));
    }

    public static <T> ItemModel.Unbaked select(SelectItemModelProperty<T> $$0, List<SelectItemModel.SwitchCase<T>> $$1) {
        return new SelectItemModel.Unbaked(new SelectItemModel.UnbakedSwitch<SelectItemModelProperty<T>, T>($$0, $$1), Optional.empty());
    }

    public static ConditionalItemModelProperty isUsingItem() {
        return new IsUsingItem();
    }

    public static ConditionalItemModelProperty hasComponent(DataComponentType<?> $$0) {
        return new HasComponent($$0, false);
    }

    public static ItemModel.Unbaked inOverworld(ItemModel.Unbaked $$0, ItemModel.Unbaked $$1) {
        return ItemModelUtils.a(new ContextDimension(), $$1, ItemModelUtils.when(Level.OVERWORLD, $$0));
    }

    public static <T extends Comparable<T>> ItemModel.Unbaked selectBlockItemProperty(Property<T> $$0, ItemModel.Unbaked $$12, Map<T, ItemModel.Unbaked> $$2) {
        List $$3 = $$2.entrySet().stream().sorted(Map.Entry.comparingByKey()).map($$1 -> {
            String $$2 = $$0.getName((Comparable)$$1.getKey());
            return new SelectItemModel.SwitchCase(List.of((Object)$$2), (ItemModel.Unbaked)$$1.getValue());
        }).toList();
        return ItemModelUtils.select(new ItemBlockState($$0.getName()), $$12, $$3);
    }

    public static ItemModel.Unbaked isXmas(ItemModel.Unbaked $$0, ItemModel.Unbaked $$1) {
        return ItemModelUtils.select(LocalTime.create("MM-dd", "", Optional.empty()), $$1, List.of(ItemModelUtils.when(List.of((Object)"12-24", (Object)"12-25", (Object)"12-26"), $$0)));
    }
}

