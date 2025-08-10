/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.packs;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.InclusiveRange;

public record OverlayMetadataSection(List<OverlayEntry> overlays) {
    private static final Pattern DIR_VALIDATOR = Pattern.compile("[-_a-zA-Z0-9.]+");
    private static final Codec<OverlayMetadataSection> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)OverlayEntry.CODEC.listOf().fieldOf("entries").forGetter(OverlayMetadataSection::overlays)).apply((Applicative)$$0, OverlayMetadataSection::new));
    public static final MetadataSectionType<OverlayMetadataSection> TYPE = new MetadataSectionType<OverlayMetadataSection>("overlays", CODEC);

    private static DataResult<String> validateOverlayDir(String $$0) {
        if (!DIR_VALIDATOR.matcher($$0).matches()) {
            return DataResult.error(() -> $$0 + " is not accepted directory name");
        }
        return DataResult.success((Object)$$0);
    }

    public List<String> overlaysForVersion(int $$0) {
        return this.overlays.stream().filter($$1 -> $$1.isApplicable($$0)).map(OverlayEntry::overlay).toList();
    }

    public record OverlayEntry(InclusiveRange<Integer> format, String overlay) {
        static final Codec<OverlayEntry> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)InclusiveRange.codec(Codec.INT).fieldOf("formats").forGetter(OverlayEntry::format), (App)Codec.STRING.validate(OverlayMetadataSection::validateOverlayDir).fieldOf("directory").forGetter(OverlayEntry::overlay)).apply((Applicative)$$0, OverlayEntry::new));

        public boolean isApplicable(int $$0) {
            return this.format.isValueInRange($$0);
        }
    }
}

