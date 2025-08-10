/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.advancements.critereon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public record TagPredicate<T>(TagKey<T> tag, boolean expected) {
    public static <T> Codec<TagPredicate<T>> codec(ResourceKey<? extends Registry<T>> $$0) {
        return RecordCodecBuilder.create($$1 -> $$1.group((App)TagKey.codec($$0).fieldOf("id").forGetter(TagPredicate::tag), (App)Codec.BOOL.fieldOf("expected").forGetter(TagPredicate::expected)).apply((Applicative)$$1, TagPredicate::new));
    }

    public static <T> TagPredicate<T> is(TagKey<T> $$0) {
        return new TagPredicate<T>($$0, true);
    }

    public static <T> TagPredicate<T> isNot(TagKey<T> $$0) {
        return new TagPredicate<T>($$0, false);
    }

    public boolean matches(Holder<T> $$0) {
        return $$0.is(this.tag) == this.expected;
    }
}

