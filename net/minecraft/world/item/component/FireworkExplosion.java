/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.world.item.component;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record FireworkExplosion(Shape shape, IntList colors, IntList fadeColors, boolean hasTrail, boolean hasTwinkle) implements TooltipProvider
{
    public static final FireworkExplosion DEFAULT = new FireworkExplosion(Shape.SMALL_BALL, IntList.of(), IntList.of(), false, false);
    public static final Codec<IntList> COLOR_LIST_CODEC = Codec.INT.listOf().xmap(IntArrayList::new, ArrayList::new);
    public static final Codec<FireworkExplosion> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Shape.CODEC.fieldOf("shape").forGetter(FireworkExplosion::shape), (App)COLOR_LIST_CODEC.optionalFieldOf("colors", (Object)IntList.of()).forGetter(FireworkExplosion::colors), (App)COLOR_LIST_CODEC.optionalFieldOf("fade_colors", (Object)IntList.of()).forGetter(FireworkExplosion::fadeColors), (App)Codec.BOOL.optionalFieldOf("has_trail", (Object)false).forGetter(FireworkExplosion::hasTrail), (App)Codec.BOOL.optionalFieldOf("has_twinkle", (Object)false).forGetter(FireworkExplosion::hasTwinkle)).apply((Applicative)$$0, FireworkExplosion::new));
    private static final StreamCodec<ByteBuf, IntList> COLOR_LIST_STREAM_CODEC = ByteBufCodecs.INT.apply(ByteBufCodecs.list()).map(IntArrayList::new, ArrayList::new);
    public static final StreamCodec<ByteBuf, FireworkExplosion> STREAM_CODEC = StreamCodec.composite(Shape.STREAM_CODEC, FireworkExplosion::shape, COLOR_LIST_STREAM_CODEC, FireworkExplosion::colors, COLOR_LIST_STREAM_CODEC, FireworkExplosion::fadeColors, ByteBufCodecs.BOOL, FireworkExplosion::hasTrail, ByteBufCodecs.BOOL, FireworkExplosion::hasTwinkle, FireworkExplosion::new);
    private static final Component CUSTOM_COLOR_NAME = Component.translatable("item.minecraft.firework_star.custom_color");

    @Override
    public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
        $$1.accept(this.shape.getName().withStyle(ChatFormatting.GRAY));
        this.addAdditionalTooltip($$1);
    }

    public void addAdditionalTooltip(Consumer<Component> $$0) {
        if (!this.colors.isEmpty()) {
            $$0.accept(FireworkExplosion.appendColors(Component.empty().withStyle(ChatFormatting.GRAY), this.colors));
        }
        if (!this.fadeColors.isEmpty()) {
            $$0.accept(FireworkExplosion.appendColors(Component.translatable("item.minecraft.firework_star.fade_to").append(CommonComponents.SPACE).withStyle(ChatFormatting.GRAY), this.fadeColors));
        }
        if (this.hasTrail) {
            $$0.accept(Component.translatable("item.minecraft.firework_star.trail").withStyle(ChatFormatting.GRAY));
        }
        if (this.hasTwinkle) {
            $$0.accept(Component.translatable("item.minecraft.firework_star.flicker").withStyle(ChatFormatting.GRAY));
        }
    }

    private static Component appendColors(MutableComponent $$0, IntList $$1) {
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            if ($$2 > 0) {
                $$0.append(", ");
            }
            $$0.append(FireworkExplosion.getColorName($$1.getInt($$2)));
        }
        return $$0;
    }

    private static Component getColorName(int $$0) {
        DyeColor $$1 = DyeColor.byFireworkColor($$0);
        if ($$1 == null) {
            return CUSTOM_COLOR_NAME;
        }
        return Component.translatable("item.minecraft.firework_star." + $$1.getName());
    }

    public FireworkExplosion withFadeColors(IntList $$0) {
        return new FireworkExplosion(this.shape, this.colors, (IntList)new IntArrayList($$0), this.hasTrail, this.hasTwinkle);
    }

    public static final class Shape
    extends Enum<Shape>
    implements StringRepresentable {
        public static final /* enum */ Shape SMALL_BALL = new Shape(0, "small_ball");
        public static final /* enum */ Shape LARGE_BALL = new Shape(1, "large_ball");
        public static final /* enum */ Shape STAR = new Shape(2, "star");
        public static final /* enum */ Shape CREEPER = new Shape(3, "creeper");
        public static final /* enum */ Shape BURST = new Shape(4, "burst");
        private static final IntFunction<Shape> BY_ID;
        public static final StreamCodec<ByteBuf, Shape> STREAM_CODEC;
        public static final Codec<Shape> CODEC;
        private final int id;
        private final String name;
        private static final /* synthetic */ Shape[] $VALUES;

        public static Shape[] values() {
            return (Shape[])$VALUES.clone();
        }

        public static Shape valueOf(String $$0) {
            return Enum.valueOf(Shape.class, $$0);
        }

        private Shape(int $$0, String $$1) {
            this.id = $$0;
            this.name = $$1;
        }

        public MutableComponent getName() {
            return Component.translatable("item.minecraft.firework_star.shape." + this.name);
        }

        public int getId() {
            return this.id;
        }

        public static Shape byId(int $$0) {
            return BY_ID.apply($$0);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ Shape[] d() {
            return new Shape[]{SMALL_BALL, LARGE_BALL, STAR, CREEPER, BURST};
        }

        static {
            $VALUES = Shape.d();
            BY_ID = ByIdMap.a(Shape::getId, Shape.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
            STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Shape::getId);
            CODEC = StringRepresentable.fromValues(Shape::values);
        }
    }
}

