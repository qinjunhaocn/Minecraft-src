/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class RecipeManager
extends SimplePreparableReloadListener<RecipeMap>
implements RecipeAccess {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ResourceKey<RecipePropertySet>, IngredientExtractor> RECIPE_PROPERTY_SETS = Map.of(RecipePropertySet.SMITHING_ADDITION, $$0 -> {
        Optional<Object> optional;
        if ($$0 instanceof SmithingRecipe) {
            SmithingRecipe $$1 = (SmithingRecipe)$$0;
            optional = $$1.additionIngredient();
        } else {
            optional = Optional.empty();
        }
        return optional;
    }, RecipePropertySet.SMITHING_BASE, $$0 -> {
        Optional<Object> optional;
        if ($$0 instanceof SmithingRecipe) {
            SmithingRecipe $$1 = (SmithingRecipe)$$0;
            optional = Optional.of($$1.baseIngredient());
        } else {
            optional = Optional.empty();
        }
        return optional;
    }, RecipePropertySet.SMITHING_TEMPLATE, $$0 -> {
        Optional<Object> optional;
        if ($$0 instanceof SmithingRecipe) {
            SmithingRecipe $$1 = (SmithingRecipe)$$0;
            optional = $$1.templateIngredient();
        } else {
            optional = Optional.empty();
        }
        return optional;
    }, RecipePropertySet.FURNACE_INPUT, (Object)RecipeManager.forSingleInput(RecipeType.SMELTING), RecipePropertySet.BLAST_FURNACE_INPUT, (Object)RecipeManager.forSingleInput(RecipeType.BLASTING), RecipePropertySet.SMOKER_INPUT, (Object)RecipeManager.forSingleInput(RecipeType.SMOKING), RecipePropertySet.CAMPFIRE_INPUT, (Object)RecipeManager.forSingleInput(RecipeType.CAMPFIRE_COOKING));
    private static final FileToIdConverter RECIPE_LISTER = FileToIdConverter.registry(Registries.RECIPE);
    private final HolderLookup.Provider registries;
    private RecipeMap recipes = RecipeMap.EMPTY;
    private Map<ResourceKey<RecipePropertySet>, RecipePropertySet> propertySets = Map.of();
    private SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes = SelectableRecipe.SingleInputSet.empty();
    private List<ServerDisplayInfo> allDisplays = List.of();
    private Map<ResourceKey<Recipe<?>>, List<ServerDisplayInfo>> recipeToDisplay = Map.of();

    public RecipeManager(HolderLookup.Provider $$0) {
        this.registries = $$0;
    }

    @Override
    protected RecipeMap prepare(ResourceManager $$0, ProfilerFiller $$12) {
        TreeMap<ResourceLocation, Recipe> $$22 = new TreeMap<ResourceLocation, Recipe>();
        SimpleJsonResourceReloadListener.scanDirectory($$0, RECIPE_LISTER, this.registries.createSerializationContext(JsonOps.INSTANCE), Recipe.CODEC, $$22);
        ArrayList $$3 = new ArrayList($$22.size());
        $$22.forEach(($$1, $$2) -> {
            ResourceKey<Recipe<?>> $$3 = ResourceKey.create(Registries.RECIPE, $$1);
            RecipeHolder<Recipe> $$4 = new RecipeHolder<Recipe>($$3, (Recipe)$$2);
            $$3.add($$4);
        });
        return RecipeMap.create($$3);
    }

    @Override
    protected void apply(RecipeMap $$0, ResourceManager $$1, ProfilerFiller $$2) {
        this.recipes = $$0;
        LOGGER.info("Loaded {} recipes", (Object)$$0.values().size());
    }

    public void finalizeRecipeLoading(FeatureFlagSet $$02) {
        ArrayList $$12 = new ArrayList();
        List $$2 = RECIPE_PROPERTY_SETS.entrySet().stream().map($$0 -> new IngredientCollector((ResourceKey)$$0.getKey(), (IngredientExtractor)$$0.getValue())).toList();
        this.recipes.values().forEach($$3 -> {
            Object $$4 = $$3.value();
            if (!$$4.isSpecial() && $$4.placementInfo().isImpossibleToPlace()) {
                LOGGER.warn("Recipe {} can't be placed due to empty ingredients and will be ignored", (Object)$$3.id().location());
                return;
            }
            $$2.forEach($$1 -> $$1.accept((Recipe<?>)$$4));
            if ($$4 instanceof StonecutterRecipe) {
                StonecutterRecipe $$5 = (StonecutterRecipe)$$4;
                RecipeHolder $$6 = $$3;
                if (RecipeManager.isIngredientEnabled($$02, $$5.input()) && $$5.resultDisplay().isEnabled($$02)) {
                    $$12.add(new SelectableRecipe.SingleInputEntry($$5.input(), new SelectableRecipe($$5.resultDisplay(), Optional.of($$6))));
                }
            }
        });
        this.propertySets = (Map)$$2.stream().collect(Collectors.toUnmodifiableMap($$0 -> $$0.key, $$1 -> $$1.asPropertySet($$02)));
        this.stonecutterRecipes = new SelectableRecipe.SingleInputSet($$12);
        this.allDisplays = RecipeManager.unpackRecipeInfo(this.recipes.values(), $$02);
        this.recipeToDisplay = this.allDisplays.stream().collect(Collectors.groupingBy($$0 -> $$0.parent.id(), IdentityHashMap::new, Collectors.toList()));
    }

    static List<Ingredient> filterDisabled(FeatureFlagSet $$0, List<Ingredient> $$12) {
        $$12.removeIf($$1 -> !RecipeManager.isIngredientEnabled($$0, $$1));
        return $$12;
    }

    private static boolean isIngredientEnabled(FeatureFlagSet $$0, Ingredient $$12) {
        return $$12.items().allMatch($$1 -> ((Item)$$1.value()).isEnabled($$0));
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> $$0, I $$1, Level $$2, @Nullable ResourceKey<Recipe<?>> $$3) {
        RecipeHolder<T> $$4 = $$3 != null ? this.byKeyTyped($$0, $$3) : null;
        return this.getRecipeFor($$0, $$1, $$2, $$4);
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> $$0, I $$1, Level $$2, @Nullable RecipeHolder<T> $$3) {
        if ($$3 != null && $$3.value().matches($$1, $$2)) {
            return Optional.of($$3);
        }
        return this.getRecipeFor($$0, $$1, $$2);
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> $$0, I $$1, Level $$2) {
        return this.recipes.getRecipesFor($$0, $$1, $$2).findFirst();
    }

    public Optional<RecipeHolder<?>> byKey(ResourceKey<Recipe<?>> $$0) {
        return Optional.ofNullable(this.recipes.byKey($$0));
    }

    @Nullable
    private <T extends Recipe<?>> RecipeHolder<T> byKeyTyped(RecipeType<T> $$0, ResourceKey<Recipe<?>> $$1) {
        RecipeHolder<?> $$2 = this.recipes.byKey($$1);
        if ($$2 != null && $$2.value().getType().equals($$0)) {
            return $$2;
        }
        return null;
    }

    public Map<ResourceKey<RecipePropertySet>, RecipePropertySet> getSynchronizedItemProperties() {
        return this.propertySets;
    }

    public SelectableRecipe.SingleInputSet<StonecutterRecipe> getSynchronizedStonecutterRecipes() {
        return this.stonecutterRecipes;
    }

    @Override
    public RecipePropertySet propertySet(ResourceKey<RecipePropertySet> $$0) {
        return this.propertySets.getOrDefault($$0, RecipePropertySet.EMPTY);
    }

    @Override
    public SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes() {
        return this.stonecutterRecipes;
    }

    public Collection<RecipeHolder<?>> getRecipes() {
        return this.recipes.values();
    }

    @Nullable
    public ServerDisplayInfo getRecipeFromDisplay(RecipeDisplayId $$0) {
        int $$1 = $$0.index();
        return $$1 >= 0 && $$1 < this.allDisplays.size() ? this.allDisplays.get($$1) : null;
    }

    public void listDisplaysForRecipe(ResourceKey<Recipe<?>> $$0, Consumer<RecipeDisplayEntry> $$12) {
        List<ServerDisplayInfo> $$2 = this.recipeToDisplay.get($$0);
        if ($$2 != null) {
            $$2.forEach($$1 -> $$12.accept($$1.display));
        }
    }

    @VisibleForTesting
    protected static RecipeHolder<?> fromJson(ResourceKey<Recipe<?>> $$0, JsonObject $$1, HolderLookup.Provider $$2) {
        Recipe $$3 = (Recipe)Recipe.CODEC.parse($$2.createSerializationContext(JsonOps.INSTANCE), (Object)$$1).getOrThrow(JsonParseException::new);
        return new RecipeHolder<Recipe>($$0, $$3);
    }

    public static <I extends RecipeInput, T extends Recipe<I>> CachedCheck<I, T> createCheck(final RecipeType<T> $$0) {
        return new CachedCheck<I, T>(){
            @Nullable
            private ResourceKey<Recipe<?>> lastRecipe;

            @Override
            public Optional<RecipeHolder<T>> getRecipeFor(I $$02, ServerLevel $$1) {
                RecipeManager $$2 = $$1.recipeAccess();
                Optional $$3 = $$2.getRecipeFor($$0, $$02, (Level)$$1, this.lastRecipe);
                if ($$3.isPresent()) {
                    RecipeHolder $$4 = $$3.get();
                    this.lastRecipe = $$4.id();
                    return Optional.of($$4);
                }
                return Optional.empty();
            }
        };
    }

    private static List<ServerDisplayInfo> unpackRecipeInfo(Iterable<RecipeHolder<?>> $$0, FeatureFlagSet $$1) {
        ArrayList<ServerDisplayInfo> $$2 = new ArrayList<ServerDisplayInfo>();
        Object2IntOpenHashMap $$3 = new Object2IntOpenHashMap();
        for (RecipeHolder<?> $$4 : $$0) {
            Optional<List<Ingredient>> $$9;
            OptionalInt $$7;
            Object $$5 = $$4.value();
            if ($$5.group().isEmpty()) {
                OptionalInt $$6 = OptionalInt.empty();
            } else {
                $$7 = OptionalInt.of($$3.computeIfAbsent((Object)$$5.group(), arg_0 -> RecipeManager.lambda$unpackRecipeInfo$13((Object2IntMap)$$3, arg_0)));
            }
            if ($$5.isSpecial()) {
                Optional $$8 = Optional.empty();
            } else {
                $$9 = Optional.of($$5.placementInfo().ingredients());
            }
            for (RecipeDisplay $$10 : $$5.display()) {
                if (!$$10.isEnabled($$1)) continue;
                int $$11 = $$2.size();
                RecipeDisplayId $$12 = new RecipeDisplayId($$11);
                RecipeDisplayEntry $$13 = new RecipeDisplayEntry($$12, $$10, $$7, $$5.recipeBookCategory(), $$9);
                $$2.add(new ServerDisplayInfo($$13, $$4));
            }
        }
        return $$2;
    }

    private static IngredientExtractor forSingleInput(RecipeType<? extends SingleItemRecipe> $$0) {
        return $$1 -> {
            Optional<Object> optional;
            if ($$1.getType() == $$0 && $$1 instanceof SingleItemRecipe) {
                SingleItemRecipe $$2 = (SingleItemRecipe)$$1;
                optional = Optional.of($$2.input());
            } else {
                optional = Optional.empty();
            }
            return optional;
        };
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }

    private static /* synthetic */ int lambda$unpackRecipeInfo$13(Object2IntMap $$0, Object $$1) {
        return $$0.size();
    }

    public static final class ServerDisplayInfo
    extends Record {
        final RecipeDisplayEntry display;
        final RecipeHolder<?> parent;

        public ServerDisplayInfo(RecipeDisplayEntry $$0, RecipeHolder<?> $$1) {
            this.display = $$0;
            this.parent = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ServerDisplayInfo.class, "display;parent", "display", "parent"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ServerDisplayInfo.class, "display;parent", "display", "parent"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ServerDisplayInfo.class, "display;parent", "display", "parent"}, this, $$0);
        }

        public RecipeDisplayEntry display() {
            return this.display;
        }

        public RecipeHolder<?> parent() {
            return this.parent;
        }
    }

    @FunctionalInterface
    public static interface IngredientExtractor {
        public Optional<Ingredient> apply(Recipe<?> var1);
    }

    public static class IngredientCollector
    implements Consumer<Recipe<?>> {
        final ResourceKey<RecipePropertySet> key;
        private final IngredientExtractor extractor;
        private final List<Ingredient> ingredients = new ArrayList<Ingredient>();

        protected IngredientCollector(ResourceKey<RecipePropertySet> $$0, IngredientExtractor $$1) {
            this.key = $$0;
            this.extractor = $$1;
        }

        @Override
        public void accept(Recipe<?> $$0) {
            this.extractor.apply($$0).ifPresent(this.ingredients::add);
        }

        public RecipePropertySet asPropertySet(FeatureFlagSet $$0) {
            return RecipePropertySet.create(RecipeManager.filterDisabled($$0, this.ingredients));
        }

        @Override
        public /* synthetic */ void accept(Object object) {
            this.accept((Recipe)object);
        }
    }

    public static interface CachedCheck<I extends RecipeInput, T extends Recipe<I>> {
        public Optional<RecipeHolder<T>> getRecipeFor(I var1, ServerLevel var2);
    }
}

