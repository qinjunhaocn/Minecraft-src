/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.narration;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationThunk;

public class ScreenNarrationCollector {
    int generation;
    final Map<EntryKey, NarrationEntry> entries = Maps.newTreeMap(Comparator.comparing($$0 -> $$0.type).thenComparing($$0 -> $$0.depth));

    public void update(Consumer<NarrationElementOutput> $$0) {
        ++this.generation;
        $$0.accept(new Output(0));
    }

    public String collectNarrationText(boolean $$0) {
        final StringBuilder $$1 = new StringBuilder();
        Consumer<String> $$22 = new Consumer<String>(){
            private boolean firstEntry = true;

            @Override
            public void accept(String $$0) {
                if (!this.firstEntry) {
                    $$1.append(". ");
                }
                this.firstEntry = false;
                $$1.append($$0);
            }

            @Override
            public /* synthetic */ void accept(Object object) {
                this.accept((String)object);
            }
        };
        this.entries.forEach(($$2, $$3) -> {
            if ($$3.generation == this.generation && ($$0 || !$$3.alreadyNarrated)) {
                $$3.contents.getText($$22);
                $$3.alreadyNarrated = true;
            }
        });
        return $$1.toString();
    }

    class Output
    implements NarrationElementOutput {
        private final int depth;

        Output(int $$0) {
            this.depth = $$0;
        }

        @Override
        public void add(NarratedElementType $$02, NarrationThunk<?> $$1) {
            ScreenNarrationCollector.this.entries.computeIfAbsent(new EntryKey($$02, this.depth), $$0 -> new NarrationEntry()).update(ScreenNarrationCollector.this.generation, $$1);
        }

        @Override
        public NarrationElementOutput nest() {
            return new Output(this.depth + 1);
        }
    }

    static class NarrationEntry {
        NarrationThunk<?> contents = NarrationThunk.EMPTY;
        int generation = -1;
        boolean alreadyNarrated;

        NarrationEntry() {
        }

        public NarrationEntry update(int $$0, NarrationThunk<?> $$1) {
            if (!this.contents.equals($$1)) {
                this.contents = $$1;
                this.alreadyNarrated = false;
            } else if (this.generation + 1 != $$0) {
                this.alreadyNarrated = false;
            }
            this.generation = $$0;
            return this;
        }
    }

    static class EntryKey {
        final NarratedElementType type;
        final int depth;

        EntryKey(NarratedElementType $$0, int $$1) {
            this.type = $$0;
            this.depth = $$1;
        }
    }
}

