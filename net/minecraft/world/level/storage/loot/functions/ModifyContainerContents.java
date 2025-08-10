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
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ModifyContainerContents
extends LootItemConditionalFunction {
    public static final MapCodec<ModifyContainerContents> CODEC = RecordCodecBuilder.mapCodec($$02 -> ModifyContainerContents.commonFields($$02).and($$02.group((App)ContainerComponentManipulators.CODEC.fieldOf("component").forGetter($$0 -> $$0.component), (App)LootItemFunctions.ROOT_CODEC.fieldOf("modifier").forGetter($$0 -> $$0.modifier))).apply((Applicative)$$02, ModifyContainerContents::new));
    private final ContainerComponentManipulator<?> component;
    private final LootItemFunction modifier;

    private ModifyContainerContents(List<LootItemCondition> $$0, ContainerComponentManipulator<?> $$1, LootItemFunction $$2) {
        super($$0);
        this.component = $$1;
        this.modifier = $$2;
    }

    public LootItemFunctionType<ModifyContainerContents> getType() {
        return LootItemFunctions.MODIFY_CONTENTS;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$12) {
        if ($$0.isEmpty()) {
            return $$0;
        }
        this.component.modifyItems($$0, $$1 -> (ItemStack)this.modifier.apply($$1, $$12));
        return $$0;
    }

    @Override
    public void validate(ValidationContext $$0) {
        super.validate($$0);
        this.modifier.validate($$0.forChild(new ProblemReporter.FieldPathElement("modifier")));
    }
}

