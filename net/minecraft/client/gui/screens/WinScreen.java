/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class WinScreen
extends Screen {
    private static final ResourceLocation VIGNETTE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/credits_vignette.png");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component SECTION_HEADING = Component.literal("============").withStyle(ChatFormatting.WHITE);
    private static final String NAME_PREFIX = "           ";
    private static final String OBFUSCATE_TOKEN = String.valueOf(ChatFormatting.WHITE) + String.valueOf(ChatFormatting.OBFUSCATED) + String.valueOf(ChatFormatting.GREEN) + String.valueOf(ChatFormatting.AQUA);
    private static final float SPEEDUP_FACTOR = 5.0f;
    private static final float SPEEDUP_FACTOR_FAST = 15.0f;
    private static final ResourceLocation END_POEM_LOCATION = ResourceLocation.withDefaultNamespace("texts/end.txt");
    private static final ResourceLocation CREDITS_LOCATION = ResourceLocation.withDefaultNamespace("texts/credits.json");
    private static final ResourceLocation POSTCREDITS_LOCATION = ResourceLocation.withDefaultNamespace("texts/postcredits.txt");
    private final boolean poem;
    private final Runnable onFinished;
    private float scroll;
    private List<FormattedCharSequence> lines;
    private List<Component> narratorComponents;
    private IntSet centeredLines;
    private int totalScrollLength;
    private boolean speedupActive;
    private final IntSet speedupModifiers = new IntOpenHashSet();
    private float scrollSpeed;
    private final float unmodifiedScrollSpeed;
    private int direction;
    private final LogoRenderer logoRenderer = new LogoRenderer(false);

    public WinScreen(boolean $$0, Runnable $$1) {
        super(GameNarrator.NO_TITLE);
        this.poem = $$0;
        this.onFinished = $$1;
        this.unmodifiedScrollSpeed = !$$0 ? 0.75f : 0.5f;
        this.direction = 1;
        this.scrollSpeed = this.unmodifiedScrollSpeed;
    }

    private float calculateScrollSpeed() {
        if (this.speedupActive) {
            return this.unmodifiedScrollSpeed * (5.0f + (float)this.speedupModifiers.size() * 15.0f) * (float)this.direction;
        }
        return this.unmodifiedScrollSpeed * (float)this.direction;
    }

    @Override
    public void tick() {
        this.minecraft.getMusicManager().tick();
        this.minecraft.getSoundManager().tick(false);
        float $$0 = this.totalScrollLength + this.height + this.height + 24;
        if (this.scroll > $$0) {
            this.respawn();
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 265) {
            this.direction = -1;
        } else if ($$0 == 341 || $$0 == 345) {
            this.speedupModifiers.add($$0);
        } else if ($$0 == 32) {
            this.speedupActive = true;
        }
        this.scrollSpeed = this.calculateScrollSpeed();
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public boolean keyReleased(int $$0, int $$1, int $$2) {
        if ($$0 == 265) {
            this.direction = 1;
        }
        if ($$0 == 32) {
            this.speedupActive = false;
        } else if ($$0 == 341 || $$0 == 345) {
            this.speedupModifiers.remove($$0);
        }
        this.scrollSpeed = this.calculateScrollSpeed();
        return super.keyReleased($$0, $$1, $$2);
    }

    @Override
    public void onClose() {
        this.respawn();
    }

    private void respawn() {
        this.onFinished.run();
    }

    @Override
    protected void init() {
        if (this.lines != null) {
            return;
        }
        this.lines = Lists.newArrayList();
        this.narratorComponents = Lists.newArrayList();
        this.centeredLines = new IntOpenHashSet();
        if (this.poem) {
            this.wrapCreditsIO(END_POEM_LOCATION, this::addPoemFile);
        }
        this.wrapCreditsIO(CREDITS_LOCATION, this::addCreditsFile);
        if (this.poem) {
            this.wrapCreditsIO(POSTCREDITS_LOCATION, this::addPoemFile);
        }
        this.totalScrollLength = this.lines.size() * 12;
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a((Component[])this.narratorComponents.toArray(Component[]::new));
    }

    private void wrapCreditsIO(ResourceLocation $$0, CreditsReader $$1) {
        try (BufferedReader $$2 = this.minecraft.getResourceManager().openAsReader($$0);){
            $$1.read($$2);
        } catch (Exception $$3) {
            LOGGER.error("Couldn't load credits from file {}", (Object)$$0, (Object)$$3);
        }
    }

    private void addPoemFile(Reader $$0) throws IOException {
        Object $$3;
        BufferedReader $$1 = new BufferedReader($$0);
        RandomSource $$2 = RandomSource.create(8124371L);
        while (($$3 = $$1.readLine()) != null) {
            int $$4;
            $$3 = ((String)$$3).replaceAll("PLAYERNAME", this.minecraft.getUser().getName());
            while (($$4 = ((String)$$3).indexOf(OBFUSCATE_TOKEN)) != -1) {
                String $$5 = ((String)$$3).substring(0, $$4);
                String $$6 = ((String)$$3).substring($$4 + OBFUSCATE_TOKEN.length());
                $$3 = $$5 + String.valueOf(ChatFormatting.WHITE) + String.valueOf(ChatFormatting.OBFUSCATED) + "XXXXXXXX".substring(0, $$2.nextInt(4) + 3) + $$6;
            }
            this.addPoemLines((String)$$3);
            this.addEmptyLine();
        }
        for (int $$7 = 0; $$7 < 8; ++$$7) {
            this.addEmptyLine();
        }
    }

    private void addCreditsFile(Reader $$0) {
        JsonArray $$1 = GsonHelper.parseArray($$0);
        for (JsonElement $$2 : $$1) {
            JsonObject $$3 = $$2.getAsJsonObject();
            String $$4 = $$3.get("section").getAsString();
            this.addCreditsLine(SECTION_HEADING, true, false);
            this.addCreditsLine(Component.literal($$4).withStyle(ChatFormatting.YELLOW), true, true);
            this.addCreditsLine(SECTION_HEADING, true, false);
            this.addEmptyLine();
            this.addEmptyLine();
            JsonArray $$5 = $$3.getAsJsonArray("disciplines");
            for (JsonElement $$6 : $$5) {
                JsonObject $$7 = $$6.getAsJsonObject();
                String $$8 = $$7.get("discipline").getAsString();
                if (StringUtils.isNotEmpty($$8)) {
                    this.addCreditsLine(Component.literal($$8).withStyle(ChatFormatting.YELLOW), true, true);
                    this.addEmptyLine();
                    this.addEmptyLine();
                }
                JsonArray $$9 = $$7.getAsJsonArray("titles");
                for (JsonElement $$10 : $$9) {
                    JsonObject $$11 = $$10.getAsJsonObject();
                    String $$12 = $$11.get("title").getAsString();
                    JsonArray $$13 = $$11.getAsJsonArray("names");
                    this.addCreditsLine(Component.literal($$12).withStyle(ChatFormatting.GRAY), false, true);
                    for (JsonElement $$14 : $$13) {
                        String $$15 = $$14.getAsString();
                        this.addCreditsLine(Component.literal(NAME_PREFIX).append($$15).withStyle(ChatFormatting.WHITE), false, true);
                    }
                    this.addEmptyLine();
                    this.addEmptyLine();
                }
            }
        }
    }

    private void addEmptyLine() {
        this.lines.add(FormattedCharSequence.EMPTY);
        this.narratorComponents.add(CommonComponents.EMPTY);
    }

    private void addPoemLines(String $$0) {
        MutableComponent $$1 = Component.literal($$0);
        this.lines.addAll(this.minecraft.font.split($$1, 256));
        this.narratorComponents.add($$1);
    }

    private void addCreditsLine(Component $$0, boolean $$1, boolean $$2) {
        if ($$1) {
            this.centeredLines.add(this.lines.size());
        }
        this.lines.add($$0.getVisualOrderText());
        if ($$2) {
            this.narratorComponents.add($$0);
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderVignette($$0);
        this.scroll = Math.max(0.0f, this.scroll + $$3 * this.scrollSpeed);
        int $$4 = this.width / 2 - 128;
        int $$5 = this.height + 50;
        float $$6 = -this.scroll;
        $$0.pose().pushMatrix();
        $$0.pose().translate(0.0f, $$6);
        $$0.nextStratum();
        this.logoRenderer.renderLogo($$0, this.width, 1.0f, $$5);
        int $$7 = $$5 + 100;
        for (int $$8 = 0; $$8 < this.lines.size(); ++$$8) {
            float $$9;
            if ($$8 == this.lines.size() - 1 && ($$9 = (float)$$7 + $$6 - (float)(this.height / 2 - 6)) < 0.0f) {
                $$0.pose().translate(0.0f, -$$9);
            }
            if ((float)$$7 + $$6 + 12.0f + 8.0f > 0.0f && (float)$$7 + $$6 < (float)this.height) {
                FormattedCharSequence $$10 = this.lines.get($$8);
                if (this.centeredLines.contains($$8)) {
                    $$0.drawCenteredString(this.font, $$10, $$4 + 128, $$7, -1);
                } else {
                    $$0.drawString(this.font, $$10, $$4, $$7, -1);
                }
            }
            $$7 += 12;
        }
        $$0.pose().popMatrix();
    }

    private void renderVignette(GuiGraphics $$0) {
        $$0.blit(RenderPipelines.VIGNETTE, VIGNETTE_LOCATION, 0, 0, 0.0f, 0.0f, this.width, this.height, this.width, this.height);
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (this.poem) {
            TextureManager $$4 = Minecraft.getInstance().getTextureManager();
            TextureSetup $$5 = TextureSetup.doubleTexture($$4.getTexture(TheEndPortalRenderer.END_SKY_LOCATION).getTextureView(), $$4.getTexture(TheEndPortalRenderer.END_PORTAL_LOCATION).getTextureView());
            $$0.fill(RenderPipelines.END_PORTAL, $$5, 0, 0, this.width, this.height);
        } else {
            super.renderBackground($$0, $$1, $$2, $$3);
        }
    }

    @Override
    protected void renderMenuBackground(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        float $$5 = this.scroll * 0.5f;
        Screen.renderMenuBackgroundTexture($$0, Screen.MENU_BACKGROUND, 0, 0, 0.0f, $$5, $$3, $$4);
    }

    @Override
    public boolean isPauseScreen() {
        return !this.poem;
    }

    @Override
    public void removed() {
        this.minecraft.getMusicManager().stopPlaying(Musics.CREDITS);
    }

    @Override
    public Music getBackgroundMusic() {
        return Musics.CREDITS;
    }

    @FunctionalInterface
    static interface CreditsReader {
        public void read(Reader var1) throws IOException;
    }
}

