/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerLikeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerDataHolderRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.VillagerMetadataSection;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerProfessionLayer<S extends LivingEntityRenderState, M extends EntityModel<S>>
extends RenderLayer<S, M> {
    private static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), $$0 -> {
        $$0.put(1, (Object)ResourceLocation.withDefaultNamespace("stone"));
        $$0.put(2, (Object)ResourceLocation.withDefaultNamespace("iron"));
        $$0.put(3, (Object)ResourceLocation.withDefaultNamespace("gold"));
        $$0.put(4, (Object)ResourceLocation.withDefaultNamespace("emerald"));
        $$0.put(5, (Object)ResourceLocation.withDefaultNamespace("diamond"));
    });
    private final Object2ObjectMap<ResourceKey<VillagerType>, VillagerMetadataSection.Hat> typeHatCache = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<ResourceKey<VillagerProfession>, VillagerMetadataSection.Hat> professionHatCache = new Object2ObjectOpenHashMap();
    private final ResourceManager resourceManager;
    private final String path;

    public VillagerProfessionLayer(RenderLayerParent<S, M> $$0, ResourceManager $$1, String $$2) {
        super($$0);
        this.resourceManager = $$1;
        this.path = $$2;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, S $$3, float $$4, float $$5) {
        if (((LivingEntityRenderState)$$3).isInvisible) {
            return;
        }
        VillagerData $$6 = ((VillagerDataHolderRenderState)$$3).getVillagerData();
        if ($$6 == null) {
            return;
        }
        Holder<VillagerType> $$7 = $$6.type();
        Holder<VillagerProfession> $$8 = $$6.profession();
        VillagerMetadataSection.Hat $$9 = this.getHatData(this.typeHatCache, "type", $$7);
        VillagerMetadataSection.Hat $$10 = this.getHatData(this.professionHatCache, "profession", $$8);
        Object $$11 = this.getParentModel();
        ((VillagerLikeModel)$$11).hatVisible($$10 == VillagerMetadataSection.Hat.NONE || $$10 == VillagerMetadataSection.Hat.PARTIAL && $$9 != VillagerMetadataSection.Hat.FULL);
        ResourceLocation $$12 = this.getResourceLocation("type", $$7);
        VillagerProfessionLayer.renderColoredCutoutModel($$11, $$12, $$0, $$1, $$2, $$3, -1);
        ((VillagerLikeModel)$$11).hatVisible(true);
        if (!$$8.is(VillagerProfession.NONE) && !((LivingEntityRenderState)$$3).isBaby) {
            ResourceLocation $$13 = this.getResourceLocation("profession", $$8);
            VillagerProfessionLayer.renderColoredCutoutModel($$11, $$13, $$0, $$1, $$2, $$3, -1);
            if (!$$8.is(VillagerProfession.NITWIT)) {
                ResourceLocation $$14 = this.getResourceLocation("profession_level", (ResourceLocation)LEVEL_LOCATIONS.get(Mth.clamp($$6.level(), 1, LEVEL_LOCATIONS.size())));
                VillagerProfessionLayer.renderColoredCutoutModel($$11, $$14, $$0, $$1, $$2, $$3, -1);
            }
        }
    }

    private ResourceLocation getResourceLocation(String $$0, ResourceLocation $$12) {
        return $$12.withPath($$1 -> "textures/entity/" + this.path + "/" + $$0 + "/" + $$1 + ".png");
    }

    private ResourceLocation getResourceLocation(String $$0, Holder<?> $$12) {
        return $$12.unwrapKey().map($$1 -> this.getResourceLocation($$0, $$1.location())).orElse(MissingTextureAtlasSprite.getLocation());
    }

    public <K> VillagerMetadataSection.Hat getHatData(Object2ObjectMap<ResourceKey<K>, VillagerMetadataSection.Hat> $$0, String $$1, Holder<K> $$22) {
        ResourceKey $$3 = $$22.unwrapKey().orElse(null);
        if ($$3 == null) {
            return VillagerMetadataSection.Hat.NONE;
        }
        return (VillagerMetadataSection.Hat)$$0.computeIfAbsent((Object)$$3, $$2 -> this.resourceManager.getResource(this.getResourceLocation($$1, $$3.location())).flatMap($$0 -> {
            try {
                return $$0.metadata().getSection(VillagerMetadataSection.TYPE).map(VillagerMetadataSection::hat);
            } catch (IOException $$1) {
                return Optional.empty();
            }
        }).orElse(VillagerMetadataSection.Hat.NONE));
    }
}

