/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.levelgen.material;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;

public final class MaterialRuleList
extends Record
implements NoiseChunk.BlockStateFiller {
    private final NoiseChunk.BlockStateFiller[] materialRuleList;

    public MaterialRuleList(NoiseChunk.BlockStateFiller[] $$0) {
        this.materialRuleList = $$0;
    }

    @Override
    @Nullable
    public BlockState calculate(DensityFunction.FunctionContext $$0) {
        for (NoiseChunk.BlockStateFiller $$1 : this.materialRuleList) {
            BlockState $$2 = $$1.calculate($$0);
            if ($$2 == null) continue;
            return $$2;
        }
        return null;
    }

    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MaterialRuleList.class, "materialRuleList", "materialRuleList"}, this);
    }

    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MaterialRuleList.class, "materialRuleList", "materialRuleList"}, this);
    }

    public final boolean equals(Object $$0) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MaterialRuleList.class, "materialRuleList", "materialRuleList"}, this, $$0);
    }

    public NoiseChunk.BlockStateFiller[] a() {
        return this.materialRuleList;
    }
}

