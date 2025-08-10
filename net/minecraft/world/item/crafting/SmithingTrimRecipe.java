/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.crafting;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPattern;

public class SmithingTrimRecipe
implements SmithingRecipe {
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final Holder<TrimPattern> pattern;
    @Nullable
    private PlacementInfo placementInfo;

    public SmithingTrimRecipe(Ingredient $$0, Ingredient $$1, Ingredient $$2, Holder<TrimPattern> $$3) {
        this.template = $$0;
        this.base = $$1;
        this.addition = $$2;
        this.pattern = $$3;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput $$0, HolderLookup.Provider $$1) {
        return SmithingTrimRecipe.applyTrim($$1, $$0.base(), $$0.addition(), this.pattern);
    }

    public static ItemStack applyTrim(HolderLookup.Provider $$0, ItemStack $$1, ItemStack $$2, Holder<TrimPattern> $$3) {
        Optional<Holder<TrimMaterial>> $$4 = TrimMaterials.getFromIngredient($$0, $$2);
        if ($$4.isPresent()) {
            ArmorTrim $$6;
            ArmorTrim $$5 = $$1.get(DataComponents.TRIM);
            if (Objects.equals($$5, $$6 = new ArmorTrim($$4.get(), $$3))) {
                return ItemStack.EMPTY;
            }
            ItemStack $$7 = $$1.copyWithCount(1);
            $$7.set(DataComponents.TRIM, $$6);
            return $$7;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Optional<Ingredient> templateIngredient() {
        return Optional.of(this.template);
    }

    @Override
    public Ingredient baseIngredient() {
        return this.base;
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return Optional.of(this.addition);
    }

    @Override
    public RecipeSerializer<SmithingTrimRecipe> getSerializer() {
        return RecipeSerializer.SMITHING_TRIM;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(List.of((Object)this.template, (Object)this.base, (Object)this.addition));
        }
        return this.placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        SlotDisplay $$0 = this.base.display();
        SlotDisplay $$1 = this.addition.display();
        SlotDisplay $$2 = this.template.display();
        return List.of((Object)new SmithingRecipeDisplay($$2, $$0, $$1, new SlotDisplay.SmithingTrimDemoSlotDisplay($$0, $$1, this.pattern), new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)));
    }

    public static class Serializer
    implements RecipeSerializer<SmithingTrimRecipe> {
        private static final MapCodec<SmithingTrimRecipe> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Ingredient.CODEC.fieldOf("template").forGetter($$0 -> $$0.template), (App)Ingredient.CODEC.fieldOf("base").forGetter($$0 -> $$0.base), (App)Ingredient.CODEC.fieldOf("addition").forGetter($$0 -> $$0.addition), (App)TrimPattern.CODEC.fieldOf("pattern").forGetter($$0 -> $$0.pattern)).apply((Applicative)$$02, SmithingTrimRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> STREAM_CODEC = StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, $$0 -> $$0.template, Ingredient.CONTENTS_STREAM_CODEC, $$0 -> $$0.base, Ingredient.CONTENTS_STREAM_CODEC, $$0 -> $$0.addition, TrimPattern.STREAM_CODEC, $$0 -> $$0.pattern, SmithingTrimRecipe::new);

        @Override
        public MapCodec<SmithingTrimRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

