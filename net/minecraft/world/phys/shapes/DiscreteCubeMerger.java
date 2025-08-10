/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.world.phys.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.IndexMerger;
import net.minecraft.world.phys.shapes.Shapes;

public final class DiscreteCubeMerger
implements IndexMerger {
    private final CubePointRange result;
    private final int firstDiv;
    private final int secondDiv;

    DiscreteCubeMerger(int $$0, int $$1) {
        this.result = new CubePointRange((int)Shapes.lcm($$0, $$1));
        int $$2 = IntMath.gcd($$0, $$1);
        this.firstDiv = $$0 / $$2;
        this.secondDiv = $$1 / $$2;
    }

    @Override
    public boolean forMergedIndexes(IndexMerger.IndexConsumer $$0) {
        int $$1 = this.result.size() - 1;
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            if ($$0.merge($$2 / this.secondDiv, $$2 / this.firstDiv, $$2)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int size() {
        return this.result.size();
    }

    @Override
    public DoubleList getList() {
        return this.result;
    }
}

