/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.level.block.state.properties;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public final class TestBlockMode
extends Enum<TestBlockMode>
implements StringRepresentable {
    public static final /* enum */ TestBlockMode START = new TestBlockMode(0, "start");
    public static final /* enum */ TestBlockMode LOG = new TestBlockMode(1, "log");
    public static final /* enum */ TestBlockMode FAIL = new TestBlockMode(2, "fail");
    public static final /* enum */ TestBlockMode ACCEPT = new TestBlockMode(3, "accept");
    private static final IntFunction<TestBlockMode> BY_ID;
    public static final Codec<TestBlockMode> CODEC;
    public static final StreamCodec<ByteBuf, TestBlockMode> STREAM_CODEC;
    private final int id;
    private final String name;
    private final Component displayName;
    private final Component detailedMessage;
    private static final /* synthetic */ TestBlockMode[] $VALUES;

    public static TestBlockMode[] values() {
        return (TestBlockMode[])$VALUES.clone();
    }

    public static TestBlockMode valueOf(String $$0) {
        return Enum.valueOf(TestBlockMode.class, $$0);
    }

    private TestBlockMode(int $$0, String $$1) {
        this.id = $$0;
        this.name = $$1;
        this.displayName = Component.translatable("test_block.mode." + $$1);
        this.detailedMessage = Component.translatable("test_block.mode_info." + $$1);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public Component getDetailedMessage() {
        return this.detailedMessage;
    }

    private static /* synthetic */ TestBlockMode[] d() {
        return new TestBlockMode[]{START, LOG, FAIL, ACCEPT};
    }

    static {
        $VALUES = TestBlockMode.d();
        BY_ID = ByIdMap.a($$0 -> $$0.id, TestBlockMode.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        CODEC = StringRepresentable.fromEnum(TestBlockMode::values);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, $$0 -> $$0.id);
    }
}

