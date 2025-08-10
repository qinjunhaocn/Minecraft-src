/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.util.RegistryContextSwapper;

public record ClientItem(ItemModel.Unbaked model, Properties properties, @Nullable RegistryContextSwapper registrySwapper) {
    public static final Codec<ClientItem> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ItemModels.CODEC.fieldOf("model").forGetter(ClientItem::model), (App)Properties.MAP_CODEC.forGetter(ClientItem::properties)).apply((Applicative)$$0, ClientItem::new));

    public ClientItem(ItemModel.Unbaked $$0, Properties $$1) {
        this($$0, $$1, null);
    }

    public ClientItem withRegistrySwapper(RegistryContextSwapper $$0) {
        return new ClientItem(this.model, this.properties, $$0);
    }

    @Nullable
    public RegistryContextSwapper registrySwapper() {
        return this.registrySwapper;
    }

    public record Properties(boolean handAnimationOnSwap, boolean oversizedInGui) {
        public static final Properties DEFAULT = new Properties(true, false);
        public static final MapCodec<Properties> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.BOOL.optionalFieldOf("hand_animation_on_swap", (Object)true).forGetter(Properties::handAnimationOnSwap), (App)Codec.BOOL.optionalFieldOf("oversized_in_gui", (Object)false).forGetter(Properties::oversizedInGui)).apply((Applicative)$$0, Properties::new));
    }
}

