/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record StructureSpawnOverride(BoundingBoxType boundingBox, WeightedList<MobSpawnSettings.SpawnerData> spawns) {
    public static final Codec<StructureSpawnOverride> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)BoundingBoxType.CODEC.fieldOf("bounding_box").forGetter(StructureSpawnOverride::boundingBox), (App)WeightedList.codec(MobSpawnSettings.SpawnerData.CODEC).fieldOf("spawns").forGetter(StructureSpawnOverride::spawns)).apply((Applicative)$$0, StructureSpawnOverride::new));

    public static final class BoundingBoxType
    extends Enum<BoundingBoxType>
    implements StringRepresentable {
        public static final /* enum */ BoundingBoxType PIECE = new BoundingBoxType("piece");
        public static final /* enum */ BoundingBoxType STRUCTURE = new BoundingBoxType("full");
        public static final Codec<BoundingBoxType> CODEC;
        private final String id;
        private static final /* synthetic */ BoundingBoxType[] $VALUES;

        public static BoundingBoxType[] values() {
            return (BoundingBoxType[])$VALUES.clone();
        }

        public static BoundingBoxType valueOf(String $$0) {
            return Enum.valueOf(BoundingBoxType.class, $$0);
        }

        private BoundingBoxType(String $$0) {
            this.id = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }

        private static /* synthetic */ BoundingBoxType[] a() {
            return new BoundingBoxType[]{PIECE, STRUCTURE};
        }

        static {
            $VALUES = BoundingBoxType.a();
            CODEC = StringRepresentable.fromEnum(BoundingBoxType::values);
        }
    }
}

