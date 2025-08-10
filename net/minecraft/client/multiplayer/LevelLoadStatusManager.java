/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.multiplayer;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;

public class LevelLoadStatusManager {
    private final LocalPlayer player;
    private final ClientLevel level;
    private final LevelRenderer levelRenderer;
    private Status status = Status.WAITING_FOR_SERVER;

    public LevelLoadStatusManager(LocalPlayer $$0, ClientLevel $$1, LevelRenderer $$2) {
        this.player = $$0;
        this.level = $$1;
        this.levelRenderer = $$2;
    }

    public void tick() {
        switch (this.status.ordinal()) {
            case 0: 
            case 2: {
                break;
            }
            case 1: {
                BlockPos $$0 = this.player.blockPosition();
                boolean $$1 = this.level.isOutsideBuildHeight($$0.getY());
                if (!$$1 && !this.levelRenderer.isSectionCompiled($$0) && !this.player.isSpectator() && this.player.isAlive()) break;
                this.status = Status.LEVEL_READY;
            }
        }
    }

    public boolean levelReady() {
        return this.status == Status.LEVEL_READY;
    }

    public void loadingPacketsReceived() {
        if (this.status == Status.WAITING_FOR_SERVER) {
            this.status = Status.WAITING_FOR_PLAYER_CHUNK;
        }
    }

    static final class Status
    extends Enum<Status> {
        public static final /* enum */ Status WAITING_FOR_SERVER = new Status();
        public static final /* enum */ Status WAITING_FOR_PLAYER_CHUNK = new Status();
        public static final /* enum */ Status LEVEL_READY = new Status();
        private static final /* synthetic */ Status[] $VALUES;

        public static Status[] values() {
            return (Status[])$VALUES.clone();
        }

        public static Status valueOf(String $$0) {
            return Enum.valueOf(Status.class, $$0);
        }

        private static /* synthetic */ Status[] a() {
            return new Status[]{WAITING_FOR_SERVER, WAITING_FOR_PLAYER_CHUNK, LEVEL_READY};
        }

        static {
            $VALUES = Status.a();
        }
    }
}

