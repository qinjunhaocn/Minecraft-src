/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.resources.model;

import com.mojang.math.Quadrant;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.SimpleUnbakedGeometry;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class MissingBlockModel {
    private static final String TEXTURE_SLOT = "missingno";
    public static final ResourceLocation LOCATION = ResourceLocation.withDefaultNamespace("builtin/missing");

    public static UnbakedModel missingModel() {
        BlockElementFace.UVs $$0 = new BlockElementFace.UVs(0.0f, 0.0f, 16.0f, 16.0f);
        Map<Direction, BlockElementFace> $$12 = Util.makeEnumMap(Direction.class, $$1 -> new BlockElementFace((Direction)$$1, -1, TEXTURE_SLOT, $$0, Quadrant.R0));
        BlockElement $$2 = new BlockElement((Vector3fc)new Vector3f(0.0f, 0.0f, 0.0f), (Vector3fc)new Vector3f(16.0f, 16.0f, 16.0f), $$12);
        return new BlockModel(new SimpleUnbakedGeometry(List.of((Object)((Object)$$2))), null, null, ItemTransforms.NO_TRANSFORMS, new TextureSlots.Data.Builder().addReference("particle", TEXTURE_SLOT).addTexture(TEXTURE_SLOT, new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation())).build(), null);
    }
}

