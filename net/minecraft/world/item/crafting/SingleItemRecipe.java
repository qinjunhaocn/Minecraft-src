/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.crafting;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public abstract class SingleItemRecipe
implements Recipe<SingleRecipeInput> {
    private final Ingredient input;
    private final ItemStack result;
    private final String group;
    @Nullable
    private PlacementInfo placementInfo;

    public SingleItemRecipe(String $$0, Ingredient $$1, ItemStack $$2) {
        this.group = $$0;
        this.input = $$1;
        this.result = $$2;
    }

    @Override
    public abstract RecipeSerializer<? extends SingleItemRecipe> getSerializer();

    @Override
    public abstract RecipeType<? extends SingleItemRecipe> getType();

    @Override
    public boolean matches(SingleRecipeInput $$0, Level $$1) {
        return this.input.test($$0.item());
    }

    @Override
    public String group() {
        return this.group;
    }

    public Ingredient input() {
        return this.input;
    }

    protected ItemStack result() {
        return this.result;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.input);
        }
        return this.placementInfo;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput $$0, HolderLookup.Provider $$1) {
        return this.result.copy();
    }

    @FunctionalInterface
    public static interface Factory<T extends SingleItemRecipe> {
        public T create(String var1, Ingredient var2, ItemStack var3);
    }

    public static class Serializer<T extends SingleItemRecipe>
    implements RecipeSerializer<T> {
        private final MapCodec<T> codec = RecordCodecBuilder.mapCodec($$1 -> $$1.group((App)Codec.STRING.optionalFieldOf("group", (Object)"").forGetter(SingleItemRecipe::group), (App)Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleItemRecipe::input), (App)ItemStack.STRICT_CODEC.fieldOf("result").forGetter(SingleItemRecipe::result)).apply((Applicative)$$1, $$0::create));
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, SingleItemRecipe::group, Ingredient.CONTENTS_STREAM_CODEC, SingleItemRecipe::input, ItemStack.STREAM_CODEC, SingleItemRecipe::result, $$0::create);

        protected Serializer(Factory<T> $$0) {
        }

        @Override
        public MapCodec<T> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return this.streamCodec;
        }
    }
}

