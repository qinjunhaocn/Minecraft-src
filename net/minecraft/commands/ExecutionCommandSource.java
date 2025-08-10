/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ResultConsumer
 *  com.mojang.brigadier.exceptions.CommandExceptionType
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.PermissionSource;
import net.minecraft.commands.execution.TraceCallbacks;

public interface ExecutionCommandSource<T extends ExecutionCommandSource<T>>
extends PermissionSource {
    public T withCallback(CommandResultCallback var1);

    public CommandResultCallback callback();

    default public T clearCallbacks() {
        return this.withCallback(CommandResultCallback.EMPTY);
    }

    public CommandDispatcher<T> dispatcher();

    public void handleError(CommandExceptionType var1, Message var2, boolean var3, @Nullable TraceCallbacks var4);

    public boolean isSilent();

    default public void handleError(CommandSyntaxException $$0, boolean $$1, @Nullable TraceCallbacks $$2) {
        this.handleError($$0.getType(), $$0.getRawMessage(), $$1, $$2);
    }

    public static <T extends ExecutionCommandSource<T>> ResultConsumer<T> resultConsumer() {
        return ($$0, $$1, $$2) -> ((ExecutionCommandSource)$$0.getSource()).callback().onResult($$1, $$2);
    }
}

