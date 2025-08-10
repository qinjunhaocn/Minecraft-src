/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public final class ItemDisplayContext
extends Enum<ItemDisplayContext>
implements StringRepresentable {
    public static final /* enum */ ItemDisplayContext NONE = new ItemDisplayContext(0, "none");
    public static final /* enum */ ItemDisplayContext THIRD_PERSON_LEFT_HAND = new ItemDisplayContext(1, "thirdperson_lefthand");
    public static final /* enum */ ItemDisplayContext THIRD_PERSON_RIGHT_HAND = new ItemDisplayContext(2, "thirdperson_righthand");
    public static final /* enum */ ItemDisplayContext FIRST_PERSON_LEFT_HAND = new ItemDisplayContext(3, "firstperson_lefthand");
    public static final /* enum */ ItemDisplayContext FIRST_PERSON_RIGHT_HAND = new ItemDisplayContext(4, "firstperson_righthand");
    public static final /* enum */ ItemDisplayContext HEAD = new ItemDisplayContext(5, "head");
    public static final /* enum */ ItemDisplayContext GUI = new ItemDisplayContext(6, "gui");
    public static final /* enum */ ItemDisplayContext GROUND = new ItemDisplayContext(7, "ground");
    public static final /* enum */ ItemDisplayContext FIXED = new ItemDisplayContext(8, "fixed");
    public static final Codec<ItemDisplayContext> CODEC;
    public static final IntFunction<ItemDisplayContext> BY_ID;
    private final byte id;
    private final String name;
    private static final /* synthetic */ ItemDisplayContext[] $VALUES;

    public static ItemDisplayContext[] values() {
        return (ItemDisplayContext[])$VALUES.clone();
    }

    public static ItemDisplayContext valueOf(String $$0) {
        return Enum.valueOf(ItemDisplayContext.class, $$0);
    }

    private ItemDisplayContext(int $$0, String $$1) {
        this.name = $$1;
        this.id = (byte)$$0;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public byte getId() {
        return this.id;
    }

    public boolean firstPerson() {
        return this == FIRST_PERSON_LEFT_HAND || this == FIRST_PERSON_RIGHT_HAND;
    }

    public boolean leftHand() {
        return this == FIRST_PERSON_LEFT_HAND || this == THIRD_PERSON_LEFT_HAND;
    }

    private static /* synthetic */ ItemDisplayContext[] e() {
        return new ItemDisplayContext[]{NONE, THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, HEAD, GUI, GROUND, FIXED};
    }

    static {
        $VALUES = ItemDisplayContext.e();
        CODEC = StringRepresentable.fromEnum(ItemDisplayContext::values);
        BY_ID = ByIdMap.a(ItemDisplayContext::getId, ItemDisplayContext.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    }
}

