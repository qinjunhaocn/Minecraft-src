/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelLocationUtils {
    @Deprecated
    public static ResourceLocation decorateBlockModelLocation(String $$0) {
        return ResourceLocation.withDefaultNamespace("block/" + $$0);
    }

    public static ResourceLocation decorateItemModelLocation(String $$0) {
        return ResourceLocation.withDefaultNamespace("item/" + $$0);
    }

    public static ResourceLocation getModelLocation(Block $$0, String $$12) {
        ResourceLocation $$2 = BuiltInRegistries.BLOCK.getKey($$0);
        return $$2.withPath($$1 -> "block/" + $$1 + $$12);
    }

    public static ResourceLocation getModelLocation(Block $$0) {
        ResourceLocation $$1 = BuiltInRegistries.BLOCK.getKey($$0);
        return $$1.withPrefix("block/");
    }

    public static ResourceLocation getModelLocation(Item $$0) {
        ResourceLocation $$1 = BuiltInRegistries.ITEM.getKey($$0);
        return $$1.withPrefix("item/");
    }

    public static ResourceLocation getModelLocation(Item $$0, String $$12) {
        ResourceLocation $$2 = BuiltInRegistries.ITEM.getKey($$0);
        return $$2.withPath($$1 -> "item/" + $$1 + $$12);
    }
}

