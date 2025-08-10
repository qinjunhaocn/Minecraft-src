/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ServerboundSeenAdvancementsPacket
implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundSeenAdvancementsPacket> STREAM_CODEC = Packet.codec(ServerboundSeenAdvancementsPacket::write, ServerboundSeenAdvancementsPacket::new);
    private final Action action;
    @Nullable
    private final ResourceLocation tab;

    public ServerboundSeenAdvancementsPacket(Action $$0, @Nullable ResourceLocation $$1) {
        this.action = $$0;
        this.tab = $$1;
    }

    public static ServerboundSeenAdvancementsPacket openedTab(AdvancementHolder $$0) {
        return new ServerboundSeenAdvancementsPacket(Action.OPENED_TAB, $$0.id());
    }

    public static ServerboundSeenAdvancementsPacket closedScreen() {
        return new ServerboundSeenAdvancementsPacket(Action.CLOSED_SCREEN, null);
    }

    private ServerboundSeenAdvancementsPacket(FriendlyByteBuf $$0) {
        this.action = $$0.readEnum(Action.class);
        this.tab = this.action == Action.OPENED_TAB ? $$0.readResourceLocation() : null;
    }

    private void write(FriendlyByteBuf $$0) {
        $$0.writeEnum(this.action);
        if (this.action == Action.OPENED_TAB) {
            $$0.writeResourceLocation(this.tab);
        }
    }

    @Override
    public PacketType<ServerboundSeenAdvancementsPacket> type() {
        return GamePacketTypes.SERVERBOUND_SEEN_ADVANCEMENTS;
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleSeenAdvancements(this);
    }

    public Action getAction() {
        return this.action;
    }

    @Nullable
    public ResourceLocation getTab() {
        return this.tab;
    }

    public static final class Action
    extends Enum<Action> {
        public static final /* enum */ Action OPENED_TAB = new Action();
        public static final /* enum */ Action CLOSED_SCREEN = new Action();
        private static final /* synthetic */ Action[] $VALUES;

        public static Action[] values() {
            return (Action[])$VALUES.clone();
        }

        public static Action valueOf(String $$0) {
            return Enum.valueOf(Action.class, $$0);
        }

        private static /* synthetic */ Action[] a() {
            return new Action[]{OPENED_TAB, CLOSED_SCREEN};
        }

        static {
            $VALUES = Action.a();
        }
    }
}

