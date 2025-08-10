/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.saveddata.maps;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

public record MapFrame(BlockPos pos, int rotation, int entityId) {
    public static final Codec<MapFrame> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)BlockPos.CODEC.fieldOf("pos").forGetter(MapFrame::pos), (App)Codec.INT.fieldOf("rotation").forGetter(MapFrame::rotation), (App)Codec.INT.fieldOf("entity_id").forGetter(MapFrame::entityId)).apply((Applicative)$$0, MapFrame::new));

    public String getId() {
        return MapFrame.frameId(this.pos);
    }

    public static String frameId(BlockPos $$0) {
        return "frame-" + $$0.getX() + "," + $$0.getY() + "," + $$0.getZ();
    }
}

