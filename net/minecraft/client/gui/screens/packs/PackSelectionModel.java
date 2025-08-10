/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.packs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;

public class PackSelectionModel {
    private final PackRepository repository;
    final List<Pack> selected;
    final List<Pack> unselected;
    final Function<Pack, ResourceLocation> iconGetter;
    final Runnable onListChanged;
    private final Consumer<PackRepository> output;

    public PackSelectionModel(Runnable $$0, Function<Pack, ResourceLocation> $$1, PackRepository $$2, Consumer<PackRepository> $$3) {
        this.onListChanged = $$0;
        this.iconGetter = $$1;
        this.repository = $$2;
        this.selected = Lists.newArrayList($$2.getSelectedPacks());
        Collections.reverse(this.selected);
        this.unselected = Lists.newArrayList($$2.getAvailablePacks());
        this.unselected.removeAll(this.selected);
        this.output = $$3;
    }

    public Stream<Entry> getUnselected() {
        return this.unselected.stream().map($$0 -> new UnselectedPackEntry((Pack)$$0));
    }

    public Stream<Entry> getSelected() {
        return this.selected.stream().map($$0 -> new SelectedPackEntry((Pack)$$0));
    }

    void updateRepoSelectedList() {
        this.repository.setSelected(Lists.reverse(this.selected).stream().map(Pack::getId).collect(ImmutableList.toImmutableList()));
    }

    public void commit() {
        this.updateRepoSelectedList();
        this.output.accept(this.repository);
    }

    public void findNewPacks() {
        this.repository.reload();
        this.selected.retainAll(this.repository.getAvailablePacks());
        this.unselected.clear();
        this.unselected.addAll(this.repository.getAvailablePacks());
        this.unselected.removeAll(this.selected);
    }

    class SelectedPackEntry
    extends EntryBase {
        public SelectedPackEntry(Pack $$0) {
            super($$0);
        }

        @Override
        protected List<Pack> getSelfList() {
            return PackSelectionModel.this.selected;
        }

        @Override
        protected List<Pack> getOtherList() {
            return PackSelectionModel.this.unselected;
        }

        @Override
        public boolean isSelected() {
            return true;
        }

        @Override
        public void select() {
        }

        @Override
        public void unselect() {
            this.toggleSelection();
        }
    }

    class UnselectedPackEntry
    extends EntryBase {
        public UnselectedPackEntry(Pack $$0) {
            super($$0);
        }

        @Override
        protected List<Pack> getSelfList() {
            return PackSelectionModel.this.unselected;
        }

        @Override
        protected List<Pack> getOtherList() {
            return PackSelectionModel.this.selected;
        }

        @Override
        public boolean isSelected() {
            return false;
        }

        @Override
        public void select() {
            this.toggleSelection();
        }

        @Override
        public void unselect() {
        }
    }

    abstract class EntryBase
    implements Entry {
        private final Pack pack;

        public EntryBase(Pack $$0) {
            this.pack = $$0;
        }

        protected abstract List<Pack> getSelfList();

        protected abstract List<Pack> getOtherList();

        @Override
        public ResourceLocation getIconTexture() {
            return PackSelectionModel.this.iconGetter.apply(this.pack);
        }

        @Override
        public PackCompatibility getCompatibility() {
            return this.pack.getCompatibility();
        }

        @Override
        public String getId() {
            return this.pack.getId();
        }

        @Override
        public Component getTitle() {
            return this.pack.getTitle();
        }

        @Override
        public Component getDescription() {
            return this.pack.getDescription();
        }

        @Override
        public PackSource getPackSource() {
            return this.pack.getPackSource();
        }

        @Override
        public boolean isFixedPosition() {
            return this.pack.isFixedPosition();
        }

        @Override
        public boolean isRequired() {
            return this.pack.isRequired();
        }

        protected void toggleSelection() {
            this.getSelfList().remove(this.pack);
            this.pack.getDefaultPosition().insert(this.getOtherList(), this.pack, Pack::selectionConfig, true);
            PackSelectionModel.this.onListChanged.run();
            PackSelectionModel.this.updateRepoSelectedList();
            this.updateHighContrastOptionInstance();
        }

        private void updateHighContrastOptionInstance() {
            if (this.pack.getId().equals("high_contrast")) {
                OptionInstance<Boolean> $$0;
                $$0.set(($$0 = Minecraft.getInstance().options.highContrast()).get() == false);
            }
        }

        protected void move(int $$0) {
            List<Pack> $$1 = this.getSelfList();
            int $$2 = $$1.indexOf(this.pack);
            $$1.remove($$2);
            $$1.add($$2 + $$0, this.pack);
            PackSelectionModel.this.onListChanged.run();
        }

        @Override
        public boolean canMoveUp() {
            List<Pack> $$0 = this.getSelfList();
            int $$1 = $$0.indexOf(this.pack);
            return $$1 > 0 && !$$0.get($$1 - 1).isFixedPosition();
        }

        @Override
        public void moveUp() {
            this.move(-1);
        }

        @Override
        public boolean canMoveDown() {
            List<Pack> $$0 = this.getSelfList();
            int $$1 = $$0.indexOf(this.pack);
            return $$1 >= 0 && $$1 < $$0.size() - 1 && !$$0.get($$1 + 1).isFixedPosition();
        }

        @Override
        public void moveDown() {
            this.move(1);
        }
    }

    public static interface Entry {
        public ResourceLocation getIconTexture();

        public PackCompatibility getCompatibility();

        public String getId();

        public Component getTitle();

        public Component getDescription();

        public PackSource getPackSource();

        default public Component getExtendedDescription() {
            return this.getPackSource().decorate(this.getDescription());
        }

        public boolean isFixedPosition();

        public boolean isRequired();

        public void select();

        public void unselect();

        public void moveUp();

        public void moveDown();

        public boolean isSelected();

        default public boolean canSelect() {
            return !this.isSelected();
        }

        default public boolean canUnselect() {
            return this.isSelected() && !this.isRequired();
        }

        public boolean canMoveUp();

        public boolean canMoveDown();
    }
}

