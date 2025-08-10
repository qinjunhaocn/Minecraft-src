/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ListOperation;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetWrittenBookPagesFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetWrittenBookPagesFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetWrittenBookPagesFunction.commonFields($$02).and($$02.group((App)WrittenBookContent.PAGES_CODEC.fieldOf("pages").forGetter($$0 -> $$0.pages), (App)ListOperation.UNLIMITED_CODEC.forGetter($$0 -> $$0.pageOperation))).apply((Applicative)$$02, SetWrittenBookPagesFunction::new));
    private final List<Filterable<Component>> pages;
    private final ListOperation pageOperation;

    protected SetWrittenBookPagesFunction(List<LootItemCondition> $$0, List<Filterable<Component>> $$1, ListOperation $$2) {
        super($$0);
        this.pages = $$1;
        this.pageOperation = $$2;
    }

    @Override
    protected ItemStack run(ItemStack $$0, LootContext $$1) {
        $$0.update(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY, this::apply);
        return $$0;
    }

    @VisibleForTesting
    public WrittenBookContent apply(WrittenBookContent $$0) {
        List<Filterable<Component>> $$1 = this.pageOperation.apply($$0.pages(), this.pages);
        return $$0.withReplacedPages((List)$$1);
    }

    public LootItemFunctionType<SetWrittenBookPagesFunction> getType() {
        return LootItemFunctions.SET_WRITTEN_BOOK_PAGES;
    }
}

