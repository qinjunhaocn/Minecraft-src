/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.multiplayer.chat;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.chat.ChatTrustLevel;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.util.ExtraCodecs;

public interface LoggedChatMessage
extends LoggedChatEvent {
    public static Player player(GameProfile $$0, PlayerChatMessage $$1, ChatTrustLevel $$2) {
        return new Player($$0, $$1, $$2);
    }

    public static System system(Component $$0, Instant $$1) {
        return new System($$0, $$1);
    }

    public Component toContentComponent();

    default public Component toNarrationComponent() {
        return this.toContentComponent();
    }

    public boolean canReport(UUID var1);

    public record Player(GameProfile profile, PlayerChatMessage message, ChatTrustLevel trustLevel) implements LoggedChatMessage
    {
        public static final MapCodec<Player> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ExtraCodecs.GAME_PROFILE.fieldOf("profile").forGetter(Player::profile), (App)PlayerChatMessage.MAP_CODEC.forGetter(Player::message), (App)ChatTrustLevel.CODEC.optionalFieldOf("trust_level", (Object)ChatTrustLevel.SECURE).forGetter(Player::trustLevel)).apply((Applicative)$$0, Player::new));
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        @Override
        public Component toContentComponent() {
            if (!this.message.filterMask().isEmpty()) {
                Component $$0 = this.message.filterMask().applyWithFormatting(this.message.signedContent());
                return $$0 != null ? $$0 : Component.empty();
            }
            return this.message.decoratedContent();
        }

        @Override
        public Component toNarrationComponent() {
            Component $$0 = this.toContentComponent();
            Component $$1 = this.getTimeComponent();
            return Component.a("gui.chatSelection.message.narrate", new Object[]{this.profile.getName(), $$0, $$1});
        }

        public Component toHeadingComponent() {
            Component $$0 = this.getTimeComponent();
            return Component.a("gui.chatSelection.heading", new Object[]{this.profile.getName(), $$0});
        }

        private Component getTimeComponent() {
            LocalDateTime $$0 = LocalDateTime.ofInstant(this.message.timeStamp(), ZoneOffset.systemDefault());
            return Component.literal($$0.format(TIME_FORMATTER)).a(ChatFormatting.ITALIC, ChatFormatting.GRAY);
        }

        @Override
        public boolean canReport(UUID $$0) {
            return this.message.hasSignatureFrom($$0);
        }

        public UUID profileId() {
            return this.profile.getId();
        }

        @Override
        public LoggedChatEvent.Type type() {
            return LoggedChatEvent.Type.PLAYER;
        }
    }

    public record System(Component message, Instant timeStamp) implements LoggedChatMessage
    {
        public static final MapCodec<System> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ComponentSerialization.CODEC.fieldOf("message").forGetter(System::message), (App)ExtraCodecs.INSTANT_ISO8601.fieldOf("time_stamp").forGetter(System::timeStamp)).apply((Applicative)$$0, System::new));

        @Override
        public Component toContentComponent() {
            return this.message;
        }

        @Override
        public boolean canReport(UUID $$0) {
            return false;
        }

        @Override
        public LoggedChatEvent.Type type() {
            return LoggedChatEvent.Type.SYSTEM;
        }
    }
}

