/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.entity;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public final class EquipmentSlot
extends Enum<EquipmentSlot>
implements StringRepresentable {
    public static final /* enum */ EquipmentSlot MAINHAND = new EquipmentSlot(Type.HAND, 0, 0, "mainhand");
    public static final /* enum */ EquipmentSlot OFFHAND = new EquipmentSlot(Type.HAND, 1, 5, "offhand");
    public static final /* enum */ EquipmentSlot FEET = new EquipmentSlot(Type.HUMANOID_ARMOR, 0, 1, 1, "feet");
    public static final /* enum */ EquipmentSlot LEGS = new EquipmentSlot(Type.HUMANOID_ARMOR, 1, 1, 2, "legs");
    public static final /* enum */ EquipmentSlot CHEST = new EquipmentSlot(Type.HUMANOID_ARMOR, 2, 1, 3, "chest");
    public static final /* enum */ EquipmentSlot HEAD = new EquipmentSlot(Type.HUMANOID_ARMOR, 3, 1, 4, "head");
    public static final /* enum */ EquipmentSlot BODY = new EquipmentSlot(Type.ANIMAL_ARMOR, 0, 1, 6, "body");
    public static final /* enum */ EquipmentSlot SADDLE = new EquipmentSlot(Type.SADDLE, 0, 1, 7, "saddle");
    public static final int NO_COUNT_LIMIT = 0;
    public static final List<EquipmentSlot> VALUES;
    public static final IntFunction<EquipmentSlot> BY_ID;
    public static final StringRepresentable.EnumCodec<EquipmentSlot> CODEC;
    public static final StreamCodec<ByteBuf, EquipmentSlot> STREAM_CODEC;
    private final Type type;
    private final int index;
    private final int countLimit;
    private final int id;
    private final String name;
    private static final /* synthetic */ EquipmentSlot[] $VALUES;

    public static EquipmentSlot[] values() {
        return (EquipmentSlot[])$VALUES.clone();
    }

    public static EquipmentSlot valueOf(String $$0) {
        return Enum.valueOf(EquipmentSlot.class, $$0);
    }

    private EquipmentSlot(Type $$0, int $$1, int $$2, int $$3, String $$4) {
        this.type = $$0;
        this.index = $$1;
        this.countLimit = $$2;
        this.id = $$3;
        this.name = $$4;
    }

    private EquipmentSlot(Type $$0, int $$1, int $$2, String $$3) {
        this($$0, $$1, 0, $$2, $$3);
    }

    public Type getType() {
        return this.type;
    }

    public int getIndex() {
        return this.index;
    }

    public int getIndex(int $$0) {
        return $$0 + this.index;
    }

    public ItemStack limit(ItemStack $$0) {
        return this.countLimit > 0 ? $$0.split(this.countLimit) : $$0;
    }

    public int getId() {
        return this.id;
    }

    public int getFilterBit(int $$0) {
        return this.id + $$0;
    }

    public String getName() {
        return this.name;
    }

    public boolean isArmor() {
        return this.type == Type.HUMANOID_ARMOR || this.type == Type.ANIMAL_ARMOR;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean canIncreaseExperience() {
        return this.type != Type.SADDLE;
    }

    public static EquipmentSlot byName(String $$0) {
        EquipmentSlot $$1 = CODEC.byName($$0);
        if ($$1 != null) {
            return $$1;
        }
        throw new IllegalArgumentException("Invalid slot '" + $$0 + "'");
    }

    private static /* synthetic */ EquipmentSlot[] h() {
        return new EquipmentSlot[]{MAINHAND, OFFHAND, FEET, LEGS, CHEST, HEAD, BODY, SADDLE};
    }

    static {
        $VALUES = EquipmentSlot.h();
        VALUES = List.of((Object[])EquipmentSlot.values());
        BY_ID = ByIdMap.a($$0 -> $$0.id, EquipmentSlot.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        CODEC = StringRepresentable.fromEnum(EquipmentSlot::values);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type HAND = new Type();
        public static final /* enum */ Type HUMANOID_ARMOR = new Type();
        public static final /* enum */ Type ANIMAL_ARMOR = new Type();
        public static final /* enum */ Type SADDLE = new Type();
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private static /* synthetic */ Type[] a() {
            return new Type[]{HAND, HUMANOID_ARMOR, ANIMAL_ARMOR, SADDLE};
        }

        static {
            $VALUES = Type.a();
        }
    }
}

