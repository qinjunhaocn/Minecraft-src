/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  org.jetbrains.annotations.Contract
 */
package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Abilities;
import org.jetbrains.annotations.Contract;

public final class GameType
extends Enum<GameType>
implements StringRepresentable {
    public static final /* enum */ GameType SURVIVAL = new GameType(0, "survival");
    public static final /* enum */ GameType CREATIVE = new GameType(1, "creative");
    public static final /* enum */ GameType ADVENTURE = new GameType(2, "adventure");
    public static final /* enum */ GameType SPECTATOR = new GameType(3, "spectator");
    public static final GameType DEFAULT_MODE;
    public static final StringRepresentable.EnumCodec<GameType> CODEC;
    private static final IntFunction<GameType> BY_ID;
    public static final StreamCodec<ByteBuf, GameType> STREAM_CODEC;
    @Deprecated
    public static final Codec<GameType> LEGACY_ID_CODEC;
    private static final int NOT_SET = -1;
    private final int id;
    private final String name;
    private final Component shortName;
    private final Component longName;
    private static final /* synthetic */ GameType[] $VALUES;

    public static GameType[] values() {
        return (GameType[])$VALUES.clone();
    }

    public static GameType valueOf(String $$0) {
        return Enum.valueOf(GameType.class, $$0);
    }

    private GameType(int $$0, String $$1) {
        this.id = $$0;
        this.name = $$1;
        this.shortName = Component.translatable("selectWorld.gameMode." + $$1);
        this.longName = Component.translatable("gameMode." + $$1);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Component getLongDisplayName() {
        return this.longName;
    }

    public Component getShortDisplayName() {
        return this.shortName;
    }

    public void updatePlayerAbilities(Abilities $$0) {
        if (this == CREATIVE) {
            $$0.mayfly = true;
            $$0.instabuild = true;
            $$0.invulnerable = true;
        } else if (this == SPECTATOR) {
            $$0.mayfly = true;
            $$0.instabuild = false;
            $$0.invulnerable = true;
            $$0.flying = true;
        } else {
            $$0.mayfly = false;
            $$0.instabuild = false;
            $$0.invulnerable = false;
            $$0.flying = false;
        }
        $$0.mayBuild = !this.isBlockPlacingRestricted();
    }

    public boolean isBlockPlacingRestricted() {
        return this == ADVENTURE || this == SPECTATOR;
    }

    public boolean isCreative() {
        return this == CREATIVE;
    }

    public boolean isSurvival() {
        return this == SURVIVAL || this == ADVENTURE;
    }

    public static GameType byId(int $$0) {
        return BY_ID.apply($$0);
    }

    public static GameType byName(String $$0) {
        return GameType.byName($$0, SURVIVAL);
    }

    @Nullable
    @Contract(value="_,!null->!null;_,null->_")
    public static GameType byName(String $$0, @Nullable GameType $$1) {
        GameType $$2 = CODEC.byName($$0);
        return $$2 != null ? $$2 : $$1;
    }

    public static int getNullableId(@Nullable GameType $$0) {
        return $$0 != null ? $$0.id : -1;
    }

    @Nullable
    public static GameType byNullableId(int $$0) {
        if ($$0 == -1) {
            return null;
        }
        return GameType.byId($$0);
    }

    public static boolean isValidId(int $$0) {
        return Arrays.stream(GameType.values()).anyMatch($$1 -> $$1.id == $$0);
    }

    private static /* synthetic */ GameType[] i() {
        return new GameType[]{SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR};
    }

    static {
        $VALUES = GameType.i();
        DEFAULT_MODE = SURVIVAL;
        CODEC = StringRepresentable.fromEnum(GameType::values);
        BY_ID = ByIdMap.a(GameType::getId, GameType.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GameType::getId);
        LEGACY_ID_CODEC = Codec.INT.xmap(GameType::byId, GameType::getId);
    }
}

