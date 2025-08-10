/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap
 */
package net.minecraft.client.gui.screens.worldselection;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.util.Mth;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class OptimizeWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ToIntFunction<ResourceKey<Level>> DIMENSION_COLORS = (ToIntFunction)Util.make(new Reference2IntOpenHashMap(), $$0 -> {
        $$0.put(Level.OVERWORLD, -13408734);
        $$0.put(Level.NETHER, -10075085);
        $$0.put(Level.END, -8943531);
        $$0.defaultReturnValue(-2236963);
    });
    private final BooleanConsumer callback;
    private final WorldUpgrader upgrader;

    @Nullable
    public static OptimizeWorldScreen create(Minecraft $$0, BooleanConsumer $$1, DataFixer $$2, LevelStorageSource.LevelStorageAccess $$3, boolean $$4) {
        WorldOpenFlows $$5 = $$0.createWorldOpenFlows();
        PackRepository $$6 = ServerPacksSource.createPackRepository($$3);
        WorldStem $$7 = $$5.loadWorldStem($$3.getDataTag(), false, $$6);
        try {
            WorldData $$8 = $$7.worldData();
            RegistryAccess.Frozen $$9 = $$7.registries().compositeAccess();
            $$3.saveDataTag($$9, $$8);
            OptimizeWorldScreen optimizeWorldScreen = new OptimizeWorldScreen($$1, $$2, $$3, $$8, $$4, $$9);
            if ($$7 != null) {
                $$7.close();
            }
            return optimizeWorldScreen;
        } catch (Throwable throwable) {
            try {
                if ($$7 != null) {
                    try {
                        $$7.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            } catch (Exception $$10) {
                LOGGER.warn("Failed to load datapacks, can't optimize world", $$10);
                return null;
            }
        }
    }

    private OptimizeWorldScreen(BooleanConsumer $$0, DataFixer $$1, LevelStorageSource.LevelStorageAccess $$2, WorldData $$3, boolean $$4, RegistryAccess $$5) {
        super(Component.a("optimizeWorld.title", $$3.getLevelSettings().levelName()));
        this.callback = $$0;
        this.upgrader = new WorldUpgrader($$2, $$1, $$3, $$5, $$4, false);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> {
            this.upgrader.cancel();
            this.callback.accept(false);
        }).bounds(this.width / 2 - 100, this.height / 4 + 150, 200, 20).build());
    }

    @Override
    public void tick() {
        if (this.upgrader.isFinished()) {
            this.callback.accept(true);
        }
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    @Override
    public void removed() {
        this.upgrader.cancel();
        this.upgrader.close();
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 20, -1);
        int $$4 = this.width / 2 - 150;
        int $$5 = this.width / 2 + 150;
        int $$6 = this.height / 4 + 100;
        int $$7 = $$6 + 10;
        $$0.drawCenteredString(this.font, this.upgrader.getStatus(), this.width / 2, $$6 - this.font.lineHeight - 2, -6250336);
        if (this.upgrader.getTotalChunks() > 0) {
            $$0.fill($$4 - 1, $$6 - 1, $$5 + 1, $$7 + 1, -16777216);
            $$0.drawString(this.font, Component.a("optimizeWorld.info.converted", this.upgrader.getConverted()), $$4, 40, -6250336);
            $$0.drawString(this.font, Component.a("optimizeWorld.info.skipped", this.upgrader.getSkipped()), $$4, 40 + this.font.lineHeight + 3, -6250336);
            $$0.drawString(this.font, Component.a("optimizeWorld.info.total", this.upgrader.getTotalChunks()), $$4, 40 + (this.font.lineHeight + 3) * 2, -6250336);
            int $$8 = 0;
            for (ResourceKey<Level> $$9 : this.upgrader.levels()) {
                int $$10 = Mth.floor(this.upgrader.dimensionProgress($$9) * (float)($$5 - $$4));
                $$0.fill($$4 + $$8, $$6, $$4 + $$8 + $$10, $$7, DIMENSION_COLORS.applyAsInt($$9));
                $$8 += $$10;
            }
            int $$11 = this.upgrader.getConverted() + this.upgrader.getSkipped();
            MutableComponent $$12 = Component.a("optimizeWorld.progress.counter", $$11, this.upgrader.getTotalChunks());
            MutableComponent $$13 = Component.a("optimizeWorld.progress.percentage", Mth.floor(this.upgrader.getProgress() * 100.0f));
            $$0.drawCenteredString(this.font, $$12, this.width / 2, $$6 + 2 * this.font.lineHeight + 2, -6250336);
            $$0.drawCenteredString(this.font, $$13, this.width / 2, $$6 + ($$7 - $$6) / 2 - this.font.lineHeight / 2, -6250336);
        }
    }
}

