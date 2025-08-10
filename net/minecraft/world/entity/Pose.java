/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.entity;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public final class Pose
extends Enum<Pose> {
    public static final /* enum */ Pose STANDING = new Pose(0);
    public static final /* enum */ Pose FALL_FLYING = new Pose(1);
    public static final /* enum */ Pose SLEEPING = new Pose(2);
    public static final /* enum */ Pose SWIMMING = new Pose(3);
    public static final /* enum */ Pose SPIN_ATTACK = new Pose(4);
    public static final /* enum */ Pose CROUCHING = new Pose(5);
    public static final /* enum */ Pose LONG_JUMPING = new Pose(6);
    public static final /* enum */ Pose DYING = new Pose(7);
    public static final /* enum */ Pose CROAKING = new Pose(8);
    public static final /* enum */ Pose USING_TONGUE = new Pose(9);
    public static final /* enum */ Pose SITTING = new Pose(10);
    public static final /* enum */ Pose ROARING = new Pose(11);
    public static final /* enum */ Pose SNIFFING = new Pose(12);
    public static final /* enum */ Pose EMERGING = new Pose(13);
    public static final /* enum */ Pose DIGGING = new Pose(14);
    public static final /* enum */ Pose SLIDING = new Pose(15);
    public static final /* enum */ Pose SHOOTING = new Pose(16);
    public static final /* enum */ Pose INHALING = new Pose(17);
    public static final IntFunction<Pose> BY_ID;
    public static final StreamCodec<ByteBuf, Pose> STREAM_CODEC;
    private final int id;
    private static final /* synthetic */ Pose[] $VALUES;

    public static Pose[] values() {
        return (Pose[])$VALUES.clone();
    }

    public static Pose valueOf(String $$0) {
        return Enum.valueOf(Pose.class, $$0);
    }

    private Pose(int $$0) {
        this.id = $$0;
    }

    public int id() {
        return this.id;
    }

    private static /* synthetic */ Pose[] b() {
        return new Pose[]{STANDING, FALL_FLYING, SLEEPING, SWIMMING, SPIN_ATTACK, CROUCHING, LONG_JUMPING, DYING, CROAKING, USING_TONGUE, SITTING, ROARING, SNIFFING, EMERGING, DIGGING, SLIDING, SHOOTING, INHALING};
    }

    static {
        $VALUES = Pose.b();
        BY_ID = ByIdMap.a(Pose::id, Pose.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Pose::id);
    }
}

