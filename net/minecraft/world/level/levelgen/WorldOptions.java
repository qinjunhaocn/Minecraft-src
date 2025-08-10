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
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.StringUtils;

public class WorldOptions {
    public static final MapCodec<WorldOptions> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.LONG.fieldOf("seed").stable().forGetter(WorldOptions::seed), (App)Codec.BOOL.fieldOf("generate_features").orElse((Object)true).stable().forGetter(WorldOptions::generateStructures), (App)Codec.BOOL.fieldOf("bonus_chest").orElse((Object)false).stable().forGetter(WorldOptions::generateBonusChest), (App)Codec.STRING.lenientOptionalFieldOf("legacy_custom_options").stable().forGetter($$0 -> $$0.legacyCustomOptions)).apply((Applicative)$$02, $$02.stable(WorldOptions::new)));
    public static final WorldOptions DEMO_OPTIONS = new WorldOptions("North Carolina".hashCode(), true, true);
    private final long seed;
    private final boolean generateStructures;
    private final boolean generateBonusChest;
    private final Optional<String> legacyCustomOptions;

    public WorldOptions(long $$0, boolean $$1, boolean $$2) {
        this($$0, $$1, $$2, Optional.empty());
    }

    public static WorldOptions defaultWithRandomSeed() {
        return new WorldOptions(WorldOptions.randomSeed(), true, false);
    }

    public static WorldOptions testWorldWithRandomSeed() {
        return new WorldOptions(WorldOptions.randomSeed(), false, false);
    }

    private WorldOptions(long $$0, boolean $$1, boolean $$2, Optional<String> $$3) {
        this.seed = $$0;
        this.generateStructures = $$1;
        this.generateBonusChest = $$2;
        this.legacyCustomOptions = $$3;
    }

    public long seed() {
        return this.seed;
    }

    public boolean generateStructures() {
        return this.generateStructures;
    }

    public boolean generateBonusChest() {
        return this.generateBonusChest;
    }

    public boolean isOldCustomizedWorld() {
        return this.legacyCustomOptions.isPresent();
    }

    public WorldOptions withBonusChest(boolean $$0) {
        return new WorldOptions(this.seed, this.generateStructures, $$0, this.legacyCustomOptions);
    }

    public WorldOptions withStructures(boolean $$0) {
        return new WorldOptions(this.seed, $$0, this.generateBonusChest, this.legacyCustomOptions);
    }

    public WorldOptions withSeed(OptionalLong $$0) {
        return new WorldOptions($$0.orElse(WorldOptions.randomSeed()), this.generateStructures, this.generateBonusChest, this.legacyCustomOptions);
    }

    public static OptionalLong parseSeed(String $$0) {
        if (StringUtils.isEmpty($$0 = $$0.trim())) {
            return OptionalLong.empty();
        }
        try {
            return OptionalLong.of(Long.parseLong($$0));
        } catch (NumberFormatException $$1) {
            return OptionalLong.of($$0.hashCode());
        }
    }

    public static long randomSeed() {
        return RandomSource.create().nextLong();
    }
}

