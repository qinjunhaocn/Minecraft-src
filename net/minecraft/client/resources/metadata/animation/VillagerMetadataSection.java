/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.resources.metadata.animation;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.StringRepresentable;

public record VillagerMetadataSection(Hat hat) {
    public static final Codec<VillagerMetadataSection> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Hat.CODEC.optionalFieldOf("hat", (Object)Hat.NONE).forGetter(VillagerMetadataSection::hat)).apply((Applicative)$$0, VillagerMetadataSection::new));
    public static final MetadataSectionType<VillagerMetadataSection> TYPE = new MetadataSectionType<VillagerMetadataSection>("villager", CODEC);

    public static final class Hat
    extends Enum<Hat>
    implements StringRepresentable {
        public static final /* enum */ Hat NONE = new Hat("none");
        public static final /* enum */ Hat PARTIAL = new Hat("partial");
        public static final /* enum */ Hat FULL = new Hat("full");
        public static final Codec<Hat> CODEC;
        private final String name;
        private static final /* synthetic */ Hat[] $VALUES;

        public static Hat[] values() {
            return (Hat[])$VALUES.clone();
        }

        public static Hat valueOf(String $$0) {
            return Enum.valueOf(Hat.class, $$0);
        }

        private Hat(String $$0) {
            this.name = $$0;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Hat[] a() {
            return new Hat[]{NONE, PARTIAL, FULL};
        }

        static {
            $VALUES = Hat.a();
            CODEC = StringRepresentable.fromEnum(Hat::values);
        }
    }
}

