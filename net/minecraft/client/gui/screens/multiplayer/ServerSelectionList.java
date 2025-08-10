/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.gui.screens.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.server.LanServer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.slf4j.Logger;

public class ServerSelectionList
extends ObjectSelectionList<Entry> {
    static final ResourceLocation INCOMPATIBLE_SPRITE = ResourceLocation.withDefaultNamespace("server_list/incompatible");
    static final ResourceLocation UNREACHABLE_SPRITE = ResourceLocation.withDefaultNamespace("server_list/unreachable");
    static final ResourceLocation PING_1_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_1");
    static final ResourceLocation PING_2_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_2");
    static final ResourceLocation PING_3_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_3");
    static final ResourceLocation PING_4_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_4");
    static final ResourceLocation PING_5_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_5");
    static final ResourceLocation PINGING_1_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_1");
    static final ResourceLocation PINGING_2_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_2");
    static final ResourceLocation PINGING_3_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_3");
    static final ResourceLocation PINGING_4_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_4");
    static final ResourceLocation PINGING_5_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_5");
    static final ResourceLocation JOIN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("server_list/join_highlighted");
    static final ResourceLocation JOIN_SPRITE = ResourceLocation.withDefaultNamespace("server_list/join");
    static final ResourceLocation MOVE_UP_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("server_list/move_up_highlighted");
    static final ResourceLocation MOVE_UP_SPRITE = ResourceLocation.withDefaultNamespace("server_list/move_up");
    static final ResourceLocation MOVE_DOWN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("server_list/move_down_highlighted");
    static final ResourceLocation MOVE_DOWN_SPRITE = ResourceLocation.withDefaultNamespace("server_list/move_down");
    static final Logger LOGGER = LogUtils.getLogger();
    static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
    static final Component SCANNING_LABEL = Component.translatable("lanServer.scanning");
    static final Component CANT_RESOLVE_TEXT = Component.translatable("multiplayer.status.cannot_resolve").withColor(-65536);
    static final Component CANT_CONNECT_TEXT = Component.translatable("multiplayer.status.cannot_connect").withColor(-65536);
    static final Component INCOMPATIBLE_STATUS = Component.translatable("multiplayer.status.incompatible");
    static final Component NO_CONNECTION_STATUS = Component.translatable("multiplayer.status.no_connection");
    static final Component PINGING_STATUS = Component.translatable("multiplayer.status.pinging");
    static final Component ONLINE_STATUS = Component.translatable("multiplayer.status.online");
    private final JoinMultiplayerScreen screen;
    private final List<OnlineServerEntry> onlineServers = Lists.newArrayList();
    private final Entry lanHeader = new LANHeader();
    private final List<NetworkServerEntry> networkServers = Lists.newArrayList();

    public ServerSelectionList(JoinMultiplayerScreen $$0, Minecraft $$1, int $$2, int $$3, int $$4, int $$5) {
        super($$1, $$2, $$3, $$4, $$5);
        this.screen = $$0;
    }

    private void refreshEntries() {
        this.clearEntries();
        this.onlineServers.forEach($$1 -> this.addEntry($$1));
        this.addEntry(this.lanHeader);
        this.networkServers.forEach($$1 -> this.addEntry($$1));
    }

    @Override
    public void setSelected(@Nullable Entry $$0) {
        super.setSelected($$0);
        this.screen.onSelectedChange();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        Entry $$3 = (Entry)this.getSelected();
        return $$3 != null && $$3.keyPressed($$0, $$1, $$2) || super.keyPressed($$0, $$1, $$2);
    }

    public void updateOnlineServers(ServerList $$0) {
        this.onlineServers.clear();
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            this.onlineServers.add(new OnlineServerEntry(this.screen, $$0.get($$1)));
        }
        this.refreshEntries();
    }

    public void updateNetworkServers(List<LanServer> $$0) {
        int $$1 = $$0.size() - this.networkServers.size();
        this.networkServers.clear();
        for (LanServer $$2 : $$0) {
            this.networkServers.add(new NetworkServerEntry(this.screen, $$2));
        }
        this.refreshEntries();
        for (int $$3 = this.networkServers.size() - $$1; $$3 < this.networkServers.size(); ++$$3) {
            NetworkServerEntry $$4 = this.networkServers.get($$3);
            int $$5 = $$3 - this.networkServers.size() + this.children().size();
            int $$6 = this.getRowTop($$5);
            int $$7 = this.getRowBottom($$5);
            if ($$7 < this.getY() || $$6 > this.getBottom()) continue;
            this.minecraft.getNarrator().saySystemQueued(Component.a("multiplayer.lan.server_found", $$4.getServerNarration()));
        }
    }

    @Override
    public int getRowWidth() {
        return 305;
    }

    public void removed() {
    }

    public static class LANHeader
    extends Entry {
        private final Minecraft minecraft = Minecraft.getInstance();

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            int $$10 = $$2 + $$5 / 2 - this.minecraft.font.lineHeight / 2;
            $$0.drawString(this.minecraft.font, SCANNING_LABEL, this.minecraft.screen.width / 2 - this.minecraft.font.width(SCANNING_LABEL) / 2, $$10, -1);
            String $$11 = LoadingDotsText.get(Util.getMillis());
            $$0.drawString(this.minecraft.font, $$11, this.minecraft.screen.width / 2 - this.minecraft.font.width($$11) / 2, $$10 + this.minecraft.font.lineHeight, -8355712);
        }

        @Override
        public Component getNarration() {
            return SCANNING_LABEL;
        }
    }

    public static abstract class Entry
    extends ObjectSelectionList.Entry<Entry>
    implements AutoCloseable {
        @Override
        public void close() {
        }
    }

    public class OnlineServerEntry
    extends Entry {
        private static final int ICON_WIDTH = 32;
        private static final int ICON_HEIGHT = 32;
        private static final int SPACING = 5;
        private static final int STATUS_ICON_WIDTH = 10;
        private static final int STATUS_ICON_HEIGHT = 8;
        private final JoinMultiplayerScreen screen;
        private final Minecraft minecraft;
        private final ServerData serverData;
        private final FaviconTexture icon;
        @Nullable
        private byte[] lastIconBytes;
        private long lastClickTime;
        @Nullable
        private List<Component> onlinePlayersTooltip;
        @Nullable
        private ResourceLocation statusIcon;
        @Nullable
        private Component statusIconTooltip;

        protected OnlineServerEntry(JoinMultiplayerScreen $$1, ServerData $$2) {
            this.screen = $$1;
            this.serverData = $$2;
            this.minecraft = Minecraft.getInstance();
            this.icon = FaviconTexture.forServer(this.minecraft.getTextureManager(), $$2.ip);
            this.refreshStatus();
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            byte[] $$14;
            if (this.serverData.state() == ServerData.State.INITIAL) {
                this.serverData.setState(ServerData.State.PINGING);
                this.serverData.motd = CommonComponents.EMPTY;
                this.serverData.status = CommonComponents.EMPTY;
                THREAD_POOL.submit(() -> {
                    try {
                        this.screen.getPinger().pingServer(this.serverData, () -> this.minecraft.execute(this::updateServerList), () -> {
                            this.serverData.setState(this.serverData.protocol == SharedConstants.getCurrentVersion().protocolVersion() ? ServerData.State.SUCCESSFUL : ServerData.State.INCOMPATIBLE);
                            this.minecraft.execute(this::refreshStatus);
                        });
                    } catch (UnknownHostException $$0) {
                        this.serverData.setState(ServerData.State.UNREACHABLE);
                        this.serverData.motd = CANT_RESOLVE_TEXT;
                        this.minecraft.execute(this::refreshStatus);
                    } catch (Exception $$1) {
                        this.serverData.setState(ServerData.State.UNREACHABLE);
                        this.serverData.motd = CANT_CONNECT_TEXT;
                        this.minecraft.execute(this::refreshStatus);
                    }
                });
            }
            $$0.drawString(this.minecraft.font, this.serverData.name, $$3 + 32 + 3, $$2 + 1, -1);
            List<FormattedCharSequence> $$10 = this.minecraft.font.split(this.serverData.motd, $$4 - 32 - 2);
            for (int $$11 = 0; $$11 < Math.min($$10.size(), 2); ++$$11) {
                $$0.drawString(this.minecraft.font, $$10.get($$11), $$3 + 32 + 3, $$2 + 12 + this.minecraft.font.lineHeight * $$11, -8355712);
            }
            this.drawIcon($$0, $$3, $$2, this.icon.textureLocation());
            if (this.serverData.state() == ServerData.State.PINGING) {
                int $$12 = (int)(Util.getMillis() / 100L + (long)($$1 * 2) & 7L);
                if ($$12 > 4) {
                    $$12 = 8 - $$12;
                }
                this.statusIcon = switch ($$12) {
                    default -> PINGING_1_SPRITE;
                    case 1 -> PINGING_2_SPRITE;
                    case 2 -> PINGING_3_SPRITE;
                    case 3 -> PINGING_4_SPRITE;
                    case 4 -> PINGING_5_SPRITE;
                };
            }
            int $$13 = $$3 + $$4 - 10 - 5;
            if (this.statusIcon != null) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.statusIcon, $$13, $$2, 10, 8);
            }
            if (!Arrays.equals($$14 = this.serverData.c(), this.lastIconBytes)) {
                if (this.a($$14)) {
                    this.lastIconBytes = $$14;
                } else {
                    this.serverData.a(null);
                    this.updateServerList();
                }
            }
            Component $$15 = this.serverData.state() == ServerData.State.INCOMPATIBLE ? this.serverData.version.copy().withStyle(ChatFormatting.RED) : this.serverData.status;
            int $$16 = this.minecraft.font.width($$15);
            int $$17 = $$13 - $$16 - 5;
            $$0.drawString(this.minecraft.font, $$15, $$17, $$2 + 1, -8355712);
            if (this.statusIconTooltip != null && $$6 >= $$13 && $$6 <= $$13 + 10 && $$7 >= $$2 && $$7 <= $$2 + 8) {
                $$0.setTooltipForNextFrame(this.statusIconTooltip, $$6, $$7);
            } else if (this.onlinePlayersTooltip != null && $$6 >= $$17 && $$6 <= $$17 + $$16 && $$7 >= $$2 && $$7 <= $$2 - 1 + this.minecraft.font.lineHeight) {
                $$0.setTooltipForNextFrame(Lists.transform(this.onlinePlayersTooltip, Component::getVisualOrderText), $$6, $$7);
            }
            if (this.minecraft.options.touchscreen().get().booleanValue() || $$8) {
                $$0.fill($$3, $$2, $$3 + 32, $$2 + 32, -1601138544);
                int $$18 = $$6 - $$3;
                int $$19 = $$7 - $$2;
                if (this.canJoin()) {
                    if ($$18 < 32 && $$18 > 16) {
                        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, JOIN_HIGHLIGHTED_SPRITE, $$3, $$2, 32, 32);
                    } else {
                        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, JOIN_SPRITE, $$3, $$2, 32, 32);
                    }
                }
                if ($$1 > 0) {
                    if ($$18 < 16 && $$19 < 16) {
                        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, MOVE_UP_HIGHLIGHTED_SPRITE, $$3, $$2, 32, 32);
                    } else {
                        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, MOVE_UP_SPRITE, $$3, $$2, 32, 32);
                    }
                }
                if ($$1 < this.screen.getServers().size() - 1) {
                    if ($$18 < 16 && $$19 > 16) {
                        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, MOVE_DOWN_HIGHLIGHTED_SPRITE, $$3, $$2, 32, 32);
                    } else {
                        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, MOVE_DOWN_SPRITE, $$3, $$2, 32, 32);
                    }
                }
            }
        }

        private void refreshStatus() {
            this.onlinePlayersTooltip = null;
            switch (this.serverData.state()) {
                case INITIAL: 
                case PINGING: {
                    this.statusIcon = PING_1_SPRITE;
                    this.statusIconTooltip = PINGING_STATUS;
                    break;
                }
                case INCOMPATIBLE: {
                    this.statusIcon = INCOMPATIBLE_SPRITE;
                    this.statusIconTooltip = INCOMPATIBLE_STATUS;
                    this.onlinePlayersTooltip = this.serverData.playerList;
                    break;
                }
                case UNREACHABLE: {
                    this.statusIcon = UNREACHABLE_SPRITE;
                    this.statusIconTooltip = NO_CONNECTION_STATUS;
                    break;
                }
                case SUCCESSFUL: {
                    this.statusIcon = this.serverData.ping < 150L ? PING_5_SPRITE : (this.serverData.ping < 300L ? PING_4_SPRITE : (this.serverData.ping < 600L ? PING_3_SPRITE : (this.serverData.ping < 1000L ? PING_2_SPRITE : PING_1_SPRITE)));
                    this.statusIconTooltip = Component.a("multiplayer.status.ping", this.serverData.ping);
                    this.onlinePlayersTooltip = this.serverData.playerList;
                }
            }
        }

        public void updateServerList() {
            this.screen.getServers().save();
        }

        protected void drawIcon(GuiGraphics $$0, int $$1, int $$2, ResourceLocation $$3) {
            $$0.blit(RenderPipelines.GUI_TEXTURED, $$3, $$1, $$2, 0.0f, 0.0f, 32, 32, 32, 32);
        }

        private boolean canJoin() {
            return true;
        }

        private boolean a(@Nullable byte[] $$0) {
            if ($$0 == null) {
                this.icon.clear();
            } else {
                try {
                    this.icon.upload(NativeImage.a($$0));
                } catch (Throwable $$1) {
                    LOGGER.error("Invalid icon for server {} ({})", this.serverData.name, this.serverData.ip, $$1);
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean keyPressed(int $$0, int $$1, int $$2) {
            if (Screen.hasShiftDown()) {
                ServerSelectionList $$3 = this.screen.serverSelectionList;
                int $$4 = $$3.children().indexOf(this);
                if ($$4 == -1) {
                    return true;
                }
                if ($$0 == 264 && $$4 < this.screen.getServers().size() - 1 || $$0 == 265 && $$4 > 0) {
                    this.swap($$4, $$0 == 264 ? $$4 + 1 : $$4 - 1);
                    return true;
                }
            }
            return super.keyPressed($$0, $$1, $$2);
        }

        private void swap(int $$0, int $$1) {
            this.screen.getServers().swap($$0, $$1);
            this.screen.serverSelectionList.updateOnlineServers(this.screen.getServers());
            Entry $$2 = (Entry)this.screen.serverSelectionList.children().get($$1);
            this.screen.serverSelectionList.setSelected($$2);
            ServerSelectionList.this.ensureVisible($$2);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            double $$3 = $$0 - (double)ServerSelectionList.this.getRowLeft();
            double $$4 = $$1 - (double)ServerSelectionList.this.getRowTop(ServerSelectionList.this.children().indexOf(this));
            if ($$3 <= 32.0) {
                if ($$3 < 32.0 && $$3 > 16.0 && this.canJoin()) {
                    this.screen.setSelected(this);
                    this.screen.joinSelectedServer();
                    return true;
                }
                int $$5 = this.screen.serverSelectionList.children().indexOf(this);
                if ($$3 < 16.0 && $$4 < 16.0 && $$5 > 0) {
                    this.swap($$5, $$5 - 1);
                    return true;
                }
                if ($$3 < 16.0 && $$4 > 16.0 && $$5 < this.screen.getServers().size() - 1) {
                    this.swap($$5, $$5 + 1);
                    return true;
                }
            }
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.screen.joinSelectedServer();
            }
            this.lastClickTime = Util.getMillis();
            return super.mouseClicked($$0, $$1, $$2);
        }

        public ServerData getServerData() {
            return this.serverData;
        }

        @Override
        public Component getNarration() {
            MutableComponent $$0 = Component.empty();
            $$0.append(Component.a("narrator.select", this.serverData.name));
            $$0.append(CommonComponents.NARRATION_SEPARATOR);
            switch (this.serverData.state()) {
                case INCOMPATIBLE: {
                    $$0.append(INCOMPATIBLE_STATUS);
                    $$0.append(CommonComponents.NARRATION_SEPARATOR);
                    $$0.append(Component.a("multiplayer.status.version.narration", this.serverData.version));
                    $$0.append(CommonComponents.NARRATION_SEPARATOR);
                    $$0.append(Component.a("multiplayer.status.motd.narration", this.serverData.motd));
                    break;
                }
                case UNREACHABLE: {
                    $$0.append(NO_CONNECTION_STATUS);
                    break;
                }
                case PINGING: {
                    $$0.append(PINGING_STATUS);
                    break;
                }
                default: {
                    $$0.append(ONLINE_STATUS);
                    $$0.append(CommonComponents.NARRATION_SEPARATOR);
                    $$0.append(Component.a("multiplayer.status.ping.narration", this.serverData.ping));
                    $$0.append(CommonComponents.NARRATION_SEPARATOR);
                    $$0.append(Component.a("multiplayer.status.motd.narration", this.serverData.motd));
                    if (this.serverData.players == null) break;
                    $$0.append(CommonComponents.NARRATION_SEPARATOR);
                    $$0.append(Component.a("multiplayer.status.player_count.narration", this.serverData.players.online(), this.serverData.players.max()));
                    $$0.append(CommonComponents.NARRATION_SEPARATOR);
                    $$0.append(ComponentUtils.formatList(this.serverData.playerList, Component.literal(", ")));
                }
            }
            return $$0;
        }

        @Override
        public void close() {
            this.icon.close();
        }
    }

    public static class NetworkServerEntry
    extends Entry {
        private static final int ICON_WIDTH = 32;
        private static final Component LAN_SERVER_HEADER = Component.translatable("lanServer.title");
        private static final Component HIDDEN_ADDRESS_TEXT = Component.translatable("selectServer.hiddenAddress");
        private final JoinMultiplayerScreen screen;
        protected final Minecraft minecraft;
        protected final LanServer serverData;
        private long lastClickTime;

        protected NetworkServerEntry(JoinMultiplayerScreen $$0, LanServer $$1) {
            this.screen = $$0;
            this.serverData = $$1;
            this.minecraft = Minecraft.getInstance();
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            $$0.drawString(this.minecraft.font, LAN_SERVER_HEADER, $$3 + 32 + 3, $$2 + 1, -1);
            $$0.drawString(this.minecraft.font, this.serverData.getMotd(), $$3 + 32 + 3, $$2 + 12, -8355712);
            if (this.minecraft.options.hideServerAddress) {
                $$0.drawString(this.minecraft.font, HIDDEN_ADDRESS_TEXT, $$3 + 32 + 3, $$2 + 12 + 11, -13619152);
            } else {
                $$0.drawString(this.minecraft.font, this.serverData.getAddress(), $$3 + 32 + 3, $$2 + 12 + 11, -13619152);
            }
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.screen.joinSelectedServer();
            }
            this.lastClickTime = Util.getMillis();
            return super.mouseClicked($$0, $$1, $$2);
        }

        public LanServer getServerData() {
            return this.serverData;
        }

        @Override
        public Component getNarration() {
            return Component.a("narrator.select", this.getServerNarration());
        }

        public Component getServerNarration() {
            return Component.empty().append(LAN_SERVER_HEADER).append(CommonComponents.SPACE).append(this.serverData.getMotd());
        }
    }
}

