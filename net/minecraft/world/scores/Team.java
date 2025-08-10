/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.scores;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public abstract class Team {
    public boolean isAlliedTo(@Nullable Team $$0) {
        if ($$0 == null) {
            return false;
        }
        return this == $$0;
    }

    public abstract String getName();

    public abstract MutableComponent getFormattedName(Component var1);

    public abstract boolean canSeeFriendlyInvisibles();

    public abstract boolean isAllowFriendlyFire();

    public abstract Visibility getNameTagVisibility();

    public abstract ChatFormatting getColor();

    public abstract Collection<String> getPlayers();

    public abstract Visibility getDeathMessageVisibility();

    public abstract CollisionRule getCollisionRule();

    public static final class CollisionRule
    extends Enum<CollisionRule>
    implements StringRepresentable {
        public static final /* enum */ CollisionRule ALWAYS = new CollisionRule("always", 0);
        public static final /* enum */ CollisionRule NEVER = new CollisionRule("never", 1);
        public static final /* enum */ CollisionRule PUSH_OTHER_TEAMS = new CollisionRule("pushOtherTeams", 2);
        public static final /* enum */ CollisionRule PUSH_OWN_TEAM = new CollisionRule("pushOwnTeam", 3);
        public static final Codec<CollisionRule> CODEC;
        private static final IntFunction<CollisionRule> BY_ID;
        public static final StreamCodec<ByteBuf, CollisionRule> STREAM_CODEC;
        public final String name;
        public final int id;
        private static final /* synthetic */ CollisionRule[] $VALUES;

        public static CollisionRule[] values() {
            return (CollisionRule[])$VALUES.clone();
        }

        public static CollisionRule valueOf(String $$0) {
            return Enum.valueOf(CollisionRule.class, $$0);
        }

        private CollisionRule(String $$0, int $$1) {
            this.name = $$0;
            this.id = $$1;
        }

        public Component getDisplayName() {
            return Component.translatable("team.collision." + this.name);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ CollisionRule[] b() {
            return new CollisionRule[]{ALWAYS, NEVER, PUSH_OTHER_TEAMS, PUSH_OWN_TEAM};
        }

        static {
            $VALUES = CollisionRule.b();
            CODEC = StringRepresentable.fromEnum(CollisionRule::values);
            BY_ID = ByIdMap.a($$0 -> $$0.id, CollisionRule.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
            STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
        }
    }

    public static final class Visibility
    extends Enum<Visibility>
    implements StringRepresentable {
        public static final /* enum */ Visibility ALWAYS = new Visibility("always", 0);
        public static final /* enum */ Visibility NEVER = new Visibility("never", 1);
        public static final /* enum */ Visibility HIDE_FOR_OTHER_TEAMS = new Visibility("hideForOtherTeams", 2);
        public static final /* enum */ Visibility HIDE_FOR_OWN_TEAM = new Visibility("hideForOwnTeam", 3);
        public static final Codec<Visibility> CODEC;
        private static final IntFunction<Visibility> BY_ID;
        public static final StreamCodec<ByteBuf, Visibility> STREAM_CODEC;
        public final String name;
        public final int id;
        private static final /* synthetic */ Visibility[] $VALUES;

        public static Visibility[] values() {
            return (Visibility[])$VALUES.clone();
        }

        public static Visibility valueOf(String $$0) {
            return Enum.valueOf(Visibility.class, $$0);
        }

        private Visibility(String $$0, int $$1) {
            this.name = $$0;
            this.id = $$1;
        }

        public Component getDisplayName() {
            return Component.translatable("team.visibility." + this.name);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Visibility[] b() {
            return new Visibility[]{ALWAYS, NEVER, HIDE_FOR_OTHER_TEAMS, HIDE_FOR_OWN_TEAM};
        }

        static {
            $VALUES = Visibility.b();
            CODEC = StringRepresentable.fromEnum(Visibility::values);
            BY_ID = ByIdMap.a($$0 -> $$0.id, Visibility.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
            STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
        }
    }
}

