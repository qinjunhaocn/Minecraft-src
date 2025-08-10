/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.server.PlayerAdvancements;

public class ImpossibleTrigger
implements CriterionTrigger<TriggerInstance> {
    @Override
    public void addPlayerListener(PlayerAdvancements $$0, CriterionTrigger.Listener<TriggerInstance> $$1) {
    }

    @Override
    public void removePlayerListener(PlayerAdvancements $$0, CriterionTrigger.Listener<TriggerInstance> $$1) {
    }

    @Override
    public void removePlayerListeners(PlayerAdvancements $$0) {
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance() implements CriterionTriggerInstance
    {
        public static final Codec<TriggerInstance> CODEC = Codec.unit((Object)new TriggerInstance());

        @Override
        public void validate(CriterionValidator $$0) {
        }
    }
}

