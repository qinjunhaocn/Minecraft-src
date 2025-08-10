/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.rcon;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class RconConsoleSource
implements CommandSource {
    private static final String RCON = "Rcon";
    private static final Component RCON_COMPONENT = Component.literal("Rcon");
    private final StringBuffer buffer = new StringBuffer();
    private final MinecraftServer server;

    public RconConsoleSource(MinecraftServer $$0) {
        this.server = $$0;
    }

    public void prepareForCommand() {
        this.buffer.setLength(0);
    }

    public String getCommandResponse() {
        return this.buffer.toString();
    }

    public CommandSourceStack createCommandSourceStack() {
        ServerLevel $$0 = this.server.overworld();
        return new CommandSourceStack(this, Vec3.atLowerCornerOf($$0.getSharedSpawnPos()), Vec2.ZERO, $$0, 4, RCON, RCON_COMPONENT, this.server, null);
    }

    @Override
    public void sendSystemMessage(Component $$0) {
        this.buffer.append($$0.getString());
    }

    @Override
    public boolean acceptsSuccess() {
        return true;
    }

    @Override
    public boolean acceptsFailure() {
        return true;
    }

    @Override
    public boolean shouldInformAdmins() {
        return this.server.shouldRconBroadcast();
    }
}

