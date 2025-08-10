/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.util.StringRepresentable;

public interface LoggedChatEvent {
    public static final Codec<LoggedChatEvent> CODEC = StringRepresentable.fromEnum(Type::values).dispatch(LoggedChatEvent::type, Type::codec);

    public Type type();

    public static final class Type
    extends Enum<Type>
    implements StringRepresentable {
        public static final /* enum */ Type PLAYER = new Type("player", () -> LoggedChatMessage.Player.CODEC);
        public static final /* enum */ Type SYSTEM = new Type("system", () -> LoggedChatMessage.System.CODEC);
        private final String serializedName;
        private final Supplier<MapCodec<? extends LoggedChatEvent>> codec;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private Type(String $$0, Supplier<MapCodec<? extends LoggedChatEvent>> $$1) {
            this.serializedName = $$0;
            this.codec = $$1;
        }

        private MapCodec<? extends LoggedChatEvent> codec() {
            return this.codec.get();
        }

        @Override
        public String getSerializedName() {
            return this.serializedName;
        }

        private static /* synthetic */ Type[] e() {
            return new Type[]{PLAYER, SYSTEM};
        }

        static {
            $VALUES = Type.e();
        }
    }
}

