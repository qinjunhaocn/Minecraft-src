/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.structures.JungleTemplePiece;

public class JungleTempleStructure
extends SinglePieceStructure {
    public static final MapCodec<JungleTempleStructure> CODEC = JungleTempleStructure.simpleCodec(JungleTempleStructure::new);

    public JungleTempleStructure(Structure.StructureSettings $$0) {
        super(JungleTemplePiece::new, 12, 15, $$0);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.JUNGLE_TEMPLE;
    }
}

