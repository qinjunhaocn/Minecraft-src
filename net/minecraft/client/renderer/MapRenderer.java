/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MapDecorationTextureManager;
import net.minecraft.client.resources.MapTextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

public class MapRenderer {
    private static final float MAP_Z_OFFSET = -0.01f;
    private static final float DECORATION_Z_OFFSET = -0.001f;
    public static final int WIDTH = 128;
    public static final int HEIGHT = 128;
    private final MapTextureManager mapTextureManager;
    private final MapDecorationTextureManager decorationTextures;

    public MapRenderer(MapDecorationTextureManager $$0, MapTextureManager $$1) {
        this.decorationTextures = $$0;
        this.mapTextureManager = $$1;
    }

    public void render(MapRenderState $$0, PoseStack $$1, MultiBufferSource $$2, boolean $$3, int $$4) {
        Matrix4f $$5 = $$1.last().pose();
        VertexConsumer $$6 = $$2.getBuffer(RenderType.text($$0.texture));
        $$6.addVertex($$5, 0.0f, 128.0f, -0.01f).setColor(-1).setUv(0.0f, 1.0f).setLight($$4);
        $$6.addVertex($$5, 128.0f, 128.0f, -0.01f).setColor(-1).setUv(1.0f, 1.0f).setLight($$4);
        $$6.addVertex($$5, 128.0f, 0.0f, -0.01f).setColor(-1).setUv(1.0f, 0.0f).setLight($$4);
        $$6.addVertex($$5, 0.0f, 0.0f, -0.01f).setColor(-1).setUv(0.0f, 0.0f).setLight($$4);
        int $$7 = 0;
        for (MapRenderState.MapDecorationRenderState $$8 : $$0.decorations) {
            if ($$3 && !$$8.renderOnFrame) continue;
            $$1.pushPose();
            $$1.translate((float)$$8.x / 2.0f + 64.0f, (float)$$8.y / 2.0f + 64.0f, -0.02f);
            $$1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)($$8.rot * 360) / 16.0f));
            $$1.scale(4.0f, 4.0f, 3.0f);
            $$1.translate(-0.125f, 0.125f, 0.0f);
            Matrix4f $$9 = $$1.last().pose();
            TextureAtlasSprite $$10 = $$8.atlasSprite;
            if ($$10 != null) {
                VertexConsumer $$11 = $$2.getBuffer(RenderType.text($$10.atlasLocation()));
                $$11.addVertex($$9, -1.0f, 1.0f, (float)$$7 * -0.001f).setColor(-1).setUv($$10.getU0(), $$10.getV0()).setLight($$4);
                $$11.addVertex($$9, 1.0f, 1.0f, (float)$$7 * -0.001f).setColor(-1).setUv($$10.getU1(), $$10.getV0()).setLight($$4);
                $$11.addVertex($$9, 1.0f, -1.0f, (float)$$7 * -0.001f).setColor(-1).setUv($$10.getU1(), $$10.getV1()).setLight($$4);
                $$11.addVertex($$9, -1.0f, -1.0f, (float)$$7 * -0.001f).setColor(-1).setUv($$10.getU0(), $$10.getV1()).setLight($$4);
                $$1.popPose();
            }
            if ($$8.name != null) {
                Font $$12 = Minecraft.getInstance().font;
                float $$13 = $$12.width($$8.name);
                float f = 25.0f / $$13;
                Objects.requireNonNull($$12);
                float $$14 = Mth.clamp(f, 0.0f, 6.0f / 9.0f);
                $$1.pushPose();
                $$1.translate((float)$$8.x / 2.0f + 64.0f - $$13 * $$14 / 2.0f, (float)$$8.y / 2.0f + 64.0f + 4.0f, -0.025f);
                $$1.scale($$14, $$14, -1.0f);
                $$1.translate(0.0f, 0.0f, 0.1f);
                $$12.drawInBatch($$8.name, 0.0f, 0.0f, -1, false, $$1.last().pose(), $$2, Font.DisplayMode.NORMAL, Integer.MIN_VALUE, $$4);
                $$1.popPose();
            }
            ++$$7;
        }
    }

    public void extractRenderState(MapId $$0, MapItemSavedData $$1, MapRenderState $$2) {
        $$2.texture = this.mapTextureManager.prepareMapTexture($$0, $$1);
        $$2.decorations.clear();
        for (MapDecoration $$3 : $$1.getDecorations()) {
            $$2.decorations.add(this.extractDecorationRenderState($$3));
        }
    }

    private MapRenderState.MapDecorationRenderState extractDecorationRenderState(MapDecoration $$0) {
        MapRenderState.MapDecorationRenderState $$1 = new MapRenderState.MapDecorationRenderState();
        $$1.atlasSprite = this.decorationTextures.get($$0);
        $$1.x = $$0.x();
        $$1.y = $$0.y();
        $$1.rot = $$0.rot();
        $$1.name = $$0.name().orElse(null);
        $$1.renderOnFrame = $$0.renderOnFrame();
        return $$1;
    }
}

