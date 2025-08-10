/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.multiplayer;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.client.searchtree.IdSearchTree;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;

public class SessionSearchTrees {
    private static final Key RECIPE_COLLECTIONS = new Key();
    private static final Key CREATIVE_NAMES = new Key();
    private static final Key CREATIVE_TAGS = new Key();
    private CompletableFuture<SearchTree<ItemStack>> creativeByNameSearch = CompletableFuture.completedFuture(SearchTree.empty());
    private CompletableFuture<SearchTree<ItemStack>> creativeByTagSearch = CompletableFuture.completedFuture(SearchTree.empty());
    private CompletableFuture<SearchTree<RecipeCollection>> recipeSearch = CompletableFuture.completedFuture(SearchTree.empty());
    private final Map<Key, Runnable> reloaders = new IdentityHashMap<Key, Runnable>();

    private void register(Key $$0, Runnable $$1) {
        $$1.run();
        this.reloaders.put($$0, $$1);
    }

    public void rebuildAfterLanguageChange() {
        for (Runnable $$0 : this.reloaders.values()) {
            $$0.run();
        }
    }

    private static Stream<String> getTooltipLines(Stream<ItemStack> $$02, Item.TooltipContext $$1, TooltipFlag $$22) {
        return $$02.flatMap($$2 -> $$2.getTooltipLines($$1, null, $$22).stream()).map($$0 -> ChatFormatting.stripFormatting($$0.getString()).trim()).filter($$0 -> !$$0.isEmpty());
    }

    public void updateRecipes(ClientRecipeBook $$0, Level $$1) {
        this.register(RECIPE_COLLECTIONS, () -> {
            List<RecipeCollection> $$2 = $$0.getCollections();
            RegistryAccess $$3 = $$1.registryAccess();
            HolderLookup.RegistryLookup $$4 = $$3.lookupOrThrow(Registries.ITEM);
            Item.TooltipContext $$5 = Item.TooltipContext.of($$3);
            ContextMap $$6 = SlotDisplayContext.fromLevel($$1);
            TooltipFlag.Default $$7 = TooltipFlag.Default.NORMAL;
            CompletableFuture<SearchTree<RecipeCollection>> $$8 = this.recipeSearch;
            this.recipeSearch = CompletableFuture.supplyAsync(() -> SessionSearchTrees.lambda$updateRecipes$8($$6, $$5, $$7, (Registry)$$4, $$2), Util.backgroundExecutor());
            $$8.cancel(true);
        });
    }

    public SearchTree<RecipeCollection> recipes() {
        return this.recipeSearch.join();
    }

    public void updateCreativeTags(List<ItemStack> $$0) {
        this.register(CREATIVE_TAGS, () -> {
            CompletableFuture<SearchTree<ItemStack>> $$1 = this.creativeByTagSearch;
            this.creativeByTagSearch = CompletableFuture.supplyAsync(() -> new IdSearchTree<ItemStack>($$0 -> $$0.getTags().map(TagKey::location), $$0), Util.backgroundExecutor());
            $$1.cancel(true);
        });
    }

    public SearchTree<ItemStack> creativeTagSearch() {
        return this.creativeByTagSearch.join();
    }

    public void updateCreativeTooltips(HolderLookup.Provider $$0, List<ItemStack> $$1) {
        this.register(CREATIVE_NAMES, () -> {
            Item.TooltipContext $$2 = Item.TooltipContext.of($$0);
            TooltipFlag.Default $$3 = TooltipFlag.Default.NORMAL.asCreative();
            CompletableFuture<SearchTree<ItemStack>> $$4 = this.creativeByNameSearch;
            this.creativeByNameSearch = CompletableFuture.supplyAsync(() -> new FullTextSearchTree<ItemStack>($$2 -> SessionSearchTrees.getTooltipLines(Stream.of($$2), $$2, $$3), $$0 -> $$0.getItemHolder().unwrapKey().map(ResourceKey::location).stream(), $$1), Util.backgroundExecutor());
            $$4.cancel(true);
        });
    }

    public SearchTree<ItemStack> creativeNameSearch() {
        return this.creativeByNameSearch.join();
    }

    private static /* synthetic */ SearchTree lambda$updateRecipes$8(ContextMap $$0, Item.TooltipContext $$1, TooltipFlag $$22, Registry $$32, List $$4) {
        return new FullTextSearchTree<RecipeCollection>($$3 -> SessionSearchTrees.getTooltipLines($$3.getRecipes().stream().flatMap($$1 -> $$1.resultItems($$0).stream()), $$1, $$22), $$2 -> $$2.getRecipes().stream().flatMap($$1 -> $$1.resultItems($$0).stream()).map($$1 -> $$32.getKey($$1.getItem())), $$4);
    }

    static class Key {
        Key() {
        }
    }
}

