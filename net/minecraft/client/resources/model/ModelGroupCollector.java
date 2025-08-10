/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.client.resources.model;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ModelGroupCollector {
    static final int SINGLETON_MODEL_GROUP = -1;
    private static final int INVISIBLE_MODEL_GROUP = 0;

    public static Object2IntMap<BlockState> build(BlockColors $$0, BlockStateModelLoader.LoadedModels $$1) {
        HashMap $$2 = new HashMap();
        HashMap $$32 = new HashMap();
        $$1.models().forEach(($$3, $$4) -> {
            List $$5 = $$2.computeIfAbsent($$3.getBlock(), $$1 -> List.copyOf($$0.getColoringProperties((Block)$$1)));
            GroupKey $$6 = GroupKey.create($$3, $$4, $$5);
            $$32.computeIfAbsent($$6, $$0 -> Sets.newIdentityHashSet()).add($$3);
        });
        int $$42 = 1;
        Object2IntOpenHashMap $$5 = new Object2IntOpenHashMap();
        $$5.defaultReturnValue(-1);
        for (Set $$6 : $$32.values()) {
            Iterator $$7 = $$6.iterator();
            while ($$7.hasNext()) {
                BlockState $$8 = (BlockState)$$7.next();
                if ($$8.getRenderShape() == RenderShape.MODEL) continue;
                $$7.remove();
                $$5.put((Object)$$8, 0);
            }
            if ($$6.size() <= 1) continue;
            int $$9 = $$42++;
            $$6.forEach(arg_0 -> ModelGroupCollector.lambda$build$3((Object2IntMap)$$5, $$9, arg_0));
        }
        return $$5;
    }

    private static /* synthetic */ void lambda$build$3(Object2IntMap $$0, int $$1, BlockState $$2) {
        $$0.put((Object)$$2, $$1);
    }

    record GroupKey(Object equalityGroup, List<Object> coloringValues) {
        public static GroupKey create(BlockState $$0, BlockStateModel.UnbakedRoot $$1, List<Property<?>> $$2) {
            List<Object> $$3 = GroupKey.getColoringValues($$0, $$2);
            Object $$4 = $$1.visualEqualityGroup($$0);
            return new GroupKey($$4, $$3);
        }

        private static List<Object> getColoringValues(BlockState $$0, List<Property<?>> $$1) {
            Object[] $$2 = new Object[$$1.size()];
            for (int $$3 = 0; $$3 < $$1.size(); ++$$3) {
                $$2[$$3] = $$0.getValue($$1.get($$3));
            }
            return List.of((Object[])$$2);
        }
    }
}

