/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.world.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.item.crafting.Ingredient;

public class PlacementInfo {
    public static final int EMPTY_SLOT = -1;
    public static final PlacementInfo NOT_PLACEABLE = new PlacementInfo(List.of(), IntList.of());
    private final List<Ingredient> ingredients;
    private final IntList slotsToIngredientIndex;

    private PlacementInfo(List<Ingredient> $$0, IntList $$1) {
        this.ingredients = $$0;
        this.slotsToIngredientIndex = $$1;
    }

    public static PlacementInfo create(Ingredient $$0) {
        if ($$0.isEmpty()) {
            return NOT_PLACEABLE;
        }
        return new PlacementInfo(List.of((Object)$$0), IntList.of((int)0));
    }

    public static PlacementInfo createFromOptionals(List<Optional<Ingredient>> $$0) {
        int $$1 = $$0.size();
        ArrayList<Ingredient> $$2 = new ArrayList<Ingredient>($$1);
        IntArrayList $$3 = new IntArrayList($$1);
        int $$4 = 0;
        for (Optional<Ingredient> $$5 : $$0) {
            if ($$5.isPresent()) {
                Ingredient $$6 = $$5.get();
                if ($$6.isEmpty()) {
                    return NOT_PLACEABLE;
                }
                $$2.add($$6);
                $$3.add($$4++);
                continue;
            }
            $$3.add(-1);
        }
        return new PlacementInfo($$2, (IntList)$$3);
    }

    public static PlacementInfo create(List<Ingredient> $$0) {
        int $$1 = $$0.size();
        IntArrayList $$2 = new IntArrayList($$1);
        for (int $$3 = 0; $$3 < $$1; ++$$3) {
            Ingredient $$4 = $$0.get($$3);
            if ($$4.isEmpty()) {
                return NOT_PLACEABLE;
            }
            $$2.add($$3);
        }
        return new PlacementInfo($$0, (IntList)$$2);
    }

    public IntList slotsToIngredientIndex() {
        return this.slotsToIngredientIndex;
    }

    public List<Ingredient> ingredients() {
        return this.ingredients;
    }

    public boolean isImpossibleToPlace() {
        return this.slotsToIngredientIndex.isEmpty();
    }
}

