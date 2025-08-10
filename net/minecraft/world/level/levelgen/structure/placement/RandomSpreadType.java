/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.MatchException
 */
package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

public final class RandomSpreadType
extends Enum<RandomSpreadType>
implements StringRepresentable {
    public static final /* enum */ RandomSpreadType LINEAR = new RandomSpreadType("linear");
    public static final /* enum */ RandomSpreadType TRIANGULAR = new RandomSpreadType("triangular");
    public static final Codec<RandomSpreadType> CODEC;
    private final String id;
    private static final /* synthetic */ RandomSpreadType[] $VALUES;

    public static RandomSpreadType[] values() {
        return (RandomSpreadType[])$VALUES.clone();
    }

    public static RandomSpreadType valueOf(String $$0) {
        return Enum.valueOf(RandomSpreadType.class, $$0);
    }

    private RandomSpreadType(String $$0) {
        this.id = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    public int evaluate(RandomSource $$0, int $$1) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> $$0.nextInt($$1);
            case 1 -> ($$0.nextInt($$1) + $$0.nextInt($$1)) / 2;
        };
    }

    private static /* synthetic */ RandomSpreadType[] a() {
        return new RandomSpreadType[]{LINEAR, TRIANGULAR};
    }

    static {
        $VALUES = RandomSpreadType.a();
        CODEC = StringRepresentable.fromEnum(RandomSpreadType::values);
    }
}

