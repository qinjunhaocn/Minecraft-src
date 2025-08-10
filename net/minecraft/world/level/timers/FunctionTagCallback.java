/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.timers;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;

public record FunctionTagCallback(ResourceLocation tagId) implements TimerCallback<MinecraftServer>
{
    public static final MapCodec<FunctionTagCallback> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("Name").forGetter(FunctionTagCallback::tagId)).apply((Applicative)$$0, FunctionTagCallback::new));

    @Override
    public void handle(MinecraftServer $$0, TimerQueue<MinecraftServer> $$1, long $$2) {
        ServerFunctionManager $$3 = $$0.getFunctions();
        List<CommandFunction<CommandSourceStack>> $$4 = $$3.getTag(this.tagId);
        for (CommandFunction<CommandSourceStack> $$5 : $$4) {
            $$3.execute($$5, $$3.getGameLoopSender());
        }
    }

    @Override
    public MapCodec<FunctionTagCallback> codec() {
        return CODEC;
    }
}

