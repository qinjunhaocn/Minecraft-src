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
package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record IsKeybindDown(KeyMapping keybind) implements ConditionalItemModelProperty
{
    private static final Codec<KeyMapping> KEYBIND_CODEC = Codec.STRING.comapFlatMap($$0 -> {
        KeyMapping $$1 = KeyMapping.get($$0);
        return $$1 != null ? DataResult.success((Object)$$1) : DataResult.error(() -> "Invalid keybind: " + $$0);
    }, KeyMapping::getName);
    public static final MapCodec<IsKeybindDown> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)KEYBIND_CODEC.fieldOf("keybind").forGetter(IsKeybindDown::keybind)).apply((Applicative)$$0, IsKeybindDown::new));

    @Override
    public boolean get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3, ItemDisplayContext $$4) {
        return this.keybind.isDown();
    }

    public MapCodec<IsKeybindDown> type() {
        return MAP_CODEC;
    }
}

