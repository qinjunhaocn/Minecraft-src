/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public class BlockStateArgument
implements ArgumentType<BlockInput> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");
    private final HolderLookup<Block> blocks;

    public BlockStateArgument(CommandBuildContext $$0) {
        this.blocks = $$0.lookupOrThrow(Registries.BLOCK);
    }

    public static BlockStateArgument block(CommandBuildContext $$0) {
        return new BlockStateArgument($$0);
    }

    public BlockInput parse(StringReader $$0) throws CommandSyntaxException {
        BlockStateParser.BlockResult $$1 = BlockStateParser.parseForBlock(this.blocks, $$0, true);
        return new BlockInput($$1.blockState(), $$1.properties().keySet(), $$1.nbt());
    }

    public static BlockInput getBlock(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (BlockInput)$$0.getArgument($$1, BlockInput.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
        return BlockStateParser.fillSuggestions(this.blocks, $$1, false, true);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

