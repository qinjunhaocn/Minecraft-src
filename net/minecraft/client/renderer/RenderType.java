/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4fc
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public abstract class RenderType
extends RenderStateShard {
    private static final int MEGABYTE = 0x100000;
    public static final int BIG_BUFFER_SIZE = 0x400000;
    public static final int SMALL_BUFFER_SIZE = 786432;
    public static final int TRANSIENT_BUFFER_SIZE = 1536;
    private static final RenderType SOLID = RenderType.create("solid", 1536, true, false, RenderPipelines.SOLID, CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));
    private static final RenderType CUTOUT_MIPPED = RenderType.create("cutout_mipped", 1536, true, false, RenderPipelines.CUTOUT_MIPPED, CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));
    private static final RenderType CUTOUT = RenderType.create("cutout", 1536, true, false, RenderPipelines.CUTOUT, CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET).createCompositeState(true));
    private static final RenderType TRANSLUCENT_MOVING_BLOCK = RenderType.create("translucent_moving_block", 786432, false, true, RenderPipelines.TRANSLUCENT_MOVING_BLOCK, CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(true));
    private static final Function<ResourceLocation, RenderType> ARMOR_CUTOUT_NO_CULL = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(true);
        return RenderType.create("armor_cutout_no_cull", 1536, true, false, RenderPipelines.ARMOR_CUTOUT_NO_CULL, $$1);
    });
    private static final Function<ResourceLocation, RenderType> ARMOR_TRANSLUCENT = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(true);
        return RenderType.create("armor_translucent", 1536, true, true, RenderPipelines.ARMOR_TRANSLUCENT, $$1);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_SOLID = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return RenderType.create("entity_solid", 1536, true, false, RenderPipelines.ENTITY_SOLID, $$1);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_SOLID_Z_OFFSET_FORWARD = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING_FORWARD).createCompositeState(true);
        return RenderType.create("entity_solid_z_offset_forward", 1536, true, false, RenderPipelines.ENTITY_SOLID_Z_OFFSET_FORWARD, $$1);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return RenderType.create("entity_cutout", 1536, true, false, RenderPipelines.ENTITY_CUTOUT, $$1);
    });
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL = Util.memoize(($$0, $$1) -> {
        CompositeState $$2 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState((boolean)$$1);
        return RenderType.create("entity_cutout_no_cull", 1536, true, false, RenderPipelines.ENTITY_CUTOUT_NO_CULL, $$2);
    });
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL_Z_OFFSET = Util.memoize(($$0, $$1) -> {
        CompositeState $$2 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState((boolean)$$1);
        return RenderType.create("entity_cutout_no_cull_z_offset", 1536, true, false, RenderPipelines.ENTITY_CUTOUT_NO_CULL_Z_OFFSET, $$2);
    });
    private static final Function<ResourceLocation, RenderType> ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setOutputState(ITEM_ENTITY_TARGET).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return RenderType.create("item_entity_translucent_cull", 1536, true, true, RenderPipelines.ITEM_ENTITY_TRANSLUCENT_CULL, $$1);
    });
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT = Util.memoize(($$0, $$1) -> {
        CompositeState $$2 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState((boolean)$$1);
        return RenderType.create("entity_translucent", 1536, true, true, RenderPipelines.ENTITY_TRANSLUCENT, $$2);
    });
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(($$0, $$1) -> {
        CompositeState $$2 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setOverlayState(OVERLAY).createCompositeState((boolean)$$1);
        return RenderType.create("entity_translucent_emissive", 1536, true, true, RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE, $$2);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_SMOOTH_CUTOUT = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return RenderType.create("entity_smooth_cutout", 1536, RenderPipelines.ENTITY_SMOOTH_CUTOUT, $$1);
    });
    private static final BiFunction<ResourceLocation, Boolean, RenderType> BEACON_BEAM = Util.memoize(($$0, $$1) -> {
        CompositeState $$2 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).createCompositeState(false);
        return RenderType.create("beacon_beam", 1536, false, true, $$1 != false ? RenderPipelines.BEACON_BEAM_TRANSLUCENT : RenderPipelines.BEACON_BEAM_OPAQUE, $$2);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_DECAL = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false);
        return RenderType.create("entity_decal", 1536, RenderPipelines.ENTITY_DECAL, $$1);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_NO_OUTLINE = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false);
        return RenderType.create("entity_no_outline", 1536, false, true, RenderPipelines.ENTITY_NO_OUTLINE, $$1);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_SHADOW = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false);
        return RenderType.create("entity_shadow", 1536, false, false, RenderPipelines.ENTITY_SHADOW, $$1);
    });
    private static final Function<ResourceLocation, RenderType> DRAGON_EXPLOSION_ALPHA = Util.memoize($$0 -> {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).createCompositeState(true);
        return RenderType.create("entity_alpha", 1536, RenderPipelines.DRAGON_EXPLOSION_ALPHA, $$1);
    });
    private static final Function<ResourceLocation, RenderType> EYES = Util.memoize($$0 -> {
        RenderStateShard.TextureStateShard $$1 = new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false);
        return RenderType.create("eyes", 1536, false, true, RenderPipelines.EYES, CompositeState.builder().setTextureState($$1).createCompositeState(false));
    });
    private static final RenderType LEASH = RenderType.create("leash", 1536, RenderPipelines.LEASH, CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false));
    private static final RenderType WATER_MASK = RenderType.create("water_mask", 1536, RenderPipelines.WATER_MASK, CompositeState.builder().setTextureState(NO_TEXTURE).createCompositeState(false));
    private static final RenderType ARMOR_ENTITY_GLINT = RenderType.create("armor_entity_glint", 1536, RenderPipelines.GLINT, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ARMOR, false)).setTexturingState(ARMOR_ENTITY_GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
    private static final RenderType GLINT_TRANSLUCENT = RenderType.create("glint_translucent", 1536, RenderPipelines.GLINT, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, false)).setTexturingState(GLINT_TEXTURING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
    private static final RenderType GLINT = RenderType.create("glint", 1536, RenderPipelines.GLINT, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, false)).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
    private static final RenderType ENTITY_GLINT = RenderType.create("entity_glint", 1536, RenderPipelines.GLINT, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, false)).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
    private static final Function<ResourceLocation, RenderType> CRUMBLING = Util.memoize($$0 -> {
        RenderStateShard.TextureStateShard $$1 = new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false);
        return RenderType.create("crumbling", 1536, false, true, RenderPipelines.CRUMBLING, CompositeState.builder().setTextureState($$1).createCompositeState(false));
    });
    private static final Function<ResourceLocation, RenderType> TEXT = Util.memoize($$0 -> RenderType.create("text", 786432, false, false, RenderPipelines.TEXT, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).createCompositeState(false)));
    private static final RenderType TEXT_BACKGROUND = RenderType.create("text_background", 1536, false, true, RenderPipelines.TEXT_BACKGROUND, CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false));
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY = Util.memoize($$0 -> RenderType.create("text_intensity", 786432, false, false, RenderPipelines.TEXT_INTENSITY, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> TEXT_POLYGON_OFFSET = Util.memoize($$0 -> RenderType.create("text_polygon_offset", 1536, false, true, RenderPipelines.TEXT_POLYGON_OFFSET, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize($$0 -> RenderType.create("text_intensity_polygon_offset", 1536, false, true, RenderPipelines.TEXT_INTENSITY, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> TEXT_SEE_THROUGH = Util.memoize($$0 -> RenderType.create("text_see_through", 1536, false, false, RenderPipelines.TEXT_SEE_THROUGH, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).createCompositeState(false)));
    private static final RenderType TEXT_BACKGROUND_SEE_THROUGH = RenderType.create("text_background_see_through", 1536, false, true, RenderPipelines.TEXT_BACKGROUND_SEE_THROUGH, CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false));
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_SEE_THROUGH = Util.memoize($$0 -> RenderType.create("text_intensity_see_through", 1536, false, true, RenderPipelines.TEXT_INTENSITY_SEE_THROUGH, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).createCompositeState(false)));
    private static final RenderType LIGHTNING = RenderType.create("lightning", 1536, false, true, RenderPipelines.LIGHTNING, CompositeState.builder().setOutputState(WEATHER_TARGET).createCompositeState(false));
    private static final RenderType DRAGON_RAYS = RenderType.create("dragon_rays", 1536, false, false, RenderPipelines.DRAGON_RAYS, CompositeState.builder().createCompositeState(false));
    private static final RenderType DRAGON_RAYS_DEPTH = RenderType.create("dragon_rays_depth", 1536, false, false, RenderPipelines.DRAGON_RAYS_DEPTH, CompositeState.builder().createCompositeState(false));
    private static final RenderType TRIPWIRE = RenderType.create("tripwire", 1536, true, true, RenderPipelines.TRIPWIRE, CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setOutputState(WEATHER_TARGET).createCompositeState(true));
    private static final RenderType END_PORTAL = RenderType.create("end_portal", 1536, false, false, RenderPipelines.END_PORTAL, CompositeState.builder().setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(TheEndPortalRenderer.END_SKY_LOCATION, false).add(TheEndPortalRenderer.END_PORTAL_LOCATION, false).build()).createCompositeState(false));
    private static final RenderType END_GATEWAY = RenderType.create("end_gateway", 1536, false, false, RenderPipelines.END_GATEWAY, CompositeState.builder().setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(TheEndPortalRenderer.END_SKY_LOCATION, false).add(TheEndPortalRenderer.END_PORTAL_LOCATION, false).build()).createCompositeState(false));
    public static final CompositeRenderType LINES = RenderType.create("lines", 1536, RenderPipelines.LINES, CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
    public static final CompositeRenderType SECONDARY_BLOCK_OUTLINE = RenderType.create("secondary_block_outline", 1536, RenderPipelines.SECONDARY_BLOCK_OUTLINE, CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(7.0))).setLayeringState(VIEW_OFFSET_Z_LAYERING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
    public static final CompositeRenderType LINE_STRIP = RenderType.create("line_strip", 1536, RenderPipelines.LINE_STRIP, CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
    private static final Function<Double, CompositeRenderType> DEBUG_LINE_STRIP = Util.memoize($$0 -> RenderType.create("debug_line_strip", 1536, RenderPipelines.DEBUG_LINE_STRIP, CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of($$0))).createCompositeState(false)));
    private static final CompositeRenderType DEBUG_FILLED_BOX = RenderType.create("debug_filled_box", 1536, false, true, RenderPipelines.DEBUG_FILLED_BOX, CompositeState.builder().setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
    private static final CompositeRenderType DEBUG_QUADS = RenderType.create("debug_quads", 1536, false, true, RenderPipelines.DEBUG_QUADS, CompositeState.builder().createCompositeState(false));
    private static final CompositeRenderType DEBUG_TRIANGLE_FAN = RenderType.create("debug_triangle_fan", 1536, false, true, RenderPipelines.DEBUG_TRIANGLE_FAN, CompositeState.builder().createCompositeState(false));
    private static final CompositeRenderType DEBUG_STRUCTURE_QUADS = RenderType.create("debug_structure_quads", 1536, false, true, RenderPipelines.DEBUG_STRUCTURE_QUADS, CompositeState.builder().createCompositeState(false));
    private static final CompositeRenderType DEBUG_SECTION_QUADS = RenderType.create("debug_section_quads", 1536, false, true, RenderPipelines.DEBUG_SECTION_QUADS, CompositeState.builder().setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
    private static final Function<ResourceLocation, RenderType> OPAQUE_PARTICLE = Util.memoize($$0 -> RenderType.create("opaque_particle", 1536, false, false, RenderPipelines.OPAQUE_PARTICLE, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setLightmapState(LIGHTMAP).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> TRANSLUCENT_PARTICLE = Util.memoize($$0 -> RenderType.create("translucent_particle", 1536, false, false, RenderPipelines.TRANSLUCENT_PARTICLE, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setOutputState(PARTICLES_TARGET).setLightmapState(LIGHTMAP).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> WEATHER_DEPTH_WRITE = RenderType.createWeather(RenderPipelines.WEATHER_DEPTH_WRITE);
    private static final Function<ResourceLocation, RenderType> WEATHER_NO_DEPTH_WRITE = RenderType.createWeather(RenderPipelines.WEATHER_NO_DEPTH_WRITE);
    private static final RenderType SUNRISE_SUNSET = RenderType.create("sunrise_sunset", 1536, false, false, RenderPipelines.SUNRISE_SUNSET, CompositeState.builder().createCompositeState(false));
    private static final Function<ResourceLocation, RenderType> CELESTIAL = Util.memoize($$0 -> RenderType.create("celestial", 1536, false, false, RenderPipelines.CELESTIAL, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> BLOCK_SCREEN_EFFECT = Util.memoize($$0 -> RenderType.create("block_screen_effect", 1536, false, false, RenderPipelines.BLOCK_SCREEN_EFFECT, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).createCompositeState(false)));
    private static final Function<ResourceLocation, RenderType> FIRE_SCREEN_EFFECT = Util.memoize($$0 -> RenderType.create("fire_screen_effect", 1536, false, false, RenderPipelines.FIRE_SCREEN_EFFECT, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).createCompositeState(false)));
    private final int bufferSize;
    private final boolean affectsCrumbling;
    private final boolean sortOnUpload;

    public static RenderType solid() {
        return SOLID;
    }

    public static RenderType cutoutMipped() {
        return CUTOUT_MIPPED;
    }

    public static RenderType cutout() {
        return CUTOUT;
    }

    public static RenderType translucentMovingBlock() {
        return TRANSLUCENT_MOVING_BLOCK;
    }

    public static RenderType armorCutoutNoCull(ResourceLocation $$0) {
        return ARMOR_CUTOUT_NO_CULL.apply($$0);
    }

    public static RenderType createArmorDecalCutoutNoCull(ResourceLocation $$0) {
        CompositeState $$1 = CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard($$0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(true);
        return RenderType.create("armor_decal_cutout_no_cull", 1536, true, false, RenderPipelines.ARMOR_DECAL_CUTOUT_NO_CULL, $$1);
    }

    public static RenderType armorTranslucent(ResourceLocation $$0) {
        return ARMOR_TRANSLUCENT.apply($$0);
    }

    public static RenderType entitySolid(ResourceLocation $$0) {
        return ENTITY_SOLID.apply($$0);
    }

    public static RenderType entitySolidZOffsetForward(ResourceLocation $$0) {
        return ENTITY_SOLID_Z_OFFSET_FORWARD.apply($$0);
    }

    public static RenderType entityCutout(ResourceLocation $$0) {
        return ENTITY_CUTOUT.apply($$0);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation $$0, boolean $$1) {
        return ENTITY_CUTOUT_NO_CULL.apply($$0, $$1);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation $$0) {
        return RenderType.entityCutoutNoCull($$0, true);
    }

    public static RenderType entityCutoutNoCullZOffset(ResourceLocation $$0, boolean $$1) {
        return ENTITY_CUTOUT_NO_CULL_Z_OFFSET.apply($$0, $$1);
    }

    public static RenderType entityCutoutNoCullZOffset(ResourceLocation $$0) {
        return RenderType.entityCutoutNoCullZOffset($$0, true);
    }

    public static RenderType itemEntityTranslucentCull(ResourceLocation $$0) {
        return ITEM_ENTITY_TRANSLUCENT_CULL.apply($$0);
    }

    public static RenderType entityTranslucent(ResourceLocation $$0, boolean $$1) {
        return ENTITY_TRANSLUCENT.apply($$0, $$1);
    }

    public static RenderType entityTranslucent(ResourceLocation $$0) {
        return RenderType.entityTranslucent($$0, true);
    }

    public static RenderType entityTranslucentEmissive(ResourceLocation $$0, boolean $$1) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply($$0, $$1);
    }

    public static RenderType entityTranslucentEmissive(ResourceLocation $$0) {
        return RenderType.entityTranslucentEmissive($$0, true);
    }

    public static RenderType entitySmoothCutout(ResourceLocation $$0) {
        return ENTITY_SMOOTH_CUTOUT.apply($$0);
    }

    public static RenderType beaconBeam(ResourceLocation $$0, boolean $$1) {
        return BEACON_BEAM.apply($$0, $$1);
    }

    public static RenderType entityDecal(ResourceLocation $$0) {
        return ENTITY_DECAL.apply($$0);
    }

    public static RenderType entityNoOutline(ResourceLocation $$0) {
        return ENTITY_NO_OUTLINE.apply($$0);
    }

    public static RenderType entityShadow(ResourceLocation $$0) {
        return ENTITY_SHADOW.apply($$0);
    }

    public static RenderType dragonExplosionAlpha(ResourceLocation $$0) {
        return DRAGON_EXPLOSION_ALPHA.apply($$0);
    }

    public static RenderType eyes(ResourceLocation $$0) {
        return EYES.apply($$0);
    }

    public static RenderType breezeEyes(ResourceLocation $$0) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply($$0, false);
    }

    public static RenderType breezeWind(ResourceLocation $$0, float $$1, float $$2) {
        return RenderType.create("breeze_wind", 1536, false, true, RenderPipelines.BREEZE_WIND, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard($$0, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard($$1, $$2)).setLightmapState(LIGHTMAP).setOverlayState(NO_OVERLAY).createCompositeState(false));
    }

    public static RenderType energySwirl(ResourceLocation $$0, float $$1, float $$2) {
        return RenderType.create("energy_swirl", 1536, false, true, RenderPipelines.ENERGY_SWIRL, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard($$0, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard($$1, $$2)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }

    public static RenderType leash() {
        return LEASH;
    }

    public static RenderType waterMask() {
        return WATER_MASK;
    }

    public static RenderType outline(ResourceLocation $$0) {
        return CompositeRenderType.OUTLINE.apply($$0, false);
    }

    public static RenderType armorEntityGlint() {
        return ARMOR_ENTITY_GLINT;
    }

    public static RenderType glintTranslucent() {
        return GLINT_TRANSLUCENT;
    }

    public static RenderType glint() {
        return GLINT;
    }

    public static RenderType entityGlint() {
        return ENTITY_GLINT;
    }

    public static RenderType crumbling(ResourceLocation $$0) {
        return CRUMBLING.apply($$0);
    }

    public static RenderType text(ResourceLocation $$0) {
        return TEXT.apply($$0);
    }

    public static RenderType textBackground() {
        return TEXT_BACKGROUND;
    }

    public static RenderType textIntensity(ResourceLocation $$0) {
        return TEXT_INTENSITY.apply($$0);
    }

    public static RenderType textPolygonOffset(ResourceLocation $$0) {
        return TEXT_POLYGON_OFFSET.apply($$0);
    }

    public static RenderType textIntensityPolygonOffset(ResourceLocation $$0) {
        return TEXT_INTENSITY_POLYGON_OFFSET.apply($$0);
    }

    public static RenderType textSeeThrough(ResourceLocation $$0) {
        return TEXT_SEE_THROUGH.apply($$0);
    }

    public static RenderType textBackgroundSeeThrough() {
        return TEXT_BACKGROUND_SEE_THROUGH;
    }

    public static RenderType textIntensitySeeThrough(ResourceLocation $$0) {
        return TEXT_INTENSITY_SEE_THROUGH.apply($$0);
    }

    public static RenderType lightning() {
        return LIGHTNING;
    }

    public static RenderType dragonRays() {
        return DRAGON_RAYS;
    }

    public static RenderType dragonRaysDepth() {
        return DRAGON_RAYS_DEPTH;
    }

    public static RenderType tripwire() {
        return TRIPWIRE;
    }

    public static RenderType endPortal() {
        return END_PORTAL;
    }

    public static RenderType endGateway() {
        return END_GATEWAY;
    }

    public static RenderType lines() {
        return LINES;
    }

    public static RenderType secondaryBlockOutline() {
        return SECONDARY_BLOCK_OUTLINE;
    }

    public static RenderType lineStrip() {
        return LINE_STRIP;
    }

    public static RenderType debugLineStrip(double $$0) {
        return DEBUG_LINE_STRIP.apply($$0);
    }

    public static RenderType debugFilledBox() {
        return DEBUG_FILLED_BOX;
    }

    public static RenderType debugQuads() {
        return DEBUG_QUADS;
    }

    public static RenderType debugTriangleFan() {
        return DEBUG_TRIANGLE_FAN;
    }

    public static RenderType debugStructureQuads() {
        return DEBUG_STRUCTURE_QUADS;
    }

    public static RenderType debugSectionQuads() {
        return DEBUG_SECTION_QUADS;
    }

    public static RenderType opaqueParticle(ResourceLocation $$0) {
        return OPAQUE_PARTICLE.apply($$0);
    }

    public static RenderType translucentParticle(ResourceLocation $$0) {
        return TRANSLUCENT_PARTICLE.apply($$0);
    }

    private static Function<ResourceLocation, RenderType> createWeather(RenderPipeline $$0) {
        return Util.memoize($$1 -> RenderType.create("weather", 1536, false, false, $$0, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$1, false)).setOutputState(WEATHER_TARGET).setLightmapState(LIGHTMAP).createCompositeState(false)));
    }

    public static RenderType weather(ResourceLocation $$0, boolean $$1) {
        return ($$1 ? WEATHER_DEPTH_WRITE : WEATHER_NO_DEPTH_WRITE).apply($$0);
    }

    public static RenderType sunriseSunset() {
        return SUNRISE_SUNSET;
    }

    public static RenderType celestial(ResourceLocation $$0) {
        return CELESTIAL.apply($$0);
    }

    public static RenderType blockScreenEffect(ResourceLocation $$0) {
        return BLOCK_SCREEN_EFFECT.apply($$0);
    }

    public static RenderType fireScreenEffect(ResourceLocation $$0) {
        return FIRE_SCREEN_EFFECT.apply($$0);
    }

    public RenderType(String $$0, int $$1, boolean $$2, boolean $$3, Runnable $$4, Runnable $$5) {
        super($$0, $$4, $$5);
        this.bufferSize = $$1;
        this.affectsCrumbling = $$2;
        this.sortOnUpload = $$3;
    }

    static CompositeRenderType create(String $$0, int $$1, RenderPipeline $$2, CompositeState $$3) {
        return RenderType.create($$0, $$1, false, false, $$2, $$3);
    }

    private static CompositeRenderType create(String $$0, int $$1, boolean $$2, boolean $$3, RenderPipeline $$4, CompositeState $$5) {
        return new CompositeRenderType($$0, $$1, $$2, $$3, $$4, $$5);
    }

    public abstract void draw(MeshData var1);

    public int bufferSize() {
        return this.bufferSize;
    }

    public abstract VertexFormat format();

    public abstract VertexFormat.Mode mode();

    public Optional<RenderType> outline() {
        return Optional.empty();
    }

    public boolean isOutline() {
        return false;
    }

    public boolean affectsCrumbling() {
        return this.affectsCrumbling;
    }

    public boolean canConsolidateConsecutiveGeometry() {
        return !this.mode().connectedPrimitives;
    }

    public boolean sortOnUpload() {
        return this.sortOnUpload;
    }

    protected static final class CompositeState {
        final RenderStateShard.EmptyTextureStateShard textureState;
        final RenderStateShard.OutputStateShard outputState;
        final OutlineProperty outlineProperty;
        final ImmutableList<RenderStateShard> states;

        CompositeState(RenderStateShard.EmptyTextureStateShard $$0, RenderStateShard.LightmapStateShard $$1, RenderStateShard.OverlayStateShard $$2, RenderStateShard.LayeringStateShard $$3, RenderStateShard.OutputStateShard $$4, RenderStateShard.TexturingStateShard $$5, RenderStateShard.LineStateShard $$6, OutlineProperty $$7) {
            this.textureState = $$0;
            this.outputState = $$4;
            this.outlineProperty = $$7;
            this.states = ImmutableList.of($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        }

        public String toString() {
            return "CompositeState[" + String.valueOf(this.states) + ", outlineProperty=" + String.valueOf((Object)this.outlineProperty) + "]";
        }

        public static CompositeStateBuilder builder() {
            return new CompositeStateBuilder();
        }

        public static class CompositeStateBuilder {
            private RenderStateShard.EmptyTextureStateShard textureState = RenderStateShard.NO_TEXTURE;
            private RenderStateShard.LightmapStateShard lightmapState = RenderStateShard.NO_LIGHTMAP;
            private RenderStateShard.OverlayStateShard overlayState = RenderStateShard.NO_OVERLAY;
            private RenderStateShard.LayeringStateShard layeringState = RenderStateShard.NO_LAYERING;
            private RenderStateShard.OutputStateShard outputState = RenderStateShard.MAIN_TARGET;
            private RenderStateShard.TexturingStateShard texturingState = RenderStateShard.DEFAULT_TEXTURING;
            private RenderStateShard.LineStateShard lineState = RenderStateShard.DEFAULT_LINE;

            CompositeStateBuilder() {
            }

            protected CompositeStateBuilder setTextureState(RenderStateShard.EmptyTextureStateShard $$0) {
                this.textureState = $$0;
                return this;
            }

            protected CompositeStateBuilder setLightmapState(RenderStateShard.LightmapStateShard $$0) {
                this.lightmapState = $$0;
                return this;
            }

            protected CompositeStateBuilder setOverlayState(RenderStateShard.OverlayStateShard $$0) {
                this.overlayState = $$0;
                return this;
            }

            protected CompositeStateBuilder setLayeringState(RenderStateShard.LayeringStateShard $$0) {
                this.layeringState = $$0;
                return this;
            }

            protected CompositeStateBuilder setOutputState(RenderStateShard.OutputStateShard $$0) {
                this.outputState = $$0;
                return this;
            }

            protected CompositeStateBuilder setTexturingState(RenderStateShard.TexturingStateShard $$0) {
                this.texturingState = $$0;
                return this;
            }

            protected CompositeStateBuilder setLineState(RenderStateShard.LineStateShard $$0) {
                this.lineState = $$0;
                return this;
            }

            protected CompositeState createCompositeState(boolean $$0) {
                return this.createCompositeState($$0 ? OutlineProperty.AFFECTS_OUTLINE : OutlineProperty.NONE);
            }

            protected CompositeState createCompositeState(OutlineProperty $$0) {
                return new CompositeState(this.textureState, this.lightmapState, this.overlayState, this.layeringState, this.outputState, this.texturingState, this.lineState, $$0);
            }
        }
    }

    static final class CompositeRenderType
    extends RenderType {
        static final BiFunction<ResourceLocation, Boolean, RenderType> OUTLINE = Util.memoize(($$0, $$1) -> RenderType.create("outline", 1536, $$1 != false ? RenderPipelines.OUTLINE_CULL : RenderPipelines.OUTLINE_NO_CULL, CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard((ResourceLocation)$$0, false)).setOutputState(OUTLINE_TARGET).createCompositeState(OutlineProperty.IS_OUTLINE)));
        private final CompositeState state;
        private final RenderPipeline renderPipeline;
        private final Optional<RenderType> outline;
        private final boolean isOutline;

        CompositeRenderType(String $$0, int $$12, boolean $$2, boolean $$3, RenderPipeline $$4, CompositeState $$5) {
            super($$0, $$12, $$2, $$3, () -> $$0.states.forEach(RenderStateShard::setupRenderState), () -> $$0.states.forEach(RenderStateShard::clearRenderState));
            this.state = $$5;
            this.renderPipeline = $$4;
            this.outline = $$5.outlineProperty == OutlineProperty.AFFECTS_OUTLINE ? $$5.textureState.cutoutTexture().map($$1 -> OUTLINE.apply((ResourceLocation)$$1, $$4.isCull())) : Optional.empty();
            this.isOutline = $$5.outlineProperty == OutlineProperty.IS_OUTLINE;
        }

        @Override
        public Optional<RenderType> outline() {
            return this.outline;
        }

        @Override
        public boolean isOutline() {
            return this.isOutline;
        }

        @Override
        public VertexFormat format() {
            return this.renderPipeline.getVertexFormat();
        }

        @Override
        public VertexFormat.Mode mode() {
            return this.renderPipeline.getVertexFormatMode();
        }

        @Override
        public void draw(MeshData $$0) {
            this.setupRenderState();
            GpuBufferSlice $$1 = RenderSystem.getDynamicUniforms().writeTransform((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)RenderSystem.getModelOffset(), (Matrix4fc)RenderSystem.getTextureMatrix(), RenderSystem.getShaderLineWidth());
            try (MeshData meshData = $$0;){
                GpuTextureView $$9;
                VertexFormat.IndexType $$7;
                GpuBuffer $$6;
                GpuBuffer $$2 = this.renderPipeline.getVertexFormat().uploadImmediateVertexBuffer($$0.vertexBuffer());
                if ($$0.indexBuffer() == null) {
                    RenderSystem.AutoStorageIndexBuffer $$3 = RenderSystem.getSequentialBuffer($$0.drawState().mode());
                    GpuBuffer $$4 = $$3.getBuffer($$0.drawState().indexCount());
                    VertexFormat.IndexType $$5 = $$3.type();
                } else {
                    $$6 = this.renderPipeline.getVertexFormat().uploadImmediateIndexBuffer($$0.indexBuffer());
                    $$7 = $$0.drawState().indexType();
                }
                RenderTarget $$8 = this.state.outputState.getRenderTarget();
                GpuTextureView gpuTextureView = $$9 = RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : $$8.getColorTextureView();
                GpuTextureView $$10 = $$8.useDepth ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : $$8.getDepthTextureView()) : null;
                try (RenderPass $$11 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Immediate draw for " + this.getName(), $$9, OptionalInt.empty(), $$10, OptionalDouble.empty());){
                    $$11.setPipeline(this.renderPipeline);
                    ScissorState $$12 = RenderSystem.getScissorStateForRenderTypeDraws();
                    if ($$12.enabled()) {
                        $$11.enableScissor($$12.x(), $$12.y(), $$12.width(), $$12.height());
                    }
                    RenderSystem.bindDefaultUniforms($$11);
                    $$11.setUniform("DynamicTransforms", $$1);
                    $$11.setVertexBuffer(0, $$2);
                    for (int $$13 = 0; $$13 < 12; ++$$13) {
                        GpuTextureView $$14 = RenderSystem.getShaderTexture($$13);
                        if ($$14 == null) continue;
                        $$11.bindSampler("Sampler" + $$13, $$14);
                    }
                    $$11.setIndexBuffer($$6, $$7);
                    $$11.drawIndexed(0, 0, $$0.drawState().indexCount(), 1);
                }
            }
            this.clearRenderState();
        }

        @Override
        public String toString() {
            return "RenderType[" + this.name + ":" + String.valueOf(this.state) + "]";
        }
    }

    protected static final class OutlineProperty
    extends Enum<OutlineProperty> {
        public static final /* enum */ OutlineProperty NONE = new OutlineProperty("none");
        public static final /* enum */ OutlineProperty IS_OUTLINE = new OutlineProperty("is_outline");
        public static final /* enum */ OutlineProperty AFFECTS_OUTLINE = new OutlineProperty("affects_outline");
        private final String name;
        private static final /* synthetic */ OutlineProperty[] $VALUES;

        public static OutlineProperty[] values() {
            return (OutlineProperty[])$VALUES.clone();
        }

        public static OutlineProperty valueOf(String $$0) {
            return Enum.valueOf(OutlineProperty.class, $$0);
        }

        private OutlineProperty(String $$0) {
            this.name = $$0;
        }

        public String toString() {
            return this.name;
        }

        private static /* synthetic */ OutlineProperty[] a() {
            return new OutlineProperty[]{NONE, IS_OUTLINE, AFFECTS_OUTLINE};
        }

        static {
            $VALUES = OutlineProperty.a();
        }
    }
}

