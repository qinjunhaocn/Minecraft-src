/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.client.resources.model.AtlasIds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

public class MapDecorationTextureManager
extends TextureAtlasHolder {
    public MapDecorationTextureManager(TextureManager $$0) {
        super($$0, ResourceLocation.withDefaultNamespace("textures/atlas/map_decorations.png"), AtlasIds.MAP_DECORATIONS);
    }

    public TextureAtlasSprite get(MapDecoration $$0) {
        return this.getSprite($$0.getSpriteLocation());
    }
}

