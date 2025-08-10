/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.chat;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface ChatDecorator {
    public static final ChatDecorator PLAIN = ($$0, $$1) -> $$1;

    public Component decorate(@Nullable ServerPlayer var1, Component var2);
}

