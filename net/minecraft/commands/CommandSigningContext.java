/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.commands;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.chat.PlayerChatMessage;

public interface CommandSigningContext {
    public static final CommandSigningContext ANONYMOUS = new CommandSigningContext(){

        @Override
        @Nullable
        public PlayerChatMessage getArgument(String $$0) {
            return null;
        }
    };

    @Nullable
    public PlayerChatMessage getArgument(String var1);

    public record SignedArguments(Map<String, PlayerChatMessage> arguments) implements CommandSigningContext
    {
        @Override
        @Nullable
        public PlayerChatMessage getArgument(String $$0) {
            return this.arguments.get($$0);
        }
    }
}

