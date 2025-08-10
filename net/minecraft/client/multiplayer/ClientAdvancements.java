/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 */
package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class ClientAdvancements {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    private final WorldSessionTelemetryManager telemetryManager;
    private final AdvancementTree tree = new AdvancementTree();
    private final Map<AdvancementHolder, AdvancementProgress> progress = new Object2ObjectOpenHashMap();
    @Nullable
    private Listener listener;
    @Nullable
    private AdvancementHolder selectedTab;

    public ClientAdvancements(Minecraft $$0, WorldSessionTelemetryManager $$1) {
        this.minecraft = $$0;
        this.telemetryManager = $$1;
    }

    public void update(ClientboundUpdateAdvancementsPacket $$0) {
        if ($$0.shouldReset()) {
            this.tree.clear();
            this.progress.clear();
        }
        this.tree.remove($$0.getRemoved());
        this.tree.addAll($$0.getAdded());
        for (Map.Entry<ResourceLocation, AdvancementProgress> $$1 : $$0.getProgress().entrySet()) {
            AdvancementNode $$2 = this.tree.get($$1.getKey());
            if ($$2 != null) {
                AdvancementProgress $$3 = $$1.getValue();
                $$3.update($$2.advancement().requirements());
                this.progress.put($$2.holder(), $$3);
                if (this.listener != null) {
                    this.listener.onUpdateAdvancementProgress($$2, $$3);
                }
                if ($$0.shouldReset() || !$$3.isDone()) continue;
                if (this.minecraft.level != null) {
                    this.telemetryManager.onAdvancementDone(this.minecraft.level, $$2.holder());
                }
                Optional<DisplayInfo> $$4 = $$2.advancement().display();
                if (!$$0.shouldShowAdvancements() || !$$4.isPresent() || !$$4.get().shouldShowToast()) continue;
                this.minecraft.getToastManager().addToast(new AdvancementToast($$2.holder()));
                continue;
            }
            LOGGER.warn("Server informed client about progress for unknown advancement {}", (Object)$$1.getKey());
        }
    }

    public AdvancementTree getTree() {
        return this.tree;
    }

    public void setSelectedTab(@Nullable AdvancementHolder $$0, boolean $$1) {
        ClientPacketListener $$2 = this.minecraft.getConnection();
        if ($$2 != null && $$0 != null && $$1) {
            $$2.send(ServerboundSeenAdvancementsPacket.openedTab($$0));
        }
        if (this.selectedTab != $$0) {
            this.selectedTab = $$0;
            if (this.listener != null) {
                this.listener.onSelectedTabChanged($$0);
            }
        }
    }

    public void setListener(@Nullable Listener $$0) {
        this.listener = $$0;
        this.tree.setListener($$0);
        if ($$0 != null) {
            this.progress.forEach(($$1, $$2) -> {
                AdvancementNode $$3 = this.tree.get((AdvancementHolder)((Object)$$1));
                if ($$3 != null) {
                    $$0.onUpdateAdvancementProgress($$3, (AdvancementProgress)$$2);
                }
            });
            $$0.onSelectedTabChanged(this.selectedTab);
        }
    }

    @Nullable
    public AdvancementHolder get(ResourceLocation $$0) {
        AdvancementNode $$1 = this.tree.get($$0);
        return $$1 != null ? $$1.holder() : null;
    }

    public static interface Listener
    extends AdvancementTree.Listener {
        public void onUpdateAdvancementProgress(AdvancementNode var1, AdvancementProgress var2);

        public void onSelectedTabChanged(@Nullable AdvancementHolder var1);
    }
}

