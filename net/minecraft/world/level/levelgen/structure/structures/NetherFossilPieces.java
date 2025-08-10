/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.structure.structures;

import net.minecraft.Util;
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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class NetherFossilPieces {
    private static final ResourceLocation[] FOSSILS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("nether_fossils/fossil_1"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_2"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_3"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_4"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_5"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_6"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_7"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_8"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_9"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_10"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_11"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_12"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_13"), ResourceLocation.withDefaultNamespace("nether_fossils/fossil_14")};

    public static void addPieces(StructureTemplateManager $$0, StructurePieceAccessor $$1, RandomSource $$2, BlockPos $$3) {
        Rotation $$4 = Rotation.getRandom($$2);
        $$1.addPiece(new NetherFossilPiece($$0, Util.a(FOSSILS, $$2), $$3, $$4));
    }

    public static class NetherFossilPiece
    extends TemplateStructurePiece {
        public NetherFossilPiece(StructureTemplateManager $$0, ResourceLocation $$1, BlockPos $$2, Rotation $$3) {
            super(StructurePieceType.NETHER_FOSSIL, 0, $$0, $$1, $$1.toString(), NetherFossilPiece.makeSettings($$3), $$2);
        }

        public NetherFossilPiece(StructureTemplateManager $$0, CompoundTag $$12) {
            super(StructurePieceType.NETHER_FOSSIL, $$12, $$0, (ResourceLocation $$1) -> NetherFossilPiece.makeSettings((Rotation)$$12.read("Rot", Rotation.LEGACY_CODEC).orElseThrow()));
        }

        private static StructurePlaceSettings makeSettings(Rotation $$0) {
            return new StructurePlaceSettings().setRotation($$0).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.store("Rot", Rotation.LEGACY_CODEC, this.placeSettings.getRotation());
        }

        @Override
        protected void handleDataMarker(String $$0, BlockPos $$1, ServerLevelAccessor $$2, RandomSource $$3, BoundingBox $$4) {
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            BoundingBox $$7 = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
            $$4.encapsulate($$7);
            super.postProcess($$0, $$1, $$2, $$3, $$4, $$5, $$6);
            this.placeDriedGhast($$0, $$3, $$7, $$4);
        }

        private void placeDriedGhast(WorldGenLevel $$0, RandomSource $$1, BoundingBox $$2, BoundingBox $$3) {
            int $$7;
            int $$6;
            int $$5;
            BlockPos $$8;
            RandomSource $$4 = RandomSource.create($$0.getSeed()).forkPositional().at($$2.getCenter());
            if ($$4.nextFloat() < 0.5f && $$0.getBlockState($$8 = new BlockPos($$5 = $$2.minX() + $$4.nextInt($$2.getXSpan()), $$6 = $$2.minY(), $$7 = $$2.minZ() + $$4.nextInt($$2.getZSpan()))).isAir() && $$3.isInside($$8)) {
                $$0.setBlock($$8, Blocks.DRIED_GHAST.defaultBlockState().rotate(Rotation.getRandom($$4)), 2);
            }
        }
    }
}

