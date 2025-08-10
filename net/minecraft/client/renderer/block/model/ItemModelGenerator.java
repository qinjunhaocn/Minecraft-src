/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer.block.model;

import com.mojang.math.Quadrant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.SimpleUnbakedGeometry;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ItemModelGenerator
implements UnbakedModel {
    public static final ResourceLocation GENERATED_ITEM_MODEL_ID = ResourceLocation.withDefaultNamespace("builtin/generated");
    public static final List<String> LAYERS = List.of((Object)"layer0", (Object)"layer1", (Object)"layer2", (Object)"layer3", (Object)"layer4");
    private static final float MIN_Z = 7.5f;
    private static final float MAX_Z = 8.5f;
    private static final TextureSlots.Data TEXTURE_SLOTS = new TextureSlots.Data.Builder().addReference("particle", "layer0").build();
    private static final BlockElementFace.UVs SOUTH_FACE_UVS = new BlockElementFace.UVs(0.0f, 0.0f, 16.0f, 16.0f);
    private static final BlockElementFace.UVs NORTH_FACE_UVS = new BlockElementFace.UVs(16.0f, 0.0f, 0.0f, 16.0f);

    @Override
    public TextureSlots.Data textureSlots() {
        return TEXTURE_SLOTS;
    }

    @Override
    public UnbakedGeometry geometry() {
        return ItemModelGenerator::bake;
    }

    @Override
    @Nullable
    public UnbakedModel.GuiLight guiLight() {
        return UnbakedModel.GuiLight.FRONT;
    }

    private static QuadCollection bake(TextureSlots $$0, ModelBaker $$1, ModelState $$2, ModelDebugName $$3) {
        return ItemModelGenerator.bake($$0, $$1.sprites(), $$2, $$3);
    }

    private static QuadCollection bake(TextureSlots $$0, SpriteGetter $$1, ModelState $$2, ModelDebugName $$3) {
        String $$6;
        Material $$7;
        ArrayList<BlockElement> $$4 = new ArrayList<BlockElement>();
        for (int $$5 = 0; $$5 < LAYERS.size() && ($$7 = $$0.getMaterial($$6 = LAYERS.get($$5))) != null; ++$$5) {
            SpriteContents $$8 = $$1.get($$7, $$3).contents();
            $$4.addAll(ItemModelGenerator.processFrames($$5, $$6, $$8));
        }
        return SimpleUnbakedGeometry.bake($$4, $$0, $$1, $$2, $$3);
    }

    private static List<BlockElement> processFrames(int $$0, String $$1, SpriteContents $$2) {
        Map $$3 = Map.of((Object)Direction.SOUTH, (Object)((Object)new BlockElementFace(null, $$0, $$1, SOUTH_FACE_UVS, Quadrant.R0)), (Object)Direction.NORTH, (Object)((Object)new BlockElementFace(null, $$0, $$1, NORTH_FACE_UVS, Quadrant.R0)));
        ArrayList<BlockElement> $$4 = new ArrayList<BlockElement>();
        $$4.add(new BlockElement((Vector3fc)new Vector3f(0.0f, 0.0f, 7.5f), (Vector3fc)new Vector3f(16.0f, 16.0f, 8.5f), $$3));
        $$4.addAll(ItemModelGenerator.createSideElements($$2, $$1, $$0));
        return $$4;
    }

    private static List<BlockElement> createSideElements(SpriteContents $$0, String $$1, int $$2) {
        float $$3 = $$0.width();
        float $$4 = $$0.height();
        ArrayList<BlockElement> $$5 = new ArrayList<BlockElement>();
        for (Span $$6 : ItemModelGenerator.getSpans($$0)) {
            float $$7 = 0.0f;
            float $$8 = 0.0f;
            float $$9 = 0.0f;
            float $$10 = 0.0f;
            float $$11 = 0.0f;
            float $$12 = 0.0f;
            float $$13 = 0.0f;
            float $$14 = 0.0f;
            float $$15 = 16.0f / $$3;
            float $$16 = 16.0f / $$4;
            float $$17 = $$6.getMin();
            float $$18 = $$6.getMax();
            float $$19 = $$6.getAnchor();
            SpanFacing $$20 = $$6.getFacing();
            switch ($$20.ordinal()) {
                case 0: {
                    $$7 = $$11 = $$17;
                    $$9 = $$12 = $$18 + 1.0f;
                    $$8 = $$13 = $$19;
                    $$10 = $$19;
                    $$14 = $$19 + 1.0f;
                    break;
                }
                case 1: {
                    $$13 = $$19;
                    $$14 = $$19 + 1.0f;
                    $$7 = $$11 = $$17;
                    $$9 = $$12 = $$18 + 1.0f;
                    $$8 = $$19 + 1.0f;
                    $$10 = $$19 + 1.0f;
                    break;
                }
                case 2: {
                    $$7 = $$11 = $$19;
                    $$9 = $$19;
                    $$12 = $$19 + 1.0f;
                    $$8 = $$14 = $$17;
                    $$10 = $$13 = $$18 + 1.0f;
                    break;
                }
                case 3: {
                    $$11 = $$19;
                    $$12 = $$19 + 1.0f;
                    $$7 = $$19 + 1.0f;
                    $$9 = $$19 + 1.0f;
                    $$8 = $$14 = $$17;
                    $$10 = $$13 = $$18 + 1.0f;
                }
            }
            $$7 *= $$15;
            $$9 *= $$15;
            $$8 *= $$16;
            $$10 *= $$16;
            $$8 = 16.0f - $$8;
            $$10 = 16.0f - $$10;
            Map $$21 = Map.of((Object)$$20.getDirection(), (Object)((Object)new BlockElementFace(null, $$2, $$1, new BlockElementFace.UVs($$11 *= $$15, $$13 *= $$16, $$12 *= $$15, $$14 *= $$16), Quadrant.R0)));
            switch ($$20.ordinal()) {
                case 0: {
                    $$5.add(new BlockElement((Vector3fc)new Vector3f($$7, $$8, 7.5f), (Vector3fc)new Vector3f($$9, $$8, 8.5f), $$21));
                    break;
                }
                case 1: {
                    $$5.add(new BlockElement((Vector3fc)new Vector3f($$7, $$10, 7.5f), (Vector3fc)new Vector3f($$9, $$10, 8.5f), $$21));
                    break;
                }
                case 2: {
                    $$5.add(new BlockElement((Vector3fc)new Vector3f($$7, $$8, 7.5f), (Vector3fc)new Vector3f($$7, $$10, 8.5f), $$21));
                    break;
                }
                case 3: {
                    $$5.add(new BlockElement((Vector3fc)new Vector3f($$9, $$8, 7.5f), (Vector3fc)new Vector3f($$9, $$10, 8.5f), $$21));
                }
            }
        }
        return $$5;
    }

    private static List<Span> getSpans(SpriteContents $$0) {
        int $$1 = $$0.width();
        int $$2 = $$0.height();
        ArrayList<Span> $$3 = new ArrayList<Span>();
        $$0.getUniqueFrames().forEach($$4 -> {
            for (int $$5 = 0; $$5 < $$2; ++$$5) {
                for (int $$6 = 0; $$6 < $$1; ++$$6) {
                    boolean $$7 = !ItemModelGenerator.isTransparent($$0, $$4, $$6, $$5, $$1, $$2);
                    ItemModelGenerator.checkTransition(SpanFacing.UP, $$3, $$0, $$4, $$6, $$5, $$1, $$2, $$7);
                    ItemModelGenerator.checkTransition(SpanFacing.DOWN, $$3, $$0, $$4, $$6, $$5, $$1, $$2, $$7);
                    ItemModelGenerator.checkTransition(SpanFacing.LEFT, $$3, $$0, $$4, $$6, $$5, $$1, $$2, $$7);
                    ItemModelGenerator.checkTransition(SpanFacing.RIGHT, $$3, $$0, $$4, $$6, $$5, $$1, $$2, $$7);
                }
            }
        });
        return $$3;
    }

    private static void checkTransition(SpanFacing $$0, List<Span> $$1, SpriteContents $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8) {
        boolean $$9;
        boolean bl = $$9 = ItemModelGenerator.isTransparent($$2, $$3, $$4 + $$0.getXOffset(), $$5 + $$0.getYOffset(), $$6, $$7) && $$8;
        if ($$9) {
            ItemModelGenerator.createOrExpandSpan($$1, $$0, $$4, $$5);
        }
    }

    private static void createOrExpandSpan(List<Span> $$0, SpanFacing $$1, int $$2, int $$3) {
        int $$8;
        Span $$4 = null;
        for (Span $$5 : $$0) {
            int $$6;
            if ($$5.getFacing() != $$1) continue;
            int n = $$6 = $$1.isHorizontal() ? $$3 : $$2;
            if ($$5.getAnchor() != $$6) continue;
            $$4 = $$5;
            break;
        }
        int $$7 = $$1.isHorizontal() ? $$3 : $$2;
        int n = $$8 = $$1.isHorizontal() ? $$2 : $$3;
        if ($$4 == null) {
            $$0.add(new Span($$1, $$8, $$7));
        } else {
            $$4.expand($$8);
        }
    }

    private static boolean isTransparent(SpriteContents $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        if ($$2 < 0 || $$3 < 0 || $$2 >= $$4 || $$3 >= $$5) {
            return true;
        }
        return $$0.isTransparent($$1, $$2, $$3);
    }

    static class Span {
        private final SpanFacing facing;
        private int min;
        private int max;
        private final int anchor;

        public Span(SpanFacing $$0, int $$1, int $$2) {
            this.facing = $$0;
            this.min = $$1;
            this.max = $$1;
            this.anchor = $$2;
        }

        public void expand(int $$0) {
            if ($$0 < this.min) {
                this.min = $$0;
            } else if ($$0 > this.max) {
                this.max = $$0;
            }
        }

        public SpanFacing getFacing() {
            return this.facing;
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }

        public int getAnchor() {
            return this.anchor;
        }
    }

    static final class SpanFacing
    extends Enum<SpanFacing> {
        public static final /* enum */ SpanFacing UP = new SpanFacing(Direction.UP, 0, -1);
        public static final /* enum */ SpanFacing DOWN = new SpanFacing(Direction.DOWN, 0, 1);
        public static final /* enum */ SpanFacing LEFT = new SpanFacing(Direction.EAST, -1, 0);
        public static final /* enum */ SpanFacing RIGHT = new SpanFacing(Direction.WEST, 1, 0);
        private final Direction direction;
        private final int xOffset;
        private final int yOffset;
        private static final /* synthetic */ SpanFacing[] $VALUES;

        public static SpanFacing[] values() {
            return (SpanFacing[])$VALUES.clone();
        }

        public static SpanFacing valueOf(String $$0) {
            return Enum.valueOf(SpanFacing.class, $$0);
        }

        private SpanFacing(Direction $$0, int $$1, int $$2) {
            this.direction = $$0;
            this.xOffset = $$1;
            this.yOffset = $$2;
        }

        public Direction getDirection() {
            return this.direction;
        }

        public int getXOffset() {
            return this.xOffset;
        }

        public int getYOffset() {
            return this.yOffset;
        }

        boolean isHorizontal() {
            return this == DOWN || this == UP;
        }

        private static /* synthetic */ SpanFacing[] e() {
            return new SpanFacing[]{UP, DOWN, LEFT, RIGHT};
        }

        static {
            $VALUES = SpanFacing.e();
        }
    }
}

