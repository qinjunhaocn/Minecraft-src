/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;

public class JigsawReplacementProcessor
extends StructureProcessor {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<JigsawReplacementProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
    public static final JigsawReplacementProcessor INSTANCE = new JigsawReplacementProcessor();

    private JigsawReplacementProcessor() {
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader $$0, BlockPos $$1, BlockPos $$2, StructureTemplate.StructureBlockInfo $$3, StructureTemplate.StructureBlockInfo $$4, StructurePlaceSettings $$5) {
        void $$11;
        BlockState $$6 = $$4.state();
        if (!$$6.is(Blocks.JIGSAW)) {
            return $$4;
        }
        if ($$4.nbt() == null) {
            LOGGER.warn("Jigsaw block at {} is missing nbt, will not replace", (Object)$$1);
            return $$4;
        }
        String $$7 = $$4.nbt().getStringOr("final_state", "minecraft:air");
        try {
            BlockStateParser.BlockResult $$8 = BlockStateParser.parseForBlock($$0.holderLookup(Registries.BLOCK), $$7, true);
            BlockState $$9 = $$8.blockState();
        } catch (CommandSyntaxException $$10) {
            LOGGER.error("Failed to parse jigsaw replacement state '{}' at {}: {}", $$7, $$1, $$10.getMessage());
            return null;
        }
        if ($$11.is(Blocks.STRUCTURE_VOID)) {
            return null;
        }
        return new StructureTemplate.StructureBlockInfo($$4.pos(), (BlockState)$$11, null);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.JIGSAW_REPLACEMENT;
    }
}

