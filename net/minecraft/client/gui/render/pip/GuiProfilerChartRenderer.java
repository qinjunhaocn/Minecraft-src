/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.GuiProfilerChartRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ResultField;
import org.joml.Matrix4f;

public class GuiProfilerChartRenderer
extends PictureInPictureRenderer<GuiProfilerChartRenderState> {
    public GuiProfilerChartRenderer(MultiBufferSource.BufferSource $$0) {
        super($$0);
    }

    @Override
    public Class<GuiProfilerChartRenderState> getRenderStateClass() {
        return GuiProfilerChartRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiProfilerChartRenderState $$0, PoseStack $$1) {
        double $$2 = 0.0;
        $$1.translate(0.0f, -5.0f, 0.0f);
        Matrix4f $$3 = $$1.last().pose();
        for (ResultField $$4 : $$0.chartData()) {
            int $$5 = Mth.floor($$4.percentage / 4.0) + 1;
            VertexConsumer $$6 = this.bufferSource.getBuffer(RenderType.debugTriangleFan());
            int $$7 = ARGB.opaque($$4.getColor());
            int $$8 = ARGB.multiply($$7, -8355712);
            $$6.addVertex($$3, 0.0f, 0.0f, 0.0f).setColor($$7);
            for (int $$9 = $$5; $$9 >= 0; --$$9) {
                float $$10 = (float)(($$2 + $$4.percentage * (double)$$9 / (double)$$5) * 6.2831854820251465 / 100.0);
                float $$11 = Mth.sin($$10) * 105.0f;
                float $$12 = Mth.cos($$10) * 105.0f * 0.5f;
                $$6.addVertex($$3, $$11, $$12, 0.0f).setColor($$7);
            }
            $$6 = this.bufferSource.getBuffer(RenderType.debugQuads());
            for (int $$13 = $$5; $$13 > 0; --$$13) {
                float $$14 = (float)(($$2 + $$4.percentage * (double)$$13 / (double)$$5) * 6.2831854820251465 / 100.0);
                float $$15 = Mth.sin($$14) * 105.0f;
                float $$16 = Mth.cos($$14) * 105.0f * 0.5f;
                float $$17 = (float)(($$2 + $$4.percentage * (double)($$13 - 1) / (double)$$5) * 6.2831854820251465 / 100.0);
                float $$18 = Mth.sin($$17) * 105.0f;
                float $$19 = Mth.cos($$17) * 105.0f * 0.5f;
                if (($$16 + $$19) / 2.0f < 0.0f) continue;
                $$6.addVertex($$3, $$15, $$16, 0.0f).setColor($$8);
                $$6.addVertex($$3, $$15, $$16 + 10.0f, 0.0f).setColor($$8);
                $$6.addVertex($$3, $$18, $$19 + 10.0f, 0.0f).setColor($$8);
                $$6.addVertex($$3, $$18, $$19, 0.0f).setColor($$8);
            }
            $$2 += $$4.percentage;
        }
    }

    @Override
    protected float getTranslateY(int $$0, int $$1) {
        return (float)$$0 / 2.0f;
    }

    @Override
    protected String getTextureLabel() {
        return "profiler chart";
    }
}

