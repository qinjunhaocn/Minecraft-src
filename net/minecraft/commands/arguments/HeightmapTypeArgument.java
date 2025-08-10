/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.serialization.Codec
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.Heightmap;

public class HeightmapTypeArgument
extends StringRepresentableArgument<Heightmap.Types> {
    private static final Codec<Heightmap.Types> LOWER_CASE_CODEC = StringRepresentable.fromEnumWithMapping(HeightmapTypeArgument::b, $$0 -> $$0.toLowerCase(Locale.ROOT));

    private static Heightmap.Types[] b() {
        return (Heightmap.Types[])Arrays.stream(Heightmap.Types.values()).filter(Heightmap.Types::keepAfterWorldgen).toArray(Heightmap.Types[]::new);
    }

    private HeightmapTypeArgument() {
        super(LOWER_CASE_CODEC, HeightmapTypeArgument::b);
    }

    public static HeightmapTypeArgument heightmap() {
        return new HeightmapTypeArgument();
    }

    public static Heightmap.Types getHeightmap(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (Heightmap.Types)$$0.getArgument($$1, Heightmap.Types.class);
    }

    @Override
    protected String convertId(String $$0) {
        return $$0.toLowerCase(Locale.ROOT);
    }
}

