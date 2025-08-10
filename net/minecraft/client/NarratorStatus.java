/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;

public final class NarratorStatus
extends Enum<NarratorStatus> {
    public static final /* enum */ NarratorStatus OFF = new NarratorStatus(0, "options.narrator.off");
    public static final /* enum */ NarratorStatus ALL = new NarratorStatus(1, "options.narrator.all");
    public static final /* enum */ NarratorStatus CHAT = new NarratorStatus(2, "options.narrator.chat");
    public static final /* enum */ NarratorStatus SYSTEM = new NarratorStatus(3, "options.narrator.system");
    private static final IntFunction<NarratorStatus> BY_ID;
    private final int id;
    private final Component name;
    private static final /* synthetic */ NarratorStatus[] $VALUES;

    public static NarratorStatus[] values() {
        return (NarratorStatus[])$VALUES.clone();
    }

    public static NarratorStatus valueOf(String $$0) {
        return Enum.valueOf(NarratorStatus.class, $$0);
    }

    private NarratorStatus(int $$0, String $$1) {
        this.id = $$0;
        this.name = Component.translatable($$1);
    }

    public int getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }

    public static NarratorStatus byId(int $$0) {
        return BY_ID.apply($$0);
    }

    public boolean shouldNarrateChat() {
        return this == ALL || this == CHAT;
    }

    public boolean shouldNarrateSystem() {
        return this == ALL || this == SYSTEM;
    }

    public boolean shouldNarrateSystemOrChat() {
        return this == ALL || this == SYSTEM || this == CHAT;
    }

    private static /* synthetic */ NarratorStatus[] f() {
        return new NarratorStatus[]{OFF, ALL, CHAT, SYSTEM};
    }

    static {
        $VALUES = NarratorStatus.f();
        BY_ID = ByIdMap.a(NarratorStatus::getId, NarratorStatus.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}

