/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.world.scores;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public interface ScoreHolder {
    public static final String WILDCARD_NAME = "*";
    public static final ScoreHolder WILDCARD = new ScoreHolder(){

        @Override
        public String getScoreboardName() {
            return ScoreHolder.WILDCARD_NAME;
        }
    };

    public String getScoreboardName();

    @Nullable
    default public Component getDisplayName() {
        return null;
    }

    default public Component getFeedbackDisplayName() {
        Component $$02 = this.getDisplayName();
        if ($$02 != null) {
            return $$02.copy().withStyle($$0 -> $$0.withHoverEvent(new HoverEvent.ShowText(Component.literal(this.getScoreboardName()))));
        }
        return Component.literal(this.getScoreboardName());
    }

    public static ScoreHolder forNameOnly(final String $$0) {
        if ($$0.equals(WILDCARD_NAME)) {
            return WILDCARD;
        }
        final MutableComponent $$1 = Component.literal($$0);
        return new ScoreHolder(){

            @Override
            public String getScoreboardName() {
                return $$0;
            }

            @Override
            public Component getFeedbackDisplayName() {
                return $$1;
            }
        };
    }

    public static ScoreHolder fromGameProfile(GameProfile $$0) {
        final String $$1 = $$0.getName();
        return new ScoreHolder(){

            @Override
            public String getScoreboardName() {
                return $$1;
            }
        };
    }
}

