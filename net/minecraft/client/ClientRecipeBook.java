/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public class ClientRecipeBook
extends RecipeBook {
    private final Map<RecipeDisplayId, RecipeDisplayEntry> known = new HashMap<RecipeDisplayId, RecipeDisplayEntry>();
    private final Set<RecipeDisplayId> highlight = new HashSet<RecipeDisplayId>();
    private Map<ExtendedRecipeBookCategory, List<RecipeCollection>> collectionsByTab = Map.of();
    private List<RecipeCollection> allCollections = List.of();

    public void add(RecipeDisplayEntry $$0) {
        this.known.put($$0.id(), $$0);
    }

    public void remove(RecipeDisplayId $$0) {
        this.known.remove((Object)$$0);
        this.highlight.remove((Object)$$0);
    }

    public void clear() {
        this.known.clear();
        this.highlight.clear();
    }

    public boolean willHighlight(RecipeDisplayId $$0) {
        return this.highlight.contains((Object)$$0);
    }

    public void removeHighlight(RecipeDisplayId $$0) {
        this.highlight.remove((Object)$$0);
    }

    public void addHighlight(RecipeDisplayId $$0) {
        this.highlight.add($$0);
    }

    public void rebuildCollections() {
        Map<RecipeBookCategory, List<List<RecipeDisplayEntry>>> $$0 = ClientRecipeBook.categorizeAndGroupRecipes(this.known.values());
        HashMap<SearchRecipeBookCategory, List> $$12 = new HashMap<SearchRecipeBookCategory, List>();
        ImmutableList.Builder $$22 = ImmutableList.builder();
        $$0.forEach(($$2, $$3) -> $$12.put((SearchRecipeBookCategory)$$2, $$3.stream().map(RecipeCollection::new).peek($$22::add).collect(ImmutableList.toImmutableList())));
        for (SearchRecipeBookCategory $$32 : SearchRecipeBookCategory.values()) {
            $$12.put($$32, $$32.includedCategories().stream().flatMap($$1 -> $$12.getOrDefault($$1, List.of()).stream()).collect(ImmutableList.toImmutableList()));
        }
        this.collectionsByTab = Map.copyOf($$12);
        this.allCollections = $$22.build();
    }

    private static Map<RecipeBookCategory, List<List<RecipeDisplayEntry>>> categorizeAndGroupRecipes(Iterable<RecipeDisplayEntry> $$02) {
        HashMap<RecipeBookCategory, List<List<RecipeDisplayEntry>>> $$1 = new HashMap<RecipeBookCategory, List<List<RecipeDisplayEntry>>>();
        HashBasedTable $$2 = HashBasedTable.create();
        for (RecipeDisplayEntry $$3 : $$02) {
            RecipeBookCategory $$4 = $$3.category();
            OptionalInt $$5 = $$3.group();
            if ($$5.isEmpty()) {
                $$1.computeIfAbsent($$4, $$0 -> new ArrayList()).add(List.of((Object)((Object)$$3)));
                continue;
            }
            ArrayList<RecipeDisplayEntry> $$6 = (ArrayList<RecipeDisplayEntry>)$$2.get($$4, $$5.getAsInt());
            if ($$6 == null) {
                $$6 = new ArrayList<RecipeDisplayEntry>();
                $$2.put($$4, $$5.getAsInt(), $$6);
                $$1.computeIfAbsent($$4, $$0 -> new ArrayList()).add($$6);
            }
            $$6.add($$3);
        }
        return $$1;
    }

    public List<RecipeCollection> getCollections() {
        return this.allCollections;
    }

    public List<RecipeCollection> getCollection(ExtendedRecipeBookCategory $$0) {
        return this.collectionsByTab.getOrDefault($$0, Collections.emptyList());
    }
}

