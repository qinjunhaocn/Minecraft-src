/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.enchantment.providers;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCostWithDifficulty;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;

public interface VanillaEnchantmentProviders {
    public static final ResourceKey<EnchantmentProvider> MOB_SPAWN_EQUIPMENT = VanillaEnchantmentProviders.create("mob_spawn_equipment");
    public static final ResourceKey<EnchantmentProvider> PILLAGER_SPAWN_CROSSBOW = VanillaEnchantmentProviders.create("pillager_spawn_crossbow");
    public static final ResourceKey<EnchantmentProvider> RAID_PILLAGER_POST_WAVE_3 = VanillaEnchantmentProviders.create("raid/pillager_post_wave_3");
    public static final ResourceKey<EnchantmentProvider> RAID_PILLAGER_POST_WAVE_5 = VanillaEnchantmentProviders.create("raid/pillager_post_wave_5");
    public static final ResourceKey<EnchantmentProvider> RAID_VINDICATOR = VanillaEnchantmentProviders.create("raid/vindicator");
    public static final ResourceKey<EnchantmentProvider> RAID_VINDICATOR_POST_WAVE_5 = VanillaEnchantmentProviders.create("raid/vindicator_post_wave_5");
    public static final ResourceKey<EnchantmentProvider> ENDERMAN_LOOT_DROP = VanillaEnchantmentProviders.create("enderman_loot_drop");

    public static void bootstrap(BootstrapContext<EnchantmentProvider> $$0) {
        HolderGetter<Enchantment> $$1 = $$0.lookup(Registries.ENCHANTMENT);
        $$0.register(MOB_SPAWN_EQUIPMENT, new EnchantmentsByCostWithDifficulty($$1.getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT), 5, 17));
        $$0.register(PILLAGER_SPAWN_CROSSBOW, new SingleEnchantment($$1.getOrThrow(Enchantments.PIERCING), ConstantInt.of(1)));
        $$0.register(RAID_PILLAGER_POST_WAVE_3, new SingleEnchantment($$1.getOrThrow(Enchantments.QUICK_CHARGE), ConstantInt.of(1)));
        $$0.register(RAID_PILLAGER_POST_WAVE_5, new SingleEnchantment($$1.getOrThrow(Enchantments.QUICK_CHARGE), ConstantInt.of(2)));
        $$0.register(RAID_VINDICATOR, new SingleEnchantment($$1.getOrThrow(Enchantments.SHARPNESS), ConstantInt.of(1)));
        $$0.register(RAID_VINDICATOR_POST_WAVE_5, new SingleEnchantment($$1.getOrThrow(Enchantments.SHARPNESS), ConstantInt.of(2)));
        $$0.register(ENDERMAN_LOOT_DROP, new SingleEnchantment($$1.getOrThrow(Enchantments.SILK_TOUCH), ConstantInt.of(1)));
    }

    public static ResourceKey<EnchantmentProvider> create(String $$0) {
        return ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, ResourceLocation.withDefaultNamespace($$0));
    }
}

