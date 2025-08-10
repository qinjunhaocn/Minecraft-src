/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.resources.metadata.gui;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public interface GuiSpriteScaling {
    public static final Codec<GuiSpriteScaling> CODEC = Type.CODEC.dispatch(GuiSpriteScaling::type, Type::codec);
    public static final GuiSpriteScaling DEFAULT = new Stretch();

    public Type type();

    public static final class Type
    extends Enum<Type>
    implements StringRepresentable {
        public static final /* enum */ Type STRETCH = new Type("stretch", Stretch.CODEC);
        public static final /* enum */ Type TILE = new Type("tile", Tile.CODEC);
        public static final /* enum */ Type NINE_SLICE = new Type("nine_slice", NineSlice.CODEC);
        public static final Codec<Type> CODEC;
        private final String key;
        private final MapCodec<? extends GuiSpriteScaling> codec;
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private Type(String $$0, MapCodec<? extends GuiSpriteScaling> $$1) {
            this.key = $$0;
            this.codec = $$1;
        }

        @Override
        public String getSerializedName() {
            return this.key;
        }

        public MapCodec<? extends GuiSpriteScaling> codec() {
            return this.codec;
        }

        private static /* synthetic */ Type[] b() {
            return new Type[]{STRETCH, TILE, NINE_SLICE};
        }

        static {
            $VALUES = Type.b();
            CODEC = StringRepresentable.fromEnum(Type::values);
        }
    }

    public record Stretch() implements GuiSpriteScaling
    {
        public static final MapCodec<Stretch> CODEC = MapCodec.unit(Stretch::new);

        @Override
        public Type type() {
            return Type.STRETCH;
        }
    }

    public record NineSlice(int width, int height, Border border, boolean stretchInner) implements GuiSpriteScaling
    {
        public static final MapCodec<NineSlice> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(NineSlice::width), (App)ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(NineSlice::height), (App)Border.CODEC.fieldOf("border").forGetter(NineSlice::border), (App)Codec.BOOL.optionalFieldOf("stretch_inner", (Object)false).forGetter(NineSlice::stretchInner)).apply((Applicative)$$0, NineSlice::new)).validate(NineSlice::validate);

        private static DataResult<NineSlice> validate(NineSlice $$0) {
            Border $$1 = $$0.border();
            if ($$1.left() + $$1.right() >= $$0.width()) {
                return DataResult.error(() -> "Nine-sliced texture has no horizontal center slice: " + $$1.left() + " + " + $$1.right() + " >= " + $$0.width());
            }
            if ($$1.top() + $$1.bottom() >= $$0.height()) {
                return DataResult.error(() -> "Nine-sliced texture has no vertical center slice: " + $$1.top() + " + " + $$1.bottom() + " >= " + $$0.height());
            }
            return DataResult.success((Object)$$0);
        }

        @Override
        public Type type() {
            return Type.NINE_SLICE;
        }

        public record Border(int left, int top, int right, int bottom) {
            private static final Codec<Border> VALUE_CODEC = ExtraCodecs.POSITIVE_INT.flatComapMap($$0 -> new Border((int)$$0, (int)$$0, (int)$$0, (int)$$0), $$0 -> {
                OptionalInt $$1 = $$0.unpackValue();
                if ($$1.isPresent()) {
                    return DataResult.success((Object)$$1.getAsInt());
                }
                return DataResult.error(() -> "Border has different side sizes");
            });
            private static final Codec<Border> RECORD_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("left").forGetter(Border::left), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("top").forGetter(Border::top), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("right").forGetter(Border::right), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("bottom").forGetter(Border::bottom)).apply((Applicative)$$0, Border::new));
            static final Codec<Border> CODEC = Codec.either(VALUE_CODEC, RECORD_CODEC).xmap(Either::unwrap, $$0 -> {
                if ($$0.unpackValue().isPresent()) {
                    return Either.left((Object)$$0);
                }
                return Either.right((Object)$$0);
            });

            private OptionalInt unpackValue() {
                if (this.left() == this.top() && this.top() == this.right() && this.right() == this.bottom()) {
                    return OptionalInt.of(this.left());
                }
                return OptionalInt.empty();
            }
        }
    }

    public record Tile(int width, int height) implements GuiSpriteScaling
    {
        public static final MapCodec<Tile> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(Tile::width), (App)ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(Tile::height)).apply((Applicative)$$0, Tile::new));

        @Override
        public Type type() {
            return Type.TILE;
        }
    }
}

