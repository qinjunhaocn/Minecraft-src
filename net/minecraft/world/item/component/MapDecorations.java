/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.component;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public record MapDecorations(Map<String, Entry> decorations) {
    public static final MapDecorations EMPTY = new MapDecorations(Map.of());
    public static final Codec<MapDecorations> CODEC = Codec.unboundedMap((Codec)Codec.STRING, Entry.CODEC).xmap(MapDecorations::new, MapDecorations::decorations);

    public MapDecorations withDecoration(String $$0, Entry $$1) {
        return new MapDecorations(Util.copyAndPut(this.decorations, $$0, $$1));
    }

    public record Entry(Holder<MapDecorationType> type, double x, double z, float rotation) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)MapDecorationType.CODEC.fieldOf("type").forGetter(Entry::type), (App)Codec.DOUBLE.fieldOf("x").forGetter(Entry::x), (App)Codec.DOUBLE.fieldOf("z").forGetter(Entry::z), (App)Codec.FLOAT.fieldOf("rotation").forGetter(Entry::rotation)).apply((Applicative)$$0, Entry::new));
    }
}

