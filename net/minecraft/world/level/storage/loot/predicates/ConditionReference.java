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
package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.slf4j.Logger;

public record ConditionReference(ResourceKey<LootItemCondition> name) implements LootItemCondition
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<ConditionReference> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceKey.codec(Registries.PREDICATE).fieldOf("name").forGetter(ConditionReference::name)).apply((Applicative)$$0, ConditionReference::new));

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.REFERENCE;
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
        LootItemCondition.super.validate($$0);
        $$0.resolver().get(this.name).ifPresentOrElse($$1 -> ((LootItemCondition)$$1.value()).validate($$0.enterElement(new ProblemReporter.ElementReferencePathElement(this.name), this.name)), () -> $$0.reportProblem(new ValidationContext.MissingReferenceProblem(this.name)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean test(LootContext $$0) {
        LootItemCondition $$1 = $$0.getResolver().get(this.name).map(Holder.Reference::value).orElse(null);
        if ($$1 == null) {
            LOGGER.warn("Tried using unknown condition table called {}", (Object)this.name.location());
            return false;
        }
        LootContext.VisitedEntry<LootItemCondition> $$2 = LootContext.createVisitedEntry($$1);
        if ($$0.pushVisitedElement($$2)) {
            try {
                boolean bl = $$1.test($$0);
                return bl;
            } finally {
                $$0.popVisitedElement($$2);
            }
        }
        LOGGER.warn("Detected infinite loop in loot tables");
        return false;
    }

    public static LootItemCondition.Builder conditionReference(ResourceKey<LootItemCondition> $$0) {
        return () -> new ConditionReference($$0);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }
}

