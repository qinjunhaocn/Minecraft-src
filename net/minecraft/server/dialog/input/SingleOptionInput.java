/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dialog.input;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.input.InputControl;
import net.minecraft.util.ExtraCodecs;

public record SingleOptionInput(int width, List<Entry> entries, Component label, boolean labelVisible) implements InputControl
{
    public static final MapCodec<SingleOptionInput> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Dialog.WIDTH_CODEC.optionalFieldOf("width", (Object)200).forGetter(SingleOptionInput::width), (App)ExtraCodecs.nonEmptyList(Entry.CODEC.listOf()).fieldOf("options").forGetter(SingleOptionInput::entries), (App)ComponentSerialization.CODEC.fieldOf("label").forGetter(SingleOptionInput::label), (App)Codec.BOOL.optionalFieldOf("label_visible", (Object)true).forGetter(SingleOptionInput::labelVisible)).apply((Applicative)$$0, SingleOptionInput::new)).validate($$0 -> {
        long $$1 = $$0.entries.stream().filter(Entry::initial).count();
        if ($$1 > 1L) {
            return DataResult.error(() -> "Multiple initial values");
        }
        return DataResult.success((Object)$$0);
    });

    public MapCodec<SingleOptionInput> mapCodec() {
        return MAP_CODEC;
    }

    public Optional<Entry> initial() {
        return this.entries.stream().filter(Entry::initial).findFirst();
    }

    public record Entry(String id, Optional<Component> display, boolean initial) {
        public static final Codec<Entry> FULL_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.STRING.fieldOf("id").forGetter(Entry::id), (App)ComponentSerialization.CODEC.optionalFieldOf("display").forGetter(Entry::display), (App)Codec.BOOL.optionalFieldOf("initial", (Object)false).forGetter(Entry::initial)).apply((Applicative)$$0, Entry::new));
        public static final Codec<Entry> CODEC = Codec.withAlternative(FULL_CODEC, (Codec)Codec.STRING, $$0 -> new Entry((String)$$0, Optional.empty(), false));

        public Component displayOrDefault() {
            return this.display.orElseGet(() -> Component.literal(this.id));
        }
    }
}

