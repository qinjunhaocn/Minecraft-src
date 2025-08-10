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

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ListOperation;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetWritableBookPagesFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetWritableBookPagesFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetWritableBookPagesFunction.commonFields($$02).and($$02.group((App)WritableBookContent.PAGES_CODEC.fieldOf("pages").forGetter($$0 -> $$0.pages), (App)ListOperation.codec(100).forGetter($$0 -> $$0.pageOperation))).apply((Applicative)$$02, SetWritableBookPagesFunction::new));
    private final List<Filterable<String>> pages;
    private final ListOperation pageOperation;

    protected SetWritableBookPagesFunction(List<LootItemCondition> $$0, List<Filterable<String>> $$1, ListOperation $$2) {
        super($$0);
        this.pages = $$1;
        this.pageOperation = $$2;
    }

    @Override
    protected ItemStack run(ItemStack $$0, LootContext $$1) {
        $$0.update(DataComponents.WRITABLE_BOOK_CONTENT, WritableBookContent.EMPTY, this::apply);
        return $$0;
    }

    public WritableBookContent apply(WritableBookContent $$0) {
        List<Filterable<String>> $$1 = this.pageOperation.apply($$0.pages(), this.pages, 100);
        return $$0.withReplacedPages((List)$$1);
    }

    public LootItemFunctionType<SetWritableBookPagesFunction> getType() {
        return LootItemFunctions.SET_WRITABLE_BOOK_PAGES;
    }
}

