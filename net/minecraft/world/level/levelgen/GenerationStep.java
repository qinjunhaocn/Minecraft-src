/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public class GenerationStep {

    public static final class Decoration
    extends Enum<Decoration>
    implements StringRepresentable {
        public static final /* enum */ Decoration RAW_GENERATION = new Decoration("raw_generation");
        public static final /* enum */ Decoration LAKES = new Decoration("lakes");
        public static final /* enum */ Decoration LOCAL_MODIFICATIONS = new Decoration("local_modifications");
        public static final /* enum */ Decoration UNDERGROUND_STRUCTURES = new Decoration("underground_structures");
        public static final /* enum */ Decoration SURFACE_STRUCTURES = new Decoration("surface_structures");
        public static final /* enum */ Decoration STRONGHOLDS = new Decoration("strongholds");
        public static final /* enum */ Decoration UNDERGROUND_ORES = new Decoration("underground_ores");
        public static final /* enum */ Decoration UNDERGROUND_DECORATION = new Decoration("underground_decoration");
        public static final /* enum */ Decoration FLUID_SPRINGS = new Decoration("fluid_springs");
        public static final /* enum */ Decoration VEGETAL_DECORATION = new Decoration("vegetal_decoration");
        public static final /* enum */ Decoration TOP_LAYER_MODIFICATION = new Decoration("top_layer_modification");
        public static final Codec<Decoration> CODEC;
        private final String name;
        private static final /* synthetic */ Decoration[] $VALUES;

        public static Decoration[] values() {
            return (Decoration[])$VALUES.clone();
        }

        public static Decoration valueOf(String $$0) {
            return Enum.valueOf(Decoration.class, $$0);
        }

        private Decoration(String $$0) {
            this.name = $$0;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Decoration[] b() {
            return new Decoration[]{RAW_GENERATION, LAKES, LOCAL_MODIFICATIONS, UNDERGROUND_STRUCTURES, SURFACE_STRUCTURES, STRONGHOLDS, UNDERGROUND_ORES, UNDERGROUND_DECORATION, FLUID_SPRINGS, VEGETAL_DECORATION, TOP_LAYER_MODIFICATION};
        }

        static {
            $VALUES = Decoration.b();
            CODEC = StringRepresentable.fromEnum(Decoration::values);
        }
    }
}

