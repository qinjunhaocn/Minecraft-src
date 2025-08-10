/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class LevelLoadingScreen
extends Screen {
    private static final long NARRATION_DELAY_MS = 2000L;
    private final StoringChunkProgressListener progressListener;
    private long lastNarration = -1L;
    private boolean done;
    private static final Object2IntMap<ChunkStatus> COLORS = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), $$0 -> {
        $$0.defaultReturnValue(0);
        $$0.put((Object)ChunkStatus.EMPTY, 0x545454);
        $$0.put((Object)ChunkStatus.STRUCTURE_STARTS, 0x999999);
        $$0.put((Object)ChunkStatus.STRUCTURE_REFERENCES, 6250897);
        $$0.put((Object)ChunkStatus.BIOMES, 8434258);
        $$0.put((Object)ChunkStatus.NOISE, 0xD1D1D1);
        $$0.put((Object)ChunkStatus.SURFACE, 7497737);
        $$0.put((Object)ChunkStatus.CARVERS, 3159410);
        $$0.put((Object)ChunkStatus.FEATURES, 2213376);
        $$0.put((Object)ChunkStatus.INITIALIZE_LIGHT, 0xCCCCCC);
        $$0.put((Object)ChunkStatus.LIGHT, 16769184);
        $$0.put((Object)ChunkStatus.SPAWN, 15884384);
        $$0.put((Object)ChunkStatus.FULL, 0xFFFFFF);
    });

    public LevelLoadingScreen(StoringChunkProgressListener $$0) {
        super(GameNarrator.NO_TITLE);
        this.progressListener = $$0;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected boolean shouldNarrateNavigation() {
        return false;
    }

    @Override
    public void removed() {
        this.done = true;
        this.triggerImmediateNarration(true);
    }

    @Override
    protected void updateNarratedWidget(NarrationElementOutput $$0) {
        if (this.done) {
            $$0.add(NarratedElementType.TITLE, Component.translatable("narrator.loading.done"));
        } else {
            $$0.add(NarratedElementType.TITLE, this.getFormattedProgress());
        }
    }

    private Component getFormattedProgress() {
        return Component.a("loading.progress", Mth.clamp(this.progressListener.getProgress(), 0, 100));
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        long $$4 = Util.getMillis();
        if ($$4 - this.lastNarration > 2000L) {
            this.lastNarration = $$4;
            this.triggerImmediateNarration(true);
        }
        int $$5 = this.width / 2;
        int $$6 = this.height / 2;
        LevelLoadingScreen.renderChunks($$0, this.progressListener, $$5, $$6, 2, 0);
        int $$7 = this.progressListener.getDiameter() + this.font.lineHeight + 2;
        $$0.drawCenteredString(this.font, this.getFormattedProgress(), $$5, $$6 - $$7, -1);
    }

    public static void renderChunks(GuiGraphics $$0, StoringChunkProgressListener $$1, int $$2, int $$3, int $$4, int $$5) {
        int $$6 = $$4 + $$5;
        int $$7 = $$1.getFullDiameter();
        int $$8 = $$7 * $$6 - $$5;
        int $$9 = $$1.getDiameter();
        int $$10 = $$9 * $$6 - $$5;
        int $$11 = $$2 - $$10 / 2;
        int $$12 = $$3 - $$10 / 2;
        int $$13 = $$8 / 2 + 1;
        int $$14 = -16772609;
        if ($$5 != 0) {
            $$0.fill($$2 - $$13, $$3 - $$13, $$2 - $$13 + 1, $$3 + $$13, -16772609);
            $$0.fill($$2 + $$13 - 1, $$3 - $$13, $$2 + $$13, $$3 + $$13, -16772609);
            $$0.fill($$2 - $$13, $$3 - $$13, $$2 + $$13, $$3 - $$13 + 1, -16772609);
            $$0.fill($$2 - $$13, $$3 + $$13 - 1, $$2 + $$13, $$3 + $$13, -16772609);
        }
        for (int $$15 = 0; $$15 < $$9; ++$$15) {
            for (int $$16 = 0; $$16 < $$9; ++$$16) {
                ChunkStatus $$17 = $$1.getStatus($$15, $$16);
                int $$18 = $$11 + $$15 * $$6;
                int $$19 = $$12 + $$16 * $$6;
                $$0.fill($$18, $$19, $$18 + $$4, $$19 + $$4, ARGB.opaque(COLORS.getInt((Object)$$17)));
            }
        }
    }
}

