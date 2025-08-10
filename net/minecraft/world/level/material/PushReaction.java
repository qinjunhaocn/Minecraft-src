/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.material;

public final class PushReaction
extends Enum<PushReaction> {
    public static final /* enum */ PushReaction NORMAL = new PushReaction();
    public static final /* enum */ PushReaction DESTROY = new PushReaction();
    public static final /* enum */ PushReaction BLOCK = new PushReaction();
    public static final /* enum */ PushReaction IGNORE = new PushReaction();
    public static final /* enum */ PushReaction PUSH_ONLY = new PushReaction();
    private static final /* synthetic */ PushReaction[] $VALUES;

    public static PushReaction[] values() {
        return (PushReaction[])$VALUES.clone();
    }

    public static PushReaction valueOf(String $$0) {
        return Enum.valueOf(PushReaction.class, $$0);
    }

    private static /* synthetic */ PushReaction[] a() {
        return new PushReaction[]{NORMAL, DESTROY, BLOCK, IGNORE, PUSH_ONLY};
    }

    static {
        $VALUES = PushReaction.a();
    }
}

