/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Graph;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public class FeatureSorter {
    public static <T> List<StepFeatureData> buildFeaturesPerStep(List<T> $$0, Function<T, List<HolderSet<PlacedFeature>>> $$12, boolean $$2) {
        Object2IntOpenHashMap $$3 = new Object2IntOpenHashMap();
        MutableInt $$4 = new MutableInt(0);
        record FeatureData(int featureIndex, int step, PlacedFeature feature) {
        }
        Comparator<FeatureData> $$5 = Comparator.comparingInt(FeatureData::step).thenComparingInt(FeatureData::featureIndex);
        TreeMap<FeatureData, Set> $$6 = new TreeMap<FeatureData, Set>($$5);
        int $$7 = 0;
        for (T $$8 : $$0) {
            ArrayList<FeatureData> $$9 = Lists.newArrayList();
            List<HolderSet<PlacedFeature>> $$10 = $$12.apply($$8);
            $$7 = Math.max($$7, $$10.size());
            for (int $$11 = 0; $$11 < $$10.size(); ++$$11) {
                for (Holder $$122 : (HolderSet)$$10.get($$11)) {
                    PlacedFeature $$13 = (PlacedFeature)((Object)$$122.value());
                    $$9.add(new FeatureData($$3.computeIfAbsent((Object)$$13, $$1 -> $$4.getAndIncrement()), $$11, $$13));
                }
            }
            for (int $$14 = 0; $$14 < $$9.size(); ++$$14) {
                Set $$15 = $$6.computeIfAbsent((FeatureData)((Object)$$9.get($$14)), $$1 -> new TreeSet($$5));
                if ($$14 >= $$9.size() - 1) continue;
                $$15.add((FeatureData)((Object)$$9.get($$14 + 1)));
            }
        }
        TreeSet<FeatureData> $$16 = new TreeSet<FeatureData>($$5);
        TreeSet<FeatureData> $$17 = new TreeSet<FeatureData>($$5);
        ArrayList $$18 = Lists.newArrayList();
        for (FeatureData $$19 : $$6.keySet()) {
            if (!$$17.isEmpty()) {
                throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
            }
            if ($$16.contains((Object)$$19) || !Graph.depthFirstSearch($$6, $$16, $$17, $$18::add, $$19)) continue;
            if ($$2) {
                int $$21;
                ArrayList<T> $$20 = new ArrayList<T>($$0);
                do {
                    $$21 = $$20.size();
                    ListIterator $$22 = $$20.listIterator();
                    while ($$22.hasNext()) {
                        Object $$23 = $$22.next();
                        $$22.remove();
                        try {
                            FeatureSorter.buildFeaturesPerStep($$20, $$12, false);
                        } catch (IllegalStateException $$24) {
                            continue;
                        }
                        $$22.add($$23);
                    }
                } while ($$21 != $$20.size());
                throw new IllegalStateException("Feature order cycle found, involved sources: " + String.valueOf($$20));
            }
            throw new IllegalStateException("Feature order cycle found");
        }
        Collections.reverse($$18);
        ImmutableList.Builder $$25 = ImmutableList.builder();
        int $$26 = 0;
        while ($$26 < $$7) {
            int $$27 = $$26++;
            List<PlacedFeature> $$28 = $$18.stream().filter($$1 -> $$1.step() == $$27).map(FeatureData::feature).collect(Collectors.toList());
            $$25.add((Object)new StepFeatureData($$28));
        }
        return $$25.build();
    }

    public record StepFeatureData(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
        StepFeatureData(List<PlacedFeature> $$0) {
            this($$0, Util.createIndexIdentityLookup($$0));
        }
    }
}

