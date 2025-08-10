/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.FriendlyByteBuf;

public interface ArgumentTypeInfo<A extends ArgumentType<?>, T extends Template<A>> {
    public void serializeToNetwork(T var1, FriendlyByteBuf var2);

    public T deserializeFromNetwork(FriendlyByteBuf var1);

    public void serializeToJson(T var1, JsonObject var2);

    public T unpack(A var1);

    public static interface Template<A extends ArgumentType<?>> {
        public A instantiate(CommandBuildContext var1);

        public ArgumentTypeInfo<A, ?> type();
    }
}

