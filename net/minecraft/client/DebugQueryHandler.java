/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQueryPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQueryPacket;

public class DebugQueryHandler {
    private final ClientPacketListener connection;
    private int transactionId = -1;
    @Nullable
    private Consumer<CompoundTag> callback;

    public DebugQueryHandler(ClientPacketListener $$0) {
        this.connection = $$0;
    }

    public boolean handleResponse(int $$0, @Nullable CompoundTag $$1) {
        if (this.transactionId == $$0 && this.callback != null) {
            this.callback.accept($$1);
            this.callback = null;
            return true;
        }
        return false;
    }

    private int startTransaction(Consumer<CompoundTag> $$0) {
        this.callback = $$0;
        return ++this.transactionId;
    }

    public void queryEntityTag(int $$0, Consumer<CompoundTag> $$1) {
        int $$2 = this.startTransaction($$1);
        this.connection.send(new ServerboundEntityTagQueryPacket($$2, $$0));
    }

    public void queryBlockEntityTag(BlockPos $$0, Consumer<CompoundTag> $$1) {
        int $$2 = this.startTransaction($$1);
        this.connection.send(new ServerboundBlockEntityTagQueryPacket($$2, $$0));
    }
}

