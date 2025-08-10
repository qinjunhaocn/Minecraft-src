/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class IglooPieces {
    public static final int GENERATION_HEIGHT = 90;
    static final ResourceLocation STRUCTURE_LOCATION_IGLOO = ResourceLocation.withDefaultNamespace("igloo/top");
    private static final ResourceLocation STRUCTURE_LOCATION_LADDER = ResourceLocation.withDefaultNamespace("igloo/middle");
    private static final ResourceLocation STRUCTURE_LOCATION_LABORATORY = ResourceLocation.withDefaultNamespace("igloo/bottom");
    static final Map<ResourceLocation, BlockPos> PIVOTS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, new BlockPos(3, 5, 5), STRUCTURE_LOCATION_LADDER, new BlockPos(1, 3, 1), STRUCTURE_LOCATION_LABORATORY, new BlockPos(3, 6, 7));
    static final Map<ResourceLocation, BlockPos> OFFSETS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, BlockPos.ZERO, STRUCTURE_LOCATION_LADDER, new BlockPos(2, -3, 4), STRUCTURE_LOCATION_LABORATORY, new BlockPos(0, -3, -2));

    public static void addPieces(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2, StructurePieceAccessor $$3, RandomSource $$4) {
        if ($$4.nextDouble() < 0.5) {
            int $$5 = $$4.nextInt(8) + 4;
            $$3.addPiece(new IglooPiece($$0, STRUCTURE_LOCATION_LABORATORY, $$1, $$2, $$5 * 3));
            for (int $$6 = 0; $$6 < $$5 - 1; ++$$6) {
                $$3.addPiece(new IglooPiece($$0, STRUCTURE_LOCATION_LADDER, $$1, $$2, $$6 * 3));
            }
        }
        $$3.addPiece(new IglooPiece($$0, STRUCTURE_LOCATION_IGLOO, $$1, $$2, 0));
    }

    public static class IglooPiece
    extends TemplateStructurePiece {
        public IglooPiece(StructureTemplateManager $$0, ResourceLocation $$1, BlockPos $$2, Rotation $$3, int $$4) {
            super(StructurePieceType.IGLOO, 0, $$0, $$1, $$1.toString(), IglooPiece.makeSettings($$3, $$1), IglooPiece.makePosition($$1, $$2, $$4));
        }

        public IglooPiece(StructureTemplateManager $$0, CompoundTag $$12) {
            super(StructurePieceType.IGLOO, $$12, $$0, $$1 -> IglooPiece.makeSettings((Rotation)$$12.read("Rot", Rotation.LEGACY_CODEC).orElseThrow(), $$1));
        }

        private static StructurePlaceSettings makeSettings(Rotation $$0, ResourceLocation $$1) {
            return new StructurePlaceSettings().setRotation($$0).setMirror(Mirror.NONE).setRotationPivot(PIVOTS.get($$1)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK).setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
        }

        private static BlockPos makePosition(ResourceLocation $$0, BlockPos $$1, int $$2) {
            return $$1.offset(OFFSETS.get($$0)).below($$2);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.store("Rot", Rotation.LEGACY_CODEC, this.placeSettings.getRotation());
        }

        @Override
        protected void handleDataMarker(String $$0, BlockPos $$1, ServerLevelAccessor $$2, RandomSource $$3, BoundingBox $$4) {
            if (!"chest".equals($$0)) {
                return;
            }
            $$2.setBlock($$1, Blocks.AIR.defaultBlockState(), 3);
            BlockEntity $$5 = $$2.getBlockEntity($$1.below());
            if ($$5 instanceof ChestBlockEntity) {
                ((ChestBlockEntity)$$5).setLootTable(BuiltInLootTables.IGLOO_CHEST, $$3.nextLong());
            }
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            BlockPos $$13;
            BlockState $$14;
            ResourceLocation $$7 = ResourceLocation.parse(this.templateName);
            StructurePlaceSettings $$8 = IglooPiece.makeSettings(this.placeSettings.getRotation(), $$7);
            BlockPos $$9 = OFFSETS.get($$7);
            BlockPos $$10 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition($$8, new BlockPos(3 - $$9.getX(), 0, -$$9.getZ())));
            int $$11 = $$0.getHeight(Heightmap.Types.WORLD_SURFACE_WG, $$10.getX(), $$10.getZ());
            BlockPos $$12 = this.templatePosition;
            this.templatePosition = this.templatePosition.offset(0, $$11 - 90 - 1, 0);
            super.postProcess($$0, $$1, $$2, $$3, $$4, $$5, $$6);
            if ($$7.equals(STRUCTURE_LOCATION_IGLOO) && !($$14 = $$0.getBlockState(($$13 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition($$8, new BlockPos(3, 0, 5)))).below())).isAir() && !$$14.is(Blocks.LADDER)) {
                $$0.setBlock($$13, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
            }
            this.templatePosition = $$12;
        }
    }
}

