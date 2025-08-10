/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package net.minecraft.client.model.geom.builders;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;

public class PartDefinition {
    private final List<CubeDefinition> cubes;
    private final PartPose partPose;
    private final Map<String, PartDefinition> children = Maps.newHashMap();

    PartDefinition(List<CubeDefinition> $$0, PartPose $$1) {
        this.cubes = $$0;
        this.partPose = $$1;
    }

    public PartDefinition addOrReplaceChild(String $$0, CubeListBuilder $$1, PartPose $$2) {
        PartDefinition $$3 = new PartDefinition($$1.getCubes(), $$2);
        return this.addOrReplaceChild($$0, $$3);
    }

    public PartDefinition addOrReplaceChild(String $$0, PartDefinition $$1) {
        PartDefinition $$2 = this.children.put($$0, $$1);
        if ($$2 != null) {
            $$1.children.putAll($$2.children);
        }
        return $$1;
    }

    public PartDefinition clearChild(String $$0) {
        PartDefinition $$1 = this.children.get($$0);
        if ($$1 == null) {
            throw new IllegalArgumentException("No child with name: " + $$0);
        }
        return this.addOrReplaceChild($$0, CubeListBuilder.create(), $$1.partPose);
    }

    public ModelPart bake(int $$02, int $$12) {
        Object2ObjectArrayMap $$22 = this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, $$2 -> ((PartDefinition)$$2.getValue()).bake($$02, $$12), ($$0, $$1) -> $$0, Object2ObjectArrayMap::new));
        List $$3 = this.cubes.stream().map($$2 -> $$2.bake($$02, $$12)).toList();
        ModelPart $$4 = new ModelPart($$3, (Map<String, ModelPart>)$$22);
        $$4.setInitialPose(this.partPose);
        $$4.loadPose(this.partPose);
        return $$4;
    }

    public PartDefinition getChild(String $$0) {
        return this.children.get($$0);
    }

    public Set<Map.Entry<String, PartDefinition>> getChildren() {
        return this.children.entrySet();
    }

    public PartDefinition transformed(UnaryOperator<PartPose> $$0) {
        PartDefinition $$1 = new PartDefinition(this.cubes, (PartPose)((Object)$$0.apply(this.partPose)));
        $$1.children.putAll(this.children);
        return $$1;
    }
}

