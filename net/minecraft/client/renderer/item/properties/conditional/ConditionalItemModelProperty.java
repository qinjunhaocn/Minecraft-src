/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.conditional.ItemModelPropertyTest;

public interface ConditionalItemModelProperty
extends ItemModelPropertyTest {
    public MapCodec<? extends ConditionalItemModelProperty> type();
}

