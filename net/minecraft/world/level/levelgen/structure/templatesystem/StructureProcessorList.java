/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.List;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

public class StructureProcessorList {
    private final List<StructureProcessor> list;

    public StructureProcessorList(List<StructureProcessor> $$0) {
        this.list = $$0;
    }

    public List<StructureProcessor> list() {
        return this.list;
    }

    public String toString() {
        return "ProcessorList[" + String.valueOf(this.list) + "]";
    }
}

