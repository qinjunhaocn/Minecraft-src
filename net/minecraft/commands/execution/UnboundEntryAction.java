/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.commands.execution;

import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

@FunctionalInterface
public interface UnboundEntryAction<T> {
    public void execute(T var1, ExecutionContext<T> var2, Frame var3);

    default public EntryAction<T> bind(T $$0) {
        return ($$1, $$2) -> this.execute($$0, $$1, $$2);
    }
}

