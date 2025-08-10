/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.Enchantment;

public class ItemEnchantments
implements TooltipProvider {
    public static final ItemEnchantments EMPTY = new ItemEnchantments((Object2IntOpenHashMap<Holder<Enchantment>>)new Object2IntOpenHashMap());
    private static final Codec<Integer> LEVEL_CODEC = Codec.intRange((int)1, (int)255);
    public static final Codec<ItemEnchantments> CODEC = Codec.unboundedMap(Enchantment.CODEC, LEVEL_CODEC).xmap($$0 -> new ItemEnchantments((Object2IntOpenHashMap<Holder<Enchantment>>)new Object2IntOpenHashMap($$0)), $$0 -> $$0.enchantments);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEnchantments> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(Object2IntOpenHashMap::new, Enchantment.STREAM_CODEC, ByteBufCodecs.VAR_INT), $$0 -> $$0.enchantments, ItemEnchantments::new);
    final Object2IntOpenHashMap<Holder<Enchantment>> enchantments;

    ItemEnchantments(Object2IntOpenHashMap<Holder<Enchantment>> $$0) {
        this.enchantments = $$0;
        for (Object2IntMap.Entry $$1 : $$0.object2IntEntrySet()) {
            int $$2 = $$1.getIntValue();
            if ($$2 >= 0 && $$2 <= 255) continue;
            throw new IllegalArgumentException("Enchantment " + String.valueOf($$1.getKey()) + " has invalid level " + $$2);
        }
    }

    public int getLevel(Holder<Enchantment> $$0) {
        return this.enchantments.getInt($$0);
    }

    @Override
    public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
        HolderLookup.Provider $$4 = $$0.registries();
        HolderSet<Enchantment> $$5 = ItemEnchantments.getTagOrEmpty($$4, Registries.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER);
        for (Holder holder : $$5) {
            int $$7 = this.enchantments.getInt((Object)holder);
            if ($$7 <= 0) continue;
            $$1.accept(Enchantment.getFullname(holder, $$7));
        }
        for (Object2IntMap.Entry entry : this.enchantments.object2IntEntrySet()) {
            Holder $$9 = (Holder)entry.getKey();
            if ($$5.contains($$9)) continue;
            $$1.accept(Enchantment.getFullname((Holder)entry.getKey(), entry.getIntValue()));
        }
    }

    private static <T> HolderSet<T> getTagOrEmpty(@Nullable HolderLookup.Provider $$0, ResourceKey<Registry<T>> $$1, TagKey<T> $$2) {
        Optional<HolderSet.Named<T>> $$3;
        if ($$0 != null && ($$3 = $$0.lookupOrThrow($$1).get($$2)).isPresent()) {
            return $$3.get();
        }
        return HolderSet.a(new Holder[0]);
    }

    public Set<Holder<Enchantment>> keySet() {
        return Collections.unmodifiableSet(this.enchantments.keySet());
    }

    public Set<Object2IntMap.Entry<Holder<Enchantment>>> entrySet() {
        return Collections.unmodifiableSet(this.enchantments.object2IntEntrySet());
    }

    public int size() {
        return this.enchantments.size();
    }

    public boolean isEmpty() {
        return this.enchantments.isEmpty();
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof ItemEnchantments) {
            ItemEnchantments $$1 = (ItemEnchantments)$$0;
            return this.enchantments.equals($$1.enchantments);
        }
        return false;
    }

    public int hashCode() {
        return this.enchantments.hashCode();
    }

    public String toString() {
        return "ItemEnchantments{enchantments=" + String.valueOf(this.enchantments) + "}";
    }

    public static class Mutable {
        private final Object2IntOpenHashMap<Holder<Enchantment>> enchantments = new Object2IntOpenHashMap();

        public Mutable(ItemEnchantments $$0) {
            this.enchantments.putAll($$0.enchantments);
        }

        public void set(Holder<Enchantment> $$0, int $$1) {
            if ($$1 <= 0) {
                this.enchantments.removeInt($$0);
            } else {
                this.enchantments.put($$0, Math.min($$1, 255));
            }
        }

        public void upgrade(Holder<Enchantment> $$0, int $$1) {
            if ($$1 > 0) {
                this.enchantments.merge($$0, Math.min($$1, 255), Integer::max);
            }
        }

        public void removeIf(Predicate<Holder<Enchantment>> $$0) {
            this.enchantments.keySet().removeIf($$0);
        }

        public int getLevel(Holder<Enchantment> $$0) {
            return this.enchantments.getOrDefault($$0, 0);
        }

        public Set<Holder<Enchantment>> keySet() {
            return this.enchantments.keySet();
        }

        public ItemEnchantments toImmutable() {
            return new ItemEnchantments(this.enchantments);
        }
    }
}

