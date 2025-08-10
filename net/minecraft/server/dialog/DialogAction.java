/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.server.dialog;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public final class DialogAction
extends Enum<DialogAction>
implements StringRepresentable {
    public static final /* enum */ DialogAction CLOSE = new DialogAction(0, "close");
    public static final /* enum */ DialogAction NONE = new DialogAction(1, "none");
    public static final /* enum */ DialogAction WAIT_FOR_RESPONSE = new DialogAction(2, "wait_for_response");
    public static final IntFunction<DialogAction> BY_ID;
    public static final StringRepresentable.EnumCodec<DialogAction> CODEC;
    public static final StreamCodec<ByteBuf, DialogAction> STREAM_CODEC;
    private final int id;
    private final String name;
    private static final /* synthetic */ DialogAction[] $VALUES;

    public static DialogAction[] values() {
        return (DialogAction[])$VALUES.clone();
    }

    public static DialogAction valueOf(String $$0) {
        return Enum.valueOf(DialogAction.class, $$0);
    }

    private DialogAction(int $$0, String $$1) {
        this.id = $$0;
        this.name = $$1;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean willUnpause() {
        return this == CLOSE || this == WAIT_FOR_RESPONSE;
    }

    private static /* synthetic */ DialogAction[] b() {
        return new DialogAction[]{CLOSE, NONE, WAIT_FOR_RESPONSE};
    }

    static {
        $VALUES = DialogAction.b();
        BY_ID = ByIdMap.a($$0 -> $$0.id, DialogAction.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        CODEC = StringRepresentable.fromEnum(DialogAction::values);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
    }
}

