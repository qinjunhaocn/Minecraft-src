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
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBookCoverFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetBookCoverFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetBookCoverFunction.commonFields($$02).and($$02.group((App)Filterable.codec(Codec.string((int)0, (int)32)).optionalFieldOf("title").forGetter($$0 -> $$0.title), (App)Codec.STRING.optionalFieldOf("author").forGetter($$0 -> $$0.author), (App)ExtraCodecs.intRange(0, 3).optionalFieldOf("generation").forGetter($$0 -> $$0.generation))).apply((Applicative)$$02, SetBookCoverFunction::new));
    private final Optional<String> author;
    private final Optional<Filterable<String>> title;
    private final Optional<Integer> generation;

    public SetBookCoverFunction(List<LootItemCondition> $$0, Optional<Filterable<String>> $$1, Optional<String> $$2, Optional<Integer> $$3) {
        super($$0);
        this.author = $$2;
        this.title = $$1;
        this.generation = $$3;
    }

    @Override
    protected ItemStack run(ItemStack $$0, LootContext $$1) {
        $$0.update(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY, this::apply);
        return $$0;
    }

    private WrittenBookContent apply(WrittenBookContent $$0) {
        return new WrittenBookContent(this.title.orElseGet($$0::title), this.author.orElseGet($$0::author), this.generation.orElseGet($$0::generation), $$0.pages(), $$0.resolved());
    }

    public LootItemFunctionType<SetBookCoverFunction> getType() {
        return LootItemFunctions.SET_BOOK_COVER;
    }
}

