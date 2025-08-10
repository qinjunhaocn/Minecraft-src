/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;

public class SeedCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02, boolean $$1) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("seed").requires(Commands.hasPermission($$1 ? 2 : 0))).executes($$0 -> {
            long $$1 = ((CommandSourceStack)$$0.getSource()).getLevel().getSeed();
            MutableComponent $$2 = ComponentUtils.copyOnClickText(String.valueOf($$1));
            ((CommandSourceStack)$$0.getSource()).sendSuccess(() -> Component.a("commands.seed.success", $$2), false);
            return (int)$$1;
        }));
    }
}

