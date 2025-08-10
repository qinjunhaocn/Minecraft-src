/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.yggdrasil.ProfileResult
 */
package net.minecraft.client.gui.screens.inventory.tooltip;

import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class ClientActivePlayersTooltip
implements ClientTooltipComponent {
    private static final int SKIN_SIZE = 10;
    private static final int PADDING = 2;
    private final List<ProfileResult> activePlayers;

    public ClientActivePlayersTooltip(ActivePlayersTooltip $$0) {
        this.activePlayers = $$0.profiles();
    }

    @Override
    public int getHeight(Font $$0) {
        return this.activePlayers.size() * 12 + 2;
    }

    @Override
    public int getWidth(Font $$0) {
        int $$1 = 0;
        for (ProfileResult $$2 : this.activePlayers) {
            int $$3 = $$0.width($$2.profile().getName());
            if ($$3 <= $$1) continue;
            $$1 = $$3;
        }
        return $$1 + 10 + 6;
    }

    @Override
    public void renderImage(Font $$0, int $$1, int $$2, int $$3, int $$4, GuiGraphics $$5) {
        for (int $$6 = 0; $$6 < this.activePlayers.size(); ++$$6) {
            ProfileResult $$7 = this.activePlayers.get($$6);
            int $$8 = $$2 + 2 + $$6 * 12;
            PlayerFaceRenderer.draw($$5, Minecraft.getInstance().getSkinManager().getInsecureSkin($$7.profile()), $$1 + 2, $$8, 10);
            $$5.drawString($$0, $$7.profile().getName(), $$1 + 10 + 4, $$8 + 2, -1);
        }
    }

    public record ActivePlayersTooltip(List<ProfileResult> profiles) implements TooltipComponent
    {
    }
}

