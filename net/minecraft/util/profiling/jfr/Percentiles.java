/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2DoubleRBTreeMap
 *  it.unimi.dsi.fastutil.ints.Int2DoubleSortedMap
 *  it.unimi.dsi.fastutil.ints.Int2DoubleSortedMaps
 */
package net.minecraft.util.profiling.jfr;

import com.google.common.math.Quantiles;
import it.unimi.dsi.fastutil.ints.Int2DoubleRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMaps;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;

public class Percentiles {
    public static final Quantiles.ScaleAndIndexes DEFAULT_INDEXES = Quantiles.scale(100).indexes(50, 75, 90, 99);

    private Percentiles() {
    }

    public static Map<Integer, Double> a(long[] $$0) {
        return $$0.length == 0 ? Map.of() : Percentiles.sorted(DEFAULT_INDEXES.compute($$0));
    }

    public static Map<Integer, Double> a(double[] $$0) {
        return $$0.length == 0 ? Map.of() : Percentiles.sorted(DEFAULT_INDEXES.compute($$0));
    }

    private static Map<Integer, Double> sorted(Map<Integer, Double> $$0) {
        Int2DoubleSortedMap $$12 = (Int2DoubleSortedMap)Util.make(new Int2DoubleRBTreeMap(Comparator.reverseOrder()), $$1 -> $$1.putAll($$0));
        return Int2DoubleSortedMaps.unmodifiable((Int2DoubleSortedMap)$$12);
    }
}

