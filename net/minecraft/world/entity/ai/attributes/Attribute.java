/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.MatchException
 */
package net.minecraft.world.entity.ai.attributes;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class Attribute {
    public static final Codec<Holder<Attribute>> CODEC = BuiltInRegistries.ATTRIBUTE.holderByNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Attribute>> STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE);
    private final double defaultValue;
    private boolean syncable;
    private final String descriptionId;
    private Sentiment sentiment = Sentiment.POSITIVE;

    protected Attribute(String $$0, double $$1) {
        this.defaultValue = $$1;
        this.descriptionId = $$0;
    }

    public double getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isClientSyncable() {
        return this.syncable;
    }

    public Attribute setSyncable(boolean $$0) {
        this.syncable = $$0;
        return this;
    }

    public Attribute setSentiment(Sentiment $$0) {
        this.sentiment = $$0;
        return this;
    }

    public double sanitizeValue(double $$0) {
        return $$0;
    }

    public String getDescriptionId() {
        return this.descriptionId;
    }

    public ChatFormatting getStyle(boolean $$0) {
        return this.sentiment.getStyle($$0);
    }

    public static final class Sentiment
    extends Enum<Sentiment> {
        public static final /* enum */ Sentiment POSITIVE = new Sentiment();
        public static final /* enum */ Sentiment NEUTRAL = new Sentiment();
        public static final /* enum */ Sentiment NEGATIVE = new Sentiment();
        private static final /* synthetic */ Sentiment[] $VALUES;

        public static Sentiment[] values() {
            return (Sentiment[])$VALUES.clone();
        }

        public static Sentiment valueOf(String $$0) {
            return Enum.valueOf(Sentiment.class, $$0);
        }

        public ChatFormatting getStyle(boolean $$0) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> {
                    if ($$0) {
                        yield ChatFormatting.BLUE;
                    }
                    yield ChatFormatting.RED;
                }
                case 1 -> ChatFormatting.GRAY;
                case 2 -> $$0 ? ChatFormatting.RED : ChatFormatting.BLUE;
            };
        }

        private static /* synthetic */ Sentiment[] a() {
            return new Sentiment[]{POSITIVE, NEUTRAL, NEGATIVE};
        }

        static {
            $VALUES = Sentiment.a();
        }
    }
}

