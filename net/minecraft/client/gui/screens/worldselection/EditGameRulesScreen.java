/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameRules;

public class EditGameRulesScreen
extends Screen {
    private static final Component TITLE = Component.translatable("editGamerule.title");
    private static final int SPACING = 8;
    final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Consumer<Optional<GameRules>> exitCallback;
    private final Set<RuleEntry> invalidEntries = Sets.newHashSet();
    private final GameRules gameRules;
    @Nullable
    private RuleList ruleList;
    @Nullable
    private Button doneButton;

    public EditGameRulesScreen(GameRules $$0, Consumer<Optional<GameRules>> $$1) {
        super(TITLE);
        this.gameRules = $$0;
        this.exitCallback = $$1;
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(TITLE, this.font);
        this.ruleList = this.layout.addToContents(new RuleList(this.gameRules));
        LinearLayout $$02 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        this.doneButton = $$02.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.exitCallback.accept(Optional.of(this.gameRules))).build());
        $$02.addChild(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onClose()).build());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.ruleList != null) {
            this.ruleList.updateSize(this.width, this.layout);
        }
    }

    @Override
    public void onClose() {
        this.exitCallback.accept(Optional.empty());
    }

    private void updateDoneButton() {
        if (this.doneButton != null) {
            this.doneButton.active = this.invalidEntries.isEmpty();
        }
    }

    void markInvalid(RuleEntry $$0) {
        this.invalidEntries.add($$0);
        this.updateDoneButton();
    }

    void clearInvalid(RuleEntry $$0) {
        this.invalidEntries.remove($$0);
        this.updateDoneButton();
    }

    public class RuleList
    extends ContainerObjectSelectionList<RuleEntry> {
        private static final int ITEM_HEIGHT = 24;

        public RuleList(final GameRules $$1) {
            super(Minecraft.getInstance(), EditGameRulesScreen.this.width, EditGameRulesScreen.this.layout.getContentHeight(), EditGameRulesScreen.this.layout.getHeaderHeight(), 24);
            final HashMap $$2 = Maps.newHashMap();
            $$1.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor(){

                @Override
                public void visitBoolean(GameRules.Key<GameRules.BooleanValue> $$02, GameRules.Type<GameRules.BooleanValue> $$12) {
                    this.addEntry($$02, ($$0, $$1, $$2, $$3) -> new BooleanRuleEntry(EditGameRulesScreen.this, $$0, $$1, $$2, (GameRules.BooleanValue)$$3));
                }

                @Override
                public void visitInteger(GameRules.Key<GameRules.IntegerValue> $$02, GameRules.Type<GameRules.IntegerValue> $$12) {
                    this.addEntry($$02, ($$0, $$1, $$2, $$3) -> new IntegerRuleEntry($$0, $$1, $$2, (GameRules.IntegerValue)$$3));
                }

                private <T extends GameRules.Value<T>> void addEntry(GameRules.Key<T> $$02, EntryFactory<T> $$14) {
                    String $$13;
                    ImmutableList<FormattedCharSequence> $$12;
                    MutableComponent $$22 = Component.translatable($$02.getDescriptionId());
                    MutableComponent $$3 = Component.literal($$02.getId()).withStyle(ChatFormatting.YELLOW);
                    T $$4 = $$1.getRule($$02);
                    String $$5 = ((GameRules.Value)$$4).serialize();
                    MutableComponent $$6 = Component.a("editGamerule.default", Component.literal($$5)).withStyle(ChatFormatting.GRAY);
                    String $$7 = $$02.getDescriptionId() + ".description";
                    if (I18n.exists($$7)) {
                        ImmutableCollection.Builder $$8 = ImmutableList.builder().add($$3.getVisualOrderText());
                        MutableComponent $$9 = Component.translatable($$7);
                        EditGameRulesScreen.this.font.split($$9, 150).forEach(((ImmutableList.Builder)$$8)::add);
                        ImmutableCollection $$10 = ((ImmutableList.Builder)((ImmutableList.Builder)$$8).add($$6.getVisualOrderText())).build();
                        String $$11 = $$9.getString() + "\n" + $$6.getString();
                    } else {
                        $$12 = ImmutableList.of($$3.getVisualOrderText(), $$6.getVisualOrderText());
                        $$13 = $$6.getString();
                    }
                    $$2.computeIfAbsent($$02.getCategory(), $$0 -> Maps.newHashMap()).put($$02, $$14.create($$22, $$12, $$13, $$4));
                }
            });
            $$2.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach($$02 -> {
                this.addEntry(new CategoryRuleEntry(Component.translatable(((GameRules.Category)((Object)((Object)$$02.getKey()))).getDescriptionId()).a(ChatFormatting.BOLD, ChatFormatting.YELLOW)));
                ((Map)$$02.getValue()).entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(GameRules.Key::getId))).forEach($$0 -> this.addEntry((RuleEntry)$$0.getValue()));
            });
        }

        @Override
        public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            super.renderWidget($$0, $$1, $$2, $$3);
            RuleEntry $$4 = (RuleEntry)this.getHovered();
            if ($$4 != null && $$4.tooltip != null) {
                $$0.setTooltipForNextFrame($$4.tooltip, $$1, $$2);
            }
        }
    }

    public class IntegerRuleEntry
    extends GameRuleEntry {
        private final EditBox input;

        public IntegerRuleEntry(Component $$12, List<FormattedCharSequence> $$2, String $$3, GameRules.IntegerValue $$4) {
            super($$2, $$12);
            this.input = new EditBox(((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font, 10, 5, 44, 20, $$12.copy().append("\n").append($$3).append("\n"));
            this.input.setValue(Integer.toString($$4.get()));
            this.input.setResponder($$1 -> {
                if ($$4.tryDeserialize((String)$$1)) {
                    this.input.setTextColor(-2039584);
                    EditGameRulesScreen.this.clearInvalid(this);
                } else {
                    this.input.setTextColor(-65536);
                    EditGameRulesScreen.this.markInvalid(this);
                }
            });
            this.children.add(this.input);
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderLabel($$0, $$2, $$3);
            this.input.setX($$3 + $$4 - 45);
            this.input.setY($$2);
            this.input.render($$0, $$6, $$7, $$9);
        }
    }

    public class BooleanRuleEntry
    extends GameRuleEntry {
        private final CycleButton<Boolean> checkbox;

        public BooleanRuleEntry(EditGameRulesScreen $$0, Component $$12, List<FormattedCharSequence> $$22, String $$3, GameRules.BooleanValue $$4) {
            super($$22, $$12);
            this.checkbox = CycleButton.onOffBuilder($$4.get()).displayOnlyValue().withCustomNarration($$1 -> $$1.createDefaultNarrationMessage().append("\n").append($$3)).create(10, 5, 44, 20, $$12, ($$1, $$2) -> $$4.set((boolean)$$2, null));
            this.children.add(this.checkbox);
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderLabel($$0, $$2, $$3);
            this.checkbox.setX($$3 + $$4 - 45);
            this.checkbox.setY($$2);
            this.checkbox.render($$0, $$6, $$7, $$9);
        }
    }

    public abstract class GameRuleEntry
    extends RuleEntry {
        private final List<FormattedCharSequence> label;
        protected final List<AbstractWidget> children;

        public GameRuleEntry(List<FormattedCharSequence> $$1, Component $$2) {
            super($$1);
            this.children = Lists.newArrayList();
            this.label = ((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font.split($$2, 175);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }

        protected void renderLabel(GuiGraphics $$0, int $$1, int $$2) {
            if (this.label.size() == 1) {
                $$0.drawString(((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font, this.label.get(0), $$2, $$1 + 5, -1);
            } else if (this.label.size() >= 2) {
                $$0.drawString(((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font, this.label.get(0), $$2, $$1, -1);
                $$0.drawString(((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font, this.label.get(1), $$2, $$1 + 10, -1);
            }
        }
    }

    @FunctionalInterface
    static interface EntryFactory<T extends GameRules.Value<T>> {
        public RuleEntry create(Component var1, List<FormattedCharSequence> var2, String var3, T var4);
    }

    public class CategoryRuleEntry
    extends RuleEntry {
        final Component label;

        public CategoryRuleEntry(Component $$1) {
            super(null);
            this.label = $$1;
        }

        @Override
        public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            $$0.drawCenteredString(((EditGameRulesScreen)EditGameRulesScreen.this).minecraft.font, this.label, $$3 + $$4 / 2, $$2 + 5, -1);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry(){

                @Override
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                @Override
                public void updateNarration(NarrationElementOutput $$0) {
                    $$0.add(NarratedElementType.TITLE, CategoryRuleEntry.this.label);
                }
            });
        }
    }

    public static abstract class RuleEntry
    extends ContainerObjectSelectionList.Entry<RuleEntry> {
        @Nullable
        final List<FormattedCharSequence> tooltip;

        public RuleEntry(@Nullable List<FormattedCharSequence> $$0) {
            this.tooltip = $$0;
        }
    }
}

