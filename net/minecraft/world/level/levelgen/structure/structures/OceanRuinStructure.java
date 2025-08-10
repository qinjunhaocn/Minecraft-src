/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinPieces;

public class OceanRuinStructure
extends Structure {
    public static final MapCodec<OceanRuinStructure> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group(OceanRuinStructure.settingsCodec($$02), (App)Type.CODEC.fieldOf("biome_temp").forGetter($$0 -> $$0.biomeTemp), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("large_probability").forGetter($$0 -> Float.valueOf($$0.largeProbability)), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("cluster_probability").forGetter($$0 -> Float.valueOf($$0.clusterProbability))).apply((Applicative)$$02, OceanRuinStructure::new));
    public final Type biomeTemp;
    public final float largeProbability;
    public final float clusterProbability;

    public OceanRuinStructure(Structure.StructureSettings $$0, Type $$1, float $$2, float $$3) {
        super($$0);
        this.biomeTemp = $$1;
        this.largeProbability = $$2;
        this.clusterProbability = $$3;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        return OceanRuinStructure.onTopOfChunkCenter($$0, Heightmap.Types.OCEAN_FLOOR_WG, $$1 -> this.generatePieces((StructurePiecesBuilder)$$1, $$0));
    }

    private void generatePieces(StructurePiecesBuilder $$0, Structure.GenerationContext $$1) {
        BlockPos $$2 = new BlockPos($$1.chunkPos().getMinBlockX(), 90, $$1.chunkPos().getMinBlockZ());
        Rotation $$3 = Rotation.getRandom($$1.random());
        OceanRuinPieces.addPieces($$1.structureTemplateManager(), $$2, $$3, $$0, $$1.random(), this);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.OCEAN_RUIN;
    }

    public static final class Type
    extends Enum<Type>
    implements StringRepresentable {
        public static final /* enum */ Type WARM = new Type("warm");
        public static final /* enum */ Type COLD = new Type("cold");
        public static final Codec<Type> CODEC;
        @Deprecated
        public static final Codec<Type> LEGACY_CODEC;
        private final String name;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private Type(String $$0) {
            this.name = $$0;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Type[] b() {
            return new Type[]{WARM, COLD};
        }

        static {
            $VALUES = Type.b();
            CODEC = StringRepresentable.fromEnum(Type::values);
            LEGACY_CODEC = ExtraCodecs.legacyEnum(Type::valueOf);
        }
    }
}

