/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.DecoratedPotRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.FireworkStarFadeRecipe;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.item.crafting.MapCloningRecipe;
import net.minecraft.world.item.crafting.MapExtendingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.ShieldDecorationRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.TippedArrowRecipe;
import net.minecraft.world.item.crafting.TransmuteRecipe;

public interface RecipeSerializer<T extends Recipe<?>> {
    public static final RecipeSerializer<ShapedRecipe> SHAPED_RECIPE = RecipeSerializer.register("crafting_shaped", new ShapedRecipe.Serializer());
    public static final RecipeSerializer<ShapelessRecipe> SHAPELESS_RECIPE = RecipeSerializer.register("crafting_shapeless", new ShapelessRecipe.Serializer());
    public static final RecipeSerializer<ArmorDyeRecipe> ARMOR_DYE = RecipeSerializer.register("crafting_special_armordye", new CustomRecipe.Serializer<ArmorDyeRecipe>(ArmorDyeRecipe::new));
    public static final RecipeSerializer<BookCloningRecipe> BOOK_CLONING = RecipeSerializer.register("crafting_special_bookcloning", new CustomRecipe.Serializer<BookCloningRecipe>(BookCloningRecipe::new));
    public static final RecipeSerializer<MapCloningRecipe> MAP_CLONING = RecipeSerializer.register("crafting_special_mapcloning", new CustomRecipe.Serializer<MapCloningRecipe>(MapCloningRecipe::new));
    public static final RecipeSerializer<MapExtendingRecipe> MAP_EXTENDING = RecipeSerializer.register("crafting_special_mapextending", new CustomRecipe.Serializer<MapExtendingRecipe>(MapExtendingRecipe::new));
    public static final RecipeSerializer<FireworkRocketRecipe> FIREWORK_ROCKET = RecipeSerializer.register("crafting_special_firework_rocket", new CustomRecipe.Serializer<FireworkRocketRecipe>(FireworkRocketRecipe::new));
    public static final RecipeSerializer<FireworkStarRecipe> FIREWORK_STAR = RecipeSerializer.register("crafting_special_firework_star", new CustomRecipe.Serializer<FireworkStarRecipe>(FireworkStarRecipe::new));
    public static final RecipeSerializer<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = RecipeSerializer.register("crafting_special_firework_star_fade", new CustomRecipe.Serializer<FireworkStarFadeRecipe>(FireworkStarFadeRecipe::new));
    public static final RecipeSerializer<TippedArrowRecipe> TIPPED_ARROW = RecipeSerializer.register("crafting_special_tippedarrow", new CustomRecipe.Serializer<TippedArrowRecipe>(TippedArrowRecipe::new));
    public static final RecipeSerializer<BannerDuplicateRecipe> BANNER_DUPLICATE = RecipeSerializer.register("crafting_special_bannerduplicate", new CustomRecipe.Serializer<BannerDuplicateRecipe>(BannerDuplicateRecipe::new));
    public static final RecipeSerializer<ShieldDecorationRecipe> SHIELD_DECORATION = RecipeSerializer.register("crafting_special_shielddecoration", new CustomRecipe.Serializer<ShieldDecorationRecipe>(ShieldDecorationRecipe::new));
    public static final RecipeSerializer<TransmuteRecipe> TRANSMUTE = RecipeSerializer.register("crafting_transmute", new TransmuteRecipe.Serializer());
    public static final RecipeSerializer<RepairItemRecipe> REPAIR_ITEM = RecipeSerializer.register("crafting_special_repairitem", new CustomRecipe.Serializer<RepairItemRecipe>(RepairItemRecipe::new));
    public static final RecipeSerializer<SmeltingRecipe> SMELTING_RECIPE = RecipeSerializer.register("smelting", new AbstractCookingRecipe.Serializer<SmeltingRecipe>(SmeltingRecipe::new, 200));
    public static final RecipeSerializer<BlastingRecipe> BLASTING_RECIPE = RecipeSerializer.register("blasting", new AbstractCookingRecipe.Serializer<BlastingRecipe>(BlastingRecipe::new, 100));
    public static final RecipeSerializer<SmokingRecipe> SMOKING_RECIPE = RecipeSerializer.register("smoking", new AbstractCookingRecipe.Serializer<SmokingRecipe>(SmokingRecipe::new, 100));
    public static final RecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING_RECIPE = RecipeSerializer.register("campfire_cooking", new AbstractCookingRecipe.Serializer<CampfireCookingRecipe>(CampfireCookingRecipe::new, 100));
    public static final RecipeSerializer<StonecutterRecipe> STONECUTTER = RecipeSerializer.register("stonecutting", new SingleItemRecipe.Serializer<StonecutterRecipe>(StonecutterRecipe::new));
    public static final RecipeSerializer<SmithingTransformRecipe> SMITHING_TRANSFORM = RecipeSerializer.register("smithing_transform", new SmithingTransformRecipe.Serializer());
    public static final RecipeSerializer<SmithingTrimRecipe> SMITHING_TRIM = RecipeSerializer.register("smithing_trim", new SmithingTrimRecipe.Serializer());
    public static final RecipeSerializer<DecoratedPotRecipe> DECORATED_POT_RECIPE = RecipeSerializer.register("crafting_decorated_pot", new CustomRecipe.Serializer<DecoratedPotRecipe>(DecoratedPotRecipe::new));

    public MapCodec<T> codec();

    @Deprecated
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String $$0, S $$1) {
        return (S)Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, $$0, $$1);
    }
}

