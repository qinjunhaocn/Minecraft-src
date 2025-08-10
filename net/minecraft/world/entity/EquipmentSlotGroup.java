/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  java.lang.MatchException
 */
package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;

public final class EquipmentSlotGroup
extends Enum<EquipmentSlotGroup>
implements StringRepresentable,
Iterable<EquipmentSlot> {
    public static final /* enum */ EquipmentSlotGroup ANY = new EquipmentSlotGroup(0, "any", $$0 -> true);
    public static final /* enum */ EquipmentSlotGroup MAINHAND = new EquipmentSlotGroup(1, "mainhand", EquipmentSlot.MAINHAND);
    public static final /* enum */ EquipmentSlotGroup OFFHAND = new EquipmentSlotGroup(2, "offhand", EquipmentSlot.OFFHAND);
    public static final /* enum */ EquipmentSlotGroup HAND = new EquipmentSlotGroup(3, "hand", $$0 -> $$0.getType() == EquipmentSlot.Type.HAND);
    public static final /* enum */ EquipmentSlotGroup FEET = new EquipmentSlotGroup(4, "feet", EquipmentSlot.FEET);
    public static final /* enum */ EquipmentSlotGroup LEGS = new EquipmentSlotGroup(5, "legs", EquipmentSlot.LEGS);
    public static final /* enum */ EquipmentSlotGroup CHEST = new EquipmentSlotGroup(6, "chest", EquipmentSlot.CHEST);
    public static final /* enum */ EquipmentSlotGroup HEAD = new EquipmentSlotGroup(7, "head", EquipmentSlot.HEAD);
    public static final /* enum */ EquipmentSlotGroup ARMOR = new EquipmentSlotGroup(8, "armor", EquipmentSlot::isArmor);
    public static final /* enum */ EquipmentSlotGroup BODY = new EquipmentSlotGroup(9, "body", EquipmentSlot.BODY);
    public static final /* enum */ EquipmentSlotGroup SADDLE = new EquipmentSlotGroup(10, "saddle", EquipmentSlot.SADDLE);
    public static final IntFunction<EquipmentSlotGroup> BY_ID;
    public static final Codec<EquipmentSlotGroup> CODEC;
    public static final StreamCodec<ByteBuf, EquipmentSlotGroup> STREAM_CODEC;
    private final int id;
    private final String key;
    private final Predicate<EquipmentSlot> predicate;
    private final List<EquipmentSlot> slots;
    private static final /* synthetic */ EquipmentSlotGroup[] $VALUES;

    public static EquipmentSlotGroup[] values() {
        return (EquipmentSlotGroup[])$VALUES.clone();
    }

    public static EquipmentSlotGroup valueOf(String $$0) {
        return Enum.valueOf(EquipmentSlotGroup.class, $$0);
    }

    private EquipmentSlotGroup(int $$0, String $$1, Predicate<EquipmentSlot> $$2) {
        this.id = $$0;
        this.key = $$1;
        this.predicate = $$2;
        this.slots = EquipmentSlot.VALUES.stream().filter($$2).toList();
    }

    private EquipmentSlotGroup(int $$0, String $$12, EquipmentSlot $$2) {
        this($$0, $$12, (EquipmentSlot $$1) -> $$1 == $$2);
    }

    public static EquipmentSlotGroup bySlot(EquipmentSlot $$0) {
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case EquipmentSlot.MAINHAND -> MAINHAND;
            case EquipmentSlot.OFFHAND -> OFFHAND;
            case EquipmentSlot.FEET -> FEET;
            case EquipmentSlot.LEGS -> LEGS;
            case EquipmentSlot.CHEST -> CHEST;
            case EquipmentSlot.HEAD -> HEAD;
            case EquipmentSlot.BODY -> BODY;
            case EquipmentSlot.SADDLE -> SADDLE;
        };
    }

    @Override
    public String getSerializedName() {
        return this.key;
    }

    public boolean test(EquipmentSlot $$0) {
        return this.predicate.test($$0);
    }

    public List<EquipmentSlot> slots() {
        return this.slots;
    }

    @Override
    public Iterator<EquipmentSlot> iterator() {
        return this.slots.iterator();
    }

    private static /* synthetic */ EquipmentSlotGroup[] b() {
        return new EquipmentSlotGroup[]{ANY, MAINHAND, OFFHAND, HAND, FEET, LEGS, CHEST, HEAD, ARMOR, BODY, SADDLE};
    }

    static {
        $VALUES = EquipmentSlotGroup.b();
        BY_ID = ByIdMap.a($$0 -> $$0.id, EquipmentSlotGroup.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        CODEC = StringRepresentable.fromEnum(EquipmentSlotGroup::values);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
    }
}

