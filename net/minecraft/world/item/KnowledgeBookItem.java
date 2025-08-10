/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.world.item;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class KnowledgeBookItem
extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();

    public KnowledgeBookItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        List $$4 = $$3.getOrDefault(DataComponents.RECIPES, List.of());
        $$3.consume(1, $$1);
        if ($$4.isEmpty()) {
            return InteractionResult.FAIL;
        }
        if (!$$0.isClientSide) {
            RecipeManager $$5 = $$0.getServer().getRecipeManager();
            ArrayList $$6 = new ArrayList($$4.size());
            for (ResourceKey $$7 : $$4) {
                Optional<RecipeHolder<?>> $$8 = $$5.byKey($$7);
                if ($$8.isPresent()) {
                    $$6.add($$8.get());
                    continue;
                }
                LOGGER.error("Invalid recipe: {}", (Object)$$7);
                return InteractionResult.FAIL;
            }
            $$1.awardRecipes($$6);
            $$1.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResult.SUCCESS;
    }
}

