/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.screens.recipebook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public class RecipeCollection {
    public static final RecipeCollection EMPTY = new RecipeCollection(List.of());
    private final List<RecipeDisplayEntry> entries;
    private final Set<RecipeDisplayId> craftable = new HashSet<RecipeDisplayId>();
    private final Set<RecipeDisplayId> selected = new HashSet<RecipeDisplayId>();

    public RecipeCollection(List<RecipeDisplayEntry> $$0) {
        this.entries = $$0;
    }

    public void selectRecipes(StackedItemContents $$0, Predicate<RecipeDisplay> $$1) {
        for (RecipeDisplayEntry $$2 : this.entries) {
            boolean $$3 = $$1.test($$2.display());
            if ($$3) {
                this.selected.add($$2.id());
            } else {
                this.selected.remove((Object)$$2.id());
            }
            if ($$3 && $$2.canCraft($$0)) {
                this.craftable.add($$2.id());
                continue;
            }
            this.craftable.remove((Object)$$2.id());
        }
    }

    public boolean isCraftable(RecipeDisplayId $$0) {
        return this.craftable.contains((Object)$$0);
    }

    public boolean hasCraftable() {
        return !this.craftable.isEmpty();
    }

    public boolean hasAnySelected() {
        return !this.selected.isEmpty();
    }

    public List<RecipeDisplayEntry> getRecipes() {
        return this.entries;
    }

    public List<RecipeDisplayEntry> getSelectedRecipes(CraftableStatus $$02) {
        Predicate<RecipeDisplayId> $$1 = switch ($$02.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.selected::contains;
            case 1 -> this.craftable::contains;
            case 2 -> $$0 -> this.selected.contains($$0) && !this.craftable.contains($$0);
        };
        ArrayList<RecipeDisplayEntry> $$2 = new ArrayList<RecipeDisplayEntry>();
        for (RecipeDisplayEntry $$3 : this.entries) {
            if (!$$1.test($$3.id())) continue;
            $$2.add($$3);
        }
        return $$2;
    }

    public static final class CraftableStatus
    extends Enum<CraftableStatus> {
        public static final /* enum */ CraftableStatus ANY = new CraftableStatus();
        public static final /* enum */ CraftableStatus CRAFTABLE = new CraftableStatus();
        public static final /* enum */ CraftableStatus NOT_CRAFTABLE = new CraftableStatus();
        private static final /* synthetic */ CraftableStatus[] $VALUES;

        public static CraftableStatus[] values() {
            return (CraftableStatus[])$VALUES.clone();
        }

        public static CraftableStatus valueOf(String $$0) {
            return Enum.valueOf(CraftableStatus.class, $$0);
        }

        private static /* synthetic */ CraftableStatus[] a() {
            return new CraftableStatus[]{ANY, CRAFTABLE, NOT_CRAFTABLE};
        }

        static {
            $VALUES = CraftableStatus.a();
        }
    }
}

