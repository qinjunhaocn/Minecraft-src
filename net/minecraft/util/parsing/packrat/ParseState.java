/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.util.parsing.packrat;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.parsing.packrat.Control;
import net.minecraft.util.parsing.packrat.ErrorCollector;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.Scope;

public interface ParseState<S> {
    public Scope scope();

    public ErrorCollector<S> errorCollector();

    default public <T> Optional<T> parseTopRule(NamedRule<S, T> $$0) {
        T $$1 = this.parse($$0);
        if ($$1 != null) {
            this.errorCollector().finish(this.mark());
        }
        if (!this.scope().hasOnlySingleFrame()) {
            throw new IllegalStateException("Malformed scope: " + String.valueOf(this.scope()));
        }
        return Optional.ofNullable($$1);
    }

    @Nullable
    public <T> T parse(NamedRule<S, T> var1);

    public S input();

    public int mark();

    public void restore(int var1);

    public Control acquireControl();

    public void releaseControl();

    public ParseState<S> silent();
}

