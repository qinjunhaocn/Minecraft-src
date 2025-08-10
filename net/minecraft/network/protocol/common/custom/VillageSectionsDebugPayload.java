/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record VillageSectionsDebugPayload(Set<SectionPos> villageChunks, Set<SectionPos> notVillageChunks) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, VillageSectionsDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(VillageSectionsDebugPayload::write, VillageSectionsDebugPayload::new);
    public static final CustomPacketPayload.Type<VillageSectionsDebugPayload> TYPE = CustomPacketPayload.createType("debug/village_sections");

    private VillageSectionsDebugPayload(FriendlyByteBuf $$0) {
        this($$0.readCollection(HashSet::new, FriendlyByteBuf::readSectionPos), $$0.readCollection(HashSet::new, FriendlyByteBuf::readSectionPos));
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeCollection(this.villageChunks, FriendlyByteBuf::writeSectionPos);
        $$0.writeCollection(this.notVillageChunks, FriendlyByteBuf::writeSectionPos);
    }

    public CustomPacketPayload.Type<VillageSectionsDebugPayload> type() {
        return TYPE;
    }
}

