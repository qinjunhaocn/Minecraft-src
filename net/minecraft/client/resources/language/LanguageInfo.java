/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.resources.language;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

public record LanguageInfo(String region, String name, boolean bidirectional) {
    public static final Codec<LanguageInfo> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.NON_EMPTY_STRING.fieldOf("region").forGetter(LanguageInfo::region), (App)ExtraCodecs.NON_EMPTY_STRING.fieldOf("name").forGetter(LanguageInfo::name), (App)Codec.BOOL.optionalFieldOf("bidirectional", (Object)false).forGetter(LanguageInfo::bidirectional)).apply((Applicative)$$0, LanguageInfo::new));

    public Component toComponent() {
        return Component.literal(this.name + " (" + this.region + ")");
    }
}

