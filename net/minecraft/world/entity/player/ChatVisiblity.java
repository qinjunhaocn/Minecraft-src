/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.entity.player;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public final class ChatVisiblity
extends Enum<ChatVisiblity>
implements OptionEnum {
    public static final /* enum */ ChatVisiblity FULL = new ChatVisiblity(0, "options.chat.visibility.full");
    public static final /* enum */ ChatVisiblity SYSTEM = new ChatVisiblity(1, "options.chat.visibility.system");
    public static final /* enum */ ChatVisiblity HIDDEN = new ChatVisiblity(2, "options.chat.visibility.hidden");
    private static final IntFunction<ChatVisiblity> BY_ID;
    private final int id;
    private final String key;
    private static final /* synthetic */ ChatVisiblity[] $VALUES;

    public static ChatVisiblity[] values() {
        return (ChatVisiblity[])$VALUES.clone();
    }

    public static ChatVisiblity valueOf(String $$0) {
        return Enum.valueOf(ChatVisiblity.class, $$0);
    }

    private ChatVisiblity(int $$0, String $$1) {
        this.id = $$0;
        this.key = $$1;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public static ChatVisiblity byId(int $$0) {
        return BY_ID.apply($$0);
    }

    private static /* synthetic */ ChatVisiblity[] c() {
        return new ChatVisiblity[]{FULL, SYSTEM, HIDDEN};
    }

    static {
        $VALUES = ChatVisiblity.c();
        BY_ID = ByIdMap.a(ChatVisiblity::getId, ChatVisiblity.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}

