/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server;

import net.minecraft.commands.CommandSourceStack;

public class ConsoleInput {
    public final String msg;
    public final CommandSourceStack source;

    public ConsoleInput(String $$0, CommandSourceStack $$1) {
        this.msg = $$0;
        this.source = $$1;
    }
}

