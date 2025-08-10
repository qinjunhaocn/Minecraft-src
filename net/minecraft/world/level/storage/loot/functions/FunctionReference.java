/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class FunctionReference
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<FunctionReference> CODEC = RecordCodecBuilder.mapCodec($$02 -> FunctionReference.commonFields($$02).and((App)ResourceKey.codec(Registries.ITEM_MODIFIER).fieldOf("name").forGetter($$0 -> $$0.name)).apply((Applicative)$$02, FunctionReference::new));
    private final ResourceKey<LootItemFunction> name;

    private FunctionReference(List<LootItemCondition> $$0, ResourceKey<LootItemFunction> $$1) {
        super($$0);
        this.name = $$1;
    }

    public LootItemFunctionType<FunctionReference> getType() {
        return LootItemFunctions.REFERENCE;
    }

    @Override
    public void validate(ValidationContext $$0) {
        if (!$$0.allowsReferences()) {
            $$0.reportProblem(new ValidationContext.ReferenceNotAllowedProblem(this.name));
            return;
        }
        if ($$0.hasVisitedElement(this.name)) {
            $$0.reportProblem(new ValidationContext.RecursiveReferenceProblem(this.name));
            return;
        }
        super.validate($$0);
        $$0.resolver().get(this.name).ifPresentOrElse($$1 -> ((LootItemFunction)$$1.value()).validate($$0.enterElement(new ProblemReporter.ElementReferencePathElement(this.name), this.name)), () -> $$0.reportProblem(new ValidationContext.MissingReferenceProblem(this.name)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ItemStack run(ItemStack $$0, LootContext $$1) {
        LootItemFunction $$2 = $$1.getResolver().get(this.name).map(Holder::value).orElse(null);
        if ($$2 == null) {
            LOGGER.warn("Unknown function: {}", (Object)this.name.location());
            return $$0;
        }
        LootContext.VisitedEntry<LootItemFunction> $$3 = LootContext.createVisitedEntry($$2);
        if ($$1.pushVisitedElement($$3)) {
            try {
                ItemStack itemStack = (ItemStack)$$2.apply($$0, $$1);
                return itemStack;
            } finally {
                $$1.popVisitedElement($$3);
            }
        }
        LOGGER.warn("Detected infinite loop in loot tables");
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> functionReference(ResourceKey<LootItemFunction> $$0) {
        return FunctionReference.simpleBuilder($$1 -> new FunctionReference((List<LootItemCondition>)$$1, $$0));
    }
}

