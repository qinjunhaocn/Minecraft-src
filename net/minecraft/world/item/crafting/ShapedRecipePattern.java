/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.chars.CharArraySet
 *  it.unimi.dsi.fastutil.chars.CharSet
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;

public final class ShapedRecipePattern {
    private static final int MAX_SIZE = 3;
    public static final char EMPTY_SLOT = ' ';
    public static final MapCodec<ShapedRecipePattern> MAP_CODEC = Data.MAP_CODEC.flatXmap(ShapedRecipePattern::unpack, $$0 -> $$0.data.map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe")));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapedRecipePattern> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, $$0 -> $$0.width, ByteBufCodecs.VAR_INT, $$0 -> $$0.height, Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), $$0 -> $$0.ingredients, ShapedRecipePattern::createFromNetwork);
    private final int width;
    private final int height;
    private final List<Optional<Ingredient>> ingredients;
    private final Optional<Data> data;
    private final int ingredientCount;
    private final boolean symmetrical;

    public ShapedRecipePattern(int $$0, int $$1, List<Optional<Ingredient>> $$2, Optional<Data> $$3) {
        this.width = $$0;
        this.height = $$1;
        this.ingredients = $$2;
        this.data = $$3;
        this.ingredientCount = (int)$$2.stream().flatMap((Function<Optional, Stream>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, stream(), (Ljava/util/Optional;)Ljava/util/stream/Stream;)()).count();
        this.symmetrical = Util.isSymmetrical($$0, $$1, $$2);
    }

    private static ShapedRecipePattern createFromNetwork(Integer $$0, Integer $$1, List<Optional<Ingredient>> $$2) {
        return new ShapedRecipePattern($$0, $$1, $$2, Optional.empty());
    }

    public static ShapedRecipePattern a(Map<Character, Ingredient> $$0, String ... $$1) {
        return ShapedRecipePattern.of($$0, List.of((Object[])$$1));
    }

    public static ShapedRecipePattern of(Map<Character, Ingredient> $$0, List<String> $$1) {
        Data $$2 = new Data($$0, $$1);
        return (ShapedRecipePattern)ShapedRecipePattern.unpack($$2).getOrThrow();
    }

    private static DataResult<ShapedRecipePattern> unpack(Data $$0) {
        String[] $$1 = ShapedRecipePattern.a($$0.pattern);
        int $$2 = $$1[0].length();
        int $$3 = $$1.length;
        ArrayList<Optional<Ingredient>> $$4 = new ArrayList<Optional<Ingredient>>($$2 * $$3);
        CharArraySet $$5 = new CharArraySet($$0.key.keySet());
        for (String $$6 : $$1) {
            for (int $$7 = 0; $$7 < $$6.length(); ++$$7) {
                Optional<Ingredient> $$11;
                char $$8 = $$6.charAt($$7);
                if ($$8 == ' ') {
                    Optional $$9 = Optional.empty();
                } else {
                    Ingredient $$10 = $$0.key.get(Character.valueOf($$8));
                    if ($$10 == null) {
                        return DataResult.error(() -> "Pattern references symbol '" + $$8 + "' but it's not defined in the key");
                    }
                    $$11 = Optional.of($$10);
                }
                $$5.remove($$8);
                $$4.add($$11);
            }
        }
        if (!$$5.isEmpty()) {
            return DataResult.error(() -> ShapedRecipePattern.lambda$unpack$7((CharSet)$$5));
        }
        return DataResult.success((Object)new ShapedRecipePattern($$2, $$3, $$4, Optional.of($$0)));
    }

    @VisibleForTesting
    static String[] a(List<String> $$0) {
        int $$1 = Integer.MAX_VALUE;
        int $$2 = 0;
        int $$3 = 0;
        int $$4 = 0;
        for (int $$5 = 0; $$5 < $$0.size(); ++$$5) {
            String $$6 = $$0.get($$5);
            $$1 = Math.min($$1, ShapedRecipePattern.firstNonEmpty($$6));
            int $$7 = ShapedRecipePattern.lastNonEmpty($$6);
            $$2 = Math.max($$2, $$7);
            if ($$7 < 0) {
                if ($$3 == $$5) {
                    ++$$3;
                }
                ++$$4;
                continue;
            }
            $$4 = 0;
        }
        if ($$0.size() == $$4) {
            return new String[0];
        }
        String[] $$8 = new String[$$0.size() - $$4 - $$3];
        for (int $$9 = 0; $$9 < $$8.length; ++$$9) {
            $$8[$$9] = $$0.get($$9 + $$3).substring($$1, $$2 + 1);
        }
        return $$8;
    }

    private static int firstNonEmpty(String $$0) {
        int $$1;
        for ($$1 = 0; $$1 < $$0.length() && $$0.charAt($$1) == ' '; ++$$1) {
        }
        return $$1;
    }

    private static int lastNonEmpty(String $$0) {
        int $$1;
        for ($$1 = $$0.length() - 1; $$1 >= 0 && $$0.charAt($$1) == ' '; --$$1) {
        }
        return $$1;
    }

    public boolean matches(CraftingInput $$0) {
        if ($$0.ingredientCount() != this.ingredientCount) {
            return false;
        }
        if ($$0.width() == this.width && $$0.height() == this.height) {
            if (!this.symmetrical && this.matches($$0, true)) {
                return true;
            }
            if (this.matches($$0, false)) {
                return true;
            }
        }
        return false;
    }

    private boolean matches(CraftingInput $$0, boolean $$1) {
        for (int $$2 = 0; $$2 < this.height; ++$$2) {
            for (int $$3 = 0; $$3 < this.width; ++$$3) {
                Optional<Ingredient> $$5;
                if ($$1) {
                    Optional<Ingredient> $$4 = this.ingredients.get(this.width - $$3 - 1 + $$2 * this.width);
                } else {
                    $$5 = this.ingredients.get($$3 + $$2 * this.width);
                }
                ItemStack $$6 = $$0.getItem($$3, $$2);
                if (Ingredient.testOptionalIngredient($$5, $$6)) continue;
                return false;
            }
        }
        return true;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public List<Optional<Ingredient>> ingredients() {
        return this.ingredients;
    }

    private static /* synthetic */ String lambda$unpack$7(CharSet $$0) {
        return "Key defines symbols that aren't used in pattern: " + String.valueOf($$0);
    }

    public static final class Data
    extends Record {
        final Map<Character, Ingredient> key;
        final List<String> pattern;
        private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap($$0 -> {
            if ($$0.size() > 3) {
                return DataResult.error(() -> "Invalid pattern: too many rows, 3 is maximum");
            }
            if ($$0.isEmpty()) {
                return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
            }
            int $$1 = ((String)$$0.getFirst()).length();
            for (String $$2 : $$0) {
                if ($$2.length() > 3) {
                    return DataResult.error(() -> "Invalid pattern: too many columns, 3 is maximum");
                }
                if ($$1 == $$2.length()) continue;
                return DataResult.error(() -> "Invalid pattern: each row must be the same width");
            }
            return DataResult.success((Object)$$0);
        }, Function.identity());
        private static final Codec<Character> SYMBOL_CODEC = Codec.STRING.comapFlatMap($$0 -> {
            if ($$0.length() != 1) {
                return DataResult.error(() -> "Invalid key entry: '" + $$0 + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals($$0)) {
                return DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.");
            }
            return DataResult.success((Object)Character.valueOf($$0.charAt(0)));
        }, String::valueOf);
        public static final MapCodec<Data> MAP_CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ExtraCodecs.strictUnboundedMap(SYMBOL_CODEC, Ingredient.CODEC).fieldOf("key").forGetter($$0 -> $$0.key), (App)PATTERN_CODEC.fieldOf("pattern").forGetter($$0 -> $$0.pattern)).apply((Applicative)$$02, Data::new));

        public Data(Map<Character, Ingredient> $$0, List<String> $$1) {
            this.key = $$0;
            this.pattern = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Data.class, "key;pattern", "key", "pattern"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Data.class, "key;pattern", "key", "pattern"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Data.class, "key;pattern", "key", "pattern"}, this, $$0);
        }

        public Map<Character, Ingredient> key() {
            return this.key;
        }

        public List<String> pattern() {
            return this.pattern;
        }
    }
}

