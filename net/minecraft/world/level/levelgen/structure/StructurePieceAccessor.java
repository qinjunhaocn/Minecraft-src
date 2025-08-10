/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.structure;

import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

public interface StructurePieceAccessor {
    public void addPiece(StructurePiece var1);

    @Nullable
    public StructurePiece findCollisionPiece(BoundingBox var1);
}

