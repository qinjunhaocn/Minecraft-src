/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.ItemLike;

public interface SuspiciousEffectHolder {
    public SuspiciousStewEffects getSuspiciousEffects();

    public static List<SuspiciousEffectHolder> getAllEffectHolders() {
        return BuiltInRegistries.ITEM.stream().map(SuspiciousEffectHolder::tryGet).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Nullable
    public static SuspiciousEffectHolder tryGet(ItemLike $$0) {
        BlockItem $$1;
        FeatureElement featureElement = $$0.asItem();
        if (featureElement instanceof BlockItem && (featureElement = ($$1 = (BlockItem)featureElement).getBlock()) instanceof SuspiciousEffectHolder) {
            SuspiciousEffectHolder $$2 = (SuspiciousEffectHolder)((Object)featureElement);
            return $$2;
        }
        Item item = $$0.asItem();
        if (item instanceof SuspiciousEffectHolder) {
            SuspiciousEffectHolder $$3 = (SuspiciousEffectHolder)((Object)item);
            return $$3;
        }
        return null;
    }
}

