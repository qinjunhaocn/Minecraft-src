/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world;

public final class InteractionHand
extends Enum<InteractionHand> {
    public static final /* enum */ InteractionHand MAIN_HAND = new InteractionHand();
    public static final /* enum */ InteractionHand OFF_HAND = new InteractionHand();
    private static final /* synthetic */ InteractionHand[] $VALUES;

    public static InteractionHand[] values() {
        return (InteractionHand[])$VALUES.clone();
    }

    public static InteractionHand valueOf(String $$0) {
        return Enum.valueOf(InteractionHand.class, $$0);
    }

    private static /* synthetic */ InteractionHand[] a() {
        return new InteractionHand[]{MAIN_HAND, OFF_HAND};
    }

    static {
        $VALUES = InteractionHand.a();
    }
}

