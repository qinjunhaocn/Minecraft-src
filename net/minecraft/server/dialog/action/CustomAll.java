/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dialog.action;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.action.Action;

public record CustomAll(ResourceLocation id, Optional<CompoundTag> additions) implements Action
{
    public static final MapCodec<CustomAll> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("id").forGetter(CustomAll::id), (App)CompoundTag.CODEC.optionalFieldOf("additions").forGetter(CustomAll::additions)).apply((Applicative)$$0, CustomAll::new));

    public MapCodec<CustomAll> codec() {
        return MAP_CODEC;
    }

    @Override
    public Optional<ClickEvent> createAction(Map<String, Action.ValueGetter> $$0) {
        CompoundTag $$12 = this.additions.map(CompoundTag::copy).orElseGet(CompoundTag::new);
        $$0.forEach(($$1, $$2) -> $$12.put((String)$$1, $$2.asTag()));
        return Optional.of(new ClickEvent.Custom(this.id, Optional.of($$12)));
    }
}

