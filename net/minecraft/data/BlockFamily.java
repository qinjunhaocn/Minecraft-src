/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Block;

public class BlockFamily {
    private final Block baseBlock;
    final Map<Variant, Block> variants = Maps.newHashMap();
    boolean generateModel = true;
    boolean generateRecipe = true;
    @Nullable
    String recipeGroupPrefix;
    @Nullable
    String recipeUnlockedBy;

    BlockFamily(Block $$0) {
        this.baseBlock = $$0;
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    public Map<Variant, Block> getVariants() {
        return this.variants;
    }

    public Block get(Variant $$0) {
        return this.variants.get((Object)$$0);
    }

    public boolean shouldGenerateModel() {
        return this.generateModel;
    }

    public boolean shouldGenerateRecipe() {
        return this.generateRecipe;
    }

    public Optional<String> getRecipeGroupPrefix() {
        if (StringUtil.isBlank(this.recipeGroupPrefix)) {
            return Optional.empty();
        }
        return Optional.of(this.recipeGroupPrefix);
    }

    public Optional<String> getRecipeUnlockedBy() {
        if (StringUtil.isBlank(this.recipeUnlockedBy)) {
            return Optional.empty();
        }
        return Optional.of(this.recipeUnlockedBy);
    }

    public static class Builder {
        private final BlockFamily family;

        public Builder(Block $$0) {
            this.family = new BlockFamily($$0);
        }

        public BlockFamily getFamily() {
            return this.family;
        }

        public Builder button(Block $$0) {
            this.family.variants.put(Variant.BUTTON, $$0);
            return this;
        }

        public Builder chiseled(Block $$0) {
            this.family.variants.put(Variant.CHISELED, $$0);
            return this;
        }

        public Builder mosaic(Block $$0) {
            this.family.variants.put(Variant.MOSAIC, $$0);
            return this;
        }

        public Builder cracked(Block $$0) {
            this.family.variants.put(Variant.CRACKED, $$0);
            return this;
        }

        public Builder cut(Block $$0) {
            this.family.variants.put(Variant.CUT, $$0);
            return this;
        }

        public Builder door(Block $$0) {
            this.family.variants.put(Variant.DOOR, $$0);
            return this;
        }

        public Builder customFence(Block $$0) {
            this.family.variants.put(Variant.CUSTOM_FENCE, $$0);
            return this;
        }

        public Builder fence(Block $$0) {
            this.family.variants.put(Variant.FENCE, $$0);
            return this;
        }

        public Builder customFenceGate(Block $$0) {
            this.family.variants.put(Variant.CUSTOM_FENCE_GATE, $$0);
            return this;
        }

        public Builder fenceGate(Block $$0) {
            this.family.variants.put(Variant.FENCE_GATE, $$0);
            return this;
        }

        public Builder sign(Block $$0, Block $$1) {
            this.family.variants.put(Variant.SIGN, $$0);
            this.family.variants.put(Variant.WALL_SIGN, $$1);
            return this;
        }

        public Builder slab(Block $$0) {
            this.family.variants.put(Variant.SLAB, $$0);
            return this;
        }

        public Builder stairs(Block $$0) {
            this.family.variants.put(Variant.STAIRS, $$0);
            return this;
        }

        public Builder pressurePlate(Block $$0) {
            this.family.variants.put(Variant.PRESSURE_PLATE, $$0);
            return this;
        }

        public Builder polished(Block $$0) {
            this.family.variants.put(Variant.POLISHED, $$0);
            return this;
        }

        public Builder trapdoor(Block $$0) {
            this.family.variants.put(Variant.TRAPDOOR, $$0);
            return this;
        }

        public Builder wall(Block $$0) {
            this.family.variants.put(Variant.WALL, $$0);
            return this;
        }

        public Builder dontGenerateModel() {
            this.family.generateModel = false;
            return this;
        }

        public Builder dontGenerateRecipe() {
            this.family.generateRecipe = false;
            return this;
        }

        public Builder recipeGroupPrefix(String $$0) {
            this.family.recipeGroupPrefix = $$0;
            return this;
        }

        public Builder recipeUnlockedBy(String $$0) {
            this.family.recipeUnlockedBy = $$0;
            return this;
        }
    }

    public static final class Variant
    extends Enum<Variant> {
        public static final /* enum */ Variant BUTTON = new Variant("button");
        public static final /* enum */ Variant CHISELED = new Variant("chiseled");
        public static final /* enum */ Variant CRACKED = new Variant("cracked");
        public static final /* enum */ Variant CUT = new Variant("cut");
        public static final /* enum */ Variant DOOR = new Variant("door");
        public static final /* enum */ Variant CUSTOM_FENCE = new Variant("fence");
        public static final /* enum */ Variant FENCE = new Variant("fence");
        public static final /* enum */ Variant CUSTOM_FENCE_GATE = new Variant("fence_gate");
        public static final /* enum */ Variant FENCE_GATE = new Variant("fence_gate");
        public static final /* enum */ Variant MOSAIC = new Variant("mosaic");
        public static final /* enum */ Variant SIGN = new Variant("sign");
        public static final /* enum */ Variant SLAB = new Variant("slab");
        public static final /* enum */ Variant STAIRS = new Variant("stairs");
        public static final /* enum */ Variant PRESSURE_PLATE = new Variant("pressure_plate");
        public static final /* enum */ Variant POLISHED = new Variant("polished");
        public static final /* enum */ Variant TRAPDOOR = new Variant("trapdoor");
        public static final /* enum */ Variant WALL = new Variant("wall");
        public static final /* enum */ Variant WALL_SIGN = new Variant("wall_sign");
        private final String recipeGroup;
        private static final /* synthetic */ Variant[] $VALUES;

        public static Variant[] values() {
            return (Variant[])$VALUES.clone();
        }

        public static Variant valueOf(String $$0) {
            return Enum.valueOf(Variant.class, $$0);
        }

        private Variant(String $$0) {
            this.recipeGroup = $$0;
        }

        public String getRecipeGroup() {
            return this.recipeGroup;
        }

        private static /* synthetic */ Variant[] b() {
            return new Variant[]{BUTTON, CHISELED, CRACKED, CUT, DOOR, CUSTOM_FENCE, FENCE, CUSTOM_FENCE_GATE, FENCE_GATE, MOSAIC, SIGN, SLAB, STAIRS, PRESSURE_PLATE, POLISHED, TRAPDOOR, WALL, WALL_SIGN};
        }

        static {
            $VALUES = Variant.b();
        }
    }
}

