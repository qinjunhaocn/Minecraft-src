/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class DataPackConfig {
    public static final DataPackConfig DEFAULT = new DataPackConfig(ImmutableList.of("vanilla"), ImmutableList.of());
    public static final Codec<DataPackConfig> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.STRING.listOf().fieldOf("Enabled").forGetter($$0 -> $$0.enabled), (App)Codec.STRING.listOf().fieldOf("Disabled").forGetter($$0 -> $$0.disabled)).apply((Applicative)$$02, DataPackConfig::new));
    private final List<String> enabled;
    private final List<String> disabled;

    public DataPackConfig(List<String> $$0, List<String> $$1) {
        this.enabled = ImmutableList.copyOf($$0);
        this.disabled = ImmutableList.copyOf($$1);
    }

    public List<String> getEnabled() {
        return this.enabled;
    }

    public List<String> getDisabled() {
        return this.disabled;
    }
}

