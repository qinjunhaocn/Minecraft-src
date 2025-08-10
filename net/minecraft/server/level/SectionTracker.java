/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.level;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;

public abstract class SectionTracker
extends DynamicGraphMinFixedPoint {
    protected SectionTracker(int $$0, int $$1, int $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected void checkNeighborsAfterUpdate(long $$0, int $$1, boolean $$2) {
        if ($$2 && $$1 >= this.levelCount - 2) {
            return;
        }
        for (int $$3 = -1; $$3 <= 1; ++$$3) {
            for (int $$4 = -1; $$4 <= 1; ++$$4) {
                for (int $$5 = -1; $$5 <= 1; ++$$5) {
                    long $$6 = SectionPos.offset($$0, $$3, $$4, $$5);
                    if ($$6 == $$0) continue;
                    this.checkNeighbor($$0, $$6, $$1, $$2);
                }
            }
        }
    }

    @Override
    protected int getComputedLevel(long $$0, long $$1, int $$2) {
        int $$3 = $$2;
        for (int $$4 = -1; $$4 <= 1; ++$$4) {
            for (int $$5 = -1; $$5 <= 1; ++$$5) {
                for (int $$6 = -1; $$6 <= 1; ++$$6) {
                    long $$7 = SectionPos.offset($$0, $$4, $$5, $$6);
                    if ($$7 == $$0) {
                        $$7 = Long.MAX_VALUE;
                    }
                    if ($$7 == $$1) continue;
                    int $$8 = this.computeLevelFromNeighbor($$7, $$0, this.getLevel($$7));
                    if ($$3 > $$8) {
                        $$3 = $$8;
                    }
                    if ($$3 != 0) continue;
                    return $$3;
                }
            }
        }
        return $$3;
    }

    @Override
    protected int computeLevelFromNeighbor(long $$0, long $$1, int $$2) {
        if (this.isSource($$0)) {
            return this.getLevelFromSource($$1);
        }
        return $$2 + 1;
    }

    protected abstract int getLevelFromSource(long var1);

    public void update(long $$0, int $$1, boolean $$2) {
        this.checkEdge(Long.MAX_VALUE, $$0, $$1, $$2);
    }
}

