/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public interface SpecialModelRenderer<T> {
    public void render(@Nullable T var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, boolean var7);

    public void getExtents(Set<Vector3f> var1);

    @Nullable
    public T extractArgument(ItemStack var1);

    public static interface Unbaked {
        @Nullable
        public SpecialModelRenderer<?> bake(EntityModelSet var1);

        public MapCodec<? extends Unbaked> type();
    }
}

