/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.render.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.client.gui.render.state.ScreenArea;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.apache.commons.lang3.mutable.MutableInt;

public class GuiRenderState {
    private static final int DEBUG_RECTANGLE_COLOR = 0x774444FF;
    private final List<Node> strata = new ArrayList<Node>();
    private int firstStratumAfterBlur = Integer.MAX_VALUE;
    private Node current;
    private final Set<Object> itemModelIdentities = new HashSet<Object>();
    @Nullable
    private ScreenRectangle lastElementBounds;

    public GuiRenderState() {
        this.nextStratum();
    }

    public void nextStratum() {
        this.current = new Node(null);
        this.strata.add(this.current);
    }

    public void blurBeforeThisStratum() {
        if (this.firstStratumAfterBlur != Integer.MAX_VALUE) {
            throw new IllegalStateException("Can only blur once per frame");
        }
        this.firstStratumAfterBlur = this.strata.size() - 1;
    }

    public void up() {
        if (this.current.up == null) {
            this.current.up = new Node(this.current);
        }
        this.current = this.current.up;
    }

    public void down() {
        if (this.current.down == null) {
            this.current.down = new Node(this.current);
        }
        this.current = this.current.down;
    }

    public void submitItem(GuiItemRenderState $$0) {
        if (!this.findAppropriateNode($$0)) {
            return;
        }
        this.itemModelIdentities.add($$0.itemStackRenderState().getModelIdentity());
        this.current.submitItem($$0);
        this.sumbitDebugRectangleIfEnabled($$0.bounds());
    }

    public void submitText(GuiTextRenderState $$0) {
        if (!this.findAppropriateNode($$0)) {
            return;
        }
        this.current.submitText($$0);
        this.sumbitDebugRectangleIfEnabled($$0.bounds());
    }

    public void submitPicturesInPictureState(PictureInPictureRenderState $$0) {
        if (!this.findAppropriateNode($$0)) {
            return;
        }
        this.current.submitPicturesInPictureState($$0);
        this.sumbitDebugRectangleIfEnabled($$0.bounds());
    }

    public void submitGuiElement(GuiElementRenderState $$0) {
        if (!this.findAppropriateNode($$0)) {
            return;
        }
        this.current.submitGuiElement($$0);
        this.sumbitDebugRectangleIfEnabled($$0.bounds());
    }

    private void sumbitDebugRectangleIfEnabled(@Nullable ScreenRectangle $$0) {
    }

    private boolean findAppropriateNode(ScreenArea $$0) {
        ScreenRectangle $$1 = $$0.bounds();
        if ($$1 == null) {
            return false;
        }
        if (this.lastElementBounds != null && this.lastElementBounds.encompasses($$1)) {
            this.up();
        } else {
            this.navigateToAboveHighestElementWithIntersectingBounds($$1);
        }
        this.lastElementBounds = $$1;
        return true;
    }

    private void navigateToAboveHighestElementWithIntersectingBounds(ScreenRectangle $$0) {
        Node $$1 = (Node)this.strata.getLast();
        while ($$1.up != null) {
            $$1 = $$1.up;
        }
        boolean $$2 = false;
        while (!$$2) {
            boolean bl = $$2 = this.hasIntersection($$0, $$1.elementStates) || this.hasIntersection($$0, $$1.itemStates) || this.hasIntersection($$0, $$1.textStates) || this.hasIntersection($$0, $$1.picturesInPictureStates);
            if ($$1.parent == null) break;
            if ($$2) continue;
            $$1 = $$1.parent;
        }
        this.current = $$1;
        if ($$2) {
            this.up();
        }
    }

    private boolean hasIntersection(ScreenRectangle $$0, @Nullable List<? extends ScreenArea> $$1) {
        if ($$1 != null) {
            for (ScreenArea screenArea : $$1) {
                ScreenRectangle $$3 = screenArea.bounds();
                if ($$3 == null || !$$3.intersects($$0)) continue;
                return true;
            }
        }
        return false;
    }

    public void submitBlitToCurrentLayer(BlitRenderState $$0) {
        this.current.submitGuiElement($$0);
    }

    public void submitGlyphToCurrentLayer(GuiElementRenderState $$0) {
        this.current.submitGlyph($$0);
    }

    public Set<Object> getItemModelIdentities() {
        return this.itemModelIdentities;
    }

    public void forEachElement(LayeredElementConsumer $$0, TraverseRange $$1) {
        MutableInt $$22 = new MutableInt(0);
        this.traverse((Node $$2) -> {
            if ($$2.elementStates == null && $$2.glyphStates == null) {
                return;
            }
            int $$3 = $$22.incrementAndGet();
            if ($$2.elementStates != null) {
                for (GuiElementRenderState $$4 : $$2.elementStates) {
                    $$0.accept($$4, $$3);
                }
            }
            if ($$2.glyphStates != null) {
                for (GuiElementRenderState $$5 : $$2.glyphStates) {
                    $$0.accept($$5, $$3);
                }
            }
        }, $$1);
    }

    public void forEachItem(Consumer<GuiItemRenderState> $$0) {
        Node $$12 = this.current;
        this.traverse((Node $$1) -> {
            if ($$1.itemStates != null) {
                this.current = $$1;
                for (GuiItemRenderState $$2 : $$1.itemStates) {
                    $$0.accept($$2);
                }
            }
        }, TraverseRange.ALL);
        this.current = $$12;
    }

    public void forEachText(Consumer<GuiTextRenderState> $$0) {
        Node $$12 = this.current;
        this.traverse((Node $$1) -> {
            if ($$1.textStates != null) {
                for (GuiTextRenderState $$2 : $$1.textStates) {
                    this.current = $$1;
                    $$0.accept($$2);
                }
            }
        }, TraverseRange.ALL);
        this.current = $$12;
    }

    public void forEachPictureInPicture(Consumer<PictureInPictureRenderState> $$0) {
        Node $$12 = this.current;
        this.traverse((Node $$1) -> {
            if ($$1.picturesInPictureStates != null) {
                this.current = $$1;
                for (PictureInPictureRenderState $$2 : $$1.picturesInPictureStates) {
                    $$0.accept($$2);
                }
            }
        }, TraverseRange.ALL);
        this.current = $$12;
    }

    public void sortElements(Comparator<GuiElementRenderState> $$0) {
        this.traverse((Node $$1) -> {
            if ($$1.elementStates != null) {
                $$1.elementStates.sort($$0);
            }
        }, TraverseRange.ALL);
    }

    private void traverse(Consumer<Node> $$0, TraverseRange $$1) {
        int $$2 = 0;
        int $$3 = this.strata.size();
        if ($$1 == TraverseRange.BEFORE_BLUR) {
            $$3 = Math.min(this.firstStratumAfterBlur, this.strata.size());
        } else if ($$1 == TraverseRange.AFTER_BLUR) {
            $$2 = this.firstStratumAfterBlur;
        }
        for (int $$4 = $$2; $$4 < $$3; ++$$4) {
            Node $$5 = this.strata.get($$4);
            this.traverse($$5, $$0);
        }
    }

    private void traverse(Node $$0, Consumer<Node> $$1) {
        if ($$0.down != null) {
            this.traverse($$0.down, $$1);
        }
        $$1.accept($$0);
        if ($$0.up != null) {
            this.traverse($$0.up, $$1);
        }
    }

    public void reset() {
        this.itemModelIdentities.clear();
        this.strata.clear();
        this.firstStratumAfterBlur = Integer.MAX_VALUE;
        this.nextStratum();
    }

    static class Node {
        @Nullable
        public final Node parent;
        @Nullable
        public Node up;
        @Nullable
        public Node down;
        @Nullable
        public List<GuiElementRenderState> elementStates;
        @Nullable
        public List<GuiElementRenderState> glyphStates;
        @Nullable
        public List<GuiItemRenderState> itemStates;
        @Nullable
        public List<GuiTextRenderState> textStates;
        @Nullable
        public List<PictureInPictureRenderState> picturesInPictureStates;

        Node(@Nullable Node $$0) {
            this.parent = $$0;
        }

        public void submitItem(GuiItemRenderState $$0) {
            if (this.itemStates == null) {
                this.itemStates = new ArrayList<GuiItemRenderState>();
            }
            this.itemStates.add($$0);
        }

        public void submitText(GuiTextRenderState $$0) {
            if (this.textStates == null) {
                this.textStates = new ArrayList<GuiTextRenderState>();
            }
            this.textStates.add($$0);
        }

        public void submitPicturesInPictureState(PictureInPictureRenderState $$0) {
            if (this.picturesInPictureStates == null) {
                this.picturesInPictureStates = new ArrayList<PictureInPictureRenderState>();
            }
            this.picturesInPictureStates.add($$0);
        }

        public void submitGuiElement(GuiElementRenderState $$0) {
            if (this.elementStates == null) {
                this.elementStates = new ArrayList<GuiElementRenderState>();
            }
            this.elementStates.add($$0);
        }

        public void submitGlyph(GuiElementRenderState $$0) {
            if (this.glyphStates == null) {
                this.glyphStates = new ArrayList<GuiElementRenderState>();
            }
            this.glyphStates.add($$0);
        }
    }

    public static interface LayeredElementConsumer {
        public void accept(GuiElementRenderState var1, int var2);
    }

    public static final class TraverseRange
    extends Enum<TraverseRange> {
        public static final /* enum */ TraverseRange ALL = new TraverseRange();
        public static final /* enum */ TraverseRange BEFORE_BLUR = new TraverseRange();
        public static final /* enum */ TraverseRange AFTER_BLUR = new TraverseRange();
        private static final /* synthetic */ TraverseRange[] $VALUES;

        public static TraverseRange[] values() {
            return (TraverseRange[])$VALUES.clone();
        }

        public static TraverseRange valueOf(String $$0) {
            return Enum.valueOf(TraverseRange.class, $$0);
        }

        private static /* synthetic */ TraverseRange[] a() {
            return new TraverseRange[]{ALL, BEFORE_BLUR, AFTER_BLUR};
        }

        static {
            $VALUES = TraverseRange.a();
        }
    }
}

