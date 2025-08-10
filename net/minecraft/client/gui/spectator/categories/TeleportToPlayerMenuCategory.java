/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.spectator.categories;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.GameType;

public class TeleportToPlayerMenuCategory
implements SpectatorMenuCategory,
SpectatorMenuItem {
    private static final ResourceLocation TELEPORT_TO_PLAYER_SPRITE = ResourceLocation.withDefaultNamespace("spectator/teleport_to_player");
    private static final Comparator<PlayerInfo> PROFILE_ORDER = Comparator.comparing($$0 -> $$0.getProfile().getId());
    private static final Component TELEPORT_TEXT = Component.translatable("spectatorMenu.teleport");
    private static final Component TELEPORT_PROMPT = Component.translatable("spectatorMenu.teleport.prompt");
    private final List<SpectatorMenuItem> items;

    public TeleportToPlayerMenuCategory() {
        this(Minecraft.getInstance().getConnection().getListedOnlinePlayers());
    }

    public TeleportToPlayerMenuCategory(Collection<PlayerInfo> $$02) {
        this.items = $$02.stream().filter($$0 -> $$0.getGameMode() != GameType.SPECTATOR).sorted(PROFILE_ORDER).map($$0 -> new PlayerMenuItem($$0.getProfile())).toList();
    }

    @Override
    public List<SpectatorMenuItem> getItems() {
        return this.items;
    }

    @Override
    public Component getPrompt() {
        return TELEPORT_PROMPT;
    }

    @Override
    public void selectItem(SpectatorMenu $$0) {
        $$0.selectCategory(this);
    }

    @Override
    public Component getName() {
        return TELEPORT_TEXT;
    }

    @Override
    public void renderIcon(GuiGraphics $$0, float $$1, float $$2) {
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, TELEPORT_TO_PLAYER_SPRITE, 0, 0, 16, 16, ARGB.colorFromFloat($$2, $$1, $$1, $$1));
    }

    @Override
    public boolean isEnabled() {
        return !this.items.isEmpty();
    }
}

