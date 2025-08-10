/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.commands;

import net.minecraft.network.chat.Component;

public interface CommandSource {
    public static final CommandSource NULL = new CommandSource(){

        @Override
        public void sendSystemMessage(Component $$0) {
        }

        @Override
        public boolean acceptsSuccess() {
            return false;
        }

        @Override
        public boolean acceptsFailure() {
            return false;
        }

        @Override
        public boolean shouldInformAdmins() {
            return false;
        }
    };

    public void sendSystemMessage(Component var1);

    public boolean acceptsSuccess();

    public boolean acceptsFailure();

    public boolean shouldInformAdmins();

    default public boolean alwaysAccepts() {
        return false;
    }
}

