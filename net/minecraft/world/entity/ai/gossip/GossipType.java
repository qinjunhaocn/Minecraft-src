/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.entity.ai.gossip;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public final class GossipType
extends Enum<GossipType>
implements StringRepresentable {
    public static final /* enum */ GossipType MAJOR_NEGATIVE = new GossipType("major_negative", -5, 100, 10, 10);
    public static final /* enum */ GossipType MINOR_NEGATIVE = new GossipType("minor_negative", -1, 200, 20, 20);
    public static final /* enum */ GossipType MINOR_POSITIVE = new GossipType("minor_positive", 1, 25, 1, 5);
    public static final /* enum */ GossipType MAJOR_POSITIVE = new GossipType("major_positive", 5, 20, 0, 20);
    public static final /* enum */ GossipType TRADING = new GossipType("trading", 1, 25, 2, 20);
    public static final int REPUTATION_CHANGE_PER_EVENT = 25;
    public static final int REPUTATION_CHANGE_PER_EVERLASTING_MEMORY = 20;
    public static final int REPUTATION_CHANGE_PER_TRADE = 2;
    public final String id;
    public final int weight;
    public final int max;
    public final int decayPerDay;
    public final int decayPerTransfer;
    public static final Codec<GossipType> CODEC;
    private static final /* synthetic */ GossipType[] $VALUES;

    public static GossipType[] values() {
        return (GossipType[])$VALUES.clone();
    }

    public static GossipType valueOf(String $$0) {
        return Enum.valueOf(GossipType.class, $$0);
    }

    private GossipType(String $$0, int $$1, int $$2, int $$3, int $$4) {
        this.id = $$0;
        this.weight = $$1;
        this.max = $$2;
        this.decayPerDay = $$3;
        this.decayPerTransfer = $$4;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    private static /* synthetic */ GossipType[] a() {
        return new GossipType[]{MAJOR_NEGATIVE, MINOR_NEGATIVE, MINOR_POSITIVE, MAJOR_POSITIVE, TRADING};
    }

    static {
        $VALUES = GossipType.a();
        CODEC = StringRepresentable.fromEnum(GossipType::values);
    }
}

