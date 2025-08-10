/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui;

import java.util.Set;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.client.resources.model.AtlasIds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public class GuiSpriteManager
extends TextureAtlasHolder {
    private static final Set<MetadataSectionType<?>> METADATA_SECTIONS = Set.of(AnimationMetadataSection.TYPE, GuiMetadataSection.TYPE);

    public GuiSpriteManager(TextureManager $$0) {
        super($$0, ResourceLocation.withDefaultNamespace("textures/atlas/gui.png"), AtlasIds.GUI, METADATA_SECTIONS);
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation $$0) {
        return super.getSprite($$0);
    }

    public GuiSpriteScaling getSpriteScaling(TextureAtlasSprite $$0) {
        return this.getMetadata($$0).scaling();
    }

    private GuiMetadataSection getMetadata(TextureAtlasSprite $$0) {
        return $$0.contents().metadata().getSection(GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT);
    }
}

