/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlackstoneReplaceProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockAgeProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.CappedProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LavaSubmergedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.NopProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public interface StructureProcessorType<P extends StructureProcessor> {
    public static final Codec<StructureProcessor> SINGLE_CODEC = BuiltInRegistries.STRUCTURE_PROCESSOR.byNameCodec().dispatch("processor_type", StructureProcessor::getType, StructureProcessorType::codec);
    public static final Codec<StructureProcessorList> LIST_OBJECT_CODEC = SINGLE_CODEC.listOf().xmap(StructureProcessorList::new, StructureProcessorList::list);
    public static final Codec<StructureProcessorList> DIRECT_CODEC = Codec.withAlternative((Codec)LIST_OBJECT_CODEC.fieldOf("processors").codec(), LIST_OBJECT_CODEC);
    public static final Codec<Holder<StructureProcessorList>> LIST_CODEC = RegistryFileCodec.create(Registries.PROCESSOR_LIST, DIRECT_CODEC);
    public static final StructureProcessorType<BlockIgnoreProcessor> BLOCK_IGNORE = StructureProcessorType.register("block_ignore", BlockIgnoreProcessor.CODEC);
    public static final StructureProcessorType<BlockRotProcessor> BLOCK_ROT = StructureProcessorType.register("block_rot", BlockRotProcessor.CODEC);
    public static final StructureProcessorType<GravityProcessor> GRAVITY = StructureProcessorType.register("gravity", GravityProcessor.CODEC);
    public static final StructureProcessorType<JigsawReplacementProcessor> JIGSAW_REPLACEMENT = StructureProcessorType.register("jigsaw_replacement", JigsawReplacementProcessor.CODEC);
    public static final StructureProcessorType<RuleProcessor> RULE = StructureProcessorType.register("rule", RuleProcessor.CODEC);
    public static final StructureProcessorType<NopProcessor> NOP = StructureProcessorType.register("nop", NopProcessor.CODEC);
    public static final StructureProcessorType<BlockAgeProcessor> BLOCK_AGE = StructureProcessorType.register("block_age", BlockAgeProcessor.CODEC);
    public static final StructureProcessorType<BlackstoneReplaceProcessor> BLACKSTONE_REPLACE = StructureProcessorType.register("blackstone_replace", BlackstoneReplaceProcessor.CODEC);
    public static final StructureProcessorType<LavaSubmergedBlockProcessor> LAVA_SUBMERGED_BLOCK = StructureProcessorType.register("lava_submerged_block", LavaSubmergedBlockProcessor.CODEC);
    public static final StructureProcessorType<ProtectedBlockProcessor> PROTECTED_BLOCKS = StructureProcessorType.register("protected_blocks", ProtectedBlockProcessor.CODEC);
    public static final StructureProcessorType<CappedProcessor> CAPPED = StructureProcessorType.register("capped", CappedProcessor.CODEC);

    public MapCodec<P> codec();

    public static <P extends StructureProcessor> StructureProcessorType<P> register(String $$0, MapCodec<P> $$1) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, $$0, () -> $$1);
    }
}

