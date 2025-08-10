/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.player;

import net.minecraft.network.chat.Component;

public final class PlayerModelPart
extends Enum<PlayerModelPart> {
    public static final /* enum */ PlayerModelPart CAPE = new PlayerModelPart(0, "cape");
    public static final /* enum */ PlayerModelPart JACKET = new PlayerModelPart(1, "jacket");
    public static final /* enum */ PlayerModelPart LEFT_SLEEVE = new PlayerModelPart(2, "left_sleeve");
    public static final /* enum */ PlayerModelPart RIGHT_SLEEVE = new PlayerModelPart(3, "right_sleeve");
    public static final /* enum */ PlayerModelPart LEFT_PANTS_LEG = new PlayerModelPart(4, "left_pants_leg");
    public static final /* enum */ PlayerModelPart RIGHT_PANTS_LEG = new PlayerModelPart(5, "right_pants_leg");
    public static final /* enum */ PlayerModelPart HAT = new PlayerModelPart(6, "hat");
    private final int bit;
    private final int mask;
    private final String id;
    private final Component name;
    private static final /* synthetic */ PlayerModelPart[] $VALUES;

    public static PlayerModelPart[] values() {
        return (PlayerModelPart[])$VALUES.clone();
    }

    public static PlayerModelPart valueOf(String $$0) {
        return Enum.valueOf(PlayerModelPart.class, $$0);
    }

    private PlayerModelPart(int $$0, String $$1) {
        this.bit = $$0;
        this.mask = 1 << $$0;
        this.id = $$1;
        this.name = Component.translatable("options.modelPart." + $$1);
    }

    public int getMask() {
        return this.mask;
    }

    public int getBit() {
        return this.bit;
    }

    public String getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }

    private static /* synthetic */ PlayerModelPart[] e() {
        return new PlayerModelPart[]{CAPE, JACKET, LEFT_SLEEVE, RIGHT_SLEEVE, LEFT_PANTS_LEG, RIGHT_PANTS_LEG, HAT};
    }

    static {
        $VALUES = PlayerModelPart.e();
    }
}

