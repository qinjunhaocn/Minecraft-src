/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.client.player.inventory;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class Hotbar {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SIZE = Inventory.getSelectionSize();
    public static final Codec<Hotbar> CODEC = Codec.PASSTHROUGH.listOf().validate($$0 -> Util.fixedSize($$0, SIZE)).xmap(Hotbar::new, $$0 -> $$0.items);
    private static final DynamicOps<Tag> DEFAULT_OPS = NbtOps.INSTANCE;
    private static final Dynamic<?> EMPTY_STACK = new Dynamic(DEFAULT_OPS, (Object)((Tag)ItemStack.OPTIONAL_CODEC.encodeStart(DEFAULT_OPS, (Object)ItemStack.EMPTY).getOrThrow()));
    private List<Dynamic<?>> items;

    private Hotbar(List<Dynamic<?>> $$0) {
        this.items = $$0;
    }

    public Hotbar() {
        this(Collections.nCopies(SIZE, EMPTY_STACK));
    }

    public List<ItemStack> load(HolderLookup.Provider $$0) {
        return this.items.stream().map($$1 -> ItemStack.OPTIONAL_CODEC.parse(RegistryOps.injectRegistryContext($$1, $$0)).resultOrPartial($$0 -> LOGGER.warn("Could not parse hotbar item: {}", $$0)).orElse(ItemStack.EMPTY)).toList();
    }

    public void storeFrom(Inventory $$02, RegistryAccess $$1) {
        RegistryOps<Tag> $$2 = $$1.createSerializationContext(DEFAULT_OPS);
        ImmutableList.Builder $$3 = ImmutableList.builderWithExpectedSize(SIZE);
        for (int $$4 = 0; $$4 < SIZE; ++$$4) {
            ItemStack $$5 = $$02.getItem($$4);
            Optional<Dynamic> $$6 = ItemStack.OPTIONAL_CODEC.encodeStart($$2, (Object)$$5).resultOrPartial($$0 -> LOGGER.warn("Could not encode hotbar item: {}", $$0)).map($$0 -> new Dynamic(DEFAULT_OPS, $$0));
            $$3.add($$6.orElse(EMPTY_STACK));
        }
        this.items = $$3.build();
    }

    public boolean isEmpty() {
        for (Dynamic<?> $$0 : this.items) {
            if (Hotbar.isEmpty($$0)) continue;
            return false;
        }
        return true;
    }

    private static boolean isEmpty(Dynamic<?> $$0) {
        return EMPTY_STACK.equals($$0);
    }
}

