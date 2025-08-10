/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;

public record BabyModelTransform(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale, float babyBodyScale, float bodyYOffset, Set<String> headParts) implements MeshTransformer
{
    public BabyModelTransform(Set<String> $$0) {
        this(false, 5.0f, 2.0f, $$0);
    }

    public BabyModelTransform(boolean $$0, float $$1, float $$2, Set<String> $$3) {
        this($$0, $$1, $$2, 2.0f, 2.0f, 24.0f, $$3);
    }

    @Override
    public MeshDefinition apply(MeshDefinition $$0) {
        float $$12 = this.scaleHead ? 1.5f / this.babyHeadScale : 1.0f;
        float $$2 = 1.0f / this.babyBodyScale;
        UnaryOperator $$3 = $$1 -> $$1.translated(0.0f, this.babyYHeadOffset, this.babyZHeadOffset).scaled($$12);
        UnaryOperator $$4 = $$1 -> $$1.translated(0.0f, this.bodyYOffset, 0.0f).scaled($$2);
        MeshDefinition $$5 = new MeshDefinition();
        for (Map.Entry<String, PartDefinition> $$6 : $$0.getRoot().getChildren()) {
            String $$7 = $$6.getKey();
            PartDefinition $$8 = $$6.getValue();
            $$5.getRoot().addOrReplaceChild($$7, $$8.transformed(this.headParts.contains($$7) ? $$3 : $$4));
        }
        return $$5;
    }
}

