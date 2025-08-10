/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.StringRepresentable;

public final class HumanoidArm
extends Enum<HumanoidArm>
implements OptionEnum,
StringRepresentable {
    public static final /* enum */ HumanoidArm LEFT = new HumanoidArm(0, "left", "options.mainHand.left");
    public static final /* enum */ HumanoidArm RIGHT = new HumanoidArm(1, "right", "options.mainHand.right");
    public static final Codec<HumanoidArm> CODEC;
    public static final IntFunction<HumanoidArm> BY_ID;
    private final int id;
    private final String name;
    private final String translationKey;
    private static final /* synthetic */ HumanoidArm[] $VALUES;

    public static HumanoidArm[] values() {
        return (HumanoidArm[])$VALUES.clone();
    }

    public static HumanoidArm valueOf(String $$0) {
        return Enum.valueOf(HumanoidArm.class, $$0);
    }

    private HumanoidArm(int $$0, String $$1, String $$2) {
        this.id = $$0;
        this.name = $$1;
        this.translationKey = $$2;
    }

    public HumanoidArm getOpposite() {
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.translationKey;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private static /* synthetic */ HumanoidArm[] f() {
        return new HumanoidArm[]{LEFT, RIGHT};
    }

    static {
        $VALUES = HumanoidArm.f();
        CODEC = StringRepresentable.fromEnum(HumanoidArm::values);
        BY_ID = ByIdMap.a(HumanoidArm::getId, HumanoidArm.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    }
}

