/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package com.mojang.blaze3d;

import com.mojang.blaze3d.vertex.VertexSorting;
import org.joml.Matrix4f;

public final class ProjectionType
extends Enum<ProjectionType> {
    public static final /* enum */ ProjectionType PERSPECTIVE = new ProjectionType(VertexSorting.DISTANCE_TO_ORIGIN, ($$0, $$1) -> $$0.scale(1.0f - $$1 / 4096.0f));
    public static final /* enum */ ProjectionType ORTHOGRAPHIC = new ProjectionType(VertexSorting.ORTHOGRAPHIC_Z, ($$0, $$1) -> $$0.translate(0.0f, 0.0f, $$1 / 512.0f));
    private final VertexSorting vertexSorting;
    private final LayeringTransform layeringTransform;
    private static final /* synthetic */ ProjectionType[] $VALUES;

    public static ProjectionType[] values() {
        return (ProjectionType[])$VALUES.clone();
    }

    public static ProjectionType valueOf(String $$0) {
        return Enum.valueOf(ProjectionType.class, $$0);
    }

    private ProjectionType(VertexSorting $$0, LayeringTransform $$1) {
        this.vertexSorting = $$0;
        this.layeringTransform = $$1;
    }

    public VertexSorting vertexSorting() {
        return this.vertexSorting;
    }

    public void applyLayeringTransform(Matrix4f $$0, float $$1) {
        this.layeringTransform.apply($$0, $$1);
    }

    private static /* synthetic */ ProjectionType[] b() {
        return new ProjectionType[]{PERSPECTIVE, ORTHOGRAPHIC};
    }

    static {
        $VALUES = ProjectionType.b();
    }

    @FunctionalInterface
    static interface LayeringTransform {
        public void apply(Matrix4f var1, float var2);
    }
}

