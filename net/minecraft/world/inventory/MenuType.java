/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.inventory;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.inventory.StonecutterMenu;

public class MenuType<T extends AbstractContainerMenu>
implements FeatureElement {
    public static final MenuType<ChestMenu> GENERIC_9x1 = MenuType.register("generic_9x1", ChestMenu::oneRow);
    public static final MenuType<ChestMenu> GENERIC_9x2 = MenuType.register("generic_9x2", ChestMenu::twoRows);
    public static final MenuType<ChestMenu> GENERIC_9x3 = MenuType.register("generic_9x3", ChestMenu::threeRows);
    public static final MenuType<ChestMenu> GENERIC_9x4 = MenuType.register("generic_9x4", ChestMenu::fourRows);
    public static final MenuType<ChestMenu> GENERIC_9x5 = MenuType.register("generic_9x5", ChestMenu::fiveRows);
    public static final MenuType<ChestMenu> GENERIC_9x6 = MenuType.register("generic_9x6", ChestMenu::sixRows);
    public static final MenuType<DispenserMenu> GENERIC_3x3 = MenuType.register("generic_3x3", DispenserMenu::new);
    public static final MenuType<CrafterMenu> CRAFTER_3x3 = MenuType.register("crafter_3x3", CrafterMenu::new);
    public static final MenuType<AnvilMenu> ANVIL = MenuType.register("anvil", AnvilMenu::new);
    public static final MenuType<BeaconMenu> BEACON = MenuType.register("beacon", BeaconMenu::new);
    public static final MenuType<BlastFurnaceMenu> BLAST_FURNACE = MenuType.register("blast_furnace", BlastFurnaceMenu::new);
    public static final MenuType<BrewingStandMenu> BREWING_STAND = MenuType.register("brewing_stand", BrewingStandMenu::new);
    public static final MenuType<CraftingMenu> CRAFTING = MenuType.register("crafting", CraftingMenu::new);
    public static final MenuType<EnchantmentMenu> ENCHANTMENT = MenuType.register("enchantment", EnchantmentMenu::new);
    public static final MenuType<FurnaceMenu> FURNACE = MenuType.register("furnace", FurnaceMenu::new);
    public static final MenuType<GrindstoneMenu> GRINDSTONE = MenuType.register("grindstone", GrindstoneMenu::new);
    public static final MenuType<HopperMenu> HOPPER = MenuType.register("hopper", HopperMenu::new);
    public static final MenuType<LecternMenu> LECTERN = MenuType.register("lectern", ($$0, $$1) -> new LecternMenu($$0));
    public static final MenuType<LoomMenu> LOOM = MenuType.register("loom", LoomMenu::new);
    public static final MenuType<MerchantMenu> MERCHANT = MenuType.register("merchant", MerchantMenu::new);
    public static final MenuType<ShulkerBoxMenu> SHULKER_BOX = MenuType.register("shulker_box", ShulkerBoxMenu::new);
    public static final MenuType<SmithingMenu> SMITHING = MenuType.register("smithing", SmithingMenu::new);
    public static final MenuType<SmokerMenu> SMOKER = MenuType.register("smoker", SmokerMenu::new);
    public static final MenuType<CartographyTableMenu> CARTOGRAPHY_TABLE = MenuType.register("cartography_table", CartographyTableMenu::new);
    public static final MenuType<StonecutterMenu> STONECUTTER = MenuType.register("stonecutter", StonecutterMenu::new);
    private final FeatureFlagSet requiredFeatures;
    private final MenuSupplier<T> constructor;

    private static <T extends AbstractContainerMenu> MenuType<T> register(String $$0, MenuSupplier<T> $$1) {
        return Registry.register(BuiltInRegistries.MENU, $$0, new MenuType<T>($$1, FeatureFlags.VANILLA_SET));
    }

    private static <T extends AbstractContainerMenu> MenuType<T> a(String $$0, MenuSupplier<T> $$1, FeatureFlag ... $$2) {
        return Registry.register(BuiltInRegistries.MENU, $$0, new MenuType<T>($$1, FeatureFlags.REGISTRY.a($$2)));
    }

    private MenuType(MenuSupplier<T> $$0, FeatureFlagSet $$1) {
        this.constructor = $$0;
        this.requiredFeatures = $$1;
    }

    public T create(int $$0, Inventory $$1) {
        return this.constructor.create($$0, $$1);
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.requiredFeatures;
    }

    static interface MenuSupplier<T extends AbstractContainerMenu> {
        public T create(int var1, Inventory var2);
    }
}

